#ifndef FLATTENEDTYPE_H
#define FLATTENEDTYPE_H

#include <string>
#include <memory>
#include <map>
#include <set>
#include <vector>

#include "fields/type.h"
#include "fields/field_definition.h"
#include "fields/field_link_definition.h"
#include "fields/flattened_type_relation.h"
#include "fields/direction.h"

class FlattenedType {
private:
    std::shared_ptr<Direction> direction;
    std::shared_ptr<Type> type;
    std::vector<short> fields;
    std::vector<std::vector<std::shared_ptr<FieldDefinition>>> fieldsReverse;
    int numberOfFields;
    std::vector<std::vector<std::shared_ptr<FlattenedTypeRelation>>> mapping;

    FlattenedType(std::shared_ptr<Direction> dir, std::shared_ptr<Type> type, const std::map<std::shared_ptr<FieldDefinition>, short>& fieldMappings, int numberOfFields);

public:
    static std::shared_ptr<FlattenedType> createInputFlattenedType(std::shared_ptr<Type> type, const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs);
    static std::shared_ptr<FlattenedType> createOutputFlattenedType(std::shared_ptr<Type> type, const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs, std::shared_ptr<FlattenedType> inputSide);

    void flatten();
    void followLinks(std::shared_ptr<Field> field);

    short getFieldIndex(std::shared_ptr<FieldDefinition> fd);
    short getNumberOfFields() const;
    std::shared_ptr<Type> getType() const;
    std::shared_ptr<FieldDefinition> getFieldDefinitionIdByIndex(short idx);

    void followLinks(std::shared_ptr<FlattenedTypeRelation> ftr, std::shared_ptr<Obj> relatedObj, std::shared_ptr<Field> field);
};

#endif // FLATTENEDTYPE_H
