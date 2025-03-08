#ifndef TEST_TYPE_H
#define TEST_TYPE_H

#include "fields/type.h"
#include "fields/relation.h"
#include "fields/type_registry.h"
#include <vector>

class TestObject;

class TestType : public Type {
public:
    // Static constants
    static RelationOne SELF;
    static RelationOne TEST_RELATION_FROM;
    static RelationOne TEST_RELATION_TO;

    // Constructor
    TestType(TypeRegistry* registry, const std::string& name);

    // Method to instantiate a new TestObject
    TestObject* instantiate();
};

#endif // TEST_TYPE_H
