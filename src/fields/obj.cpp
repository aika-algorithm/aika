#include <iostream>
#include <sstream>
#include <stdexcept>
#include <algorithm>

#include "fields/obj.h"
#include "fields/type.h"

Obj::Obj(Type* type) : type(type) {
    if (type != nullptr) {
        fields = new Field*[type->getFlattenedTypeInputSide()->getNumberOfFields()];
    }
}

void Obj::initFields() {
    for (short i = 0; i < type->getFlattenedTypeInputSide()->getNumberOfFields(); ++i) {
        auto fd = type->getFlattenedTypeInputSide()->getFieldDefinitionIdByIndex(i);
        auto field = getOrCreateFieldInput(fd);

        fd->initializeField(field);
    }
}

Type* Obj::getType() const {
    return type;
}

bool Obj::isInstanceOf(Type* t) {
    return type->isInstanceOf(t);
}

Field* Obj::getFieldInput(FieldDefinition* fd) const {
    short fieldIndex = type->getFlattenedTypeInputSide()->getFieldIndex(fd);
    return fields[fieldIndex];
}

Field* Obj::getFieldOutput(FieldDefinition* fd) const {
    short fieldIndex = type->getFlattenedTypeOutputSide()->getFieldIndex(fd);
    return fields[fieldIndex];
}

Field* Obj::getOrCreateFieldInput(FieldDefinition* fd) {
    short fieldIndex = type->getFlattenedTypeInputSide()->getFieldIndex(fd);

    auto field = fields[fieldIndex];
    if (field == nullptr) {
        field = new Field(this, fd, fieldIndex);
        fields[fieldIndex] = field;
    }
    return field;
}

Obj& Obj::setFieldValue(FieldDefinition* fd, double v) {
    getOrCreateFieldInput(fd)->setValue(v);
    return *this;
}

double Obj::getFieldValue(FieldDefinition* fd) {
    auto f = getFieldOutput(fd);
    return f ? f->getValue() : 0.0;
}

double Obj::getFieldUpdatedValue(FieldDefinition* fd) {
    auto f = getFieldOutput(fd);
    return f ? f->getUpdatedValue() : 0.0;
}

Field** Obj::getFields() {
    return fields;
}

std::string Obj::toKeyString() {
    return ""; // Implementation as needed
}

std::string Obj::toString() const {
    std::stringstream ss;
    ss << type->getId() << ":" << type->getName();
    return ss.str();
}

std::string Obj::getFieldAsString(FieldDefinition* fd) const {
    std::stringstream ss;
    ss << toString() << " fd:<" << fd->toString() << "> Number of inputs:" << fd->getInputs().size() << std::endl;

    for (auto fl : fd->getInputs()) {
        ss << "  arg:" << fl->getArgumentAsString() << " rel:<" << fl->getRelation()->getRelationId() << ":" << fl->getRelation()->getRelationLabel() << "> inputField:" << fl->getRelatedFD()->getName() << std::endl;
    }

    return ss.str();
}
