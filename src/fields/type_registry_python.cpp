
#include <pybind11/pybind11.h>

#include "network/model.h"
#include "fields/type.h"
#include "fields/type_registry.h"

int add(int i, int j) {
  return i + j;
}


// ----------------
// Python interface
// ----------------

namespace py = pybind11;

PYBIND11_MODULE(aika, m)
{
  m.def("add", &add, R"pbdoc(
        Add two numbers

        Some other explanation about the add function.
    )pbdoc");

  py::class_<Type>(m, "Type")
    .def(py::init<TypeRegistry*, const std::string&>());

  py::class_<TypeRegistry>(m, "TypeRegistry")
    .def(py::init<>())
    .def("getType", &TypeRegistry::getType)
    .def("registerType", &TypeRegistry::registerType);
}