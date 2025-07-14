# 📐 **Formal Mathematical Description of Queued Field Propagation**

---

## 1. **Abstract Definitions (Recap)**

We assume the abstract definitions from earlier:

* **Types** $\mathcal{T}$, with inheritance.
* **Objects** $\mathcal{O}$, instantiated from Types via $\tau:\mathcal{O}\to\mathcal{T}$.
* **Fields** $f(o)$, instantiated at Objects from Type-defined FieldDefinitions.
* **Flattened Field Definitions** from inheritance, explicitly precomputed:

    * Input-side: $F_T^{in}$, Output-side: $F_T^{out}$
* **Relations** between Types, realized as sets of object references.

---

## 2. **Queued Propagation Model**

### Key ideas:

* A **field update** can propagate:

    * **Immediately** (direct propagation), or
    * **Queued** (deferred, asynchronous processing).

* The queuing mechanism ensures:

    1. Updates occur in correct temporal and priority-based order.
    2. Updates can be grouped into discrete **processing phases**.
    3. Support for delaying updates to subsequent processing "rounds".

---

## 3. **Queue Structure**

Define a **Queue** $Q$ as a set of **events** $e$:

Each event $e$ is characterized by:

* An **Object** $o \in \mathcal{O}$.
* A **Field** $f$ at object $o$.
* A numeric **update value** $\Delta \in \mathbb{R}$.
* A **processing phase** $p$.
* A discrete **round number** $r \in \mathbb{N}$.
* A **priority** $s \in \mathbb{R}$ (e.g., absolute update magnitude).
* A **timestamp** $t \in \mathbb{N}$ (discrete temporal ordering).

We thus formally define an event as a tuple:

$$
e = (o, f, \Delta, p, r, s, t)
$$

---

## 4. **Ordering of Events**

Events in the Queue $Q$ are ordered by the tuple:

$$
(r, p, -s, t)
$$

Ordered by priority:

1. Lowest **round** $r$ first (lower rounds processed before higher rounds).
2. Then lowest **processing phase** $p$ rank.
3. Then highest priority $s$ first (larger magnitude updates processed first, hence negative sign).
4. Finally, earliest timestamp $t$.

Formally, given two events $e_i, e_j$, we define:

$$
e_i \prec e_j \quad\text{if and only if}\quad (r_i, p_i, -s_i, t_i) <_{\text{lex}} (r_j, p_j, -s_j, t_j)
$$

(Lexicographic order.)

---

## 5. **Queue Operations**

Define operations on queue $Q$:

* **Add Event**: $Q \leftarrow Q \cup \{e\}$
* **Remove Minimum Event** (highest priority):

    * $e_{\min}$ is minimal in $Q$ according to above ordering.
    * Remove from queue: $Q \leftarrow Q \setminus \{e_{\min}\}$
* **Process Queue**: Repeatedly remove and handle events until empty or timeout.

---

## 6. **Propagation Algorithm with Queuing**

Given an initial field update at object $o$:

```
PropagateUpdateQueued(object o, field f, update Δ, phase p, round_increment):
    T ← τ(o)

    if |Δ| ≤ tolerance(f):
        return

    current_round ← current queue processing round
    event_round ← current_round + (1 if round_increment else 0)

    s ← |Δ|  // priority based on absolute update magnitude
    t ← next available timestamp
    Add event (o, f, Δ, p, event_round, s, t) into Q
```

---

## 7. **Event Processing Algorithm**

To process events in the queue:

```
ProcessQueue(Q):
    while Q is not empty:
        e ← remove minimal event from Q according to (r,p,-s,t)
        (o, f, Δ, p, r, s, t) ← e

        ApplyFieldUpdate(o, f, Δ)
```

Applying field update $Δ$:

```
ApplyFieldUpdate(o, f, Δ):
    current_value ← f(o)
    updated_value ← current_value + Δ
    f(o) ← updated_value

    // propagate to downstream fields using flattened outputs
    Outputs ← F_{τ(o)}^{out}[f]

    for each (f_target, relation r', T_target, queued, phase', round_inc) in Outputs:
        related_objects ← r'(o)

        for related_object in related_objects:
            Δ' ← EvaluateFunction(F_{τ(related_object)}^{in}[f_target], related_object, Δ)

            if queued:
                PropagateUpdateQueued(related_object, f_target, Δ', phase', round_inc)
            else:
                if |Δ'| > tolerance(f_target):
                    ApplyFieldUpdate(related_object, f_target, Δ')
```

* Each output connection now explicitly carries flags:

    * **queued** (bool): whether propagation is queued or immediate.
    * **phase'**: phase for queued propagation.
    * **round\_inc**: if true, event is placed in next round.

---

## 8. **Evaluation Function (Generalized)**

The evaluation of input-side field function remains abstract as:

```
EvaluateFunction(InputDefinition, object o, incoming_update Δ):
    InputDefinition = (φ, {(f_i, r_i, T_i)})
    input_values = {}
    for each (f_i, r_i, T_i):
        related_objs = r_i(o)
        input_values[f_i] ← {f_i(o') for o' in related_objs}
    return φ(input_values, Δ)
```

---

## ✅ **Summary of Abstract Queue-Based Propagation**

| Component                    | Abstract Mathematical Definition                                  |
| ---------------------------- | ----------------------------------------------------------------- |
| Queue $Q$                    | Set of events $e=(o,f,\Delta,p,r,s,t)$, ordered lexicographically |
| Event Processing             | Remove minimal event, update fields, propagate updates            |
| Queued/Immediate Propagation | Decided per output field definition, explicit flags               |
| Rounds & Phases              | Explicitly part of events, control processing order               |
| Tolerance check              | Determines if propagation continues                               |
