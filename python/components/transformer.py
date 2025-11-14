"""
AIKA-Based Transformer Neural Network Type Definitions using Builder Pattern

This module defines the transformer-specific object types (dot-product neurons, softmax, etc.) 
for a minimal transformer-like architecture using the AIKA framework
with the new builder pattern architecture.

Based on the formal specification in specs/network/transformer.md
"""



import aika
import aika.fields as af
import aika.network as an
from python.types.standard import create_standard_network_types
from python.types.dot_product_types import create_dot_product_types
from python.types.softmax_types import create_softmax_types

class TransformerTypeRegistry:
    """
    Transformer type registry that defines transformer-specific concrete object types 
    according to the AIKA transformer specification using builder pattern.
    
    This builds on the dot-product and softmax neural network foundations and creates
    concrete implementations of transformer components like COMP, MIX, etc.
    
    The dot-product mathematical model is defined in dot_product_types.py.
    The softmax mathematical model is defined in softmax_types.py.
    """
    
    def __init__(self):
        # Create the standard network foundation
        print("Setting up standard neural network foundation...")
        self.standard_network = create_standard_network_types()
        self.registry = self.standard_network.get_registry()

        # Get standard types for inheritance
        self.T_STANDARD_NEURON = self.standard_network.get_standard_neuron_type()
        self.T_STANDARD_ACTIVATION = self.standard_network.get_standard_activation_type()
        self.S_STANDARD_INPUT_SIDE = self.standard_network.get_standard_input_side_type()
        self.S_STANDARD_OUTPUT_SIDE = self.standard_network.get_standard_output_side_type()


        # Create the dot-product foundation using the shared registry
        print("Setting up dot-product neural network foundation...")
        self.dot_product_network = create_dot_product_types(
            self.registry,
            self.standard_network.value_field,
            self.standard_network.input_value_field
        )

        # Create the softmax foundation using the shared registry
        print("Setting up softmax neural network foundation...")
        self.softmax_network = create_softmax_types(
            self.registry,
            self.standard_network.value_field,
            self.standard_network.input_value_field
        )

        # Get dot-product types for inheritance
        self.T_DOT = self.dot_product_network.get_dot_neuron_type()
        self.T_DOT_ACT = self.dot_product_network.get_dot_activation_type()
        self.S_DOT_PRIMARY_OUTPUT_SIDE = self.dot_product_network.get_dot_primary_output_side_type()
        self.S_DOT_SECONDARY_OUTPUT_SIDE = self.dot_product_network.get_dot_secondary_output_side_type()

        # Get softmax types for inheritance
        self.T_SOFTMAX = self.softmax_network.get_softmax_neuron_type()
        self.T_SOFTMAX_ACT = self.softmax_network.get_softmax_activation_type()
        self.S_SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE = self.softmax_network.get_softmax_input_synapse_output_side_type()
        self.S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE = self.softmax_network.get_softmax_output_synapse_input_side_type()
        
        # Build transformer-specific concrete types
        self._build_transformer_types()
        
        # Get field definitions from the specialized modules
        self.dot_product_fields = self.dot_product_network.get_dot_product_fields()
        self.softmax_fields = self.softmax_network.get_softmax_fields()
        
        # Flatten type hierarchy (includes all types)
        self.registry.flattenTypeHierarchy()
        
        # Set up field access for tests (combining dot-product and softmax fields with legacy names)
        self.dot_fields = {
            # DOT fields (inherited by COMP and MIX)
            'net': self.dot_product_fields['dot_net'],
            'value': self.dot_product_fields['dot_value'],
            'comp_net': self.dot_product_fields['dot_net'],      # COMP uses DOT net field
            'comp_value': self.dot_product_fields['dot_value'],  # COMP uses DOT value field  
            'mix_net': self.dot_product_fields['dot_net'],       # MIX uses DOT net field
            'mix_value': self.dot_product_fields['dot_value'],   # MIX uses DOT value field
            
            # Link-specific fields
            'secondary_identity': self.dot_product_fields['secondary_identity'],        # KEY_COMP, VALUE_MIX
            'primary_multiplication': self.dot_product_fields['primary_multiplication'], # QUERY_COMP, ATTENTION_MIX
            
            # Compatibility names for tests
            'key_comp_weighted': self.dot_product_fields['secondary_identity'],       # KEY_COMP identity operation
            'key_comp_mul': self.dot_product_fields['secondary_identity'],            # Legacy name for KEY_COMP
            'query_comp_mul': self.dot_product_fields['primary_multiplication'],     # QUERY_COMP multiplication
            'value_mix_weighted': self.dot_product_fields['secondary_identity'],     # VALUE_MIX identity operation
            'softmax_mix_mul': self.dot_product_fields['primary_multiplication']     # ATTENTION_MIX multiplication (legacy name)
        }
        
        # Flatten type hierarchy (includes standard + transformer types)
        self.registry.flattenTypeHierarchy()
    
    def _build_transformer_types(self):
        """Build transformer-specific types that inherit from the standard foundation"""
        print("Building transformer-specific types...")
        
        # ========================================
        # BUILD TRANSFORMER NEURON TYPES
        # ========================================
        
        # Build T_EMB (embedding neuron and activation)
        emb_builder = an.NeuronTypeBuilder(self.registry, "EMB_NEURON")
        emb_builder.setParent(self.T_STANDARD_NEURON)
        self.T_EMB = emb_builder.build()
        self.T_EMB_ACT = self.T_EMB.getActivationType()

        # Build T_KEY (key neuron and activation)
        key_builder = an.NeuronTypeBuilder(self.registry, "KEY_NEURON")
        key_builder.setParent(self.T_STANDARD_NEURON)
        self.T_KEY = key_builder.build()
        self.T_KEY_ACT = self.T_KEY.getActivationType()

        # Build T_QUERY (query neuron and activation)
        query_builder = an.NeuronTypeBuilder(self.registry, "QUERY_NEURON")
        query_builder.setParent(self.T_STANDARD_NEURON)
        self.T_QUERY = query_builder.build()
        self.T_QUERY_ACT = self.T_QUERY.getActivationType()

        # Build T_VALUE (value neuron and activation)
        value_builder = an.NeuronTypeBuilder(self.registry, "VALUE_NEURON")
        value_builder.setParent(self.T_STANDARD_NEURON)
        self.T_VALUE = value_builder.build()
        self.T_VALUE_ACT = self.T_VALUE.getActivationType()
        
        # ========================================
        # BUILD CONCRETE DOT-PRODUCT FAMILY TYPES
        # ========================================
        
        # Build T_COMP (comparison neuron and activation) - inherits from abstract DOT
        comp_builder = an.NeuronTypeBuilder(self.registry, "COMP_NEURON")
        comp_builder.setParent(self.T_DOT)  # Inherit from abstract DOT
        self.T_COMP = comp_builder.build()
        self.T_COMP_ACT = self.T_COMP.getActivationType()

        # Build T_MIX (mixing neuron and activation) - inherits from abstract DOT
        mix_builder = an.NeuronTypeBuilder(self.registry, "MIX_NEURON")
        mix_builder.setParent(self.T_DOT)  # Inherit from abstract DOT
        self.T_MIX = mix_builder.build()
        self.T_MIX_ACT = self.T_MIX.getActivationType()

        print("Built concrete DOT-PRODUCT types (COMP, MIX) inheriting from abstract DOT")

        # ========================================
        # BUILD CONCRETE SOFTMAX FAMILY TYPES
        # ========================================

        # Build T_ATTENTION (attention neuron and activation) - inherits from abstract SOFTMAX
        attention_builder = an.NeuronTypeBuilder(self.registry, "ATTENTION_NEURON")
        attention_builder.setParent(self.T_SOFTMAX)  # Inherit from abstract SOFTMAX
        self.T_ATTENTION = attention_builder.build()
        self.T_ATTENTION_ACT = self.T_ATTENTION.getActivationType()
        
        print("Built concrete SOFTMAX type (ATTENTION) inheriting from abstract SOFTMAX")
        
        # ========================================
        # BUILD TRANSFORMER SYNAPSE TYPES
        # ========================================
        
        # Build S_EMB_KEY (embedding to key synapse and link)
        emb_key_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_KEY")
        emb_key_builder.setInput(self.T_EMB).setOutput(self.T_KEY)
        emb_key_builder.setInputSideParent(self.S_STANDARD_INPUT_SIDE)
        emb_key_builder.setOutputSideParent(self.S_STANDARD_OUTPUT_SIDE)
        self.S_EMB_KEY = emb_key_builder.build()
        self.L_EMB_KEY = self.S_EMB_KEY.getLinkType()

        # Build S_EMB_QUERY (embedding to query synapse and link)
        emb_query_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_QUERY")
        emb_query_builder.setInput(self.T_EMB).setOutput(self.T_QUERY)
        emb_query_builder.setInputSideParent(self.S_STANDARD_INPUT_SIDE)
        emb_query_builder.setOutputSideParent(self.S_STANDARD_OUTPUT_SIDE)
        self.S_EMB_QUERY = emb_query_builder.build()
        self.L_EMB_QUERY = self.S_EMB_QUERY.getLinkType()

        # Build S_EMB_VALUE (embedding to value synapse and link)
        emb_value_builder = an.SynapseTypeBuilder(self.registry, "S_EMB_VALUE")
        emb_value_builder.setInput(self.T_EMB).setOutput(self.T_VALUE)
        emb_value_builder.setInputSideParent(self.S_STANDARD_INPUT_SIDE)
        emb_value_builder.setOutputSideParent(self.S_STANDARD_OUTPUT_SIDE)
        self.S_EMB_VALUE = emb_value_builder.build()
        self.L_EMB_VALUE = self.S_EMB_VALUE.getLinkType()

        # ========================================
        # BUILD CONCRETE DOT-PRODUCT SYNAPSE TYPES
        # ========================================
        
        # Build concrete KEY_COMP and QUERY_COMP synapses (inherit from abstract DOT types)
        key_comp_builder = an.SynapseTypeBuilder(self.registry, "S_KEY_COMP")
        key_comp_builder.setInput(self.T_KEY).setOutput(self.T_COMP)
        key_comp_builder.setInputSideParent(self.S_STANDARD_INPUT_SIDE)
        key_comp_builder.setOutputSideParent(self.S_DOT_SECONDARY_OUTPUT_SIDE)
        self.S_KEY_COMP = key_comp_builder.build()
        self.L_KEY_COMP = self.S_KEY_COMP.getLinkType()

        query_comp_builder = an.SynapseTypeBuilder(self.registry, "S_QUERY_COMP")
        query_comp_builder.setInput(self.T_QUERY).setOutput(self.T_COMP)
        query_comp_builder.setInputSideParent(self.S_STANDARD_INPUT_SIDE)
        query_comp_builder.setOutputSideParent(self.S_DOT_PRIMARY_OUTPUT_SIDE)
        self.S_QUERY_COMP = query_comp_builder.build()
        self.L_QUERY_COMP = self.S_QUERY_COMP.getLinkType()

        print("Built concrete DOT-PRODUCT synapses:")
        print("  - KEY_COMP (secondary): Standard input-side + DOT_SECONDARY output-side")
        print("  - QUERY_COMP (primary): Standard input-side + DOT_PRIMARY output-side")

        # Build S_COMP_ATTENTION (comparison to attention synapse and link)
        comp_attention_builder = an.SynapseTypeBuilder(self.registry, "S_COMP_ATTENTION")
        comp_attention_builder.setInput(self.T_COMP).setOutput(self.T_ATTENTION)
        comp_attention_builder.setInputSideParent(self.S_STANDARD_INPUT_SIDE)
        comp_attention_builder.setOutputSideParent(self.S_SOFTMAX_INPUT_SYNAPSE_OUTPUT_SIDE)
        self.S_COMP_ATTENTION = comp_attention_builder.build()
        self.L_COMP_ATTENTION = self.S_COMP_ATTENTION.getLinkType()

        # Build MIX dot-product synapses (inherit from abstract DOT types)
        value_mix_builder = an.SynapseTypeBuilder(self.registry, "S_VALUE_MIX")
        value_mix_builder.setInput(self.T_VALUE).setOutput(self.T_MIX)
        value_mix_builder.setInputSideParent(self.S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE)
        value_mix_builder.setOutputSideParent(self.S_DOT_SECONDARY_OUTPUT_SIDE)
        self.S_VALUE_MIX = value_mix_builder.build()
        self.L_VALUE_MIX = self.S_VALUE_MIX.getLinkType()

        attention_mix_builder = an.SynapseTypeBuilder(self.registry, "S_ATTENTION_MIX")
        attention_mix_builder.setInput(self.T_ATTENTION).setOutput(self.T_MIX)
        attention_mix_builder.setInputSideParent(self.S_SOFTMAX_OUTPUT_SYNAPSE_INPUT_SIDE)
        attention_mix_builder.setOutputSideParent(self.S_DOT_PRIMARY_OUTPUT_SIDE)
        self.S_ATTENTION_MIX = attention_mix_builder.build()
        self.L_ATTENTION_MIX = self.S_ATTENTION_MIX.getLinkType()
        
        print("Built concrete MIX DOT-PRODUCT synapses:")
        print("  - VALUE_MIX (secondary): Identity operation, inherits from DOT_SECONDARY")
        print("  - ATTENTION_MIX (primary): Multiplication with PAIR_IN, inherits from DOT_PRIMARY")
        
        print("Transformer-specific types built successfully")
    
    def _setup_dot_product_fields(self):
        """Setup dot-product field definitions using abstract DOT types with primary/secondary architecture"""
        print("Setting up dot-product field definitions...")
        
        # ========================================
        # ABSTRACT DOT-PRODUCT FIELD IMPLEMENTATION
        # ========================================
        
        # Abstract DOT neuron type gets the mathematical implementation
        # - DOT neurons have NO bias (no inheritance from standard)
        # - DOT synapses have NO weight (no inheritance from standard)
        # - Primary synapses: multiplication operation (QUERY_COMP, ATTENTION_MIX)
        # - Secondary synapses: identity operation (KEY_COMP, VALUE_MIX)
        
        print("Implementing abstract DOT-PRODUCT mathematical model...")
        
        # ========================================
        # DOT ACTIVATION FIELDS (ABSTRACT)
        # ========================================
        
        # DOT net field: Sum all pair contributions from primary links  
        self.dot_net_field = self.T_DOT_ACT.sum("net")
        
        # DOT value field: Identity (value = net, no activation function)
        self.dot_value_field = self.T_DOT_ACT.identity("value")
        # Connect value = net (identity function)
        self.dot_value_field.input(self.T_DOT_ACT.SELF, self.dot_net_field, 0)
        
        print("Set up abstract DOT activation fields: net (sum) and value (identity)")
        
        # ========================================
        # SECONDARY LINK FIELDS (IDENTITY)
        # ========================================
        
        # Secondary links provide identity operation: output = input_activation.value
        self.secondary_identity_field = self.L_DOT_SECONDARY.identity("identityOutput")
        # Connect to input activation's value via INPUT relation
        self.secondary_identity_field.input(self.L_DOT_SECONDARY.INPUT, self.dot_value_field, 0)
        
        print("Set up DOT_SECONDARY link: Identity operation")
        
        # ========================================
        # PRIMARY LINK FIELDS (MULTIPLICATION)
        # ========================================
        
        # Primary links contain multiplication: this.identityOutput × PAIR_IN.identityOutput
        self.primary_multiplication_field = self.L_DOT_PRIMARY.mul("pairMultiplication")
        # Input 0: This link's identity output
        self.primary_multiplication_field.input(self.L_DOT_PRIMARY.SELF, self.secondary_identity_field, 0)
        # Input 1: Paired secondary link's identity output (via PAIR_IN)
        self.primary_multiplication_field.input(self.L_DOT_PRIMARY.PAIR_IN, self.secondary_identity_field, 1)
        
        print("Set up DOT_PRIMARY link: Multiplication with PAIR_IN relation")
        
        # Connect DOT net field to primary multiplication results
        self.dot_net_field.input(self.T_DOT_ACT.INPUT, self.primary_multiplication_field, 0)
        
        print("Connected DOT net field to primary multiplication results")
        
        # ========================================
        # COMPLETED ABSTRACT DOT-PRODUCT IMPLEMENTATION
        # ========================================
        
        # Store field references for access in tests
        self.dot_fields = {
            # Abstract DOT fields (inherited by COMP and MIX)
            'net': self.dot_net_field,
            'value': self.dot_value_field,
            'comp_net': self.dot_net_field,      # COMP uses DOT net field
            'comp_value': self.dot_value_field,  # COMP uses DOT value field  
            'mix_net': self.dot_net_field,       # MIX uses DOT net field
            'mix_value': self.dot_value_field,   # MIX uses DOT value field
            
            # Link-specific fields
            'secondary_identity': self.secondary_identity_field,    # KEY_COMP, VALUE_MIX
            'primary_multiplication': self.primary_multiplication_field, # QUERY_COMP, SOFTMAX_MIX
            
            # Compatibility names for tests
            'key_comp_weighted': self.secondary_identity_field,     # Legacy name
            'query_comp_mul': self.primary_multiplication_field,   # Legacy name
            'value_mix_weighted': self.secondary_identity_field,    # Legacy name  
            'softmax_mix_mul': self.primary_multiplication_field   # Legacy name
        }

    
    def get_registry(self):
        """Return the type registry (includes both standard and transformer types)"""
        return self.registry
    
    def get_standard_network(self):
        """Return the underlying standard network foundation"""
        return self.standard_network
    
    def get_dot_product_network(self):
        """Return the underlying dot-product network foundation"""
        return self.dot_product_network
    
    def get_softmax_network(self):
        """Return the underlying softmax network foundation"""
        return self.softmax_network

def create_transformer_types():
    """Factory function to create and return the transformer type registry"""
    return TransformerTypeRegistry()