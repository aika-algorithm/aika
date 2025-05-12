#ifndef NETWORK_LINK_H
#define NETWORK_LINK_H

#include "fields/obj.h"

#include "network/link_definition.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/typedefs.h"
#include "network/timestamp.h"

#include <string>

class Synapse;
class Activation;
class Document;
class Model;

class Link : public Obj, public Element, public ModelProvider {
public:
    Link(LinkDefinition* type, Synapse* s, Activation* input, Activation* output);

    RelatedObjectIterable* followManyRelation(Relation* rel) override;
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
    Queue* getQueue() const override;
    Model* getModel() override;
    std::string getInputKeyString();
    std::string getOutputKeyString();
    std::string toString();
    std::string toKeyString();

private:
    Synapse* synapse;
    Activation* input;
    Activation* output;
};

#endif // NETWORK_LINK_H 