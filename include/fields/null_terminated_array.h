#ifndef NULL_TERMINATED_ARRAY_H
#define NULL_TERMINATED_ARRAY_H

#include <vector>

// Static function to convert a vector to a null-terminated array
template <typename T>
T* nullTerminatedArrayFromVector(const std::vector<T>& vec) {
    // Allocate memory for the array with one extra slot for nullptr
    T* arr = new T[vec.size() + 1];

    // Copy elements from vector to array
    for (size_t i = 0; i < vec.size(); ++i) {
        arr[i] = vec[i];
    }

    // Null-terminate the array
    arr[vec.size()] = nullptr;

    return arr;
}

#endif // NULL_TERMINATED_ARRAY_H
