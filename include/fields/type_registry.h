#include <vector>
#include <set>
#include <memory>


#ifndef TYPEREGISTRY_H
#define TYPEREGISTRY_H


class Type;


class TypeRegistry {
private:
    std::vector<std::shared_ptr<Type>> types;
    int fieldIdCounter = 0;

public:
    TypeRegistry();

    // Registers a type and returns a short ID
    short registerType(std::shared_ptr<Type> type);

    // Retrieves a type by ID
    template <typename T>
    std::shared_ptr<T> getType(short typeId);

    // Returns all registered types
    std::vector<std::shared_ptr<Type>> getTypes() const;

    // Creates and returns a unique field ID
    int createFieldId();

    // Returns the number of field definitions created
    int getNumberOfFieldDefinitions() const;

    // Flattens the type hierarchy
    void flattenTypeHierarchy();

    // Comparator for sorting types (if needed)
    struct TypeComparator {
        bool operator()(const std::shared_ptr<Type>& t1, const std::shared_ptr<Type>& t2) const;
    };
};


#endif //TYPEREGISTRY_H
