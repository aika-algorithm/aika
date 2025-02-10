
#ifndef FIELD_LINK_DEFINITION_H
#define FIELD_LINK_DEFINITION_H

#include <string>
#include <memory>

#include "fields/field_definition.h"
#include "fields/relation.h"
#include "fields/direction.h"
#include "fields/obj.h"


class FieldLinkDefinition {
private:
    FieldLinkDefinition* oppositeSide;
    FieldDefinition* originFD;
    FieldDefinition* relatedFD;
    Relation* relation;
    Direction* direction;
    std::optional<int> argument;

public:
    static void link(FieldDefinition* input,
                     FieldDefinition* output,
                     Relation* relation,
                     std::optional<int> argument);

    FieldLinkDefinition(FieldDefinition* originFD,
                        FieldDefinition* relatedFD,
                        Relation* relation,
                        Direction* direction,
                        std::optional<int> argument);

    FieldLinkDefinition(FieldDefinition* originFD,
                        FieldDefinition* relatedFD,
                        Relation* relation,
                        Direction* direction);

    FieldLinkDefinition* getOppositeSide() const;
    void setOppositeSide(FieldLinkDefinition* os);

    FieldDefinition* getOriginFD() const;
    FieldDefinition* getRelatedFD() const;
    Relation* getRelation() const;
    Direction* getDirection() const;
    int getArgument() const;

    Field* getInputField(Obj* obj);
    double getInputValue(Obj* obj);
    double getUpdatedInputValue(Obj* obj);

    std::string toString() const;
};

#endif //FIELD_LINK_DEFINITION_H
