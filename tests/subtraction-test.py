import unittest
import aika

class MyTestCase(unittest.TestCase):
    def test_something(self):
        self.assertEqual(True, False)  # add assertion here

    def testSubtraction(self):
        registry = aika.TypeRegistry()

        typeA = aika.Type(registry, "A")
        typeB = aika.Type(registry, "B")

        a = typeA.inputField("a")
        b = typeB.inputField("b")

        c = typeB.sub("c")
        c.in(TEST_RELATION_FROM, a, 0)
        c.in(TEST_RELATION_FROM, b, 1)

        registry.flattenTypeHierarchy()

        oa = typeA.instantiate()
        ob = typeB.instantiate()

        if(linkingPos == 0)
            linkObjects(oa, ob)
            ob.initFields()

        oa.setFieldValue(a, 50.0)

        if(linkingPos == 1)
            linkObjects(oa, ob)
            ob.initFields()

        oa.setFieldValue(b, 20.0)

        if(linkingPos == 2)
            linkObjects(oa, ob)
            ob.initFields()

        Assertions.assertEquals(30.0, ob.getFieldOutput(c).getValue())

if __name__ == '__main__':
    unittest.main()
