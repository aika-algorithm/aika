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
        
        # Initialize relations
        self._setup_relations()
        
        # Initialize all type definitions
        self._setup_neuron_types()
        self._setup_activation_types()
        self._setup_synapse_types()
        self._setup_link_types()
        
        # Setup field definitions and mathematical model
        self._setup_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _setup_relations(self):
        """Setup the relations used in the transformer model."""
        # Core relations from AIKA framework
        self.INPUT = aika.RelationOne(1, "INPUT")
        self.OUTPUT = aika.RelationOne(2, "OUTPUT")
        self.SYNAPSE = aika.RelationOne(3, "SYNAPSE")
        self.ACTIVATION = aika.RelationOne(4, "ACTIVATION")
        self.NEURON = aika.RelationOne(5, "NEURON")
        self.SELF = aika.RelationSelf(6, "SELF")
        
        # Special relations for inhibitory softmax computation
        self.PAIR_IN = aika.RelationOne(7, "PAIR_IN")
        self.PAIR_OUT = aika.RelationOne(8, "PAIR_OUT")
        
        # Set up bidirectional relations
        self.OUTPUT.setReversed(self.INPUT)
        self.INPUT.setReversed(self.OUTPUT)
        self.NEURON.setReversed(self.ACTIVATION)
        self.ACTIVATION.setReversed(self.NEURON)
        self.PAIR_OUT.setReversed(self.PAIR_IN)
        self.PAIR_IN.setReversed(self.PAIR_OUT)
    
    def _setup_neuron_types(self):
        """Setup neuron type definitions: T_N = {T_EMB, T_KEY, T_QUERY, T_INHIB, T_VALUE}"""
        self.T_EMB = aika.NeuronDefinition(self.registry, "EMB_NEURON")
        self.T_KEY = aika.NeuronDefinition(self.registry, "KEY_NEURON")
        self.T_QUERY = aika.NeuronDefinition(self.registry, "QUERY_NEURON")
        self.T_INHIB = aika.NeuronDefinition(self.registry, "INHIB_NEURON")
        self.T_VALUE = aika.NeuronDefinition(self.registry, "VALUE_NEURON")
    
    def _setup_activation_types(self):
        """Setup activation type definitions: T_A"""
        self.T_EMB_ACT = aika.ActivationDefinition(self.registry, "EMB_ACTIVATION")
        self.T_KEY_ACT = aika.ActivationDefinition(self.registry, "KEY_ACTIVATION")
        self.T_QUERY_ACT = aika.ActivationDefinition(self.registry, "QUERY_ACTIVATION")
        self.T_INHIB_ACT = aika.ActivationDefinition(self.registry, "INHIB_ACTIVATION")
        self.T_VALUE_ACT = aika.ActivationDefinition(self.registry, "VALUE_ACTIVATION")
        
        # Link neuron types to their activation types
        self.T_EMB.setActivation(self.T_EMB_ACT)
        self.T_KEY.setActivation(self.T_KEY_ACT)
        self.T_QUERY.setActivation(self.T_QUERY_ACT)
        self.T_INHIB.setActivation(self.T_INHIB_ACT)
        self.T_VALUE.setActivation(self.T_VALUE_ACT)
    
    def _setup_synapse_types(self):
        """Setup synapse type definitions: T_S"""
        # Synapse types according to transformer specification
        self.S_EMB_KEY = aika.SynapseDefinition(self.registry, "S_EMB_KEY")
        self.S_EMB_QUERY = aika.SynapseDefinition(self.registry, "S_EMB_QUERY")
        self.S_KEY_QUERY = aika.SynapseDefinition(self.registry, "S_KEY_QUERY")
        self.S_QUERY_INHIB = aika.SynapseDefinition(self.registry, "S_QUERY_INHIB")
        self.S_INHIB_VALUE = aika.SynapseDefinition(self.registry, "S_INHIB_VALUE")
        self.S_EMB_VALUE = aika.SynapseDefinition(self.registry, "S_EMB_VALUE")
        
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
        self.L_EMB_KEY = aika.LinkDefinition(self.registry, "L_EMB_KEY")
        self.L_EMB_QUERY = aika.LinkDefinition(self.registry, "L_EMB_QUERY")
        self.L_KEY_QUERY = aika.LinkDefinition(self.registry, "L_KEY_QUERY")
        self.L_QUERY_INHIB = aika.LinkDefinition(self.registry, "L_QUERY_INHIB")
        self.L_INHIB_VALUE = aika.LinkDefinition(self.registry, "L_INHIB_VALUE")
        self.L_EMB_VALUE = aika.LinkDefinition(self.registry, "L_EMB_VALUE")
        
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
            net_field.input(self.INPUT, self.L_EMB_KEY.mul("weightedInput"), 0)
            net_field.input(self.INPUT, self.L_EMB_QUERY.mul("weightedInput"), 1)
            net_field.input(self.INPUT, self.L_KEY_QUERY.mul("weightedInput"), 2)
            net_field.input(self.INPUT, self.L_QUERY_INHIB.mul("weightedInput"), 3)
            net_field.input(self.INPUT, self.L_INHIB_VALUE.mul("weightedInput"), 4)
            net_field.input(self.INPUT, self.L_EMB_VALUE.mul("weightedInput"), 5)
            
            # Field: value - f_val^·(a) - Activation output
            # f_val(a) = φ(f_net(a)) where φ is activation function (ReLU or identity)
            value_field = act_type.add("value")
            value_field.input(self.SELF, net_field, 0)
            value_field.input(self.NEURON, neuron_type.inputField("bias"), 1)
            
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
            weighted_input.input(self.SYNAPSE, link_type.getSynapse().inputField("weight"), 0)
            weighted_input.input(self.INPUT, link_type.getInput().add("value"), 1)
        
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
        numerator.input(self.PAIR_IN, self.L_QUERY_INHIB.getInput().add("value"), 0)
        
        # Denominator: Σ exp(f_val^INPUT(PAIR_IN(l'))) for all l' leading to same inhib activation
        denominator = self.T_INHIB_ACT.sum("softmax_denominator")
        exp_component = self.L_QUERY_INHIB.exp("exp_component")
        exp_component.input(self.INPUT, self.L_QUERY_INHIB.getInput().add("value"), 0)
        denominator.input(self.INPUT, exp_component, 0)
        
        # Softmax ratio: numerator / denominator
        softmax_ratio = self.L_INHIB_VALUE.div("softmax_ratio")
        softmax_ratio.input(self.SELF, numerator, 0)
        softmax_ratio.input(self.OUTPUT, denominator, 1)
        
        # Final weighted input: softmax_ratio * weight
        softmax_weighted_input.input(self.SELF, softmax_ratio, 0)
        softmax_weighted_input.input(self.SYNAPSE, self.S_INHIB_VALUE.inputField("weight"), 1)
    
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
    print("Creating transformer type definitions...")
    
    # Create the type registry with all transformer types
    transformer_types = create_transformer_types()
    
    print("Transformer type registry created successfully!")
    print(f"Registry contains {len(transformer_types.get_neuron_types())} neuron types")
    print(f"Registry contains {len(transformer_types.get_activation_types())} activation types")
    print(f"Registry contains {len(transformer_types.get_synapse_types())} synapse types")
    print(f"Registry contains {len(transformer_types.get_link_types())} link types")
    
    # Display the types
    print("\nNeuron Types:")
    for name, neuron_type in transformer_types.get_neuron_types().items():
        print(f"  - {name}: {neuron_type}")
    
    print("\nSynapse Types:")
    for name, synapse_type in transformer_types.get_synapse_types().items():
        print(f"  - {name}: {synapse_type}")
    
    print("\nField definitions and mathematical model configured according to transformer specification.")


if __name__ == "__main__":
    main()