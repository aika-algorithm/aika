#include "fields/test_object.h"

// Constructor
TestObject::TestObject(TestType* type) : Obj(type) {}

// Follow a single relation
Obj* TestObject::followSingleRelation(const Relation* rel) {
    if (rel == &TestType::TEST_RELATION_FROM || rel == &TestType::TEST_RELATION_TO) {
        return getRelatedTestObject();
    } else {
        throw std::runtime_error("Invalid Relation");
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
