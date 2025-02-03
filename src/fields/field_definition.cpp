#include "fields/type.h"
#include "fields/field.h"
#include "fields/field_definition.h"
#include "fields/obj.h"
#include <iostream>
#include <sstream>

FieldDefinition::FieldDefinition(std::shared_ptr<Type> objectType, const std::string& name)
    : objectType(objectType), name(name), isNextRound(false) {
    objectType->setFieldDefinition(shared_from_this());
}

FieldDefinition::FieldDefinition(std::shared_ptr<Type> objectType, const std::string& name, double tolerance)
    : FieldDefinition(objectType, name) {
    this->tolerance = tolerance;
}

void FieldDefinition::setFieldId(int fieldId) {
    this->fieldId = fieldId;
}

void FieldDefinition::transmit(std::shared_ptr<Field> targetField, std::shared_ptr<FieldLinkDefinitionOutputSide> fieldLink, double update) {
    receiveUpdate(targetField, update);
}

void FieldDefinition::receiveUpdate(std::shared_ptr<Field> field, double update) {
    if (!field->getObject()->isInstanceOf(objectType)) return;

//    if (ToleranceUtils::belowTolerance(getTolerance().value_or(0.0), update)) return;

    field->receiveUpdate(update);
}

std::shared_ptr<FieldDefinition> FieldDefinition::getParent() const {
    return parent;
}

std::shared_ptr<FieldDefinition> FieldDefinition::setParent(std::shared_ptr<FieldDefinition> parent) {
    this->parent = parent;
    parent->children.push_back(shared_from_this());
    return std::shared_ptr<FieldDefinition>(this);
}

std::vector<std::shared_ptr<FieldDefinition>> FieldDefinition::getChildren() const {
    return children;
}

bool FieldDefinition::isFieldRequired(const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs) {
    return resolveInheritedFieldDefinition(fieldDefs) == shared_from_this();
}

std::shared_ptr<FieldDefinition> FieldDefinition::resolveInheritedFieldDefinition(const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs) {
    for (const auto& child : children) {
        if (fieldDefs.find(child) != fieldDefs.end()) {
            return child->resolveInheritedFieldDefinition(fieldDefs);
        }
    }
    return shared_from_this();
}

void FieldDefinition::initializeField(std::shared_ptr<Field> field) {
    field->getObject()->getType()->getFlattenedTypeInputSide()->followLinks(field);
}

void FieldDefinition::addInput(std::shared_ptr<FieldLinkDefinitionOutputSide> fl) {
    throw std::logic_error("Unsupported operation.");
}

std::vector<std::shared_ptr<FieldLinkDefinitionOutputSide>> FieldDefinition::getInputs() {
    throw std::logic_error("Unsupported operation.");
}

void FieldDefinition::addOutput(std::shared_ptr<FieldLinkDefinitionInputSide> fl) {
    outputs.push_back(fl);
}

std::vector<std::shared_ptr<FieldLinkDefinitionInputSide>> FieldDefinition::getOutputs() {
    return outputs;
}

FieldDefinition& FieldDefinition::out(std::shared_ptr<Relation> relation, std::shared_ptr<FixedArgumentsFieldDefinition> output, int arg) {
    link(shared_from_this(), output, relation, arg);
    assert(relation || objectType->isInstanceOf(output->getObjectType()) || output->getObjectType()->isInstanceOf(objectType));
    return *this;
}

FieldDefinition& FieldDefinition::out(std::shared_ptr<Relation> relation, std::shared_ptr<VariableArgumentsFieldDefinition> output) {
    link(shared_from_this(), output, relation, nullptr);
    assert(relation || objectType->isInstanceOf(output->getObjectType()) || output->getObjectType()->isInstanceOf(objectType));
    return *this;
}

FieldDefinition& FieldDefinition::setName(const std::string& name) {
    this->name = name;
    return *this;
}

std::string FieldDefinition::getName() const {
    return name;
}

std::shared_ptr<Type> FieldDefinition::getObjectType() const {
    return objectType;
}

std::optional<int> FieldDefinition::getId() const {
    return fieldId;
}

FieldDefinition& FieldDefinition::setObjectType(std::shared_ptr<Type> objectType) {
    this->objectType = objectType;
    return *this;
}

std::optional<double> FieldDefinition::getTolerance() const {
    return tolerance;
}

FieldDefinition& FieldDefinition::setTolerance(std::optional<double> tolerance) {
    this->tolerance = tolerance;
    return *this;
}

std::shared_ptr<ProcessingPhase> FieldDefinition::getPhase() const {
    return phase;
}

FieldDefinition& FieldDefinition::setPhase(std::shared_ptr<ProcessingPhase> phase) {
    this->phase = phase;
    return *this;
}

bool FieldDefinition::getIsNextRound() const {
    return isNextRound;
}

FieldDefinition& FieldDefinition::setNextRound(bool nextRound) {
    isNextRound = nextRound;
    return *this;
}

FieldDefinition& FieldDefinition::setQueued(std::shared_ptr<ProcessingPhase> phase) {
    this->phase = phase;
    return *this;
}

std::string FieldDefinition::toString() const {
    std::stringstream ss;
    ss << getId().value_or(-1) << ":" << name;
    return ss.str();
}

bool FieldDefinition::operator<(const FieldDefinition& fd) const {
    return fieldId.value_or(0) < fd.fieldId.value_or(0);
}

// Explicit instantiation of shared_from_this
template std::shared_ptr<FieldDefinition> FieldDefinition::shared_from_this();
