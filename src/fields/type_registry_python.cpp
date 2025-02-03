
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
  py::class_<Type, std::shared_ptr<Type>>(m, "Type")
    .def(py::init<std::shared_ptr<TypeRegistry>, const std::string&>());

  py::class_<TypeRegistry>(m, "TypeRegistry")
    .def(py::init<>())
    .def("getType", &TypeRegistry::getType)
    .def("registerType", &TypeRegistry::registerType);
}