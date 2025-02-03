
#include "fields/relation.h"


Relation::Relation(int relationId, const std::string& relationName)
    : relationId(relationId), relationName(relationName), reversed(nullptr) {}

int Relation::getRelationId() const {
    return relationId;
}

void Relation::setReversed(std::shared_ptr<Relation> reversed) {
    this->reversed = reversed;
}

std::shared_ptr<Relation> Relation::getReverse() {
    return reversed;
}

std::string Relation::toString() const {
    return relationName + " -> " + reversed->getRelationLabel();
}


RelationMany::RelationMany(int relationId, const std::string& relationName)
    : Relation(relationId, relationName) {}

std::vector<std::shared_ptr<Obj>> RelationMany::followMany(std::shared_ptr<Obj> fromObj) const {
    return fromObj->followManyRelation(shared_from_this()); // Call the followManyRelation method of the Obj object
}

bool RelationMany::testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const {
    return getReverse()->testRelation(toObj, fromObj); // Test the reverse relation
}

std::string RelationMany::getRelationLabel() const {
    return relationName + " (Many)"; // Append " (Many)" to the relation name
}


RelationOne::RelationOne(int relationId, const std::string& relationName)
    : Relation(relationId, relationName) {}

std::shared_ptr<Obj> RelationOne::followOne(std::shared_ptr<Obj> fromObj) const {
    return fromObj->followSingleRelation(shared_from_this()); // Call the followSingleRelation method of the Obj object
}

std::vector<std::shared_ptr<Obj>> RelationOne::followMany(std::shared_ptr<Obj> fromObj) const {
    auto toObj = followOne(fromObj); // Follow the relation one
    return toObj != nullptr ? std::vector<std::shared_ptr<Obj>>{toObj} : std::vector<std::shared_ptr<Obj>>(); // Return as a vector
}

bool RelationOne::testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const {
    return followOne(fromObj) == toObj; // Test if the single related object matches the given one
}

std::string RelationOne::getRelationLabel() const {
    return relationName + " (One)"; // Append " (One)" to the relation name
}


RelationSelf::RelationSelf(int relationId, const std::string& relationName)
    : RelationOne(relationId, relationName) {}

void RelationSelf::setReversed(std::shared_ptr<Relation> reversed) {
    throw std::logic_error("Self-relation cannot be reversed");
}

std::shared_ptr<Relation> RelationSelf::getReverse() const {
    return shared_from_this(); // Return itself as the reverse
}

std::shared_ptr<Obj> RelationSelf::followOne(std::shared_ptr<Obj> fromObj) const {
    return fromObj; // A self-relation follows to the same object
}

std::vector<std::shared_ptr<Obj>> RelationSelf::followMany(std::shared_ptr<Obj> fromObj) const {
    return {fromObj}; // A self-relation follows to the same object
}

bool RelationSelf::testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const {
    return fromObj == toObj; // A self-relation tests true if both objects are the same
}
