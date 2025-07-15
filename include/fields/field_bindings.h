#ifndef FIELD_BINDINGS_H
#define FIELD_BINDINGS_H

#include <pybind11/pybind11.h>

namespace py = pybind11;

void bind_fields(py::module_& m);


#endif //FIELD_BINDINGS_H
