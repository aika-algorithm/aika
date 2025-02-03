#include "fields/flattened_type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"
#include "fields/obj.h"
#include "fields/field.h"
#include "utils.h"
#include "queue_interceptor.h"

FlattenedType::FlattenedType(Direction dir, std::shared_ptr<Type> type, const std::map<std::shared_ptr<FieldDefinition>, short>& fieldMappings, int numberOfFields)
    : direction(dir), type(type), numberOfFields(numberOfFields) {

    fields.resize(type->getTypeRegistry()->getNumberOfFieldDefinitions(), -1);

    std::map<short, std::vector<std::shared_ptr<FieldDefinition>>> groupedMap;
    for (const auto& e : fieldMappings) {
        fields[e.first->getId()] = e.second;
        groupedMap[e.second].push_back(e.first);
    }

    fieldsReverse.resize(numberOfFields);
    for (const auto& e : groupedMap) {
        fieldsReverse[e.first] = e.second;
    }
}

std::shared_ptr<FlattenedType> FlattenedType::createInputFlattenedType(std::shared_ptr<Type> type, const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs) {
    std::map<std::shared_ptr<FieldDefinition>, short> fieldMappings;

    std::vector<std::shared_ptr<FieldDefinition>> requiredFields;
    for (const auto& fd : fieldDefs) {
        if (fd->isFieldRequired(fieldDefs)) {
            requiredFields.push_back(fd);
        }
    }

    for (short i = 0; i < requiredFields.size(); i++) {
        fieldMappings[requiredFields[i]] = i;
    }

    return std::make_shared<FlattenedType>(Direction::INPUT, type, fieldMappings, requiredFields.size());
}

std::shared_ptr<FlattenedType> FlattenedType::createOutputFlattenedType(std::shared_ptr<Type> type, const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs, std::shared_ptr<FlattenedType> inputSide) {
    std::map<std::shared_ptr<FieldDefinition>, short> fieldMappings;
    for (const auto& fd : fieldDefs) {
        auto resolvedFD = fd->resolveInheritedFieldDefinition(fieldDefs);
        short fieldIndex = inputSide->fields[resolvedFD->getId()];
        fieldMappings[fd] = fieldIndex;
    }

    return std::make_shared<FlattenedType>(Direction::OUTPUT, type, fieldMappings, inputSide->numberOfFields);
}

void FlattenedType::flatten() {
    mapping.resize(type->getRelations().size());

    for (const auto& rel : type->getRelations()) {
        std::vector<std::shared_ptr<FlattenedTypeRelation>> resultsPerRelation(type->getTypeRegistry()->getTypes().size());
        for (const auto& relatedType : type->getTypeRegistry()->getTypes()) {
            resultsPerRelation[relatedType->getId()] = flattenPerType(rel, relatedType);
        }

        if (!ArrayUtils::isAllNull(resultsPerRelation)) {
            mapping[rel->getRelationId()] = resultsPerRelation;
        }
    }
}

std::shared_ptr<FlattenedTypeRelation> FlattenedType::flattenPerType(std::shared_ptr<Relation> relation, std::shared_ptr<Type> relatedType) {
    std::vector<std::shared_ptr<FieldLinkDefinition>> fieldLinks;

    for (const auto& fieldArr : fieldsReverse) {
        for (const auto& fl : fieldArr) {
            if (fl->getRelation()->getRelationId() == relation->getRelationId() &&
                relatedType->isInstanceOf(fl->getRelatedFD()->getObjectType()) &&
                direction.invert()->getFlattenedType(relatedType)->fields[fl->getRelatedFD()->getId()] >= 0) {
                fieldLinks.push_back(fl);
            }
        }
    }

    return fieldLinks.empty() ? nullptr : std::make_shared<FlattenedTypeRelation>(this, fieldLinks);
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
