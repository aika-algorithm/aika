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
---The C++ code is intended to be high performance with as little overhead as possible. The code base should avoid smart pointers and manage its memory manully. 
For perfomance critical portions of the code, dynamic data structures should be avoided and array based data-structure should be used instead. ---


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
    - **Purpose**: Implements a softmax operation across multiple inputs.
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

### `network.aika.fields.defs`

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


