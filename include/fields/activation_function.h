#ifndef ACTIVATION_FUNCTION_H
#define ACTIVATION_FUNCTION_H

class ActivationFunction {
public:
    virtual ~ActivationFunction() = default;

    virtual double f(double x) = 0;
    virtual double outerGrad(double x) = 0;
};

#endif // ACTIVATION_FUNCTION_H 