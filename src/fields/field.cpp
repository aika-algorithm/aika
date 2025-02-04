#include <stdexcept>
#include <iostream>
#include <sstream>

#include "fields/field.h"
#include "fields/obj.h"
#include "fields/field_definition.h"
#include "fields/queue_interceptor.h"

Field::Field(std::shared_ptr<Obj> obj, std::shared_ptr<FieldDefinition> fd, short id)
    : object(obj), fieldDefinition(fd), id(id), value(0.0), updatedValue(0.0), withinUpdate(false), interceptor(nullptr) {}

short Field::getId() const {
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

std::shared_ptr<Obj> Field::getObject() {
    return object;
}

Field& Field::setQueued(std::shared_ptr<Queue> q, std::shared_ptr<ProcessingPhase> phase, bool isNextRound) {
    interceptor = std::make_shared<QueueInterceptor>(q, std::shared_ptr<Field>(this), phase, isNextRound);
    return *this;
}

std::shared_ptr<FieldDefinition> Field::getFieldDefinition() {
    return fieldDefinition;
}

double Field::getTolerance() const {
    return fieldDefinition->getTolerance().value_or(0.0);
}

std::string Field::getName() const {
    return fieldDefinition->getName();
}

std::shared_ptr<QueueInterceptor> Field::getInterceptor() {
    return interceptor;
}

void Field::setInterceptor(std::shared_ptr<QueueInterceptor> interceptor) {
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
    object->getType()->getFlattenedTypeOutputSide()->followLinks(std::shared_ptr<Field>(this));
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