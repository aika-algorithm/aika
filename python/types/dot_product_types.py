"""
AIKA-Based Dot-Product Neural Network Types using Builder Pattern

This module defines specialized dot-product neural network types that provide
mathematical dot-product operations for transformer architectures and other
advanced models. These types do NOT inherit from standard neural network types
and have no bias or activation functions.

Based on the formal specification in specs/network/transformer.md
"""

import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

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
    
    def __init__(self, registry, standard_value_field):
        """Initialize with existing registry and standard value field reference"""
        self.registry = registry
        self.standard_value_field = standard_value_field
        
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
        # BUILD ABSTRACT DOT-PRODUCT SYNAPSE TYPES
        # ========================================
        
        # Build abstract DOT synapse types (no weights, no standard inheritance)
        dot_primary_builder = an.SynapseTypeBuilder(self.registry, "DOT_PRIMARY_SYNAPSE")
        self.S_DOT_PRIMARY = dot_primary_builder.build()
        self.L_DOT_PRIMARY = self.S_DOT_PRIMARY.getLinkType()
        
        dot_secondary_builder = an.SynapseTypeBuilder(self.registry, "DOT_SECONDARY_SYNAPSE") 
        self.S_DOT_SECONDARY = dot_secondary_builder.build()
        self.L_DOT_SECONDARY = self.S_DOT_SECONDARY.getLinkType()
        
        print("Built abstract DOT synapse types:")
        print("  - DOT_PRIMARY_SYNAPSE: Contains multiplication operation (no weights)")
        print("  - DOT_SECONDARY_SYNAPSE: Contains identity operation (no weights)")
        
        print("Dot-product neural network types built successfully")
    
    def _setup_dot_product_field_definitions(self):
        """Setup dot-product field definitions for mathematical operations"""
        print("Setting up dot-product field definitions...")
        
        # ========================================
        # DOT-PRODUCT MATHEMATICAL MODEL IMPLEMENTATION
        # ========================================
        
        # Mathematical formula: f_net^DOT(a) = Σ C(p) where C(p) = weightedInput(l1) × weightedInput(l2)
        # - Primary synapses: multiplication operation
        # - Secondary synapses: identity operation
        
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
        # SECONDARY LINK FIELDS (IDENTITY)
        # ========================================
        
        # Secondary links provide identity operation: identityValue = input_activation.value
        self.secondary_identity_field = self.L_DOT_SECONDARY.identity("identityValue")
        # Connect to input activation's value via INPUT relation
        # Note: We connect to the standard value field since secondary links come from standard neurons
        self.secondary_identity_field.input(self.L_DOT_SECONDARY.INPUT, self.standard_value_field, 0)
        
        print("Set up DOT_SECONDARY link: Identity operation")
        
        # ========================================
        # PRIMARY LINK FIELDS (MULTIPLICATION)
        # ========================================
        
        # Primary links contain multiplication: input_activation.value × PAIR.identityValue
        self.primary_multiplication_field = self.L_DOT_PRIMARY.mul("pairMultiplication")
        # Input 0: Connect to input activation's value via INPUT relation
        self.primary_multiplication_field.input(self.L_DOT_PRIMARY.INPUT, self.standard_value_field, 0)
        # Input 1: Paired secondary link's identity value (via PAIR)
        self.primary_multiplication_field.input(self.L_DOT_PRIMARY.PAIR, self.secondary_identity_field, 1)
        
        print("Set up DOT_PRIMARY link: Multiplication with PAIR relation")
        
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
    
    def get_dot_primary_synapse_type(self):
        """Get the abstract DOT_PRIMARY synapse type"""
        return self.S_DOT_PRIMARY
    
    def get_dot_primary_link_type(self):
        """Get the abstract DOT_PRIMARY link type"""
        return self.L_DOT_PRIMARY
    
    def get_dot_secondary_synapse_type(self):
        """Get the abstract DOT_SECONDARY synapse type"""
        return self.S_DOT_SECONDARY
    
    def get_dot_secondary_link_type(self):
        """Get the abstract DOT_SECONDARY link type"""
        return self.L_DOT_SECONDARY
    
    def get_dot_product_fields(self):
        """Get the dot-product field definitions"""
        return self.dot_product_fields

def create_dot_product_types(registry, standard_value_field):
    """Factory function to create and return the dot-product type registry"""
    return DotProductTypeRegistry(registry, standard_value_field)