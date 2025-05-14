#include "fields/test_object.h"

// Constructor
TestObject::TestObject(TestType* type) : Obj(type) {}

// Follow a single relation
Obj* TestObject::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationId() == TestType::TEST_RELATION_FROM.getRelationId() || rel->getRelationId() == TestType::TEST_RELATION_TO.getRelationId()) {
        return getRelatedTestObject();
    } else {
        if(rel != nullptr) {
            throw std::runtime_error("Object:" + this->toString() + " Invalid Relation:" + std::to_string(rel->getRelationId()) + ":" + rel->getRelationLabel());
        } else {
            throw std::runtime_error("Object:" + this->toString() + " Invalid Relation: nullptr");
        }
    }
}

// Get related test object
TestObject* TestObject::getRelatedTestObject() const {
    return relatedTestObject;
}

// Static method to link two objects
void TestObject::linkObjects(TestObject* objA, TestObject* objB) {
    objA->relatedTestObject = objB;
    objB->relatedTestObject = objA;
}

// Follow a many-relation (returns a vector instead of Stream)
RelatedObjectIterable* TestObject::followManyRelation(Relation* rel) const {
    return new SingleObjectIterable(relatedTestObject);
}

Queue* TestObject::getQueue() const {
    return nullptr;
}
