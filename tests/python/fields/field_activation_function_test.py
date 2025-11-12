import unittest

import aika

class FieldActivationFunctionTestCase(unittest.TestCase):

    def testFieldActivationFunction(self):
        print("Module 'aika' was loaded from:", aika.__file__)

        TEST_RELATION_FROM = aika.fields.RelationOne(1, "TEST_FROM")
        TEST_RELATION_TO = aika.fields.RelationOne(2, "TEST_TO")
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

        registry = aika.fields.TypeRegistry()

        typeA = aika.fields.TestType(registry, "A")
        typeB = aika.fields.TestType(registry, "B")

        # Input field for raw input value
        input_val = typeA.inputField("input_val")

        # Create a sigmoid activation function
        sigmoid_func = aika.fields.SigmoidActivationFunction()
        
        # Create field activation function with sigmoid and tolerance
        sigmoid_field = typeB.fieldActivationFunc("sigmoid_output", sigmoid_func, 0.001)

        # Connect input to the sigmoid field
        sigmoid_field.input(TEST_RELATION_FROM, input_val, 0)

        registry.flattenTypeHierarchy()

        oa = typeA.instantiate()
        ob = typeB.instantiate()

        aika.fields.TestObj.linkObjects(oa, ob)
        ob.initFields()

        # Test sigmoid activation with input value 0 (should give ~0.5)
        oa.setFieldValue(input_val, 0.0)
        result = ob.getFieldValue(sigmoid_field)
        print(f"Sigmoid(0.0): {result}")
        self.assertAlmostEqual(result, 0.5, places=3)

        # Test sigmoid activation with input value 1 (should give ~0.731) 
        oa.setFieldValue(input_val, 1.0)
        result = ob.getFieldValue(sigmoid_field)
        print(f"Sigmoid(1.0): {result}")
        self.assertAlmostEqual(result, 0.731, places=3)

        # Test sigmoid activation with input value -1 (should give ~0.269)
        oa.setFieldValue(input_val, -1.0)
        result = ob.getFieldValue(sigmoid_field)
        print(f"Sigmoid(-1.0): {result}")
        self.assertAlmostEqual(result, 0.269, places=3)
        
        print("✅ Field activation function with Sigmoid works correctly!")

if __name__ == '__main__':
    unittest.main() 