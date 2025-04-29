#ifndef NETWORK_UTILS_H
#define NETWORK_UTILS_H

#include "network/activation.h"

#include <vector>
#include <string>

class Utils {
public:
    static constexpr double TOLERANCE = 0.001;

    static std::vector<double> add(const std::vector<double>& a, const std::vector<double>& b);
    static std::vector<double> scale(const std::vector<double>& a, double s);
    static double sum(const std::vector<double>& a);
    static bool belowTolerance(double tolerance, const std::vector<double>& x);
    static bool belowTolerance(double tolerance, double x);
    static std::string depthToSpace(int depth);
    static std::string idToString(Activation* act);
};

#endif // NETWORK_UTILS_H 