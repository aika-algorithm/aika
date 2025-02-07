
#include "fields/type.h"
#include "fields/type_registry.h"


TypeRegistry::TypeRegistry() {
}

short TypeRegistry::registerType(Type* type) {
    short id = types.size();
    types.push_back(type);
    return id;
}

Type* TypeRegistry::getType(int typeId) {
    return types[typeId];
}

std::vector<Type*> TypeRegistry::getTypes() const {
    return types;
}

int TypeRegistry::createFieldId() {
    return fieldIdCounter++;
}

int TypeRegistry::getNumberOfFieldDefinitions() const {
    return fieldIdCounter;
}

void TypeRegistry::flattenTypeHierarchy() {
    // TreeSet equivalent is std::set in C++, sorted by the comparator
    std::set<std::shared_ptr<Type>, TypeComparator> sortedTypes(types.begin(), types.end());
/*
    for (const auto& type : sortedTypes) {
        type->initFlattenedType();
    }

    for (const auto& type : sortedTypes) {
        type->getFlattenedTypeInputSide().flatten();
        type->getFlattenedTypeOutputSide().flatten();
    }
 */
}

bool TypeRegistry::TypeComparator::operator()(const Type* t1, const Type* t2) const {
    // Implement comparison logic here (e.g., based on some attribute of Type)
    // Example:
    // return t1->someAttribute() < t2->someAttribute();
    return false; // Example placeholder, you need to implement the real comparison logic
}
