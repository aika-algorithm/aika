"""
AIKA-Based Dot-Product Neural Network Types using Builder Pattern

This module defines specialized dot-product neural network types that provide
mathematical dot-product operations for transformer architectures and other
advanced models. These types do NOT inherit from standard neural network types
and have no bias or activation functions.

Based on the formal specification in specs/network/transformer.md
"""



import aika
import aika.fields as af
import aika.network as an

class DotProductTypeRegistry:
    """
    Dot-product type registry that defines specialized object types and their field relationships
    for dot-product neural network operations. These types implement the mathematical model
    for transformer attention mechanisms.
    
    DOT types do NOT inherit from standard neural network types:
    - No neuron bias
    - No activation functions  
    - No synapse weights
    - Pure mathematical dot-product operations
    """
    
    def __init__(self, registry, standard_value_field, standard_input_value_field):
        """Initialize with existing registry and standard field references"""
        self.registry = registry
        self.standard_value_field = standard_value_field
        self.standard_input_value_field = standard_input_value_field

        # Build dot-product types
        self._build_dot_product_types()

        # Set up dot-product field definitions
        self._setup_dot_product_field_definitions()

        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _build_dot_product_types(self):
        """Build dot-product types that don't inherit from standard types"""
        print("Building dot-product neural network types...")

        # ========================================
        # BUILD ABSTRACT DOT-PRODUCT NEURON TYPE
        # ========================================

        # Build T_DOT (abstract dot-product neuron and activation)
        # DOT neurons do NOT inherit from standard neurons - no bias, no activation function
        dot_builder = an.NeuronTypeBuilder(self.registry, "DOT_NEURON")
        self.T_DOT = dot_builder.build()
        self.T_DOT_ACT = self.T_DOT.getActivationType()

        print("Built abstract DOT neuron type (no bias, no activation function)")

        # ========================================
        # BUILD DOT-PRODUCT INPUT-SIDE AND OUTPUT-SIDE BASE TYPES
        # ========================================

        # Build S_DOT_PRIMARY_OUTPUT_SIDE - provides pair_product field (multiplication)
        dot_primary_output_side_builder = an.SynapseTypeBuilder(self.registry, "DOT_PRIMARY_OUTPUT_SIDE")
        self.S_DOT_PRIMARY_OUTPUT_SIDE = dot_primary_output_side_builder.build()
        self.L_DOT_PRIMARY_OUTPUT_SIDE = self.S_DOT_PRIMARY_OUTPUT_SIDE.getLinkType()

        # Build S_DOT_SECONDARY_OUTPUT_SIDE - provides secondary_identity field
        dot_secondary_output_side_builder = an.SynapseTypeBuilder(self.registry, "DOT_SECONDARY_OUTPUT_SIDE")
        self.S_DOT_SECONDARY_OUTPUT_SIDE = dot_secondary_output_side_builder.build()
        self.L_DOT_SECONDARY_OUTPUT_SIDE = self.S_DOT_SECONDARY_OUTPUT_SIDE.getLinkType()

        print("Built DOT input-side and output-side base types:")
        print("  - DOT_PRIMARY_OUTPUT_SIDE: Contains multiplication operation (no weights)")
        print("  - DOT_SECONDARY_OUTPUT_SIDE: Contains identity operation (no weights)")
        print("  - Note: Both use standard input-side for input_value field")

        print("Dot-product neural network types built successfully")
    
    def _setup_dot_product_field_definitions(self):
        """Setup dot-product field definitions for mathematical operations"""
        print("Setting up dot-product field definitions...")

        # ========================================
        # DOT-PRODUCT MATHEMATICAL MODEL IMPLEMENTATION
        # ========================================

        # Mathematical formula: f_net^DOT(a) = Σ C(p) where C(p) = input_value(l1) × input_value(l2)
        # - Primary synapses: multiplication of input_value fields
        # - Secondary synapses: pass-through of input_value
        # - Both use standard input-side for input_value field

        print("Implementing DOT-PRODUCT mathematical model...")

        # ========================================
        # DOT ACTIVATION FIELDS
        # ========================================

        # DOT net field: Sum all pair contributions from primary links
        self.dot_net_field = self.T_DOT_ACT.sum("net")

        # DOT value field: Identity (value = net, no activation function)
        self.dot_value_field = self.T_DOT_ACT.identity("value")
        # Connect value = net (identity function)
        self.dot_value_field.input(self.T_DOT_ACT.SELF, self.dot_net_field, 0)

        print("Set up DOT activation fields: net (sum) and value (identity)")

        # ========================================
        # DOT_SECONDARY_OUTPUT_SIDE LINK FIELDS (IDENTITY)
        # ========================================

        # Secondary output-side provides identity of input_value from input-side
        self.secondary_identity_field = self.L_DOT_SECONDARY_OUTPUT_SIDE.identity("secondary_identity")
        # Connect to input-side's input_value field via SELF relation
        self.secondary_identity_field.input(self.L_DOT_SECONDARY_OUTPUT_SIDE.SELF, self.standard_input_value_field, 0)

        print("Set up DOT_SECONDARY_OUTPUT_SIDE link: Identity of input_value")

        # ========================================
        # DOT_PRIMARY_OUTPUT_SIDE LINK FIELDS (MULTIPLICATION)
        # ========================================

        # Primary output-side multiplies input_value with paired secondary's input_value
        self.primary_multiplication_field = self.L_DOT_PRIMARY_OUTPUT_SIDE.mul("pair_product")
        # Input 0: This link's input_value (via SELF)
        self.primary_multiplication_field.input(self.L_DOT_PRIMARY_OUTPUT_SIDE.SELF, self.standard_input_value_field, 0)
        # Input 1: Paired secondary link's input_value (via PAIR_IN)
        self.primary_multiplication_field.input(self.L_DOT_PRIMARY_OUTPUT_SIDE.PAIR_IN, self.standard_input_value_field, 1)

        print("Set up DOT_PRIMARY_OUTPUT_SIDE link: Multiplication with PAIR_IN")

        # Connect DOT net field to primary multiplication results
        self.dot_net_field.input(self.T_DOT_ACT.INPUT, self.primary_multiplication_field, 0)

        print("Connected DOT net field to primary multiplication results")

        # ========================================
        # COMPLETED DOT-PRODUCT IMPLEMENTATION
        # ========================================

        # Store field references for access by concrete implementations
        self.dot_product_fields = {
            'dot_net': self.dot_net_field,
            'dot_value': self.dot_value_field,
            'secondary_identity': self.secondary_identity_field,
            'primary_multiplication': self.primary_multiplication_field,
        }

    
    def get_registry(self):
        """Return the type registry"""
        return self.registry
    
    
    # DOT type getters
    def get_dot_neuron_type(self):
        """Get the abstract DOT neuron type"""
        return self.T_DOT
    
    def get_dot_activation_type(self):
        """Get the abstract DOT activation type"""
        return self.T_DOT_ACT
    
    def get_dot_primary_output_side_type(self):
        """Get the DOT_PRIMARY_OUTPUT_SIDE type"""
        return self.S_DOT_PRIMARY_OUTPUT_SIDE

    def get_dot_secondary_output_side_type(self):
        """Get the DOT_SECONDARY_OUTPUT_SIDE type"""
        return self.S_DOT_SECONDARY_OUTPUT_SIDE

    def get_dot_product_fields(self):
        """Get the dot-product field definitions"""
        return self.dot_product_fields

def create_dot_product_types(registry, standard_value_field, standard_input_value_field):
    """Factory function to create and return the dot-product type registry"""
    return DotProductTypeRegistry(registry, standard_value_field, standard_input_value_field)