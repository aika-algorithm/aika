#include "network/lock_exception.h"

LockException::LockException(const std::exception& cause)
    : std::runtime_error("Failed to acquire or release lock"), cause(cause) {}

const std::exception& LockException::getCause() const {
    return cause;
} 