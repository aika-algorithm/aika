#ifndef NETWORK_BINDINGS_H
#define NETWORK_BINDINGS_H

#include <pybind11/pybind11.h>

namespace py = pybind11;

void bind_network(py::module_& m);

#endif // NETWORK_BINDINGS_H