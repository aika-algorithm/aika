"""
AIKA Debug Utilities - Human-readable object dumping

This module provides utilities for dumping AIKA objects (TypeRegistry, Model, Context, 
Neuron, Activation, Synapse, Link) in human-readable format with configurable detail levels.

Usage:
    from python.aika_debug_utils import AikaDebugger, DebugConfig
    
    # Basic usage
    debugger = AikaDebugger()
    debugger.dump_type_registry(registry)
    
    # With custom configuration
    config = DebugConfig(
        max_depth=3,
        show_fields=True,
        show_binding_signals=True,
        show_relations=True
    )
    debugger = AikaDebugger(config)
    debugger.dump_model(model)
"""

from typing import Any, Dict, List, Set, Optional, Union
from dataclasses import dataclass
from enum import Enum


import aika
import aika.fields as af
import aika.network as an

class DetailLevel(Enum):
    """Detail level for dumps"""
    MINIMAL = 1      # Just basic info
    NORMAL = 2       # Include important relationships
    DETAILED = 3     # Include fields and most relationships
    VERBOSE = 4      # Include everything

@dataclass
class DebugConfig:
    """Configuration for debug output"""
    detail_level: DetailLevel = DetailLevel.NORMAL
    max_depth: int = 3
    max_items: int = 50
    show_fields: bool = True
    show_binding_signals: bool = True
    show_relations: bool = True
    show_parents: bool = True
    show_children: bool = False  # Can be very verbose
    show_object_addresses: bool = False
    indent_size: int = 2
    
    # Type-specific options
    show_activations_in_context: bool = True
    show_synapses_in_neuron: bool = True
    show_links_in_activation: bool = True
    show_field_values: bool = False  # Can be very verbose

class AikaDebugger:
    """Main debug utility class for dumping AIKA objects"""
    
    def __init__(self, config: Optional[DebugConfig] = None):
        self.config = config or DebugConfig()
        self.visited: Set[int] = set()  # Track visited objects to avoid infinite recursion
        self.current_depth = 0
        
    def _reset_state(self):
        """Reset internal state for new dump operation"""
        self.visited.clear()
        self.current_depth = 0
    
    def _get_object_id(self, obj: Any) -> str:
        """Get a unique identifier for an object"""
        if obj is None:
            return "None"
        
        obj_id = id(obj)
        if self.config.show_object_addresses:
            return f"@{obj_id:x}"
        return f"#{obj_id % 10000}"
    
    def _should_continue(self, obj: Any) -> bool:
        """Check if we should continue dumping this object"""
        if obj is None:
            return False
        if self.current_depth >= self.config.max_depth:
            return False
        
        obj_id = id(obj)
        if obj_id in self.visited:
            return False
        
        return True
    
    def _indent(self, level: int = None) -> str:
        """Get indentation string for given level"""
        if level is None:
            level = self.current_depth
        return " " * (level * self.config.indent_size)
    
    def _format_value(self, value: Any, max_length: int = 50) -> str:
        """Format a value for display"""
        if value is None:
            return "None"
        
        str_value = str(value)
        if len(str_value) > max_length:
            return str_value[:max_length-3] + "..."
        return str_value
    
    def _dump_object_header(self, obj: Any, type_name: str, extra_info: str = "") -> str:
        """Create header line for object dump"""
        obj_id = self._get_object_id(obj)
        header = f"{self._indent()}{type_name} {obj_id}"
        if extra_info:
            header += f" ({extra_info})"
        return header
    
    def _safe_call(self, obj: Any, method_name: str, *args, **kwargs) -> Any:
        """Safely call a method on an object"""
        try:
            if hasattr(obj, method_name):
                method = getattr(obj, method_name)
                return method(*args, **kwargs)
        except Exception as e:
            return f"ERROR: {e}"
        return None
    
    def _safe_get_attr(self, obj: Any, attr_name: str) -> Any:
        """Safely get an attribute from an object"""
        try:
            if hasattr(obj, attr_name):
                return getattr(obj, attr_name)
        except Exception as e:
            return f"ERROR: {e}"
        return None
    
    def dump_type_registry(self, registry) -> str:
        """Dump TypeRegistry contents"""
        self._reset_state()
        
        if not self._should_continue(registry):
            return f"{self._dump_object_header(registry, 'TypeRegistry')} [already visited or max depth]"
        
        self.visited.add(id(registry))
        
        lines = []
        lines.append(self._dump_object_header(registry, "TypeRegistry"))
        
        self.current_depth += 1
        
        # Get all types if available
        try:
            if hasattr(registry, 'getTypes'):
                types = self._safe_call(registry, 'getTypes')
                if types and self.config.detail_level.value >= DetailLevel.NORMAL.value:
                    lines.append(f"{self._indent()}Types ({len(types) if hasattr(types, '__len__') else '?'}):")
                    self.current_depth += 1
                    
                    for i, type_obj in enumerate(types):
                        if i >= self.config.max_items:
                            lines.append(f"{self._indent()}... ({len(types) - self.config.max_items} more)")
                            break
                        lines.append(self.dump_type(type_obj))
                    
                    self.current_depth -= 1
        except Exception as e:
            lines.append(f"{self._indent()}Types: ERROR - {e}")
        
        self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_type(self, type_obj) -> str:
        """Dump a Type object"""
        if not self._should_continue(type_obj):
            return f"{self._dump_object_header(type_obj, 'Type')} [already visited or max depth]"
        
        self.visited.add(id(type_obj))
        
        type_name = self._safe_call(type_obj, 'getName') or "Unknown"
        lines = []
        lines.append(self._dump_object_header(type_obj, "Type", type_name))
        
        if self.config.detail_level.value >= DetailLevel.NORMAL.value:
            self.current_depth += 1
            
            # Show parents if requested
            if self.config.show_parents:
                parents = self._safe_call(type_obj, 'getParents')
                if parents:
                    lines.append(f"{self._indent()}Parents:")
                    self.current_depth += 1
                    for parent in parents:
                        parent_name = self._safe_call(parent, 'getName') or "Unknown"
                        lines.append(f"{self._indent()}- {parent_name} {self._get_object_id(parent)}")
                    self.current_depth -= 1
            
            # Show relations if requested
            if self.config.show_relations:
                relations = self._safe_call(type_obj, 'getRelations')
                if relations:
                    lines.append(f"{self._indent()}Relations:")
                    self.current_depth += 1
                    for relation in relations:
                        relation_name = self._safe_call(relation, 'getName') or "Unknown"
                        lines.append(f"{self._indent()}- {relation_name}")
                    self.current_depth -= 1
            
            self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_model(self, model) -> str:
        """Dump Model contents"""
        self._reset_state()
        
        if not self._should_continue(model):
            return f"{self._dump_object_header(model, 'Model')} [already visited or max depth]"
        
        self.visited.add(id(model))
        
        lines = []
        lines.append(self._dump_object_header(model, "Model"))
        
        self.current_depth += 1
        
        # Basic model info
        config = self._safe_call(model, 'getConfig')
        if config:
            lines.append(f"{self._indent()}Config: {self._get_object_id(config)}")
        
        type_registry = self._safe_call(model, 'getTypeRegistry')
        if type_registry:
            lines.append(f"{self._indent()}TypeRegistry: {self._get_object_id(type_registry)}")
            if self.config.detail_level.value >= DetailLevel.DETAILED.value:
                self.current_depth += 1
                lines.append(self.dump_type_registry(type_registry))
                self.current_depth -= 1
        
        # Active neurons if available
        if self.config.detail_level.value >= DetailLevel.NORMAL.value:
            active_neurons = self._safe_call(model, 'getActiveNeurons')
            if active_neurons:
                lines.append(f"{self._indent()}Active Neurons ({len(active_neurons) if hasattr(active_neurons, '__len__') else '?'}):")
                self.current_depth += 1
                
                count = 0
                for neuron in active_neurons:
                    if count >= self.config.max_items:
                        lines.append(f"{self._indent()}... (truncated)")
                        break
                    lines.append(self.dump_neuron(neuron))
                    count += 1
                
                self.current_depth -= 1
        
        self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_context(self, context) -> str:
        """Dump Context contents"""
        self._reset_state()
        
        if not self._should_continue(context):
            return f"{self._dump_object_header(context, 'Context')} [already visited or max depth]"
        
        self.visited.add(id(context))
        
        lines = []
        context_id = self._safe_call(context, 'getId')
        lines.append(self._dump_object_header(context, "Context", f"ID: {context_id}"))
        
        self.current_depth += 1
        
        # Model reference
        model = self._safe_call(context, 'getModel')
        if model:
            lines.append(f"{self._indent()}Model: {self._get_object_id(model)}")
        
        # Activations if requested
        if self.config.show_activations_in_context and self.config.detail_level.value >= DetailLevel.NORMAL.value:
            activations = self._safe_call(context, 'getActivations')
            if activations:
                lines.append(f"{self._indent()}Activations ({len(activations) if hasattr(activations, '__len__') else '?'}):")
                self.current_depth += 1
                
                count = 0
                for activation in activations:
                    if count >= self.config.max_items:
                        lines.append(f"{self._indent()}... (truncated)")
                        break
                    lines.append(self.dump_activation(activation))
                    count += 1
                
                self.current_depth -= 1
        
        # Binding signals if requested
        if self.config.show_binding_signals and self.config.detail_level.value >= DetailLevel.DETAILED.value:
            # Note: Binding signals access might vary by implementation
            lines.append(f"{self._indent()}Binding Signals: (implementation specific)")
        
        self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_neuron(self, neuron) -> str:
        """Dump Neuron contents"""
        if not self._should_continue(neuron):
            return f"{self._dump_object_header(neuron, 'Neuron')} [already visited or max depth]"
        
        self.visited.add(id(neuron))
        
        lines = []
        neuron_id = self._safe_call(neuron, 'getId')
        lines.append(self._dump_object_header(neuron, "Neuron", f"ID: {neuron_id}"))
        
        if self.config.detail_level.value >= DetailLevel.NORMAL.value:
            self.current_depth += 1
            
            # Neuron type
            neuron_type = self._safe_get_attr(neuron, 'type')
            if neuron_type:
                type_name = self._safe_call(neuron_type, 'getName') or "Unknown"
                lines.append(f"{self._indent()}Type: {type_name} {self._get_object_id(neuron_type)}")
            
            # Synapses if requested
            if self.config.show_synapses_in_neuron:
                input_synapses = self._safe_call(neuron, 'getInputSynapses')
                if input_synapses:
                    lines.append(f"{self._indent()}Input Synapses ({len(input_synapses) if hasattr(input_synapses, '__len__') else '?'}):")
                    self.current_depth += 1
                    
                    count = 0
                    for synapse in input_synapses:
                        if count >= self.config.max_items:
                            lines.append(f"{self._indent()}... (truncated)")
                            break
                        lines.append(self.dump_synapse(synapse))
                        count += 1
                    
                    self.current_depth -= 1
                
                output_synapses = self._safe_call(neuron, 'getOutputSynapses')
                if output_synapses:
                    lines.append(f"{self._indent()}Output Synapses ({len(output_synapses) if hasattr(output_synapses, '__len__') else '?'}):")
                    self.current_depth += 1
                    
                    count = 0
                    for synapse in output_synapses:
                        if count >= self.config.max_items:
                            lines.append(f"{self._indent()}... (truncated)")
                            break
                        lines.append(self.dump_synapse(synapse))
                        count += 1
                    
                    self.current_depth -= 1
            
            self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_activation(self, activation) -> str:
        """Dump Activation contents"""
        if not self._should_continue(activation):
            return f"{self._dump_object_header(activation, 'Activation')} [already visited or max depth]"
        
        self.visited.add(id(activation))
        
        lines = []
        activation_id = self._safe_call(activation, 'getId')
        lines.append(self._dump_object_header(activation, "Activation", f"ID: {activation_id}"))
        
        if self.config.detail_level.value >= DetailLevel.NORMAL.value:
            self.current_depth += 1
            
            # Neuron reference
            neuron = self._safe_call(activation, 'getNeuron')
            if neuron:
                neuron_id = self._safe_call(neuron, 'getId')
                lines.append(f"{self._indent()}Neuron: {neuron_id} {self._get_object_id(neuron)}")
            
            # Activation state
            fired = self._safe_call(activation, 'getFired')
            if fired is not None:
                lines.append(f"{self._indent()}Fired: {fired}")
            
            created = self._safe_call(activation, 'getCreated')
            if created is not None:
                lines.append(f"{self._indent()}Created: {created}")
            
            # Fields if requested
            if self.config.show_fields and self.config.detail_level.value >= DetailLevel.DETAILED.value:
                lines.append(f"{self._indent()}Fields:")
                self.current_depth += 1
                
                # Try to get field values (implementation specific)
                if self.config.show_field_values:
                    lines.append(f"{self._indent()}(Field values require specific field access)")
                else:
                    lines.append(f"{self._indent()}(Field definitions from type)")
                
                self.current_depth -= 1
            
            # Links if requested
            if self.config.show_links_in_activation:
                input_links = self._safe_call(activation, 'getInputLinks')
                if input_links:
                    lines.append(f"{self._indent()}Input Links ({len(input_links) if hasattr(input_links, '__len__') else '?'}):")
                    self.current_depth += 1
                    
                    count = 0
                    for link in input_links:
                        if count >= self.config.max_items:
                            lines.append(f"{self._indent()}... (truncated)")
                            break
                        lines.append(self.dump_link(link))
                        count += 1
                    
                    self.current_depth -= 1
                
                output_links = self._safe_call(activation, 'getOutputLinks')
                if output_links:
                    lines.append(f"{self._indent()}Output Links ({len(output_links) if hasattr(output_links, '__len__') else '?'}):")
                    self.current_depth += 1
                    
                    count = 0
                    for link in output_links:
                        if count >= self.config.max_items:
                            lines.append(f"{self._indent()}... (truncated)")
                            break
                        lines.append(self.dump_link(link))
                        count += 1
                    
                    self.current_depth -= 1
            
            # Binding signals if requested
            if self.config.show_binding_signals:
                binding_signals = self._safe_call(activation, 'getBindingSignals')
                if binding_signals:
                    lines.append(f"{self._indent()}Binding Signals ({len(binding_signals) if hasattr(binding_signals, '__len__') else '?'}):")
                    self.current_depth += 1
                    
                    for signal in binding_signals:
                        token_id = self._safe_call(signal, 'getTokenId')
                        lines.append(f"{self._indent()}Token {token_id}: {self._get_object_id(signal)}")
                    
                    self.current_depth -= 1
            
            self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_synapse(self, synapse) -> str:
        """Dump Synapse contents"""
        if not self._should_continue(synapse):
            return f"{self._dump_object_header(synapse, 'Synapse')} [already visited or max depth]"
        
        self.visited.add(id(synapse))
        
        lines = []
        synapse_id = self._safe_call(synapse, 'getSynapseId')
        lines.append(self._dump_object_header(synapse, "Synapse", f"ID: {synapse_id}"))
        
        if self.config.detail_level.value >= DetailLevel.NORMAL.value:
            self.current_depth += 1
            
            # Synapse type
            synapse_type = self._safe_get_attr(synapse, 'type')
            if synapse_type:
                type_name = self._safe_call(synapse_type, 'getName') or "Unknown"
                lines.append(f"{self._indent()}Type: {type_name} {self._get_object_id(synapse_type)}")
                
                # Pairing info if detailed
                if self.config.detail_level.value >= DetailLevel.DETAILED.value:
                    pairing_config = self._safe_call(synapse_type, 'getPairingConfig')
                    if pairing_config:
                        pairing_type = self._safe_get_attr(pairing_config, 'type')
                        lines.append(f"{self._indent()}Pairing: {pairing_type}")
            
            # Input/Output neurons
            input_neuron = self._safe_call(synapse, 'getInput')
            if input_neuron:
                input_id = self._safe_call(input_neuron, 'getId')
                lines.append(f"{self._indent()}Input: Neuron {input_id} {self._get_object_id(input_neuron)}")
            
            output_neuron = self._safe_call(synapse, 'getOutput')
            if output_neuron:
                output_id = self._safe_call(output_neuron, 'getId')
                lines.append(f"{self._indent()}Output: Neuron {output_id} {self._get_object_id(output_neuron)}")
            
            self.current_depth -= 1
        
        return "\n".join(lines)
    
    def dump_link(self, link) -> str:
        """Dump Link contents"""
        if not self._should_continue(link):
            return f"{self._dump_object_header(link, 'Link')} [already visited or max depth]"
        
        self.visited.add(id(link))
        
        lines = []
        lines.append(self._dump_object_header(link, "Link"))
        
        if self.config.detail_level.value >= DetailLevel.NORMAL.value:
            self.current_depth += 1
            
            # Link type
            link_type = self._safe_get_attr(link, 'type')
            if link_type:
                type_name = self._safe_call(link_type, 'getName') or "Unknown"
                lines.append(f"{self._indent()}Type: {type_name} {self._get_object_id(link_type)}")
            
            # Synapse
            synapse = self._safe_call(link, 'getSynapse')
            if synapse:
                synapse_id = self._safe_call(synapse, 'getSynapseId')
                lines.append(f"{self._indent()}Synapse: {synapse_id} {self._get_object_id(synapse)}")
            
            # Input/Output activations
            input_activation = self._safe_call(link, 'getInput')
            if input_activation:
                input_id = self._safe_call(input_activation, 'getId')
                lines.append(f"{self._indent()}Input: Activation {input_id} {self._get_object_id(input_activation)}")
            
            output_activation = self._safe_call(link, 'getOutput')
            if output_activation:
                output_id = self._safe_call(output_activation, 'getId')
                lines.append(f"{self._indent()}Output: Activation {output_id} {self._get_object_id(output_activation)}")
            
            # State
            fired = self._safe_call(link, 'getFired')
            if fired is not None:
                lines.append(f"{self._indent()}Fired: {fired}")
            
            created = self._safe_call(link, 'getCreated')
            if created is not None:
                lines.append(f"{self._indent()}Created: {created}")
            
            self.current_depth -= 1
        
        return "\n".join(lines)

# Convenience functions for quick debugging
def dump_object(obj, detail_level: DetailLevel = DetailLevel.NORMAL, max_depth: int = 3) -> str:
    """Quick dump of any AIKA object"""
    config = DebugConfig(detail_level=detail_level, max_depth=max_depth)
    debugger = AikaDebugger(config)
    
    # Determine object type and call appropriate dump method
    obj_type = type(obj).__name__
    
    if 'TypeRegistry' in obj_type:
        return debugger.dump_type_registry(obj)
    elif 'Model' in obj_type:
        return debugger.dump_model(obj)
    elif 'Context' in obj_type:
        return debugger.dump_context(obj)
    elif 'Neuron' in obj_type:
        return debugger.dump_neuron(obj)
    elif 'Activation' in obj_type:
        return debugger.dump_activation(obj)
    elif 'Synapse' in obj_type:
        return debugger.dump_synapse(obj)
    elif 'Link' in obj_type:
        return debugger.dump_link(obj)
    elif 'Type' in obj_type:
        return debugger.dump_type(obj)
    else:
        return f"Unknown object type: {obj_type}"

def quick_dump(obj) -> str:
    """Very quick minimal dump"""
    return dump_object(obj, DetailLevel.MINIMAL, max_depth=1)

def verbose_dump(obj) -> str:
    """Verbose dump with all details"""
    return dump_object(obj, DetailLevel.VERBOSE, max_depth=5)

# Example usage function
def example_usage():
    """Example of how to use the debug utilities"""
    from python.networks.transformer import create_transformer_types
    
    print("=== AIKA Debug Utilities Example ===")
    
    # Create a transformer registry for demonstration
    transformer = create_transformer_types()
    
    # Different detail levels
    print("\n1. Quick dump of type registry:")
    print(quick_dump(transformer.get_registry()))
    
    print("\n2. Normal dump with custom config:")
    config = DebugConfig(
        detail_level=DetailLevel.DETAILED,
        max_depth=2,
        show_fields=True,
        show_relations=True
    )
    debugger = AikaDebugger(config)
    print(debugger.dump_type_registry(transformer.get_registry()))
    
    print("\n3. Verbose dump (truncated):")
    verbose_output = verbose_dump(transformer.get_registry())
    lines = verbose_output.split('\n')
    if len(lines) > 50:
        print('\n'.join(lines[:50]))
        print(f"... (truncated {len(lines) - 50} more lines)")
    else:
        print(verbose_output)

if __name__ == "__main__":
    example_usage()