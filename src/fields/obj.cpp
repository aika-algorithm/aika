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

Field* Obj::getFieldOutput(FieldDefinition* fd) {
    short fieldIndex = type->getFlattenedTypeOutputSide()->getFieldIndex(fd);
    return fields[fieldIndex];
}

Field* Obj::getOrCreateFieldInput(FieldDefinition* fd) {
    short fieldIndex = type->getFlattenedTypeInputSide()->getFieldIndex(fd);
    if (!fields[fieldIndex]) {
        fields[fieldIndex] = new Field(this, fd, fieldIndex);
    }
    return fields[fieldIndex];
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

Queue* Obj::getQueue() {
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
