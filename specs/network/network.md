# Abstract Mathematical Model of the Neural Network Module

## 1. Type System and Hierarchy
The Neural Network Module operates within the type system $\mathcal{T}$ defined by the Fields Module, a Directed Acyclic Graph (DAG) $(\mathcal{T}, \prec)$ where $\prec$ denotes inheritance. Subsets of $\mathcal{T}$ relevant to the network module are:

- $\mathcal{T}_N$: Set of neuron types (e.g., implemented by `NeuronDefinition`).
- $\mathcal{T}_S$: Set of synapse types (e.g., implemented by `SynapseDefinition`).
- $\mathcal{T}_A$: Set of activation types (e.g., implemented by `ActivationDefinition`).
- $\mathcal{T}_L$: Set of link types (e.g., implemented by `LinkDefinition`).

Each type $t \in \mathcal{T}$ defines relations $R_t$, which are directed associations between types (e.g., $r: t_{\text{source}} \to t_{\text{target}})$, supporting both one-to-one and one-to-many connections, as specified in the Fields Module.

## 2. Object Graph Integration
The Neural Network Module's elements—neurons, synapses, activations, and links—are objects within the object graph $G_{\text{object}} = (V_{\text{obj}}, E_{\text{obj}})$ of the Fields Module, where:
- $V_{\text{obj}}$ is the set of objects.
- $\tau: V_{\text{obj}} \to \mathcal{T}$ assigns each object a type.
- $E_{\text{obj}} \subset V_{\text{obj}} \times R \times V_{\text{obj}}$ represents typed relations.

The network module defines two specialized subgraphs: the Neural Graph and the Activations Graph, which organize these objects into static and dynamic structures, respectively.

## 3. Neural Graph
The Neural Graph represents the static structure of the neural network, comprising neurons as nodes and synapses as directed edges.

- **Definition**:  
  $G_{\text{neural}} = (\mathcal{N}, \mathcal{S})$, where:
    - $\mathcal{N} \subset V_{\text{obj}}$ is the set of neurons, with $\tau: \mathcal{N} \to \mathcal{T}_N$.
    - $\mathcal{S} \subset V_{\text{obj}}$ is the set of synapses, with $\tau: \mathcal{S} \to \mathcal{T}_S$.
    - Each synapse $(s \in \mathcal{S}$ is a directed edge $s = (n_{\text{in}}, n_{\text{out}})$, where $n_{\text{in}}, n_{\text{out}} \in \mathcal{N}$, defined by relations:
        - $n_{\text{in}} \xrightarrow{\text{OUTPUT}} s$ (from `NeuronDefinition.OUTPUT`).
        - $s \xrightarrow{\text{INPUT}} n_{\text{in}}$ (from `SynapseDefinition.INPUT`).
        - $s \xrightarrow{\text{OUTPUT}} n_{\text{out}}$ (from `SynapseDefinition.OUTPUT`).
        - $n_{\text{out}} \xrightarrow{\text{INPUT}} s$ (from `NeuronDefinition.INPUT`).

- **Properties**:
    - The graph is directed and supports a flexible, sparse topology, not constrained to layered architectures.
    - Synapse types in $\mathcal{T}_S$ may specify transitions for binding signals (see Section 6), influencing dynamic behavior.

## 4. Activations Graph
The Activations Graph represents the dynamic inference process, with activations as nodes and links as directed edges, instantiated based on input data.

- **Definition**:  
  $G_{\text{activations}} = (\mathcal{A}, \mathcal{L})$, where:
    - $\mathcal{A} \subset V_{\text{obj}}$ is the set of activations, with $\tau: \mathcal{A} \to \mathcal{T}_A$.
    - $\mathcal{L} \subset V_{\text{obj}}$ is the set of links, with $\tau: \mathcal{L} \to \mathcal{T}_L$.
    - Each link $l \in \mathcal{L}$ is a directed edge $l = (a_{\text{in}}, a_{\text{out}})$, where $a_{\text{in}}, a_{\text{out}} \in \mathcal{A}$, defined by relations:
        - $a_{\text{in}} \xrightarrow{\text{OUTPUT}} l$ (from `ActivationDefinition.OUTPUT`).
        - $l \xrightarrow{\text{INPUT}} a_{\text{in}}$ (from `LinkDefinition.INPUT`).
        - $l \xrightarrow{\text{OUTPUT}} a_{\text{out}}$ (from `LinkDefinition.OUTPUT`).
        - $a_{\text{out}} \xrightarrow{\text{INPUT}} l$ (from `ActivationDefinition.INPUT`).

- **Properties**:
    - The graph is sparse and dynamically constructed for specific input data.
    - Each link $l \in \mathcal{L}$ is associated with a synapse $s \in \mathcal{S}$ via the relation $l \xrightarrow{\text{SYNAPSE}} s$ (from `LinkDefinition.SYNAPSE`).

## 5. Relationships Between Graphs
The Neural Graph and Activations Graph are interconnected through mappings that reflect their static-to-dynamic relationship:

- **Neuron-to-Activation Mapping**:  
  $\phi: \mathcal{A} \to \mathcal{N}$, where each activation $a \in \mathcal{A}$ corresponds to a neuron $n = \phi(a) \in \mathcal{N}$, via the relation $a \xrightarrow{\text{NEURON}} n$ (from `ActivationDefinition.NEURON`).

- **Synapse-to-Link Mapping**:  
  $\psi: \mathcal{L} \to \mathcal{S}$, where each link $l \in \mathcal{L}$ corresponds to a synapse $s = \psi(l) \in \mathcal{S}$, preserving directionality:
    - If $l = (a_{\text{in}}, a_{\text{out}})$, then $s = \psi(l) = (\phi(a_{\text{in}}), \phi(a_{\text{out}}))$.

- **Type Consistency**:
    - For a neuron $n \in \mathcal{N}$ with type $\tau(n) = t_n \in \mathcal{T}_N$, its activations $a \in \mathcal{A}$ with $\phi(a) = n$ have type $\tau(a) = \alpha(t_n) \in \mathcal{T}_A$, where $\alpha: \mathcal{T}_N \to \mathcal{T}_A$ is defined by `NeuronDefinition.ACTIVATION`.
    - For a synapse $s \in \mathcal{S}$ with type $\tau(s) = t_s \in \mathcal{T}_S$, its links $l \in \mathcal{L}$ with $\psi(l) = s$ have type $\tau(l) = \lambda(t_s) \in \mathcal{T}_L$, where $\lambda: \mathcal{T}_S \to \mathcal{T}_L$ is defined by `SynapseDefinition.LINK`.

## 6. Binding Signals and Linking Mechanism
Binding signals facilitate the dynamic construction of $G_{\text{activations}}$:

- **Definition**:
    - Let $\mathcal{B}$ be the set of binding signal types ($\text{BSType}$), defined in the mathematical model.
    - Each activation $a \in \mathcal{A}$ has a partial function $\beta_a: \mathcal{B} \to \mathcal{I}$, where $\mathcal{I}$ is the set of input token IDs, mapping each binding signal type to at most one token ID.

- **Transitions**:
    - Each synapse type $t_s \in \mathcal{T}_S$ defines a set of transitions $\text{trans}(t_s) \subset \mathcal{B} \times \mathcal{B}$, mapping input binding signal types to output types.
    - For a synapse $s \in \mathcal{S}$ with $\tau(s) = t_s$, the propagation of binding signals from an input activation $a_{\text{in}}$ to an output activation $a_{\text{out}}$ is:
      $$
      \beta_{a_{\text{out}}}(b_{\text{out}}) = \beta_{a_{\text{in}}}(b_{\text{in}}) \text{ if } (b_{\text{in}}, b_{\text{out}}) \in \text{trans}(t_s) \text{ and } \beta_{a_{\text{in}}}(b_{\text{in}}) \text{ is defined}.
      $$

- **Linking Process**:
    - When an activation $a_{\text{in}}$ fires (threshold exceeded, determined by the Fields Module’s field graph), for each output synapse $s = (n_{\text{in}}, n_{\text{out}})$ with $n_{\text{in}} = \phi(a_{\text{in}})$:
        1. Compute propagated binding signals: $\beta_{\text{prop}} = \text{transitionForward}(\beta_{a_{\text{in}}})$.
        2. Identify candidate activations $a_{\text{out}} \in \mathcal{A}$ with $\phi(a_{\text{out}}) = n_{\text{out}}$ that share at least one binding signal with $a_{\text{in}}$.
        3. For each $a_{\text{out}}$, if $\beta_{a_{\text{out}}}$ does not conflict with $\beta_{\text{prop}}$ (i.e., no differing values for the same $b \in \mathcal{B}$), create a link $l = (a_{\text{in}}, a_{\text{out}})$ with $\psi(l) = s$.
        4. If no compatible $a_{\text{out}}$ exists and $s$ is propagable, instantiate a new activation $a_{\text{out}}$ for $n_{\text{out}}$ with $\beta_{a_{\text{out}}} = \beta_{\text{prop}}$, and create a link $l = (a_{\text{in}}, a_{\text{out}})$.

## 7. Event-Driven Dynamics
The instantiation and linking in $G_{\text{activations}}$ are managed asynchronously via an event queue, as per the Fields Module’s event-driven updates. Key events include:
- **Neuron Firing**: Triggers linking and activation instantiation.
- **Field Updates**: Reflect changes in activation states, computed in the separate mathematical model.

## 8. Conjunctive and Disjunctive Elements
- **Disjunctive**: Neurons or synapses where a single input activation suffices to fire (low bias, positive weights in the field graph).
- **Conjunctive**: Neurons or synapses requiring multiple input activations (higher bias). Only conjunctive activations accept additional input links during linking.

---

# Summary
The Neural Network Module defines:
- **Neural Graph**: $G_{\text{neural}} = (\mathcal{N}, \mathcal{S})$, a static structure of neurons and synapses.
- **Activations Graph**: $G_{\text{activations}} = (\mathcal{A}, \mathcal{L})$, a dynamic structure of activations and links, built via binding signals and linking.
- **Integration**: Connected to the Fields Module’s type system $\mathcal{T}$ and object graph $G_{\text{object}}$, with mathematical computations deferred to a Python module.

This model supports a flexible, sparse, and non-layered neural network, dynamically adapting to input data through event-driven processes.


