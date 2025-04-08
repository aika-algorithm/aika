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
- **Directory Structure**:
  ```
  - include
    - fields
    - network
  - src
    - fields
    - network
  ```

### Fields Module Classes
1. **Type Hierarchy and Object Graph**:
   - `type_registry_python.cpp`: Python-specific type registry bindings.
   - `type_registry.cpp`: Manages type registrations.
   - `type.cpp`: Defines base types for objects.
   - `obj.cpp`: Represents object instances.
   - `relation.cpp`: Handles relationships between objects.

2. **Field Graph and Definitions**:
   - `field_definition.cpp`: Defines field types.
   - `field.cpp`: Implements field instances.
   - `field_link_definition.cpp`: Defines field input edges.
   - `field_update.cpp`: Manages field value updates.
   - `abstract_function_definition.cpp`: Base for mathematical function definitions.

3. **Flattened Types**:
   - `flattened_type.cpp`: Preprocessed flat type structures for runtime efficiency.
   - `flattened_type_relation.cpp`: Relations in flattened types.

4. **Event Processing**:
   - `queue.cpp`: Event queue implementation.
   - `queue_key.cpp`: Event ordering keys.
   - `step.cpp`: Event processing steps.
   - `queue_interceptor.cpp`: Event interception logic.

5. **Mathematical Functions**:
   - `input_field.cpp`: Input field handling.
   - `addition.cpp`, `subtraction.cpp`, `multiplication.cpp`: Basic arithmetic operations.
   - `sum_field.cpp`: Summation function.
   - `identity_field.cpp`: Identity function.
   - `soft_max_fields.cpp`: Softmax function.
   - `scale_function.cpp`: Scaling operations.
   - `threshold_operator.cpp`: Thresholding logic.
   - `invert_function.cpp`: Inversion function.

6. **Utilities**:
   - `direction.cpp`: Direction-related utilities.
   - `utils.cpp`: General helper functions.

#### High-Level Description for each class

##### Type class
The Type class, housed within the Fields Module of the AIKA framework, serves as a cornerstone of the project's type hierarchy, defining the structural blueprint for network elements such as neurons and synapses. This class is pivotal in enabling AIKA's flexible, non-layered architecture by supporting multiple inheritance, which allows types to inherit properties and behaviors from multiple parent types. This capability fosters a rich and adaptable system for modeling complex relationships within the neural network.

Each Type instance encapsulates field definitions, which are mathematical properties or functions tied to the field graph—a declarative representation of the network's computational models. These definitions bridge the static structure of the network to its mathematical underpinnings. Additionally, the class manages relations, which likely play a role in facilitating binding signals that propagate through the activation network, supporting relational references during dynamic inference.

To enhance runtime efficiency—a key goal of AIKA for managing large-scale, sparse networks—the Type class includes mechanisms to generate flattened type structures. These precomputed representations streamline access to hierarchical information, aligning with the framework's emphasis on selective activation and relevance-based processing. By defining the types of objects instantiated in the object graph, which represents the static knowledge of the neural network (e.g., neurons and synapses), the Type class underpins the Neural Network Module, ensuring a seamless integration of static and dynamic components.

In essence, the Type class embodies AIKA's innovative approach by providing a flexible, hierarchical foundation for network elements, optimizing performance through flattened structures, and linking mathematical models to the network's static and dynamic behaviors.

### Neural Network Module
*(Note: Specific classes were not provided in the original description. The following is inferred based on context.)*
Likely includes classes for:
- Neurons and synapses (static graph).
- Activations and links (dynamic graph).
- Linker implementation.
- Binding signal management.

### Additional Notes
- **Inter-Module Dependency**: The Neural Network Module builds on the Fields Module.
- **Unit Tests**: Should cover all classes and functionalities for correctness.
- **Python Integration**: The example model uses pybind11 to interface with the C++ library, demonstrating practical application.

