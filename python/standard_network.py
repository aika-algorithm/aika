"""
AIKA-Based Standard Neural Network Foundation using Builder Pattern

This module defines the foundational object types (neurons, synapses, activations, links) 
and field definitions for standard neural network operations using the AIKA framework
with the builder pattern architecture.

This serves as the base for more specialized network architectures like transformers.
"""

import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

class StandardNetworkTypeRegistry:
    """
    Standard neural network type registry that defines foundational object types 
    and their field relationships according to AIKA specifications using builder pattern.
    """
    
    def __init__(self):
        self.registry = af.TypeRegistry()
        
        # Build foundational types
        self._build_standard_types()
        
        # Setup field definitions
        self._setup_standard_field_definitions()
        
        # Flatten type hierarchy
        self.registry.flattenTypeHierarchy()
    
    def _build_standard_types(self):
        """Build standard foundational types that other architectures can inherit from."""
        print("Building standard neural network foundation types...")
        
        # ========================================
        # BUILD BASE STANDARD TYPES
        # ========================================
        
        # Build T_STANDARD_NEURON and activation (root neuron type)
        standard_neuron_builder = an.NeuronTypeBuilder(self.registry, "STANDARD_NEURON")
        self.T_STANDARD_NEURON = standard_neuron_builder.build()
        self.T_STANDARD_ACTIVATION = self.T_STANDARD_NEURON.getActivationType()
        
        # Build T_STANDARD_SYNAPSE and link (root synapse type)
        standard_synapse_builder = an.SynapseTypeBuilder(self.registry, "STANDARD_SYNAPSE")
        self.T_STANDARD_SYNAPSE = standard_synapse_builder.build()
        self.T_STANDARD_LINK = self.T_STANDARD_SYNAPSE.getLinkType()
        
        print("Standard foundation types built successfully")
    
    def _setup_standard_field_definitions(self):
        """Setup standard field definitions that will be inherited by derived types."""
        
        print("Setting up standard field definitions...")
        
        # Define foundational fields on standard types (these will be inherited)
        # Neuron bias field
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
        
        print("Standard field definitions setup complete")
    
    def get_registry(self):
        """Return the type registry"""
        return self.registry
    
    def get_standard_neuron_type(self):
        """Return the standard neuron type for inheritance"""
        return self.T_STANDARD_NEURON
    
    def get_standard_activation_type(self):
        """Return the standard activation type for inheritance"""
        return self.T_STANDARD_ACTIVATION
    
    def get_standard_synapse_type(self):
        """Return the standard synapse type for inheritance"""
        return self.T_STANDARD_SYNAPSE
    
    def get_standard_link_type(self):
        """Return the standard link type for inheritance"""
        return self.T_STANDARD_LINK

def create_standard_network_types():
    """Factory function to create and return the standard network type registry"""
    return StandardNetworkTypeRegistry()