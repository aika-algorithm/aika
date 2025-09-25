# Abstract Mathematical Model for the Neural Network Module

This model formalizes the Neural Network Module of the AIKA Project, building upon the Fields Module (as described in the provided abstract formal description). It references the Fields Module's type system ($\mathcal{T}$), object set ($\mathcal{O}$), relations ($\mathcal{R}$), field definitions ($F_T$), and functional computations ($\phi \in \Phi$) without duplication. The Neural Network Module instantiates these constructs to define static (neural) and dynamic (activation) graphs, with type-specialized hierarchies for neurons, synapses, activations, and links. Mathematical models for field computations (e.g., activation thresholds, propagation) are deferred to the separate Python-based module, analogous to PyTorch's separation of graph construction from computational semantics.

## High-Level Ontology

The Neural Network Module extends the Fields Module's dual-graph structure ($G_{\text{field}}$ and $G_{\text{object}}$) to support:

1. **Type System Specialization**: Subsets of $\mathcal{T}$ for neural elements.
2. **Relational Object Graph Extension**: Instances in $\mathcal{O}$ with neural-specific relations.
3. **Functional Graph Layer Integration**: Fields anchored to neural objects, enabling computations like threshold checks.
4. **Neural Network Layer**: Static graph $G_{\text{neural}}$ representing knowledge.
5. **Activations Network Layer**: Dynamic graph $G_{\text{activations}}$ for inference.

All elements (neurons, synapses, activations, links) are objects $o \in \mathcal{O}$ typed via $\tau: \mathcal{O} \to \mathcal{T}$, with relations $r \in \mathcal{R}$ navigating the graphs. Event-driven updates (e.g., firing) propagate via the Fields Module's queue, triggering instantiations and bindings.

## Type Hierarchy $\mathcal{T}$

The type system is partitioned into specialized subsets:

- $\mathcal{T}_N \subseteq \mathcal{T}$: Neuron types (e.g., conjunctive, disjunctive, inhibitory).
- $\mathcal{T}_A \subseteq \mathcal{T}$: Activation types (e.g., conjunctive, disjunctive, inhibitory).
- $\mathcal{T}_S \subseteq \mathcal{T}$: Synapse types (e.g., conjunctive, disjunctive).
- $\mathcal{T}_L \subseteq \mathcal{T}$: Link types.

Inheritance forms a DAG: $T_1 \prec T_2$ if $T_2$ inherits from $T_1$. Each type $T \in \mathcal{T}$ defines relations $R_T \subseteq \mathcal{R}$, where each $r \in R_T$ is $r: T_{\text{source}} \to T_{\text{target}}$ (one-to-one or one-to-many). Relations may have reverses (e.g., $r^{-1}$). Field definitions inherit independently on input/output sides per the Fields Module.

Subtypes include:

- **Neuron Types ($\mathcal{T}_N$)**: Define activation logic (e.g., bias for conjunctive/disjunctive behavior).
- **Activation Types ($\mathcal{T}_A$)**: Specify subtypes (conjunctive, disjunctive, inhibitory) and wildcards for binding signals.
- **Synapse Types ($\mathcal{T}_S$)**: Include transitions for binding signals and storage direction.
- **Link Types ($\mathcal{T}_L$)**: Pair with synapses, supporting causal checks.

## Object Graph $G_{\text{object}}$

The object graph is $G_{\text{object}} = (V_{\text{obj}}, E_{\text{obj}})$, where:

- $V_{\text{obj}} = \mathcal{O}$ (objects typed via $\tau$).
- $E_{\text{obj}} \subseteq V_{\text{obj}} \times \mathcal{R} \times V_{\text{obj}}$, with $(o_s, r, o_t) \in E_{\text{obj}}$ iff $\tau(o_s) = T_{\text{source}}$, $\tau(o_t) = T_{\text{target}}$ for $r: T_{\text{source}} \to T_{\text{target}}$, and $o_t \in r(o_s)$.

Navigation: For $o \in \mathcal{O}$ and $r \in \mathcal{R}$, $r(o) = \{ o_t \mid (o, r, o_t) \in E_{\text{obj}} \}$ (singleton for one-to-one, set for one-to-many).

Fields anchor to objects per the Fields Module: Each $f \in F_T$ instantiates $f(o)$ for $o \in \mathcal{O}$ with $\tau(o) = T$, computed via $\phi$ over related fields (e.g., $f^{\cdot}_T(o) = \phi(\{f^{r_i}_{T_i}(o_i)\}_{i})$).

## Relation Types

Relations in $\mathcal{R}$ are named, directed, and typed. Key relations (grouped by defining type):

- **From Activation Types ($\mathcal{T}_A$)**:
    - $\texttt{SELF}: T_A \to T_A$ (self-reference, one-to-one).
    - $\texttt{INPUT}: T_A \to T_L$ (incoming links, one-to-many).
    - $\texttt{OUTPUT}: T_A \to T_L$ (outgoing links, one-to-many).
    - $\texttt{NEURON}: T_A \to T_N$ (associated neuron, one-to-one; reverse: $\texttt{ACTIVATION}$).

- **From Neuron Types ($\mathcal{T}_N$)**:
    - $\texttt{SELF}: T_N \to T_N$ (self-reference, one-to-one).
    - $\texttt{INPUT}: T_N \to T_S$ (incoming synapses, one-to-many).
    - $\texttt{OUTPUT}: T_N \to T_S$ (outgoing synapses, one-to-many).
    - $\texttt{ACTIVATION}: T_N \to T_A$ (associated activations, one-to-many; reverse: $\texttt{NEURON}$).

- **From Link Types ($\mathcal{T}_L$)**:
    - $\texttt{SELF}: T_L \to T_L$ (self-reference, one-to-one).
    - $\texttt{INPUT}: T_L \to T_A$ (input activation, one-to-one; reverse: $\texttt{OUTPUT}$).
    - $\texttt{OUTPUT}: T_L \to T_A$ (output activation, one-to-one; reverse: $\texttt{INPUT}$).
    - $\texttt{SYNAPSE}: T_L \to T_S$ (associated synapse, one-to-one; reverse: $\texttt{LINK}$).
    - $\texttt{PAIR}: T_L \to T_L$ (paired links required for the inner multiplication in a dot product neuron, one-to-one; mutual reverses).

- **From Synapse Types ($\mathcal{T}_S$)**:
    - $\texttt{SELF}: T_S \to T_S$ (self-reference, one-to-one).
    - $\texttt{INPUT}: T_S \to T_N$ (source neuron, one-to-one; reverse: $\texttt{OUTPUT}$).
    - $\texttt{OUTPUT}: T_S \to T_N$ (target neuron, one-to-one; reverse: $\texttt{INPUT}$).
    - $\texttt{LINK}: T_S \to T_L$ (instantiated links, one-to-many; reverse: $\texttt{SYNAPSE}$).

These relations enable graph traversal (e.g., from neuron to activations via $\texttt{ACTIVATION}$).

## Neural Network Layer: Static Graph $G_{\text{neural}}$

The neural graph is $G_{\text{neural}} = (\mathcal{N}, \mathcal{S})$, a directed multigraph representing static knowledge:

- Nodes: $\mathcal{N} = \{ o \in \mathcal{O} \mid \tau(o) \in \mathcal{T}_N \}$ (neurons).
- Edges: $\mathcal{S} = \{ o \in \mathcal{O} \mid \tau(o) \in \mathcal{T}_S \}$ (synapses), with source/target via relations: For $s \in \mathcal{S}$, source $n_s = \texttt{INPUT}(s)$, target $n_t = \texttt{OUTPUT}(s)$.

Instantiation: A neuron $n \in \mathcal{N}$ is created from $T_N \in \mathcal{T}_N$ via $n = T_N.\texttt{instantiate}(m)$, where $m$ is the model. Synapses connect neurons: $s = T_S.\texttt{instantiate}(n_s, n_t)$.

Fields (e.g., weights, biases) attach to nodes/edges per Fields Module, with computations like bias inheritance.

Conjunctive/disjunctive semantics: Derived from fields (e.g., high bias requires multiple inputs for firing, low bias allows single inputs).

## Activations Network Layer: Dynamic Graph $G_{\text{activations}}$

The activations graph is $G_{\text{activations}} = (\mathcal{A}, \mathcal{L})$, a directed multigraph for dynamic inference, tied to input data (e.g., documents):

- Nodes: $\mathcal{A} = \{ o \in \mathcal{O} \mid \tau(o) \in \mathcal{T}_A \}$ (activations), each linked to a neuron via $\texttt{NEURON}(a) = n \in \mathcal{N}$.
- Edges: $\mathcal{L} = \{ o \in \mathcal{O} \mid \tau(o) \in \mathcal{T}_L \}$ (links), with source/target via relations: For $l \in \mathcal{L}$, source $a_s = \texttt{INPUT}(l)$, target $a_t = \texttt{OUTPUT}(l)$, associated synapse $s = \texttt{SYNAPSE}(l)$.

Instantiation: Triggered event-driven (e.g., when a neuron's field exceeds threshold via Fields Module queue). For neuron $n \in \mathcal{N}$, activation $a = T_A.\texttt{instantiate}(...)$, where $T_A = n.\texttt{getActivation}()$. Links form via $l = T_L.\texttt{instantiate}(s, a_s, a_t)$.

Linking uses binding signals (BS): Let $\mathcal{B}$ be BS types (defined in mathematical model), with transitions $\delta: \mathcal{B} \times \mathcal{T}_S \to \mathcal{B}$ (forward/backward). A link candidate is valid if BS propagate without conflict (e.g., no mismatched signals in $a_t$).

Propagation: If no target $a_t$ exists, create via $a_t = n_t.\texttt{createActivation}(...)$. Fields update asynchronously (e.g., net value accumulation may fire $a_t$).

Conjunctive/disjunctive: Conjunctive activations accept multiple inputs; disjunctive fire on single inputs. Inhibitory subtypes modulate via wildcards.

## Event-Driven Processing and Integration with Fields Module

Updates queue events (e.g., firing when $f_{\text{net}}(a) > \theta$ via Fields Module). Binding signals route along $\mathcal{L}$, filtered by transitions. Graphs evolve: $G_{\text{activations}}$ per input instance, referencing static $G_{\text{neural}}$. Sparsity: Only threshold-exceeding elements activate, leveraging Fields Module's selective computations.
