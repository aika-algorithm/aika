"""
Evaluation utilities for testing and validation.

This module provides a generic evaluation framework for assessing model
performance on various tasks and metrics.
"""

from typing import Iterable, Callable, Dict, Any
import numpy as np

from .batch_types import LabeledBatch, ModelOutputs
from .model_api import TokenModel


def evaluate(
    model: TokenModel,
    data_loader: Iterable[LabeledBatch],
    metric_fn: Callable[[ModelOutputs, LabeledBatch], Dict[str, float]],
) -> Dict[str, float]:
    """
    Generic evaluation loop for model assessment.

    This function runs the model over a dataset and aggregates metrics.
    It's flexible enough to handle various tasks (classification, generation,
    sequence labeling, etc.) through the metric_fn callback.

    Args:
        model: TokenModel implementation to evaluate
        data_loader: Iterable yielding LabeledBatch objects (e.g., DataLoader)
        metric_fn: Function that takes model outputs and labeled batch,
                  returns a dict of metric names to values for that batch.
                  Metrics will be averaged across all batches.

    Returns:
        Dictionary mapping metric names to their averaged values across
        all batches.

    Usage:
        # Define metrics for classification
        def accuracy_metric(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits  # (B, num_classes)
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels
            accuracy = np.mean(predictions == labels)
            return {"accuracy": accuracy}

        # Evaluate
        tokenizer = TokenizerAdapter.from_pretrained("bert-base-uncased")
        test_loader = DataLoader(test_dataset, tokenizer, config)
        results = evaluate(model, test_loader, accuracy_metric)
        print(f"Test accuracy: {results['accuracy']:.2%}")

        # For multiple metrics
        def multi_metric(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels

            accuracy = np.mean(predictions == labels)

            # Compute precision for binary classification
            true_positives = np.sum((predictions == 1) & (labels == 1))
            false_positives = np.sum((predictions == 1) & (labels == 0))
            precision = true_positives / (true_positives + false_positives) if (true_positives + false_positives) > 0 else 0.0

            return {
                "accuracy": accuracy,
                "precision": precision,
            }

        results = evaluate(model, test_loader, multi_metric)
    """
    metric_sums: Dict[str, float] = {}
    count = 0

    for batch in data_loader:
        # Forward pass
        outputs: ModelOutputs = model.forward_labeled(batch)

        # Compute metrics for this batch
        metrics = metric_fn(outputs, batch)

        # Accumulate metrics
        for k, v in metrics.items():
            metric_sums[k] = metric_sums.get(k, 0.0) + float(v)

        count += 1

    # Average metrics across all batches
    if count == 0:
        return {}

    return {k: v / count for k, v in metric_sums.items()}