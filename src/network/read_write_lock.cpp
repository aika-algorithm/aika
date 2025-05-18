#include "network/read_write_lock.h"

void ReadWriteLock::acquireWriteLock() const {
    std::unique_lock<std::mutex> lock(mtx);
    ++writeRequests;
    cv.wait(lock, [this]() { return readers == 0 && writers == 0; });
    --writeRequests;
    ++writers;
}

void ReadWriteLock::acquireReadLock() const {
    std::unique_lock<std::mutex> lock(mtx);
    cv.wait(lock, [this]() { return writers == 0; });
    ++readers;
}

void ReadWriteLock::releaseWriteLock() const {
    std::lock_guard<std::mutex> lock(mtx);
    --writers;
    cv.notify_all();
}

void ReadWriteLock::releaseReadLock() const {
    std::lock_guard<std::mutex> lock(mtx);
    --readers;
    if (readers == 0) {
        cv.notify_all();
    }
} 