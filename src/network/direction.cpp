#include "network/direction.h"
#include "network/input.h"
#include "network/output.h"

Direction* Direction::read(DataInput* in) {
    return in->readBoolean() ? new Output() : new Input();
} 