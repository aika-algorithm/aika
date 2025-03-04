#include <pybind11/pybind11.h>

#include "fields/field_definition.h"
#include "fields/field_update.h"
#include "fields/type.h"
#include "fields/type_registry.h"
#include "fields/input_field.h"
#include "fields/subtraction.h"


// ----------------
// Python interface
// ----------------

namespace py = pybind11;

PYBIND11_MODULE(aika, m)
{
      // Bind Relation
      py::class_<Relation>(m, "Relation");

      py::class_<FieldUpdate>(m, "FieldUpdate")
            .def(py::init<ProcessingPhase&, QueueInterceptor*>());

      // Bind FieldDefinition first
      py::class_<FieldDefinition>(m, "FieldDefinition")
            .def("in", &FieldDefinition::in, py::return_value_policy::reference_internal, 
                  py::arg("relation"), py::arg("input"), py::arg("arg"))
            .def("out", &FieldDefinition::out, py::return_value_policy::reference_internal, 
                  py::arg("relation"), py::arg("output"), py::arg("arg"));

      // Bind AbstractFunctionDefinition (inherits from FieldDefinition)
      py::class_<AbstractFunctionDefinition, FieldDefinition>(m, "AbstractFunctionDefinition");

      // Bind Subtraction (inherits from AbstractFunctionDefinition)
      py::class_<Subtraction, AbstractFunctionDefinition>(m, "Subtraction");

      py::class_<InputField>(m, "InputField")
            .def(py::init<Type*, const std::string &>())
            .def("__str__", [](const InputField &f) {
                  return f.toString();
            });

      py::class_<Type>(m, "Type")
            .def(py::init<TypeRegistry*, const std::string&>())
            .def("__str__", [](const Type &t) {
                  return t.toString();
            })
            .def("inputField", [](const Type &ref, const std::string &name) {
                  return new InputField(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::take_ownership)
            .def("sub", [](const Type &ref, const std::string &name) {
                  return new Subtraction(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::take_ownership);

      py::class_<TypeRegistry>(m, "TypeRegistry")
            .def(py::init<>())
            .def("getType", &TypeRegistry::getType)
            .def("registerType", &TypeRegistry::registerType);
}