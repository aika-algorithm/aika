"""
Unit tests for tokenizer_adapter module.

Tests the TokenizerBackend protocol, TokenizedBatch, and TokenizerAdapter.
"""

import unittest
import numpy as np
from typing import List, Mapping, Optional, Any

from python.integration.tokenizer_adapter import (
    TokenizerBackend,
    TokenizedBatch,
    TokenizerAdapter,
    HFTokenizerBackend,
)


class MockTokenizerBackend:
    """
    Mock tokenizer backend for testing without HuggingFace dependencies.
    """

    def __init__(self, vocab_size: int = 1000):
        self._vocab_size = vocab_size

    def encode_plus(
        self,
        text: str,
        max_length: Optional[int] = None,
        truncation: bool = True,
        padding: bool = False,
        return_tensors: Optional[str] = None,
        return_attention_mask: bool = True,
        add_special_tokens: bool = True,
    ) -> Mapping[str, Any]:
        """Mock encoding that creates simple token IDs based on word count."""
        words = text.split()
        token_ids = [1] if add_special_tokens else []  # [CLS] token

        for word in words:
            # Simple hash-based tokenization
            token_id = (hash(word) % (self._vocab_size - 10)) + 10
            token_ids.append(token_id)

        if add_special_tokens:
            token_ids.append(2)  # [SEP] token

        if max_length and truncation:
            token_ids = token_ids[:max_length]

        attention_mask = [1] * len(token_ids)

        result = {
            "input_ids": token_ids,
            "attention_mask": attention_mask,
        }

        if return_tensors == "np":
            result["input_ids"] = np.array([result["input_ids"]])
            result["attention_mask"] = np.array([result["attention_mask"]])

        return result

    def batch_encode_plus(
        self,
        texts: List[str],
        max_length: Optional[int] = None,
        truncation: bool = True,
        padding: str = "longest",
        return_tensors: Optional[str] = None,
        return_attention_mask: bool = True,
        add_special_tokens: bool = True,
    ) -> Mapping[str, Any]:
        """Mock batch encoding."""
        all_input_ids = []
        all_attention_masks = []

        for text in texts:
            encoded = self.encode_plus(
                text,
                max_length=max_length,
                truncation=truncation,
                padding=False,
                return_tensors=None,
                return_attention_mask=return_attention_mask,
                add_special_tokens=add_special_tokens,
            )
            all_input_ids.append(encoded["input_ids"])
            all_attention_masks.append(encoded["attention_mask"])

        # Apply padding
        if padding == "longest" and all_input_ids:
            max_len = max(len(ids) for ids in all_input_ids)
            for i in range(len(all_input_ids)):
                pad_len = max_len - len(all_input_ids[i])
                all_input_ids[i] = all_input_ids[i] + [0] * pad_len
                all_attention_masks[i] = all_attention_masks[i] + [0] * pad_len

        result = {
            "input_ids": all_input_ids,
            "attention_mask": all_attention_masks,
        }

        if return_tensors == "np":
            result["input_ids"] = np.array(result["input_ids"])
            result["attention_mask"] = np.array(result["attention_mask"])

        return result

    @property
    def vocab_size(self) -> int:
        return self._vocab_size

    def save_pretrained(self, save_directory: str) -> None:
        pass

    @classmethod
    def from_pretrained(cls, path_or_name: str) -> "MockTokenizerBackend":
        return cls()


class TestTokenizedBatch(unittest.TestCase):
    """Test TokenizedBatch dataclass."""

    def test_tokenized_batch_creation(self):
        """Test creating a TokenizedBatch."""
        input_ids = np.array([[1, 2, 3, 4]])
        attention_mask = np.array([[1, 1, 1, 1]])

        batch = TokenizedBatch(
            input_ids=input_ids,
            attention_mask=attention_mask
        )

        self.assertTrue(np.array_equal(batch.input_ids, input_ids))
        self.assertTrue(np.array_equal(batch.attention_mask, attention_mask))

    def test_tokenized_batch_shapes(self):
        """Test that TokenizedBatch maintains correct shapes."""
        batch_size = 3
        seq_len = 5
        input_ids = np.random.randint(0, 1000, size=(batch_size, seq_len))
        attention_mask = np.ones((batch_size, seq_len), dtype=int)

        batch = TokenizedBatch(input_ids=input_ids, attention_mask=attention_mask)

        self.assertEqual(batch.input_ids.shape, (batch_size, seq_len))
        self.assertEqual(batch.attention_mask.shape, (batch_size, seq_len))


class TestTokenizerAdapter(unittest.TestCase):
    """Test TokenizerAdapter with mock backend."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.adapter = TokenizerAdapter(self.backend)

    def test_vocab_size(self):
        """Test vocab_size property."""
        self.assertEqual(self.adapter.vocab_size, 1000)

    def test_encode_single_text(self):
        """Test encoding a single text."""
        text = "Hello world"
        batch = self.adapter.encode(text)

        self.assertIsInstance(batch, TokenizedBatch)
        self.assertEqual(batch.input_ids.shape[0], 1)  # batch size = 1
        self.assertGreater(batch.input_ids.shape[1], 0)  # has tokens
        self.assertEqual(batch.input_ids.shape, batch.attention_mask.shape)

    def test_encode_with_special_tokens(self):
        """Test encoding with and without special tokens."""
        text = "Hello world"

        batch_with = self.adapter.encode(text, add_special_tokens=True)
        batch_without = self.adapter.encode(text, add_special_tokens=False)

        # With special tokens should be longer (has [CLS] and [SEP])
        self.assertGreater(
            batch_with.input_ids.shape[1],
            batch_without.input_ids.shape[1]
        )

    def test_encode_with_max_length(self):
        """Test encoding with max_length truncation."""
        text = "This is a very long sentence that should be truncated"
        max_length = 5

        batch = self.adapter.encode(text, max_length=max_length, truncation=True)

        self.assertLessEqual(batch.input_ids.shape[1], max_length)

    def test_encode_batch_multiple_texts(self):
        """Test encoding multiple texts."""
        texts = ["Hello world", "Goodbye", "Testing tokenizer"]

        batch = self.adapter.encode_batch(texts)

        self.assertIsInstance(batch, TokenizedBatch)
        self.assertEqual(batch.input_ids.shape[0], 3)  # batch size = 3
        self.assertEqual(batch.input_ids.shape, batch.attention_mask.shape)

    def test_encode_batch_padding(self):
        """Test that batch encoding pads sequences to same length."""
        texts = ["Short", "This is a much longer sentence"]

        batch = self.adapter.encode_batch(texts, padding="longest")

        # All sequences should have the same length
        self.assertEqual(batch.input_ids.shape[0], 2)
        seq_len = batch.input_ids.shape[1]

        # Check that shorter sequence is padded (has 0s in attention mask)
        first_seq_mask = batch.attention_mask[0]
        self.assertTrue(np.any(first_seq_mask == 0))  # has padding

        # Check that longer sequence has no padding
        second_seq_mask = batch.attention_mask[1]
        self.assertTrue(np.all(second_seq_mask == 1))  # no padding

    def test_encode_batch_empty_list(self):
        """Test encoding an empty list of texts."""
        texts = []

        batch = self.adapter.encode_batch(texts)

        self.assertEqual(batch.input_ids.shape[0], 0)
        self.assertEqual(batch.attention_mask.shape[0], 0)

    def test_encode_batch_single_text(self):
        """Test encoding a batch with a single text."""
        texts = ["Single text"]

        batch = self.adapter.encode_batch(texts)

        self.assertEqual(batch.input_ids.shape[0], 1)


class TestHFTokenizerBackend(unittest.TestCase):
    """Test HFTokenizerBackend integration."""

    def test_from_pretrained_requires_transformers(self):
        """Test that from_pretrained raises error without transformers."""
        # Note: This test will fail if transformers is installed
        # In a real environment, we'd use mock to simulate this
        try:
            import transformers
            self.skipTest("transformers is installed, skipping import error test")
        except ImportError:
            with self.assertRaises(ImportError) as context:
                HFTokenizerBackend.from_pretrained("bert-base-uncased")
            self.assertIn("transformers", str(context.exception))


class TestTokenizerAdapterIntegration(unittest.TestCase):
    """Integration tests with different scenarios."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.adapter = TokenizerAdapter(self.backend)

    def test_typical_inference_workflow(self):
        """Test typical inference workflow."""
        # Process a single text
        text = "This is a test sentence for inference"
        batch = self.adapter.encode(text, max_length=128)

        self.assertEqual(batch.input_ids.shape[0], 1)
        self.assertGreater(batch.input_ids.shape[1], 0)
        self.assertTrue(np.all(batch.attention_mask >= 0))
        self.assertTrue(np.all(batch.attention_mask <= 1))

    def test_typical_training_workflow(self):
        """Test typical training workflow with batches."""
        # Process multiple texts for training
        texts = [
            "First training example",
            "Second training example",
            "Third training example",
        ]

        batch = self.adapter.encode_batch(texts, max_length=64, padding="longest")

        self.assertEqual(batch.input_ids.shape[0], 3)
        # All sequences should have same length due to padding
        self.assertEqual(len(set(len(seq) for seq in batch.input_ids)), 1)

    def test_consistency_between_encode_and_encode_batch(self):
        """Test that encoding single vs batch produces compatible results."""
        text = "Test sentence"

        single = self.adapter.encode(text)
        batch = self.adapter.encode_batch([text])

        # Shapes should be the same
        self.assertEqual(single.input_ids.shape, batch.input_ids.shape)
        self.assertEqual(single.attention_mask.shape, batch.attention_mask.shape)

        # Values should be the same
        self.assertTrue(np.array_equal(single.input_ids, batch.input_ids))
        self.assertTrue(np.array_equal(single.attention_mask, batch.attention_mask))


if __name__ == "__main__":
    unittest.main()