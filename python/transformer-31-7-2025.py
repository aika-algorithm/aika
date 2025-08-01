"""
AIKA-Based Transformer Neural Network Implementation

This module implements a minimal transformer-like architecture using the AIKA framework's
object-type hierarchy, field definitions, relations, and event-driven processing.

Based on the formal specification in specs/network/transformer.md
"""

import sys
import os
import math
from typing import Dict, List, Optional, Set, Tuple

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika


class TransformerModel:
    """
    Main transformer model implementation using AIKA framework.
    
    Implements the neuron types: EMB, KEY, QUERY, INHIB, VALUE
    with corresponding activation types, synapse types, and link types.
    """
    
    def __init__(self):
        self.registry = aika.TypeRegistry()
        self.model = aika.Model(self.registry)
        
        # Initialize relations
        self._setup_relations()
        
        # Initialize neuron types
        self._setup_neuron_types()
        
        # Initialize activation types
        self._setup_activation_types()
        
        # Initialize synapse types
        self._setup_synapse_types()
        
        # Initialize link types
        self._setup_link_types()
        
        # Setup field definitions
        self._setup_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()

    def _setup_relations(self):
        """Setup the relations used in the transformer model."""
        # Basic relations from the specification
        self.INPUT = aika.RelationOne(1, "INPUT")
        self.OUTPUT = aika.RelationOne(2, "OUTPUT")
        self.SYNAPSE = aika.RelationOne(3, "SYNAPSE")
        self.ACTIVATION = aika.RelationOne(4, "ACTIVATION")
        self.NEURON = aika.RelationOne(5, "NEURON")
        self.SELF = aika.RelationSelf(6, "SELF")
        self.PAIR_IN = aika.RelationOne(7, "PAIR_IN")
        self.PAIR_OUT = aika.RelationOne(8, "PAIR_OUT")
        
        # Set up reverse relations
        self.OUTPUT.setReversed(self.INPUT)
        self.INPUT.setReversed(self.OUTPUT)
        self.NEURON.setReversed(self.ACTIVATION)
        self.ACTIVATION.setReversed(self.NEURON)
        self.PAIR_OUT.setReversed(self.PAIR_IN)
        self.PAIR_IN.setReversed(self.PAIR_OUT)
        
    def _setup_neuron_types(self):
        """Setup neuron type definitions according to transformer spec."""
        # T_N = {T_EMB, T_KEY, T_QUERY, T_INHIB, T_VALUE}: neuron types
        self.T_EMB = aika.NeuronDefinition(self.registry, "EMB_NEURON")
        self.T_KEY = aika.NeuronDefinition(self.registry, "KEY_NEURON")
        self.T_QUERY = aika.NeuronDefinition(self.registry, "QUERY_NEURON")
        self.T_INHIB = aika.NeuronDefinition(self.registry, "INHIB_NEURON")
        self.T_VALUE = aika.NeuronDefinition(self.registry, "VALUE_NEURON")
        
        # Add bias field to all neuron types
        for neuron_type in [self.T_EMB, self.T_KEY, self.T_QUERY, self.T_INHIB, self.T_VALUE]:
            bias_field = neuron_type.inputField("bias")
            
    def _setup_activation_types(self):
        """Setup activation type definitions."""
        # T_A: corresponding activation types
        self.T_EMB_ACT = aika.ActivationDefinition(self.registry, "EMB_ACTIVATION")
        self.T_KEY_ACT = aika.ActivationDefinition(self.registry, "KEY_ACTIVATION")
        self.T_QUERY_ACT = aika.ActivationDefinition(self.registry, "QUERY_ACTIVATION")
        self.T_INHIB_ACT = aika.ActivationDefinition(self.registry, "INHIB_ACTIVATION")
        self.T_VALUE_ACT = aika.ActivationDefinition(self.registry, "VALUE_ACTIVATION")
        
        # Set up neuron-activation relationships
        self.T_EMB.setActivation(self.T_EMB_ACT)
        self.T_KEY.setActivation(self.T_KEY_ACT)
        self.T_QUERY.setActivation(self.T_QUERY_ACT)
        self.T_INHIB.setActivation(self.T_INHIB_ACT)
        self.T_VALUE.setActivation(self.T_VALUE_ACT)
        
        # Add activation fields: net, value, fired
        for act_type in [self.T_EMB_ACT, self.T_KEY_ACT, self.T_QUERY_ACT, self.T_INHIB_ACT, self.T_VALUE_ACT]:
            net_field = act_type.sum("net")
            value_field = act_type.fieldActivationFunc("value", aika.ActivationFunction(), 0.0)
            fired_field = act_type.inputField("fired")
            bs_field = act_type.inputField("bs")  # Binding signal field
            
    def _setup_synapse_types(self):
        """Setup synapse type definitions according to transformer spec."""
        # T_S: synapse types linking above neuron types
        self.S_EMB_KEY = aika.SynapseDefinition(self.registry, "S_EMB_KEY")
        self.S_EMB_QUERY = aika.SynapseDefinition(self.registry, "S_EMB_QUERY")
        self.S_KEY_QUERY = aika.SynapseDefinition(self.registry, "S_KEY_QUERY")
        self.S_QUERY_INHIB = aika.SynapseDefinition(self.registry, "S_QUERY_INHIB")
        self.S_INHIB_VALUE = aika.SynapseDefinition(self.registry, "S_INHIB_VALUE")
        self.S_EMB_VALUE = aika.SynapseDefinition(self.registry, "S_EMB_VALUE")
        
        # Set input/output relationships
        self.S_EMB_KEY.setInput(self.T_EMB)
        self.S_EMB_KEY.setOutput(self.T_KEY)
        
        self.S_EMB_QUERY.setInput(self.T_EMB)
        self.S_EMB_QUERY.setOutput(self.T_QUERY)
        
        self.S_KEY_QUERY.setInput(self.T_KEY)
        self.S_KEY_QUERY.setOutput(self.T_QUERY)
        
        self.S_QUERY_INHIB.setInput(self.T_QUERY)
        self.S_QUERY_INHIB.setOutput(self.T_INHIB)
        
        self.S_INHIB_VALUE.setInput(self.T_INHIB)
        self.S_INHIB_VALUE.setOutput(self.T_VALUE)
        
        self.S_EMB_VALUE.setInput(self.T_EMB)
        self.S_EMB_VALUE.setOutput(self.T_VALUE)
        
        # Add weight field to all synapse types
        for synapse_type in [self.S_EMB_KEY, self.S_EMB_QUERY, self.S_KEY_QUERY, 
                           self.S_QUERY_INHIB, self.S_INHIB_VALUE, self.S_EMB_VALUE]:
            weight_field = synapse_type.inputField("weight")
            
    def _setup_link_types(self):
        """Setup link type definitions."""
        # T_L: link types created during inference
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
        
        # Set input/output activation types
        self.L_EMB_KEY.setInput(self.T_EMB_ACT)
        self.L_EMB_KEY.setOutput(self.T_KEY_ACT)
        
        self.L_EMB_QUERY.setInput(self.T_EMB_ACT)
        self.L_EMB_QUERY.setOutput(self.T_QUERY_ACT)
        
        self.L_KEY_QUERY.setInput(self.T_KEY_ACT)
        self.L_KEY_QUERY.setOutput(self.T_QUERY_ACT)
        
        self.L_QUERY_INHIB.setInput(self.T_QUERY_ACT)
        self.L_QUERY_INHIB.setOutput(self.T_INHIB_ACT)
        
        self.L_INHIB_VALUE.setInput(self.T_INHIB_ACT)
        self.L_INHIB_VALUE.setOutput(self.T_VALUE_ACT)
        
        self.L_EMB_VALUE.setInput(self.T_EMB_ACT)
        self.L_EMB_VALUE.setOutput(self.T_VALUE_ACT)
        
    def _setup_field_definitions(self):
        """Setup mathematical field definitions according to transformer spec."""
        # Define weightedInput field for links
        # f_weightedInput(l) = f_weight^SYNAPSE(l) * f_val^INPUT(l)
        for link_type in [self.L_EMB_KEY, self.L_EMB_QUERY, self.L_KEY_QUERY, 
                         self.L_QUERY_INHIB, self.L_INHIB_VALUE, self.L_EMB_VALUE]:
            weighted_input = link_type.mul("weightedInput")
            # This will be connected properly during runtime when links are created
            
        # For inhibitory links, we need special softmax handling
        # This will be implemented in the softmax computation method

        
    def create_embedding_neuron(self, token_id: int, embedding_values: List[float]) -> aika.Neuron:
        """Create an embedding neuron for a token with initial embedding values."""
        neuron = self.T_EMB.instantiate(self.model)
        neuron.setFieldValue(neuron.getType().inputField("bias"), 0.0)
        self.neurons[f"emb_{token_id}"] = neuron
        return neuron
        
    def create_key_neuron(self, token_id: int) -> aika.Neuron:
        """Create a key neuron for a token."""
        neuron = self.T_KEY.instantiate(self.model)
        neuron.setFieldValue(neuron.getType().inputField("bias"), 0.0)
        self.neurons[f"key_{token_id}"] = neuron
        return neuron
        
    def create_query_neuron(self, token_id: int) -> aika.Neuron:
        """Create a query neuron for a token."""
        neuron = self.T_QUERY.instantiate(self.model)
        neuron.setFieldValue(neuron.getType().inputField("bias"), 0.0)
        self.neurons[f"query_{token_id}"] = neuron
        return neuron
        
    def create_inhibitory_neuron(self, token_id: int) -> aika.Neuron:
        """Create an inhibitory neuron for softmax computation."""
        neuron = self.T_INHIB.instantiate(self.model)
        neuron.setFieldValue(neuron.getType().inputField("bias"), 0.0)
        self.neurons[f"inhib_{token_id}"] = neuron
        return neuron
        
    def create_value_neuron(self, token_id: int) -> aika.Neuron:
        """Create a value neuron for a token."""
        neuron = self.T_VALUE.instantiate(self.model)
        neuron.setFieldValue(neuron.getType().inputField("bias"), 0.0)
        self.neurons[f"value_{token_id}"] = neuron
        return neuron
        
    def create_synapse(self, synapse_type: aika.SynapseDefinition, weight: float, 
                      input_neuron: aika.Neuron, output_neuron: aika.Neuron) -> aika.Synapse:
        """Create a synapse between two neurons with specified weight."""
        synapse = synapse_type.instantiate(input_neuron, output_neuron)
        synapse.setFieldValue(synapse_type.inputField("weight"), weight)
        return synapse
        
    def process_sequence(self, doc: aika.Document, tokens: List[int], 
                        embeddings: Dict[int, List[float]]) -> Dict[int, float]:
        """
        Process a sequence of tokens through the transformer.
        
        Args:
            doc: Document to process in
            tokens: List of token IDs
            embeddings: Dictionary mapping token IDs to embedding vectors
            
        Returns:
            Dictionary mapping token IDs to final output values
        """
        # Step 1: Create embedding activations for each token
        emb_activations = {}
        for token_id in tokens:
            emb_neuron = self.create_embedding_neuron(token_id, embeddings[token_id])
            
            # Create activation with binding signal
            bs = doc.getOrCreateBindingSignal(aika.TestBSType(f"token_{token_id}"), token_id)
            bs_map = {aika.TestBSType(f"token_{token_id}"): bs}
            
            emb_act = aika.ConjunctiveActivation(
                self.T_EMB_ACT, None, 0, emb_neuron, doc, bs_map
            )
            emb_act.setFieldValue(self.T_EMB_ACT.inputField("bs"), token_id)
            emb_act.setFieldValue(self.T_EMB_ACT.inputField("value"), sum(embeddings[token_id]))
            emb_activations[token_id] = emb_act
            
        # Step 2: Create key and query neurons and activations
        key_activations = {}
        query_activations = {}
        
        for token_id in tokens:
            # Create key neuron and activation
            key_neuron = self.create_key_neuron(token_id)
            key_act = aika.ConjunctiveActivation(
                self.T_KEY_ACT, None, 0, key_neuron, doc, 
                {aika.TestBSType(f"token_{token_id}"): doc.getBindingSignal(aika.TestBSType(f"token_{token_id}"), token_id)}
            )
            key_activations[token_id] = key_act
            
            # Create query neuron and activation
            query_neuron = self.create_query_neuron(token_id)
            query_act = aika.ConjunctiveActivation(
                self.T_QUERY_ACT, None, 0, query_neuron, doc,
                {aika.TestBSType(f"token_{token_id}"): doc.getBindingSignal(aika.TestBSType(f"token_{token_id}"), token_id)}
            )
            query_activations[token_id] = query_act
            
        # Step 3: Create synapses and links
        self._create_transformer_connections(doc, tokens, emb_activations, 
                                           key_activations, query_activations)
        
        # Step 4: Compute attention via inhibitory neurons
        attention_weights = self._compute_attention(doc, tokens, query_activations)
        
        # Step 5: Create value neurons and compute final outputs
        final_outputs = self._compute_values(doc, tokens, emb_activations, attention_weights)
        
        return final_outputs
        
    def _create_transformer_connections(self, doc: aika.Document, tokens: List[int],
                                      emb_activations: Dict[int, aika.Activation],
                                      key_activations: Dict[int, aika.Activation],
                                      query_activations: Dict[int, aika.Activation]):
        """Create the synapse connections for the transformer."""
        # Create EMB -> KEY connections
        for token_id in tokens:
            emb_neuron = emb_activations[token_id].getNeuron()
            key_neuron = key_activations[token_id].getNeuron()
            
            synapse = self.create_synapse(self.S_EMB_KEY, 1.0, emb_neuron, key_neuron)
            link = synapse.createLink(emb_activations[token_id], key_activations[token_id])
            
        # Create EMB -> QUERY connections
        for token_id in tokens:
            emb_neuron = emb_activations[token_id].getNeuron()
            query_neuron = query_activations[token_id].getNeuron()
            
            synapse = self.create_synapse(self.S_EMB_QUERY, 1.0, emb_neuron, query_neuron)
            link = synapse.createLink(emb_activations[token_id], query_activations[token_id])
            
        # Create KEY -> QUERY connections (attention mechanism)
        for query_token in tokens:
            for key_token in tokens:
                key_neuron = key_activations[key_token].getNeuron()
                query_neuron = query_activations[query_token].getNeuron()
                
                # Use scaled dot-product attention weight
                weight = self._compute_attention_weight(key_token, query_token)
                synapse = self.create_synapse(self.S_KEY_QUERY, weight, key_neuron, query_neuron)
                link = synapse.createLink(key_activations[key_token], query_activations[query_token])
                
    def _compute_attention_weight(self, key_token: int, query_token: int) -> float:
        """Compute attention weight between key and query tokens."""
        # Simplified attention weight computation
        # In a real implementation, this would involve dot product of key and query vectors
        if key_token == query_token:
            return 1.0
        else:
            return 0.1  # Simplified cross-attention weight
            
    def _compute_attention(self, doc: aika.Document, tokens: List[int],
                          query_activations: Dict[int, aika.Activation]) -> Dict[int, Dict[int, float]]:
        """Compute attention weights using inhibitory neurons (softmax)."""
        attention_weights = {}
        
        for query_token in tokens:
            # Create inhibitory neuron for this query position
            inhib_neuron = self.create_inhibitory_neuron(query_token)
            inhib_act = aika.ConjunctiveActivation(
                self.T_INHIB_ACT, None, 0, inhib_neuron, doc,
                {aika.TestBSType(f"token_{query_token}"): doc.getBindingSignal(aika.TestBSType(f"token_{query_token}"), query_token)}
            )
            
            # Create QUERY -> INHIB connections
            query_values = []
            for key_token in tokens:
                query_neuron = query_activations[query_token].getNeuron()
                synapse = self.create_synapse(self.S_QUERY_INHIB, 1.0, query_neuron, inhib_neuron)
                link = synapse.createLink(query_activations[query_token], inhib_act)
                
                # Get query activation value for softmax computation
                query_val = query_activations[query_token].getFieldValue(self.T_QUERY_ACT.inputField("value"))
                query_values.append(query_val)
                
            # Compute softmax
            softmax_values = self._softmax(query_values)
            attention_weights[query_token] = {tokens[i]: softmax_values[i] for i in range(len(tokens))}
            
        return attention_weights
        

    def _compute_values(self, doc: aika.Document, tokens: List[int],
                       emb_activations: Dict[int, aika.Activation],
                       attention_weights: Dict[int, Dict[int, float]]) -> Dict[int, float]:
        """Compute final value outputs using attention weights."""
        final_outputs = {}
        
        for token_id in tokens:
            # Create value neuron
            value_neuron = self.create_value_neuron(token_id)
            value_act = aika.ConjunctiveActivation(
                self.T_VALUE_ACT, None, 0, value_neuron, doc,
                {aika.TestBSType(f"token_{token_id}"): doc.getBindingSignal(aika.TestBSType(f"token_{token_id}"), token_id)}
            )
            
            # Compute weighted sum of embeddings using attention weights
            weighted_sum = 0.0
            for source_token in tokens:
                emb_value = emb_activations[source_token].getFieldValue(self.T_EMB_ACT.inputField("value"))
                attention_weight = attention_weights[token_id].get(source_token, 0.0)
                weighted_sum += emb_value * attention_weight
                
            value_act.setFieldValue(self.T_VALUE_ACT.inputField("value"), weighted_sum)
            final_outputs[token_id] = weighted_sum
            
        return final_outputs


def main():
    """Example usage of the transformer model."""
    # Create transformer model
    transformer = TransformerModel()
    
    # Create document
    doc = transformer.create_document(1)
    
    # Example tokens and embeddings
    tokens = [1, 2, 3]
    embeddings = {
        1: [0.1, 0.2, 0.3, 0.4],
        2: [0.5, 0.6, 0.7, 0.8],
        3: [0.9, 1.0, 1.1, 1.2]
    }
    
    # Process sequence
    outputs = transformer.process_sequence(doc, tokens, embeddings)
    
    print("Transformer outputs:")
    for token_id, output_value in outputs.items():
        print(f"Token {token_id}: {output_value}")
    
    # Process the document queue
    doc.process()
    
    print("Processing complete.")


if __name__ == "__main__":
    main()