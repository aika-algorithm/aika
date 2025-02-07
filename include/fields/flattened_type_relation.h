#ifndef FLATTENED_TYPE_RELATION_H
#define FLATTENED_TYPE_RELATION_H

#include <vector>
#include <map>
#include <memory>
#include <list>

#include "fields/direction.h"
#include "fields/field.h"
#include "fields/field_link_definition.h"

class Obj;


class FlattenedTypeRelation {
private:
    std::vector<FieldLinkDefinition*>* fieldLinks;

public:
    FlattenedTypeRelation(FlattenedType* flattenedType, const std::vector<FieldLinkDefinition*>& fls);

    void followLinks(Direction* direction, Obj* relatedObj, Field* field);
};

#endif //FLATTENED_TYPE_RELATION_H
