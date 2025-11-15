/**
 * @file flattened_type.cpp
 * @brief Implementation of the FlattenedType class.
 * 
 * This file contains the implementation of the FlattenedType class methods,
 * which provide optimized type hierarchy representation and traversal.
 */

#include "fields/flattened_type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"
#include "fields/object.h"
#include "fields/field.h"
#include "fields/utils.h"
#include "fields/queue_interceptor.h"
#include "fields/null_terminated_array.h"


/**
 * @brief Constructs a FlattenedType for the given type
 * 
 * Initializes the flattened representation by:
 * 1. Collecting all field definitions
 * 2. Creating index mappings
 * 3. Flattening type relations
 * 
 * @param type The type to flatten
 */
FlattenedType::FlattenedType(Direction* dir, Type* type, const std::map<FieldDefinition*, int>& fieldMappings, int numberOfFields)
    : direction(dir), type(type), numberOfFields(numberOfFields) {

    fields = new int[type->getTypeRegistry()->getNumberOfFieldDefinitions()];

    std::map<int, std::vector<FieldDefinition*>> groupedMap;
    for (const auto& e : fieldMappings) {
        fields[e.first->getId()] = e.second;
        groupedMap[e.second].push_back(e.first);
    }

    fieldsReverse = new FieldDefinition**[numberOfFields];
    for (const auto& e : groupedMap) {
        fieldsReverse[e.first] = nullTerminatedArrayFromVector<FieldDefinition*>(e.second);
    }
}

/**
 * @brief Creates a flattened type for input direction
 *
 * This function creates a flattened type representation for the input direction.
 * A field definition is included only if it has input field links.
 * ProxyFields never have input links and thus are excluded from the input map.
 *
 * @param type The type to flatten
 * @param fieldDefs The set of field definitions to include in the flattened type
 * @return A new FlattenedType object representing the input flattened type
 */
FlattenedType* FlattenedType::createInputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs) {
    std::map<FieldDefinition*, int> fieldMappings;

    // Collect field definitions that have input links
    std::vector<FieldDefinition*> fieldsWithInputs;
    for (const auto& fd : fieldDefs) {
        if (!fd->getInputs().empty()) {
            fieldsWithInputs.push_back(fd);
        }
    }

    // Assign sequential indices
    short index = 0;
    for (const auto& fd : fieldsWithInputs) {
        fieldMappings[fd] = index++;
    }

    return new FlattenedType(Direction::INPUT, type, fieldMappings, fieldsWithInputs.size());
}

/**
 * @brief Creates a flattened type for output direction
 *
 * This function creates a flattened type representation for the output direction.
 * A field definition is included only if it has output field links.
 *
 * Index assignment uses field name as the identity:
 * - If a field with the same name exists in input side, reuse its index
 * - Otherwise assign a new index
 *
 * This naturally handles ProxyFields: since they share the same name as their
 * target field, they automatically map to the same index without special handling.
 *
 * @param type The type to flatten
 * @param fieldDefs The set of field definitions to include in the flattened type
 * @param inputSide The flattened type of the input side
 * @return A new FlattenedType object representing the output flattened type
 */
FlattenedType* FlattenedType::createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide) {
    std::map<FieldDefinition*, int> fieldMappings;
    short nextNewIndex = inputSide->numberOfFields;

    // Build a name-to-index map from input side for quick lookup
    std::map<std::string, short> inputNameToIndex;
    for (int i = 0; i < inputSide->numberOfFields; i++) {
        FieldDefinition* fd = inputSide->fieldsReverse[i][0]; // Get first field at this index
        inputNameToIndex[fd->getName()] = i;
    }

    // Collect field definitions that have output links
    for (const auto& fd : fieldDefs) {
        if (fd->getOutputs().empty()) {
            continue; // Skip fields without output links
        }

        // Check if a field with this name exists in input side
        auto it = inputNameToIndex.find(fd->getName());
        short fieldIndex;
        if (it != inputNameToIndex.end()) {
            fieldIndex = it->second; // Reuse input side index
        } else {
            fieldIndex = nextNewIndex++; // Assign new index
        }

        fieldMappings[fd] = fieldIndex;
    }

    return new FlattenedType(Direction::OUTPUT, type, fieldMappings, nextNewIndex);
}

/**
 * @brief Gets the field index
 * 
 * This function gets the field index by looking up the field definition in the
 * flattened type.  
 * 
 * @param fd The field definition to get the index of
 * @return The index of the field definition
 */
int FlattenedType::getFieldIndex(FieldDefinition* fd) {
    return fields[fd->getId()];
}

/**
 * @brief Gets the number of fields
 * 
 * This function gets the number of fields in the flattened type.
 * 
 * @return The number of fields in the flattened type
 */ 
int FlattenedType::getNumberOfFields() const {
    return numberOfFields;
}

/**
 * @brief Gets the type
 * 
 * This function gets the type in the flattened type.
 * 
 * @return The type in the flattened type
 */ 
Type* FlattenedType::getType() const {
    return type;
}

/**
 * @brief Gets the direction
 * 
 * This function gets the direction in the flattened type.
 * 
 * @return The direction in the flattened type
 */ 
Direction* FlattenedType::getDirection() const {
    return direction;
}

/**
 * @brief Gets the fields reverse
 * 
 * This function gets the fields reverse in the flattened type.
 * 
 * @return The fields reverse in the flattened type
 */  
FieldDefinition*** FlattenedType::getFieldsReverse() {
    return fieldsReverse;
}

/**
 * @brief Gets the field definition by index
 * 
 * This function gets the field definition by index in the flattened type.
 * 
 * @param idx The index of the field definition to get  
 * @return The field definition by index in the flattened type
 */  
FieldDefinition* FlattenedType::getFieldDefinitionIdByIndex(short idx) {
    return fieldsReverse[idx][0];
}
