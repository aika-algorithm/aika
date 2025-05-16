#ifndef NETWORK_DIRECTION_H
#define NETWORK_DIRECTION_H

#include "network/model.h"
#include "network/activation.h"
#include "network/link.h"
#include "network/neuron.h"
#include "network/synapse.h"
#include "network/bs_type.h"
#include "network/transition.h"

class Direction {
public:
    virtual ~Direction() = default;
    virtual Direction* invert() = 0;
    virtual Neuron* getNeuron(Model* m, Synapse* s) = 0;
    virtual Activation* getActivation(Link* l) = 0;
    virtual int getOrder() = 0;
    virtual BSType* transition(BSType* s, Transition* t) = 0;
    virtual void write(DataOutput* out) = 0;

    static Direction* read(DataInput* in);
};

#endif // NETWORK_DIRECTION_H 