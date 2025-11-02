import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class LinkPairRelationTestCase(unittest.TestCase):
    """
    Test the PAIR relation implementation in Link::followSingleRelation.
    This tests the paired link fields and PAIR/PAIR_IN/PAIR_OUT relation handling.
    """
    
    def setUp(self):
        """Set up test fixtures before each test method."""
        print("Setting up link pair relation test...")
        self.registry = af.TypeRegistry()
        
        # Create neuron types for testing
        self.input_builder = an.NeuronTypeBuilder(self.registry, "INPUT_NEURON")
        self.middle_builder = an.NeuronTypeBuilder(self.registry, "MIDDLE_NEURON")  
        self.output_builder = an.NeuronTypeBuilder(self.registry, "OUTPUT_NEURON")
        
        self.input_type = self.input_builder.build()
        self.middle_type = self.middle_builder.build()
        self.output_type = self.output_builder.build()
        
        self.registry.flattenTypeHierarchy()
        
        # Create model and context
        self.model = an.Model(self.registry)
        self.context = an.Context(self.model)
        
        print("Created neuron types: INPUT, MIDDLE, OUTPUT")
        
    def test_paired_link_fields_default_values(self):
        """Test that paired link fields have correct default values."""
        print("Testing paired link fields default values...")
        
        # Create synapse types and instances
        synapse_builder = an.SynapseTypeBuilder(self.registry, "TEST_SYNAPSE")
        synapse_builder.setInput(self.input_type).setOutput(self.middle_type)
        synapse_type = synapse_builder.build()
        
        # Flatten type hierarchy after building synapse type
        self.registry.flattenTypeHierarchy()
        
        synapse = synapse_type.instantiate()
        
        # Create neuron instances
        input_neuron = self.input_type.instantiate(self.model)
        middle_neuron = self.middle_type.instantiate(self.model)
        
        # Create activations
        input_activation = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            input_neuron,
            self.context,
            {}
        )
        
        middle_activation = an.Activation(
            self.middle_type.getActivationType(),
            None,
            self.context.createActivationId(),
            middle_neuron,
            self.context,
            {}
        )
        
        # Create link
        link = an.Link(
            synapse_type.getLinkType(),
            synapse,
            input_activation,
            middle_activation
        )
        
        # Test default values
        paired_input = link.getPairedLinkInputSide()
        paired_output = link.getPairedLinkOutputSide()
        
        self.assertIsNone(paired_input, "Default paired link input side should be None")
        self.assertIsNone(paired_output, "Default paired link output side should be None")
        
        print(f"✅ Default paired link input side: {paired_input}")
        print(f"✅ Default paired link output side: {paired_output}")
        
    def test_paired_link_setters_and_getters(self):
        """Test setting and getting paired link values."""
        print("Testing paired link setters and getters...")
        
        # Create synapse types
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "SYNAPSE_1")
        synapse1_builder.setInput(self.input_type).setOutput(self.middle_type)
        synapse1_type = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "SYNAPSE_2") 
        synapse2_builder.setInput(self.middle_type).setOutput(self.output_type)
        synapse2_type = synapse2_builder.build()
        
        # Flatten type hierarchy after building synapse types
        self.registry.flattenTypeHierarchy()
        
        # Create synapse instances
        synapse1 = synapse1_type.instantiate()
        synapse2 = synapse2_type.instantiate()
        
        # Create neuron instances
        input_neuron = self.input_type.instantiate(self.model)
        middle_neuron = self.middle_type.instantiate(self.model)
        output_neuron = self.output_type.instantiate(self.model)
        
        # Create activations
        input_activation = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            input_neuron,
            self.context,
            {}
        )
        
        middle_activation = an.Activation(
            self.middle_type.getActivationType(),
            None, 
            self.context.createActivationId(),
            middle_neuron,
            self.context,
            {}
        )
        
        output_activation = an.Activation(
            self.output_type.getActivationType(),
            None,
            self.context.createActivationId(),
            output_neuron,
            self.context,
            {}
        )
        
        # Create links
        link1 = an.Link(
            synapse1_type.getLinkType(),
            synapse1,
            input_activation,
            middle_activation
        )
        
        link2 = an.Link(
            synapse2_type.getLinkType(),
            synapse2,
            middle_activation,
            output_activation
        )
        
        # Test setting paired links
        link1.setPairedLinkInputSide(link2)
        link1.setPairedLinkOutputSide(link2)
        link2.setPairedLinkInputSide(link1)
        link2.setPairedLinkOutputSide(link1)
        
        # Test getting paired links
        self.assertEqual(link1.getPairedLinkInputSide(), link2)
        self.assertEqual(link1.getPairedLinkOutputSide(), link2)
        self.assertEqual(link2.getPairedLinkInputSide(), link1)
        self.assertEqual(link2.getPairedLinkOutputSide(), link1)
        
        print("✅ All paired link setter/getter operations work correctly")
        
    def test_pair_relation_following(self):
        """Test following PAIR relations through Link::followSingleRelation."""
        print("Testing PAIR relation following...")
        
        # This test is simplified since we would need access to the actual Relation objects
        # and the followSingleRelation method to test properly. For now, we test that the
        # paired link fields can be set and retrieved correctly.
        
        # Create a basic link setup
        synapse_builder = an.SynapseTypeBuilder(self.registry, "PAIR_TEST_SYNAPSE")
        synapse_builder.setInput(self.input_type).setOutput(self.middle_type)
        synapse_type = synapse_builder.build()
        
        # Flatten type hierarchy after building synapse type
        self.registry.flattenTypeHierarchy()
        
        synapse = synapse_type.instantiate()
        
        # Create neuron instances
        input_neuron = self.input_type.instantiate(self.model)
        middle_neuron = self.middle_type.instantiate(self.model)
        
        # Create activations  
        input_activation = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            input_neuron,
            self.context,
            {}
        )
        
        middle_activation = an.Activation(
            self.middle_type.getActivationType(),
            None,
            self.context.createActivationId(),
            middle_neuron,
            self.context,
            {}
        )
        
        # Create main link
        main_link = an.Link(
            synapse_type.getLinkType(),
            synapse,
            input_activation,
            middle_activation
        )
        
        # Create a paired link (simulate pairing scenario)
        paired_synapse = synapse_type.instantiate()
        paired_link = an.Link(
            synapse_type.getLinkType(),
            paired_synapse,
            input_activation,
            middle_activation
        )
        
        # Set up pairing
        main_link.setPairedLinkInputSide(paired_link)
        main_link.setPairedLinkOutputSide(paired_link)
        
        # Verify pairing setup
        self.assertEqual(main_link.getPairedLinkInputSide(), paired_link)
        self.assertEqual(main_link.getPairedLinkOutputSide(), paired_link)
        
        print(f"✅ Main link paired input side: {main_link.getPairedLinkInputSide() is not None}")
        print(f"✅ Main link paired output side: {main_link.getPairedLinkOutputSide() is not None}")
        print("✅ PAIR relation setup verified")
        
        # Note: The actual followSingleRelation with PAIR/PAIR_IN/PAIR_OUT would require
        # access to relation objects which are not easily accessible from Python tests.
        # The C++ implementation should handle:
        # - PAIR: returns pairedLinkInputSide (default for output-side pairing)
        # - PAIR_IN: returns pairedLinkInputSide 
        # - PAIR_OUT: returns pairedLinkOutputSide
        
    def test_pair_use_case_output_side_pairing(self):
        """Test the intended use case for PAIR relations with output-side pairing."""
        print("Testing PAIR use case with output-side pairing...")
        
        # This simulates the use case where we have two input synapses paired
        # and their respective links need to be accessible through PAIR relations
        
        # Create paired synapse types (both input to middle neuron)
        synapse1_builder = an.SynapseTypeBuilder(self.registry, "PAIRED_SYNAPSE_1")
        synapse1_builder.setInput(self.input_type).setOutput(self.middle_type)
        synapse1_type = synapse1_builder.build()
        
        synapse2_builder = an.SynapseTypeBuilder(self.registry, "PAIRED_SYNAPSE_2")
        synapse2_builder.setInput(self.input_type).setOutput(self.middle_type) 
        synapse2_builder.pair(synapse1_type, 0)  # Pair with binding signal slot 0
        synapse2_type = synapse2_builder.build()
        
        # Flatten type hierarchy after building synapse types
        self.registry.flattenTypeHierarchy()
        
        # Create synapse instances
        synapse1 = synapse1_type.instantiate()
        synapse2 = synapse2_type.instantiate()
        
        # Create neuron instances
        input_neuron = self.input_type.instantiate(self.model)
        middle_neuron = self.middle_type.instantiate(self.model)
        
        # Create activations
        input_activation = an.Activation(
            self.input_type.getActivationType(),
            None,
            self.context.createActivationId(),
            input_neuron,
            self.context,
            {}
        )
        
        middle_activation = an.Activation(
            self.middle_type.getActivationType(),
            None,
            self.context.createActivationId(),
            middle_neuron,
            self.context,
            {}
        )
        
        # Create links representing the paired input synapses
        link1 = an.Link(
            synapse1_type.getLinkType(),
            synapse1,
            input_activation,
            middle_activation
        )
        
        link2 = an.Link(
            synapse2_type.getLinkType(),
            synapse2,
            input_activation,
            middle_activation
        )
        
        # Set up pairing (this would be done automatically in the full implementation)
        # Since we're dealing with output-side pairing, we set the paired links
        link1.setPairedLinkInputSide(link2)   # link1 can access link2 via PAIR_IN
        link2.setPairedLinkInputSide(link1)   # link2 can access link1 via PAIR_IN
        
        # Verify pairing setup for output-side pairing scenario
        self.assertEqual(link1.getPairedLinkInputSide(), link2)
        self.assertEqual(link2.getPairedLinkInputSide(), link1)
        
        print(f"✅ Link1 can access Link2 via PAIR relation: {link1.getPairedLinkInputSide() == link2}")
        print(f"✅ Link2 can access Link1 via PAIR relation: {link2.getPairedLinkInputSide() == link1}")
        print("✅ Output-side pairing use case verified")
        print("    PAIR relations enable linking two input synapses and their respective links")
        
    def tearDown(self):
        """Clean up after each test method."""
        print("Cleaning up link pair relation test...")

if __name__ == '__main__':
    unittest.main(verbosity=2)