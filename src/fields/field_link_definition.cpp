
#include <sstream>
#include <memory>

#include "fields/field_link_definition.h"

void FieldLinkDefinition::link(FieldDefinition* input,
                                FieldDefinition* output,
                                Relation* relation,
                                std::optional<int> argument) {
    auto flo = new FieldLinkDefinitionOutputSide(output, input, relation->getReverse(), Direction::OUTPUT, argument);
    auto fli = new FieldLinkDefinitionInputSide(input, output, relation, Direction::INPUT, argument);

    output->addInput(flo);
    input->addOutput(fli);

    flo->setInputSide(fli);
    fli->setOutputSide(flo);
}

FieldLinkDefinition::FieldLinkDefinition(FieldDefinition* originFD,
                                         FieldDefinition* relatedFD,
                                         Relation* relation,
                                         Direction* direction,
                                         std::optional<int> argument)
    : originFD(originFD), relatedFD(relatedFD), relation(relation), direction(direction), argument(argument) {}

FieldLinkDefinition::FieldLinkDefinition(FieldDefinition* originFD,
                                         FieldDefinition* relatedFD,
                                         Relation* relation,
                                         Direction* direction)
    : FieldLinkDefinition(originFD, relatedFD, relation, direction, std::nullopt) {}

FieldDefinition* FieldLinkDefinition::getOriginFD() const {
    return originFD;
}

FieldDefinition* FieldLinkDefinition::getRelatedFD() const {
    return relatedFD;
}

Relation* FieldLinkDefinition::getRelation() const {
    return relation;
}

Direction* FieldLinkDefinition::getDirection() const {
    return direction;
}

int FieldLinkDefinition::getArgument() const {
    return argument.value_or(0); // Return 0 if no argument is set
}

std::string FieldLinkDefinition::toString() const {
//    std::stringstream ss;
//    ss << *originFD << " -- (" << *relation << ") -> " << *relatedFD;
    return ""; //ss.str();
}


FieldLinkDefinitionInputSide::FieldLinkDefinitionInputSide(
    FieldDefinition* input,
    FieldDefinition* output,
    Relation* relation,
    Direction* direction,
    std::optional<int> argument)
    : FieldLinkDefinition(input, output, relation, direction, argument) {}

FieldLinkDefinitionOutputSide* FieldLinkDefinitionInputSide::getOutputSide() const {
    return outputSide;
}

void FieldLinkDefinitionInputSide::setOutputSide(FieldLinkDefinitionOutputSide* outputSide) {
    this->outputSide = outputSide;
}


FieldLinkDefinitionOutputSide::FieldLinkDefinitionOutputSide(
    FieldDefinition* output,
    FieldDefinition* input,
    Relation* relation,
    Direction* direction,
    std::optional<int> argument)
    : FieldLinkDefinition(output, input, relation, direction, argument) {}

FieldLinkDefinitionOutputSide::FieldLinkDefinitionOutputSide(
    FieldDefinition* output,
    FieldDefinition* input,
    Relation* relation,
    Direction* direction)
    : FieldLinkDefinition(output, input, relation, direction) {}

Field* FieldLinkDefinitionOutputSide::getInputField(Obj* obj) {
    RelationOne* rt = (RelationOne*) getRelation();
    auto inputObj = rt->followOne(obj);
    return inputObj->getFieldOutput(getRelatedFD());
}

double FieldLinkDefinitionOutputSide::getInputValue(Obj* obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getValue() : 0.0;
}

double FieldLinkDefinitionOutputSide::getUpdatedInputValue(Obj* obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getUpdatedValue() : 0.0;
}

FieldLinkDefinitionInputSide* FieldLinkDefinitionOutputSide::getInputSide() const {
    return inputSide;
}

void FieldLinkDefinitionOutputSide::setInputSide(FieldLinkDefinitionInputSide* inputSide) {
    this->inputSide = inputSide;
}
