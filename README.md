# AIKA: Artificial Intelligence for Knowledge Acquisition

## Overview
### Idea and Core Concept:
AIKA (Artificial Intelligence for Knowledge Acquisition) is an innovative approach to neural network design, diverging from traditional architectures that rely heavily on rigid matrix and vector operations.

The AIKA Project introduces a flexible, sparse, and non-layered network representation, derived from a type hierarchy. 

The Aika separates the primary neural network from its activation network. During the processing of a document, only those neurons and synapses in the activation network that are truly relevant are activated. The global neural network can encompass millions of neurons, yet the activation remains highly sparse. This sparsity is designed to enable efficient processing by focusing on the relevant subsections of the model.

### Linker and Binding Signals:
A central element is the so-called "Linker," which transfers the structure of the neural network to the activation network. Here, not only activations but also so-called binding signals propagate through the network. Binding signals are inspired by two concepts:

* Individual constants from predicate logic.
* Temporally synchronized spiking activity in biological neural networks.
### Event-Driven Processing:
The Aika algorithm processes all changes in the network asynchronously as time-ordered events. Whenever a neuron exceeds its activation threshold, this is recorded as an event in a queue. The algorithm then processes these events sequentially. This creates a clear temporal order in which activations and binding signals are propagated. This event-based approach allows the system to flexibly respond to dynamic changes in the network while ensuring the correctness of the computation sequence at all times.

### Type Hierarchy of Network Elements:
Unlike classical neural networks, which rely heavily on vector and matrix operations, Aika uses a hierarchy of different neuron types (e.g., excitatory, inhibitory) to define the architecture and behavior of the network. The mathematical models associated with these neuron types are organized as graphs of mathematical functions. This structure allows for flexible responses to dynamic changes in the activation network.

## Project Structure

### Fields Module
This module contains the mathematical core of AIKA, featuring:

- **Graph-Based Representation**: Declarative graph structures to represent the mathematical models.
- **Type Hierarchy**: Representing network elements such as neurons, synapses, and activations.
- **Event-Driven Updates**: Asynchronous state changes propagated via an event queue to maintain processing order.

The `Fields Module` acts as the foundation for building and instantiating the neural network. It ensures that state changes 
in the mathematical model are asynchronously propagated through the network.

### Neural Network Module
The Neural Network Module introduces a conceptual separation between:

1. **Neurons and Synapses**: Representing the static knowledge acquired by the network.
2. **Activations and Links**: Representing the dynamic information inferred from input data.

#### Key Concepts
- **Dual Graph Structure**: Separate graphs for neurons (knowledge representation) and activations (input-specific inference).
- **Dynamic Activation**: Multiple activations for a single neuron, each tied to specific occurrences in the input data.
- **Flexible Topology**: Abandoning fixed-layered architectures, the sequence of activations adapts dynamically to the input data.
- **Linker Component**: Translates the neural network's structure into the activation network while propagating binding signals.

## Getting Started

### Prerequisites
- Java 23 or higher
- Maven for dependency management

### Installation
Clone the repository and build the project using Maven:
```bash
# Clone the repository
git clone https://github.com/aika-algorithm/aika.git

# Navigate to the project directory
cd aika

# Build the project using Maven
mvn clean install
```

### Usage
1. Instantiate the neural network using the `Fields Module`.
2. Use the `Core Module` to process input data and generate activations.
3. The project is currently being heavily restructured. A good starting point for getting 
familiar with the project are the test-cases within the `Core Module`.

## Contributing
We welcome contributions from the open-source community! To contribute:
1. Fork the repository.
2. Create a new branch (`feature/your-feature-name`).
3. Commit your changes.
4. Submit a pull request.

## License
This project is licensed under the Apache License Version 2.0. See the [LICENSE](LICENSE) file for details.

---

Start exploring AIKA and join us in advancing the future of neural network design!
