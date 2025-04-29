#include "network/in_memory_suspension_callback.h"

InMemorySuspensionCallback::InMemorySuspensionCallback() : currentId(0) {}

void InMemorySuspensionCallback::prepareNewModel() {}

void InMemorySuspensionCallback::open() {}

void InMemorySuspensionCallback::close() {}

long InMemorySuspensionCallback::createId() {
    return ++currentId;
}

long InMemorySuspensionCallback::getCurrentId() const {
    return currentId;
}

void InMemorySuspensionCallback::store(long id, const std::vector<char>& data) {
    storage[id] = data;
}

std::vector<char> InMemorySuspensionCallback::retrieve(long id) {
    return storage[id];
}

void InMemorySuspensionCallback::remove(long id) {
    storage.erase(id);
}

long InMemorySuspensionCallback::getIdByTokenId(int tokenId) {
    return tokenIds[tokenId];
}

void InMemorySuspensionCallback::putTokenId(int tokenId, long id) {
    tokenIds[tokenId] = id;
}

void InMemorySuspensionCallback::removeTokenId(int tokenId) {
    tokenIds.erase(tokenId);
}

void InMemorySuspensionCallback::loadIndex(Model* m) {
    throw std::runtime_error("UnsupportedOperationException");
}

void InMemorySuspensionCallback::saveIndex(Model* m) {
    throw std::runtime_error("UnsupportedOperationException");
} 