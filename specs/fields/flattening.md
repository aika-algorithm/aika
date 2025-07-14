## 📐 **Abstract Formal Algorithms**

### Definitions (Recap):

* **Types:** $\mathcal{T}$, structured as a DAG via inheritance $(\prec)$.
* **Objects:** $\mathcal{O}$, instantiated from Types via $\tau:\mathcal{O}\to\mathcal{T}$.
* **Fields:** Each Type $T$ defines field definitions $F_T$.
* **Relations:** $r : T_{\text{source}} \to T_{\text{target}}$, realized at object-level.
* **Inheritance:** Independent for input-side $f_{\text{in}}$ and output-side $f_{\text{out}}$.
* **Flattening:** Precomputes effective field definitions per Type for runtime efficiency.

---

## 🚩 **1. Flattening Algorithm (Inheritance Initialization)**

This algorithm precomputes **effective field definitions** (inputs/outputs separately) per Type.

### Input:

* Type $T \in \mathcal{T}$

### Output:

* Effective input-side and output-side definitions for all fields at Type $T$.

### Algorithm (Mathematical pseudocode):

**FlattenInputs(Type $T$):**

```
Let Inputs = {}
for each parent type P of T:
    Inputs ← Inputs ∪ FlattenInputs(P)

for each field f explicitly defined at T:
    Inputs[f] ← f_in(T)  // Overrides inherited input or adds new

return Inputs
```

**FlattenOutputs(Type $T$):**

```
Let Outputs = {}
for each parent type P of T:
    Outputs ← Outputs ∪ FlattenOutputs(P)

for each field f explicitly defined at T:
    Outputs[f] ← f_out(T)  // Overrides inherited output or adds new

return Outputs
```

* After applying the algorithm, we have for each type $T$:

    * $F_{T}^{\text{in}} = FlattenInputs(T)$
    * $F_{T}^{\text{out}} = FlattenOutputs(T)$

These are the **flattened field definitions** (input and output) at $T$.

---

## 🚩 **2. Runtime Field Value Propagation Algorithm**

Given the flattened input-side and output-side field definitions, this algorithm propagates updated field values at runtime.

### Input:

* An object $o \in \mathcal{O}$ of Type $T$
* A field $f \in F_T$ with a computed value change (update) $\Delta$

### Algorithm (Mathematical pseudocode):

```
PropagateUpdate(object o, field f, update Δ):
    T ← τ(o)
    Outputs ← F_T^out[f]  // precomputed output relations of f at Type T

    for each (f_target, relation r, T_target) in Outputs:
        related_objects ← r(o)
        for each related_object in related_objects:
            T_rel ← τ(related_object)
            InputDef ← F_{T_rel}^in[f_target] // precomputed input definition of f_target
            Δ' ← EvaluateFunction(InputDef, related_object, Δ)
            
            if |Δ'| > tolerance(f_target):
                PropagateUpdate(related_object, f_target, Δ')
```

**Explanation**:

* The propagation algorithm uses precomputed (flattened) output definitions for fast propagation.
* For each downstream field, the algorithm:

    1. Follows the appropriate object relation.
    2. Computes the update ($\Delta'$) via the resolved input function.
    3. Propagates recursively if the update is significant.

---

## 🚩 **3. Evaluation of a Field’s Input Function**

A simplified abstract form:

```
EvaluateFunction(InputDef, object o, incoming_update Δ):
    let InputDef = (φ, {(f_1, r_1, T_1), ..., (f_k, r_k, T_k)})
    inputs = {}
    for each (f_i, r_i, T_i):
        objects_i = r_i(o)
        inputs[f_i] ← { f_i(o') for o' in objects_i }

    return φ(inputs, Δ)
```

* Function $φ$ uses fields referenced via relations to compute updated values.

---

## 🚩 **4. Explicit Example (Softmax)**

For clarity, here is a specialized runtime propagation pseudocode example for the Softmax scenario involving three types (input edges, norm node, output edges):

* Types: $T_{in}$, $T_{norm}$, $T_{out}$
* Relations: $r_{in}: T_{norm}\to T_{in}$, $r_{out}: T_{norm}\to T_{out}$, $r_{pair}: T_{out}\to T_{in}$

**Softmax Runtime Update (Pseudocode)**:

```
UpdateSoftmax(object e_out of type T_out):
    n ← r_out^(-1)(e_out)   // Node associated with e_out
    input_edges ← r_in(n)

    numerator ← exp(f_in(r_pair(e_out)))
    denominator ← sum { exp(f_in(e_in)) for e_in ∈ input_edges }

    new_value ← numerator / denominator
    Δ ← new_value - current_value(f(e_out))

    if |Δ| > tolerance:
        PropagateUpdate(e_out, f, Δ)
```

* This demonstrates clear object-type navigation, relation usage, and correct Softmax evaluation across multiple types and objects.

---

## 🚩 **Summary of Abstracted Algorithms**

| Algorithm       | Purpose                                               | Input                                  | Output                                   |
| --------------- | ----------------------------------------------------- | -------------------------------------- | ---------------------------------------- |
| **Flattening**  | Initialize inheritance, precompute inputs/outputs     | Type $T$                               | Flattened input/output field definitions |
| **Propagation** | Runtime value propagation using flattened definitions | Object $o$, Field $f$, update $\Delta$ | Propagated updates in field values       |
