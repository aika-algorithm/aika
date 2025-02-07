
#ifndef TYPE_REGISTRY_H
#define TYPE_REGISTRY_H

#include <vector>

class Type;


class TypeRegistry {
private:
    std::vector<Type*> types;
    int fieldIdCounter = 0;

public:
    TypeRegistry();

    // Registers a type and returns a short ID
    short registerType(Type* type);

    // Retrieves a type by ID
    Type* getType(int typeId);

    // Returns all registered types
    std::vector<Type*> getTypes() const;

    // Creates and returns a unique field ID
    int createFieldId();

    // Returns the number of field definitions created
    int getNumberOfFieldDefinitions() const;

    // Flattens the type hierarchy
    void flattenTypeHierarchy();

    // Comparator for sorting types (if needed)
    struct TypeComparator {
        bool operator()(const Type* t1, const Type* t2) const;
    };
};


#endif //TYPE_REGISTRY_H
