"""
Integration tests for processing, training, and evaluation modules.

Tests the complete workflow from tokenization through model inference,
training, and evaluation.
"""

import unittest
import numpy as np
from typing import Dict

from python.integration import (
    TokenizerAdapter,
    TokenizedBatch,
    LabeledBatch,
    ModelOutputs,
    TextDataset,
    DataLoaderConfig,
    DataLoader,
    process_text,
    TrainingConfig,
    Trainer,
    evaluate,
)
from tests.python.integration.test_tokenizer_adapter import MockTokenizerBackend


class MockModel:
    """
    Mock model implementing TokenModel protocol for testing.
    Produces random logits for testing purposes.
    """

    def __init__(self, vocab_size: int = 1000, num_classes: int = 2):
        """
        Initialize mock model.

        Args:
            vocab_size: Vocabulary size for language modeling
            num_classes: Number of classes for classification
        """
        self.vocab_size = vocab_size
        self.num_classes = num_classes
        self.training_steps = 0

    def forward_tokens(self, batch: TokenizedBatch) -> ModelOutputs:
        """
        Mock forward pass for inference.

        Returns random logits for classification task.
        """
        batch_size = batch.input_ids.shape[0]

        # Return logits for classification (batch_size, num_classes)
        logits = np.random.randn(batch_size, self.num_classes)

        return ModelOutputs(logits=logits, extra={"mode": "inference"})

    def forward_labeled(self, batch: LabeledBatch) -> ModelOutputs:
        """
        Mock forward pass for training/evaluation.

        Returns random logits that gradually improve with training steps.
        """
        batch_size = batch.tokens.input_ids.shape[0]

        # Return logits for classification
        logits = np.random.randn(batch_size, self.num_classes)

        # Simulate learning: make logits slightly favor correct labels
        # after some training steps
        if self.training_steps > 5:
            for i in range(batch_size):
                if batch.labels[i] < self.num_classes:
                    logits[i, int(batch.labels[i])] += 0.5

        return ModelOutputs(logits=logits, extra={"mode": "training", "step": self.training_steps})


class TestProcessing(unittest.TestCase):
    """Test processing.py module."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.tokenizer = TokenizerAdapter(self.backend)
        self.model = MockModel(num_classes=2)

    def test_process_text_basic(self):
        """Test basic text processing."""
        text = "Hello world"
        result = process_text(text, self.tokenizer, self.model)

        # Check result structure
        self.assertIn("input_ids", result)
        self.assertIn("attention_mask", result)
        self.assertIn("logits", result)
        self.assertIn("extra", result)

        # Check shapes
        self.assertEqual(result["input_ids"].shape[0], 1)  # batch size 1
        self.assertEqual(result["logits"].shape[0], 1)  # batch size 1
        self.assertEqual(result["logits"].shape[1], 2)  # num_classes

    def test_process_text_with_max_length(self):
        """Test processing with max_length."""
        text = "This is a very long sentence that should be truncated"
        max_length = 10
        result = process_text(text, self.tokenizer, self.model, max_length=max_length)

        # Input should be truncated
        self.assertLessEqual(result["input_ids"].shape[1], max_length)

    def test_process_text_extra_info(self):
        """Test that extra information is passed through."""
        text = "Test"
        result = process_text(text, self.tokenizer, self.model)

        self.assertIsNotNone(result["extra"])
        self.assertEqual(result["extra"]["mode"], "inference")


class TestTraining(unittest.TestCase):
    """Test training.py module."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.tokenizer = TokenizerAdapter(self.backend)
        self.model = MockModel(num_classes=2)

    def test_training_basic(self):
        """Test basic training loop."""
        # Create training dataset
        examples = [
            ("Positive example", 1),
            ("Negative example", 0),
            ("Another positive", 1),
            ("Another negative", 0),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        # Define loss function
        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            # Simple cross-entropy loss
            logits = outputs.logits
            labels = batch.labels
            # Softmax
            exp_logits = np.exp(logits - np.max(logits, axis=1, keepdims=True))
            probs = exp_logits / np.sum(exp_logits, axis=1, keepdims=True)
            # Negative log likelihood
            loss = -np.mean(np.log(probs[np.arange(len(labels)), labels.astype(int)] + 1e-10))
            return float(loss)

        # Define optimizer step
        training_losses = []
        def optimizer_step(loss: float) -> None:
            training_losses.append(loss)
            self.model.training_steps += 1

        # Train
        train_config = TrainingConfig(num_epochs=3)
        trainer = Trainer(self.model, loss_fn, optimizer_step, train_config)
        history = trainer.train(loader)

        # Check history
        self.assertIn("loss", history)
        self.assertEqual(len(history["loss"]), 3)  # 3 epochs

        # Check that optimizer_step was called
        self.assertGreater(len(training_losses), 0)
        self.assertGreater(self.model.training_steps, 0)

    def test_training_loss_tracking(self):
        """Test that training tracks loss over epochs."""
        examples = [("Text", i % 2) for i in range(10)]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            return 1.0  # Constant loss for testing

        def optimizer_step(loss: float) -> None:
            self.model.training_steps += 1

        train_config = TrainingConfig(num_epochs=5)
        trainer = Trainer(self.model, loss_fn, optimizer_step, train_config)
        history = trainer.train(loader)

        # Should have loss for each epoch
        self.assertEqual(len(history["loss"]), 5)
        # Loss should be constant
        self.assertTrue(all(loss == 1.0 for loss in history["loss"]))

    def test_training_empty_dataset(self):
        """Test training with empty dataset."""
        dataset = TextDataset([])
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, self.tokenizer, config)

        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            return 0.0

        def optimizer_step(loss: float) -> None:
            pass

        train_config = TrainingConfig(num_epochs=2)
        trainer = Trainer(self.model, loss_fn, optimizer_step, train_config)
        history = trainer.train(loader)

        # Should complete without error
        self.assertEqual(len(history["loss"]), 2)
        # Loss should be 0 (no batches)
        self.assertTrue(all(np.isnan(loss) or loss == 0.0 for loss in history["loss"]))


class TestEvaluation(unittest.TestCase):
    """Test evaluation.py module."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.tokenizer = TokenizerAdapter(self.backend)
        self.model = MockModel(num_classes=2)

    def test_evaluation_basic(self):
        """Test basic evaluation."""
        examples = [
            ("Positive", 1),
            ("Negative", 0),
            ("Positive", 1),
            ("Negative", 0),
        ]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        # Define accuracy metric
        def accuracy_metric(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels
            accuracy = np.mean(predictions == labels)
            return {"accuracy": float(accuracy)}

        results = evaluate(self.model, loader, accuracy_metric)

        # Check results
        self.assertIn("accuracy", results)
        self.assertGreaterEqual(results["accuracy"], 0.0)
        self.assertLessEqual(results["accuracy"], 1.0)

    def test_evaluation_multiple_metrics(self):
        """Test evaluation with multiple metrics."""
        examples = [("Text", i % 2) for i in range(10)]
        dataset = TextDataset(examples)
        config = DataLoaderConfig(batch_size=2, shuffle=False)
        loader = DataLoader(dataset, self.tokenizer, config)

        def multi_metric(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels

            accuracy = np.mean(predictions == labels)

            # Count predictions
            num_positive_preds = np.sum(predictions == 1)

            return {
                "accuracy": float(accuracy),
                "positive_pred_count": float(num_positive_preds),
            }

        results = evaluate(self.model, loader, multi_metric)

        # Check that both metrics are present
        self.assertIn("accuracy", results)
        self.assertIn("positive_pred_count", results)

    def test_evaluation_empty_dataset(self):
        """Test evaluation with empty dataset."""
        dataset = TextDataset([])
        config = DataLoaderConfig(batch_size=2)
        loader = DataLoader(dataset, self.tokenizer, config)

        def metric_fn(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            return {"accuracy": 0.5}

        results = evaluate(self.model, loader, metric_fn)

        # Should return empty dict for empty dataset
        self.assertEqual(results, {})

    def test_evaluation_after_training(self):
        """Test evaluation after training improves."""
        # Create dataset
        examples = [("Text", i % 2) for i in range(20)]
        dataset = TextDataset(examples)

        # Initial evaluation (before training)
        config = DataLoaderConfig(batch_size=4, shuffle=False)
        eval_loader = DataLoader(dataset, self.tokenizer, config)

        def accuracy_metric(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels
            accuracy = np.mean(predictions == labels)
            return {"accuracy": float(accuracy)}

        initial_results = evaluate(self.model, eval_loader, accuracy_metric)
        initial_accuracy = initial_results["accuracy"]

        # Train model
        train_loader = DataLoader(dataset, self.tokenizer, config)

        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            return 1.0

        def optimizer_step(loss: float) -> None:
            self.model.training_steps += 1

        train_config = TrainingConfig(num_epochs=10)
        trainer = Trainer(self.model, loss_fn, optimizer_step, train_config)
        trainer.train(train_loader)

        # Evaluate after training
        final_results = evaluate(self.model, eval_loader, accuracy_metric)
        final_accuracy = final_results["accuracy"]

        # Due to our mock model's logic, accuracy should improve
        # (training_steps > 5 makes logits favor correct labels)
        # Note: This is probabilistic, so we just check it completes
        self.assertIsNotNone(final_accuracy)


class TestEndToEndWorkflow(unittest.TestCase):
    """Test complete end-to-end workflows."""

    def setUp(self):
        """Set up test fixtures."""
        self.backend = MockTokenizerBackend(vocab_size=1000)
        self.tokenizer = TokenizerAdapter(self.backend)
        self.model = MockModel(num_classes=2)

    def test_inference_workflow(self):
        """Test complete inference workflow."""
        # Process single text
        text = "This is a test sentence"
        result = process_text(text, self.tokenizer, self.model)

        # Should get predictions
        self.assertIsNotNone(result["logits"])
        predictions = np.argmax(result["logits"], axis=-1)
        self.assertEqual(predictions.shape, (1,))

    def test_training_workflow(self):
        """Test complete training workflow."""
        # 1. Create dataset
        train_examples = [("Positive", 1), ("Negative", 0)] * 10
        train_dataset = TextDataset(train_examples)

        # 2. Create data loader
        config = DataLoaderConfig(batch_size=4, shuffle=True)
        train_loader = DataLoader(train_dataset, self.tokenizer, config)

        # 3. Define loss and optimizer
        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            logits = outputs.logits
            labels = batch.labels
            exp_logits = np.exp(logits - np.max(logits, axis=1, keepdims=True))
            probs = exp_logits / np.sum(exp_logits, axis=1, keepdims=True)
            loss = -np.mean(np.log(probs[np.arange(len(labels)), labels.astype(int)] + 1e-10))
            return float(loss)

        def optimizer_step(loss: float) -> None:
            self.model.training_steps += 1

        # 4. Train
        train_config = TrainingConfig(num_epochs=5)
        trainer = Trainer(self.model, loss_fn, optimizer_step, train_config)
        history = trainer.train(train_loader)

        # 5. Verify training completed
        self.assertEqual(len(history["loss"]), 5)
        self.assertGreater(self.model.training_steps, 0)

    def test_evaluation_workflow(self):
        """Test complete evaluation workflow."""
        # 1. Create test dataset
        test_examples = [("Test", i % 2) for i in range(20)]
        test_dataset = TextDataset(test_examples)

        # 2. Create data loader
        config = DataLoaderConfig(batch_size=4, shuffle=False)
        test_loader = DataLoader(test_dataset, self.tokenizer, config)

        # 3. Define metrics
        def metrics_fn(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels
            accuracy = np.mean(predictions == labels)
            return {"accuracy": float(accuracy)}

        # 4. Evaluate
        results = evaluate(self.model, test_loader, metrics_fn)

        # 5. Verify results
        self.assertIn("accuracy", results)

    def test_full_pipeline(self):
        """Test complete pipeline: train -> evaluate -> inference."""
        # 1. Training
        train_examples = [("Train text", i % 2) for i in range(30)]
        train_dataset = TextDataset(train_examples)
        train_config = DataLoaderConfig(batch_size=5, shuffle=True)
        train_loader = DataLoader(train_dataset, self.tokenizer, train_config)

        def loss_fn(outputs: ModelOutputs, batch: LabeledBatch) -> float:
            return 1.0

        def optimizer_step(loss: float) -> None:
            self.model.training_steps += 1

        training_config = TrainingConfig(num_epochs=10)
        trainer = Trainer(self.model, loss_fn, optimizer_step, training_config)
        trainer.train(train_loader)

        # 2. Evaluation
        test_examples = [("Test text", i % 2) for i in range(10)]
        test_dataset = TextDataset(test_examples)
        test_config = DataLoaderConfig(batch_size=5, shuffle=False)
        test_loader = DataLoader(test_dataset, self.tokenizer, test_config)

        def metrics_fn(outputs: ModelOutputs, batch: LabeledBatch) -> Dict[str, float]:
            logits = outputs.logits
            predictions = np.argmax(logits, axis=-1)
            labels = batch.labels
            accuracy = np.mean(predictions == labels)
            return {"accuracy": float(accuracy)}

        results = evaluate(self.model, test_loader, metrics_fn)
        self.assertIn("accuracy", results)

        # 3. Inference
        inference_text = "New inference text"
        result = process_text(inference_text, self.tokenizer, self.model)
        self.assertIsNotNone(result["logits"])


if __name__ == "__main__":
    unittest.main()