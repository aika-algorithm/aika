#include "network/direction.h"
#include "network/input.h"
#include "network/output.h"

// Initialize the static variables
NetworkDirection* NetworkDirection::INPUT = new NetworkInput();
NetworkDirection* NetworkDirection::OUTPUT = new NetworkOutput();

