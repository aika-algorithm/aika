import unittest
import sys
import os
from parameterized import parameterized

# Add the project root to Python's module search path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "../..")))

import aika

class DivisionTestCase(unittest.TestCase):

    @parameterized.expand([
        (0, "linking_pos_0"),
        (1, "linking_pos_1"),
        (2, "linking_pos_2")
    ])
    def testDivision(self, linking_pos, test_name):
        print("Module 'aika' was loaded from:", aika.__file__)

        TEST_RELATION_FROM = aika.fields.RelationOne(1, "TEST_FROM")
        TEST_RELATION_TO = aika.fields.RelationOne(2, "TEST_TO")
        TEST_RELATION_TO.setReversed(TEST_RELATION_FROM)
        TEST_RELATION_FROM.setReversed(TEST_RELATION_TO)

        assert isinstance(TEST_RELATION_FROM, aika.fields.Relation)
        assert isinstance(TEST_RELATION_TO, aika.fields.Relation)

        registry = aika.fields.TypeRegistry()

        typeA = aika.fields.TestType(registry, "A")
        typeB = aika.fields.TestType(registry, "B")

        a = typeA.inputField("a")
        b = typeA.inputField("b")

        c = typeB.div("c")

        print("Type of c:", type(c))
        print("Type of TEST_RELATION_FROM:", type(TEST_RELATION_FROM))
        print("Type of a:", type(a))

        assert isinstance(a, aika.fields.FieldDefinition)
        assert isinstance(c, aika.fields.FieldDefinition)

        c.input(TEST_RELATION_FROM, a, 0)
        c.input(TEST_RELATION_FROM, b, 1)

        registry.flattenTypeHierarchy()

        oa = typeA.instantiate()
        ob = typeB.instantiate()

        print("linking_pos:", linking_pos)

        if linking_pos == 0:
            aika.fields.TestObj.linkObjects(oa, ob)
            ob.initFields()

        oa.setFieldValue(a, 50.0)
        print("oa.getFieldValue(a):", oa.getFieldValue(a))

        if linking_pos == 1:
            aika.fields.TestObj.linkObjects(oa, ob)
            ob.initFields()

        oa.setFieldValue(b, 2.0)
        print("oa.getFieldValue(b):", oa.getFieldValue(b))

        if linking_pos == 2:
            aika.fields.TestObj.linkObjects(oa, ob)
            ob.initFields()

        print("ob.getFieldValue(c):", ob.getFieldValue(c))

        print("ob.getFieldAsString(c):", ob.getFieldAsString(c))

        self.assertEqual(25.0, ob.getFieldValue(c))

if __name__ == '__main__':
    unittest.main() 