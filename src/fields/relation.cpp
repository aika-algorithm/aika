
#include "fields/relation.h"


Relation::Relation(int relationId, const std::string& relationName)
    : relationId(relationId), relationName(relationName), reversed(nullptr) {}

int Relation::getRelationId() const {
    return relationId;
}

void Relation::setReversed(Relation* reversed) {
    this->reversed = reversed;
}

Relation* Relation::getReverse() {
    return reversed;
}

std::string Relation::toString() const {
    return relationName + " -> " + reversed->getRelationLabel();
}


RelationMany::RelationMany(int relationId, const std::string& relationName)
    : Relation(relationId, relationName) {}

RelatedObjectIterable& RelationMany::followMany(Obj* fromObj) {
    return fromObj->followManyRelation(this); // Call the followManyRelation method of the Obj object
}

bool RelationMany::testRelation(Obj* fromObj, Obj* toObj) const {
    return getReverse()->testRelation(toObj, fromObj); // Test the reverse relation
}

std::string RelationMany::getRelationLabel() const {
    return relationName + " (Many)"; // Append " (Many)" to the relation name
}


RelationOne::RelationOne(int relationId, const std::string& relationName)
    : Relation(relationId, relationName) {}

Obj* RelationOne::followOne(Obj* fromObj) const {
    return fromObj->followSingleRelation(this); // Call the followSingleRelation method of the Obj object
}

RelatedObjectIterable& RelationOne::followMany(Obj* fromObj) const {
    auto toObj = followOne(fromObj); // Follow the relation one
    return toObj != nullptr ? std::vector<Obj*>{toObj} : std::vector<Obj*>(); // Return as a vector
}

bool RelationOne::testRelation(Obj* fromObj, Obj* toObj) const {
    return followOne(fromObj) == toObj; // Test if the single related object matches the given one
}

std::string RelationOne::getRelationLabel() const {
    return relationName + " (One)"; // Append " (One)" to the relation name
}


RelationSelf::RelationSelf(int relationId, const std::string& relationName)
    : RelationOne(relationId, relationName) {}

void RelationSelf::setReversed(Relation* reversed) {
    throw std::logic_error("Self-relation cannot be reversed");
}

Relation* RelationSelf::getReverse() const {
    return this; // Return itself as the reverse
}

Obj* RelationSelf::followOne(Obj* fromObj) const {
    return fromObj; // A self-relation follows to the same object
}

RelatedObjectIterable& RelationSelf::followMany(Obj* fromObj) const {
    return {fromObj}; // A self-relation follows to the same object
}

bool RelationSelf::testRelation(Obj* fromObj, Obj* toObj) const {
    return fromObj == toObj; // A self-relation tests true if both objects are the same
}
