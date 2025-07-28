#include <pybind11/pybind11.h>

#include "fields/field_bindings.h"

#include "fields/relation.h"
#include "fields/field_definition.h"
#include "fields/field_update.h"
#include "fields/type.h"
#include "fields/object.h"
#include "fields/type_registry.h"
#include "fields/input_field.h"
#include "fields/subtraction.h"
#include "fields/test_type.h"
#include "fields/test_object.h"
#include "fields/field.h"
#include "fields/addition.h"
#include "fields/multiplication.h"
#include "fields/division.h"
#include "fields/exponential_function.h"
#include "fields/summation.h"
#include "fields/field_activation_function.h"
#include "fields/queue.h"

namespace py = pybind11;

// ----------------
// Python interface
// ----------------

void bind_fields(py::module_& m) {
      // Bind Queue class first (needed as base for Document and other classes)
      py::class_<Queue>(m, "Queue")
            .def("getTimeout", &Queue::getTimeout)
            .def("getTimestampOnProcess", &Queue::getTimestampOnProcess)
            .def("getCurrentTimestamp", &Queue::getCurrentTimestamp)
            .def("getNextTimestamp", &Queue::getNextTimestamp)
            .def("addStep", &Queue::addStep)
            .def("removeStep", &Queue::removeStep)
            .def("process", py::overload_cast<>(&Queue::process))
            .def("getQueueEntries", &Queue::getQueueEntries, py::return_value_policy::reference_internal)
            .def("getCurrentRound", &Queue::getCurrentRound);

      // Bind Relation
      py::class_<Relation>(m, "Relation")
            .def("setReversed", [](Relation &rel, Relation* reversed) {
                  rel.setReversed(reversed);
            });

      py::class_<RelationOne, Relation>(m, "RelationOne")
            .def(py::init<int, const std::string&>());

      py::class_<RelationMany, Relation>(m, "RelationMany")
            .def(py::init<int, const std::string&>());      

      py::class_<RelationSelf, RelationOne>(m, "RelationSelf")
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

      // Bind Addition (inherits from AbstractFunctionDefinition)
      py::class_<Addition, AbstractFunctionDefinition>(m, "Addition");

      // Bind Multiplication (inherits from AbstractFunctionDefinition)
      py::class_<Multiplication, AbstractFunctionDefinition>(m, "Multiplication");

      // Bind Division (inherits from AbstractFunctionDefinition)
      py::class_<Division, AbstractFunctionDefinition>(m, "Division");

      py::class_<ExponentialFunction, AbstractFunctionDefinition>(m, "ExponentialFunction");

      // Bind Summation (inherits from AbstractFunctionDefinition)
      py::class_<Summation, AbstractFunctionDefinition>(m, "Summation");

            // Bind FieldActivationFunction (inherits from AbstractFunctionDefinition)
      py::class_<FieldActivationFunction, AbstractFunctionDefinition>(m, "FieldActivationFunction")
            .def(py::init<Type*, const std::string&, ActivationFunction*, double>());

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
            }, py::return_value_policy::reference_internal)
            .def("sub", [](const Type &ref, const std::string &name) {
                  return new Subtraction(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::reference_internal)
            .def("add", [](const Type &ref, const std::string &name) {
                  return new Addition(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::reference_internal)
            .def("mul", [](const Type &ref, const std::string &name) {
                  return new Multiplication(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::reference_internal)
            .def("div", [](const Type &ref, const std::string &name) {
                  return new Division(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::reference_internal)
            .def("exp", [](const Type &ref, const std::string &name) {
                  return new ExponentialFunction(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::reference_internal)
            .def("sum", [](const Type &ref, const std::string &name) {
                  return new Summation(
                        const_cast<Type*>(&ref),
                        name
                  );
            }, py::return_value_policy::reference_internal)
            .def("fieldActivationFunc", [](const Type &ref, const std::string &name, ActivationFunction* actFunction, double tolerance) {
                  return new FieldActivationFunction(
                        const_cast<Type*>(&ref),
                        name,
                        actFunction,
                        tolerance
                  );
            }, py::return_value_policy::reference_internal);

      py::class_<Object>(m, "Object")
            .def("__str__", [](const Object &t) {
                  return t.toString();
            })
            .def("getFieldAsString", &Object::getFieldAsString)
            .def("setFieldValue", &Object::setFieldValue)
            .def("getFieldValue", &Object::getFieldValue)
            .def("initFields", &Object::initFields)
            .def("getType", &Object::getType, py::return_value_policy::reference_internal)
            .def("isInstanceOf", &Object::isInstanceOf)
            .def("getFieldOutput", &Object::getFieldOutput, py::return_value_policy::reference_internal)
            .def("getOrCreateFieldInput", &Object::getOrCreateFieldInput, py::return_value_policy::reference_internal);

      py::class_<TestType, Type>(m, "TestType")
            .def(py::init<TypeRegistry*, const std::string&>())
            .def("instantiate", &TestType::instantiate, py::return_value_policy::reference_internal);    

      py::class_<TestObject, Object>(m, "TestObj")
            .def(py::init<TestType*>())
            .def_static("linkObjects", &TestObject::linkObjects);    

      py::class_<TypeRegistry>(m, "TypeRegistry")
            .def(py::init<>())
            .def("getType", &TypeRegistry::getType)
            .def("registerType", &TypeRegistry::registerType)
            .def("flattenTypeHierarchy", &TypeRegistry::flattenTypeHierarchy);

      py::class_<Field>(m, "Field")
            .def("getValue", &Field::getValue)
            .def("getUpdatedValue", &Field::getUpdatedValue)
            .def("__str__", [](const Field &f) {
                  return f.toString();
            });
}