//
// Created by Lukas Molzberger on 02.02.25.
//

#include "network/model.h"
#include "fields/type_registry.h"
#include "network/neuron.h"
#include "network/document.h"
#include "network/suspension_callback.h"
#include "network/in_memory_suspension_callback.h"
#include <iostream>

/*#include "Config.h"
#include "type_registry.h"
#include "SuspensionCallback.h"
#include "Neuron.h"
#include "Document.h"
*/

class TypeRegistry;

Model::Model(TypeRegistry *typeRegistry)
    : typeRegistry(typeRegistry), suspensionCallback(new InMemorySuspensionCallback()), documentIdCounter(0), N(0) {}

long Model::getTimeout() const {
    return 0; //config ? config->getTimeout() : 0;
}

long Model::createNeuronId() {
    return suspensionCallback->createId();
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

void Model::registerDocument(Document *doc) {
    documents[doc->getId()] = doc;
}

void Model::deregisterDocument(Document *doc) {
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

bool Model::canBeSuspended(long lastUsed) const {
    long tId = getLowestDocumentId();
    if (tId == 0) {
        tId = lastProcessedDocument;
    }
    return lastUsed < tId - config.getNeuronProviderRetention();
}

Neuron *Model::getNeuron(long id) {
    return activeNeurons[id];
}

void Model::registerNeuron(Neuron *n) {
    activeNeurons[n->getId()] = n;
}

void Model::unregister(Neuron *n) {
    activeNeurons.erase(n->getId());
}

void Model::open(bool create) {
    if (create) {
        suspensionCallback->prepareNewModel();
    } else {
        suspensionCallback->loadIndex(this);
    }
    suspensionCallback->open();
}

void Model::close(bool store) {
    if (store) {
        suspensionCallback->saveIndex(this);
    }
    suspensionCallback->close();
}

long Model::createThoughtId() {
    return ++documentIdCounter;
}

Config *Model::getConfig() {
    if (!config) {
        config = new Config();
    }
    return config;
}

void Model::setConfig(Config *config) {
    this->config = config;
}

void Model::write(std::ostream &out) const {
    out << N;
}

void Model::readFields(std::istream &in, Model *m) {
    in >> N;
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