import unittest

import aika

class ActivationFunctionSimpleTestCase(unittest.TestCase):
    """Simple test to verify activation functions work correctly"""

    def test_activation_functions(self):
        print("Module 'aika' was loaded from:", aika.__file__)
        
        # Test Sigmoid activation function
        sigmoid = aika.fields.SigmoidActivationFunction()
        self.assertAlmostEqual(sigmoid.f(0.0), 0.5, places=5)
        self.assertAlmostEqual(sigmoid.f(1.0), 0.7310585786300049, places=5)
        self.assertAlmostEqual(sigmoid.f(-1.0), 0.2689414213699951, places=5)
        
        # Test gradient
        self.assertAlmostEqual(sigmoid.outerGrad(0.0), 0.25, places=5)  # sigmoid(0) * (1 - sigmoid(0)) = 0.5 * 0.5 = 0.25
        
        # Test Tanh activation function
        tanh = aika.fields.TanhActivationFunction()
        self.assertAlmostEqual(tanh.f(0.0), 0.0, places=5)
        self.assertAlmostEqual(tanh.f(1.0), 0.7615941559557649, places=5)
        self.assertAlmostEqual(tanh.f(-1.0), -0.7615941559557649, places=5)
        
        # Test gradient: 1 - tanh²(x)
        self.assertAlmostEqual(tanh.outerGrad(0.0), 1.0, places=5)  # 1 - tanh²(0) = 1 - 0² = 1
        
        # Test ReLU activation function
        relu = aika.fields.ReLUActivationFunction()
        self.assertEqual(relu.f(0.0), 0.0)
        self.assertEqual(relu.f(1.0), 1.0)
        self.assertEqual(relu.f(-1.0), 0.0)
        self.assertEqual(relu.f(5.5), 5.5)
        
        # Test gradient
        self.assertEqual(relu.outerGrad(1.0), 1.0)  # x > 0 -> 1
        self.assertEqual(relu.outerGrad(-1.0), 0.0)  # x <= 0 -> 0
        self.assertEqual(relu.outerGrad(0.0), 0.0)  # x <= 0 -> 0
        
        # Test Leaky ReLU activation function
        leaky_relu = aika.fields.LeakyReLUActivationFunction()
        self.assertEqual(leaky_relu.f(1.0), 1.0)
        self.assertEqual(leaky_relu.f(-1.0), -0.01)  # default alpha = 0.01
        self.assertEqual(leaky_relu.getAlpha(), 0.01)
        
        # Test custom alpha
        leaky_relu_custom = aika.fields.LeakyReLUActivationFunction(0.1)
        self.assertEqual(leaky_relu_custom.f(-1.0), -0.1)
        self.assertEqual(leaky_relu_custom.getAlpha(), 0.1)
        
        # Test Linear activation function
        linear = aika.fields.LinearActivationFunction()
        self.assertEqual(linear.f(0.0), 0.0)
        self.assertEqual(linear.f(1.0), 1.0)
        self.assertEqual(linear.f(-1.0), -1.0)
        self.assertEqual(linear.f(3.14), 3.14)
        
        # Test gradient (always 1)
        self.assertEqual(linear.outerGrad(0.0), 1.0)
        self.assertEqual(linear.outerGrad(100.0), 1.0)
        self.assertEqual(linear.outerGrad(-100.0), 1.0)
        
        print("✅ All activation functions work correctly!")

if __name__ == '__main__':
    unittest.main()