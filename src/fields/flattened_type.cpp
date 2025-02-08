#include "fields/flattened_type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"
#include "fields/obj.h"
#include "fields/field.h"
#include "fields/utils.h"
#include "fields/queue_interceptor.h"


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
        FieldDefinition** tmp = new FieldDefinition*[e.second.size()];
        for (int i = 0; i < e.second.size(); i++) {
          tmp[i] = e.second[i];
        }

        fieldsReverse[e.first] = tmp;
    }
}

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

std::shared_ptr<FlattenedType> FlattenedType::createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide) {
    std::map<std::shared_ptr<FieldDefinition>, int> fieldMappings;
    for (const auto& fd : fieldDefs) {
        auto resolvedFD = fd->resolveInheritedFieldDefinition(fieldDefs);
        short fieldIndex = inputSide->fields[resolvedFD->getId()];
        fieldMappings[fd] = fieldIndex;
    }

    return std::shared_ptr<FlattenedType>(new FlattenedType(Direction::OUTPUT, type, fieldMappings, inputSide->numberOfFields));
}

#include <iostream>
#include <vector>

template <typename T>
bool isAllNull(const std::vector<T>& vec) {
    for (const auto& element : vec) {
        if (element != nullptr) {
            return false; // As soon as a non-null element is found, return false
        }
    }
    return true; // If no non-null element is found, return true
}

void FlattenedType::flatten() {
    mapping.resize(type->getRelations().size());

    for (const auto& rel : type->getRelations()) {
        std::vector<std::shared_ptr<FlattenedTypeRelation>> resultsPerRelation(type->getTypeRegistry()->getTypes().size());
        for (const auto& relatedType : type->getTypeRegistry()->getTypes()) {
            resultsPerRelation[relatedType->getId()] = flattenPerType(rel, relatedType);
        }

        if (!isAllNull(resultsPerRelation)) {
            mapping[rel->getRelationId()] = resultsPerRelation;
        }
    }
}

std::shared_ptr<FlattenedTypeRelation> FlattenedType::flattenPerType(std::shared_ptr<Relation> relation, std::shared_ptr<Type> relatedType) {
    std::vector<std::shared_ptr<FieldLinkDefinition>> fieldLinks;

    for (const auto& fieldArr : fieldsReverse) {
        for (const auto& fd : fieldArr) {
            for(const auto& fl : direction->getFieldLinkDefinitions(fd)) {
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
                              std::shared_ptr<FlattenedTypeRelation>(
                                  new FlattenedTypeRelation(std::shared_ptr<FlattenedType>(this), fieldLinks)
                                  );
}

void FlattenedType::followLinks(std::shared_ptr<Field> field) {
    for (int relationId = 0; relationId < mapping.size(); relationId++) {
        auto& ftr = mapping[relationId];

        if (ftr != nullptr) {
            auto relation = type->getRelations()[relationId];
            relation->followMany(field->getObject())
                    .forEach([&](std::shared_ptr<Obj> relatedObj) {
                        followLinks(ftr[relatedObj->getType()->getId()], relatedObj, field);
                    });
        }
    }
}

void FlattenedType::followLinks(std::shared_ptr<FlattenedTypeRelation> ftr, std::shared_ptr<Obj> relatedObj, std::shared_ptr<Field> field) {
    if (ftr != nullptr) {
        ftr->followLinks(direction, relatedObj, field);
    }
}

short FlattenedType::getFieldIndex(std::shared_ptr<FieldDefinition> fd) {
    return fields[fd->getId()];
}

short FlattenedType::getNumberOfFields() const {
    return static_cast<short>(fieldsReverse.size());
}

std::shared_ptr<Type> FlattenedType::getType() const {
    return type;
}

std::shared_ptr<FieldDefinition> FlattenedType::getFieldDefinitionIdByIndex(short idx) {
    return fieldsReverse[idx][0];
}
