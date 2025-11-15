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
#include <set>

#include "fields/field_definition.h"
#include "fields/direction.h"

class Type;
class FieldDefinition;

/**
 * @class FlattenedType
 * @brief Optimized representation of type hierarchies with field index mapping.
 *
 * The FlattenedType class provides:
 * - Efficient bidirectional mapping between field definitions and array indices
 * - Separate input/output maps based on link presence
 * - Field inheritance resolution through flattening
 *
 * Relations are resolved at runtime during propagation, not stored in flattening.
 * Flattening focuses on field hierarchy management and index assignment.
 */
class FlattenedType {
private:
    Direction* direction;
    Type* type;                                                  ///< The type being flattened

    int numberOfFields;
    int* fields;                                                 ///< FieldDefinition ID → index mapping
    FieldDefinition*** fieldsReverse;                           ///< Index → FieldDefinitions (null-terminated)

    FlattenedType(Direction* dir, Type* type, const std::map<FieldDefinition*, int>& fieldMappings, int numberOfFields);

public:
    static FlattenedType* createInputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs);
    static FlattenedType* createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide);

    int getFieldIndex(FieldDefinition* fd);
    int getNumberOfFields() const;
    Type* getType() const;
    Direction* getDirection() const;

    FieldDefinition*** getFieldsReverse();
    FieldDefinition* getFieldDefinitionIdByIndex(short idx);
};

#endif // FLATTENED_TYPE_H
