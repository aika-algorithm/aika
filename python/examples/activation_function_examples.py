"""
AIKA Activation Function Examples

This module demonstrates how to use different activation functions (Sigmoid, Tanh, ReLU, etc.)
in Python network configurations using the AIKA framework.
"""



import aika


class ActivationFunctionExamples:
    """Examples of using different activation functions in AIKA networks"""
    
    def __init__(self):
        self.registry = aika.fields.TypeRegistry()
        self._setup_relations()
        self._setup_types()
    
    def _setup_relations(self):
        """Setup test relations between objects"""
        self.INPUT_RELATION = aika.fields.RelationOne(1, "INPUT")
        self.OUTPUT_RELATION = aika.fields.RelationOne(2, "OUTPUT")
        self.OUTPUT_RELATION.setReversed(self.INPUT_RELATION)
        self.INPUT_RELATION.setReversed(self.OUTPUT_RELATION)
    
    def _setup_types(self):
        """Setup input and output types"""
        self.input_type = aika.fields.TestType(self.registry, "InputType")
        self.output_type = aika.fields.TestType(self.registry, "OutputType")
        
        # Input field
        self.input_field = self.input_type.inputField("input_value")
    
    def create_sigmoid_network(self):
        """Create a network with Sigmoid activation function"""
        print("🔄 Creating Sigmoid Network...")
        
        # Create sigmoid activation function
        sigmoid_func = aika.fields.SigmoidActivationFunction()
        
        # Create field with sigmoid activation
        sigmoid_output = self.output_type.fieldActivationFunc("sigmoid_out", sigmoid_func, 0.001)
        sigmoid_output.input(self.INPUT_RELATION, self.input_field, 0)
        
        self.registry.flattenTypeHierarchy()
        
        # Create objects
        input_obj = self.input_type.instantiate()
        output_obj = self.output_type.instantiate()
        
        aika.fields.TestObj.linkObjects(input_obj, output_obj)
        output_obj.initFields()
        
        # Test different input values
        test_values = [-2.0, -1.0, 0.0, 1.0, 2.0]
        print("Input → Sigmoid Output:")
        for val in test_values:
            input_obj.setFieldValue(self.input_field, val)
            result = output_obj.getFieldValue(sigmoid_output)
            print(f"  {val:4.1f} → {result:.6f}")
        
        return input_obj, output_obj, sigmoid_output
    
    def create_tanh_network(self):
        """Create a network with Tanh activation function"""
        print("\n🔄 Creating Tanh Network...")
        
        # Create new types for tanh network
        tanh_output_type = aika.fields.TestType(self.registry, "TanhOutputType")
        
        # Create tanh activation function
        tanh_func = aika.fields.TanhActivationFunction()
        
        # Create field with tanh activation
        tanh_output = tanh_output_type.fieldActivationFunc("tanh_out", tanh_func, 0.001)
        tanh_output.input(self.INPUT_RELATION, self.input_field, 0)
        
        self.registry.flattenTypeHierarchy()
        
        # Create objects
        input_obj = self.input_type.instantiate()
        output_obj = tanh_output_type.instantiate()
        
        aika.fields.TestObj.linkObjects(input_obj, output_obj)
        output_obj.initFields()
        
        # Test different input values
        test_values = [-2.0, -1.0, 0.0, 1.0, 2.0]
        print("Input → Tanh Output:")
        for val in test_values:
            input_obj.setFieldValue(self.input_field, val)
            result = output_obj.getFieldValue(tanh_output)
            print(f"  {val:4.1f} → {result:.6f}")
        
        return input_obj, output_obj, tanh_output
    
    def create_relu_network(self):
        """Create a network with ReLU activation function"""
        print("\n🔄 Creating ReLU Network...")
        
        # Create new types for ReLU network
        relu_output_type = aika.fields.TestType(self.registry, "ReLUOutputType")
        
        # Create ReLU activation function
        relu_func = aika.fields.ReLUActivationFunction()
        
        # Create field with ReLU activation
        relu_output = relu_output_type.fieldActivationFunc("relu_out", relu_func, 0.001)
        relu_output.input(self.INPUT_RELATION, self.input_field, 0)
        
        self.registry.flattenTypeHierarchy()
        
        # Create objects
        input_obj = self.input_type.instantiate()
        output_obj = relu_output_type.instantiate()
        
        aika.fields.TestObj.linkObjects(input_obj, output_obj)
        output_obj.initFields()
        
        # Test different input values
        test_values = [-2.0, -1.0, 0.0, 1.0, 2.0]
        print("Input → ReLU Output:")
        for val in test_values:
            input_obj.setFieldValue(self.input_field, val)
            result = output_obj.getFieldValue(relu_output)
            print(f"  {val:4.1f} → {result:.6f}")
        
        return input_obj, output_obj, relu_output
    
    def create_leaky_relu_network(self, alpha=0.01):
        """Create a network with Leaky ReLU activation function"""
        print(f"\n🔄 Creating Leaky ReLU Network (alpha={alpha})...")
        
        # Create new types for Leaky ReLU network
        leaky_relu_output_type = aika.fields.TestType(self.registry, f"LeakyReLUOutputType_{alpha}")
        
        # Create Leaky ReLU activation function with custom alpha
        leaky_relu_func = aika.fields.LeakyReLUActivationFunction(alpha)
        
        # Create field with Leaky ReLU activation
        leaky_relu_output = leaky_relu_output_type.fieldActivationFunc("leaky_relu_out", leaky_relu_func, 0.001)
        leaky_relu_output.input(self.INPUT_RELATION, self.input_field, 0)
        
        self.registry.flattenTypeHierarchy()
        
        # Create objects
        input_obj = self.input_type.instantiate()
        output_obj = leaky_relu_output_type.instantiate()
        
        aika.fields.TestObj.linkObjects(input_obj, output_obj)
        output_obj.initFields()
        
        # Test different input values
        test_values = [-2.0, -1.0, 0.0, 1.0, 2.0]
        print(f"Input → Leaky ReLU (α={alpha}) Output:")
        for val in test_values:
            input_obj.setFieldValue(self.input_field, val)
            result = output_obj.getFieldValue(leaky_relu_output)
            print(f"  {val:4.1f} → {result:.6f}")
        
        return input_obj, output_obj, leaky_relu_output
    
    def create_linear_network(self):
        """Create a network with Linear activation function"""
        print("\n🔄 Creating Linear Network...")
        
        # Create new types for Linear network
        linear_output_type = aika.fields.TestType(self.registry, "LinearOutputType")
        
        # Create Linear activation function
        linear_func = aika.fields.LinearActivationFunction()
        
        # Create field with Linear activation
        linear_output = linear_output_type.fieldActivationFunc("linear_out", linear_func, 0.001)
        linear_output.input(self.INPUT_RELATION, self.input_field, 0)
        
        self.registry.flattenTypeHierarchy()
        
        # Create objects
        input_obj = self.input_type.instantiate()
        output_obj = linear_output_type.instantiate()
        
        aika.fields.TestObj.linkObjects(input_obj, output_obj)
        output_obj.initFields()
        
        # Test different input values
        test_values = [-2.0, -1.0, 0.0, 1.0, 2.0]
        print("Input → Linear Output:")
        for val in test_values:
            input_obj.setFieldValue(self.input_field, val)
            result = output_obj.getFieldValue(linear_output)
            print(f"  {val:4.1f} → {result:.6f}")
        
        return input_obj, output_obj, linear_output


def main():
    """Demonstrate different activation functions in AIKA"""
    print("🧠 AIKA Activation Function Examples")
    print("=" * 50)
    
    examples = ActivationFunctionExamples()
    
    # Demonstrate different activation functions
    examples.create_sigmoid_network()
    examples.create_tanh_network()
    examples.create_relu_network()
    examples.create_leaky_relu_network(0.01)  # Default alpha
    examples.create_leaky_relu_network(0.1)   # Custom alpha
    examples.create_linear_network()
    
    print("\n✅ All activation function examples completed successfully!")
    print("\n📝 Usage Summary:")
    print("• Sigmoid: Good for binary classification, outputs in (0,1)")
    print("• Tanh: Good for hidden layers, outputs in (-1,1), zero-centered")
    print("• ReLU: Most popular for hidden layers, fast, addresses vanishing gradient")
    print("• Leaky ReLU: Like ReLU but allows small negative values")
    print("• Linear: No activation, direct pass-through (useful for regression output)")


if __name__ == "__main__":
    main()