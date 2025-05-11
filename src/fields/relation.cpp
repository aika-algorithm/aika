#include "fields/relation.h"
#include <stdexcept>


Relation::Relation(int relationId, const std::string& relationName)
    : relationId(relationId), relationName(relationName), reversed(nullptr) {}

int Relation::getRelationId() const {
    return relationId;
}

void Relation::setReversed(Relation* rev) {
    reversed = rev;
}

Relation* Relation::getReverse() {
    return reversed;
}

RelationMany::RelationMany(int relationId, const std::string& relationName)
    : Relation(relationId, relationName) {}

RelatedObjectIterable* RelationMany::followMany(Obj* fromObj) {
    return fromObj->followManyRelation(this); // Call the followManyRelation method of the Obj object
}

bool RelationMany::testRelation(Obj* fromObj, Obj* toObj) const {
    return reversed->testRelation(toObj, fromObj); // Test the reverse relation
}

std::string RelationMany::getRelationLabel() const {
    return relationName + " (Many)"; // Append " (Many)" to the relation name
}

RelationOne::RelationOne(int relationId, const std::string& relationName)
    : Relation(relationId, relationName) {}

Obj* RelationOne::followOne(Obj* fromObj) const {
    return fromObj->followSingleRelation(this); // Call the followSingleRelation method of the Obj object
}

RelatedObjectIterable* RelationOne::followMany(Obj* fromObj) {
    auto toObj = followOne(fromObj); // Follow the relation one
    return new SingleObjectIterable(toObj); // Return as a vector
}

bool RelationOne::testRelation(Obj* fromObj, Obj* toObj) const {
    return followOne(fromObj) == toObj; // Test if the single related object matches the given one
}

std::string RelationOne::getRelationLabel() const {
    return relationName + " (One)"; // Append " (One)" to the relation name
}

// RelationSelf implementation
RelationSelf::RelationSelf(int relationId, const std::string& relationName)
    : RelationOne(relationId, relationName) {}

Obj* RelationSelf::followOne(Obj* fromObj) const {
    return fromObj; // Return the same object
}

RelatedObjectIterable* RelationSelf::followMany(Obj* fromObj) {
    return new SingleObjectIterable(fromObj); // Return a singleton iterable with the same object
}

bool RelationSelf::testRelation(Obj* fromObj, Obj* toObj) const {
    return fromObj == toObj; // Test if both objects are the same
}

std::string RelationSelf::getRelationLabel() const {
    return relationName + " (Self)"; // Append " (Self)" to the relation name
}