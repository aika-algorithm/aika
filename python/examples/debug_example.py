#!/usr/bin/env python3
"""
AIKA Debug Utilities - Usage Examples

This script shows practical examples of how to use the AIKA debug utilities
for debugging and understanding your AIKA neural networks.
"""

import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika
import aika.fields as af
import aika.network as an

from python.utils.aika_debug_utils import AikaDebugger, DebugConfig, DetailLevel, dump_object, quick_dump, verbose_dump
from python.networks.transformer import create_transformer_types

def example_basic_usage():
    """Example 1: Basic usage with convenience functions"""
    print("="*60)
    print("EXAMPLE 1: BASIC USAGE")
    print("="*60)
    
    # Create a transformer for demonstration
    transformer = create_transformer_types()
    model = an.Model(transformer.get_registry())
    
    print("1. Quick dump of a neuron type:")
    emb_type = transformer.T_EMB
    print(quick_dump(emb_type))
    
    print("\n2. Normal dump of the model:")
    print(dump_object(model, DetailLevel.NORMAL))
    
    print("\n3. Quick dump of type registry:")
    print(quick_dump(transformer.get_registry()))

def example_custom_configuration():
    """Example 2: Custom configuration for specific debugging needs"""
    print("\n" + "="*60)
    print("EXAMPLE 2: CUSTOM CONFIGURATION")
    print("="*60)
    
    transformer = create_transformer_types()
    model = an.Model(transformer.get_registry())
    
    # Create some neurons to examine
    emb_neuron = transformer.T_EMB.instantiate(model)
    key_neuron = transformer.T_KEY.instantiate(model)
    emb_key_synapse = transformer.S_EMB_KEY.instantiate(emb_neuron, key_neuron)
    
    print("1. Detailed neuron dump with custom settings:")
    config = DebugConfig(
        detail_level=DetailLevel.DETAILED,
        max_depth=3,
        show_synapses_in_neuron=True,
        show_object_addresses=True,
        indent_size=4,
        max_items=10
    )
    debugger = AikaDebugger(config)
    print(debugger.dump_neuron(emb_neuron))
    
    print("\n2. Synapse dump with pairing information:")
    config = DebugConfig(
        detail_level=DetailLevel.DETAILED,
        max_depth=2,
        show_relations=True
    )
    debugger = AikaDebugger(config)
    print(debugger.dump_synapse(emb_key_synapse))

def example_debugging_workflow():
    """Example 3: Typical debugging workflow"""
    print("\n" + "="*60)
    print("EXAMPLE 3: DEBUGGING WORKFLOW")
    print("="*60)
    
    transformer = create_transformer_types()
    model = an.Model(transformer.get_registry())
    context = an.Context(model)
    
    # Create a small network for debugging
    emb_neuron = transformer.T_EMB.instantiate(model)
    key_neuron = transformer.T_KEY.instantiate(model)
    query_neuron = transformer.T_QUERY.instantiate(model)
    comp_neuron = transformer.T_COMP.instantiate(model)
    
    # Connect them
    emb_key_synapse = transformer.S_EMB_KEY.instantiate(emb_neuron, key_neuron)
    emb_query_synapse = transformer.S_EMB_QUERY.instantiate(emb_neuron, query_neuron)
    key_comp_synapse = transformer.S_KEY_COMP.instantiate(key_neuron, comp_neuron)
    query_comp_synapse = transformer.S_QUERY_COMP.instantiate(query_neuron, comp_neuron)
    
    print("DEBUGGING SCENARIO: Examining dot-product computation setup")
    print("Network: EMB -> KEY/QUERY -> COMP (dot-product)")
    
    print("\nStep 1: Quick overview of the model")
    print(dump_object(model, DetailLevel.MINIMAL))
    
    print("\nStep 2: Examine the COMP neuron in detail")
    config = DebugConfig(
        detail_level=DetailLevel.DETAILED,
        max_depth=2,
        show_synapses_in_neuron=True,
        show_relations=False  # Focus on structure, not relations
    )
    debugger = AikaDebugger(config)
    print(debugger.dump_neuron(comp_neuron))
    
    print("\nStep 3: Check the pairing between KEY_COMP and QUERY_COMP synapses")
    print("KEY_COMP synapse:")
    print(debugger.dump_synapse(key_comp_synapse))
    print("\nQUERY_COMP synapse:")
    print(debugger.dump_synapse(query_comp_synapse))
    
    print("\nStep 4: Examine the context state")
    print(debugger.dump_context(context))

def example_field_examination():
    """Example 4: Examining field definitions and relationships"""
    print("\n" + "="*60)
    print("EXAMPLE 4: FIELD EXAMINATION")
    print("="*60)
    
    transformer = create_transformer_types()
    
    print("1. Examining DOT-product types and their mathematical fields:")
    config = DebugConfig(
        detail_level=DetailLevel.VERBOSE,
        max_depth=3,
        show_fields=True,
        show_relations=True,
        show_parents=True
    )
    debugger = AikaDebugger(config)
    
    print("DOT neuron type:")
    print(debugger.dump_type(transformer.T_DOT))
    
    print("\n2. Field definitions from the dot-product network:")
    # Access field information
    dot_fields = transformer.dot_fields
    print("Available dot-product fields:")
    for field_name, field_obj in dot_fields.items():
        print(f"  - {field_name}: {type(field_obj).__name__}")

def example_error_debugging():
    """Example 5: Debugging common errors"""
    print("\n" + "="*60)
    print("EXAMPLE 5: ERROR DEBUGGING SCENARIOS")
    print("="*60)
    
    transformer = create_transformer_types()
    model = an.Model(transformer.get_registry())
    
    print("Scenario: Investigating why a neuron isn't receiving expected inputs")
    
    # Create a neuron that should receive inputs
    comp_neuron = transformer.T_COMP.instantiate(model)
    
    print("1. Check if neuron has expected input synapses:")
    config = DebugConfig(
        detail_level=DetailLevel.DETAILED,
        max_depth=1,
        show_synapses_in_neuron=True
    )
    debugger = AikaDebugger(config)
    comp_dump = debugger.dump_neuron(comp_neuron)
    print(comp_dump)
    
    if "Input Synapses (0)" in comp_dump:
        print("❌ ISSUE FOUND: No input synapses connected to COMP neuron")
        print("   SOLUTION: Create and connect KEY_COMP and QUERY_COMP synapses")
        
        # Show how to fix it
        key_neuron = transformer.T_KEY.instantiate(model)
        query_neuron = transformer.T_QUERY.instantiate(model)
        
        key_comp_synapse = transformer.S_KEY_COMP.instantiate(key_neuron, comp_neuron)
        query_comp_synapse = transformer.S_QUERY_COMP.instantiate(query_neuron, comp_neuron)
        
        print("\n   After connecting synapses:")
        print(debugger.dump_neuron(comp_neuron))
    
    print("2. Verify synapse pairing for dot-product operation:")
    if 'key_comp_synapse' in locals() and 'query_comp_synapse' in locals():
        key_dump = debugger.dump_synapse(key_comp_synapse)
        query_dump = debugger.dump_synapse(query_comp_synapse)
        
        print("KEY_COMP synapse details:")
        print(key_dump)
        print("\nQUERY_COMP synapse details:")
        print(query_dump)

def main():
    """Run all examples"""
    print("AIKA DEBUG UTILITIES - PRACTICAL EXAMPLES")
    print("This demonstrates how to use the debug utilities for real debugging tasks")
    
    try:
        example_basic_usage()
        example_custom_configuration()
        example_debugging_workflow()
        example_field_examination()
        example_error_debugging()
        
        print("\n" + "="*60)
        print("ALL EXAMPLES COMPLETED SUCCESSFULLY!")
        print("="*60)
        print("\nKey takeaways:")
        print("1. Use quick_dump() for fast overview")
        print("2. Use custom DebugConfig for specific debugging needs")
        print("3. Start with minimal detail, then increase as needed")
        print("4. Focus on specific object types (neuron, synapse, etc.) when debugging")
        print("5. Use show_synapses_in_neuron=True to debug connectivity issues")
        print("6. Check pairing information when debugging dot-product operations")
        
    except Exception as e:
        print(f"Error in examples: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()