#ifndef NETWORK_LINK_H
#define NETWORK_LINK_H

#include "fields/object.h"

#include "network/types/link_type.h"
#include "network/element.h"
#include "network/model_provider.h"
#include "network/typedefs.h"
#include "network/timestamp.h"

#include <string>

class Synapse;
class Activation;
class Context;
class Model;

class Link : public Object, public Element, public ModelProvider {
public:
    Link(LinkType* type, Synapse* s, Activation* input, Activation* output);

    RelatedObjectIterable* followManyRelation(Relation* rel) const override;
    Object* followSingleRelation(const Relation* rel) const override;
    long getFired() const override;
    long getCreated() const override;
    Synapse* getSynapse() const;
    void setSynapse(Synapse* synapse);
    Activation* getInput() const;
    Activation* getOutput() const;
    bool isCausal() const;
    static bool isCausal(Activation* iAct, Activation* oAct);
    Context* getContext() const;
    Queue* getQueue() const override;
    Model* getModel() const override;
    Config* getConfig() const override;
    std::string getInputKeyString() const;
    std::string getOutputKeyString() const;
    std::string toString() const;
    std::string toKeyString() const;
    
    // Methods for paired links
    Link* getPairedLinkInputSide() const;
    void setPairedLinkInputSide(Link* pairedLink);
    Link* getPairedLinkOutputSide() const;
    void setPairedLinkOutputSide(Link* pairedLink);

private:
    Synapse* synapse;
    Activation* input;
    Activation* output;
    
    // Paired links for PAIR relations
    Link* pairedLinkInputSide;   // Paired link on input side
    Link* pairedLinkOutputSide;  // Paired link on output side
};

#endif // NETWORK_LINK_H 