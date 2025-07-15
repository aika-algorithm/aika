import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
from abstract_activation_test import AbstractActivationTest, TestBSType

class MinimalNetworkTest(AbstractActivationTest):
    
    def test_basic_neuron_creation(self):
        """Test basic neuron creation and instantiation"""
        # Create neuron definition
        neuron_def = aika.network.NeuronDefinition(self.type_registry, "test_neuron")
        
        # Create activation definition
        activation_def = aika.network.ActivationDefinition(self.type_registry, "test_activation")
        neuron_def.setActivation(activation_def)
        
        # Flatten type hierarchy
        self.type_registry.flattenTypeHierarchy()
        
        # Instantiate neuron
        neuron = neuron_def.instantiate(self.model)
        
        # Verify neuron properties
        self.assertIsNotNone(neuron)
        self.assertEqual(neuron.getModel(), self.model)
        self.assertIsNotNone(neuron.getId())
    
    def test_document_creation(self):
        """Test document creation and basic operations"""
        # Create document
        doc = aika.network.Document(self.model, 200)
        
        # Verify document properties
        self.assertIsNotNone(doc)
        self.assertEqual(doc.getModel(), self.model)
        self.assertIsNotNone(doc.getId())
    
    def test_binding_signal_creation(self):
        """Test binding signal creation and operations"""
        # Create binding signal
        bs = aika.network.BindingSignal(42, self.doc)
        
        # Verify binding signal properties
        self.assertEqual(42, bs.getTokenId())
        self.assertEqual(self.doc, bs.getDocument())
    
    def test_activation_creation(self):
        """Test basic activation creation"""
        # Create binding signal
        bs = aika.network.BindingSignal(1, self.doc)
        
        # Create activation
        binding_signals = {self.BSType_A: bs}
        activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,  # parent
            self.doc.createActivationId(),
            self.neuron,
            self.doc,
            binding_signals
        )
        
        # Verify activation properties
        self.assertIsNotNone(activation)
        self.assertEqual(self.neuron, activation.getNeuron())
        self.assertEqual(self.doc, activation.getDocument())
        self.assertEqual(self.model, activation.getModel())
        self.assertEqual(bs, activation.getBindingSignal(self.BSType_A))
    
    def test_synapse_creation(self):
        """Test basic synapse creation"""
        # Create input neuron
        input_neuron_def = aika.network.NeuronDefinition(self.type_registry, "input_neuron")
        input_neuron = input_neuron_def.instantiate(self.model)
        
        # Create synapse definition
        synapse_def = aika.network.SynapseDefinition(self.type_registry, "test_synapse")
        synapse_def.setSubType(aika.network.SynapseSubType.CONJUNCTIVE)
        synapse_def.setInput(input_neuron_def)
        synapse_def.setOutput(self.neuron_def)
        
        # Flatten type hierarchy
        self.type_registry.flattenTypeHierarchy()
        
        # Create synapse
        synapse = synapse_def.instantiate(input_neuron, self.neuron)
        
        # Verify synapse properties
        self.assertIsNotNone(synapse)
        self.assertEqual(input_neuron, synapse.getInput())
        self.assertEqual(self.neuron, synapse.getOutput())
    
    def test_link_creation(self):
        """Test basic link creation"""
        # Create input neuron and activation
        input_neuron_def = aika.network.NeuronDefinition(self.type_registry, "input_neuron")
        input_activation_def = aika.network.ActivationDefinition(self.type_registry, "input_activation")
        input_neuron_def.setActivation(input_activation_def)
        
        input_neuron = input_neuron_def.instantiate(self.model)
        
        # Create synapse
        synapse_def = aika.network.SynapseDefinition(self.type_registry, "test_synapse")
        synapse_def.setInput(input_neuron_def)
        synapse_def.setOutput(self.neuron_def)
        
        # Create link definition
        link_def = aika.network.LinkDefinition(self.type_registry, "test_link")
        link_def.setInput(input_activation_def)
        link_def.setOutput(self.activation_def)
        link_def.setSynapse(synapse_def)
        
        # Flatten type hierarchy
        self.type_registry.flattenTypeHierarchy()
        
        # Create synapse instance
        synapse = synapse_def.instantiate(input_neuron, self.neuron)
        
        # Create activations
        bs = aika.network.BindingSignal(1, self.doc)
        
        input_activation = aika.network.ConjunctiveActivation(
            input_activation_def,
            None,
            self.doc.createActivationId(),
            input_neuron,
            self.doc,
            {self.BSType_A: bs}
        )
        
        output_activation = aika.network.ConjunctiveActivation(
            self.activation_def,
            None,
            self.doc.createActivationId(),
            self.neuron,
            self.doc,
            {self.BSType_B: bs}
        )
        
        # Create link
        link = aika.network.Link(
            link_def,
            synapse,
            input_activation,
            output_activation
        )
        
        # Verify link properties
        self.assertIsNotNone(link)
        self.assertEqual(synapse, link.getSynapse())
        self.assertEqual(input_activation, link.getInput())
        self.assertEqual(output_activation, link.getOutput())


if __name__ == '__main__':
    unittest.main()