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
import aika.fields as af
import aika.network as an

class TransformerTypeRegistry:
    """
    Transformer type registry that defines all object types and their field relationships
    according to the AIKA transformer specification.
    """
    
    def __init__(self):
        self.registry = af.TypeRegistry()
        
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
        self.T_EMB = an.NeuronType(self.registry, "EMB_NEURON")
        self.T_KEY = an.NeuronType(self.registry, "KEY_NEURON")
        self.T_QUERY = an.NeuronType(self.registry, "QUERY_NEURON")
        self.T_INHIB = an.NeuronType(self.registry, "INHIB_NEURON")
        self.T_VALUE = an.NeuronType(self.registry, "VALUE_NEURON")
    
    def _setup_activation_types(self):
        """Setup activation type definitions: T_A"""
        self.T_EMB_ACT = an.ActivationType(self.registry, "EMB_ACTIVATION")
        self.T_KEY_ACT = an.ActivationType(self.registry, "KEY_ACTIVATION")
        self.T_QUERY_ACT = an.ActivationType(self.registry, "QUERY_ACTIVATION")
        self.T_INHIB_ACT = an.ActivationType(self.registry, "INHIB_ACTIVATION")
        self.T_VALUE_ACT = an.ActivationType(self.registry, "VALUE_ACTIVATION")
        
        # Link neuron types to their activation types
        self.T_EMB.setActivation(self.T_EMB_ACT)
        self.T_KEY.setActivation(self.T_KEY_ACT)
        self.T_QUERY.setActivation(self.T_QUERY_ACT)
        self.T_INHIB.setActivation(self.T_INHIB_ACT)
        self.T_VALUE.setActivation(self.T_VALUE_ACT)
    
    def _setup_synapse_types(self):
        """Setup synapse type definitions: T_S"""
        # Synapse types according to transformer specification
        self.S_EMB_KEY = an.SynapseType(self.registry, "S_EMB_KEY")
        self.S_EMB_QUERY = an.SynapseType(self.registry, "S_EMB_QUERY")
        self.S_KEY_QUERY = an.SynapseType(self.registry, "S_KEY_QUERY")
        self.S_QUERY_INHIB = an.SynapseType(self.registry, "S_QUERY_INHIB")
        self.S_INHIB_VALUE = an.SynapseType(self.registry, "S_INHIB_VALUE")
        self.S_EMB_VALUE = an.SynapseType(self.registry, "S_EMB_VALUE")
        
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
        self.L_EMB_KEY = an.LinkType(self.registry, "L_EMB_KEY")
        self.L_EMB_QUERY = an.LinkType(self.registry, "L_EMB_QUERY")
        self.L_KEY_QUERY = an.LinkType(self.registry, "L_KEY_QUERY")
        self.L_QUERY_INHIB = an.LinkType(self.registry, "L_QUERY_INHIB")
        self.L_INHIB_VALUE = an.LinkType(self.registry, "L_INHIB_VALUE")
        self.L_EMB_VALUE = an.LinkType(self.registry, "L_EMB_VALUE")
        
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
        
        print("Starting field definitions setup...")
        
        # ========================================
        # BASE TYPE DEFINITIONS (ROOT TYPES)
        # ========================================
        
        # Base Standard Neuron Type - root for normal neurons
        print("Creating T_STANDARD_NEURON...")
        self.T_STANDARD_NEURON = an.NeuronType(self.registry, "STANDARD_NEURON")
        bias_field = self.T_STANDARD_NEURON.inputField("bias")
        
        # Base Standard Activation Type - root for normal activations
        print("Creating T_STANDARD_ACTIVATION...")
        self.T_STANDARD_ACTIVATION = an.ActivationType(self.registry, "STANDARD_ACTIVATION")
        # Standard activation fields:
        net_field = self.T_STANDARD_ACTIVATION.sum("net")
        tanh_func = af.TanhActivationFunction()
        value_field = self.T_STANDARD_ACTIVATION.fieldActivationFunc("value", tanh_func, 0.001)
        fired_field = self.T_STANDARD_ACTIVATION.inputField("fired")

        # Base Standard Synapse Type - root for all synapses
        print("Creating T_STANDARD_SYNAPSE...")
        self.T_STANDARD_SYNAPSE = an.SynapseType(self.registry, "STANDARD_SYNAPSE")
        weight_field = self.T_STANDARD_SYNAPSE.inputField("weight")

        # Base Standard Link Type - root for normal links
        print("Creating T_STANDARD_LINK...")
        self.T_STANDARD_LINK = an.LinkType(self.registry, "STANDARD_LINK")
        # Standard weightedInput: f(l) = f_weight^SYNAPSE(l) · f_val^INPUT(l)
        weighted_input = self.T_STANDARD_LINK.mul("weightedInput")

        weighted_input.input(an.LinkType.SYNAPSE, self.T_STANDARD_SYNAPSE.inputField("weight"), 0)
        weighted_input.input(an.LinkType.INPUT, self.T_STANDARD_ACTIVATION.add("value"), 1)

        # ========================================
        # INHIBITORY TYPES (STANDALONE)
        # ========================================
        
        # Standalone inhibitory neuron with bias field
        print("Creating standalone T_INHIB neuron type...")
        
        # Standalone inhibitory activation with softmax fields
        print("Creating standalone T_INHIB_ACT activation type...")
        
        # Special field for softmax normalization
        softmax_denominator = self.T_INHIB_ACT.sum("softmax_denominator")
        
        # Standalone inhibitory link with softmax computation
        print("Creating standalone L_INHIB_VALUE link type...")
        
        # Numerator: exp(f_val^INPUT(PAIR_IN(l)))
        numerator = self.L_INHIB_VALUE.exp("softmax_numerator")
        numerator.input(an.LinkType.PAIR_IN, self.T_INHIB_ACT.add("value"), 0)
        
        # Softmax ratio: numerator / denominator
        softmax_ratio = self.L_INHIB_VALUE.div("softmax_ratio")
        softmax_ratio.input(an.LinkType.SELF, numerator, 0)
        softmax_ratio.input(an.LinkType.OUTPUT, softmax_denominator, 1)
        
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
        # Special case: inhibitory output synapse
        self.S_INHIB_VALUE.addParent(self.T_STANDARD_SYNAPSE)
        
        # Standard links inherit from standard link
        print("Setting link inheritance...")
        self.L_EMB_KEY.addParent(self.T_STANDARD_LINK)
        self.L_EMB_QUERY.addParent(self.T_STANDARD_LINK)
        self.L_KEY_QUERY.addParent(self.T_STANDARD_LINK)
        self.L_QUERY_INHIB.addParent(self.T_STANDARD_LINK)
        self.L_EMB_VALUE.addParent(self.T_STANDARD_LINK)
        
        # L_INHIB_VALUE is standalone (no inheritance)
        
        print("Type hierarchy setup completed.")
    
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
        print(f"📊 Registry contains all transformer types:")
        print(f"   • Neuron types: EMB, KEY, QUERY, INHIB, VALUE")
        print(f"   • Activation types: EMB_ACT, KEY_ACT, QUERY_ACT, INHIB_ACT, VALUE_ACT") 
        print(f"   • Synapse types: S_EMB_KEY, S_EMB_QUERY, S_KEY_QUERY, S_QUERY_INHIB, S_INHIB_VALUE, S_EMB_VALUE")
        print(f"   • Link types: L_EMB_KEY, L_EMB_QUERY, L_KEY_QUERY, L_QUERY_INHIB, L_INHIB_VALUE, L_EMB_VALUE")
        
        # Display the hierarchical structure
        print("\n🏗️ Type Hierarchy:")
        print(f"   • Base standard types: STANDARD_NEURON, STANDARD_ACTIVATION, STANDARD_SYNAPSE, STANDARD_LINK")
        print(f"   • Standalone inhibitory types: T_INHIB, T_INHIB_ACT, L_INHIB_VALUE")
        
        print("\n🔬 Transformer Type Inheritance:")
        print(f"   • EMB, KEY, QUERY, VALUE inherit from STANDARD_NEURON")
        print(f"   • INHIB is standalone (no inheritance)")
        print(f"   • EMB_ACT, KEY_ACT, QUERY_ACT, VALUE_ACT inherit from STANDARD_ACTIVATION")
        print(f"   • INHIB_ACT is standalone (no inheritance)")
        
        print("\n🔗 Synapse & Link Inheritance:")
        print(f"   • All S_* types inherit from STANDARD_SYNAPSE")
        print(f"   • Most L_* types inherit from STANDARD_LINK")
        print(f"   • L_INHIB_VALUE is standalone (no inheritance)")
        
        print("\n🧮 Mathematical Model Features:")
        print("   • Simplified type hierarchy with base standard types")
        print("   • Standard weightedInput: weight × input_value (inherited)")
        print("   • Standalone inhibitory types with softmax computation")
        print("   • Softmax: exp(input) / Σ(exp(all_inputs)) × weight")
        print("   • Binding signal propagation for token identity")
        print("   • Field-based computation graph with inheritance")
        
        print("\n🎯 Implementation follows simplified transformer specification:")
        print("   • Base types: STANDARD_NEURON/ACTIVATION → inherited by EMB, KEY, QUERY, VALUE")
        print("   • Standalone types: T_INHIB, T_INHIB_ACT, L_INHIB_VALUE with softmax fields")
        print("   • Attention mechanism: KEY→QUERY→INHIB→VALUE")
        print("   • Softmax normalization using PAIR_IN/PAIR_OUT relations")
        print("   • Simplified hierarchy removes intermediate INHIBITORY types")
        
        return transformer_types
        
    except Exception as e:
        print(f"❌ Error creating transformer types: {e}")
        import traceback
        traceback.print_exc()
        return None


if __name__ == "__main__":
    main()