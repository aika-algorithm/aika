import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika

class SumTestCase(unittest.TestCase):

    def testSum(self):
        print("Module 'aika' was loaded from:", aika.__file__)

        TEST_RELATION_FROM = aika.RelationOne(1, "TEST_FROM")
        TEST_RELATION_TO = aika.RelationOne(2, "TEST_TO")
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

        registry = aika.TypeRegistry()

        typeA = aika.TestType(registry, "A")
        typeB = aika.TestType(registry, "B")

        a = typeA.inputField("a")
        b = typeA.inputField("b")

        c = typeB.add("c")

        c.input(TEST_RELATION_FROM, a, 0)
        c.input(TEST_RELATION_FROM, b, 1)

        registry.flattenTypeHierarchy()

        oa = typeA.instantiate()
        ob = typeB.instantiate()

        aika.TestObj.linkObjects(oa, ob)
        ob.initFields()

        oa.setFieldValue(a, 50.0)
        oa.setFieldValue(b, 20.0)

        print("ob.getFieldValue(c):", ob.getFieldValue(c))

        self.assertEqual(70.0, ob.getFieldValue(c))

if __name__ == '__main__':
    unittest.main() 