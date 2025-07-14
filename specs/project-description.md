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

