#include "fields/type.h"
#include "fields/obj.h"
#include "fields/type_registry.h"
#include <iostream>
#include <set>
#include <algorithm>
#include <stdexcept>

std::function<bool(const std::shared_ptr<Type>&, const std::shared_ptr<Type>&)> Type::TYPE_COMPARATOR =
    [](const std::shared_ptr<Type>& t1, const std::shared_ptr<Type>& t2) {
        if (t1->getDepth() != t2->getDepth()) return t1->getDepth() < t2->getDepth();
        return t1->getId() < t2->getId();
    };

Type::Type(std::shared_ptr<TypeRegistry> registry, const std::string& name)
    : registry(registry), name(name), depth(std::nullopt) {
    id = registry->registerType(std::shared_ptr<Type>(this));
}

short Type::getId() const {
    return id;
}

bool Type::isAbstract() const {
    return !children.empty();
}

std::vector<std::shared_ptr<Relation>> Type::getRelations() const {
    return relations;
}

void Type::initFlattenedType() {
    auto fieldDefs = getCollectFlattenedFieldDefinitions();
//    flattenedTypeInputSide = createInputFlattenedType(std::shared_ptr<Type>(this), fieldDefs);
//    flattenedTypeOutputSide = createOutputFlattenedType(std::shared_ptr<Type>(this), fieldDefs, flattenedTypeInputSide);
}

std::set<std::shared_ptr<FieldDefinition>> Type::getCollectFlattenedFieldDefinitions() {
    std::set<std::shared_ptr<FieldDefinition>> fieldDefs;
    for (const auto& t : collectTypes()) {
        auto tFieldDefs = t->getFieldDefinitions();
        fieldDefs.insert(tFieldDefs.begin(), tFieldDefs.end());
    }
    return fieldDefs;
}

std::set<std::shared_ptr<Type>> Type::collectTypes() const {
    std::set<std::shared_ptr<Type>> sortedTypes;
    collectTypesRecursiveStep(sortedTypes);
    return sortedTypes;
}

void Type::collectTypesRecursiveStep(std::set<std::shared_ptr<Type>>& sortedTypes) {
    for (const auto& p : parents) {
        p->collectTypesRecursiveStep(sortedTypes);
    }
    sortedTypes.insert(std::shared_ptr<Type>(this));
}

int Type::getDepth() {
    if (!depth.has_value()) {
        depth = 0;
        for (const auto& p : parents) {
            depth = std::max(depth.value(), p->getDepth() + 1);
        }
    }
    return depth.value();
}

bool Type::isInstanceOf(std::shared_ptr<Obj> obj) {
    return isInstanceOf(obj->getType());
}

bool Type::isInstanceOf(std::shared_ptr<Type> type) {
    return this == type || std::any_of(parents.begin(), parents.end(), [&](const auto& p) {
        return p->isInstanceOf(type);
    });
}

std::string Type::getName() const {
    return name;
}

std::shared_ptr<TypeRegistry> Type::getTypeRegistry() {
    return registry;
}

std::shared_ptr<FlattenedType> Type::getFlattenedTypeInputSide() {
    if (!flattenedTypeInputSide) {
        throw std::runtime_error("Type has not been flattened yet. TypeRegistry.flattenTypeHierarchy() needs to be called beforehand.");
    }
    return flattenedTypeInputSide;
}

std::shared_ptr<FlattenedType> Type::getFlattenedTypeOutputSide() {
    if (!flattenedTypeOutputSide) {
        throw std::runtime_error("Type has not been flattened yet. TypeRegistry.flattenTypeHierarchy() needs to be called beforehand.");
    }
    return flattenedTypeOutputSide;
}

void Type::setFieldDefinition(std::shared_ptr<FieldDefinition> fieldDef) {
    fieldDef->setFieldId(registry->createFieldId());
    fieldDefinitions.insert(fieldDef);
}

std::set<std::shared_ptr<FieldDefinition>> Type::getFieldDefinitions() const {
    return fieldDefinitions;
}

Type& Type::addParent(std::shared_ptr<Type> p) {
    parents.push_back(p);
    p->children.push_back(std::shared_ptr<Type>(this));
    return *this;
}

std::vector<std::shared_ptr<Type>> Type::getParents() const {
    return parents;
}

std::vector<std::shared_ptr<Type>> Type::getChildren() const {
    return children;
}

template <typename R>
R Type::getFromParent(std::function<R(std::shared_ptr<Type>)> f) {
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

// Explicit instantiation of shared_from_this
template std::shared_ptr<Type> Type::getFromParent<std::shared_ptr<Type>>(std::function<std::shared_ptr<Type>(std::shared_ptr<Type>)>);
