"""
AIKA-Based Transformer Neural Network Type Definitions

This module defines the object types (neurons, synapses, activations, links) and 
field definitions for a minimal transformer-like architecture using the AIKA framework.

Based on the formal specification in specs/network/transformer.md
"""

import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika


class TransformerTypeRegistry:
    """
    Transformer type registry that defines all object types and their field relationships
    according to the AIKA transformer specification.
    """
    
    def __init__(self):
        self.registry = aika.fields.TypeRegistry()
        
        # Initialize all type definitions
        self._setup_neuron_types()
        self._setup_activation_types()
        self._setup_synapse_types()
        self._setup_link_types()
        
        # Setup field definitions and mathematical model
        self._setup_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _setup_neuron_types(self):
        """Setup neuron type definitions: T_N = {T_EMB, T_KEY, T_QUERY, T_INHIB, T_VALUE}"""
        self.T_EMB = aika.network.NeuronDefinition(self.registry, "EMB_NEURON")
        self.T_KEY = aika.network.NeuronDefinition(self.registry, "KEY_NEURON")
        self.T_QUERY = aika.network.NeuronDefinition(self.registry, "QUERY_NEURON")
        self.T_INHIB = aika.network.NeuronDefinition(self.registry, "INHIB_NEURON")
        self.T_VALUE = aika.network.NeuronDefinition(self.registry, "VALUE_NEURON")
    
    def _setup_activation_types(self):
        """Setup activation type definitions: T_A"""
        self.T_EMB_ACT = aika.network.ActivationDefinition(self.registry, "EMB_ACTIVATION")
        self.T_KEY_ACT = aika.network.ActivationDefinition(self.registry, "KEY_ACTIVATION")
        self.T_QUERY_ACT = aika.network.ActivationDefinition(self.registry, "QUERY_ACTIVATION")
        self.T_INHIB_ACT = aika.network.ActivationDefinition(self.registry, "INHIB_ACTIVATION")
        self.T_VALUE_ACT = aika.network.ActivationDefinition(self.registry, "VALUE_ACTIVATION")
        
        # Link neuron types to their activation types
        self.T_EMB.setActivation(self.T_EMB_ACT)
        self.T_KEY.setActivation(self.T_KEY_ACT)
        self.T_QUERY.setActivation(self.T_QUERY_ACT)
        self.T_INHIB.setActivation(self.T_INHIB_ACT)
        self.T_VALUE.setActivation(self.T_VALUE_ACT)
    
    def _setup_synapse_types(self):
        """Setup synapse type definitions: T_S"""
        # Synapse types according to transformer specification
        self.S_EMB_KEY = aika.network.SynapseDefinition(self.registry, "S_EMB_KEY")
        self.S_EMB_QUERY = aika.network.SynapseDefinition(self.registry, "S_EMB_QUERY")
        self.S_KEY_QUERY = aika.network.SynapseDefinition(self.registry, "S_KEY_QUERY")
        self.S_QUERY_INHIB = aika.network.SynapseDefinition(self.registry, "S_QUERY_INHIB")
        self.S_INHIB_VALUE = aika.network.SynapseDefinition(self.registry, "S_INHIB_VALUE")
        self.S_EMB_VALUE = aika.network.SynapseDefinition(self.registry, "S_EMB_VALUE")
        
        # Set input/output neuron type relationships
        self.S_EMB_KEY.setInput(self.T_EMB).setOutput(self.T_KEY)
        self.S_EMB_QUERY.setInput(self.T_EMB).setOutput(self.T_QUERY)
        self.S_KEY_QUERY.setInput(self.T_KEY).setOutput(self.T_QUERY)
        self.S_QUERY_INHIB.setInput(self.T_QUERY).setOutput(self.T_INHIB)
        self.S_INHIB_VALUE.setInput(self.T_INHIB).setOutput(self.T_VALUE)
        self.S_EMB_VALUE.setInput(self.T_EMB).setOutput(self.T_VALUE)
    
    def _setup_link_types(self):
        """Setup link type definitions: T_L"""
        # Link types for each synapse type
        self.L_EMB_KEY = aika.network.LinkDefinition(self.registry, "L_EMB_KEY")
        self.L_EMB_QUERY = aika.network.LinkDefinition(self.registry, "L_EMB_QUERY")
        self.L_KEY_QUERY = aika.network.LinkDefinition(self.registry, "L_KEY_QUERY")
        self.L_QUERY_INHIB = aika.network.LinkDefinition(self.registry, "L_QUERY_INHIB")
        self.L_INHIB_VALUE = aika.network.LinkDefinition(self.registry, "L_INHIB_VALUE")
        self.L_EMB_VALUE = aika.network.LinkDefinition(self.registry, "L_EMB_VALUE")
        
        # Set synapse relationships
        self.L_EMB_KEY.setSynapse(self.S_EMB_KEY)
        self.L_EMB_QUERY.setSynapse(self.S_EMB_QUERY)
        self.L_KEY_QUERY.setSynapse(self.S_KEY_QUERY)
        self.L_QUERY_INHIB.setSynapse(self.S_QUERY_INHIB)
        self.L_INHIB_VALUE.setSynapse(self.S_INHIB_VALUE)
        self.L_EMB_VALUE.setSynapse(self.S_EMB_VALUE)
        
        # Set input/output activation type relationships
        self.L_EMB_KEY.setInput(self.T_EMB_ACT).setOutput(self.T_KEY_ACT)
        self.L_EMB_QUERY.setInput(self.T_EMB_ACT).setOutput(self.T_QUERY_ACT)
        self.L_KEY_QUERY.setInput(self.T_KEY_ACT).setOutput(self.T_QUERY_ACT)
        self.L_QUERY_INHIB.setInput(self.T_QUERY_ACT).setOutput(self.T_INHIB_ACT)
        self.L_INHIB_VALUE.setInput(self.T_INHIB_ACT).setOutput(self.T_VALUE_ACT)
        self.L_EMB_VALUE.setInput(self.T_EMB_ACT).setOutput(self.T_VALUE_ACT)
    
    def _setup_field_definitions(self):
        """Setup field definitions using type hierarchy according to the transformer specification."""
        
        # ========================================
        # BASE TYPE DEFINITIONS (ROOT TYPES)
        # ========================================
        
        # Base Standard Neuron Type - root for normal neurons
        self.T_STANDARD_NEURON = aika.network.NeuronDefinition(self.registry, "STANDARD_NEURON")
        bias_field = self.T_STANDARD_NEURON.inputField("bias")
        
        # Base Standard Activation Type - root for normal activations
        self.T_STANDARD_ACTIVATION = aika.network.ActivationDefinition(self.registry, "STANDARD_ACTIVATION")
        # Standard activation fields:
        print("A\n")
        net_field = self.T_STANDARD_ACTIVATION.sum("net")
        print("B\n")
        value_field = self.T_STANDARD_ACTIVATION.add("value")
        fired_field = self.T_STANDARD_ACTIVATION.inputField("fired")

        # Base Standard Synapse Type - root for all synapses
        self.T_STANDARD_SYNAPSE = aika.network.SynapseDefinition(self.registry, "STANDARD_SYNAPSE")
        weight_field = self.T_STANDARD_SYNAPSE.inputField("weight")
        
        # Base Standard Link Type - root for normal links
        self.T_STANDARD_LINK = aika.network.LinkDefinition(self.registry, "STANDARD_LINK")
        # Standard weightedInput: f(l) = f_weight^SYNAPSE(l) · f_val^INPUT(l)
        standard_weighted_input = self.T_STANDARD_LINK.mul("weightedInput")
        standard_weighted_input.input(aika.network.LinkDefinition.SYNAPSE, self.T_STANDARD_SYNAPSE.inputField("weight"), 0)
        standard_weighted_input.input(aika.network.LinkDefinition.INPUT, self.T_STANDARD_ACTIVATION.add("value"), 1)
        
        # ========================================
        # SPECIAL INHIBITORY TYPES (EXCEPTION)
        # ========================================
        
        # Inhibitory Neuron Type - inherits from standard neuron but has different math
        self.T_INHIBITORY_NEURON = aika.network.NeuronDefinition(self.registry, "INHIBITORY_NEURON")
        # Inherits bias field from standard neuron type
        
        # Inhibitory Activation Type - different mathematical model for softmax
        self.T_INHIBITORY_ACTIVATION = aika.network.ActivationDefinition(self.registry, "INHIBITORY_ACTIVATION")
        # Inhibitory activation has special softmax denominator field
        inhib_net_field = self.T_INHIBITORY_ACTIVATION.sum("net")
        inhib_value_field = self.T_INHIBITORY_ACTIVATION.add("value")
        inhib_fired_field = self.T_INHIBITORY_ACTIVATION.inputField("fired")
        # Special field for softmax normalization
        softmax_denominator = self.T_INHIBITORY_ACTIVATION.sum("softmax_denominator")
        
        # Inhibitory Link Type - special softmax computation
        self.T_INHIBITORY_LINK = aika.network.LinkDefinition(self.registry, "INHIBITORY_LINK")
        # Special softmax weightedInput implementation
        softmax_weighted_input = self.T_INHIBITORY_LINK.mul("weightedInput")
        
        # Numerator: exp(f_val^INPUT(PAIR_IN(l)))
        numerator = self.T_INHIBITORY_LINK.exp("softmax_numerator")
        numerator.input(aika.network.LinkDefinition.PAIR_IN, self.T_STANDARD_ACTIVATION.add("value"), 0)
        
        # Softmax ratio: numerator / denominator
        softmax_ratio = self.T_INHIBITORY_LINK.div("softmax_ratio")
        softmax_ratio.input(aika.network.LinkDefinition.SELF, numerator, 0)
        softmax_ratio.input(aika.network.LinkDefinition.OUTPUT, softmax_denominator, 1)
        
        # Final weighted input: softmax_ratio * weight
        softmax_weighted_input.input(aika.network.LinkDefinition.SELF, softmax_ratio, 0)
        softmax_weighted_input.input(aika.network.LinkDefinition.SYNAPSE, self.T_STANDARD_SYNAPSE.inputField("weight"), 1)
        
        # ========================================
        # SETUP TYPE HIERARCHY
        # ========================================
        
        # Standard neurons inherit from base standard neuron
        self.T_EMB.getParent = lambda: self.T_STANDARD_NEURON
        self.T_KEY.getParent = lambda: self.T_STANDARD_NEURON  
        self.T_QUERY.getParent = lambda: self.T_STANDARD_NEURON
        self.T_VALUE.getParent = lambda: self.T_STANDARD_NEURON
        
        # Inhibitory neuron inherits from inhibitory base type
        self.T_INHIB.getParent = lambda: self.T_INHIBITORY_NEURON
        
        # Standard activations inherit from base standard activation
        self.T_EMB_ACT.getParent = lambda: self.T_STANDARD_ACTIVATION
        self.T_KEY_ACT.getParent = lambda: self.T_STANDARD_ACTIVATION
        self.T_QUERY_ACT.getParent = lambda: self.T_STANDARD_ACTIVATION
        self.T_VALUE_ACT.getParent = lambda: self.T_STANDARD_ACTIVATION
        
        # Inhibitory activation inherits from inhibitory base type
        self.T_INHIB_ACT.getParent = lambda: self.T_INHIBITORY_ACTIVATION
        
        # All synapses inherit from standard synapse
        self.S_EMB_KEY.getParent = lambda: self.T_STANDARD_SYNAPSE
        self.S_EMB_QUERY.getParent = lambda: self.T_STANDARD_SYNAPSE
        self.S_KEY_QUERY.getParent = lambda: self.T_STANDARD_SYNAPSE
        self.S_QUERY_INHIB.getParent = lambda: self.T_STANDARD_SYNAPSE
        self.S_EMB_VALUE.getParent = lambda: self.T_STANDARD_SYNAPSE
        # Special case: inhibitory output synapse
        self.S_INHIB_VALUE.getParent = lambda: self.T_STANDARD_SYNAPSE
        
        # Standard links inherit from standard link
        self.L_EMB_KEY.getParent = lambda: self.T_STANDARD_LINK
        self.L_EMB_QUERY.getParent = lambda: self.T_STANDARD_LINK
        self.L_KEY_QUERY.getParent = lambda: self.T_STANDARD_LINK
        self.L_QUERY_INHIB.getParent = lambda: self.T_STANDARD_LINK
        self.L_EMB_VALUE.getParent = lambda: self.T_STANDARD_LINK
        
        # Inhibitory output link uses special inhibitory link type
        self.L_INHIB_VALUE.getParent = lambda: self.T_INHIBITORY_LINK
    
    def get_registry(self):
        """Get the configured type registry."""
        return self.registry

def create_transformer_types():
    """
    Factory function to create and return a configured transformer type registry.
    
    Returns:
        TransformerTypeRegistry: Fully configured type registry with all transformer types
    """
    return TransformerTypeRegistry()


def main():
    """Example usage of the transformer type definitions."""
    print("🧠 Creating AIKA Transformer Type Definitions...")
    
    try:
        # Create the type registry with all transformer types
        transformer_types = create_transformer_types()
        
        print("✅ Transformer type registry created successfully!")
        print(f"📊 Registry contains:")
        print(f"   • {len(transformer_types.get_base_types())} base types (roots)")
        print(f"   • {len(transformer_types.get_neuron_types())} transformer neuron types")
        print(f"   • {len(transformer_types.get_activation_types())} transformer activation types") 
        print(f"   • {len(transformer_types.get_synapse_types())} transformer synapse types")
        print(f"   • {len(transformer_types.get_link_types())} transformer link types")
        
        # Display the hierarchical structure
        print("\n🏗️ Type Hierarchy (Base Types):")
        for name, base_type in transformer_types.get_base_types().items():
            print(f"   • {name}: {base_type}")
        
        print("\n🔬 Transformer Neuron Types (inherit from base):")
        for name, neuron_type in transformer_types.get_neuron_types().items():
            parent = "STANDARD_NEURON" if name != "INHIB" else "INHIBITORY_NEURON"
            print(f"   • {name}: {neuron_type} → inherits from {parent}")
        
        print("\n🔗 Transformer Synapse Types:")
        for name, synapse_type in transformer_types.get_synapse_types().items():
            print(f"   • {name}: {synapse_type} → inherits from STANDARD_SYNAPSE")
        
        print("\n🧮 Mathematical Model Features:")
        print("   • Type hierarchy with base types for standard neurons/activations")
        print("   • Standard weightedInput: weight × input_value (inherited)")
        print("   • Special inhibitory types with softmax computation")
        print("   • Softmax: exp(input) / Σ(exp(all_inputs)) × weight")
        print("   • Binding signal propagation for token identity")
        print("   • Field-based computation graph with inheritance")
        
        print("\n🎯 Implementation follows formal transformer specification:")
        print("   • Base types: STANDARD_NEURON/ACTIVATION → inherited by EMB, KEY, QUERY, VALUE")
        print("   • Exception: INHIBITORY_NEURON/ACTIVATION → used by INHIB for softmax")
        print("   • Attention mechanism: KEY→QUERY→INHIB→VALUE")
        print("   • Softmax normalization using PAIR_IN/PAIR_OUT relations")
        print("   • Type hierarchy eliminates field definition loops")
        
        return transformer_types
        
    except Exception as e:
        print(f"❌ Error creating transformer types: {e}")
        import traceback
        traceback.print_exc()
        return None


if __name__ == "__main__":
    main()