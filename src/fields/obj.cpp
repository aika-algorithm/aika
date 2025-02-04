#include <iostream>
#include <stdexcept>
#include <algorithm>

#include "fields/obj.h"
#include "fields/type.h"

Obj::Obj(std::shared_ptr<Type> type) : type(type) {
    if (type != nullptr) {
        fields.resize(type->getFlattenedTypeInputSide()->getNumberOfFields());
    }
}

void Obj::initFields() {
    for (short i = 0; i < type->getFlattenedTypeInputSide()->getNumberOfFields(); ++i) {
        auto fd = type->getFlattenedTypeInputSide()->getFieldDefinitionIdByIndex(i);
        auto field = getOrCreateFieldInput(fd);
        fd->initializeField(field);
    }
}

std::shared_ptr<Type> Obj::getType() const {
    return type;
}

std::shared_ptr<Obj> Obj::followManyRelation(std::shared_ptr<Relation> rel) {
    return nullptr; // Implementation as required, for now returning nullptr
}

std::shared_ptr<Obj> Obj::followSingleRelation(std::shared_ptr<Relation> rel) {
    return nullptr; // Implementation as required, for now returning nullptr
}

bool Obj::isInstanceOf(std::shared_ptr<Type> t) {
    return type->isInstanceOf(t);
}

std::shared_ptr<Field> Obj::getFieldOutput(std::shared_ptr<FieldDefinition> fd) {
    short fieldIndex = type->getFlattenedTypeOutputSide()->getFieldIndex(fd);
    return fields[fieldIndex];
}

std::shared_ptr<Field> Obj::getOrCreateFieldInput(std::shared_ptr<FieldDefinition> fd) {
    short fieldIndex = type->getFlattenedTypeInputSide()->getFieldIndex(fd);
    if (!fields[fieldIndex]) {
        fields[fieldIndex] = std::make_shared<Field>(shared_from_this(), fd, fieldIndex);
    }
    return fields[fieldIndex];
}

Obj& Obj::setFieldValue(std::shared_ptr<FieldDefinition> fd, double v) {
    getOrCreateFieldInput(fd)->setValue(v);
    return *this;
}

double Obj::getFieldValue(std::shared_ptr<FieldDefinition> fd) {
    auto f = getFieldOutput(fd);
    return f ? f->getValue() : 0.0;
}

double Obj::getFieldUpdatedValue(std::shared_ptr<FieldDefinition> fd) {
    auto f = getFieldOutput(fd);
    return f ? f->getUpdatedValue() : 0.0;
}

std::vector<std::shared_ptr<Field>> Obj::getFields() {
    std::vector<std::shared_ptr<Field>> nonNullFields;
    std::copy_if(fields.begin(), fields.end(), std::back_inserter(nonNullFields),
                 [](const auto& field) { return field != nullptr; });
    return nonNullFields;
}

std::shared_ptr<Queue> Obj::getQueue() {
    return nullptr; // Implementation for queue handling, for now returning nullptr
}

std::string Obj::toKeyString() {
    return ""; // Implementation as needed
}

std::string Obj::toString() const {
    std::stringstream ss;
    ss << type;
    return ss.str();
}
