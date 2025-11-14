"""
Integration layer for tokenization, datasets, and model interface.

This package provides a pure Python outer framework for:
- Tokenization of text to input_ids and attention_mask
- Dataset and batching utilities
- Simple model interface that deals with batches of tokens and labels

This layer is AIKA-agnostic and has no knowledge of:
- binding signals
- neuron types
- event queues
"""

from .tokenizer_adapter import (
    TokenizerBackend,
    TokenizerAdapter,
    TokenizedBatch,
)

from .batch_types import (
    LabeledBatch,
    ModelOutputs,
)

from .datasets import (
    TextDataset,
    TextExample,
    DataLoaderConfig,
    DataLoader,
)

from .model_api import TokenModel

from .processing import process_text

from .training import (
    TrainingConfig,
    Trainer,
)

from .evaluation import evaluate

__all__ = [
    # Tokenizer
    "TokenizerBackend",
    "TokenizerAdapter",
    "TokenizedBatch",
    # Batch types
    "LabeledBatch",
    "ModelOutputs",
    # Datasets
    "TextDataset",
    "TextExample",
    "DataLoaderConfig",
    "DataLoader",
    # Model API
    "TokenModel",
    # Processing
    "process_text",
    # Training
    "TrainingConfig",
    "Trainer",
    # Evaluation
    "evaluate",
]