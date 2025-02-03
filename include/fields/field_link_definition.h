
#ifndef FIELD_LINK_DEFINITION_H
#define FIELD_LINK_DEFINITION_H

#include <string>
#include <memory>
#include "fields/field_definition.h"
#include "fields/relation.h"
#include "fields/direction.h"

class FieldLinkDefinition {
private:
    std::shared_ptr<FieldDefinition> originFD;
    std::shared_ptr<FieldDefinition> relatedFD;
    std::shared_ptr<Relation> relation;
    Direction direction;
    std::optional<int> argument;

public:
    static void link(std::shared_ptr<FieldDefinition> input,
                     std::shared_ptr<FieldDefinition> output,
                     std::shared_ptr<Relation> relation,
                     std::optional<int> argument);

    FieldLinkDefinition(std::shared_ptr<FieldDefinition> originFD,
                        std::shared_ptr<FieldDefinition> relatedFD,
                        std::shared_ptr<Relation> relation,
                        Direction direction,
                        std::optional<int> argument);

    FieldLinkDefinition(std::shared_ptr<FieldDefinition> originFD,
                        std::shared_ptr<FieldDefinition> relatedFD,
                        std::shared_ptr<Relation> relation,
                        Direction direction);

    std::shared_ptr<FieldDefinition> getOriginFD() const;
    std::shared_ptr<FieldDefinition> getRelatedFD() const;
    std::shared_ptr<Relation> getRelation() const;
    Direction getDirection() const;
    int getArgument() const;

    std::string toString() const;
};



class FieldLinkDefinitionInputSide : public FieldLinkDefinition {
private:
    std::shared_ptr<FieldLinkDefinitionOutputSide> outputSide;

public:
    FieldLinkDefinitionInputSide(std::shared_ptr<FieldDefinition> input,
                                 std::shared_ptr<FieldDefinition> output,
                                 std::shared_ptr<Relation> relation,
                                 Direction direction,
                                 std::optional<int> argument = std::nullopt);

    std::shared_ptr<FieldLinkDefinitionOutputSide> getOutputSide() const;
    void setOutputSide(std::shared_ptr<FieldLinkDefinitionOutputSide> outputSide);
};



class FieldLinkDefinitionOutputSide : public FieldLinkDefinition {
private:
    std::shared_ptr<FieldLinkDefinitionInputSide> inputSide;

public:
    FieldLinkDefinitionOutputSide(std::shared_ptr<FieldDefinition> output,
                                  std::shared_ptr<FieldDefinition> input,
                                  std::shared_ptr<Relation> relation,
                                  Direction direction,
                                  std::optional<int> argument = std::nullopt);

    FieldLinkDefinitionOutputSide(std::shared_ptr<FieldDefinition> output,
                                  std::shared_ptr<FieldDefinition> input,
                                  std::shared_ptr<Relation> relation,
                                  Direction direction);

    std::shared_ptr<Field> getInputField(std::shared_ptr<Obj> obj);
    double getInputValue(std::shared_ptr<Obj> obj);
    double getUpdatedInputValue(std::shared_ptr<Obj> obj);

    std::shared_ptr<FieldLinkDefinitionInputSide> getInputSide() const;
    void setInputSide(std::shared_ptr<FieldLinkDefinitionInputSide> inputSide);
};

#endif //FIELD_LINK_DEFINITION_H
