#include "fields/type.h"
#include "fields/obj.h"
#include "fields/type_registry.h"
#include "fields/field_definition.h"
#include "fields/flattened_type.h"
#include <iostream>
#include <set>
#include <algorithm>
#include <stdexcept>

bool Type::TypeComparator::operator()(const Type *t1, const Type *t2) const {
    if (t1->getDepth() != t2->getDepth()) return t1->getDepth() < t2->getDepth();
    return t1->getId() < t2->getId();
};

Type::Type(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), depth(std::nullopt) {
    id = registry->registerType(this);
}

short Type::getId() const {
    return id;
}

bool Type::isAbstract() const {
    return !children.empty();
}

std::vector<Relation*> Type::getRelations() const {
    return relations;
}

void Type::initFlattenedType() {
    auto fieldDefs = getCollectFlattenedFieldDefinitions();

    flattenedTypeInputSide = FlattenedType::createInputFlattenedType(this, fieldDefs);
    flattenedTypeOutputSide = FlattenedType::createOutputFlattenedType(this, fieldDefs, flattenedTypeInputSide);
}

std::set<FieldDefinition*> Type::getCollectFlattenedFieldDefinitions() {
    std::set<FieldDefinition*> fieldDefs;
    for (const auto& t : collectTypes()) {
        auto tFieldDefs = t->getFieldDefinitions();
        fieldDefs.insert(tFieldDefs.begin(), tFieldDefs.end());
    }
    return fieldDefs;
}

std::set<Type*> Type::collectTypes() {
    std::set<Type*> sortedTypes;
    collectTypesRecursiveStep(sortedTypes);
    return sortedTypes;
}

void Type::collectTypesRecursiveStep(std::set<Type*>& sortedTypes) {
    for (const auto& p : parents) {
        p->collectTypesRecursiveStep(sortedTypes);
    }
    sortedTypes.insert(this);
}

void Type::initDepth() {
    if (!depth.has_value()) {
        depth = 0;
        for (const auto& p : parents) {
            depth = std::max(depth.value(), p->getDepth() + 1);
        }
    }
}

int Type::getDepth() const {
    return depth.value();
}

bool Type::isInstanceOf(Obj* obj) {
    return isInstanceOf(obj->getType());
}

bool Type::isInstanceOf(Type* type) {
    return id == type->id || std::any_of(parents.begin(), parents.end(), [&](const auto& p) {
        return p->isInstanceOf(type);
    });
}

std::string Type::getName() const {
    return name;
}

TypeRegistry* Type::getTypeRegistry() {
    return registry;
}

FlattenedType* Type::getFlattenedTypeInputSide() {
    if (!flattenedTypeInputSide) {
        throw std::runtime_error("Type has not been flattened yet. TypeRegistry.flattenTypeHierarchy() needs to be called beforehand.");
    }
    return flattenedTypeInputSide;
}

FlattenedType* Type::getFlattenedTypeOutputSide() {
    if (!flattenedTypeOutputSide) {
        throw std::runtime_error("Type has not been flattened yet. TypeRegistry.flattenTypeHierarchy() needs to be called beforehand.");
    }
    return flattenedTypeOutputSide;
}

void Type::setFieldDefinition(FieldDefinition* fieldDef) {
    fieldDef->setFieldId(registry->createFieldId());
    fieldDefinitions.insert(fieldDef);
}

std::set<FieldDefinition*> Type::getFieldDefinitions() const {
    return fieldDefinitions;
}

Type& Type::addParent(Type* p) {
    parents.push_back(p);
    p->children.push_back(this);
    return *this;
}

std::vector<Type*> Type::getParents() const {
    return parents;
}

std::vector<Type*> Type::getChildren() const {
    return children;
}

template <typename R>
R Type::getFromParent(std::function<R(Type*)> f) {
    for (const auto& p : parents) {
        R result = f(p);
        if (result) {
            return result;
        }
    }
    return nullptr;
}

std::string Type::toString() const {
    return name;
}

