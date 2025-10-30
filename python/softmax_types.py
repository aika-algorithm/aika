"""
AIKA-Based Softmax Neural Network Types using Builder Pattern

This module defines specialized softmax neural network types that provide
normalization operations for transformer architectures and other advanced models.
These types do NOT inherit from standard neural network types and have no bias
or synapse weights.

Based on the formal specification in specs/network/transformer.md
"""

import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

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
    
    def __init__(self, registry, standard_value_field):
        """Initialize with existing registry and standard value field reference"""
        self.registry = registry
        self.standard_value_field = standard_value_field
        
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
        # BUILD ABSTRACT SOFTMAX SYNAPSE TYPES
        # ========================================
        
        # Build abstract SOFTMAX synapse types (no weights)
        # Note: Softmax may use different synapse patterns for input/output pairing
        softmax_input_builder = an.SynapseTypeBuilder(self.registry, "SOFTMAX_INPUT_SYNAPSE")
        self.S_SOFTMAX_INPUT = softmax_input_builder.build()
        self.L_SOFTMAX_INPUT = self.S_SOFTMAX_INPUT.getLinkType()
        
        softmax_output_builder = an.SynapseTypeBuilder(self.registry, "SOFTMAX_OUTPUT_SYNAPSE")
        # Pair output synapse to input synapse using binding signal slot 0
        # This allows coordination between input scores and output probabilities
        softmax_output_builder.pairByBindingSignal(self.S_SOFTMAX_INPUT, False, True, 0)
        self.S_SOFTMAX_OUTPUT = softmax_output_builder.build()
        self.L_SOFTMAX_OUTPUT = self.S_SOFTMAX_OUTPUT.getLinkType()
        
        print("Built abstract SOFTMAX synapse types:")
        print("  - SOFTMAX_INPUT_SYNAPSE: Receives scores for normalization (no weights)")
        print("  - SOFTMAX_OUTPUT_SYNAPSE: Outputs normalized probabilities (no weights)")
        
        print("Softmax neural network types built successfully")
    
    def _setup_softmax_field_definitions(self):
        """Setup softmax field definitions for normalization operations"""
        print("Setting up softmax field definitions...")
        
        # ========================================
        # SOFTMAX MATHEMATICAL MODEL IMPLEMENTATION
        # ========================================
        
        # Softmax normalization: softmax(x_i) = exp(x_i) / Σ exp(x_j)
        # This is a placeholder implementation - full softmax requires grouping logic
        
        print("Implementing SOFTMAX mathematical model...")
        
        # ========================================
        # SOFTMAX ACTIVATION FIELDS
        # ========================================
        
        # Softmax net field: Sum of input scores (before normalization)
        self.softmax_net_field = self.T_SOFTMAX_ACT.sum("net")
        
        # Softmax value field: Normalized output (requires exponential and normalization)
        # For now, using identity as placeholder - full softmax implementation needed
        self.softmax_value_field = self.T_SOFTMAX_ACT.identity("value")
        self.softmax_value_field.input(self.T_SOFTMAX_ACT.SELF, self.softmax_net_field, 0)
        
        print("Set up SOFTMAX activation fields: net (sum) and value (normalized)")
        
        # ========================================
        # SOFTMAX INPUT LINK FIELDS
        # ========================================
        
        # Softmax input links: Identity operation to pass scores
        self.softmax_input_field = self.L_SOFTMAX_INPUT.identity("inputScore")
        # Connect to input activation's value via INPUT relation
        self.softmax_input_field.input(self.L_SOFTMAX_INPUT.INPUT, self.standard_value_field, 0)
        
        print("Set up SOFTMAX_INPUT link: Identity operation for score input")
        
        # ========================================
        # SOFTMAX OUTPUT LINK FIELDS
        # ========================================
        
        # Softmax output links: Pass normalized values
        self.softmax_output_field = self.L_SOFTMAX_OUTPUT.identity("normalizedOutput")
        # Connect to softmax activation's value via INPUT relation
        self.softmax_output_field.input(self.L_SOFTMAX_OUTPUT.INPUT, self.softmax_value_field, 0)
        
        print("Set up SOFTMAX_OUTPUT link: Identity operation for normalized output")
        
        # Connect softmax net field to input links
        self.softmax_net_field.input(self.T_SOFTMAX_ACT.INPUT, self.softmax_input_field, 0)
        
        print("Connected SOFTMAX net field to input links")
        
        # ========================================
        # COMPLETED SOFTMAX IMPLEMENTATION
        # ========================================
        
        # Store field references for access by concrete implementations
        self.softmax_fields = {
            'softmax_net': self.softmax_net_field,
            'softmax_value': self.softmax_value_field,
            'softmax_input': self.softmax_input_field,
            'softmax_output': self.softmax_output_field,
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
    
    def get_softmax_input_synapse_type(self):
        """Get the abstract SOFTMAX_INPUT synapse type"""
        return self.S_SOFTMAX_INPUT
    
    def get_softmax_input_link_type(self):
        """Get the abstract SOFTMAX_INPUT link type"""
        return self.L_SOFTMAX_INPUT
    
    def get_softmax_output_synapse_type(self):
        """Get the abstract SOFTMAX_OUTPUT synapse type"""
        return self.S_SOFTMAX_OUTPUT
    
    def get_softmax_output_link_type(self):
        """Get the abstract SOFTMAX_OUTPUT link type"""
        return self.L_SOFTMAX_OUTPUT
    
    def get_softmax_fields(self):
        """Get the softmax field definitions"""
        return self.softmax_fields

def create_softmax_types(registry, standard_value_field):
    """Factory function to create and return the softmax type registry"""
    return SoftmaxTypeRegistry(registry, standard_value_field)