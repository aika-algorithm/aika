"""
Training utilities for supervised and semi-supervised learning.

This module provides a lightweight training framework that is AIKA-agnostic.
It defines the structure of a training loop but delegates optimization details
to the caller.
"""

from dataclasses import dataclass
from typing import Iterable, Callable, Any, Dict
import numpy as np

from .batch_types import LabeledBatch, ModelOutputs
from .model_api import TokenModel


@dataclass
class TrainingConfig:
    """
    Configuration for training.

    Attributes:
        num_epochs: Number of complete passes through the training data
    """
    num_epochs: int
    # Future extensions could include:
    # - learning_rate: float
    # - clip_norm: Optional[float]
    # - gradient_accumulation_steps: int
    # - warmup_steps: int
    # etc.


class Trainer:
    """
    Lightweight training skeleton for the outer framework.

    This class provides a minimal training loop structure without implementing
    optimization details (gradients, learning rate, etc.). Instead, it exposes
    hooks for loss computation and optimizer steps.

    The actual optimization mechanism (PyTorch, custom gradient descent,
    integration with AIKA's internal training) is provided by the caller
    through callback functions.

    Usage:
        # Define a loss function
        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            logits = outputs.logits
            labels = batch.labels
            # Compute cross-entropy or other loss
            loss = compute_loss(logits, labels)
            return loss

        # Define an optimizer step
        def optimizer_step(loss: float) -> None:
            # Compute gradients from loss
            # Update model parameters
            # (implementation depends on your optimization framework)
            pass

        # Create trainer
        config = TrainingConfig(num_epochs=10)
        trainer = Trainer(model, loss_fn, optimizer_step, config)

        # Train
        history = trainer.train(train_loader)
        print(f"Final loss: {history['loss'][-1]}")
    """

    def __init__(
        self,
        model: TokenModel,
        loss_fn: Callable[[ModelOutputs, LabeledBatch], float],
        optimizer_step: Callable[[float], None],
        config: TrainingConfig,
    ):
        """
        Initialize the Trainer.

        Args:
            model: A TokenModel implementation (e.g., AIKA transformer)
            loss_fn: Function that takes model outputs and labeled batch,
                    returns scalar loss. This allows custom loss computation
                    based on the task.
            optimizer_step: Callback that applies gradients after loss is computed.
                          The implementation details are up to the caller
                          (could use PyTorch, TensorFlow, or custom optimization).
            config: TrainingConfig with num_epochs and other settings
        """
        self.model = model
        self.loss_fn = loss_fn
        self.optimizer_step = optimizer_step
        self.config = config

    def train(self, train_loader: Iterable[LabeledBatch]) -> Dict[str, Any]:
        """
        Execute the training loop.

        This is a skeleton for the outer framework only. It:
        1. Iterates through epochs
        2. For each batch:
           - Runs forward pass
           - Computes loss
           - Calls optimizer_step
        3. Tracks and returns loss history

        Args:
            train_loader: Iterable yielding LabeledBatch objects
                         (typically a DataLoader)

        Returns:
            Dictionary with training history:
            - "loss": List of average losses per epoch

        Note: The outer layer knows nothing about gradients, optimizers,
        or AIKA internals. How optimizer_step works is the caller's choice.
        """
        history = {"loss": []}

        for epoch in range(self.config.num_epochs):
            epoch_losses: list[float] = []

            for batch in train_loader:
                # Forward pass through model
                outputs: ModelOutputs = self.model.forward_labeled(batch)

                # Compute loss
                loss = self.loss_fn(outputs, batch)

                # Apply optimization step (gradients, parameter updates)
                self.optimizer_step(loss)

                # Track loss
                epoch_losses.append(loss)

            # Compute and store average epoch loss
            avg_loss = float(np.mean(epoch_losses)) if epoch_losses else 0.0
            history["loss"].append(avg_loss)

            # Optional: could add callbacks here for logging, checkpointing, etc.

        return history