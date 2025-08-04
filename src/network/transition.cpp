#include "network/transition.h"

Transition::Transition(int from, int to) : fromType(from), toType(to) {}

Transition* Transition::of(int from, int to) {
    return new Transition(from, to);
}

int Transition::from() const {
    return fromType;
}

int Transition::to() const {
    return toType;
} 