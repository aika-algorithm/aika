#ifndef NETWORK_SUSPENSION_CALLBACK_H
#define NETWORK_SUSPENSION_CALLBACK_H

#include "network/model.h"
#include <vector>

class SuspensionCallback {
public:
    virtual ~SuspensionCallback() = default;

    virtual void prepareNewModel() = 0;
    virtual void open() = 0;
    virtual void close() = 0;
    virtual long createId() = 0;
    virtual long getCurrentId() const = 0;
    virtual void store(long id, const std::vector<char>& data) = 0;
    virtual void remove(long id) = 0;
    virtual std::vector<char> retrieve(long id) = 0;
    virtual long getIdByTokenId(int tokenId) = 0;
    virtual void putTokenId(int tokenId, long id) = 0;
    virtual void removeTokenId(int tokenId) = 0;
    virtual void loadIndex(Model* m) = 0;
    virtual void saveIndex(Model* m) = 0;
};

#endif // NETWORK_SUSPENSION_CALLBACK_H 