"""
AIKA-Based Softmax Neural Network Types using Builder Pattern

This module defines specialized softmax neural network types that provide
normalization operations for transformer architectures and other advanced models.
These types do NOT inherit from standard neural network types and have no bias
or synapse weights.

Based on the formal specification in specs/network/transformer.md
"""



import aika
import aika.fields as af
import aika.network as an

class SoftmaxTypeRegistry:
    """
    Softmax type registry that defines specialized object types and their field relationships
    for softmax normalization operations. These types implement normalization functions
    for transformer attention mechanisms.
    
    SOFTMAX types do NOT inherit from standard neural network types:
    - No neuron bias
    - No synapse weights
    - Pure mathematical normalization operations
    """
    
    def __init__(self, registry, standard_value_field, standard_input_value_field):
        """Initialize with existing registry and standard field references"""
        self.registry = registry
        self.standard_value_field = standard_value_field
        self.standard_input_value_field = standard_input_value_field

        # Build softmax types
        self._build_softmax_types()

        # Set up softmax field definitions
        self._setup_softmax_field_definitions()

        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _build_softmax_types(self):
        """Build softmax types that don't inherit from standard types"""
        print("Building softmax neural network types...")

        # ========================================
        # BUILD ABSTRACT SOFTMAX NEURON TYPE
        # ========================================

        # Build T_SOFTMAX (abstract softmax neuron and activation)
        # SOFTMAX neurons do NOT inherit from standard neurons - no bias
        softmax_builder = an.NeuronTypeBuilder(self.registry, "SOFTMAX_NEURON")
        self.T_SOFTMAX = softmax_builder.build()
        self.T_SOFTMAX_ACT = self.T_SOFTMAX.getActivationType()

        print("Built abstract SOFTMAX neuron type (no bias, specialized normalization)")

        # ========================================
        # BUILD SOFTMAX INPUT-SIDE AND OUTPUT-SIDE BASE TYPES
        # ========================================

        # Build S_SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE - output-side of input synapse, provides exponential field
        softmax_input_synapse_output_side_builder = an.SynapseTypeBuilder(self.registry, "SOFTMAX_OUTPUT_SIDE")
        self.S_SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE = softmax_input_synapse_output_side_builder.build()
        self.L_SOFTMAX_INPUT_LINK_OUTPUT_SIDE = self.S_SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE.getLinkType()

        # Build S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE - input-side of output synapse, provides normalized input_value via division
        softmax_output_synapse_input_side_builder = an.SynapseTypeBuilder(self.registry, "SOFTMAX_INPUT_SIDE")
        self.S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE = softmax_output_synapse_input_side_builder.build()
        self.L_SOFTMAX_OUTPUT_LINK_INPUT_SIDE = self.S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE.getLinkType()

        print("Built SOFTMAX input-side and output-side base types:")
        print("  - SOFTMAX_INPUT_LINK_OUTPUT_SIDE: Exponential operation (no weights)")
        print("  - SOFTMAX_OUTPUT_LINK_OUTPUT_SIDE: Provides normalized input_value via division (no weights)")
        print("  - Note: Input synapse uses standard input-side for input_value field")

        print("Softmax neural network types built successfully")
    
    def _setup_softmax_field_definitions(self):
        """Setup softmax field definitions for normalization operations"""
        print("Setting up softmax field definitions...")

        # ========================================
        # SOFTMAX MATHEMATICAL MODEL IMPLEMENTATION
        # ========================================

        # Softmax normalization: softmax(x_i) = exp(x_i) / Σ exp(x_j)
        # Input synapse: Uses standard input-side for input_value, output-side computes exp
        # Output synapse: Input-side computes normalization (exp/sum), output-side can forward or weight it

        print("Implementing SOFTMAX mathematical model...")

        # ========================================
        # SOFTMAX ACTIVATION FIELDS
        # ========================================

        # Softmax norm field: Sum of all exponentials from input synapses
        self.softmax_norm_field = self.T_SOFTMAX_ACT.sum("norm")

        print("Set up SOFTMAX activation field: norm (sum of exponentials)")

        # ========================================
        # SOFTMAX_INPUT_LINK_OUTPUT_SIDE FIELDS
        # ========================================

        # Input synapse output-side: Exponential of input_value
        self.softmax_exponential_field = self.L_SOFTMAX_INPUT_LINK_OUTPUT_SIDE.exp("exponential")
        # Connect to input-side's input_value via SELF relation
        self.softmax_exponential_field.input(an.LinkType.SELF, self.standard_input_value_field, 0)

        print("Set up SOFTMAX_INPUT_LINK_OUTPUT_SIDE: Exponential of input_value")

        # Connect softmax norm field to exponential fields (sum all exponentials)
        self.softmax_norm_field.input(an.ActivationType.INPUT, self.softmax_exponential_field, 0)

        # ========================================
        # SOFTMAX_OUTPUT_LINK_INPUT_SIDE FIELDS
        # ========================================

        # Output synapse input-side: Normalized input_value = exp / norm
        self.softmax_input_value_field = self.L_SOFTMAX_OUTPUT_LINK_INPUT_SIDE.div("input_value")
        # Input 0: Exponential from paired input synapse (via PAIR_IN)
        self.softmax_input_value_field.input(an.LinkType.PAIR_IN, self.softmax_exponential_field, 0)
        # Input 1: Norm from softmax activation (via INPUT)
        self.softmax_input_value_field.input(an.LinkType.INPUT, self.softmax_norm_field, 1)

        print("Set up SOFTMAX_OUTPUT_LINK_INPUT_SIDE: Normalized input_value (exp/sum)")

        # ========================================
        # COMPLETED SOFTMAX IMPLEMENTATION
        # ========================================

        # Store field references for access by concrete implementations
        self.softmax_fields = {
            'softmax_norm': self.softmax_norm_field,
            'softmax_exponential': self.softmax_exponential_field,
            'softmax_input_value': self.softmax_input_value_field,
        }

    
    def get_registry(self):
        """Return the type registry"""
        return self.registry
    
    
    # SOFTMAX type getters
    def get_softmax_neuron_type(self):
        """Get the abstract SOFTMAX neuron type"""
        return self.T_SOFTMAX

    def get_softmax_activation_type(self):
        """Get the abstract SOFTMAX activation type"""
        return self.T_SOFTMAX_ACT

    def get_softmax_input_synapse_output_side_type(self):
        """Get the SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE type"""
        return self.S_SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE

    def get_softmax_output_synapse_input_side_type(self):
        """Get the SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE type"""
        return self.S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE

    def get_softmax_fields(self):
        """Get the softmax field definitions"""
        return self.softmax_fields

def create_softmax_types(registry, standard_value_field, standard_input_value_field):
    """Factory function to create and return the softmax type registry"""
    return SoftmaxTypeRegistry(registry, standard_value_field, standard_input_value_field)