#include "network/utils.h"

std::vector<double> Utils::add(const std::vector<double>& a, const std::vector<double>& b) {
    std::vector<double> result;
    for (size_t i = 0; i < a.size(); ++i) {
        result.push_back(a[i] + b[i]);
    }
    return result;
}

std::vector<double> Utils::scale(const std::vector<double>& a, double s) {
    std::vector<double> result;
    for (double val : a) {
        result.push_back(val * s);
    }
    return result;
}

double Utils::sum(const std::vector<double>& a) {
    double total = 0.0;
    for (double val : a) {
        total += val;
    }
    return total;
}

bool Utils::belowTolerance(double tolerance, const std::vector<double>& x) {
    for (double val : x) {
        if (val >= tolerance) {
            return false;
        }
    }
    return true;
}

bool Utils::belowTolerance(double tolerance, double x) {
    return x < tolerance;
}

std::string Utils::depthToSpace(int depth) {
    return std::string(depth, ' ');
}

std::string Utils::idToString(Activation* act) {
    return "Activation ID: " + std::to_string(act->getId());
} 