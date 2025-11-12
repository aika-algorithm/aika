# Aika

## Project Status

**Current Version**: Beta (Active Development)
**Overall Completion**: ~72%

### Module Status

- ✅ **Fields Module (100%)** - Fully implemented and production-ready
  - Type system with hierarchy and flattening
  - All mathematical operations (addition, subtraction, multiplication, division, exponential)
  - Event-driven queue with correct propagation
  - All activation functions (Tanh, ReLU, Sigmoid, Linear)

- ⚠️ **Network Module (85%)** - Core functionality complete, advanced features in progress
  - Complete type system for neurons, activations, synapses, and links
  - Builder pattern for easy type construction
  - Basic linking and propagation working
  - **Missing**: Latent linking mechanism (required for advanced attention)

- ⚠️ **Transformer Implementation (60%)** - Type structure complete, math incomplete
  - All transformer types defined (EMB, KEY, QUERY, VALUE, COMP, MIX)
  - Dot-product architecture implemented
  - **Critical Gap**: Softmax normalization formula needs correction
  - **Blocker**: Latent linking required for full attention mechanism

### What Works
- Field-based computations with type hierarchies
- Basic neural network construction and execution
- Standard network patterns (feedforward connections)
- Event-driven propagation
- Builder-based type definitions

### Known Limitations
- Transformer attention mechanism not yet functional (softmax formula incomplete)
- Latent linking for complex binding signal unification not implemented
- Some recent specification updates pending implementation

See [SPEC_ALIGNMENT_REPORT.md](SPEC_ALIGNMENT_REPORT.md) for detailed implementation status.

## Project Overview

Aika (Artificial Intelligence for Knowledge Acquisition) is an experimental AI framework that offers a new approach to neural network design. Originally implemented in Java, Aika has been migrated to a C++ core with Python bindings, combining high performance with Python’s ease of use. Similar to PyTorch, Aika allows you to **define the functional graph of a neural network’s mathematical model in Python while executing the heavy computations in C++**. This means you can construct your model using intuitive Python code, and under the hood Aika leverages efficient C++ for execution.

**Key Design Features:**

- **Event-Driven Processing:** Instead of the usual layer-by-layer synchronous computation, Aika processes changes asynchronously via an event queue. Neuronal activations are handled as time-ordered events, allowing the network to react to inputs in a flexible sequence rather than a fixed feed-forward pass.
- **Dynamic Object Instantiation:** Aika’s network elements (neurons, synapses, activations, etc.) are not all created upfront with fixed dimensions. They can be instantiated on-the-fly at runtime from a defined type hierarchy. This dynamic creation means the model can grow or adjust its structure as data is processed, rather than being constrained to a static graph.
- **Type-Hierarchy-Based Graph:** The architecture is built around a type system. Neural network components are defined in a class/type hierarchy, and the functional graph of the model is represented based on these types. This type-driven graph representation leads to a flexible, sparse, and non-layered network structure (in contrast to the rigid layered architecture of traditional neural nets). The type hierarchy also improves organization and reuse of components when building complex models.

In summary, Aika diverges from conventional neural network frameworks by separating the knowledge representation (the types of neurons/synapses) from the activation instances. Its event-driven, type-based approach aims to more closely mimic certain aspects of biological neural processing and provide greater flexibility in how networks can be constructed and interpreted.

## Formal Descriptions

### Fields Module
- [Fields and Type Hierarchy](specs/fields/field-and-type-system.md)
- [Type Flattening](specs/fields/flattening.md)
- [Processing Queue](specs/fields/queue.md)

### Neural Network Module
- [Neural Network](specs/network/network.md)

## Installation Instructions

To install and set up Aika, make sure you have a Python 3 environment (a virtual environment is recommended) and the necessary build tools (CMake, a C++ compiler, etc.).

### Prerequisites

- **Python 3.8+** with pip
- **CMake 3.30+**
- **C++ compiler** with C++20 support
- **pybind11** - Installed automatically via pip, or manually via:
  - macOS: `brew install pybind11`
  - Linux: `apt-get install pybind11-dev` or `pip install pybind11`
  - Windows: `pip install pybind11`

### Build and Install

Once you have the prerequisites, use the following commands to build and install the project:

```bash
# Create and activate virtual environment
python3 -m venv .venv
source .venv/bin/activate

# Install Python package in development mode (this installs dependencies too)
pip install -e .

# Build the C++ core (out-of-source)
mkdir -p build && cd build
cmake ..
cmake --build . --target install

# Run a test to verify installation
cd ..
python tests/python/fields/subtraction-test.py
```

**Steps explained:**

1. **Create and activate a virtual environment** (`.venv`) so that Python dependencies and the installation target are set up correctly.
2. **Install dependencies** like `parameterized` which is needed for running parameterized tests.
3. **Build the project** using CMake with an out-of-source build: Creates a separate `build/` directory for all build artifacts, keeping the source tree clean.
4. **Run a test script** (`tests/python/fields/subtraction-test.py`) to verify that the `aika` module was built and installed correctly. This script exercises a simple subtraction example using the Aika API.

After these steps, the `aika` package should be installed in your environment, and you can import it in Python to start defining your own models.

### Project Structure

The project is organized as follows:

- **`src/`** - C++ source code (core library and Python bindings)
- **`include/`** - C++ header files
- **`python/`** - Python helper modules and utilities
  - `networks/` - Network architectures (standard, transformer)
  - `types/` - Type definitions (softmax, dot product)
  - `utils/` - Debugging and utility functions
  - `examples/` - Example implementations
- **`tests/`** - Test suite
  - `cpp/` - C++ unit tests
  - `python/` - Python unit tests
- **`specs/`** - Formal specifications and documentation (markdown)
- **`build/`** - Build artifacts (created during out-of-source build)

All project documentation is maintained in markdown format in the root directory (`README.md`, `CLAUDE.md`) and formal specifications in the `specs/` directory.

## Usage Examples

### Basic Field Operations

This example demonstrates Aika's core concepts: defining types with field operations, establishing relations between types, and executing computations through the event-driven system.

```python
import aika
import aika.fields as af

# Define bidirectional relations between types
TEST_RELATION_FROM = af.RelationOne(1, "TEST_FROM")
TEST_RELATION_TO = af.RelationOne(2, "TEST_TO")
TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

# Create type registry and define types
registry = af.TypeRegistry()
typeA = af.TestType(registry, "A")
typeB = af.TestType(registry, "B")

# Define input fields
a = typeA.inputField("a")
b = typeA.inputField("b")

# Define computed field (c = a - b)
c = typeB.sub("c")
c.input(TEST_RELATION_FROM, a, 0)  # First input: field 'a' from related object
c.input(TEST_RELATION_FROM, b, 1)  # Second input: field 'b' from related object

# Finalize type definitions
registry.flattenTypeHierarchy()

# Instantiate objects
oa = typeA.instantiate()
ob = typeB.instantiate()

# Link objects and initialize computed fields
af.TestObj.linkObjects(oa, ob)
ob.initFields()

# Set input values
oa.setFieldValue(a, 50.0)
oa.setFieldValue(b, 20.0)

# Read computed result
result = ob.getFieldValue(c)  # Returns 30.0 (50 - 20)
print(f"Result: {result}")
```

**Key Concepts Demonstrated:**
- **Type Definition**: Creating types with input and computed fields
- **Relations**: Establishing bidirectional relationships between types
- **Field Operations**: Using built-in operations (sub, add, mul, etc.)
- **Type Flattening**: Finalizing the type hierarchy before instantiation
- **Event-Driven Execution**: Field updates propagate automatically through the computation graph
- **Object Linking**: Connecting object instances via defined relations

### Network Construction

For neural network construction, use the builder pattern:

```python
import aika.network as an
from python.networks.standard_network import create_standard_network_types

# Create standard network types
network = create_standard_network_types()
registry = network.get_registry()
model = an.Model(registry)

# Instantiate neurons using the type system
input_neuron = network.T_STANDARD_NEURON.instantiate(model)
output_neuron = network.T_STANDARD_NEURON.instantiate(model)

# Create synapses to connect neurons
synapse = network.T_STANDARD_SYNAPSE.instantiate(
    input_neuron,
    output_neuron
)
```

**Note**: The network module provides the foundation for building neural architectures. The Fields Module (100% complete) powers the underlying field propagation system.

### Transformer Architecture (Experimental)

The transformer implementation provides type structures for attention mechanisms:

```python
from python.networks.transformer import create_transformer_types

# Create transformer type hierarchy
transformer = create_transformer_types()

# Access transformer neuron types
emb_type = transformer.T_EMB        # Embedding neurons
key_type = transformer.T_KEY        # Key neurons (Q in attention)
query_type = transformer.T_QUERY    # Query neurons (K in attention)
value_type = transformer.T_VALUE    # Value neurons (V in attention)
attention_type = transformer.T_SOFTMAX  # Attention/Softmax neurons
```

**⚠️ Current Status**: Type hierarchy is complete, but the attention mechanism requires:
- Corrected softmax normalization formula (currently uses sum instead of exp)
- Latent linking implementation for binding signal unification
- See [SPEC_ALIGNMENT_REPORT.md](SPEC_ALIGNMENT_REPORT.md) for details

### Available Operations

The Fields Module provides these operations for field definitions:
- **Arithmetic**: `add()`, `sub()`, `mul()`, `div()`
- **Functions**: `exp()`, `sum()`, `identity()`
- **Activations**: `tanh()`, `relu()`, `sigmoid()`, `linear()`

All operations support the event-driven propagation model, where field updates automatically trigger recomputation of dependent fields.

## Comparison with PyTorch

While Aika’s interface might remind you of PyTorch (Python-defined models with a C++ backend), its underlying philosophy and execution model are quite different. Here are some key differences between Aika and PyTorch:

- **Dynamic Object Graph vs. Static Tensor Ops:** PyTorch primarily uses predefined tensor operations (linear layers, conv layers, etc.) on fixed-size vectors/matrices. In contrast, Aika builds its computations through dynamically instantiated objects derived from a type hierarchy. Instead of static layers with set dimensions, Aika’s network can spawn new neuron or activation instances on the fly as needed, allowing it to handle variable structures and sparsely populated data more naturally.
- **Event-Driven Asynchrony vs. Synchronous Execution:** In PyTorch, computations typically proceed in a synchronous manner (you run a forward pass, then backward pass, often on all data in a batch or sequence). Aika, however, employs an event-driven mechanism: when certain conditions are met (e.g., a neuron’s threshold is exceeded), an event is queued and processed in order. This means Aika processes information in a time-sequenced way, reacting to each activation event, which can lead to different parts of the network updating at different times. This queue-based event processing is designed to efficiently handle scenarios where data or activations are sparse or sequentially dependent.
- **Flexible Topology vs. Layered Architecture:** PyTorch models are usually defined as a stack of layers or modules that data flows through. Aika does away with a strictly layered design; its functional graph is tied to the objects created during runtime. Because of the type hierarchy and dynamic instantiation, the network topology in Aika can be more flexible and adaptive. In other words, the structure of the computation in Aika can change depending on the input and the events triggered, whereas PyTorch’s computation graph is generally fixed once the model is defined (aside from control-flow logic in dynamic graphs). This flexibility can make Aika’s models more interpretable in terms of individual activations and their relationships, since each activation is an object that can be inspected, traced, or linked to specific input conditions.

In summary, PyTorch excels at high-throughput numerical computation with static graphs and batched data, while Aika explores a more **adaptive, event-driven paradigm** where the focus is on individual activations and their interactions. Aika’s approach may be advantageous for research into neural architectures that require dynamic structure or need to capture fine-grained causality and interactions that are not easily represented in matrix form.

## Target Audience

Aika is aimed at AI researchers and advanced practitioners who are interested in exploring alternative approaches to deep learning architectures. If you are looking to experiment with neural networks that emphasize event-driven computation, dynamic graph structures, and potential interpretability benefits, Aika provides a framework to do so. The project is particularly relevant for those who want to move beyond the conventional layer-based, batch-oriented neural network paradigm and investigate models that behave more like event-driven systems or symbolic reasoning engines while still being grounded in neural computations.

## License

This project is licensed under the **Apache License 2.0**. You are free to use, modify, and distribute Aika in accordance with the terms of this license. For more details, see the `LICENSE` file included in the repository.
