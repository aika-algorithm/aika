"""
Simple test case for ProxyField basic functionality.
"""

import unittest
import sys
sys.path.insert(0, '.venv/lib/python3.12/site-packages')

from aika import fields as aika


class ProxyFieldSimpleTest(unittest.TestCase):

    def test_proxy_field_creation(self):
        """Test that a ProxyField can be created."""
        registry = aika.TypeRegistry()

        # Create a type with a real field
        base_type = aika.TestType(registry, "BaseType")
        real_field = base_type.inputField("real_field")

        # Create a proxy field that references the real field
        proxy_field = aika.ProxyField(base_type, "proxy_field", real_field)

        # Verify the proxy field was created
        self.assertIsNotNone(proxy_field)
        print("Proxy field created successfully")

    def test_proxy_field_properties(self):
        """Test ProxyField properties."""
        registry = aika.TypeRegistry()

        # Create a type with a real field
        base_type = aika.TestType(registry, "BaseType")
        real_field = base_type.inputField("real_field")

        # Create a proxy field
        proxy_field = aika.ProxyField(base_type, "proxy_field", real_field)

        # Test isProxy
        self.assertTrue(proxy_field.isProxy())
        print("isProxy() returned True")

        # Test getTargetField
        target = proxy_field.getTargetField()
        self.assertEqual(target, real_field)
        print(f"getTargetField() returned correct target")

    def test_flattening_excludes_proxy(self):
        """Test that flattening excludes proxy fields from input side."""
        registry = aika.TypeRegistry()

        # Create a type
        test_type = aika.TestType(registry, "TestType")

        # Add a real field
        real_field = test_type.inputField("real_field")

        # Add a proxy field
        proxy_field = aika.ProxyField(test_type, "proxy_field", real_field)

        # Flatten
        print("Flattening type hierarchy...")
        registry.flattenTypeHierarchy()
        print("Flattening completed")

        # Check the number of fields
        flattened_input = test_type.getFlattenedTypeInputSide()
        num_fields = flattened_input.getNumberOfFields()

        print(f"Number of fields in input side: {num_fields}")

        # Should only have 1 field (the real one, proxy excluded)
        self.assertEqual(num_fields, 1)


if __name__ == '__main__':
    unittest.main()