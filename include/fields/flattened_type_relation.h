#ifndef FLATTENED_TYPE_RELATION_H
#define FLATTENED_TYPE_RELATION_H

#include <vector>

#include "fields/direction.h"
#include "fields/field.h"
#include "fields/field_link_definition.h"

class Object;


class FlattenedTypeRelation {
private:
    FlattenedType* flattenedType;

    FieldLinkDefinition*** fieldLinks;

public:
    FlattenedTypeRelation(FlattenedType* flattenedType, const std::vector<FieldLinkDefinition*>& fls);

    void followLinks(Direction* direction, Object* relatedObj, Field* field);
};

#endif //FLATTENED_TYPE_RELATION_H
