/**
 * @file flattened_type.h
 * @brief Defines the FlattenedType class, which provides an optimized representation of type hierarchies.
 * 
 * The FlattenedType class flattens the type hierarchy for efficient runtime access
 * to field definitions and relations. It maps field definitions to indices and
 * maintains flattened relations for quick traversal.
 */

#ifndef FLATTENED_TYPE_H
#define FLATTENED_TYPE_H

#include <map>

#include "fields/field_definition.h"
#include "fields/flattened_type_relation.h"
#include "fields/direction.h"

class Type;
class Field;
class FieldDefinition;
class FlattenedTypeRelation;

/**
 * @class FlattenedType
 * @brief Optimized representation of type hierarchies.
 * 
 * The FlattenedType class provides:
 * - Efficient mapping of field definitions to indices
 * - Flattened relations for quick traversal
 * - Link following for update propagation
 * - Type hierarchy optimization
 * 
 * This class is crucial for performance in large field graphs,
 * as it eliminates the need to traverse the type hierarchy at runtime.
 */
class FlattenedType {
private:
    Direction* direction;
    Type* type;                                                  ///< The type being flattened
    
    int numberOfFields;
    int* fields;
    FieldDefinition*** fieldsReverse; // Number of fields | List of rev. field mappings terminated by nullptr.
    FlattenedTypeRelation*** mapping; // Number of Relations | Number of Types

    FlattenedType(Direction* dir, Type* type, const std::map<FieldDefinition*, int>& fieldMappings, int numberOfFields);
    FlattenedTypeRelation* flattenPerType(Relation* relation, Type* relatedType);

public:
    static FlattenedType* createInputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs);
    static FlattenedType* createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide);

    void flatten();
    void followLinks(Field* field);

    int getFieldIndex(FieldDefinition* fd);
    int getNumberOfFields() const;
    Type* getType() const;
    Direction* getDirection() const;

    FieldDefinition*** getFieldsReverse();
    FieldDefinition* getFieldDefinitionIdByIndex(short idx);

    void followLinks(FlattenedTypeRelation* ftr, Object* relatedObj, Field* field);
};

#endif // FLATTENED_TYPE_H
