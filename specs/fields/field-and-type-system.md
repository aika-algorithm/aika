## 📐 **Abstract Formal Description**

---

## 1. **Type System and Hierarchy**

* Let $\mathcal{T}$ be a finite set of abstract **Types**.
* The inheritance relationship among types is given by a Directed Acyclic Graph (DAG):

$$
(\mathcal{T}, \prec) \quad\text{where}\quad T_1 \prec T_2 \text{ indicates } T_2 \text{ inherits from } T_1
$$

---

## 2. **Objects and Type Instantiation**

* Let $\mathcal{O}$ be a finite set of abstract **Objects**.
* Each object $o \in \mathcal{O}$ instantiates exactly one Type:

$$
\tau : \mathcal{O} \to \mathcal{T},\quad\tau(o) = T
$$

---

## 3. **Relations Between Types and Objects**

Relations are defined purely at the **Type level**, but realized at the **Object level**:

* Let $\mathcal{R}$ be a set of named **Relations**.
* Each relation $r \in \mathcal{R}$ specifies a directed association between exactly two Types:

$$
r : T_{\text{source}} \rightarrow T_{\text{target}}
$$

* For each object $o_s$ of type $T_{\text{source}}$, the relation yields a set of related objects of type $T_{\text{target}}$:

$$
r(o_s) \subseteq \{o_t \mid \tau(o_t) = T_{\text{target}}\}
$$

Relations can be either:

* **One-to-One**: $|r(o_s)| \leq 1$ for each object $o_s$.
* **One-to-Many**: no restriction on $|r(o_s)|$.

Example (fully abstract):

* Type $T_{\text{Node}}$ related to Type $T_{\text{Edge}}$. Each Node object relates to multiple Edge objects.

---

## 4. **Field Definitions and Fields**

* Each Type $T \in \mathcal{T}$ defines a set of **Field Definitions**:

$$
F_T = \{f_1, f_2, \dots, f_n\}
$$

Each Field Definition $f \in F_T$ consists of three separate components:

* **Identity:** A unique symbolic identifier (name).

* **Input-side:** A computation function and the input fields via relations.

* **Output-side:** Connections to downstream fields via relations.

* An Object $o$ of Type $T$ instantiates a concrete **Field** from each Field Definition $f$:

$$
f(o),\quad f \in F_{\tau(o)}
$$

---

## 5. **Inheritance of Inputs and Outputs**

Fields inherit **input-side** and **output-side** independently across Types:

* **Input-side inheritance:** for each field $f$, the input-side definition at Type $T$ is:

  $$
  f_{\text{in}}(T) = \begin{cases}
  \text{defined explicitly at }T, &\text{if exists}\\[3pt]
  f_{\text{in}}(T'), &T' \prec T,\;\text{closest ancestor defining }f_{\text{in}}
  \end{cases}
  $$

* **Output-side inheritance:** similarly:

  $$
  f_{\text{out}}(T) = \begin{cases}
  \text{defined explicitly at }T, &\text{if exists}\\[3pt]
  f_{\text{out}}(T'), &T' \prec T,\;\text{closest ancestor defining }f_{\text{out}}
  \end{cases}
  $$

---

## 6. **Functions as Relations Between Field Definitions**

* Define a set of abstract **mathematical functions** $\Phi$, such as:

    * Addition ($+$)
    * Multiplication ($\cdot$)
    * Division ($/$)
    * Identity ($x\mapsto x$)
    * Exponential ($\exp$)
    * Softmax ($\text{softmax}$)
    * Generic function symbols as needed

* Each field definition at type $T$ defines its input-side explicitly via a function $\phi \in \Phi$ and references input fields via Type relations:

  $$
  f^{\cdot}_{T}(o) = \phi\left(f_{T_1}^{r_1}(o),\; f_{T_2}^{r_2}(o),\;\dots,\; f_{T_k}^{r_k}(o)\right)
  $$

---

## 7. **Abstracted Mathematical Field Access Notation**

Fully abstract field access notation (no domain-specific terms):

$$
f_{T}^{r}(o)
$$

Meaning:

* Access the field $f$ at Type $T$, from objects related via relation $r$, starting at object $o$.

Special self-reference relation denoted by `·` (dot):

* Current object: $f^{\cdot}_T(o)$

---

## 8. **Multi-Type, Multi-Object Field Computations (Generalized)**

* Some functions explicitly refer to multiple related objects, potentially across multiple types.
* The general form of a function $\phi$ with multiple referenced objects is:

  $$
  f^{\cdot}_{T}(o) = \phi\left(\{f^{r_1}_{T_1}(o')\}, \{f^{r_2}_{T_2}(o'')\}, \dots\right)
  $$

where:

* $\{f^{r_i}_{T_i}(o'')\}$ denotes the set of fields at objects reached by relation $r_i$.

---

## 9. **Explicit Example: Softmax Function over Three Types**

Define a generalized **Softmax function** involving exactly **three distinct Types**:

* **Input Type**: $T_{\text{in}}$, objects are edges $e_{\text{in}}$.
* **Central (Norm) Type**: $T_{\text{norm}}$, object is node $n$.
* **Output Type**: $T_{\text{out}}$, objects are edges $e_{\text{out}}$.

We require the following relations explicitly:

* Relation $r_{\text{in}}$ from node $n$ to input edges $e_{\text{in}}$.
* Relation $r_{\text{out}}$ from node $n$ to output edges $e_{\text{out}}$.
* Relation $r_{\text{pair}}$ directly linking each input edge $e_{\text{in}}$ to a corresponding output edge $e_{\text{out}}$.

The Softmax is defined at output edges $e_{\text{out}}$:

$$
f^{\cdot}_{T_{\text{out}}}(e_{\text{out}}) =
\frac{
\exp\left(f^{r_{\text{pair}}}_{T_{\text{in}}}(e_{\text{out}})\right)
}{
\sum_{e'_{\text{in}}\in r_{\text{in}}(n)}
\exp\left(f^{\cdot}_{T_{\text{in}}}(e'_{\text{in}})\right)
},\quad n\in r_{\text{out}}^{-1}(e_{\text{out}})
$$

* The denominator normalizes over all input edges connected to the central node $n$.
* Numerator is computed from the corresponding input edge linked via the pairing relation $r_{\text{pair}}$.

---

## ✅ **Final Abstracted Formal Summary**

| Concept                   | Definition                                                                                                |
| ------------------------- | --------------------------------------------------------------------------------------------------------- |
| Types and Hierarchy       | $(\mathcal{T},\prec)$, a DAG                                                                              |
| Objects and Types         | $\tau : \mathcal{O}\to \mathcal{T}$                                                                       |
| Relations                 | $r : T_{\text{source}}\to T_{\text{target}}$, explicitly one-to-one or one-to-many at object-level        |
| Fields and Definitions    | Instantiated per object from type-defined field definitions, independently inheritable input/output sides |
| Functions and Notation    | Abstracted notation $f^{r}_T(o)$, general functions $\phi$                                                |
| Multi-object Computations | Explicit relations, as demonstrated by abstract Softmax example                                           |
