#ifndef FLATTENED_TYPE_RELATION_H
#define FLATTENED_TYPE_RELATION_H

#include <vector>
#include <map>
#include <memory>
#include <list>
#include "fields/direction.h"
#include "fields/obj.h"
#include "fields/field.h"
#include "fields/field_definition.h"
#include "fields/field_link_definition.h"
#include "fields/flattened_type.h"

class FlattenedTypeRelation {
private:
    std::vector<std::shared_ptr<FieldLinkDefinition>>* fieldLinks;

public:
    FlattenedTypeRelation(std::shared_ptr<FlattenedType> flattenedType, const std::list<std::shared_ptr<FieldLinkDefinition>>& fls);

    void followLinks(Direction direction, std::shared_ptr<Obj> relatedObj, std::shared_ptr<Field> field);
};

#endif //FLATTENED_TYPE_RELATION_H
