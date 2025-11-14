"""
Text processing utilities for inference.

This module provides high-level functions for processing individual texts
through the model pipeline.
"""

from typing import Dict, Any, Optional
from .tokenizer_adapter import TokenizerAdapter
from .batch_types import ModelOutputs
from .model_api import TokenModel


def process_text(
    text: str,
    tokenizer: TokenizerAdapter,
    model: TokenModel,
    max_length: Optional[int] = None,
) -> Dict[str, Any]:
    """
    High-level helper for processing a single text through the model.

    This function:
    1. Tokenizes the input text
    2. Runs the model forward pass
    3. Returns tokens and raw model outputs

    This is the primary interface for inference use cases where you
    want to process individual texts without batching or training.

    Args:
        text: Input text to process
        tokenizer: TokenizerAdapter for tokenization
        model: TokenModel implementation (e.g., AIKA transformer)
        max_length: Maximum sequence length (None = no limit)

    Returns:
        Dictionary containing:
        - "input_ids": np.ndarray of shape (1, seq_len)
        - "attention_mask": np.ndarray of shape (1, seq_len)
        - "logits": np.ndarray from model output
        - "extra": Optional dict with additional model outputs

    Usage:
        tokenizer = TokenizerAdapter.from_pretrained("bert-base-uncased")
        model = MyAIKAModel(...)  # implements TokenModel

        result = process_text(
            "Hello, world!",
            tokenizer,
            model,
            max_length=128
        )

        logits = result["logits"]
        # ... do something with logits (e.g., get predictions)
    """
    # Tokenize the text
    token_batch = tokenizer.encode(text, max_length=max_length)

    # Run model forward pass
    outputs: ModelOutputs = model.forward_tokens(token_batch)

    # Return comprehensive results
    return {
        "input_ids": token_batch.input_ids,
        "attention_mask": token_batch.attention_mask,
        "logits": outputs.logits,
        "extra": outputs.extra,
    }