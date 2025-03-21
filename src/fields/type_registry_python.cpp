#include <pybind11/pybind11.h>

#include "fields/relation.h"
#include "fields/field_definition.h"
#include "fields/field_update.h"
#include "fields/type.h"
#include "fields/type_registry.h"
#include "fields/input_field.h"
#include "fields/subtraction.h"
#include "fields/test_type.h"


// ----------------
// Python interface
// ----------------

namespace py = pybind11;

PYBIND11_MODULE(aika, m)
{
      // Bind Relation
      py::class_<Relation>(m, "Relation")
            .def("setReversed", [](Relation &rel, Relation* reversed) {
                  rel.setReversed(reversed);
            });

      py::class_<RelationOne, Relation>(m, "RelationOne")
            .def(py::init<int, const std::string&>());

      py::class_<RelationMany, Relation>(m, "RelationMany")
            .def(py::init<int, const std::string&>());      

      py::class_<FieldUpdate>(m, "FieldUpdate")
            .def(py::init<ProcessingPhase&, QueueInterceptor*>());

      // Bind FieldDefinition first
      py::class_<FieldDefinition>(m, "FieldDefinition")
            .def("input", &FieldDefinition::input, py::return_value_policy::reference_internal, 
                  py::arg("relation"), py::arg("input"), py::arg("arg"))
            .def("output", &FieldDefinition::output, py::return_value_policy::reference_internal, 
                  py::arg("relation"), py::arg("output"), py::arg("arg"));

      // Bind AbstractFunctionDefinition (inherits from FieldDefinition)
      py::class_<AbstractFunctionDefinition, FieldDefinition>(m, "AbstractFunctionDefinition");

      // Bind Subtraction (inherits from AbstractFunctionDefinition)
      py::class_<Subtraction, AbstractFunctionDefinition>(m, "Subtraction");

      py::class_<InputField, FieldDefinition>(m, "InputField")
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

      py::class_<TestType, Type>(m, "TestType")
            .def(py::init<TypeRegistry*, const std::string&>());    

      py::class_<TypeRegistry>(m, "TypeRegistry")
            .def(py::init<>())
            .def("getType", &TypeRegistry::getType)
            .def("registerType", &TypeRegistry::registerType)
            .def("flattenTypeHierarchy", &TypeRegistry::flattenTypeHierarchy);
}