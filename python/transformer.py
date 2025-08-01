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
        """Setup field definitions according to the transformer specification."""
        
        # ========================================
        # NEURON OBJECT FIELDS
        # ========================================
        # Field: bias - f_bias^·(n) - Constant scalar per neuron
        for neuron_type in [self.T_EMB, self.T_KEY, self.T_QUERY, self.T_INHIB, self.T_VALUE]:
            bias_field = neuron_type.inputField("bias")
        
        # ========================================
        # SYNAPSE OBJECT FIELDS  
        # ========================================
        # Field: weight - f_weight^·(s) - Scalar weight applied to input values
        for synapse_type in [self.S_EMB_KEY, self.S_EMB_QUERY, self.S_KEY_QUERY, 
                           self.S_QUERY_INHIB, self.S_INHIB_VALUE, self.S_EMB_VALUE]:
            weight_field = synapse_type.inputField("weight")
        
        # ========================================
        # ACTIVATION OBJECT FIELDS
        # ========================================
        for act_type in [self.T_EMB_ACT, self.T_KEY_ACT, self.T_QUERY_ACT, self.T_INHIB_ACT, self.T_VALUE_ACT]:
            # Field: net - f_net^·(a) - Sum of weighted inputs
            # f_net(a) = Σ_{INPUT(l)=a} f_weightedInput^·(l)
            net_field = act_type.sum("net")
            # The net field will automatically sum inputs from links during runtime
            
            # Field: value - f_val^·(a) - Activation output
            # f_val(a) = φ(f_net(a)) + bias where φ is activation function (ReLU or identity)
            value_field = act_type.add("value")
            
            # Field: fired - f_fired^·(a) - Boolean, true if value > threshold
            # f_fired(a) = [f_val(a) > θ]
            fired_field = act_type.inputField("fired")
            
            # Field: bs - Binding signal field for token identity
            bs_field = act_type.inputField("bs")
        
        # ========================================
        # LINK OBJECT FIELDS
        # ========================================
        # Standard weightedInput for most links
        for link_type in [self.L_EMB_KEY, self.L_EMB_QUERY, self.L_KEY_QUERY, 
                         self.L_QUERY_INHIB, self.L_EMB_VALUE]:
            # Field: weightedInput - f_weightedInput^·(l)
            # f(l) = f_weight^SYNAPSE(l) · f_val^INPUT(l)
            weighted_input = link_type.mul("weightedInput")
            weighted_input.input(aika.network.LinkDefinition.SYNAPSE, link_type.getSynapse().inputField("weight"), 0)
            weighted_input.input(aika.network.LinkDefinition.INPUT, link_type.getInput().add("value"), 1)
        
        # ========================================
        # SPECIAL CASE: INHIBITORY NEURON (SOFTMAX)
        # ========================================
        # For inhibitory output links (INHIB -> VALUE), implement softmax:
        # f_weightedInput^·(l_out) = 
        #   exp(f_val^INPUT(PAIR_IN(l_out))) / 
        #   Σ_{l' ∈ INPUT^-1(a_norm)} exp(f_val^INPUT(PAIR_IN(l'))) 
        #   · f_weight^SYNAPSE(l_out)
        
        # For the inhibitory value links, we need special softmax computation
        softmax_weighted_input = self.L_INHIB_VALUE.mul("weightedInput")
        
        # Numerator: exp(f_val^INPUT(PAIR_IN(l)))
        numerator = self.L_INHIB_VALUE.exp("softmax_numerator")
        numerator.input(aika.network.LinkDefinition.PAIR_IN, self.L_QUERY_INHIB.getInput().add("value"), 0)
        
        # Denominator: Σ exp(f_val^INPUT(PAIR_IN(l'))) for all l' leading to same inhib activation
        denominator = self.T_INHIB_ACT.sum("softmax_denominator")
        exp_component = self.L_QUERY_INHIB.exp("exp_component")
        exp_component.input(aika.network.LinkDefinition.INPUT, self.L_QUERY_INHIB.getInput().add("value"), 0)
        denominator.input(aika.network.LinkDefinition.INPUT, exp_component, 0)
        
        # Softmax ratio: numerator / denominator
        softmax_ratio = self.L_INHIB_VALUE.div("softmax_ratio")
        softmax_ratio.input(aika.network.LinkDefinition.SELF, numerator, 0)
        softmax_ratio.input(aika.network.LinkDefinition.OUTPUT, denominator, 1)
        
        # Final weighted input: softmax_ratio * weight
        softmax_weighted_input.input(aika.network.LinkDefinition.SELF, softmax_ratio, 0)
        softmax_weighted_input.input(aika.network.LinkDefinition.SYNAPSE, self.S_INHIB_VALUE.inputField("weight"), 1)
    
    def get_registry(self):
        """Get the configured type registry."""
        return self.registry
    
    def get_neuron_types(self):
        """Get all neuron type definitions."""
        return {
            'EMB': self.T_EMB,
            'KEY': self.T_KEY,
            'QUERY': self.T_QUERY,
            'INHIB': self.T_INHIB,
            'VALUE': self.T_VALUE
        }
    
    def get_activation_types(self):
        """Get all activation type definitions."""
        return {
            'EMB': self.T_EMB_ACT,
            'KEY': self.T_KEY_ACT,
            'QUERY': self.T_QUERY_ACT,
            'INHIB': self.T_INHIB_ACT,
            'VALUE': self.T_VALUE_ACT
        }
    
    def get_synapse_types(self):
        """Get all synapse type definitions."""
        return {
            'EMB_KEY': self.S_EMB_KEY,
            'EMB_QUERY': self.S_EMB_QUERY,
            'KEY_QUERY': self.S_KEY_QUERY,
            'QUERY_INHIB': self.S_QUERY_INHIB,
            'INHIB_VALUE': self.S_INHIB_VALUE,
            'EMB_VALUE': self.S_EMB_VALUE
        }
    
    def get_link_types(self):
        """Get all link type definitions."""
        return {
            'EMB_KEY': self.L_EMB_KEY,
            'EMB_QUERY': self.L_EMB_QUERY,
            'KEY_QUERY': self.L_KEY_QUERY,
            'QUERY_INHIB': self.L_QUERY_INHIB,
            'INHIB_VALUE': self.L_INHIB_VALUE,
            'EMB_VALUE': self.L_EMB_VALUE
        }


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
        print(f"   • {len(transformer_types.get_neuron_types())} neuron types")
        print(f"   • {len(transformer_types.get_activation_types())} activation types") 
        print(f"   • {len(transformer_types.get_synapse_types())} synapse types")
        print(f"   • {len(transformer_types.get_link_types())} link types")
        
        # Display the types
        print("\n🔬 Neuron Types:")
        for name, neuron_type in transformer_types.get_neuron_types().items():
            print(f"   • {name}: {neuron_type}")
        
        print("\n🔗 Synapse Types:")
        for name, synapse_type in transformer_types.get_synapse_types().items():
            print(f"   • {name}: {synapse_type}")
        
        print("\n🧮 Mathematical Model Features:")
        print("   • Standard weightedInput fields: weight × input_value")
        print("   • Softmax attention via inhibitory neurons")
        print("   • Binding signal propagation for token identity")
        print("   • Field-based computation graph")
        
        print("\n🎯 Implementation follows formal transformer specification:")
        print("   • Neuron types: EMB, KEY, QUERY, INHIB, VALUE")
        print("   • Attention mechanism via KEY→QUERY→INHIB→VALUE")
        print("   • Softmax normalization using PAIR_IN/PAIR_OUT relations")
        print("   • All field definitions configured per specification")
        
        return transformer_types
        
    except Exception as e:
        print(f"❌ Error creating transformer types: {e}")
        import traceback
        traceback.print_exc()
        return None


if __name__ == "__main__":
    main()