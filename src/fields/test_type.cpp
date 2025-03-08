#include "fields/test_type.h"
#include "fields/test_object.h"

// Initialize static members
RelationOne TestType::SELF(0, "TEST_SELF");
RelationOne TestType::TEST_RELATION_FROM(1, "TEST_FROM");
RelationOne TestType::TEST_RELATION_TO(2, "TEST_TO");

// Static block equivalent (executed once during initialization)
struct StaticInitializer {
    StaticInitializer() {
        TestType::TEST_RELATION_TO.setReversed(&TestType::TEST_RELATION_FROM);
        TestType::TEST_RELATION_FROM.setReversed(&TestType::TEST_RELATION_TO);
    }
} staticInitializerInstance;

// Constructor
TestType::TestType(TypeRegistry* registry, const std::string& name)
    : Type(registry, name) {
    // Add relations to the list
    relations.push_back(&SELF);
    relations.push_back(&TEST_RELATION_FROM);
    relations.push_back(&TEST_RELATION_TO);
}

// Instantiate method
TestObject* TestType::instantiate() {
    return new TestObject(this);
}
