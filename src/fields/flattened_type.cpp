#include "fields/flattened_type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"
#include "fields/obj.h"
#include "fields/field.h"
#include "fields/utils.h"
#include "fields/queue_interceptor.h"
#include "fields/null_terminated_array.h"


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

FlattenedType* FlattenedType::createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide) {
    std::map<FieldDefinition*, int> fieldMappings;
    for (const auto& fd : fieldDefs) {
        auto resolvedFD = fd->resolveInheritedFieldDefinition(fieldDefs);
        short fieldIndex = inputSide->fields[resolvedFD->getId()];
        fieldMappings[fd] = fieldIndex;
    }

    return new FlattenedType(Direction::OUTPUT, type, fieldMappings, inputSide->numberOfFields);
}

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

FlattenedTypeRelation* FlattenedType::flattenPerType(Relation* relation, Type* relatedType) {
    std::vector<FieldLinkDefinition*> fieldLinks;

    for (int i = 0; i < numberOfFields; i++) {
        NullTerminatedArray fdArray(fieldsReverse[i]);
        for (FieldDefinition* fd : fdArray) {
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
                              new FlattenedTypeRelation(this, fieldLinks);
}

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

void FlattenedType::followLinks(FlattenedTypeRelation* ftr, Obj* relatedObj, Field* field) {
    if (ftr != nullptr) {
        ftr->followLinks(direction, relatedObj, field);
    }
}

int FlattenedType::getFieldIndex(FieldDefinition* fd) {
    return fields[fd->getId()];
}

int FlattenedType::getNumberOfFields() const {
    return numberOfFields;
}

Type* FlattenedType::getType() const {
    return type;
}

FieldDefinition*** FlattenedType::getFieldsReverse() {
    return fieldsReverse;
}

FieldDefinition* FlattenedType::getFieldDefinitionIdByIndex(short idx) {
    return fieldsReverse[idx][0];
}
