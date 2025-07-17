# Aika

## Project Status

Work in progress!

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

To install and set up Aika, make sure you have a Python 3 environment (a virtual environment is recommended) and the necessary build tools (CMake, a C++ compiler, etc.). Once you have the Aika source code, use the following commands to build and install the project:

```bash
python3 -m venv .venv
source .venv/bin/activate
make clean
cmake --build . --target install
python tests/subtraction-test.py
```

Additionally, ensure that the `parameterized` package is installed for running parameterized tests. You can install it using:

```bash
pip install parameterized
```

**Steps explained:**

1. Activate the Python virtual environment (here, `.venv`) so that Python dependencies and the installation target are set up correctly.
2. Run `make clean` to remove any previous build artifacts (ensuring a fresh build).
3. Use CMake to build the C++ core and install the Python package (`aika`). This will compile the C++ code and put the resulting Python module into the environment.
4. Run the provided test script (`tests/subtraction-test.py`) to verify that the `aika` module was built and loaded correctly. This script will exercise a simple subtraction example using the Aika API.

After these steps, the `aika` package should be installed in your environment, and you can import it in Python to start defining your own models.

## Usage Example

Below is an example (from the test suite) demonstrating how to use Aika’s Python API. In this snippet, we define two simple types `A` and `B`, create input fields on them, define a derived field `c` as a subtraction (`sub`) operation on type `B`, and then link the inputs of `c` to the fields of `A` and `B` using a relation. Finally, we instantiate objects of those types to show how the type definitions lead to actual runtime objects:

```python
def testSubtraction(self):
    print("Module 'aika' was loaded from:", aika.__file__)

    TEST_RELATION_FROM = aika.RelationOne(1, "TEST_FROM")
    TEST_RELATION_TO = aika.RelationOne(2, "TEST_TO")
    TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
    TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

    assert isinstance(TEST_RELATION_FROM, aika.Relation)
    assert isinstance(TEST_RELATION_TO, aika.Relation)

    registry = aika.TypeRegistry()

    typeA = aika.TestType(registry, "A")
    typeB = aika.TestType(registry, "B")

    a = typeA.inputField("a")
    b = typeB.inputField("b")

    c = typeB.sub("c")

    print("Type of c:", type(c))
    print("Type of TEST_RELATION_FROM:", type(TEST_RELATION_FROM))
    print("Type of a:", type(a))

    assert isinstance(a, aika.FieldDefinition)
    assert isinstance(c, aika.FieldDefinition)

    c.input(TEST_RELATION_FROM, a, 0)
    c.input(TEST_RELATION_FROM, b, 1)

    registry.flattenTypeHierarchy()

    oa = typeA.instantiate()
    ob = typeB.instantiate()
```

In this example, we:

- Create two one-to-one relations (`RelationOne`) named `"TEST_FROM"` and `"TEST_TO"` and mark them as reverses of each other (so the relationship is bidirectional).
- Set up a `TypeRegistry` and define two new types `A` and `B` (using a test class `TestType`).
- Define an input field on type `A` (named `"a"`) and an input field on type `B` (named `"b"`).
- Define a field `c` on type `B` as the result of a subtraction operation (`sub`). (The exact behavior of `sub` would be defined in Aika’s library for combining inputs.)
- Link the inputs of `c`: the code `c.input(TEST_RELATION_FROM, a, 0)` means that the 0th input of `c` comes from field `a` via the relation `TEST_FROM`, and similarly the 1st input of `c` comes from field `b`.
- Call `registry.flattenTypeHierarchy()`, which finalizes the type definitions (resolving the type hierarchy so that the relationships between types are ready for instantiation).
- Finally, instantiate objects `oa` and `ob` of types `A` and `B` respectively. These objects would have fields corresponding to what was defined (e.g., `oa` has field `a`).

This is a basic illustration of how Aika allows you to define types and relations in Python, construct a computation (here a subtraction) linking those types, and then create actual instances that carry out the computation. (Note: The example is incomplete and meant for demonstration; a real use-case would include running the network, feeding data, and retrieving outputs.)

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
