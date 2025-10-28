"""
AIKA-Based Transformer Neural Network Type Definitions using Builder Pattern

This module defines the object types (neurons, synapses, activations, links) and 
field definitions for a minimal transformer-like architecture using the AIKA framework
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

class TransformerTypeRegistry:
    """
    Transformer type registry that defines all object types and their field relationships
    according to the AIKA transformer specification using builder pattern.
    """
    
    def __init__(self):
        self.registry = af.TypeRegistry()
        
        # Build types following proper builder pattern
        self._build_types()
        
        # Setup field definitions and mathematical model
        self._setup_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _build_types(self):
        """Build all types following proper builder pattern"""
        print("Building types...")
        
        # ========================================
        # BUILD NEURON AND ACTIVATION TYPES FIRST
        # ========================================
        
        # Build T_EMB (embedding neuron and activation)
        emb_builder = an.NeuronTypeBuilder(self.registry, "EMB_NEURON")
        self.T_EMB = emb_builder.build()
        self.T_EMB_ACT = self.T_EMB.getActivationType()
        
        # Build T_KEY (key neuron and activation)
        key_builder = an.NeuronTypeBuilder(self.registry, "KEY_NEURON")
        self.T_KEY = key_builder.build()
        self.T_KEY_ACT = self.T_KEY.getActivationType()
        
        # Build T_QUERY (query neuron and activation)
        query_builder = an.NeuronTypeBuilder(self.registry, "QUERY_NEURON")
        self.T_QUERY = query_builder.build()
        self.T_QUERY_ACT = self.T_QUERY.getActivationType()
        
        # Build T_DOT (abstract dot-product neuron and activation)
        dot_builder = an.NeuronTypeBuilder(self.registry, "DOT_NEURON")
        self.T_DOT = dot_builder.build()
        self.T_DOT_ACT = self.T_DOT.getActivationType()
        
        # Build T_COMP (comparison neuron and activation)
        comp_builder = an.NeuronTypeBuilder(self.registry, "COMP_NEURON")
        self.T_COMP = comp_builder.build()
        self.T_COMP_ACT = self.T_COMP.getActivationType()
        
        # Build T_MIX (mixing neuron and activation)
        mix_builder = an.NeuronTypeBuilder(self.registry, "MIX_NEURON")
        self.T_MIX = mix_builder.build()
        self.T_MIX_ACT = self.T_MIX.getActivationType()
        
        # Build T_SOFTMAX (softmax neuron and activation)
        softmax_builder = an.NeuronTypeBuilder(self.registry, "SOFTMAX_NEURON")
        self.T_SOFTMAX = softmax_builder.build()
        self.T_SOFTMAX_ACT = self.T_SOFTMAX.getActivationType()
        
        # Build T_VALUE (value neuron and activation)
        value_builder = an.NeuronTypeBuilder(self.registry, "VALUE_NEURON")
        self.T_VALUE = value_builder.build()
        self.T_VALUE_ACT = self.T_VALUE.getActivationType()
        
        # ========================================
        # BUILD SYNAPSE AND LINK TYPES SECOND
        # ========================================
        
        # Build S_EMB_KEY (embedding to key synapse and link)
        emb_key_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_KEY")
        emb_key_builder.setInput(self.T_EMB).setOutput(self.T_KEY)
        self.S_EMB_KEY = emb_key_builder.build()
        self.L_EMB_KEY = self.S_EMB_KEY.getLinkType()
        
        # Build S_EMB_QUERY (embedding to query synapse and link)
        emb_query_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_QUERY")
        emb_query_builder.setInput(self.T_EMB).setOutput(self.T_QUERY)
        self.S_EMB_QUERY = emb_query_builder.build()
        self.L_EMB_QUERY = self.S_EMB_QUERY.getLinkType()
        
        # Build S_KEY_QUERY (key to query synapse and link)
        key_query_builder = an.SynapseTypeBuilder(self.registry, "S_KEY_QUERY")
        key_query_builder.setInput(self.T_KEY).setOutput(self.T_QUERY)
        self.S_KEY_QUERY = key_query_builder.build()
        self.L_KEY_QUERY = self.S_KEY_QUERY.getLinkType()
        
        # Build S_KEY_COMP (key to comparison synapse and link)
        key_comp_builder = an.SynapseTypeBuilder(self.registry, "S_KEY_COMP")
        key_comp_builder.setInput(self.T_KEY).setOutput(self.T_COMP)
        self.S_KEY_COMP = key_comp_builder.build()
        self.L_KEY_COMP = self.S_KEY_COMP.getLinkType()
        
        # Build S_QUERY_COMP (query to comparison synapse and link)
        query_comp_builder = an.SynapseTypeBuilder(self.registry, "S_QUERY_COMP")
        query_comp_builder.setInput(self.T_QUERY).setOutput(self.T_COMP)
        self.S_QUERY_COMP = query_comp_builder.build()
        self.L_QUERY_COMP = self.S_QUERY_COMP.getLinkType()
        
        # Build S_COMP_SOFTMAX (comparison to softmax synapse and link)
        comp_softmax_builder = an.SynapseTypeBuilder(self.registry, "S_COMP_SOFTMAX")
        comp_softmax_builder.setInput(self.T_COMP).setOutput(self.T_SOFTMAX)
        self.S_COMP_SOFTMAX = comp_softmax_builder.build()
        self.L_COMP_SOFTMAX = self.S_COMP_SOFTMAX.getLinkType()
        
        # Build S_SOFTMAX_MIX (softmax to mix synapse and link)
        softmax_mix_builder = an.SynapseTypeBuilder(self.registry, "S_SOFTMAX_MIX")
        softmax_mix_builder.setInput(self.T_SOFTMAX).setOutput(self.T_MIX)
        self.S_SOFTMAX_MIX = softmax_mix_builder.build()
        self.L_SOFTMAX_MIX = self.S_SOFTMAX_MIX.getLinkType()
        
        # Build S_VALUE_MIX (value to mix synapse and link)
        value_mix_builder = an.SynapseTypeBuilder(self.registry, "S_VALUE_MIX")
        value_mix_builder.setInput(self.T_VALUE).setOutput(self.T_MIX)
        self.S_VALUE_MIX = value_mix_builder.build()
        self.L_VALUE_MIX = self.S_VALUE_MIX.getLinkType()
        
        # Build S_MIX_SOFTMAX (optional mix to softmax synapse and link)
        mix_softmax_builder = an.SynapseTypeBuilder(self.registry, "S_MIX_SOFTMAX")
        mix_softmax_builder.setInput(self.T_MIX).setOutput(self.T_SOFTMAX)
        self.S_MIX_SOFTMAX = mix_softmax_builder.build()
        self.L_MIX_SOFTMAX = self.S_MIX_SOFTMAX.getLinkType()
        
        # Build S_EMB_VALUE (embedding to value synapse and link)
        emb_value_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_VALUE")
        emb_value_builder.setInput(self.T_EMB).setOutput(self.T_VALUE)
        self.S_EMB_VALUE = emb_value_builder.build()
        self.L_EMB_VALUE = self.S_EMB_VALUE.getLinkType()
        
        print("Types built successfully")
    
    def _setup_field_definitions(self):
        """Setup field definitions using type hierarchy according to the transformer specification."""
        
        print("Starting field definitions setup...")
        
        # ========================================
        # BASE TYPE DEFINITIONS (ROOT TYPES)
        # ========================================
        
        # Build base standard types using proper builder pattern
        print("Creating standard types...")
        
        # Build T_STANDARD_NEURON and activation
        standard_neuron_builder = an.NeuronTypeBuilder(self.registry, "STANDARD_NEURON")
        self.T_STANDARD_NEURON = standard_neuron_builder.build()
        self.T_STANDARD_ACTIVATION = self.T_STANDARD_NEURON.getActivationType()
        
        # Build T_STANDARD_SYNAPSE and link
        standard_synapse_builder = an.SynapseTypeBuilder(self.registry, "STANDARD_SYNAPSE")
        self.T_STANDARD_SYNAPSE = standard_synapse_builder.build()
        self.T_STANDARD_LINK = self.T_STANDARD_SYNAPSE.getLinkType()
        
        # Note: Field definitions would be set up on the built types
        # For now, we'll use the implementation types directly
        bias_field = self.T_STANDARD_NEURON.inputField("bias")
        
        # Standard activation fields:
        net_field = self.T_STANDARD_ACTIVATION.sum("net")
        tanh_func = af.TanhActivationFunction()
        value_field = self.T_STANDARD_ACTIVATION.fieldActivationFunc("value", tanh_func, 0.001)
        fired_field = self.T_STANDARD_ACTIVATION.inputField("fired")

        # Standard synapse weight field
        weight_field = self.T_STANDARD_SYNAPSE.inputField("weight")

        # Standard link weighted input
        weighted_input = self.T_STANDARD_LINK.mul("weightedInput")
        
        # ========================================
        # SETUP TYPE HIERARCHY
        # ========================================
        
        print("Setting up type hierarchy...")
        
        # Standard neurons inherit from base standard neuron
        print("Setting neuron inheritance...")
        self.T_EMB.addParent(self.T_STANDARD_NEURON)
        self.T_KEY.addParent(self.T_STANDARD_NEURON)  
        self.T_QUERY.addParent(self.T_STANDARD_NEURON)
        self.T_VALUE.addParent(self.T_STANDARD_NEURON)
        
        # DOT family inheritance: COMP and MIX inherit from DOT
        self.T_DOT.addParent(self.T_STANDARD_NEURON)
        self.T_COMP.addParent(self.T_DOT)
        self.T_MIX.addParent(self.T_DOT)
        
        # SOFTMAX inherits from standard neuron
        self.T_SOFTMAX.addParent(self.T_STANDARD_NEURON)
        
        # Standard activations inherit from base standard activation
        print("Setting activation inheritance...")
        self.T_EMB_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_KEY_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_QUERY_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_VALUE_ACT.addParent(self.T_STANDARD_ACTIVATION)
        
        # DOT family activation inheritance
        self.T_DOT_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_COMP_ACT.addParent(self.T_DOT_ACT)
        self.T_MIX_ACT.addParent(self.T_DOT_ACT)
        
        # SOFTMAX activation inherits from standard
        self.T_SOFTMAX_ACT.addParent(self.T_STANDARD_ACTIVATION)
        
        # All synapses inherit from standard synapse
        print("Setting synapse inheritance...")
        self.S_EMB_KEY.addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_QUERY.addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_VALUE.addParent(self.T_STANDARD_SYNAPSE)
        self.S_KEY_QUERY.addParent(self.T_STANDARD_SYNAPSE)
        self.S_KEY_COMP.addParent(self.T_STANDARD_SYNAPSE)
        self.S_QUERY_COMP.addParent(self.T_STANDARD_SYNAPSE)
        self.S_COMP_SOFTMAX.addParent(self.T_STANDARD_SYNAPSE)
        self.S_SOFTMAX_MIX.addParent(self.T_STANDARD_SYNAPSE)
        self.S_VALUE_MIX.addParent(self.T_STANDARD_SYNAPSE)
        self.S_MIX_SOFTMAX.addParent(self.T_STANDARD_SYNAPSE)
        
        # All links inherit from standard link
        print("Setting link inheritance...")
        self.L_EMB_KEY.addParent(self.T_STANDARD_LINK)
        self.L_EMB_QUERY.addParent(self.T_STANDARD_LINK)
        self.L_EMB_VALUE.addParent(self.T_STANDARD_LINK)
        self.L_KEY_QUERY.addParent(self.T_STANDARD_LINK)
        self.L_KEY_COMP.addParent(self.T_STANDARD_LINK)
        self.L_QUERY_COMP.addParent(self.T_STANDARD_LINK)
        self.L_COMP_SOFTMAX.addParent(self.T_STANDARD_LINK)
        self.L_SOFTMAX_MIX.addParent(self.T_STANDARD_LINK)
        self.L_VALUE_MIX.addParent(self.T_STANDARD_LINK)
        # L_MIX_SOFTMAX is optional for re-normalization
        
        print("Field definitions setup complete")
        print("Type hierarchy setup complete")
    
    def get_registry(self):
        """Return the type registry"""
        return self.registry

def create_transformer_types():
    """Factory function to create and return the transformer type registry"""
    return TransformerTypeRegistry()