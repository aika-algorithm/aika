#ifndef TEST_OBJECT_H
#define TEST_OBJECT_H

#include "fields/object.h"
#include "fields/test_type.h"
#include "fields/relation.h"
#include <stdexcept>
#include <vector>

class TestObject : public Object {
private:
    TestObject* relatedTestObject = nullptr;

public:
    // Constructor
    explicit TestObject(TestType* type);

    // Follow a single relation
    Object* followSingleRelation(const Relation* rel) const override;

    // Get related test object
    TestObject* getRelatedTestObject() const;

    // Static method to link objects
    static void linkObjects(TestObject* objA, TestObject* objB);

    // Follow a many-relation
    RelatedObjectIterable* followManyRelation(Relation* rel) const override;

    Queue* getQueue() const override;
};

#endif // TEST_OBJECT_H
