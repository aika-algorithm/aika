
#include <pybind11/pybind11.h>

#include "network/model.h"
#include "fields/type.h"
#include "fields/type_registry.h"


// ----------------
// Python interface
// ----------------

namespace py = pybind11;

PYBIND11_MODULE(aika, m)
{
  py::class_<TypeRegistry>(m, "TypeRegistry")
    .def(py::init<>())
    .def("getType", &TypeRegistry::getType)
    .def("registerType", &TypeRegistry::registerType);
}