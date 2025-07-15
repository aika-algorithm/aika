#ifndef TYPE_H
#define TYPE_H

#include <string>
#include <memory>
#include <vector>
#include <set>
#include <functional>
#include <algorithm>
#include <stdexcept>
#include <iostream>

#include "fields/flattened_type.h"

class TypeRegistry;
class FieldDefinition;
class Relation;
class Object;
class Field;

class Type {

public:
    struct TypeComparator {
        bool operator()(const Type* t1, const Type* t2) const;
    };

    int id;
    std::string name;
    std::vector<Type*> parents;
    std::vector<Type*> children;
    TypeRegistry* registry;
    std::set<FieldDefinition*> fieldDefinitions;
    std::vector<Relation*> relations;
    FlattenedType* flattenedTypeInputSide;
    FlattenedType* flattenedTypeOutputSide;
    std::optional<int> depth;

    void initDepth();

public:
    Type(TypeRegistry* registry, const std::string& name);

    short getId() const;
    bool isAbstract() const;
    std::vector<Relation*> getRelations() const;
    void initFlattenedType();
    std::set<FieldDefinition*> getCollectFlattenedFieldDefinitions();
    std::set<Type*> collectTypes();
    void collectTypesRecursiveStep(std::set<Type*>& sortedTypes);
    int getDepth() const;
    bool isInstanceOf(Object* obj) const;
    bool isInstanceOf(Type* type) const;
    std::string getName() const;
    TypeRegistry* getTypeRegistry() const;
    FlattenedType* getFlattenedTypeInputSide() const;
    FlattenedType* getFlattenedTypeOutputSide() const;
    void setFieldDefinition(FieldDefinition* fieldDef);
    std::set<FieldDefinition*> getFieldDefinitions() const;
    Type& addParent(Type* p);
    std::vector<Type*> getParents() const;
    std::vector<Type*> getChildren() const;

    template <typename R>
    R getFromParent(std::function<R(Type*)> f);

    std::string toString() const;
};

#endif // TYPE_H
