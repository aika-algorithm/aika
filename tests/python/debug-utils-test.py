#!/usr/bin/env python3
"""
Test and demonstration of AIKA Debug Utilities

This script demonstrates the debug utilities with various AIKA objects
and different configuration levels.
"""

import sys
import unittest

import aika
import aika.fields as af
import aika.network as an

from python.utils.aika_debug_utils import AikaDebugger, DebugConfig, DetailLevel, dump_object, quick_dump, verbose_dump
from python.components.transformer import create_transformer_types
from python.types.standard import create_standard_network_types

class DebugUtilsTestCase(unittest.TestCase):
    """Test cases for AIKA debug utilities"""
    
    def setUp(self):
        """Set up test objects"""
        print(f"\\nSetting up debug utils test...")
        
        # Create a full transformer setup for testing
        self.transformer_types = create_transformer_types()
        
        # Create a simple model and context for testing
        self.model = an.Model(self.transformer_types.get_registry())
        self.context = an.Context(self.model)
        
        # Create some test neurons and activations
        self.emb_neuron = self.transformer_types.T_EMB.instantiate(self.model)
        self.key_neuron = self.transformer_types.T_KEY.instantiate(self.model)
        
        # Create a simple synapse
        self.emb_key_synapse = self.transformer_types.S_EMB_KEY.instantiate(self.emb_neuron, self.key_neuron)
        
        print("Test objects created successfully")
    
    def tearDown(self):
        """Clean up after tests"""
        print("Cleaning up debug utils test...")
        # Reset context if needed
        try:
            if hasattr(self, 'context') and self.context:
                # Clean up context
                pass
        except:
            pass
    
    def test_type_registry_dump(self):
        """Test dumping TypeRegistry with different detail levels"""
        print("\\nTesting TypeRegistry dumps...")
        
        # Test different detail levels
        debugger_minimal = AikaDebugger(DebugConfig(detail_level=DetailLevel.MINIMAL, max_depth=1))
        dump_minimal = debugger_minimal.dump_type_registry(self.transformer_types.get_registry())
        self.assertIn("TypeRegistry", dump_minimal)
        print("✅ Minimal TypeRegistry dump")
        
        debugger_normal = AikaDebugger(DebugConfig(detail_level=DetailLevel.NORMAL, max_depth=2))
        dump_normal = debugger_normal.dump_type_registry(self.transformer_types.get_registry())
        self.assertIn("TypeRegistry", dump_normal)
        print("✅ Normal TypeRegistry dump")
        
        debugger_detailed = AikaDebugger(DebugConfig(detail_level=DetailLevel.DETAILED, max_depth=2))
        dump_detailed = debugger_detailed.dump_type_registry(self.transformer_types.get_registry())
        self.assertIn("TypeRegistry", dump_detailed)
        print("✅ Detailed TypeRegistry dump")
        
        # Print sample output (truncated)
        print("\\n--- Sample TypeRegistry Dump (Normal Level) ---")
        lines = dump_normal.split('\\n')
        for i, line in enumerate(lines[:20]):  # Show first 20 lines
            print(line)
        if len(lines) > 20:
            print(f"... (truncated {len(lines) - 20} more lines)")
    
    def test_model_dump(self):
        """Test dumping Model"""
        print("\\nTesting Model dump...")
        
        debugger = AikaDebugger(DebugConfig(detail_level=DetailLevel.NORMAL, max_depth=2))
        dump = debugger.dump_model(self.model)
        
        self.assertIn("Model", dump)
        print("✅ Model dump successful")
        
        print("\\n--- Sample Model Dump ---")
        lines = dump.split('\\n')
        for line in lines[:15]:  # Show first 15 lines
            print(line)
        if len(lines) > 15:
            print(f"... (truncated {len(lines) - 15} more lines)")
    
    def test_context_dump(self):
        """Test dumping Context"""
        print("\\nTesting Context dump...")
        
        debugger = AikaDebugger(DebugConfig(detail_level=DetailLevel.NORMAL, max_depth=2))
        dump = debugger.dump_context(self.context)
        
        self.assertIn("Context", dump)
        print("✅ Context dump successful")
        
        print("\\n--- Sample Context Dump ---")
        print(dump)
    
    def test_neuron_dump(self):
        """Test dumping Neuron"""
        print("\\nTesting Neuron dump...")
        
        debugger = AikaDebugger(DebugConfig(
            detail_level=DetailLevel.DETAILED, 
            max_depth=2,
            show_synapses_in_neuron=True
        ))
        dump = debugger.dump_neuron(self.emb_neuron)
        
        self.assertIn("Neuron", dump)
        print("✅ Neuron dump successful")
        
        print("\\n--- Sample Neuron Dump ---")
        print(dump)
    
    def test_synapse_dump(self):
        """Test dumping Synapse"""
        print("\\nTesting Synapse dump...")
        
        debugger = AikaDebugger(DebugConfig(detail_level=DetailLevel.DETAILED, max_depth=2))
        dump = debugger.dump_synapse(self.emb_key_synapse)
        
        self.assertIn("Synapse", dump)
        print("✅ Synapse dump successful")
        
        print("\\n--- Sample Synapse Dump ---")
        print(dump)
    
    def test_convenience_functions(self):
        """Test convenience functions"""
        print("\\nTesting convenience functions...")
        
        # Test quick_dump
        quick = quick_dump(self.transformer_types.get_registry())
        self.assertIn("TypeRegistry", quick)
        print("✅ quick_dump works")
        
        # Test dump_object with different detail levels
        normal = dump_object(self.emb_neuron, DetailLevel.NORMAL)
        self.assertIn("Neuron", normal)
        print("✅ dump_object works")
        
        # Test verbose_dump (but don't print it all)
        verbose = verbose_dump(self.emb_neuron)
        self.assertIn("Neuron", verbose)
        print("✅ verbose_dump works")
    
    def test_configuration_options(self):
        """Test various configuration options"""
        print("\\nTesting configuration options...")
        
        # Test with different options
        config = DebugConfig(
            detail_level=DetailLevel.DETAILED,
            max_depth=3,
            show_fields=True,
            show_binding_signals=True,
            show_relations=True,
            show_parents=True,
            show_object_addresses=True,
            indent_size=4
        )
        
        debugger = AikaDebugger(config)
        dump = debugger.dump_neuron(self.emb_neuron)
        
        self.assertIn("Neuron", dump)
        print("✅ Custom configuration works")
        
        print("\\n--- Sample Dump with Custom Config ---")
        lines = dump.split('\\n')
        for line in lines[:10]:  # Show first 10 lines
            print(line)
        if len(lines) > 10:
            print(f"... (truncated {len(lines) - 10} more lines)")
    
    def test_recursive_handling(self):
        """Test that recursive references are handled properly"""
        print("\\nTesting recursive reference handling...")
        
        # This should not cause infinite recursion
        debugger = AikaDebugger(DebugConfig(
            detail_level=DetailLevel.VERBOSE,
            max_depth=5,
            show_parents=True,
            show_children=True
        ))
        
        dump = debugger.dump_type_registry(self.transformer_types.get_registry())
        
        # Should complete without infinite recursion
        self.assertIn("TypeRegistry", dump)
        print("✅ Recursive reference handling works")

def demonstration():
    """Run a demonstration of the debug utilities"""
    print("\\n" + "="*60)
    print("AIKA DEBUG UTILITIES DEMONSTRATION")
    print("="*60)
    
    # Create test objects
    print("\\nSetting up demonstration objects...")
    transformer_types = create_transformer_types()
    model = an.Model(transformer_types.get_registry())
    context = an.Context(model)
    
    # Create some neurons
    emb_neuron = transformer_types.T_EMB.instantiate(model)
    key_neuron = transformer_types.T_KEY.instantiate(model)
    
    print("Objects created successfully!")
    
    print("\\n" + "-"*40)
    print("1. QUICK DUMP OF TYPE REGISTRY")
    print("-"*40)
    print(quick_dump(transformer_types.get_registry()))
    
    print("\\n" + "-"*40)
    print("2. NORMAL DUMP OF MODEL")
    print("-"*40)
    config = DebugConfig(detail_level=DetailLevel.NORMAL, max_depth=2)
    debugger = AikaDebugger(config)
    model_dump = debugger.dump_model(model)
    lines = model_dump.split('\\n')
    for line in lines[:20]:  # Limit output
        print(line)
    if len(lines) > 20:
        print(f"... (truncated {len(lines) - 20} more lines)")
    
    print("\\n" + "-"*40)
    print("3. DETAILED DUMP OF EMB NEURON")
    print("-"*40)
    config = DebugConfig(
        detail_level=DetailLevel.DETAILED,
        max_depth=2,
        show_synapses_in_neuron=True,
        show_object_addresses=True
    )
    debugger = AikaDebugger(config)
    print(debugger.dump_neuron(emb_neuron))
    
    print("\\n" + "-"*40)
    print("4. CONTEXT DUMP")
    print("-"*40)
    print(debugger.dump_context(context))
    
    print("\\n" + "="*60)
    print("DEMONSTRATION COMPLETE")
    print("="*60)

if __name__ == '__main__':
    # Run demonstration first
    demonstration()
    
    # Then run unit tests
    print("\\n\\nRunning unit tests...")
    unittest.main(verbosity=2)