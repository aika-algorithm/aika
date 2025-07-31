# 🧠 **AIKA-Based Minimal Transformer Model (Formal Specification)**

This is a formal definition of a minimal transformer-like architecture expressed in terms of AIKA’s object-type hierarchy, field definitions, relations, and event-driven processing.

---

## 1. **Overview of Components and Type Definitions**

Let:

* $\mathcal{T}*N = {T*{\text{EMB}}, T_{\text{KEY}}, T_{\text{QUERY}}, T_{\text{INHIB}}, T_{\text{VALUE}}}$: neuron types
* $\mathcal{T}_A$: corresponding activation types
* $\mathcal{T}_S$: synapse types linking above neuron types
* $\mathcal{T}_L$: link types created during inference

Each token $t_i$ in a sequence has:

* A binding signal $\beta_i \in \mathcal{B}$ uniquely associated with $t_i$.

---

## 2. **Binding Signals and Propagation**

* Binding signals $\beta_i$ are initialized at **embedding activations**.
* Transition function:
  $\delta: \mathcal{B} \times \mathcal{T}_S \to \mathcal{B} \cup \{\bot\}$
  defines how BSs propagate across links. Each synapse type defines transition rules.

---

## 3. **Types and Field Definitions**

### A. **Embedding Neuron Type** ($T_{\text{EMB}}$)

* **Description**: Represents token embeddings; no input synapses; initiates BS propagation.
* **Fields**:

    * Bias: $b_{\text{emb}}$ (fixed per neuron; defines the value)
    * Output: $f_{\text{emb}}^{\cdot}(n) = b_{\text{emb}}(n)$

### B. **Key Neuron Type** ($T_{\text{KEY}}$)

* **Inputs**: Embedding neurons (via synapse type $S_{\text{emb→key}}$)
* **Fields**:

    * $f_{\text{key}}^{\cdot}(k) = \phi_{\text{key}}\left( { f_{\text{emb}}^{r}(e) \mid e \in r(k) } \right)$

### C. **Query Neuron Type** ($T_{\text{QUERY}}$)

* **Inputs**: Embedding + Key neurons (via $S_{\text{emb→query}}, S_{\text{key→query}}$)
* **Fields**:

    * $f_{\text{query}}^{\cdot}(q) = \phi_{\text{query}}\left( { f_{\text{emb}}^{r_1}(e) }, { f_{\text{key}}^{r_2}(k) } \right)$

### D. **Inhibitory Neuron Type** ($T_{\text{INHIB}}$)

* **Purpose**: Softmax normalization layer
* **Inputs**: All query neuron activations relevant to one token
* **Fields**:

    * Softmax-style function:

      $$
      f^{\cdot}_{T_{\text{INHIB}}}(i) =
      \frac{
      \exp\left(f^{r}_{T_{\text{QUERY}}}(q)\right)
      }{
      \sum_{q' \in r(i)} \exp\left(f^{\cdot}_{T_{\text{QUERY}}}(q')\right)
      }
      $$
    * Relation $r$: references all query neurons associated with the same token (i.e., same BS)

### E. **Value Neuron Type** ($T_{\text{VALUE}}$)

* **Inputs**: Inhibitory + Embedding neuron activations
* **Fields**:

    * $f^{\cdot}*{T*{\text{VALUE}}}(v) = \phi_{\text{val}}\left( { f_{\text{inhib}}^{r_1}(i) }, { f_{\text{emb}}^{r_2}(e) } \right)$

---

## 4. **Relation Graph**

Relations used (from source type to target type):

| Relation Name    | Source Type           | Target Type           | Notes       |
| ---------------- | --------------------- | --------------------- | ----------- |
| `EMB_TO_KEY`     | $T_{\text{KEY}}$   | $T_{\text{EMB}}$   | many-to-one |
| `EMB_TO_QUERY`   | $T_{\text{QUERY}}$ | $T_{\text{EMB}}$   | many-to-one |
| `KEY_TO_QUERY`   | $T_{\text{QUERY}}$ | $T_{\text{KEY}}$   | many-to-one |
| `QUERY_TO_INHIB` | $T_{\text{INHIB}}$ | $T_{\text{QUERY}}$ | one-to-many |
| `INHIB_TO_VALUE` | $T_{\text{VALUE}}$ | $T_{\text{INHIB}}$ | many-to-one |
| `EMB_TO_VALUE`   | $T_{\text{VALUE}}$ | $T_{\text{EMB}}$   | many-to-one |

---

## 5. **Synapse Transitions and BS Semantics**

| Synapse Type                | Input         | Output   | BS Transition |
| --------------------------- | ------------- | -------- | ------------- |
| $S_{\text{emb→key}}$     | EMB → KEY     | Identity |               |
| $S_{\text{emb→query}}$   | EMB → QUERY   | Identity |               |
| $S_{\text{key→query}}$   | KEY → QUERY   | Identity |               |
| $S_{\text{query→inhib}}$ | QUERY → INHIB | Identity |               |
| $S_{\text{inhib→value}}$ | INHIB → VALUE | Identity |               |
| $S_{\text{emb→value}}$   | EMB → VALUE   | Identity |               |

* Binding signal $\beta_i$ is created at the activation of EMB neuron for token $t_i$.
* It propagates only where synapse transitions permit.

---

## 6. **Event Queue and Firing Logic**

* **Initial Event**: Activate all EMB neurons for tokens $t_i$ with corresponding BS $\beta_i$.
* **Firing Rule**: If a neuron activation exceeds its threshold, it fires:

    * Adds activations
    * Links to other neurons
    * Propagates BS based on synapse transitions
* **Softmax Normalization** (Inhibitory neurons): scheduled once all relevant query activations are available.
* **Final Output**: Value neurons aggregate attention-weighted embeddings.

---

## 7. **Summary of Network Flow**

1. Token $t_i$ → Binding signal $\beta_i$.
2. $\beta_i$ → Activation of EMB neuron for $t_i$.
3. EMB → feeds into KEY and QUERY neurons.
4. KEY → fed into QUERY neurons as additive input.
5. QUERY neurons → normalized via softmax in INHIB neurons.
6. INHIB + EMB → combined into VALUE neurons → final output.

---

## ✅ **Ready for Implementation in AIKA**

This model now provides:

* A clean set of neuron and synapse types.
* A binding-signal-aware, sparse attention mechanism.
* A field-graph-based softmax.
* No fixed-size matrices; attention logic unfolds over dynamically instantiated neurons and activations.

Would you like:

1. A visual sketch of the graph?
2. A set of concrete type and relation definitions for implementation?
3. A Python example using pybind11 bindings that mimics this minimal transformer?

Let me know how you'd like to proceed.
