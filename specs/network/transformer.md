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

Excellent clarification. Based on your requirements, I’ll now refine **Section 3** (Field Definitions) and **Section 4** (Relations) to specify all fields and their corresponding mathematical functions **explicitly per object type** (Neuron, Synapse, Activation, Link), and account for the special case of the **Inhibitory neuron** that performs a softmax-like normalization using paired links.

---

## 3. **Field Definitions and Functions per Object Type**

We define the following fields for each type category:

### A. **Neuron Object Fields** (Object type $\tau(n) \in \mathcal{T}_N$)

| Field  | Name                            | Description                                              | Function |
| ------ | ------------------------------- | -------------------------------------------------------- | -------- |
| `bias` | $f_{\text{bias}}^{\cdot}(n)$ | Constant scalar per neuron (external input or parameter) | Static   |

---

### B. **Synapse Object Fields** (Object type $\tau(s) \in \mathcal{T}_S$)

| Field    | Name                              | Description                           | Function |
| -------- | --------------------------------- | ------------------------------------- | -------- |
| `weight` | $f_{\text{weight}}^{\cdot}(s)$ | Scalar weight applied to input values | Static   |

---

### C. **Activation Object Fields** (Object type $\tau(a) \in \mathcal{T}_A$)

| Field   | Name                             | Description                        | Function                                                                                                            |
| ------- | -------------------------------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| `net`   | $f_{\text{net}}^{\cdot}(a)$   | Sum of weighted inputs             | $f_{\text{net}}(a) = \sum_{\texttt{INPUT}(l)=a} f_{\text{weightedInput}}^{\cdot}(l)$                            |
| `value` | $f_{\text{val}}^{\cdot}(a)$   | Activation output                  | $f_{\text{val}}(a) = \phi(f_{\text{net}}(a))$ where $\phi$ is the activation function (e.g., ReLU or identity) |
| `fired` | $f_{\text{fired}}^{\cdot}(a)$ | Boolean, true if value > threshold | $f_{\text{fired}}(a) = [f_{\text{val}}(a) > \theta]$                                                            |

---

### D. **Link Object Fields** (Object type $\tau(l) \in \mathcal{T}_L$)

| Field           | Name                                     | Description                                    | Function                                                                                      |
| --------------- | ---------------------------------------- | ---------------------------------------------- | --------------------------------------------------------------------------------------------- |
| `weightedInput` | $f_{\text{weightedInput}}^{\cdot}(l)$ | Product of input activation and synapse weight | $f(l) = f_{\text{weight}}^{\texttt{SYNAPSE}(l)} \cdot f_{\text{val}}^{\texttt{INPUT}(l)}$ |

---

## 🧮 Special Case: **Inhibitory Neuron (Softmax)**

Inhibitory neurons deviate from this structure. Each output link has a **pair input link** (via relation $\texttt{PAIR_IN}$). The softmax is computed **per output link**, using a norm defined on the target activation (i.e. inhibitory activation):

Let:

* $l_{\text{out}}$: output link
* $l_{\text{in}} = \texttt{PAIR_IN}(l_{\text{out}})$: paired input link
* $a_{\text{norm}} = \texttt{OUTPUT}(l_{\text{out}})$: inhibitory activation receiving softmaxed inputs

We define:

### Link Field: Softmax Output

$$
f_{\text{weightedInput}}^{\cdot}(l_{\text{out}}) =
\frac{
\exp\left(f_{\text{val}}^{\texttt{INPUT}(l_{\text{in}})}\right)
}{
\sum_{l' \in \texttt{INPUT}^{-1}(a_{\text{norm}})}
\exp\left(f_{\text{val}}^{\texttt{INPUT}(\texttt{PAIR_IN}(l'))}\right)
}
\cdot
f_{\text{weight}}^{\texttt{SYNAPSE}(l_{\text{out}})}
$$

Explanation:

* The numerator is the exponential of the **input activation** of the *paired* input link.
* The denominator is a sum of exponentials over all paired input links leading to the same inhibitory activation.
* This implements the softmax attention vector without matrix notation.

---

## 4. **Relations (Expanded and Clarified)**

All relations are declared with source/target types and directionality. The reverse relation is implied and named accordingly.

| Relation     | From Type  | To Type    | Cardinality | Purpose                                            |
| ------------ | ---------- | ---------- | ----------- | -------------------------------------------------- |
| `INPUT`      | Link       | Activation | 1:1         | Source activation                                  |
| `OUTPUT`     | Link       | Activation | 1:1         | Target activation                                  |
| `SYNAPSE`    | Link       | Synapse    | 1:1         | Originating synapse                                |
| `PAIR_IN`    | Link       | Link       | 1:1         | Paired input link (softmax)                        |
| `PAIR_OUT`   | Link       | Link       | 1:1         | Paired output link (softmax), reverse of `PAIR_IN` |
| `INPUT`      | Synapse    | Neuron     | 1:1         | Source neuron                                      |
| `OUTPUT`     | Synapse    | Neuron     | 1:1         | Target neuron                                      |
| `ACTIVATION` | Neuron     | Activation | 1\:N        | Associated activations                             |
| `NEURON`     | Activation | Neuron     | 1:1         | Reverse of above                                   |
| `INPUT`      | Neuron     | Synapse    | 1\:N        | Incoming synapses                                  |
| `OUTPUT`     | Neuron     | Synapse    | 1\:N        | Outgoing synapses                                  |
| `SELF`       | Any        | Same       | 1:1         | Identity/self-reference                            |

---

### Optional: Token Binding via BS

To track **which token identity** (binding signal) each activation refers to:

| Field | Object     | Description                                                 |
| ----- | ---------- | ----------------------------------------------------------- |
| `bs`  | Activation | The binding signal $\beta_i$ carried with the activation |

Transitions in synapses define how $\beta_i$ changes. Invalid transitions block linking.

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
