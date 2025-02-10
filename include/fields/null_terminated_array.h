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

template <typename T>
class NullTerminatedIterator {
public:
    using ValueType = T;
    using Pointer = T*;
    using Reference = T&;

    NullTerminatedIterator(Pointer ptr) : ptr_(ptr) {}

    // Dereference operator
    Reference operator*() const {
        return *ptr_;
    }

    // Arrow operator
    Pointer operator->() const {
        return ptr_;
    }

    // Pre-increment operator
    NullTerminatedIterator& operator++() {
        ++ptr_;
        return *this;
    }

    // Post-increment operator
    NullTerminatedIterator operator++(int) {
        NullTerminatedIterator temp = *this;
        ++(*this);
        return temp;
    }

    // Equality operator
    bool operator!=(const NullTerminatedIterator& other) const {
        return ptr_ != other.ptr_;
    }

private:
    Pointer ptr_;
};

// NullTerminatedArray allows for easy iteration over null-terminated arrays
template <typename T>
class NullTerminatedArray {
public:
    using Iterator = NullTerminatedIterator<T>;

    NullTerminatedArray(T* arr) : arr_(arr) {}

    Iterator begin() const {
        return Iterator(arr_);
    }

    Iterator end() const {
        return nullptr;  // not implemented!
    }

private:
    T* arr_;
};

#endif // NULL_TERMINATED_ARRAY_H
