"""
Tokenizer adapter module - wraps standard tokenizers behind a minimal interface.

This module provides:
- TokenizerBackend: Protocol defining minimal tokenizer requirements
- HFTokenizerBackend: Concrete implementation using HuggingFace tokenizers
- TokenizedBatch: Data class representing tokenized text
- TokenizerAdapter: Main adapter class for tokenization
"""

from dataclasses import dataclass
from typing import List, Mapping, Optional, Any, Protocol
import numpy as np


class TokenizerBackend(Protocol):
    """
    Minimal protocol a tokenizer must implement.
    Intended to be backed by HuggingFace tokenizers, but independent of them.
    """

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
        """Encode a single text into token IDs and attention mask."""
        ...

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
        """Encode a batch of texts into token IDs and attention masks."""
        ...

    @property
    def vocab_size(self) -> int:
        """Return the vocabulary size."""
        ...

    def save_pretrained(self, save_directory: str) -> None:
        """Save tokenizer to directory."""
        ...

    @classmethod
    def from_pretrained(cls, path_or_name: str) -> "TokenizerBackend":
        """Load tokenizer from path or model name."""
        ...


@dataclass
class TokenizedBatch:
    """
    Purely token-level view used by the outer framework.
    No AIKA-specific fields.
    """
    input_ids: np.ndarray        # shape: (batch, seq_len), dtype=int
    attention_mask: np.ndarray   # shape: (batch, seq_len), 1=real, 0=padding


class HFTokenizerBackend:
    """
    Concrete implementation of TokenizerBackend using HuggingFace transformers.
    """

    def __init__(self, hf_tokenizer):
        """
        Initialize with a HuggingFace tokenizer instance.

        Args:
            hf_tokenizer: An instance of transformers.PreTrainedTokenizer or PreTrainedTokenizerFast
        """
        self._tokenizer = hf_tokenizer

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
        """Encode a single text using HuggingFace tokenizer."""
        encoded = self._tokenizer.encode_plus(
            text,
            max_length=max_length,
            truncation=truncation,
            padding=padding,
            return_tensors=return_tensors,
            return_attention_mask=return_attention_mask,
            add_special_tokens=add_special_tokens,
        )

        # Convert to numpy if return_tensors was "np"
        if return_tensors == "np":
            result = {}
            for key, value in encoded.items():
                if hasattr(value, "numpy"):
                    result[key] = value.numpy()
                elif isinstance(value, list):
                    result[key] = np.array(value)
                else:
                    result[key] = value
            return result

        return encoded

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
        """Encode a batch of texts using HuggingFace tokenizer."""
        encoded = self._tokenizer.batch_encode_plus(
            texts,
            max_length=max_length,
            truncation=truncation,
            padding=padding,
            return_tensors=return_tensors,
            return_attention_mask=return_attention_mask,
            add_special_tokens=add_special_tokens,
        )

        # Convert to numpy if return_tensors was "np"
        if return_tensors == "np":
            result = {}
            for key, value in encoded.items():
                if hasattr(value, "numpy"):
                    result[key] = value.numpy()
                elif isinstance(value, list):
                    result[key] = np.array(value)
                else:
                    result[key] = value
            return result

        return encoded

    @property
    def vocab_size(self) -> int:
        """Return the vocabulary size."""
        return len(self._tokenizer)

    def save_pretrained(self, save_directory: str) -> None:
        """Save tokenizer to directory."""
        self._tokenizer.save_pretrained(save_directory)

    @classmethod
    def from_pretrained(cls, path_or_name: str) -> "HFTokenizerBackend":
        """
        Load tokenizer from HuggingFace hub or local path.

        Args:
            path_or_name: Model name on HuggingFace hub or local path

        Returns:
            HFTokenizerBackend instance
        """
        try:
            from transformers import AutoTokenizer
        except ImportError:
            raise ImportError(
                "transformers library is required for HFTokenizerBackend. "
                "Install with: pip install transformers"
            )

        hf_tokenizer = AutoTokenizer.from_pretrained(path_or_name)
        return cls(hf_tokenizer)


class TokenizerAdapter:
    """
    Thin wrapper around a concrete tokenizer backend.
    Responsible only for text <-> ids, nothing else.
    """

    def __init__(self, backend: TokenizerBackend):
        """
        Initialize with a tokenizer backend.

        Args:
            backend: A TokenizerBackend implementation
        """
        self._backend = backend

    @property
    def vocab_size(self) -> int:
        """Return the vocabulary size."""
        return self._backend.vocab_size

    @classmethod
    def from_pretrained(cls, path_or_name: str) -> "TokenizerAdapter":
        """
        Create TokenizerAdapter using HuggingFace tokenizer.

        Args:
            path_or_name: Model name on HuggingFace hub or local path

        Returns:
            TokenizerAdapter instance
        """
        backend = HFTokenizerBackend.from_pretrained(path_or_name)
        return cls(backend)

    def save_pretrained(self, save_directory: str) -> None:
        """Save tokenizer to directory."""
        self._backend.save_pretrained(save_directory)

    def encode(
        self,
        text: str,
        max_length: Optional[int] = None,
        truncation: bool = True,
        add_special_tokens: bool = True,
    ) -> TokenizedBatch:
        """
        Encode a single text into tokens.

        Args:
            text: Text to tokenize
            max_length: Maximum sequence length
            truncation: Whether to truncate sequences
            add_special_tokens: Whether to add special tokens (e.g., [CLS], [SEP])

        Returns:
            TokenizedBatch with input_ids and attention_mask
        """
        encoded = self._backend.encode_plus(
            text=text,
            max_length=max_length,
            truncation=truncation,
            padding=False,
            return_tensors="np",
            return_attention_mask=True,
            add_special_tokens=add_special_tokens,
        )
        return TokenizedBatch(
            input_ids=encoded["input_ids"],          # shape: (1, L)
            attention_mask=encoded["attention_mask"]
        )

    def encode_batch(
        self,
        texts: List[str],
        max_length: Optional[int] = None,
        truncation: bool = True,
        padding: str = "longest",  # or "max_length"
        add_special_tokens: bool = True,
    ) -> TokenizedBatch:
        """
        Encode a batch of texts into tokens.

        Args:
            texts: List of texts to tokenize
            max_length: Maximum sequence length
            truncation: Whether to truncate sequences
            padding: Padding strategy ("longest" or "max_length")
            add_special_tokens: Whether to add special tokens

        Returns:
            TokenizedBatch with input_ids and attention_mask
        """
        encoded = self._backend.batch_encode_plus(
            texts=texts,
            max_length=max_length,
            truncation=truncation,
            padding=padding,
            return_tensors="np",
            return_attention_mask=True,
            add_special_tokens=add_special_tokens,
        )
        return TokenizedBatch(
            input_ids=encoded["input_ids"],
            attention_mask=encoded["attention_mask"],
        )