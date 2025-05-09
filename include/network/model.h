#ifndef NETWORK_MODEL_H
#define NETWORK_MODEL_H

#include "fields/queue.h"
#include "fields/type_registry.h"
#include "network/config.h"
#include <map>
#include <string>
#include <vector>

class Neuron;
class Document;
class SuspensionCallback;

class Model : public Queue {
public:
    Model(TypeRegistry* typeRegistry);

    long createNeuronId();
    void registerDocument(Document* doc);
    void deregisterDocument(Document* doc);
    void addToN(int l);
    long getN() const;
    void setN(long n);
    bool canBeSuspended(long lastUsed) const;
    Neuron* getNeuron(long id);
    void registerNeuron(Neuron* n);
    void unregister(Neuron* n);
    void open(bool create);
    void close(bool store);
    long createThoughtId();
    Config* getConfig();
    void setConfig(Config* config);
    void write(std::ostream& out) const;
    void readFields(std::istream& in, Model* m);
    std::string toString() const;

private:
    TypeRegistry* typeRegistry;
    SuspensionCallback* suspensionCallback;
    long documentIdCounter;
    long N;
    Config* config;
    std::map<long, Neuron*> activeNeurons;
    std::map<long, Document*> documents;
    long lastProcessedDocument;
};

#endif // NETWORK_MODEL_H
