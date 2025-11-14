"""
AIKA-Based Standard Neural Network Foundation using Builder Pattern

This module defines the foundational object types (neurons, synapses, activations, links) 
and field definitions for standard neural network operations using the AIKA framework
with the builder pattern architecture.

This serves as the base for more specialized network architectures like transformers.
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

        # Build T_STANDARD_NEURON and activation (root neuron type)
        standard_neuron_builder = an.NeuronTypeBuilder(self.registry, "STANDARD_NEURON")
        self.T_STANDARD_NEURON = standard_neuron_builder.build()
        self.T_STANDARD_ACTIVATION = self.T_STANDARD_NEURON.getActivationType()

        # ========================================
        # BUILD INPUT-SIDE AND OUTPUT-SIDE BASE TYPES
        # ========================================

        # Build S_STANDARD_INPUT_SIDE - provides input_value field
        standard_input_side_builder = an.SynapseTypeBuilder(self.registry, "STANDARD_INPUT_SIDE")
        self.S_STANDARD_INPUT_SIDE = standard_input_side_builder.build()
        self.L_STANDARD_INPUT_SIDE = self.S_STANDARD_INPUT_SIDE.getLinkType()

        # Build S_STANDARD_OUTPUT_SIDE - provides weighted multiplication
        standard_output_side_builder = an.SynapseTypeBuilder(self.registry, "STANDARD_OUTPUT_SIDE")
        self.S_STANDARD_OUTPUT_SIDE = standard_output_side_builder.build()
        self.L_STANDARD_OUTPUT_SIDE = self.S_STANDARD_OUTPUT_SIDE.getLinkType()

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
        self.net_field = self.T_STANDARD_ACTIVATION.sum("net")
        tanh_func = af.TanhActivationFunction()
        self.value_field = self.T_STANDARD_ACTIVATION.fieldActivationFunc("value", tanh_func, 0.001)
        self.fired_field = self.T_STANDARD_ACTIVATION.inputField("fired")

        # ========================================
        # INPUT-SIDE FIELDS
        # ========================================

        # Input-side provides input_value field (identity copy of activation.value)
        self.input_value_field = self.L_STANDARD_INPUT_SIDE.identity("input_value")

        # ========================================
        # OUTPUT-SIDE FIELDS
        # ========================================

        # Output-side synapse weight field
        self.weight_field = self.S_STANDARD_OUTPUT_SIDE.inputField("weight")

        # Output-side link weighted input
        self.weighted_input = self.L_STANDARD_OUTPUT_SIDE.mul("weighted_input")

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
    
    def get_standard_activation_type(self):
        """Return the standard activation type for inheritance"""
        return self.T_STANDARD_ACTIVATION
    
    def get_standard_input_side_type(self):
        """Return the standard input-side synapse type for inheritance"""
        return self.S_STANDARD_INPUT_SIDE

    def get_standard_output_side_type(self):
        """Return the standard output-side synapse type for inheritance"""
        return self.S_STANDARD_OUTPUT_SIDE

def create_standard_network_types():
    """Factory function to create and return the standard network type registry"""
    return StandardNetworkTypeRegistry()