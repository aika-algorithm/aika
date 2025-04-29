#ifndef NETWORK_LOCK_EXCEPTION_H
#define NETWORK_LOCK_EXCEPTION_H

#include <stdexcept>

class LockException : public std::runtime_error {
public:
    explicit LockException(const std::exception& cause);
    const std::exception& getCause() const;

private:
    const std::exception& cause;
};

#endif // NETWORK_LOCK_EXCEPTION_H 