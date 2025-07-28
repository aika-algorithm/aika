import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

import aika

class ExponentialFunctionTestCase(unittest.TestCase):

    def testExponentialFunction(self):
        print("Module 'aika' was loaded from:", aika.__file__)

        TEST_RELATION_FROM = aika.fields.RelationOne(1, "TEST_FROM")
        TEST_RELATION_TO = aika.fields.RelationOne(2, "TEST_TO")
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

        registry = aika.fields.TypeRegistry()

        typeA = aika.fields.TestType(registry, "A")
        typeB = aika.fields.TestType(registry, "B")

        a = typeA.inputField("a")

        exp_func = typeB.exp("exp_func")

        exp_func.input(TEST_RELATION_FROM, a, 0)

        registry.flattenTypeHierarchy()

        oa = typeA.instantiate()
        ob = typeB.instantiate()

        aika.fields.TestObj.linkObjects(oa, ob)
        ob.initFields()

        oa.setFieldValue(a, 5.0)  

        expected_value = 148.413159102576603
        actual_value = ob.getFieldValue(exp_func)
        print("ob.getFieldValue(exp_func):", actual_value)

        self.assertAlmostEqual(expected_value, actual_value, places=5)

if __name__ == '__main__':
    unittest.main() 