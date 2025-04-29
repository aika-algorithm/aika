#include "network/read_write_lock.h"

void ReadWriteLock::acquireWriteLock() {
    std::unique_lock<std::mutex> lock(mtx);
    ++writeRequests;
    cv.wait(lock, [this]() { return readers == 0 && writers == 0; });
    --writeRequests;
    ++writers;
}

void ReadWriteLock::acquireReadLock() {
    std::unique_lock<std::mutex> lock(mtx);
    cv.wait(lock, [this]() { return writers == 0; });
    ++readers;
}

void ReadWriteLock::releaseWriteLock() {
    std::lock_guard<std::mutex> lock(mtx);
    --writers;
    cv.notify_all();
}

void ReadWriteLock::releaseReadLock() {
    std::lock_guard<std::mutex> lock(mtx);
    --readers;
    if (readers == 0) {
        cv.notify_all();
    }
} 