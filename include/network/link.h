#ifndef NETWORK_LINK_H
#define NETWORK_LINK_H

#include "fields/queue_provider.h"
#include "fields/obj.h"

#include "network/element.h"
#include "network/model_provider.h"
#include "network/typedefs.h"
#include "network/timestamp.h"

#include <string>

class Synapse;
class Activation;
class Document;
class Model;

class Link : public Obj, public Element, public ModelProvider, public QueueProvider {
public:
    Link(LinkDefinition* type, Synapse* s, Activation* input, Activation* output);

    Stream<Obj*> followManyRelation(Relation* rel) override;
    Obj* followSingleRelation(Relation* rel) override;
    Timestamp getFired() override;
    Timestamp getCreated() override;
    Synapse* getSynapse();
    void setSynapse(Synapse* synapse);
    Activation* getInput();
    Activation* getOutput();
    bool isCausal();
    static bool isCausal(Activation* iAct, Activation* oAct);
    Document* getDocument();
    Queue* getQueue() override;
    Model* getModel() override;
    std::string getInputKeyString();
    std::string getOutputKeyString();
    std::string toString() override;
    std::string toKeyString() override;

private:
    Synapse* synapse;
    Activation* input;
    Activation* output;
};

#endif // NETWORK_LINK_H 