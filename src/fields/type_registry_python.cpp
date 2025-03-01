
#include <pybind11/pybind11.h>

#include "fields/field_update.h"
#include "fields/type.h"
#include "fields/type_registry.h"
#include "fields/input_field.h"


// ----------------
// Python interface
// ----------------

namespace py = pybind11;

PYBIND11_MODULE(aika, m)
{
  py::class_<FieldUpdate>(m, "FieldUpdate")
  .def(py::init<ProcessingPhase&, QueueInterceptor*>());

  py::class_<Type>(m, "Type")
  .def(py::init<TypeRegistry*, const std::string&>())
  .def("__str__", [](const Type &t) {
        return t.toString();
  })
  .def("inputField", [](const Type &ref, const std::string &name) {
      new InputField(
                ref,
                name
        );
  });

  py::class_<TypeRegistry>(m, "TypeRegistry")
    .def(py::init<>())
    .def("getType", &TypeRegistry::getType)
    .def("registerType", &TypeRegistry::registerType);
}