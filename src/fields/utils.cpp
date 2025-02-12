
#include "fields/utils.h"

int convert(double newSortValue) {
    return static_cast<int>(PRECISION * newSortValue);  // Equivalent of type casting in Java
}
