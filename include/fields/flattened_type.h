#ifndef FLATTENED_TYPE_H
#define FLATTENED_TYPE_H

#include <map>

#include "fields/field_definition.h"
#include "fields/flattened_type_relation.h"
#include "fields/direction.h"

class Type;


class FlattenedType {
private:
    Direction* direction;
    Type* type;
    int* fields;
    FieldDefinition*** fieldsReverse;
    int numberOfFields;
    FlattenedTypeRelation*** mapping;

    FlattenedType(Direction* dir, Type* type, const std::map<FieldDefinition*, int>& fieldMappings, int numberOfFields);

    FlattenedTypeRelation* flattenPerType(Relation* relation, Type* relatedType);

public:
    static FlattenedType* createInputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs);
    static FlattenedType* createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide);

    void flatten();
    void followLinks(Field* field);

    short getFieldIndex(FieldDefinition* fd);
    short getNumberOfFields() const;
    Type* getType() const;
    FieldDefinition* getFieldDefinitionIdByIndex(short idx);

    void followLinks(FlattenedTypeRelation* ftr, Obj* relatedObj, Field* field);
};

#endif // FLATTENED_TYPE_H
