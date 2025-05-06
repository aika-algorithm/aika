# AIKA: Artificial Intelligence for Knowledge Acquisition

## Meta Information
This document provides a comprehensive natural language description of the AIKA project, sufficient for a Large Language Model (LLM) to generate all required source files. These include:

- C++ header and implementation files.
- Pybind11 binding code for Python integration.
- Unit tests to verify functionality.
- CMakeLists.txt for building the project.
- An example neural network (NN) model in Python, utilizing the library modules.

No user interface (UI) is required. Each generated file should include detailed comments describing the file’s purpose and each method’s functionality. The description is complete when an LLM can build the project, run all unit tests, and execute the example NN model in a Python environment.

## Overview

### Idea and Core Concept
AIKA is an innovative neural network framework that departs from traditional architectures reliant on rigid matrix and vector operations. It features:

- **Flexible, Sparse, Non-Layered Representation**: Derived from a type hierarchy, enabling arbitrary neural graphs where neurons connect only to relevant inputs, rather than fully connected layers.
- **Network Separation**: 
  - **Neural Network**: Static knowledge represented by neurons and synapses.
  - **Activation Network**: Dynamic inference represented by activations and links, specific to input data (e.g., tokenized text).
- **Sparse Activation**: During processing, only neurons and synapses exceeding their activation threshold are activated. This sparsity—stemming from selective connections and relevance-based activation—enables efficient handling of large networks (millions of neurons) by focusing on relevant subsections.

### Linker and Binding Signals
- **Linker**: Transfers the neural network’s structure to the activation network. When a neuron’s activation exceeds its threshold, the Linker instantiates an activation node based on the neuron and creates input links from contributing synapses. 
- **Binding Signals (BS)**: Relational references that propagate along links, determining valid connections in the activation graph. Inspired by:
  - Individual constants in predicate logic.
  - Temporally synchronized spiking in biological neural networks (akin to synchronized firing in the human brain).
- **BS Rules**: Defined in the Python model specification, these rules dictate which BS-types propagate through specific neuron and synapse types.

### Event-Driven Processing
AIKA processes network changes asynchronously via time-ordered events in a queue, ensuring correct temporal ordering of activations and binding signal propagations. Event types include:
- **Neuron Firing**: Occurs when a neuron’s activation exceeds its threshold, triggering the Linker and BS propagation.
- **Field Value Updates**: Updates in the mathematical fields graph, queued only for recurrent-risk fields (specified in the Python model).

### Type Hierarchy of Network Elements
Unlike traditional neural networks using vector/matrix operations, AIKA employs a hierarchy of neuron types (e.g., excitatory, inhibitory) with mathematical models organized as graphs of functions. This structure supports flexible, dynamic responses in the activation network.

## Project Structure

### Fields Module
The mathematical core of AIKA, featuring:
- **Graph-Based Representation**: Declarative graphs for mathematical models.
- **Type Hierarchy**: Defines network elements (neurons, synapses, activations, links).
- **Event-Driven Updates**: Asynchronous state changes via an event queue.
- **Dual Graphs**:
  - **Field Graph**: Represents mathematical models, with nodes as functions and edges as inputs. Each field node references an object node.
  - **Object Graph**: Used by the Neural Network Module to model neurons, synapses, activations, and links.
- **Type Definitions**: Field nodes use `FieldDefinition` types; object nodes use `Type`.

### Neural Network Module
Focuses on the neural and activation networks, with:
- **Dual Graph Structure**: Separates static knowledge (neurons/synapses) from dynamic inference (activations/links).
- **Dynamic Activation**: Multiple activations per neuron, tied to specific input data occurrences.
- **Flexible Topology**: Adapts dynamically to input data, abandoning fixed layers.
- **Linker Component**: Translates structure and propagates binding signals.

### Example NN Model in Python
A Python-based example specification demonstrating the use of the Fields and Neural Network modules via pybind11 bindings.

## Technical Implementation

### General Coding Guidelines
- **Setup**: C++ library using CMake as the build system and pybind11 for Python integration (similar to PyTorch).
The C++ code is intended to be high performance with as little overhead as possible. The code base should avoid smart pointers and manage its memory manully. 
For perfomance critical portions of the code, dynamic data structures should be avoided and array based data-structure should be used instead.


#### Overall Description of the Fields Module

The fields module is the mathematical core of the AIKA neural network framework, designed to support flexible, sparse, and non-layered representations of neural computations. It implements a dual-graph structure comprising the **field graph** and the **object graph**, which together serve as the computational substrate for the actual neural network implementation. The field graph is a declarative, graph-based representation of mathematical models, where nodes are computational units called "fields" that perform operations such as addition, multiplication, or thresholding, and edges represent data flow between these units. Each field is tied to an object in the object graph—such as a neuron or activation—via a type hierarchy that defines the structure and capabilities of network elements.

Fields are defined by the `FieldDefinition` class, which specifies the type of computation (e.g., fixed-argument operations like `Addition` or variable-argument operations like `SumField`) and their connections through `FieldLinkDefinition`. These connections, directed by `Direction` implementations (`Input` and `Output`), form the edges of the field graph, facilitating event-driven updates that propagate through the network via an event queue managed by classes like `QueueInterceptor`. At runtime, the `Field` class instantiates these definitions, maintaining current and updated values for computations and coordinating updates with the queue system.

The type hierarchy, implemented by `Type` and optimized by `FlattenedType`, organizes network elements hierarchically, supporting multiple inheritance and efficient runtime access to field definitions and relations. Objects in the object graph, represented by the `Obj` class, serve as containers for fields, linking mathematical computations to the neural network's structure. Relations between objects, defined in the `network.aika.type.relations` package (e.g., `RelationOne`, `RelationMany`), guide the propagation of data and binding signals across the network.

This module supports AIKA's sparse activation paradigm by ensuring that only relevant fields are updated, leveraging tolerance thresholds and event-driven processing to maintain efficiency. Mathematical operations are encapsulated in specialized classes (e.g., `Multiplication`, `SoftmaxFields`), providing a flexible toolkit for constructing complex models. Together, these components enable the fields module to underpin the dynamic and scalable behavior of the AIKA framework.


##### Grouped Descriptions by Functionality

To provide an intermediate granularity, classes are grouped by their primary roles within the fields module, reflecting their contributions to the field graph, object graph, and event-driven updates.

###### Field Definitions and Mathematical Operations
- **Purpose**: Define the computational units (fields) in the field graph and specify their mathematical behavior.
- **Classes**:
  - `FieldDefinition`: Base class for defining fields, managing properties like name, object type, and tolerance, and handling connections.
  - `FixedArgumentsFieldDefinition`: Extends `FieldDefinition` for operations with a fixed number of inputs (e.g., binary operations).
  - `VariableArgumentsFieldDefinition`: Extends `FieldDefinition` for operations with a variable number of inputs (e.g., summation).
  - `AbstractFunctionDefinition`: Abstract base for mathematical functions with fixed arguments, providing update computation logic.
  - Specific Operations: `Addition`, `Subtraction`, `Multiplication`, `Division`, `ExponentialFunction`, `ScaleFunction`, `InvertFunction`, `IdentityFunction`, `FieldActivationFunction`, `ThresholdOperator`, `SumField`, `SoftmaxFields`, `InputField`, `EventListener`.
- **Relation to Project**: These classes form the nodes of the field graph, implementing the mathematical models required for neural computations. They support the type hierarchy by associating operations with object types and enable sparse activation through selective updates.

###### Field Links and Data Flow
- **Purpose**: Establish and manage connections between fields, defining the edges of the field graph.
- **Classes**:
  - `FieldLinkDefinition`: Base class for links, specifying origin and related field definitions, relation, and direction.
  - `Direction` (interface), `Input`, `Output`: Define the direction of data flow in links.
- **Relation to Project**: These classes enable the graph-based representation of mathematical models, directing data flow and supporting event-driven updates by transmitting changes along the field graph.

###### Runtime Fields and Updates
- **Purpose**: Manage the runtime state of fields and coordinate updates within the event-driven system.
- **Classes**:
  - `Field`: Runtime instance of a field, holding values and managing updates.
  - `FieldInput`, `FieldOutput`: Interfaces for accessing field states and receiving updates.
  - `QueueInterceptor`: Integrates field updates with the event queue, ensuring proper timing and ordering.
  - `UpdateListener`: Interface for entities that respond to field updates.
- **Relation to Project**: These classes instantiate the field graph at runtime, linking it to the object graph and supporting asynchronous updates critical for dynamic inference.

###### Type Hierarchy and Object Management
- **Purpose**: Structure network elements and associate them with fields and relations.
- **Classes**:
  - `Type`: Defines the type hierarchy with multiple inheritance, associating field definitions and relations.
  - `FlattenedType`: Optimized, flattened representation of the type hierarchy for runtime efficiency.
  - `Obj`: Represent objects in the object graph, managing their fields and relations.
- **Relation to Project**: These classes implement the type hierarchy and object graph, enabling flexible topology and efficient access to computational units.

###### Supporting Utilities
- **Purpose**: Provide auxiliary functionality for the fields module.
- **Classes**: Found in `network.aika.utils` (e.g., `ToleranceUtils`, `StringUtils`, `ApproximateComparisonValueUtil`, `ArrayUtils`).
- **Relation to Project**: Enhance robustness and efficiency, supporting tolerance checks, string formatting, and array operations.


##### Per-File Descriptions

Below are detailed descriptions for key classes within the fields module, focusing on those in `network.aika.fields`, `network.aika.fields.defs`, `network.aika.fields.direction`, `network.aika.fields.field`, and related type classes. Each description includes purpose, key features, and relation to the AIKA project.

###### `network.aika.fields`

1. **AbstractFunctionDefinition.java**
   - **Purpose**: Base class for mathematical functions with a fixed number of arguments in the field graph.
   - **Key Features**:
     - Extends `FixedArgumentsFieldDefinition` to specify a fixed number of inputs.
     - Abstract method `computeUpdate` calculates updates based on input changes.
     - Transmits updates to connected fields via `transmit`.
   - **Relation**: Provides a foundation for mathematical operations, enabling the declarative field graph.

2. **ActivationFunction.java**
   - **Purpose**: Interface for activation functions used in fields (e.g., neural activation).
   - **Key Features**:
     - Defines `f` (function output) and `outerGrad` (gradient for optimization).
   - **Relation**: Supports dynamic inference by applying activation logic to field values.

3. **Addition.java**
   - **Purpose**: Implements addition as a binary operation in the field graph.
   - **Key Features**:
     - Takes two inputs and passes updates directly (sum of inputs).
   - **Relation**: A basic building block for mathematical models in the neural network.

4. **Division.java**
   - **Purpose**: Implements division as a binary operation.
   - **Key Features**:
     - Initializes field with dividend/divisor; computes updates considering divisor changes.
   - **Relation**: Supports complex computations, handling edge cases like division by zero.

5. **EventListener.java**
   - **Purpose**: Triggers a function when a field updates, supporting event-driven behavior.
   - **Key Features**:
     - Takes a single input and a `BiConsumer` trigger function.
     - Executes the trigger on update receipt.
   - **Relation**: Facilitates asynchronous processing, e.g., neuron firing events.

6. **ExponentialFunction.java**
   - **Purpose**: Applies an exponential function to a single input.
   - **Key Features**:
     - Initializes with `exp(input)`; computes update as difference from current value.
   - **Relation**: Useful for activation functions or probability calculations.

7. **FieldActivationFunction.java**
   - **Purpose**: Applies a custom activation function to a field.
   - **Key Features**:
     - Uses an `ActivationFunction` instance; computes update based on function output.
   - **Relation**: Enhances flexibility in modeling neural activation behaviors.

8. **IdentityFunction.java**
   - **Purpose**: Passes input directly as output (identity operation).
   - **Key Features**:
     - Single input; update equals input change.
   - **Relation**: Simplifies field graph connections where no transformation is needed.

9. **InputField.java**
   - **Purpose**: Represents an input source with no computation.
   - **Key Features**:
     - Zero inputs; returns zero update (placeholder for external inputs).
   - **Relation**: Serves as an entry point for data into the field graph.

10. **InvertFunction.java**
    - **Purpose**: Inverts input (1 - input).
    - **Key Features**:
      - Single input; computes update as inverted value change.
    - **Relation**: Useful for inhibitory effects or normalization.

11. **Multiplication.java**
    - **Purpose**: Implements multiplication as a binary operation.
    - **Key Features**:
      - Initializes with product of two inputs; update scales with the other input.
    - **Relation**: Core operation for weighting or combining signals.

12. **ScaleFunction.java**
    - **Purpose**: Scales input by a constant factor.
    - **Key Features**:
      - Single input; update is input change times scale factor.
    - **Relation**: Adjusts signal magnitude in the field graph.

13. **SoftmaxFields.java**
    - **Purpose**: Implements a softmax operation across multiple inputs and input objects.
    - **Key Features**:
      - Composes `ExponentialFunction`, `SumField`, and `Division` to normalize inputs.
      - Uses relations to connect input, normalization, and output fields.
    - **Relation**: Enables probability distributions, key for classification tasks.

14. **Subtraction.java**
    - **Purpose**: Implements subtraction as a binary operation.
    - **Key Features**:
      - Two inputs; update is positive for first input, negative for second.
    - **Relation**: Supports difference-based computations in models.

15. **SumField.java**
    - **Purpose**: Sums a variable number of inputs.
    - **Key Features**:
      - Extends `VariableArgumentsFieldDefinition`; aggregates all inputs.
    - **Relation**: Facilitates aggregation, critical for summing activations.

16. **ThresholdOperator.java**
    - **Purpose**: Applies a threshold to determine binary output (0 or 1).
    - **Key Features**:
      - Single input; supports comparisons (e.g., above, below); optional final state.
    - **Relation**: Implements sparse activation by thresholding field values.

###### `network.aika.fields.defs`

1. **FieldDefinition.java**
   - **Purpose**: Defines a field in the field graph, serving as a blueprint.
   - **Key Features**:
     - Manages name, object type, tolerance, phase, and connections (inputs/outputs).
     - Initializes fields and propagates updates with tolerance checks.
   - **Relation**: Core class for declaring mathematical models in the field graph.

2. **FieldLinkDefinition.java**
   - **Purpose**: Defines edges between fields, specifying data flow.
   - **Key Features**:
     - Links origin and related field definitions with a relation and direction.
     - Static `link` method pairs input and output sides.
   - **Relation**: Forms the graph structure, enabling dynamic updates.


###### `network.aika.fields.direction`

1. **Direction.java**
   - **Purpose**: Interface for directing data flow in field links.
   - **Key Features**:
     - Defines methods for direction ID, inversion, link retrieval, and transmission.
   - **Relation**: Ensures proper flow in the field graph, supporting event-driven updates.

2. **Input.java**
   - **Purpose**: Implements input direction for field links.
   - **Key Features**:
     - Retrieves input links; transmits values from related fields to origin.
   - **Relation**: Directs incoming data, integral to graph traversal.

3. **Output.java**
   - **Purpose**: Implements output direction for field links.
   - **Key Features**:
     - Retrieves output links; transmits updates to related fields.
   - **Relation**: Propagates changes outward, enabling network responsiveness.

###### `network.aika.fields.field`

1. **Field.java**
   - **Purpose**: Runtime instance of a field, managing state and updates.
   - **Key Features**:
     - Holds current and updated values; integrates with queue via `QueueInterceptor`.
     - Propagates updates using flattened type relations.
   - **Relation**: Executes computations in the field graph, tied to object graph entities.

2. **FieldInput.java**
   - **Purpose**: Interface for fields receiving updates.
   - **Key Features**:
     - Extends `UpdateListener` for update reception.
   - **Relation**: Ensures fields can respond to incoming changes.

3. **FieldOutput.java**
   - **Purpose**: Interface for accessing field state.
   - **Key Features**:
     - Provides value access (current/updated) and metadata.
   - **Relation**: Exposes field results for downstream use or monitoring.

4. **QueueInterceptor.java**
   - **Purpose**: Mediates field updates with the event queue.
   - **Key Features**:
     - Queues updates via `FieldUpdate`; processes when triggered.
   - **Relation**: Implements event-driven processing, ensuring temporal order.

5. **UpdateListener.java**
   - **Purpose**: Interface for entities reacting to field updates.
   - **Key Features**:
     - Defines `receiveUpdate` method.
   - **Relation**: Supports event-driven interactions across the network.

###### `network.aika.type`

1. **Type.java**
   - **Purpose**: Defines the type hierarchy for network elements.
   - **Key Features**:
     - Supports multiple inheritance; manages field definitions and relations.
     - Generates flattened types for efficiency.
   - **Relation**: Structures the object graph, linking static knowledge to computations.

2. **FlattenedType.java**
   - **Purpose**: Optimized representation of the type hierarchy.
   - **Key Features**:
     - Maps field definitions to indices; flattens relations for runtime use.
     - Follows links to propagate updates.
   - **Relation**: Enhances performance for large, sparse networks.

3. **Obj.java**
   - **Purpose**: Base implementation of `Obj`.
   - **Key Features**:
     - Manages fields, relations, and queue access. Initializes and stores fields based on type; provides field access methods.
   - **Relation**: Concrete representation of network objects, integrating with fields.

Below is a structured textual description of the neural network module from the AIKA project's Java codebase. This description is designed to be high-level yet detailed enough to serve as a blueprint for implementing equivalent C++ classes. It captures the purpose and functionality of each class, relates them to the neural network module's goals as outlined in the AIKA project description, and fills in missing details where applicable. The description is organized at multiple granularities: an overall summary, per-package overviews, and detailed per-class descriptions for key components.


#### Neural Network Module

##### Overall Description of the Neural Network Module

The neural network module in AIKA is a core component of an innovative neural network framework that emphasizes flexibility, sparsity, and dynamic inference over traditional layered architectures. It separates the static structure of the neural network—composed of neurons and synapses—from the dynamic activation network, which consists of activations and links tied to specific input data (e.g., tokenized text). This separation enables efficient processing of large-scale networks by activating only relevant subsections based on input and thresholds, a concept known as sparse activation.

Key features include:
- **Neurons**: Static computational units defined by a type hierarchy, managing synapses and capable of suspension to optimize memory usage.
- **Synapses**: Connections between neurons, with types (e.g., conjunctive, disjunctive) determining signal propagation and binding signal transitions.
- **Activations**: Dynamic instances of neurons, created for specific inputs, handling binding signals and linking logic.
- **Links**: Connections between activations, mirroring synapses and facilitating the flow of binding signals during inference.
- **Binding Signals (BS)**: Relational references that propagate through the network, ensuring coherent activation patterns by defining valid connections.
- **Event-Driven Processing**: Managed via a time-ordered queue, processing events like neuron firings and link instantiations asynchronously.
- **Linker**: A distributed mechanism (not a single class) that transfers the neural network structure to the activation network by creating activations and links based on firing events and binding signal propagation.

The module builds on the fields module, which provides the mathematical foundation through graph-based computations. The type hierarchy, implemented in the `typedefs` package, defines the properties and behaviors of network elements, supporting the flexible topology required for dynamic responses. This design aligns with AIKA's goal of handling large, sparse networks efficiently, leveraging selective activation and relational coherence via binding signals.

##### Per-Package Descriptions

##### `network.aika`
**Purpose**: Provides core framework classes for configuration, document management, and model oversight.  
- **Config**: Stores settings like learning rate and timeouts, influencing training and processing behavior.
- **Document**: Represents an input instance (e.g., a document), managing activations, binding signals, and the processing queue.
- **Element**: Interface for activation graph elements (activations and links), tracking creation and firing timestamps.
- **Model**: Oversees the neural network, managing neurons, documents, and suspension logic.
- **ModelProvider**: Interface for accessing the model instance.  
**Relation to Project**: These classes establish the framework's foundation, coordinating the static neural network and dynamic inference processes.

###### `network.aika.activations`
**Purpose**: Manages dynamic inference through activations and links.  
- **Activation**: Abstract base for activations, with subtypes (`ConjunctiveActivation`, `DisjunctiveActivation`, `InhibitoryActivation`) handling specific linking behaviors.
- **ActivationKey**: Record for uniquely identifying activations.
- **Link**: Connects activations, carrying binding signals and reflecting synapse relationships.  
**Relation to Project**: Implements the activation network, enabling sparse and dynamic responses to input data.

###### `network.aika.bindingsignal`
**Purpose**: Defines and manages binding signals for relational coherence.  
- **BSType**: Interface for binding signal types.
- **BindingSignal**: Represents a binding signal tied to a token, tracking associated activations.
- **Transition**: Defines binding signal transitions across synapses.  
**Relation to Project**: Ensures valid connections in the activation graph, a key feature of AIKA’s sparse activation mechanism.

###### `network.aika.misc.direction`
**Purpose**: Specifies directionality for synapses and links.  
- **Direction**: Interface with implementations `Input` and `Output`.  
**Relation to Project**: Supports the graph structure by defining data flow directions.

###### `network.aika.misc.exceptions`
**Purpose**: Custom exceptions for error handling (e.g., `LockException`, `MissingNeuronException`, `NeuronSerializationException`).  
**Relation to Project**: Enhances robustness, though not directly tied to neural functionality.

###### `network.aika.misc.suspension`
**Purpose**: Manages neuron suspension to reduce memory usage.  
- **SuspensionCallback**: Interface for suspension logic, with implementations `FSSuspensionCallback` (file-based) and `InMemorySuspensionCallback` (memory-based).  
**Relation to Project**: Optimizes resource use for large networks, supporting scalability.

###### `network.aika.misc.utils`
**Purpose**: Utility classes for concurrency and general operations.  
- **ReadWriteLock**: Manages concurrent access.
- **Utils**: Provides helper functions (e.g., tolerance checks).  
**Relation to Project**: Supports efficient and safe operation of the framework.

###### `network.aika.neurons`
**Purpose**: Defines the static neural network structure.  
- **Neuron**: Represents a neuron, managing synapses and references.
- **Synapse**: Abstract base for synapses (`ConjunctiveSynapse`, `DisjunctiveSynapse`), handling connections and signal transitions.
- **NeuronReference**: Tracks neuron references with suspension support.
- **RefType**: Enum for reference types.  
**Relation to Project**: Forms the static knowledge base, enabling dynamic instantiation in the activation network.

###### `network.aika.queue`
**Purpose**: Implements event-driven processing.  
- **ElementStep**: Abstract step for queue elements.
- **Phase**: Enum for processing phases.
- **Queue**: Manages the event queue.
- **QueueProvider**: Interface for queue access.
- **Timestamp**: Tracks event timing.  
**Relation to Project**: Ensures temporal ordering of inference events.

###### `network.aika.queue.steps`
**Purpose**: Defines specific queue steps.  
- **Fired**: Handles activation firing and linking.
- **Save**: Saves neuron states.  
**Relation to Project**: Implements key inference and persistence actions.

###### `network.aika.typedefs`
**Purpose**: Defines the type hierarchy for network elements.  
- **ActivationDefinition**: Specifies activation types and properties.
- **EdgeDefinition**: Pairs synapse and link definitions.
- **LinkDefinition**: Defines link types.
- **NodeDefinition**: Pairs neuron and activation definitions.
- **NeuronDefinition**: Defines neuron types.
- **SynapseDefinition**: Defines synapse types and transitions.  
**Relation to Project**: Provides flexibility and extensibility through a structured type system.


##### Detailed Class Descriptions

Below are detailed descriptions of key classes, focusing on their purpose, features, and relation to the neural network module.

###### `network.aika.Document`
- **Purpose**: Represents a single input instance (e.g., a text document), coordinating its processing.
- **Key Features**:
  - Manages activations (`TreeMap<Integer, Activation>`), binding signals (`TreeMap<Integer, BindingSignal>`), and a processing queue.
  - Methods: `addActivation`, `createActivationId`, `addToken` (creates activations with binding signals), `process` (executes queued steps).
  - Tracks document lifecycle with `disconnect` for cleanup.
- **Relation**: Serves as the entry point for input processing, orchestrating the activation network’s dynamic inference.

###### `network.aika.activations.Activation`
- **Purpose**: Abstract base class for activations, representing dynamic neuron instances.
- **Key Features**:
  - Stores binding signals (`Map<BSType, BindingSignal>`), input/output links (`NavigableMap<Integer, Link>`), and firing state (`Timestamp fired`).
  - Methods: `linkOutgoing` (creates output links), `propagate` (extends network via synapses), `branch` (handles conflicting binding signals).
  - Subtypes (`ConjunctiveActivation`, etc.) implement specific linking logic.
- **Relation**: Central to sparse activation, selectively activating and linking based on thresholds and binding signals.

###### `network.aika.neurons.Neuron`
- **Purpose**: Represents a static neuron in the neural network.
- **Key Features**:
  - Manages input (`Map<Integer, Synapse>`) and output synapses (`Map<Long, Synapse>`), propagable neurons (`Map<Long, NeuronReference>`), and reference counting.
  - Methods: `createActivation` (instantiates activations), `addInputSynapse`/`addOutputSynapse` (manages connections), `wakeupPropagable` (reactivates neurons).
  - Supports suspension via `NeuronReference`.
- **Relation**: Forms the static structure, enabling multiple dynamic activations per neuron.

###### `network.aika.neurons.Synapse`
- **Purpose**: Abstract base for synapses, connecting neurons and defining signal flow.
- **Key Features**:
  - Tracks input/output neurons via `NeuronReference`, synapse ID, and propagability.
  - Methods: `createLink` (instantiates links), `transitionForward` (maps binding signals), `link`/`unlink` (manages connections).
  - Subtypes (`ConjunctiveSynapse`, `DisjunctiveSynapse`) specialize behavior.
- **Relation**: Defines connectivity and signal propagation, critical for the Linker’s role in transferring structure to the activation network.

###### `network.aika.bindingsignal.BindingSignal`
- **Purpose**: Represents a binding signal tied to a token, ensuring relational coherence.
- **Key Features**:
  - Holds token ID and document reference; tracks activations (`NavigableMap<ActivationKey, Activation>`).
  - Methods: `addActivation`, `getActivations` (retrieves activations for a neuron).
- **Relation**: Implements binding signals, ensuring valid connections during inference.

###### `network.aika.queue.Queue`
- **Purpose**: Manages the event queue for time-ordered processing.
- **Key Features**:
  - Extends a base queue, processing steps (`Step`) with phases (`Phase`) and timestamps (`Timestamp`).
  - Ensures correct event ordering (e.g., firing before linking).
- **Relation**: Drives event-driven processing, maintaining temporal integrity of inference steps.

###### `network.aika.typedefs.*` (Type Hierarchy Classes)
- **Purpose**: Define types for activations, neurons, synapses, and links.
- **Key Features**:
  - `ActivationDefinition`, `NeuronDefinition`, `SynapseDefinition`, etc., use registries (`TypeRegistry`) and relations (`Relation`) to specify properties.
  - Methods: `instantiate` creates concrete instances; inheritance supports extensibility.
- **Relation**: Enables a flexible, hierarchical structure for network elements, supporting AIKA’s non-layered design.

##### Filling in Missing Parts

The project description mentions the **Linker** as a distinct component transferring structure from the neural network to the activation network. In the codebase, this functionality is distributed:
- **Activation.linkOutgoing()**: Triggers link creation based on output synapses and binding signals.
- **Synapse.createLink()**: Instantiates links between activations, respecting transitions.
- **Neuron.createActivation()**: Generates activations with binding signals.

**Sparse Activation** is implemented through:
- **Activation.updateFiredStep()**: Checks thresholds to trigger firing.
- **Linker Logic**: Selective linking via `collectLinkingTargets` and binding signal checks ensures only relevant connections are made.

**Event-Driven Processing** is fully realized in the `Queue` class and steps like `Fired`, aligning with the asynchronous update requirement.


