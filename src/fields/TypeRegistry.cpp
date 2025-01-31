
#include "Type.h"
#include "TypeRegistry.h"


short TypeRegistry::registerType(std::shared_ptr<Type> type) {
    short id = types.size();
    types.push_back(type);
    return id;
}

template <typename T>
std::shared_ptr<T> TypeRegistry::getType(short typeId) {
    return std::dynamic_pointer_cast<T>(types[typeId]);
}

std::vector<std::shared_ptr<Type>> TypeRegistry::getTypes() const {
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

    for (const auto& type : sortedTypes) {
        type->initFlattenedType();
    }
/*
    for (const auto& type : sortedTypes) {
        type->getFlattenedTypeInputSide().flatten();
        type->getFlattenedTypeOutputSide().flatten();
    }
 */
}

bool TypeRegistry::TypeComparator::operator()(const std::shared_ptr<Type>& t1, const std::shared_ptr<Type>& t2) const {
    // Implement comparison logic here (e.g., based on some attribute of Type)
    // Example:
    // return t1->someAttribute() < t2->someAttribute();
    return false; // Example placeholder, you need to implement the real comparison logic
}
