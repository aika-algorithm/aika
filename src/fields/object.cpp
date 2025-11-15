#include <iostream>
#include <sstream>
#include <stdexcept>
#include <algorithm>

#include "fields/object.h"
#include "fields/type.h"
#include "fields/field.h"
#include "fields/field_link_definition.h"

Object::Object(Type* type) : type(type) {
    if (type != nullptr) {
        fields = new Field*[type->getFlattenedTypeInputSide()->getNumberOfFields()];
    }
}

void Object::initFields() {
    for (short i = 0; i < type->getFlattenedTypeInputSide()->getNumberOfFields(); ++i) {
        auto fd = type->getFlattenedTypeInputSide()->getFieldDefinitionIdByIndex(i);
        auto field = getOrCreateFieldInput(fd);

        fd->initializeField(field);
    }
}

Type* Object::getType() const {
    return type;
}

bool Object::isInstanceOf(Type* t) const {
    return type->isInstanceOf(t);
}

Field* Object::getFieldInput(FieldDefinition* fd) const {
    short fieldIndex = type->getFlattenedTypeInputSide()->getFieldIndex(fd);
    return fields[fieldIndex];
}

Field* Object::getFieldOutput(FieldDefinition* fd) const {
    short fieldIndex = type->getFlattenedTypeOutputSide()->getFieldIndex(fd);
    return fields[fieldIndex];
}

Field* Object::getOrCreateFieldInput(FieldDefinition* fd) {
    short fieldIndex = type->getFlattenedTypeInputSide()->getFieldIndex(fd);

    auto field = fields[fieldIndex];
    if (field == nullptr) {
        field = new Field(this, fd, fieldIndex);
        fields[fieldIndex] = field;
    }
    return field;
}

Object& Object::setFieldValue(FieldDefinition* fd, double v) {
    getOrCreateFieldInput(fd)->setValue(v);
    return *this;
}

double Object::getFieldValue(FieldDefinition* fd) const {
    auto f = getFieldOutput(fd);
    return f ? f->getValue() : 0.0;
}

double Object::getFieldUpdatedValue(FieldDefinition* fd) const {
    auto f = getFieldOutput(fd);
    return f ? f->getUpdatedValue() : 0.0;
}

Field** Object::getFields() const {
    return fields;
}

std::string Object::toKeyString() const {
    return ""; // Implementation as needed
}

std::string Object::toString() const {
    std::stringstream ss;
    ss << type->getId() << ":" << type->getName();
    return ss.str();
}

std::string Object::getFieldAsString(FieldDefinition* fd) const {
    std::stringstream ss;
    ss << toString() << " fd:<" << fd->toString() << "> Number of inputs:" << fd->getInputs().size() << std::endl;

    for (auto fl : fd->getInputs()) {
        ss << "  arg:" << fl->getArgumentAsString() << " rel:<" << fl->getRelation()->getRelationId() << ":" << fl->getRelation()->getRelationLabel() << "> inputField:" << fl->getRelatedFD()->getName() << std::endl;
    }

    return ss.str();
}
