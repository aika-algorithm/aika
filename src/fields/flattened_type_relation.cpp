#include <algorithm>
#include <iterator>
#include "fields/flattened_type_relation.h"
#include "fields/null_terminated_array.h"

FlattenedTypeRelation::FlattenedTypeRelation(FlattenedType* ft, const std::vector<FieldLinkDefinition*>& fls) {
    std::cout << "  FlattenedTypeRelation constructor " << ft->getType()->getName() << " " << ft->getDirection()->getName() << std::endl;
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

    std::cout << "  FlattenedTypeRelation constructor NumberOfFields " << flattenedType->getNumberOfFields() << std::endl;

    // Fill fieldLinks with the grouped FieldLinkDefinitions
    for (int i = 0; i < flattenedType->getNumberOfFields(); ++i) {
        FieldDefinition** fdArray = flattenedType->getFieldsReverse()[i];

        std::cout << "  FlattenedTypeRelation constructor fdArray " << fdArray << std::endl;

        int j = 0;
        while (fdArray[j] != nullptr) {
            FieldDefinition* fd = fdArray[j];
            j++;

            std::cout << "  FlattenedTypeRelation constructor fd " << fd << std::endl;

            std::vector<FieldLinkDefinition*>* fls = groupedByOriginFD[fd->getId()];
            std::cout << "  FlattenedTypeRelation constructor fls " << fls << std::endl;

            if(fls != nullptr) {
                fieldLinks[i] = nullTerminatedArrayFromVector<FieldLinkDefinition*>(*fls);

                std::cout << "  FlattenedTypeRelation constructor fieldLinks[" << i << "] " << fieldLinks[i] << std::endl;
            }
        }
    }

    std::cout << "  FlattenedTypeRelation constructor end " << std::endl;
}

void FlattenedTypeRelation::followLinks(Direction* direction, Obj* relatedObj, Field* field) {
    std::cout << "followLinks field " << field << std::endl;
    std::cout << "followLinks field->getId() " << field->getId() << std::endl;
    std::cout << "followLinks field->getName() " << field->getName() << std::endl;
    std::cout << "followLinks fieldLinks[field->getId()] " << fieldLinks[field->getId()] << std::endl;
    
    FieldLinkDefinition** flArray = fieldLinks[field->getId()];

     std::cout << "followLinks flArray " << flArray << std::endl;

    int i = 0;
    while (flArray[i] != nullptr) {
        FieldLinkDefinition* fl = flArray[i];
        std::cout << "Following links for relation B " << fl << std::endl;
        std::cout << "fl->getOriginFD() " << fl->getOriginFD() << std::endl;
        std::cout << "fl->getOriginFD()->getId() " << fl->getOriginFD()->getId() << std::endl;
        i++;

        std::cout << "transmit " << field << " " << fl << " " << relatedObj << std::endl;
        direction->transmit(field, fl, relatedObj);
    }
    std::cout << "followLinks end " << std::endl;
}
