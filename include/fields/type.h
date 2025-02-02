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

class TypeRegistry;
class FieldDefinition;
class Relation;
class FlattenedType;
class Obj;
class Field;

class Type {

public:
    static std::function<bool(const std::shared_ptr<Type>&, const std::shared_ptr<Type>&)> TYPE_COMPARATOR;

    short id;
    std::string name;
    std::vector<std::shared_ptr<Type>> parents;
    std::vector<std::shared_ptr<Type>> children;
    std::shared_ptr<TypeRegistry> registry;
    std::set<std::shared_ptr<FieldDefinition>> fieldDefinitions;
    std::vector<std::shared_ptr<Relation>> relations;
    std::shared_ptr<FlattenedType> flattenedTypeInputSide;
    std::shared_ptr<FlattenedType> flattenedTypeOutputSide;
    std::optional<int> depth;

public:
    Type(std::shared_ptr<TypeRegistry> registry, const std::string& name);

    short getId() const;
    bool isAbstract() const;
    std::vector<std::shared_ptr<Relation>> getRelations() const;
    void initFlattenedType();
    std::set<std::shared_ptr<FieldDefinition>> getCollectFlattenedFieldDefinitions();
    std::set<std::shared_ptr<Type>> collectTypes() const;
    void collectTypesRecursiveStep(std::set<std::shared_ptr<Type>>& sortedTypes);
    int getDepth();
    bool isInstanceOf(std::shared_ptr<Obj> obj);
    bool isInstanceOf(std::shared_ptr<Type> type);
    std::string getName() const;
    std::shared_ptr<TypeRegistry> getTypeRegistry();
    std::shared_ptr<FlattenedType> getFlattenedTypeInputSide();
    std::shared_ptr<FlattenedType> getFlattenedTypeOutputSide();
    void setFieldDefinition(std::shared_ptr<FieldDefinition> fieldDef);
    std::set<std::shared_ptr<FieldDefinition>> getFieldDefinitions() const;
    Type& addParent(std::shared_ptr<Type> p);
    std::vector<std::shared_ptr<Type>> getParents() const;
    std::vector<std::shared_ptr<Type>> getChildren() const;

    template <typename R>
    R getFromParent(std::function<R(std::shared_ptr<Type>)> f);

    std::string toString() const;
};

#endif // TYPE_H
