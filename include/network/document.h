#ifndef NETWORK_DOCUMENT_H
#define NETWORK_DOCUMENT_H

#include <map>
#include <set>

#include "fields/queue.h"
#include "fields/step.h"
#include "fields/queue_provider.h"
#include "network/model_provider.h"
#include "network/activation.h"
#include "network/binding_signal.h"
#include "network/neuron.h"

class Document : public Queue, public ModelProvider, public QueueProvider {
public:
    Document(Model* m, int length);
    ~Document();

    long getId() const;
    long getTimeout() const override;
    void process(std::function<bool(Step*)> filter);
    Model* getModel() override;
    Config* getConfig() override;
    Step* getCurrentStep();
    void addActivation(Activation* act);
    std::set<Activation*> getActivations();
    Activation* getActivationByNeuron(Neuron* outputNeuron);
    int createActivationId();
    void disconnect();
    Queue* getQueue() override;
    Activation* addToken(Neuron* n, BSType* bsType, int tokenId);
    BindingSignal* getOrCreateBindingSignal(int tokenId);
    BindingSignal* getBindingSignal(int tokenId);
    std::string toString() const;

private:
    Model* model;
    long id;
    long absoluteBeginChar;
    int length;
    int activationIdCounter;
    std::map<int, Activation*> activations;
    std::map<int, BindingSignal*> bindingSignals;
    bool isStale;
};

#endif // NETWORK_DOCUMENT_H 