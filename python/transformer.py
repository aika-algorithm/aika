"""
AIKA-Based Transformer Neural Network Type Definitions using Builder Pattern

This module defines the transformer-specific object types (dot-product neurons, softmax, etc.) 
for a minimal transformer-like architecture using the AIKA framework
with the new builder pattern architecture.

Based on the formal specification in specs/network/transformer.md
"""

import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an
from python.standard_network import create_standard_network_types

class TransformerTypeRegistry:
    """
    Transformer type registry that defines transformer-specific object types and their field relationships
    according to the AIKA transformer specification using builder pattern.
    
    This builds on the standard neural network foundation.
    """
    
    def __init__(self):
        # Create the standard network foundation
        print("Setting up standard neural network foundation...")
        self.standard_network = create_standard_network_types()
        self.registry = self.standard_network.get_registry()
        
        # Get standard types for inheritance
        self.T_STANDARD_NEURON = self.standard_network.get_standard_neuron_type()
        self.T_STANDARD_ACTIVATION = self.standard_network.get_standard_activation_type()
        self.T_STANDARD_SYNAPSE = self.standard_network.get_standard_synapse_type()
        self.T_STANDARD_LINK = self.standard_network.get_standard_link_type()
        
        # Build transformer-specific types
        self._build_transformer_types()
        
        # Set up dot-product field definitions
        self._setup_dot_product_fields()
        
        # Flatten type hierarchy (includes standard + transformer types)
        self.registry.flattenTypeHierarchy()
    
    def _build_transformer_types(self):
        """Build transformer-specific types that inherit from the standard foundation"""
        print("Building transformer-specific types...")
        
        # ========================================
        # BUILD TRANSFORMER NEURON TYPES
        # ========================================
        
        # Build T_EMB (embedding neuron and activation)
        emb_builder = an.NeuronTypeBuilder(self.registry, "EMB_NEURON")
        emb_builder.addParent(self.T_STANDARD_NEURON)
        self.T_EMB = emb_builder.build()
        self.T_EMB_ACT = self.T_EMB.getActivationType()
        
        # Build T_KEY (key neuron and activation)
        key_builder = an.NeuronTypeBuilder(self.registry, "KEY_NEURON")
        key_builder.addParent(self.T_STANDARD_NEURON)
        self.T_KEY = key_builder.build()
        self.T_KEY_ACT = self.T_KEY.getActivationType()
        
        # Build T_QUERY (query neuron and activation)
        query_builder = an.NeuronTypeBuilder(self.registry, "QUERY_NEURON")
        query_builder.addParent(self.T_STANDARD_NEURON)
        self.T_QUERY = query_builder.build()
        self.T_QUERY_ACT = self.T_QUERY.getActivationType()
        
        # Build T_VALUE (value neuron and activation)
        value_builder = an.NeuronTypeBuilder(self.registry, "VALUE_NEURON")
        value_builder.addParent(self.T_STANDARD_NEURON)
        self.T_VALUE = value_builder.build()
        self.T_VALUE_ACT = self.T_VALUE.getActivationType()
        
        # ========================================
        # BUILD DOT-PRODUCT FAMILY TYPES
        # ========================================
        
        # Build T_DOT (abstract dot-product neuron and activation)
        # DOT neurons do NOT inherit from standard neurons - no bias, no activation function
        dot_builder = an.NeuronTypeBuilder(self.registry, "DOT_NEURON")
        self.T_DOT = dot_builder.build()
        self.T_DOT_ACT = self.T_DOT.getActivationType()
        
        # Build T_COMP (comparison neuron and activation) - inherits from DOT
        comp_builder = an.NeuronTypeBuilder(self.registry, "COMP_NEURON")
        comp_builder.addParent(self.T_DOT)
        self.T_COMP = comp_builder.build()
        self.T_COMP_ACT = self.T_COMP.getActivationType()
        
        # Build T_MIX (mixing neuron and activation) - inherits from DOT
        mix_builder = an.NeuronTypeBuilder(self.registry, "MIX_NEURON")
        mix_builder.addParent(self.T_DOT)
        self.T_MIX = mix_builder.build()
        self.T_MIX_ACT = self.T_MIX.getActivationType()
        
        # ========================================
        # BUILD SOFTMAX TYPE
        # ========================================
        
        # Build T_SOFTMAX (softmax neuron and activation)
        softmax_builder = an.NeuronTypeBuilder(self.registry, "SOFTMAX_NEURON")
        softmax_builder.addParent(self.T_STANDARD_NEURON)
        self.T_SOFTMAX = softmax_builder.build()
        self.T_SOFTMAX_ACT = self.T_SOFTMAX.getActivationType()
        
        # ========================================
        # BUILD TRANSFORMER SYNAPSE TYPES
        # ========================================
        
        # Build S_EMB_KEY (embedding to key synapse and link)
        emb_key_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_KEY")
        emb_key_builder.setInput(self.T_EMB).setOutput(self.T_KEY).addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_KEY = emb_key_builder.build()
        self.L_EMB_KEY = self.S_EMB_KEY.getLinkType()
        
        # Build S_EMB_QUERY (embedding to query synapse and link)
        emb_query_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_QUERY")
        emb_query_builder.setInput(self.T_EMB).setOutput(self.T_QUERY).addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_QUERY = emb_query_builder.build()
        self.L_EMB_QUERY = self.S_EMB_QUERY.getLinkType()
        
        # Build S_EMB_VALUE (embedding to value synapse and link)
        emb_value_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_VALUE")
        emb_value_builder.setInput(self.T_EMB).setOutput(self.T_VALUE).addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_VALUE = emb_value_builder.build()
        self.L_EMB_VALUE = self.S_EMB_VALUE.getLinkType()
        
        # Build S_KEY_QUERY (key to query synapse and link)
        key_query_builder = an.SynapseTypeBuilder(self.registry, "S_KEY_QUERY")
        key_query_builder.setInput(self.T_KEY).setOutput(self.T_QUERY).addParent(self.T_STANDARD_SYNAPSE)
        self.S_KEY_QUERY = key_query_builder.build()
        self.L_KEY_QUERY = self.S_KEY_QUERY.getLinkType()
        
        # ========================================
        # BUILD DOT-PRODUCT SYNAPSE TYPES
        # ========================================
        
        # Build S_KEY_COMP (key to comparison synapse and link)
        key_comp_builder = an.SynapseTypeBuilder(self.registry, "S_KEY_COMP")
        key_comp_builder.setInput(self.T_KEY).setOutput(self.T_COMP).addParent(self.T_STANDARD_SYNAPSE)
        
        # Build S_QUERY_COMP (query to comparison synapse and link)
        query_comp_builder = an.SynapseTypeBuilder(self.registry, "S_QUERY_COMP")
        query_comp_builder.setInput(self.T_QUERY).setOutput(self.T_COMP).addParent(self.T_STANDARD_SYNAPSE)
        
        # Build the synapse types first
        self.S_KEY_COMP = key_comp_builder.build()
        self.S_QUERY_COMP = query_comp_builder.build()
        
        # ========================================
        # SET UP SYNAPSE PAIRING FOR DOT-PRODUCT
        # ========================================
        
        # Set up pairing between the built synapse types
        # Note: Using setPairedSynapseType on the builder during build phase
        try:
            # Try to set up pairing if the method exists
            key_comp_builder.setPairedSynapseType(self.S_QUERY_COMP)
            query_comp_builder.setPairedSynapseType(self.S_KEY_COMP)
            print("Set up KEY_COMP ↔ QUERY_COMP synapse pairing")
        except AttributeError:
            # If setPairedSynapseType doesn't exist on builder, handle pairing differently
            print("Note: Synapse pairing will be handled by PAIR_IN relations in field system")
        
        self.L_KEY_COMP = self.S_KEY_COMP.getLinkType()
        self.L_QUERY_COMP = self.S_QUERY_COMP.getLinkType()
        
        # Build S_COMP_SOFTMAX (comparison to softmax synapse and link)
        comp_softmax_builder = an.SynapseTypeBuilder(self.registry, "S_COMP_SOFTMAX")
        comp_softmax_builder.setInput(self.T_COMP).setOutput(self.T_SOFTMAX).addParent(self.T_STANDARD_SYNAPSE)
        self.S_COMP_SOFTMAX = comp_softmax_builder.build()
        self.L_COMP_SOFTMAX = self.S_COMP_SOFTMAX.getLinkType()
        
        # Build S_SOFTMAX_MIX (softmax to mix synapse and link)
        softmax_mix_builder = an.SynapseTypeBuilder(self.registry, "S_SOFTMAX_MIX")
        softmax_mix_builder.setInput(self.T_SOFTMAX).setOutput(self.T_MIX).addParent(self.T_STANDARD_SYNAPSE)
        self.S_SOFTMAX_MIX = softmax_mix_builder.build()
        self.L_SOFTMAX_MIX = self.S_SOFTMAX_MIX.getLinkType()
        
        # Build S_VALUE_MIX (value to mix synapse and link)
        value_mix_builder = an.SynapseTypeBuilder(self.registry, "S_VALUE_MIX")
        value_mix_builder.setInput(self.T_VALUE).setOutput(self.T_MIX).addParent(self.T_STANDARD_SYNAPSE)
        self.S_VALUE_MIX = value_mix_builder.build()
        self.L_VALUE_MIX = self.S_VALUE_MIX.getLinkType()
        
        # Set up VALUE_MIX ↔ SOFTMAX_MIX pairing
        try:
            value_mix_builder.setPairedSynapseType(self.S_SOFTMAX_MIX)
            softmax_mix_builder.setPairedSynapseType(self.S_VALUE_MIX)
            print("Set up VALUE_MIX ↔ SOFTMAX_MIX synapse pairing")
        except (AttributeError, NameError):
            print("Note: MIX synapse pairing will be handled by PAIR_IN relations")
        
        # Build S_MIX_SOFTMAX (optional mix to softmax synapse and link)
        mix_softmax_builder = an.SynapseTypeBuilder(self.registry, "S_MIX_SOFTMAX")
        mix_softmax_builder.setInput(self.T_MIX).setOutput(self.T_SOFTMAX).addParent(self.T_STANDARD_SYNAPSE)
        self.S_MIX_SOFTMAX = mix_softmax_builder.build()
        self.L_MIX_SOFTMAX = self.S_MIX_SOFTMAX.getLinkType()
        
        print("Transformer-specific types built successfully")
    
    def _setup_dot_product_fields(self):
        """Setup dot-product field definitions using mul and sum operations on paired links"""
        print("Setting up dot-product field definitions...")
        
        # ========================================
        # DOT-PRODUCT FIELD IMPLEMENTATION USING mul AND sum
        # ========================================
        
        # The dot-product calculation uses:
        # 1. mul field on link types to multiply paired link values
        # 2. sum field on activation types to aggregate all multiplications
        
        # Create multiplication field on KEY_COMP link type
        # This will multiply the KEY input value with its paired QUERY link value
        self.key_comp_mul_field = self.L_KEY_COMP.mul("pairMultiplication")
        
        # Create multiplication field on QUERY_COMP link type  
        # This will multiply the QUERY input value with its paired KEY link value
        self.query_comp_mul_field = self.L_QUERY_COMP.mul("pairMultiplication")
        
        # Create sum field on COMP activation type to aggregate all pair multiplications
        self.comp_net_field = self.T_COMP_ACT.sum("net")
        
        # Create value field as identity (value = net for dot-product neurons)
        self.comp_value_field = self.T_COMP_ACT.inputField("value")
        
        # ========================================
        # ESTABLISH FIELD CONNECTIONS FOR COMP DOT-PRODUCT
        # ========================================
        
        # Connect the sum field to aggregate multiplication results from both link types
        # The net field gets input from KEY_COMP link multiplications
        self.comp_net_field.input(self.T_COMP_ACT.INPUT, self.key_comp_mul_field, 0)
        # The net field also gets input from QUERY_COMP link multiplications  
        self.comp_net_field.input(self.T_COMP_ACT.INPUT, self.query_comp_mul_field, 1)
        
        # Connect value field to net field (identity: value = net)
        self.comp_value_field.input(self.T_COMP_ACT.SELF, self.comp_net_field, 0)
        
        print("Set up COMP dot-product field connections: mul → sum → value")
        
        # ========================================
        # SET UP MIX DOT-PRODUCT FIELDS (similar pattern)
        # ========================================
        
        # Create multiplication fields for MIX neuron (VALUE × SOFTMAX pairs)
        self.value_mix_mul_field = self.L_VALUE_MIX.mul("pairMultiplication") 
        self.softmax_mix_mul_field = self.L_SOFTMAX_MIX.mul("pairMultiplication")
        
        # Create sum and value fields for MIX activation
        self.mix_net_field = self.T_MIX_ACT.sum("net")
        self.mix_value_field = self.T_MIX_ACT.inputField("value")
        
        # Connect MIX fields
        self.mix_net_field.input(self.T_MIX_ACT.INPUT, self.value_mix_mul_field, 0)
        self.mix_net_field.input(self.T_MIX_ACT.INPUT, self.softmax_mix_mul_field, 1)
        self.mix_value_field.input(self.T_MIX_ACT.SELF, self.mix_net_field, 0)
        
        print("Set up MIX dot-product field connections: mul → sum → value")
        
        # Store field references for easy access in tests
        self.dot_fields = {
            'comp_net': self.comp_net_field,
            'comp_value': self.comp_value_field,
            'mix_net': self.mix_net_field, 
            'mix_value': self.mix_value_field,
            'key_comp_mul': self.key_comp_mul_field,
            'query_comp_mul': self.query_comp_mul_field
        }
        
        print("Dot-product field definitions and connections setup complete")
    
    def get_registry(self):
        """Return the type registry (includes both standard and transformer types)"""
        return self.registry
    
    def get_standard_network(self):
        """Return the underlying standard network foundation"""
        return self.standard_network

def create_transformer_types():
    """Factory function to create and return the transformer type registry"""
    return TransformerTypeRegistry()