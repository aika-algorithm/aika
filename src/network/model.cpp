//
// Created by Lukas Molzberger on 02.02.25.
//

#include "network/model.h"
#include "fields/type_registry.h"

/*#include "Config.h"
#include "type_registry.h"
#include "SuspensionCallback.h"
#include "Neuron.h"
#include "Document.h"
*/

class TypeRegistry;

Model::Model(TypeRegistry *typeRegistry) {
//    suspensionCallback = new SuspensionCallback();
}

long Model::getTimeout() const {
    return 0; //config ? config->getTimeout() : 0;
}

long Model::createNeuronId() {
    return 0;
 //   return suspensionCallback->createId();
}

long Model::getLowestDocumentId() {
    std::lock_guard<std::mutex> lock(documentMutex);
    if (documents.empty()) {
        return -1; // Equivalent to returning null in Java
    }
    return documents.begin()->first;
}

TypeRegistry *Model::getTypeRegistry() {
    return typeRegistry;
}
/*
void Model::registerDocument(Document *doc) {
    std::lock_guard<std::mutex> lock(documentMutex);
    documents[doc->getId()] = doc;
}

void Model::deregisterDocument(Document *doc) {
    std::lock_guard<std::mutex> lock(documentMutex);
    documents.erase(doc->getId());
    lastProcessedDocument = std::max(lastProcessedDocument, doc->getId());
}

std::vector<Neuron *> Model::getActiveNeurons() {
    std::lock_guard<std::mutex> lock(neuronMutex);
    std::vector<Neuron *> neurons;
    for (const auto &entry : activeNeurons) {
        neurons.push_back(entry.second);
    }
    return neurons;
}
*/

void Model::registerTokenId(int tokenId, Neuron *in) {
   // suspensionCallback->putTokenId(tokenId, in->getId());
   // in->save();
}

/*
Neuron *Model::getNeuronByTokenId(int tokenId) {
    long id = suspensionCallback->getIdByTokenId(tokenId);
    return id != -1 ? getNeuron(id) : nullptr;
}
*/
/*
void Model::applyMovingAverage(Config *trainingConfig) {
    if (trainingConfig->getAlpha() != nullptr) {
        N *= *(trainingConfig->getAlpha());
    }
}

SuspensionCallback *Model::getSuspensionCallback() {
    return suspensionCallback;
}
*/

void Model::addToN(int l) {
    N += l;
}

long Model::getN() const {
    return N;
}

void Model::setN(long n) {
    N = n;
}

bool Model::canBeSuspended(long lastUsed) {
    long tId = getLowestDocumentId();
    if (tId == -1) {
        tId = lastProcessedDocument;
    }
    return true; //lastUsed < tId - (config ? config->getNeuronProviderRetention() : 0);
}

/*
Neuron *Model::getNeuron(long id) {
    std::lock_guard<std::mutex> lock(neuronMutex);
    auto it = activeNeurons.find(id);
    return it != activeNeurons.end() ? it->second : nullptr;
}

void Model::registerNeuron(Neuron *n) {
    std::lock_guard<std::mutex> lock(neuronMutex);
    auto existing = activeNeurons.insert({n->getId(), n});
    if (!existing.second) {
        LOG << "Attempted to register Neuron twice: (n:" << n->getId() << ")\n";
    }
}

void Model::unregisterNeuron(Neuron *n) {
    std::lock_guard<std::mutex> lock(neuronMutex);
    auto removed = activeNeurons.erase(n->getId());
    if (!removed) {
        LOG << "Attempted to remove Neuron twice: (n:" << n->getId() << ")\n";
    }
}
*/

void Model::open(bool create) {
/*    if (create) {
        suspensionCallback->prepareNewModel();
    } else {
        suspensionCallback->loadIndex(this);
    }
    suspensionCallback->open();
*/
}

void Model::close(bool store) {
/*    if (store) {
        suspensionCallback->saveIndex(this);
    }
    suspensionCallback->close();
*/
}

long Model::createThoughtId() {
    return documentIdCounter.fetch_add(1) + 1;
}

/*
Config *Model::getConfig() {
    if (!config) {
        config = new Config();
    }
    return config;
}
*/

void Model::write(std::ostream &out) const {
    out.write(reinterpret_cast<const char *>(&N), sizeof(N));
}

void Model::read(std::istream &in) {
    in.read(reinterpret_cast<char *>(&N), sizeof(N));
}

std::string Model::toString() const {
    return "N:" + std::to_string(N);
}
/*
bool Model::suspendNeuron(Neuron *neuron, bool saveOnSuspend) {
    // Placeholder for suspension logic
    return true;
}

Neuron *Model::reactivate(long neuronId) {
    return nullptr; // Placeholder for real implementation
}
*/