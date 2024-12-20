# AIKA: Artificial Intelligence for Knowledge Acquisition

## Overview
AIKA (Artificial Intelligence for Knowledge Acquisition) is an innovative approach to neural network design, diverging from traditional architectures that rely heavily on rigid matrix and vector operations.

The AIKA Project introduces a flexible, sparse, and non-layered network representation, derived from a type hierarchy. By separating the static structure of the neural network from its dynamic activation network, AIKA enables efficient, context-aware processing. This approach ensures that only the relevant neurons and synapses are activated during the processing of input data, maintaining sparsity and computational efficiency, even in large networks with millions of neurons.

### Key Features
- **Sparse Activation**: Only relevant network components are activated, reducing computational overhead.
- **Type Hierarchy**: Defines various neuron types (e.g., excitatory, inhibitory) to model network architecture and behavior.
- **Non-Layered Representation**: Moves away from fixed-layered topologies, allowing dynamic activation sequences driven by input data.

### Design Philosophy
AIKA is inspired by:
1. **Predicate Logic**: Using individual constants to model unique identifiers.
2. **Biological Neural Networks**: Incorporating concepts of temporally synchronized spike activity.

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
2. Use the `Neural Network Module` to process input data and generate activations.
3. Explore the sparse activation network and analyze the results.

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
