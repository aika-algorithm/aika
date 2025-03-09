import unittest
import sys
import os

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

import aika

class MyTestCase(unittest.TestCase):
    def test_something(self):
        self.assertEqual(True, False)  # add assertion here

    def testSubtraction(self):
        print("Module 'aika' was loaded from:", aika.__file__)

        TEST_RELATION_FROM = aika.RelationOne(1, "TEST_FROM")
        TEST_RELATION_TO = aika.RelationOne(2, "TEST_TO")
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

        assert isinstance(TEST_RELATION_FROM, aika.Relation)
        assert isinstance(TEST_RELATION_TO, aika.Relation)

        registry = aika.TypeRegistry()

        typeA = aika.TestType(registry, "A")
        typeB = aika.TestType(registry, "B")

        a = typeA.inputField("a")
        b = typeB.inputField("b")

        c = typeB.sub("c")

        print("Type of c:", type(c))
        print("Type of TEST_RELATION_FROM:", type(TEST_RELATION_FROM))
        print("Type of a:", type(a))

        assert isinstance(a, aika.FieldDefinition)
        assert isinstance(c, aika.FieldDefinition)

        c.input(TEST_RELATION_FROM, a, 0)
        c.input(TEST_RELATION_FROM, b, 1)

        registry.flattenTypeHierarchy()

        oa = typeA.instantiate()
        ob = typeB.instantiate()


if __name__ == '__main__':
    unittest.main()
