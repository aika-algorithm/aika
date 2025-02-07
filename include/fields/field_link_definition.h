
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

    FieldDefinition* getOriginFD() const;
    FieldDefinition* getRelatedFD() const;
    Relation* getRelation() const;
    Direction* getDirection() const;
    int getArgument() const;

    std::string toString() const;
};



class FieldLinkDefinitionInputSide : public FieldLinkDefinition {
private:
    FieldLinkDefinitionOutputSide* outputSide;

public:
    FieldLinkDefinitionInputSide(FieldDefinition* input,
                                 FieldDefinition* output,
                                 Relation* relation,
                                 Direction* direction,
                                 std::optional<int> argument = std::nullopt);

    FieldLinkDefinitionOutputSide* getOutputSide() const;
    void setOutputSide(FieldLinkDefinitionOutputSide* outputSide);
};



class FieldLinkDefinitionOutputSide : public FieldLinkDefinition {
private:
    FieldLinkDefinitionInputSide* inputSide;

public:
    FieldLinkDefinitionOutputSide(FieldDefinition* output,
                                  FieldDefinition* input,
                                  Relation* relation,
                                  Direction* direction,
                                  std::optional<int> argument = std::nullopt);

    FieldLinkDefinitionOutputSide(FieldDefinition* output,
                                  FieldDefinition* input,
                                  Relation* relation,
                                  Direction* direction);

    Field* getInputField(Obj* obj);
    double getInputValue(Obj* obj);
    double getUpdatedInputValue(Obj* obj);

    FieldLinkDefinitionInputSide* getInputSide() const;
    void setInputSide(FieldLinkDefinitionInputSide* inputSide);
};

#endif //FIELD_LINK_DEFINITION_H
