#include <algorithm>
#include <iterator>
#include "fields/flattened_type_relation.h"
#include "fields/null_terminated_array.h"

FlattenedTypeRelation::FlattenedTypeRelation(FlattenedType* ft, const std::vector<FieldLinkDefinition*>& fls) {
    flattenedType = ft;

    std::map<int, std::vector<FieldLinkDefinition*>*> groupedByOriginFD;

    // Group FieldLinkDefinitions by their origin field definitions' IDs
    for (const auto& fl : fls) {
        std::vector<FieldLinkDefinition*>* list = groupedByOriginFD[fl->getOriginFD()->getId()];
        if(list == nullptr) {
            list = new std::vector<FieldLinkDefinition*>();
            groupedByOriginFD[fl->getOriginFD()->getId()] = list;
        }
        list->push_back(fl);
    }

    // Initialize fieldLinks to the correct size
    fieldLinks = new FieldLinkDefinition**[flattenedType->getNumberOfFields()];

    // Fill fieldLinks with the grouped FieldLinkDefinitions
    for (int i = 0; i < flattenedType->getNumberOfFields(); ++i) {
        NullTerminatedArray fdArray(flattenedType->getFieldsReverse()[i]);

        for (FieldDefinition* fd : fdArray) {
            fieldLinks[i] = nullTerminatedArrayFromVector<FieldLinkDefinition*>(*groupedByOriginFD[fd->getId()]);
        }
    }
}

void FlattenedTypeRelation::followLinks(Direction* direction, Obj* relatedObj, Field* field) {
    NullTerminatedArray flArray(fieldLinks[field->getId()]);

    for (FieldLinkDefinition* fl : flArray) {
        direction->transmit(field, fl, relatedObj);
    }
}
