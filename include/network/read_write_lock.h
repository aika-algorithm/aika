#ifndef NETWORK_READ_WRITE_LOCK_H
#define NETWORK_READ_WRITE_LOCK_H

#include <mutex>
#include <condition_variable>

class ReadWriteLock {
public:
    void acquireWriteLock();
    void acquireReadLock();
    void releaseWriteLock();
    void releaseReadLock();

private:
    int readers = 0;
    int writers = 0;
    int writeRequests = 0;
    long writerThreadId = -1;
    int waitForReadLock = 0;
    int waitForWriteLock = 0;

    std::mutex writeLock;
    std::mutex mtx;
    std::condition_variable cv;
};

#endif // NETWORK_READ_WRITE_LOCK_H 