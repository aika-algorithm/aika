#include <iostream>
#include <sstream>

#include "fields/type.h"
#include "fields/field.h"
#include "fields/field_definition.h"
#include "fields/field_link_definition.h"
#include "fields/obj.h"


FieldDefinition::FieldDefinition(Type* objectType, const std::string& name)
    : objectType(objectType), name(name), isNextRound(false) {
    objectType->setFieldDefinition(this);
}

FieldDefinition::FieldDefinition(Type* objectType, const std::string& name, double tolerance)
    : FieldDefinition(objectType, name) {
    this->tolerance = tolerance;
}

void FieldDefinition::setFieldId(int fieldId) {
    this->fieldId = fieldId;
}

void FieldDefinition::transmit(Field* targetField, FieldLinkDefinitionOutputSide* fieldLink, double update) {
    receiveUpdate(targetField, update);
}

void FieldDefinition::receiveUpdate(Field* field, double update) {
    if (!field->getObject()->isInstanceOf(objectType)) return;

//    if (ToleranceUtils::belowTolerance(getTolerance().value_or(0.0), update)) return;

    field->receiveUpdate(update);
}

FieldDefinition* FieldDefinition::getParent() const {
    return parent;
}

FieldDefinition* FieldDefinition::setParent(FieldDefinition* parent) {
    this->parent = parent;
    parent->children.push_back(this);
    return this;
}

std::vector<FieldDefinition*> FieldDefinition::getChildren() const {
    return children;
}

bool FieldDefinition::isFieldRequired(const std::set<FieldDefinition*>& fieldDefs) {
    return resolveInheritedFieldDefinition(fieldDefs) == this;
}

FieldDefinition* FieldDefinition::resolveInheritedFieldDefinition(const std::set<FieldDefinition*>& fieldDefs) {
    for (const auto& child : children) {
        if (fieldDefs.find(child) != fieldDefs.end()) {
            return child->resolveInheritedFieldDefinition(fieldDefs);
        }
    }
    return this;
}

void FieldDefinition::initializeField(Field* field) {
    field->getObject()->getType()->getFlattenedTypeInputSide()->followLinks(field);
}

void FieldDefinition::addInput(FieldLinkDefinitionOutputSide* fl) {
    throw std::logic_error("Unsupported operation.");
}

std::vector<FieldLinkDefinitionOutputSide*> FieldDefinition::getInputs() {
    throw std::logic_error("Unsupported operation.");
}

void FieldDefinition::addOutput(FieldLinkDefinitionInputSide* fl) {
    outputs.push_back(fl);
}

std::vector<FieldLinkDefinitionInputSide*> FieldDefinition::getOutputs() {
    return outputs;
}

FieldDefinition& FieldDefinition::out(Relation* relation, FieldDefinition* output, int arg) {
    FieldLinkDefinition::link(this, output, relation, arg);
//    assert(relation || objectType->isInstanceOf(output->getObjectType()) || output->getObjectType()->isInstanceOf(objectType));
    return *this;
}

FieldDefinition& FieldDefinition::setName(const std::string& name) {
    this->name = name;
    return *this;
}

std::string FieldDefinition::getName() const {
    return name;
}

Type* FieldDefinition::getObjectType() const {
    return objectType;
}

int FieldDefinition::getId() const {
    return fieldId;
}

FieldDefinition& FieldDefinition::setObjectType(Type* objectType) {
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

ProcessingPhase* FieldDefinition::getPhase() const {
    return phase;
}

FieldDefinition& FieldDefinition::setPhase(ProcessingPhase* phase) {
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

FieldDefinition& FieldDefinition::setQueued(ProcessingPhase* phase) {
    this->phase = phase;
    return *this;
}

std::string FieldDefinition::toString() const {
    std::stringstream ss;
    ss << getId() << ":" << name;
    return ss.str();
}

bool FieldDefinition::operator<(const FieldDefinition& fd) const {
    return fieldId < fd.fieldId;
}

