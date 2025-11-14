# Spec: Tokenizer & Dataset Integration Layer (Outer Framework Only)

## 0. Scope & Separation of Concerns

### Outer Layer (this spec)

Implemented purely in **Python**, no knowledge of:

* binding signals
* neuron types
* event queues

The outer layer handles:

1. **Tokenization** of text to `input_ids` + `attention_mask`.
2. **Dataset & batching** for:

    * processing a text (inference),
    * training (supervised / semi-supervised),
    * testing / evaluation.
3. A **simple model interface** that only deals with batches of tokens and labels.

### Inner Layer (AIKA core, *not* in this spec)

Responsible for:

* mapping token IDs to embeddings,
* creating/assigning binding signals,
* setting up transformer graph, event queue, etc.

The **only contract** between the two layers is:

```python
tokens_in  →  model.forward(inputs)  →  outputs/logits
```

where `inputs` contain just token ids and masks (and maybe labels).

---

## 1. Package Structure (Python)

Create a dedicated integration package:

```text
python/
  integration/
    __init__.py
    tokenizer_adapter.py   # wrap HF-like tokenizer
    batch_types.py         # TokenizedBatch, LabeledBatch, etc.
    datasets.py            # text/label datasets, batching
    model_api.py           # abstract model interface
    processing.py          # "process a text" utilities
    training.py            # training outer loop skeleton
    evaluation.py          # eval outer loop skeleton
```

---

## 2. Tokenizer Adapter (no AIKA dependencies)

**File:** `python/integration/tokenizer_adapter.py`

Purpose: wrap a standard tokenizer (e.g. Hugging Face `PreTrainedTokenizerFast`) behind a minimal, project-specific interface.

```python
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
        ...

    @property
    def vocab_size(self) -> int:
        ...

    def save_pretrained(self, save_directory: str) -> None:
        ...

    @classmethod
    def from_pretrained(cls, path_or_name: str) -> "TokenizerBackend":
        ...


@dataclass
class TokenizedBatch:
    """
    Purely token-level view used by the outer framework.
    No AIKA-specific fields.
    """
    input_ids: np.ndarray        # shape: (batch, seq_len), dtype=int
    attention_mask: np.ndarray   # shape: (batch, seq_len), 1=real, 0=padding


class TokenizerAdapter:
    """
    Thin wrapper around a concrete tokenizer backend.
    Responsible only for text <-> ids, nothing else.
    """

    def __init__(self, backend: TokenizerBackend):
        self._backend = backend

    @property
    def vocab_size(self) -> int:
        return self._backend.vocab_size

    @classmethod
    def from_pretrained(cls, path_or_name: str) -> "TokenizerAdapter":
        """
        Implementation detail: use HF tokenizer under the hood.
        Claude Code will provide a concrete HFTokenizerBackend that
        conforms to TokenizerBackend.
        """
        backend = HFTokenizerBackend.from_pretrained(path_or_name)  # to be implemented
        return cls(backend)

    def save_pretrained(self, save_directory: str) -> None:
        self._backend.save_pretrained(save_directory)

    # -------- single text --------
    def encode(
        self,
        text: str,
        max_length: Optional[int] = None,
        truncation: bool = True,
        add_special_tokens: bool = True,
    ) -> TokenizedBatch:
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

    # -------- batch of texts --------
    def encode_batch(
        self,
        texts: List[str],
        max_length: Optional[int] = None,
        truncation: bool = True,
        padding: str = "longest",  # or "max_length"
        add_special_tokens: bool = True,
    ) -> TokenizedBatch:
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
```

Claude Code should also implement `HFTokenizerBackend` in the same file (or a submodule) using `transformers` but that’s straight-forward glue.

---

## 3. Batch & Label Types

**File:** `python/integration/batch_types.py`

These types define what is passed between:

* dataset loaders,
* tokenization layer,
* model interface,
* training/eval loops.

```python
from dataclasses import dataclass
from typing import Optional, Any, Dict
import numpy as np

@dataclass
class TokenizedBatch:
    """
    Re-export or import from tokenizer_adapter to keep a single definition.
    """
    input_ids: np.ndarray        # (B, L)
    attention_mask: np.ndarray   # (B, L)

@dataclass
class LabeledBatch:
    """
    Tokenized batch plus supervision targets (for training/eval).
    """
    tokens: TokenizedBatch
    # shape depends on task, but we keep it generic here
    labels: np.ndarray           # (B, ...) e.g. (B, L) for token labels, (B,) for class labels

@dataclass
class ModelOutputs:
    """
    Minimal output structure from the model.
    You can extend later with additional diagnostics.
    """
    logits: np.ndarray           # (B, L, C) or (B, C), depending on task
    # optional: loss, hidden states, etc.
    extra: Dict[str, Any] | None = None
```

The **outer framework only cares about these structures**. How logits are produced from `input_ids` is an internal concern of the AIKA core.

---

## 4. Datasets & Batching

**File:** `python/integration/datasets.py`

We define minimal dataset abstractions; they don’t depend on AIKA internals or binding signals.

```python
from dataclasses import dataclass
from typing import Iterable, List, Tuple, Optional, Iterator, Callable
import numpy as np

from .tokenizer_adapter import TokenizerAdapter, TokenizedBatch
from .batch_types import LabeledBatch


TextExample = Tuple[str, Optional[Any]]  # (text, label), label can be None for unsupervised

@dataclass
class TextDataset:
    """
    Simple in-memory dataset of text (and optional labels).
    """
    examples: List[TextExample]

    def __len__(self) -> int:
        return len(self.examples)

    def __iter__(self) -> Iterator[TextExample]:
        return iter(self.examples)


@dataclass
class DataLoaderConfig:
    batch_size: int
    shuffle: bool = True
    max_length: Optional[int] = None


class DataLoader:
    """
    Very simple DataLoader that:
      - batches examples
      - tokenizes them
      - produces TokenizedBatch or LabeledBatch
    """

    def __init__(
        self,
        dataset: TextDataset,
        tokenizer: TokenizerAdapter,
        config: DataLoaderConfig,
        label_transform: Optional[Callable[[List[Any]], np.ndarray]] = None,
    ):
        self.dataset = dataset
        self.tokenizer = tokenizer
        self.config = config
        self.label_transform = label_transform

    def __iter__(self) -> Iterator[LabeledBatch]:
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

            tokens: TokenizedBatch = self.tokenizer.encode_batch(
                texts,
                max_length=max_length,
            )

            if self.label_transform is not None:
                label_arr = self.label_transform(labels)
            else:
                # default: assume labels are already numeric-like
                label_arr = np.array(labels)

            yield LabeledBatch(tokens=tokens, labels=label_arr)
```

Use cases:

* **Processing a text** → use `TextDataset` with labels `None` and ignore `labels` later.
* **Supervised training** → labels as integers / one-hot, `label_transform` handles encoding if needed.
* **Semi-supervised** → labels can be optional; `label_transform` can map `None` to a special value or mask.

---

## 5. Model Interface (AIKA-agnostic)

**File:** `python/integration/model_api.py`

We keep the boundary extremely clear: outer framework only sees a `Model` that consumes `TokenizedBatch` or `LabeledBatch` and produces `ModelOutputs`.

```python
from typing import Protocol
from .batch_types import TokenizedBatch, LabeledBatch, ModelOutputs

class TokenModel(Protocol):
    """
    Abstract model interface used by the outer framework.
    Implementation will be the AIKA transformer (or any future model).
    """

    def forward_tokens(self, batch: TokenizedBatch) -> ModelOutputs:
        """
        Forward pass for inference or unsupervised scenarios.
        No labels involved, no loss computed at this level.
        """
        ...

    def forward_labeled(self, batch: LabeledBatch) -> ModelOutputs:
        """
        Forward pass for supervised/semi-supervised training or evaluation.
        May compute logits only; loss handling is up to the training loop
        or can be added later.
        """
        ...
```

Your **AIKA transformer wrapper** (which *is not part of this spec*) will implement `TokenModel` and can internally:

* map `batch.tokens.input_ids` → embeddings,
* create binding signals,
* run the event queue,
* produce logits.

Outer code never sees that.

---

## 6. Use Cases

### 6.1 Processing a Text (Inference)

**File:** `python/integration/processing.py`

```python
from typing import Dict, Any
from .tokenizer_adapter import TokenizerAdapter
from .batch_types import ModelOutputs
from .model_api import TokenModel

def process_text(
    text: str,
    tokenizer: TokenizerAdapter,
    model: TokenModel,
    max_length: int | None = None,
) -> Dict[str, Any]:
    """
    High-level helper:
      - tokenize a single text
      - run model
      - return tokens and raw model outputs
    """
    token_batch = tokenizer.encode(text, max_length=max_length)
    outputs: ModelOutputs = model.forward_tokens(token_batch)
    return {
        "input_ids": token_batch.input_ids,
        "attention_mask": token_batch.attention_mask,
        "logits": outputs.logits,
        "extra": outputs.extra,
    }
```

This addresses your **“Processing a Text”** use case, without any training logic.

---

### 6.2 Training Skeleton (Batches of Examples)

**File:** `python/integration/training.py`

This is **outer framework only**: it assumes the model implements `TokenModel`, but doesn’t prescribe how loss or optimization are done. It just defines a structure Claude Code can later extend.

```python
from dataclasses import dataclass
from typing import Iterable, Callable, Any, Dict
import numpy as np

from .batch_types import LabeledBatch, ModelOutputs
from .model_api import TokenModel

@dataclass
class TrainingConfig:
    num_epochs: int
    # you can add more later: learning_rate, clip_norm, etc.


class Trainer:
    """
    Very lightweight training skeleton.
    Does not implement optimization by default; instead, it exposes hooks.
    """

    def __init__(
        self,
        model: TokenModel,
        loss_fn: Callable[[ModelOutputs, LabeledBatch], float],
        optimizer_step: Callable[[float], None],
        config: TrainingConfig,
    ):
        """
        - model: implements TokenModel
        - loss_fn: takes model outputs + labeled batch, returns scalar loss
        - optimizer_step: callback that applies gradients after loss is computed
                          (implementation detail, not in this spec)
        """
        self.model = model
        self.loss_fn = loss_fn
        self.optimizer_step = optimizer_step
        self.config = config

    def train(self, train_loader: Iterable[LabeledBatch]) -> Dict[str, Any]:
        """
        Skeleton for training loop. Outer framework only.
        """
        history = {"loss": []}

        for epoch in range(self.config.num_epochs):
            epoch_losses: list[float] = []
            for batch in train_loader:
                outputs: ModelOutputs = self.model.forward_labeled(batch)
                loss = self.loss_fn(outputs, batch)
                self.optimizer_step(loss)
                epoch_losses.append(loss)

            history["loss"].append(float(np.mean(epoch_losses)))

        return history
```

Here:

* **Outer layer** knows nothing about gradients, optimizers, or AIKA internals.
* How `optimizer_step` works is **your choice** (e.g. using PyTorch, custom gradient mechanism, or something else integrated with AIKA).

---

### 6.3 Testing & Evaluation Skeleton

**File:** `python/integration/evaluation.py`

Again: outer framework only, using `TokenModel`.

```python
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
    Generic evaluation loop.
    metric_fn aggregates predictions and labels for a batch and returns
    metric values (e.g. accuracy, F1, etc.).
    """
    metric_sums: Dict[str, float] = {}
    count = 0

    for batch in data_loader:
        outputs: ModelOutputs = model.forward_labeled(batch)
        metrics = metric_fn(outputs, batch)
        for k, v in metrics.items():
            metric_sums[k] = metric_sums.get(k, 0.0) + float(v)
        count += 1

    return {k: v / count for k, v in metric_sums.items()}
```

This covers your **“Testing and evaluating”** use case at the outer level.

---

## 7. Summary of Interfaces / Contracts

What Claude Code needs to implement now (outer layer only):

1. **`TokenizerAdapter` + `HFTokenizerBackend`**

    * Wrap a standard tokenizer,
    * Provide `encode` and `encode_batch` returning `TokenizedBatch`.

2. **`batch_types.py`**

    * `TokenizedBatch`, `LabeledBatch`, `ModelOutputs`.

3. **`datasets.py`**

    * `TextDataset`, `DataLoaderConfig`, `DataLoader`.

4. **`model_api.py`**

    * `TokenModel` protocol with `forward_tokens` and `forward_labeled`.

5. **Utility modules**

    * `process_text` in `processing.py`,
    * `Trainer` in `training.py`,
    * `evaluate` in `evaluation.py`.

What this spec **explicitly excludes**:

* any mention of binding signals,
* any AIKA-specific field/network/event APIs,
* how token ids become embeddings/logits internally.

Those are delegated to the **inner AIKA transformer implementation**, which simply has to implement `TokenModel` for the outer framework to work.
