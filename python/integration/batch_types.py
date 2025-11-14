"""
Batch and label type definitions.

This module defines the data structures passed between:
- dataset loaders
- tokenization layer
- model interface
- training/eval loops

All types are AIKA-agnostic and contain only token-level information.
"""

from dataclasses import dataclass
from typing import Optional, Any, Dict
import numpy as np


@dataclass
class TokenizedBatch:
    """
    Tokenized batch representation.
    Re-exported from tokenizer_adapter to maintain a single definition.
    """
    input_ids: np.ndarray        # (B, L) - batch size × sequence length
    attention_mask: np.ndarray   # (B, L) - 1 for real tokens, 0 for padding


@dataclass
class LabeledBatch:
    """
    Tokenized batch plus supervision targets (for training/eval).

    The shape of labels depends on the task:
    - Token-level tasks (e.g., language modeling): (B, L)
    - Sequence classification: (B,)
    - Other tasks: flexible shape
    """
    tokens: TokenizedBatch
    labels: np.ndarray           # (B, ...) shape depends on task


@dataclass
class ModelOutputs:
    """
    Minimal output structure from the model.

    The outer framework only cares about logits and optional metadata.
    How logits are produced internally is not its concern.
    """
    logits: np.ndarray           # (B, L, C) or (B, C), depending on task
                                 # where C is the number of classes/vocab size
    extra: Optional[Dict[str, Any]] = None  # Optional: loss, hidden states, diagnostics, etc.