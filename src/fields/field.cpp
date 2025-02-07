#include <stdexcept>
#include <iostream>
#include <sstream>

#include "fields/field.h"
#include "fields/obj.h"
#include "fields/field_definition.h"
#include "fields/queue_interceptor.h"

Field::Field(Obj* obj, FieldDefinition* fd, int id)
    : object(obj), fieldDefinition(fd), id(id), value(0.0), updatedValue(0.0), withinUpdate(false), interceptor(nullptr) {}

int Field::getId() const {
    return id;
}

bool Field::isWithinUpdate() {
    return withinUpdate;
}

double Field::getValue() {
    return value;
}

double Field::getUpdatedValue() {
    return withinUpdate ? updatedValue : value;
}

Obj* Field::getObject() {
    return object;
}

Field& Field::setQueued(Queue* q, ProcessingPhase* phase, bool isNextRound) {
    interceptor = std::make_shared<QueueInterceptor>(q, this, phase, isNextRound);
    return *this;
}

FieldDefinition* Field::getFieldDefinition() {
    return fieldDefinition;
}

double Field::getTolerance() const {
    return fieldDefinition->getTolerance().value_or(0.0);
}

std::string Field::getName() const {
    return fieldDefinition->getName();
}

QueueInterceptor* Field::getInterceptor() {
    return interceptor;
}

void Field::setInterceptor(QueueInterceptor* interceptor) {
    this->interceptor = interceptor;
}

void Field::setValue(double v) {
    withinUpdate = true;
    updatedValue = v;
    propagateUpdate();
}

void Field::triggerUpdate(double u) {
//    if (ToleranceUtils::belowTolerance(getTolerance(), u)) {
//        return;
//    }

    withinUpdate = true;
    updatedValue = value + u;
    propagateUpdate();
}

void Field::propagateUpdate() {
    object->getType()->getFlattenedTypeOutputSide()->followLinks(this);
    value = updatedValue;
    withinUpdate = false;
}

double Field::getUpdate() const {
    return updatedValue - value;
}

void Field::receiveUpdate(double u) {
    if (interceptor != nullptr) {
        interceptor->receiveUpdate(u, false);
        return;
    }

    if (withinUpdate) {
        throw std::logic_error("Field is already within update.");
    }
    triggerUpdate(u);
}

std::string Field::toString() const {
    std::stringstream ss;
    ss << getName() << ": " << getValueString();
    return ss.str();
}

std::string Field::getValueString() const {
    return toString();
}