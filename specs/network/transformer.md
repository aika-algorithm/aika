# **AIKA-Based Minimal Transformer**

## 1) Type Sets

**Neuron types**

$$
\mathcal{T}_N=\{T_{\text{EMB}},T_{\text{KEY}},T_{\text{QUERY}},T_{\text{VALUE}},T_{\text{SOFTMAX}},T_{\text{DOT}}^{\text{(abs)}},T_{\text{COMP}},T_{\text{MIX}}\}
$$

with $T_{\text{COMP}},T_{\text{MIX}}$ <: $T_{\text{DOT}}^{\text{(abs)}}$.

* **Activations / Synapses / Links**
  $\mathcal{T}_A, \mathcal{T}_S, \mathcal{T}_L$ as before.

* **Binding signals** $\beta_i$ per token $t_i$ as before; created at $T_{\text{EMB}}$ activations.

---

## 2) Fields (by object type)

### A. Neuron (all $\tau(n)\in \mathcal{T}_N$)

| Field                        | Meaning                        |
|------------------------------| ------------------------------ |
| $f_{\text{bias}}^{\cdot}(n)$ | Static scalar bias (optional). |

### B. Synapse (all $\tau(s)\in \mathcal{T}_S$)

| Field                          | Meaning               |
|--------------------------------| --------------------- |
| $f_{\text{weight}}^{\cdot}(s)$ | Static scalar weight. |

### C. Activation (all $\tau(a)\in \mathcal{T}_A$)

| Field                         | Meaning                   | Definition                                                  |
|-------------------------------| ------------------------- |-------------------------------------------------------------|
| $f_{\text{net}}^{\cdot}(a)$   | Aggregated pre-activation | depends on neuron subtype (below)                           |
| $f_{\text{val}}^{\cdot}(a)$   | Output value              | $\phi(f_{\text{net}}^{\cdot}(a))$ (identity/ReLU as chosen) |
| $f_{\text{fired}}^{\cdot}(a)$ | Threshold flag            | $\left[ f_{\text{val}}^{\cdot}(a)>\theta \right]$                      |
| $bs(a)$                       | Binding signal            | carried from inputs via transitions                         |

### D. Link (all (\tau(l)\in \mathcal{T}_L))

| Field                                 | Meaning          | Definition                                                                        |
|---------------------------------------| ---------------- |-----------------------------------------------------------------------------------|
| $f_{\text{weightedInput}}^{\cdot}(l)$ | Weighted message | $f_{\text{weight}}^{\text{SYNAPSE}(l)}\cdot f_{\text{val}}^{\text{INPUT}(l)}$ |

> **Note**: we will override the aggregation rule $f_{\text{net}}$ for the **Dot-Product** family and the **Softmax**’s link logic below.

---

## 3) New Dot-Product Family

We introduce **paired input links** *at the input side* of Dot-Product neurons. Each pair consists of two inbound links bound together by a link–link relation (see Relations §4).

### 3.1 Definition of a Pair

For a Dot-Product activation $a$ (either COMP or MIX), a **pair** $p\in \mathcal{P}(a)$ is an ordered 2-tuple of inbound links $(l^{(1)},l^{(2)})$ such that:

* both $\text{OUTPUT}(l^{(1)})=\text{OUTPUT}(l^{(2)})=a$,
* $(l^{(1)},l^{(2)})$ are connected by $\text{PAIR\_IN}$ (link↔link),
* their **synapse types** match the neuron’s role (see 3.2 / 3.3).

We define the **pair contribution**

$$
C(p)=\big(f_{\text{weightedInput}}(l^{(1)})\big)\cdot\big(f_{\text{weightedInput}}(l^{(2)})\big).
$$

Then the Dot-Product aggregation is:

$$
f_{\text{net}}^{\text{DOT}}(a)=\sum_{p\in \mathcal{P}(a)} C(p).
$$

By default $\phi$ is identity for the DOT family, so $f_{\text{val}}^{\text{DOT}}(a)=f_{\text{net}}^{\text{DOT}}(a)$.

#### 3.2 $T_{\text{COMP}}$ (Comparison)

* **Inputs (paired)**: one link from $T_{\text{KEY}}$ and one from $T_{\text{QUERY}}$.
  Synapse types: $S_{\text{key}\to\text{comp}}$ and $S_{\text{query}\to\text{comp}}$.
  These two links are paired (one pair per key–query match element).
* **Output**: links to **Softmax** activations via $S_{\text{comp}\to\text{softmax}}$.
  Intuition: produces **scores/logits**.

#### 3.3 $T_{\text{MIX}}$ (Mixing)

* **Inputs (paired)**: one link from $T_{\text{VALUE}}$ and one from $T_{\text{SOFTMAX}}$.
  Synapse types: $S_{\text{value}\to\text{mix}}$ and $S_{\text{softmax}\to\text{mix}}$.
  These two links are paired so each value is weighted by the corresponding softmax weight.
* **Output**: links to **Softmax** via $S_{\text{mix}\to\text{softmax}}$ *(per your request; see Note below)* or directly to a downstream aggregation neuron if you skip re-normalization.

> **Note (design choice):** Standard attention usually **does not** re-softmax after mixing. You explicitly asked that **Mix → Softmax** also exists; we therefore keep $S_{\text{mix}\to\text{softmax}}$ but mark it as optional if you want canonical attention.

---

## 4) Relations (updated)

| Relation       | From       | To         | Card. | Purpose                                                                                                                                                       |
| -------------- | ---------- | ---------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **INPUT**      | Link       | Activation | 1:1   | Link’s source activation                                                                                                                                      |
| **OUTPUT**     | Link       | Activation | 1:1   | Link’s target activation                                                                                                                                      |
| **SYNAPSE**    | Link       | Synapse    | 1:1   | Link’s synapse instance/type                                                                                                                                  |
| **PAIR_IN**    | Link       | Link       | 1:1   | **Pairs two inbound links into a DOT neuron** (KEY↔QUERY for COMP; VALUE↔SOFTMAX for MIX)                                                                     |
| **PAIR_IO**    | Link       | Link       | 1:1   | **Pairs one inbound link of SOFTMAX (from COMP or MIX) with one outbound link of the same SOFTMAX** (used to carry the same score/slot through normalization) |
| **INPUT**      | Synapse    | Neuron     | 1:1   | Synapse’s source neuron                                                                                                                                       |
| **OUTPUT**     | Synapse    | Neuron     | 1:1   | Synapse’s target neuron                                                                                                                                       |
| **ACTIVATION** | Neuron     | Activation | 1:N   | Neuron ↔ its activations                                                                                                                                      |
| **NEURON**     | Activation | Neuron     | 1:1   | Reverse                                                                                                                                                       |
| **INPUT**      | Neuron     | Synapse    | 1:N   | Incoming synapses of a neuron                                                                                                                                 |
| **OUTPUT**     | Neuron     | Synapse    | 1:N   | Outgoing synapses of a neuron                                                                                                                                 |
| **SELF**       | Any        | Same       | 1:1   | Identity                                                                                                                                                      |

> Differences vs. your original: **PAIR_IN** is now used **exclusively** at the **input** of Dot-Product neurons (COMP/MIX). For **SOFTMAX**, we use **PAIR_IO** to associate each **incoming score link** with exactly one **outgoing normalized link**, slot-by-slot.

---

## 5) Softmax Neuron (renamed, with Paired IO)

Let $a_{\sigma}$ be a **Softmax** activation. It receives **scores** from either COMP (usual attention) or MIX (if you choose to re-normalize), and emits normalized weights to downstream targets.

For each outgoing link $l_{\text{out}}$ of $a_{\sigma}$, there exists exactly one paired incoming link $l_{\text{in}}=\text{PAIR\_IO}(l_{\text{out}})$ into the same $a_{\sigma}$. Define a **competition set** $\mathcal{L}_{\text{in}}(a_{\sigma},g)$ as all incoming links to $a_{\sigma}$ that share the same *grouping key* $g$.
**Grouping (g)** should at minimum include the **binding signal** (e.g., *per-query* competition). You may extend $g$ with head index, time step, etc.

Then the outgoing link's effective message is:

$$
f_{\text{weightedInput}}(l_{\text{out}})=
\frac{
\exp!\big(f_{\text{val}}^{\text{INPUT}(l_{\text{in}})}\big)
}{
\sum_{l'\in \mathcal{L}_{\text{in}}(a_{\sigma},g)}
\exp!\big(f_{\text{val}}^{\text{INPUT}(l')}\big)
}\cdot
f_{\text{weight}}^{\text{SYNAPSE}(l_{\text{out}})}.
$$

This preserves your original "paired-link softmax" idea, but now the pairing is **IO-paired at Softmax**, while Dot-Product pairing lives **on the inputs** of COMP/MIX.

---

## 6) Synapse Types & BS Transitions

All BS transitions are **identity** unless otherwise noted (you can restrict propagation where needed).

| Synapse Type                                   | From → To      | Purpose                                       | BS |
|------------------------------------------------| -------------- | --------------------------------------------- | -- |
| $S_{\text{emb}\to\text{key}}$                  | EMB → KEY      | build keys                                    | id |
| $S_{\text{emb}\to\text{query}}$                | EMB → QUERY    | build queries                                 | id |
| $S_{\text{emb}\to\text{value}}$                | EMB → VALUE    | build values                                  | id |
| $S_{\text{key}\to\text{comp}}$                 | KEY → COMP     | COMP input (paired with QUERY)                | id |
| $S_{\text{query}\to\text{comp}}$               | QUERY → COMP   | COMP input (paired with KEY)                  | id |
| $S_{\text{comp}\to\text{softmax}}$             | COMP → SOFTMAX | provide scores                                | id |
| $S_{\text{softmax}\to\text{mix}}$              | SOFTMAX → MIX  | provide attention weights (paired with VALUE) | id |
| $S_{\text{value}\to\text{mix}}$                | VALUE → MIX    | provide values (paired with SOFTMAX)          | id |
| $S_{\text{mix}\to\text{softmax}}$ *(optional)* | MIX → SOFTMAX  | re-normalize/gating if desired                | id |

> **Pairing constraints:**
>
> * For **COMP**: every inbound $S_{\text{key}\to\text{comp}}$ link must be **PAIR_IN** with exactly one inbound $S_{\text{query}\to\text{comp}}$ link (and vice versa), targeting the **same** COMP activation.
> * For **MIX**: every inbound $S_{\text{value}\to\text{mix}}$ link must be **PAIR_IN** with exactly one inbound $S_{\text{softmax}\to\text{mix}}$ link.
> * For **SOFTMAX**: each outbound link must **PAIR_IO** with **one** inbound score link; softmax is computed over all inbound score links in the same group $g$.

---

## 7) Event & Scheduling Logic

1. **Initialization:** For tokens $t_i$, create EMB activations with $\beta_i$; fire to produce KEY/QUERY/VALUE via $S_{\text{emb}\to *}$.
2. **Comparison:** When a COMP activation has at least one valid **PAIR_IN** key–query pair, compute
   $f_{\text{val}}^{\text{COMP}}=\sum_{p} C(p)$ and emit score links via $S_{\text{comp}\to\text{softmax}}$.
3. **Softmax (attention):** For each $a_{\sigma}$, once the competition set $\mathcal{L}_{\text{in}}(a_{\sigma},g)$ is complete for a group $g$, produce normalized **outgoing** links using the formula in §5 (IO-paired with their originating score links).
4. **Mixing:** When a MIX activation has available **paired** (VALUE, SOFTMAX) inputs, compute
   $f_{\text{val}}^{\text{MIX}}=\sum_{p} C(p)$.

    * If **no re-softmax**: route MIX outputs directly to the next stage (e.g., residual/MLP).
    * If **re-softmax enabled**: emit $S_{\text{mix}\to\text{softmax}}$ scores and repeat §5 for its groups.
5. **Output:** Downstream neurons aggregate MIX outputs (and possibly normalized MIX if step 4b) to form the layer output.

**Event ordering hints (AIKA-friendly):**

* Fire **COMP** as soon as any (KEY,QUERY) pair arrives; mark groups $g$ as “open”.
* Schedule **SOFTMAX** per group $g$ when it’s quiescent or reaches a boundary condition (e.g., end of window, causal fence).
* Fire **MIX** whenever a (VALUE, SOFTMAX) pair is present; MIX is purely multiplicative-sum, so it streams well.
* If you enable re-softmax after MIX, schedule the second **SOFTMAX** exactly like the first, but with different grouping $g'$ if needed.

---

## 8) Optional Field Notes / Implementation Aids

* **Pair identity:** add a small integer/string field $\text{pair\_id}(l)$ on inbound links to COMP/MIX so **PAIR_IN** can be validated cheaply.
* **Group identity for softmax:** define $g=(bs,\text{head},\text{query\_pos})$ (or your chosen subset) and cache group accumulators for the denominator.
* **Numerical stability:** implement log-sum-exp in the **SOFTMAX** denominator.

---

## 9) End-to-End Flow (concise)

1. EMB $\xrightarrow{}$ (KEY, QUERY, VALUE)
2. (KEY, QUERY) **paired** $\xrightarrow{T_{\text{COMP}}}$ scores
3. scores $\xrightarrow{T_{\text{SOFTMAX}}}$ attention weights (grouped by $g$)
4. (VALUE, weights) **paired** $\xrightarrow{T_{\text{MIX}}}$ mixed output
5. *(optional)* MIX scores $\xrightarrow{T_{\text{SOFTMAX}}}$ re-normalized/gated output
