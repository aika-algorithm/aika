#include <algorithm>
#include <iterator>
#include "fields/flattened_type_relation.h"

FlattenedTypeRelation::FlattenedTypeRelation(std::shared_ptr<FlattenedType> flattenedType, const std::vector<std::shared_ptr<FieldLinkDefinition>>& fls) {
    std::map<int, std::list<std::shared_ptr<FieldLinkDefinition>>> groupedByOriginFD;

    // Group FieldLinkDefinitions by their origin field definitions' IDs
    for (const auto& fl : fls) {
        groupedByOriginFD[fl->getOriginFD()->getId()].push_back(fl);
    }

    // Initialize fieldLinks to the correct size
    fieldLinks.resize(flattenedType->getNumberOfFields());

    // Fill fieldLinks with the grouped FieldLinkDefinitions
    for (short i = 0; i < fieldLinks->size(); ++i) {
        for (const auto& fd : flattenedType->getFieldsReverse()[i]) {
            auto it = groupedByOriginFD.find(fd->getId());
            if (it != groupedByOriginFD.end()) {
                fieldLinks[i] = std::vector<std::shared_ptr<FieldLinkDefinition>>(it->second.begin(), it->second.end());
            }
        }
    }
}

void FlattenedTypeRelation::followLinks(Direction direction, std::shared_ptr<Obj> relatedObj, std::shared_ptr<Field> field) {
    const auto& fls = fieldLinks[field->getId()];
    if (!fls.empty()) {
        for (const auto& fl : fls) {
            direction.transmit(field, fl, relatedObj);
        }
    }
}
