"""
Model interface for the integration layer.

This module defines the TokenModel protocol that any model implementation
(including AIKA transformer) must satisfy to work with the outer framework.

The protocol is AIKA-agnostic - it only deals with tokens and logits,
not with internal mechanisms like binding signals, event queues, etc.
"""

from typing import Protocol
from .batch_types import TokenizedBatch, LabeledBatch, ModelOutputs


class TokenModel(Protocol):
    """
    Abstract model interface used by the outer framework.

    Any model that wants to work with the integration layer
    (tokenizer, datasets, training, evaluation) must implement this protocol.

    The AIKA transformer wrapper will implement this protocol, handling
    internally:
    - mapping token IDs to embeddings
    - creating binding signals
    - running the event queue
    - producing logits

    The outer framework never sees these internal details.
    """

    def forward_tokens(self, batch: TokenizedBatch) -> ModelOutputs:
        """
        Forward pass for inference or unsupervised scenarios.

        This method takes tokenized inputs and produces model outputs
        without supervision labels.

        Args:
            batch: TokenizedBatch containing input_ids and attention_mask

        Returns:
            ModelOutputs with logits and optional extra information

        Usage:
            tokenizer = TokenizerAdapter.from_pretrained("bert-base")
            tokens = tokenizer.encode("Hello world")
            outputs = model.forward_tokens(tokens)
            logits = outputs.logits  # (1, seq_len, vocab_size)
        """
        ...

    def forward_labeled(self, batch: LabeledBatch) -> ModelOutputs:
        """
        Forward pass for supervised/semi-supervised training or evaluation.

        This method takes tokenized inputs plus labels and produces model outputs.
        Loss computation is typically handled by the training loop, not here,
        though the model may optionally compute and include loss in extra.

        Args:
            batch: LabeledBatch containing tokens and labels

        Returns:
            ModelOutputs with logits and optional extra information (e.g., loss)

        Usage:
            # During training
            for batch in train_loader:  # yields LabeledBatch
                outputs = model.forward_labeled(batch)
                loss = loss_fn(outputs.logits, batch.labels)
                # ... backprop and optimize
        """
        ...