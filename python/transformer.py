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
        
        # Initialize all builder types
        self._setup_neuron_builders()
        self._setup_activation_builders()
        self._setup_synapse_builders()
        self._setup_link_builders()
        
        # Build the actual implementation types
        self._build_implementation_types()
        
        # Setup field definitions and mathematical model
        self._setup_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _setup_neuron_builders(self):
        """Setup neuron builder definitions: T_N = {T_EMB, T_KEY, T_QUERY, T_INHIB, T_VALUE}"""
        self.T_EMB_BUILDER = an.NeuronTypeBuilder(self.registry, "EMB_NEURON")
        self.T_KEY_BUILDER = an.NeuronTypeBuilder(self.registry, "KEY_NEURON")
        self.T_QUERY_BUILDER = an.NeuronTypeBuilder(self.registry, "QUERY_NEURON")
        self.T_INHIB_BUILDER = an.NeuronTypeBuilder(self.registry, "INHIB_NEURON")
        self.T_VALUE_BUILDER = an.NeuronTypeBuilder(self.registry, "VALUE_NEURON")
    
    def _setup_activation_builders(self):
        """Setup activation builder definitions: T_A"""
        # Activation types are now handled by NeuronTypeBuilder internally
        # No separate ActivationTypeBuilder needed - removed obsolete builders
        pass
    
    def _setup_synapse_builders(self):
        """Setup synapse builder definitions: T_S"""
        # Synapse builders according to transformer specification
        self.S_EMB_KEY_BUILDER = an.SynapseTypeBuilder(self.registry, "S_EMB_KEY")
        self.S_EMB_QUERY_BUILDER = an.SynapseTypeBuilder(self.registry, "S_EMB_QUERY")
        self.S_KEY_QUERY_BUILDER = an.SynapseTypeBuilder(self.registry, "S_KEY_QUERY")
        self.S_QUERY_INHIB_BUILDER = an.SynapseTypeBuilder(self.registry, "S_QUERY_INHIB")
        self.S_INHIB_VALUE_BUILDER = an.SynapseTypeBuilder(self.registry, "S_INHIB_VALUE")
        self.S_EMB_VALUE_BUILDER = an.SynapseTypeBuilder(self.registry, "S_EMB_VALUE")
        
        # Set input/output neuron type relationships
        self.S_EMB_KEY_BUILDER.setInput(self.T_EMB_BUILDER).setOutput(self.T_KEY_BUILDER)
        self.S_EMB_QUERY_BUILDER.setInput(self.T_EMB_BUILDER).setOutput(self.T_QUERY_BUILDER)
        self.S_KEY_QUERY_BUILDER.setInput(self.T_KEY_BUILDER).setOutput(self.T_QUERY_BUILDER)
        self.S_QUERY_INHIB_BUILDER.setInput(self.T_QUERY_BUILDER).setOutput(self.T_INHIB_BUILDER)
        self.S_INHIB_VALUE_BUILDER.setInput(self.T_INHIB_BUILDER).setOutput(self.T_VALUE_BUILDER)
        self.S_EMB_VALUE_BUILDER.setInput(self.T_EMB_BUILDER).setOutput(self.T_VALUE_BUILDER)
    
    def _setup_link_builders(self):
        """Setup link builder definitions: T_L"""
        # Link types are now handled by SynapseTypeBuilder internally
        # No separate LinkTypeBuilder needed - removed obsolete builders
        pass
    
    def _build_implementation_types(self):
        """Build the actual implementation types from builders"""
        print("Building implementation types from builders...")
        
        # Build neuron types
        self.T_EMB = self.T_EMB_BUILDER.build()
        self.T_KEY = self.T_KEY_BUILDER.build()
        self.T_QUERY = self.T_QUERY_BUILDER.build()
        self.T_INHIB = self.T_INHIB_BUILDER.build()
        self.T_VALUE = self.T_VALUE_BUILDER.build()
        
        # Activation types are built automatically by NeuronTypeBuilder
        self.T_EMB_ACT = self.T_EMB.getActivationType()
        self.T_KEY_ACT = self.T_KEY.getActivationType()
        self.T_QUERY_ACT = self.T_QUERY.getActivationType()
        self.T_INHIB_ACT = self.T_INHIB.getActivationType()
        self.T_VALUE_ACT = self.T_VALUE.getActivationType()
        
        # Build synapse types
        self.S_EMB_KEY = self.S_EMB_KEY_BUILDER.build()
        self.S_EMB_QUERY = self.S_EMB_QUERY_BUILDER.build()
        self.S_KEY_QUERY = self.S_KEY_QUERY_BUILDER.build()
        self.S_QUERY_INHIB = self.S_QUERY_INHIB_BUILDER.build()
        self.S_INHIB_VALUE = self.S_INHIB_VALUE_BUILDER.build()
        self.S_EMB_VALUE = self.S_EMB_VALUE_BUILDER.build()
        
        # Link types are built automatically by SynapseTypeBuilder
        self.L_EMB_KEY = self.S_EMB_KEY.getLinkType()
        self.L_EMB_QUERY = self.S_EMB_QUERY.getLinkType()
        self.L_KEY_QUERY = self.S_KEY_QUERY.getLinkType()
        self.L_QUERY_INHIB = self.S_QUERY_INHIB.getLinkType()
        self.L_INHIB_VALUE = self.S_INHIB_VALUE.getLinkType()
        self.L_EMB_VALUE = self.S_EMB_VALUE.getLinkType()
        
        print("Implementation types built successfully")
    
    def _setup_field_definitions(self):
        """Setup field definitions using type hierarchy according to the transformer specification."""
        
        print("Starting field definitions setup...")
        
        # ========================================
        # BASE TYPE DEFINITIONS (ROOT TYPES)
        # ========================================
        
        # Create builders for base types
        print("Creating T_STANDARD_NEURON builder...")
        self.T_STANDARD_NEURON_BUILDER = an.NeuronTypeBuilder(self.registry, "STANDARD_NEURON")
        self.T_STANDARD_SYNAPSE_BUILDER = an.SynapseTypeBuilder(self.registry, "STANDARD_SYNAPSE")

        # Build base implementation types
        self.T_STANDARD_NEURON = self.T_STANDARD_NEURON_BUILDER.build()
        self.T_STANDARD_SYNAPSE = self.T_STANDARD_SYNAPSE_BUILDER.build()
        
        # Get derived types from builders
        self.T_STANDARD_ACTIVATION = self.T_STANDARD_NEURON.getActivationType()
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
        
        # Standard activations inherit from base standard activation
        print("Setting activation inheritance...")
        self.T_EMB_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_KEY_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_QUERY_ACT.addParent(self.T_STANDARD_ACTIVATION)
        self.T_VALUE_ACT.addParent(self.T_STANDARD_ACTIVATION)
        
        # T_INHIB_ACT is standalone (no inheritance)
        
        # All synapses inherit from standard synapse
        print("Setting synapse inheritance...")
        self.S_EMB_KEY.addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_QUERY.addParent(self.T_STANDARD_SYNAPSE)
        self.S_KEY_QUERY.addParent(self.T_STANDARD_SYNAPSE)
        self.S_QUERY_INHIB.addParent(self.T_STANDARD_SYNAPSE)
        self.S_EMB_VALUE.addParent(self.T_STANDARD_SYNAPSE)
        self.S_INHIB_VALUE.addParent(self.T_STANDARD_SYNAPSE)
        
        # All links inherit from standard link
        print("Setting link inheritance...")
        self.L_EMB_KEY.addParent(self.T_STANDARD_LINK)
        self.L_EMB_QUERY.addParent(self.T_STANDARD_LINK)
        self.L_KEY_QUERY.addParent(self.T_STANDARD_LINK)
        self.L_QUERY_INHIB.addParent(self.T_STANDARD_LINK)
        self.L_EMB_VALUE.addParent(self.T_STANDARD_LINK)
        # L_INHIB_VALUE is standalone (has special softmax computation)
        
        print("Field definitions setup complete")
        print("Type hierarchy setup complete")
    
    def get_registry(self):
        """Return the type registry"""
        return self.registry

def create_transformer_types():
    """Factory function to create and return the transformer type registry"""
    return TransformerTypeRegistry()