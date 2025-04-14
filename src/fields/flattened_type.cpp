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
#include "fields/obj.h"
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
 * It takes a type and a set of field definitions, and maps the field definitions
 * to their corresponding indices in the flattened type.
 * 
 * @param type The type to flatten
 * @param fieldDefs The set of field definitions to include in the flattened type
 * @return A new FlattenedType object representing the input flattened type
 */
FlattenedType* FlattenedType::createInputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs) {
    std::map<FieldDefinition*, int> fieldMappings;

    std::vector<FieldDefinition*> requiredFields;
    for (const auto& fd : fieldDefs) {
        if (fd->isFieldRequired(fieldDefs)) {
            requiredFields.push_back(fd);
        }
    }

    for (short i = 0; i < requiredFields.size(); i++) {
        fieldMappings[requiredFields[i]] = i;
    }

    return new FlattenedType(Direction::INPUT, type, fieldMappings, requiredFields.size());
}

/**
 * @brief Creates a flattened type for output direction
 * 
 * This function creates a flattened type representation for the output direction.
 * It takes a type and a set of field definitions, and maps the field definitions
 * to their corresponding indices in the flattened type.    
 * 
 * @param type The type to flatten
 * @param fieldDefs The set of field definitions to include in the flattened type
 * @param inputSide The flattened type of the input side
 * @return A new FlattenedType object representing the output flattened type
 */
FlattenedType* FlattenedType::createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide) {
    std::map<FieldDefinition*, int> fieldMappings;
    for (const auto& fd : fieldDefs) {
        auto resolvedFD = fd->resolveInheritedFieldDefinition(fieldDefs);
        short fieldIndex = inputSide->fields[resolvedFD->getId()];
        fieldMappings[fd] = fieldIndex;
    }

    return new FlattenedType(Direction::OUTPUT, type, fieldMappings, inputSide->numberOfFields);
}

/**
 * @brief Checks if all elements in a vector are null
 * 
 * This function checks if all elements in a vector are null.
 * It iterates through the vector and returns true if all elements are null,    
 * otherwise it returns false.
 * 
 * @param vec The vector to check
 * @return true if all elements are null, otherwise false
 */
template <typename T>
bool isAllNull(const std::vector<T>& vec) {
    for (const auto& element : vec) {
        if (element != nullptr) {
            return false; // As soon as a non-null element is found, return false
        }
    }
    return true; // If no non-null element is found, return true
}

/**
 * @brief Flattens the type hierarchy
 * 
 * This function flattens the type hierarchy by creating a mapping of relations
 * to their corresponding flattened type relations. It iterates through all 
 * relations and types to build the mapping.
 */
void FlattenedType::flatten() {
    mapping = new FlattenedTypeRelation**[type->getRelations().size()];

    for (const auto& rel : type->getRelations()) {
        std::vector<FlattenedTypeRelation*> resultsPerRelation(type->getTypeRegistry()->getTypes().size());
        for (const auto& relatedType : type->getTypeRegistry()->getTypes()) {
            resultsPerRelation[relatedType->getId()] = flattenPerType(rel, relatedType);
        }

        if (!isAllNull(resultsPerRelation)) {
            FlattenedTypeRelation** tmp = new FlattenedTypeRelation*[resultsPerRelation.size()];
            for (int i = 0; i < resultsPerRelation.size(); i++) {
                tmp[i] = resultsPerRelation[i];
            }

            mapping[rel->getRelationId()] = tmp;
        }
    }
}

/**
 * @brief Flattens the type hierarchy per type
 * 
 * This function flattens the type hierarchy per type by creating a mapping of
 * field links for a given relation and related type. It iterates through all
 * field definitions and their corresponding field link definitions to build the mapping.
 * 
 * @param relation The relation to flatten
 * @param relatedType The related type to flatten
 * @return A new FlattenedTypeRelation object representing the flattened type relation
 */
FlattenedTypeRelation* FlattenedType::flattenPerType(Relation* relation, Type* relatedType) {
    std::vector<FieldLinkDefinition*> fieldLinks;

    for (int i = 0; i < numberOfFields; i++) {
        FieldDefinition** fdArray = fieldsReverse[i];

        int j = 0;
        while (fdArray[j] != nullptr) {
            FieldDefinition* fd = fdArray[j];
            j++;

            std::vector<FieldLinkDefinition*> fls = direction->getFieldLinkDefinitions(fd);
            for(FieldLinkDefinition* fl : fls) {
                if (fl->getRelation()->getRelationId() == relation->getRelationId() &&
                    relatedType->isInstanceOf(fl->getRelatedFD()->getObjectType()) &&
                    direction->invert()->getFlattenedType(relatedType)->fields[fl->getRelatedFD()->getId()] >= 0) {
                    fieldLinks.push_back(fl);
                }
            }
       }
    }

    return fieldLinks.empty() ?
                              nullptr :
                              new FlattenedTypeRelation(this, fieldLinks);
}

/**
 * @brief Follows links in the flattened type
 * 
 * This function follows links in the flattened type by iterating through all
 * relations and their corresponding flattened type relations. It iterates through
 * all relations and their corresponding flattened type relations to follow links.
 * 
 * @param field The field to follow links from
 */
void FlattenedType::followLinks(Field* field) {
    for (int relationId = 0; relationId < type->getRelations().size(); relationId++) {
        auto& ftr = mapping[relationId];

        if (ftr != nullptr) {
            auto relation = type->getRelations()[relationId];
            auto iterable = relation->followMany(field->getObject());
            auto it = iterable->iterator();
            while (it->hasNext()) {
                auto relatedObj = it->next();
                followLinks(ftr[relatedObj->getType()->getId()], relatedObj, field);
            }
            delete it;
            delete iterable;
        }
    }
}

/**
 * @brief Follows links in the flattened type relation
 * 
 * This function follows links in the flattened type relation by iterating through
 * all field links and their corresponding field link definitions.  
 * 
 * @param ftr The flattened type relation to follow links from
 * @param relatedObj The related object to follow links from
 * @param field The field to follow links from
 */
void FlattenedType::followLinks(FlattenedTypeRelation* ftr, Obj* relatedObj, Field* field) {
    if (ftr != nullptr) {
        ftr->followLinks(direction, relatedObj, field);
    }
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
