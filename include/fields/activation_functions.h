#ifndef ACTIVATION_FUNCTIONS_H
#define ACTIVATION_FUNCTIONS_H

#include "fields/activation_function.h"
#include <cmath>
#include <algorithm>

/**
 * Sigmoid activation function: f(x) = 1 / (1 + exp(-x))
 * Gradient: f'(x) = f(x) * (1 - f(x))
 */
class SigmoidActivationFunction : public ActivationFunction {
public:
    double f(double x) override {
        // Clamp x to prevent overflow
        if (x > 500.0) return 1.0;
        if (x < -500.0) return 0.0;
        return 1.0 / (1.0 + std::exp(-x));
    }
    
    double outerGrad(double x) override {
        double sigmoid_x = f(x);
        return sigmoid_x * (1.0 - sigmoid_x);
    }
};

/**
 * Hyperbolic tangent activation function: f(x) = tanh(x)
 * Gradient: f'(x) = 1 - tanh²(x)
 */
class TanhActivationFunction : public ActivationFunction {
public:
    double f(double x) override {
        return std::tanh(x);
    }
    
    double outerGrad(double x) override {
        double tanh_x = std::tanh(x);
        return 1.0 - tanh_x * tanh_x;
    }
};

/**
 * Rectified Linear Unit activation function: f(x) = max(0, x)
 * Gradient: f'(x) = 1 if x > 0, else 0
 */
class ReLUActivationFunction : public ActivationFunction {
public:
    double f(double x) override {
        return std::max(0.0, x);
    }
    
    double outerGrad(double x) override {
        return x > 0.0 ? 1.0 : 0.0;
    }
};

/**
 * Leaky ReLU activation function: f(x) = max(alpha * x, x)
 * Gradient: f'(x) = 1 if x > 0, else alpha
 */
class LeakyReLUActivationFunction : public ActivationFunction {
private:
    double alpha;
    
public:
    LeakyReLUActivationFunction(double alpha = 0.01) : alpha(alpha) {}
    
    double f(double x) override {
        return x > 0.0 ? x : alpha * x;
    }
    
    double outerGrad(double x) override {
        return x > 0.0 ? 1.0 : alpha;
    }
    
    double getAlpha() const { return alpha; }
    void setAlpha(double a) { alpha = a; }
};

/**
 * Linear activation function: f(x) = x
 * Gradient: f'(x) = 1
 */
class LinearActivationFunction : public ActivationFunction {
public:
    double f(double x) override {
        return x;
    }
    
    double outerGrad(double x) override {
        return 1.0;
    }
};

#endif // ACTIVATION_FUNCTIONS_H