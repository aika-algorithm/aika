#ifndef FLATTENED_TYPE_H
#define FLATTENED_TYPE_H

#include <map>

#include "fields/field_definition.h"
#include "fields/flattened_type_relation.h"
#include "fields/direction.h"

class Type;
class FlattenedTypeRelation;


class FlattenedType {
private:
    Direction* direction;
    Type* type;

    int numberOfFields;
    int* fields;
    FieldDefinition*** fieldsReverse; // Number of fields | List of rev. field mappings terminated by nullptr.
    FlattenedTypeRelation*** mapping; // Number of Relations | Number of Types

    FlattenedType(Direction* dir, Type* type, const std::map<FieldDefinition*, int>& fieldMappings, int numberOfFields);
    FlattenedTypeRelation* flattenPerType(Relation* relation, Type* relatedType);

public:
    static FlattenedType* createInputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs);
    static FlattenedType* createOutputFlattenedType(Type* type, const std::set<FieldDefinition*>& fieldDefs, FlattenedType* inputSide);

    void flatten();
    void followLinks(Field* field);

    int getFieldIndex(FieldDefinition* fd);
    int getNumberOfFields() const;
    Type* getType() const;

    FieldDefinition*** getFieldsReverse();
    FieldDefinition* getFieldDefinitionIdByIndex(short idx);

    void followLinks(FlattenedTypeRelation* ftr, Obj* relatedObj, Field* field);
};

#endif // FLATTENED_TYPE_H
