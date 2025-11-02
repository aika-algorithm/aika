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

class Context : public Queue, public ModelProvider, public QueueProvider {
public:
    Context(Model* m);
    ~Context();

    long getId() const;
    long getTimeout() const override;
    void process(std::function<bool(Step*)> filter);
    Model* getModel() const override;
    Config* getConfig() const override;
    Step* getCurrentStep();
    void addActivation(Activation* act);
    void removeActivation(Activation* act);
    std::set<Activation*> getActivations();
    Activation* getActivationByNeuron(Neuron* outputNeuron);
    int createActivationId();
    void disconnect();
    Queue* getQueue() const override;
    Activation* addToken(Neuron* n, int bsType, int tokenId);
    BindingSignal* getOrCreateBindingSignal(int tokenId);
    BindingSignal* getBindingSignal(int tokenId);
    std::string toString() const;

private:
    Model* model;
    long id;
    int length;
    int activationIdCounter;
    std::map<int, Activation*> activations;
    std::map<int, BindingSignal*> bindingSignals;
    bool isStale;
};

#endif // NETWORK_DOCUMENT_H 