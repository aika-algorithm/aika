#include "network/f_s_suspension_callback.h"
#include "network/missing_neuron_exception.h"
#include <fstream>
#include <iostream>

FSSuspensionCallback::FSSuspensionCallback(const std::filesystem::path& path, const std::string& modelLabel)
    : path(path), modelLabel(modelLabel), currentId(0) {}

void FSSuspensionCallback::prepareNewModel() {
    std::filesystem::create_directories(path);
    std::filesystem::remove(getFile(MODEL));
    std::filesystem::remove(getFile(INDEX));
}

void FSSuspensionCallback::open() {
    modelStore.open(getFile(MODEL), std::ios::in | std::ios::out | std::ios::binary);
}

void FSSuspensionCallback::close() {
    modelStore.close();
}

long FSSuspensionCallback::createId() {
    return ++currentId;
}

long FSSuspensionCallback::getCurrentId() const {
    return currentId;
}

void FSSuspensionCallback::store(long id, const std::vector<char>& data) {
    modelStore.seekp(0, std::ios::end);
    index[id] = {modelStore.tellp(), data.size()};
    modelStore.write(data.data(), data.size());
}

std::vector<char> FSSuspensionCallback::retrieve(long id) {
    auto pos = index.find(id);
    if (pos == index.end()) {
        throw MissingNeuronException(id, modelLabel);
    }

    std::vector<char> data(pos->second.second);
    modelStore.seekg(pos->second.first);
    modelStore.read(data.data(), data.size());
    return data;
}

void FSSuspensionCallback::remove(long id) {
    index.erase(id);
}

void FSSuspensionCallback::loadIndex(Model* m) {
    std::ifstream indexFile(getFile(INDEX), std::ios::binary);
    if (!indexFile) return;

    indexFile.read(reinterpret_cast<char*>(&currentId), sizeof(currentId));

    int tokenId;
    long neuronId;
    while (indexFile.read(reinterpret_cast<char*>(&tokenId), sizeof(tokenId))) {
        indexFile.read(reinterpret_cast<char*>(&neuronId), sizeof(neuronId));
        tokenIds[tokenId] = neuronId;
    }

    long id;
    long pos;
    int size;
    while (indexFile.read(reinterpret_cast<char*>(&id), sizeof(id))) {
        indexFile.read(reinterpret_cast<char*>(&pos), sizeof(pos));
        indexFile.read(reinterpret_cast<char*>(&size), sizeof(size));
        index[id] = {pos, size};
    }
}

void FSSuspensionCallback::saveIndex(Model* m) {
    std::ofstream indexFile(getFile(INDEX), std::ios::binary);
    indexFile.write(reinterpret_cast<const char*>(&currentId), sizeof(currentId));

    for (const auto& [tokenId, neuronId] : tokenIds) {
        indexFile.write(reinterpret_cast<const char*>(&tokenId), sizeof(tokenId));
        indexFile.write(reinterpret_cast<const char*>(&neuronId), sizeof(neuronId));
    }

    for (const auto& [id, posSize] : index) {
        indexFile.write(reinterpret_cast<const char*>(&id), sizeof(id));
        indexFile.write(reinterpret_cast<const char*>(&posSize.first), sizeof(posSize.first));
        indexFile.write(reinterpret_cast<const char*>(&posSize.second), sizeof(posSize.second));
    }
}

std::filesystem::path FSSuspensionCallback::getFile(const std::string& prefix) const {
    return path / (prefix + "-" + modelLabel + ".dat");
} 