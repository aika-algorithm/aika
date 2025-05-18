#ifndef NETWORK_READ_WRITE_LOCK_H
#define NETWORK_READ_WRITE_LOCK_H

#include <mutex>
#include <condition_variable>

class ReadWriteLock {
public:
    void acquireWriteLock() const;
    void acquireReadLock() const;
    void releaseWriteLock() const;
    void releaseReadLock() const;

private:
    mutable int readers = 0;
    mutable int writers = 0;
    mutable int writeRequests = 0;
    mutable long writerThreadId = -1;
    mutable int waitForReadLock = 0;
    mutable int waitForWriteLock = 0;

    mutable std::mutex writeLock;
    mutable std::mutex mtx;
    mutable std::condition_variable cv;
};

#endif // NETWORK_READ_WRITE_LOCK_H 