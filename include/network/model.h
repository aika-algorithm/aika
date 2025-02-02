
#ifndef MODEL_H
#define MODEL_H


#include <iostream>
#include <map>
#include <vector>
#include <memory>
#include <mutex>
#include <atomic>

// Forward Declarations
class Neuron;
class Document;
class TypeRegistry;
class SuspensionCallback;
class Config;

class Writable {
public:
    virtual void write(std::ostream &out) const = 0;
    virtual void read(std::istream &in) = 0;
    virtual ~Writable() = default;
};

class Model : public Writable {
private:
    static inline std::ostream &LOG = std::cerr;

    std::atomic<long> N{0};
//    Config *config{nullptr};
    TypeRegistry *typeRegistry{nullptr};
//    SuspensionCallback *suspensionCallback{nullptr};
    std::atomic<long> documentIdCounter{0};

    std::map<long, Neuron *> activeNeurons;
    std::map<long, Document *> documents;
    long lastProcessedDocument{0};

    std::mutex neuronMutex;
    std::mutex documentMutex;

public:
    explicit Model(TypeRegistry *typeRegistry);

    long getTimeout() const;

    long createNeuronId();

    long getLowestDocumentId();

    TypeRegistry *getTypeRegistry();

    void registerDocument(Document *doc);
    void deregisterDocument(Document *doc);

    std::vector<Neuron *> getActiveNeurons();

    void registerTokenId(int tokenId, Neuron *in);
    Neuron *getNeuronByTokenId(int tokenId);

    void applyMovingAverage(Config *trainingConfig);

//    SuspensionCallback *getSuspensionCallback();

    void addToN(int l);
    long getN() const;
    void setN(long n);

    bool canBeSuspended(long lastUsed);

/*    Neuron *getNeuron(long id);

    void registerNeuron(Neuron *n);
    void unregisterNeuron(Neuron *n);
*/
    void open(bool create);
    void close(bool store);

    long createThoughtId();

//    Config *getConfig();

    void write(std::ostream &out) const override;
    void read(std::istream &in) override;

    std::string toString() const;

/*    bool suspendNeuron(Neuron *neuron, bool saveOnSuspend);
    Neuron *reactivate(long neuronId);
*/
};

#endif //MODEL_H
