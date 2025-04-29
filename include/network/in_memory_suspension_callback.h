#ifndef NETWORK_IN_MEMORY_SUSPENSION_CALLBACK_H
#define NETWORK_IN_MEMORY_SUSPENSION_CALLBACK_H

#include "network/suspension_callback.h"
#include "network/model.h"
#include <map>
#include <vector>

class InMemorySuspensionCallback : public SuspensionCallback {
public:
    InMemorySuspensionCallback();

    void prepareNewModel() override;
    void open() override;
    void close() override;
    long createId() override;
    long getCurrentId() const override;
    void store(long id, const std::vector<char>& data) override;
    std::vector<char> retrieve(long id) override;
    void remove(long id) override;
    long getIdByTokenId(int tokenId) override;
    void putTokenId(int tokenId, long id) override;
    void removeTokenId(int tokenId) override;
    void loadIndex(Model* m) override;
    void saveIndex(Model* m) override;

private:
    long currentId;
    std::map<long, std::vector<char>> storage;
    std::map<int, long> tokenIds;
};

#endif // NETWORK_IN_MEMORY_SUSPENSION_CALLBACK_H 