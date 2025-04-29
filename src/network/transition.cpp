#include "network/transition.h"

Transition::Transition(BSType* from, BSType* to) : fromType(from), toType(to) {}

Transition* Transition::of(BSType* from, BSType* to) {
    return new Transition(from, to);
}

BSType* Transition::from() const {
    return fromType;
}

BSType* Transition::to() const {
    return toType;
} 