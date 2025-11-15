"""
AIKA-Based Standard Neural Network Types using Builder Pattern

This module defines standard neural network base types that provide foundational
operations for neural network architectures. Like DOT-product and softmax types,
these define the basic building blocks that can be composed to create concrete networks.

Standard types include:
- STANDARD_NEURON with bias and tanh activation
- STANDARD_INPUT providing the universal input_value interface
- STANDARD_OUTPUT providing weighted multiplication

These base types serve as foundations for more specialized architectures like transformers.
"""



import aika
import aika.fields as af
import aika.network as an

class StandardNetworkTypeRegistry:
    """
    Standard neural network type registry that defines foundational object types 
    and their field relationships according to AIKA specifications using builder pattern.
    """
    
    def __init__(self):
        # Create type registry
        self.registry = af.TypeRegistry()
        
        # Build foundational types
        self._build_standard_types()
        
        # Setup field definitions
        self._setup_standard_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _build_standard_types(self):
        """Build standard foundational types that other architectures can inherit from."""
        print("Building standard neural network foundation types...")

        # ========================================
        # BUILD BASE STANDARD TYPES
        # ========================================

        # Build T_STANDARD_NEURON (root neuron type)
        standard_neuron_builder = an.NeuronTypeBuilder(self.registry, "STANDARD_NEURON")
        self.T_STANDARD_NEURON = standard_neuron_builder.build()

        # ========================================
        # BUILD INPUT-SIDE AND OUTPUT-SIDE BASE TYPES
        # ========================================

        # Build S_STANDARD_INPUT - provides input_value field
        standard_input_builder = an.SynapseTypeBuilder(self.registry, "STANDARD_INPUT")
        self.S_STANDARD_INPUT = standard_input_builder.build()

        # Build S_STANDARD_OUTPUT - provides weighted multiplication
        standard_output_builder = an.SynapseTypeBuilder(self.registry, "STANDARD_OUTPUT")
        self.S_STANDARD_OUTPUT = standard_output_builder.build()

        print("Standard foundation types built successfully")
    
    def _setup_standard_field_definitions(self):
        """Setup standard field definitions that will be inherited by derived types."""

        print("Setting up standard field definitions...")

        # ========================================
        # NEURON FIELDS
        # ========================================

        # Neuron bias field
        self.bias_field = self.T_STANDARD_NEURON.inputField("bias")

        # Standard activation fields:
        activation_type = self.T_STANDARD_NEURON.getActivationType()
        self.net_field = activation_type.sum("net")
        tanh_func = af.TanhActivationFunction()
        self.value_field = activation_type.fieldActivationFunc("value", tanh_func, 0.001)
        self.fired_field = activation_type.inputField("fired")

        # ========================================
        # INPUT-SIDE FIELDS
        # ========================================

        # Input-side provides input_value field (identity copy of activation.value)
        link_type_input = self.S_STANDARD_INPUT.getLinkType()
        self.input_value_field = link_type_input.identity("input_value")

        # ========================================
        # OUTPUT-SIDE FIELDS
        # ========================================

        # Output-side synapse weight field
        self.weight_field = self.S_STANDARD_OUTPUT.inputField("weight")

        # Output-side link weighted input
        link_type_output = self.S_STANDARD_OUTPUT.getLinkType();
        self.weighted_input = link_type_output.mul("weighted_input")

        # ========================================
        # ESTABLISH FIELD CONNECTIONS
        # ========================================
        print("Connecting field relationships...")

        # 1. input_value = input_activation.value (identity)
        self.input_value_field.input(an.LinkType.INPUT, self.value_field, 0)

        # 2. weighted_input = input_value × synapse.weight
        # weighted_input connects to input-side's input_value field via SELF relation
        self.weighted_input.input(an.LinkType.SELF, self.input_value_field, 0)
        # weighted_input connects to synapse weight via SYNAPSE relation
        self.weighted_input.input(an.LinkType.SYNAPSE, self.weight_field, 1)

        # 3. net = sum of weighted_inputs + neuron.bias
        # net field sums from incoming links via INPUT relation
        self.net_field.input(an.ActivationType.INPUT, self.weighted_input, 0)
        # net field adds neuron bias via NEURON relation
        self.net_field.input(an.ActivationType.NEURON, self.bias_field, 1)

        print("Standard field definitions and connections setup complete")
    
    def get_registry(self):
        """Return the type registry"""
        return self.registry
    
    def get_standard_neuron_type(self):
        """Return the standard neuron type for inheritance"""
        return self.T_STANDARD_NEURON

    def get_standard_input_type(self):
        """Return the standard input-side synapse type for inheritance"""
        return self.S_STANDARD_INPUT

    def get_standard_output_type(self):
        """Return the standard output-side synapse type for inheritance"""
        return self.S_STANDARD_OUTPUT

def create_standard_network_types():
    """Factory function to create and return the standard network type registry"""
    return StandardNetworkTypeRegistry()