#ifndef NETWORK_TRANSITION_H
#define NETWORK_TRANSITION_H

#include "network/bs_type.h"

class Transition {
public:
    Transition(BSType* from, BSType* to);

    static Transition* of(BSType* from, BSType* to);

    BSType* from() const;
    BSType* to() const;

private:
    BSType* fromType;
    BSType* toType;
};

#endif // NETWORK_TRANSITION_H 