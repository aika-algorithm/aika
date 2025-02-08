
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
    for (const auto& type : types) {
        type->initDepth();
    }

    // TreeSet equivalent is std::set in C++, sorted by the comparator
    std::set<Type*, Type::TypeComparator> sortedTypes(types.begin(), types.end());

    for (const auto& type : sortedTypes) {
        type->initFlattenedType();
    }

    for (const auto& type : sortedTypes) {
        type->getFlattenedTypeInputSide()->flatten();
        type->getFlattenedTypeOutputSide()->flatten();
    }
}
