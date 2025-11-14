"""
Unit tests for datasets module.

Tests TextDataset, DataLoaderConfig, and DataLoader functionality.
"""

import unittest
import numpy as np
from typing import List, Any

from python.integration.datasets import (
    TextDataset,
    DataLoaderConfig,
    DataLoader,
    TextExample,
)
from python.integration.tokenizer_adapter import TokenizerAdapter
from tests.python.integration.test_tokenizer_adapter import MockTokenizerBackend


class TestTextDataset(unittest.TestCase):
    """Test TextDataset class."""

    def test_dataset_creation(self):
        """Test creating a TextDataset."""
        examples = [
            ("Hello world", 1),
            ("Goodbye", 0),
        ]
        dataset = TextDataset(examples)

        self.assertEqual(len(dataset), 2)
        self.assertEqual(dataset.examples, examples)

    def test_dataset_iteration(self):
        """Test iterating over a TextDataset."""
        examples = [
            ("First", 1),
            ("Second", 2),
            ("Third", 3),
        ]
        dataset = TextDataset(examples)

        collected = list(dataset)
        self.assertEqual(collected, examples)

    def test_dataset_with_none_labels(self):
        """Test dataset with None labels for unsupervised learning."""
        examples = [
            ("Text one", None),
            ("Text two", None),
        ]
        dataset = TextDataset(examples)

        self.assertEqual(len(dataset), 2)
        for text, label in dataset:
            self.assertIsNone(label)

    def test_empty_dataset(self):
        """Test empty dataset."""
        dataset = TextDataset([])
        self.assertEqual(len(dataset), 0)
        self.assertEqual(list(dataset), [])


class TestDataLoaderConfig(unittest.TestCase):
    """Test DataLoaderConfig class."""

    def test_config_creation(self):
        """Test creating a DataLoaderConfig."""
        config = DataLoaderConfig(batch_size=32, shuffle=True, max_length=128)

        self.assertEqual(config.batch_size, 32)
        self.assertTrue(config.shuffle)
        self.assertEqual(config.max_length, 128)

    def test_config_defaults(self):
        """Test default values in DataLoaderConfig."""
        config = DataLoaderConfig(batch_size=16)

        self.assertEqual(config.batch_size, 16)
        self.assertTrue(config.shuffle)  # default is True
        self.assertIsNone(config.max_length)  # default is None


class TestDataLoader(unittest.TestCase):
    """Test DataLoader class."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.tokenizer = TokenizerAdapter(self.backend)

    def test_dataloader_basic_iteration(self):
        """Test basic DataLoader iteration."""
        examples = [
            ("Hello world", 1),
            ("Goodbye", 0),
            ("Test sentence", 1),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        batches = list(loader)

        # Should produce 2 batches (2 + 1)
        self.assertEqual(len(batches), 2)

        # First batch should have 2 examples
        self.assertEqual(batches[0].tokens.input_ids.shape[0], 2)
        self.assertEqual(batches[0].labels.shape[0], 2)

        # Second batch should have 1 example
        self.assertEqual(batches[1].tokens.input_ids.shape[0], 1)
        self.assertEqual(batches[1].labels.shape[0], 1)

    def test_dataloader_shuffle(self):
        """Test that shuffle produces different orders."""
        examples = [(f"Text {i}", i) for i in range(10)]
        dataset = TextDataset(examples)

        # Get batches without shuffle
        config_no_shuffle = DataLoaderConfig(batch_size=5, shuffle=False)
        loader_no_shuffle = DataLoader(dataset, self.tokenizer, config_no_shuffle)
        labels_no_shuffle = [batch.labels for batch in loader_no_shuffle]

        # Get batches with shuffle (set seed for reproducibility)
        np.random.seed(42)
        config_shuffle = DataLoaderConfig(batch_size=5, shuffle=True)
        loader_shuffle = DataLoader(dataset, self.tokenizer, config_shuffle)
        labels_shuffle = [batch.labels for batch in loader_shuffle]

        # With shuffle, at least one batch should be different
        # (This is probabilistic, but with seed 42 it should work)
        all_same = all(
            np.array_equal(a, b)
            for a, b in zip(labels_no_shuffle, labels_shuffle)
        )
        self.assertFalse(all_same, "Shuffle should produce different order")

    def test_dataloader_max_length(self):
        """Test that max_length is respected."""
        examples = [
            ("This is a very long sentence that should be truncated", 1),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=1, max_length=10)
        loader = DataLoader(dataset, self.tokenizer, config)

        batch = next(iter(loader))
        self.assertLessEqual(batch.tokens.input_ids.shape[1], 10)

    def test_dataloader_label_transform(self):
        """Test custom label transformation."""
        examples = [
            ("Text one", "positive"),
            ("Text two", "negative"),
            ("Text three", "positive"),
        ]
        dataset = TextDataset(examples)

        # Define label transform
        def label_transform(labels: List[Any]) -> np.ndarray:
            mapping = {"positive": 1, "negative": 0}
            return np.array([mapping[label] for label in labels])

        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config, label_transform)

        batches = list(loader)

        # Check first batch
        self.assertTrue(np.array_equal(batches[0].labels, np.array([1, 0])))
        # Check second batch
        self.assertTrue(np.array_equal(batches[1].labels, np.array([1])))

    def test_dataloader_with_none_labels(self):
        """Test DataLoader with None labels (unsupervised)."""
        examples = [
            ("Text one", None),
            ("Text two", None),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, self.tokenizer, config)

        batch = next(iter(loader))

        # Labels should be numpy array of None values
        self.assertEqual(batch.labels.shape[0], 2)

    def test_dataloader_padding_in_batch(self):
        """Test that sequences in a batch are padded to same length."""
        examples = [
            ("Short", 0),
            ("This is a much longer sentence", 1),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        batch = next(iter(loader))

        # Both sequences should have the same length
        self.assertEqual(batch.tokens.input_ids.shape[0], 2)
        seq_len_0 = batch.tokens.input_ids.shape[1]

        # Attention mask should indicate padding
        # First sequence should have some padding (0s)
        self.assertTrue(np.any(batch.tokens.attention_mask[0] == 0))
        # Second sequence should have no padding (all 1s)
        self.assertTrue(np.all(batch.tokens.attention_mask[1] == 1))

    def test_dataloader_len(self):
        """Test __len__ method of DataLoader."""
        examples = [(f"Text {i}", i) for i in range(10)]
        dataset = TextDataset(examples)

        config = DataLoaderConfig(batch_size=3)
        loader = DataLoader(dataset, self.tokenizer, config)

        # 10 examples / 3 batch_size = 4 batches (3+3+3+1)
        self.assertEqual(len(loader), 4)

        config2 = DataLoaderConfig(batch_size=5)
        loader2 = DataLoader(dataset, self.tokenizer, config2)

        # 10 examples / 5 batch_size = 2 batches
        self.assertEqual(len(loader2), 2)

    def test_dataloader_empty_dataset(self):
        """Test DataLoader with empty dataset."""
        dataset = TextDataset([])
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, self.tokenizer, config)

        batches = list(loader)
        self.assertEqual(len(batches), 0)

    def test_dataloader_single_example(self):
        """Test DataLoader with single example."""
        examples = [("Single text", 1)]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, self.tokenizer, config)

        batches = list(loader)
        self.assertEqual(len(batches), 1)
        self.assertEqual(batches[0].tokens.input_ids.shape[0], 1)


class TestDataLoaderIntegration(unittest.TestCase):
    """Integration tests for DataLoader with different scenarios."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.tokenizer = TokenizerAdapter(self.backend)

    def test_classification_dataset(self):
        """Test DataLoader for text classification task."""
        examples = [
            ("Great movie!", 1),
            ("Terrible film", 0),
            ("Amazing experience", 1),
            ("Waste of time", 0),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        for batch in loader:
            # Check batch structure
            self.assertEqual(batch.tokens.input_ids.shape[0], 2)
            self.assertEqual(batch.labels.shape, (2,))
            # Labels should be 0 or 1
            self.assertTrue(np.all(np.isin(batch.labels, [0, 1])))

    def test_sequence_labeling_dataset(self):
        """Test DataLoader for sequence labeling task."""
        # For sequence labeling, labels would be per-token
        examples = [
            ("John lives in NYC", [1, 0, 0, 2]),
            ("Mary works at Google", [1, 0, 0, 3]),
        ]
        dataset = TextDataset(examples)

        # Custom label transform to handle variable-length labels
        def pad_labels(labels: List[List[int]]) -> np.ndarray:
            max_len = max(len(seq) for seq in labels)
            padded = np.zeros((len(labels), max_len), dtype=int)
            for i, seq in enumerate(labels):
                padded[i, :len(seq)] = seq
            return padded

        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config, label_transform=pad_labels)

        batch = next(iter(loader))

        # Labels should be (batch_size, max_seq_len)
        self.assertEqual(len(batch.labels.shape), 2)
        self.assertEqual(batch.labels.shape[0], 2)

    def test_unsupervised_dataset(self):
        """Test DataLoader for unsupervised learning."""
        examples = [
            ("This is unlabeled text", None),
            ("Another unlabeled example", None),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, self.tokenizer, config)

        batch = next(iter(loader))

        # Should still have labels array, but with None values
        self.assertIsNotNone(batch.labels)


if __name__ == "__main__":
    unittest.main()