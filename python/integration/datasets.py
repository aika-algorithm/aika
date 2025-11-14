"""
Dataset and batching utilities.

This module provides:
- TextDataset: Simple in-memory dataset of text and optional labels
- DataLoaderConfig: Configuration for batch loading
- DataLoader: Iterator for batching and tokenizing text data

These abstractions are AIKA-agnostic and work with any TokenModel implementation.
"""

from dataclasses import dataclass
from typing import Iterable, List, Tuple, Optional, Iterator, Callable, Any
import numpy as np

from .tokenizer_adapter import TokenizerAdapter, TokenizedBatch
from .batch_types import LabeledBatch


# Type alias for (text, optional_label) tuples
TextExample = Tuple[str, Optional[Any]]


@dataclass
class TextDataset:
    """
    Simple in-memory dataset of text (and optional labels).

    Examples:
        # Unsupervised: labels are None
        dataset = TextDataset([("Hello world", None), ("Goodbye", None)])

        # Supervised classification
        dataset = TextDataset([("Great movie!", 1), ("Terrible!", 0)])

        # Token-level labels (e.g., NER)
        dataset = TextDataset([("John lives in NYC", [1, 0, 0, 2])])
    """
    examples: List[TextExample]

    def __len__(self) -> int:
        """Return the number of examples in the dataset."""
        return len(self.examples)

    def __iter__(self) -> Iterator[TextExample]:
        """Iterate over examples in the dataset."""
        return iter(self.examples)


@dataclass
class DataLoaderConfig:
    """
    Configuration for the DataLoader.

    Attributes:
        batch_size: Number of examples per batch
        shuffle: Whether to shuffle examples before batching
        max_length: Maximum sequence length for tokenization (None = no limit)
    """
    batch_size: int
    shuffle: bool = True
    max_length: Optional[int] = None


class DataLoader:
    """
    Simple DataLoader that:
    - batches examples
    - tokenizes them
    - produces LabeledBatch objects

    Usage:
        dataset = TextDataset([("text1", 0), ("text2", 1)])
        tokenizer = TokenizerAdapter.from_pretrained("bert-base-uncased")
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, tokenizer, config)

        for batch in loader:
            # batch is a LabeledBatch with tokens and labels
            pass
    """

    def __init__(
        self,
        dataset: TextDataset,
        tokenizer: TokenizerAdapter,
        config: DataLoaderConfig,
        label_transform: Optional[Callable[[List[Any]], np.ndarray]] = None,
    ):
        """
        Initialize the DataLoader.

        Args:
            dataset: TextDataset to load from
            tokenizer: TokenizerAdapter for tokenization
            config: DataLoaderConfig with batch_size, shuffle, etc.
            label_transform: Optional function to transform labels from Python list
                           to numpy array. If None, uses np.array() directly.
                           Useful for encoding categorical labels, padding sequences, etc.
        """
        self.dataset = dataset
        self.tokenizer = tokenizer
        self.config = config
        self.label_transform = label_transform

    def __iter__(self) -> Iterator[LabeledBatch]:
        """
        Iterate over batches of tokenized and labeled data.

        Yields:
            LabeledBatch objects containing tokenized inputs and labels
        """
        indices = np.arange(len(self.dataset))
        if self.config.shuffle:
            np.random.shuffle(indices)

        batch_size = self.config.batch_size
        max_length = self.config.max_length

        for start in range(0, len(indices), batch_size):
            batch_idx = indices[start:start + batch_size]
            texts: List[str] = []
            labels: List[Any] = []

            for i in batch_idx:
                text, label = self.dataset.examples[i]
                texts.append(text)
                labels.append(label)

            # Tokenize the batch of texts
            tokens: TokenizedBatch = self.tokenizer.encode_batch(
                texts,
                max_length=max_length,
            )

            # Transform labels to numpy array
            if self.label_transform is not None:
                label_arr = self.label_transform(labels)
            else:
                # Default: assume labels are already numeric-like
                label_arr = np.array(labels)

            yield LabeledBatch(tokens=tokens, labels=label_arr)

    def __len__(self) -> int:
        """
        Return the number of batches in an epoch.

        Note: This is an estimate when shuffle=True and may be off by 1
        due to rounding.
        """
        return (len(self.dataset) + self.config.batch_size - 1) // self.config.batch_size