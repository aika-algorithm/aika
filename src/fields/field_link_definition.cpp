
#include "fields/field_link_definition.h"

void FieldLinkDefinition::link(std::shared_ptr<FieldDefinition> input,
                                std::shared_ptr<FieldDefinition> output,
                                std::shared_ptr<Relation> relation,
                                std::optional<int> argument) {
    auto flo = std::make_shared<FieldLinkDefinitionOutputSide>(output, input, relation->getReverse(), Direction::OUTPUT, argument);
    auto fli = std::make_shared<FieldLinkDefinitionInputSide>(input, output, relation, Direction::INPUT, argument);

    output->addInput(flo);
    input->addOutput(fli);

    flo->setInputSide(fli);
    fli->setOutputSide(flo);
}

FieldLinkDefinition::FieldLinkDefinition(std::shared_ptr<FieldDefinition> originFD,
                                         std::shared_ptr<FieldDefinition> relatedFD,
                                         std::shared_ptr<Relation> relation,
                                         Direction direction,
                                         std::optional<int> argument)
    : originFD(originFD), relatedFD(relatedFD), relation(relation), direction(direction), argument(argument) {}

FieldLinkDefinition::FieldLinkDefinition(std::shared_ptr<FieldDefinition> originFD,
                                         std::shared_ptr<FieldDefinition> relatedFD,
                                         std::shared_ptr<Relation> relation,
                                         Direction direction)
    : FieldLinkDefinition(originFD, relatedFD, relation, direction, std::nullopt) {}

std::shared_ptr<FieldDefinition> FieldLinkDefinition::getOriginFD() const {
    return originFD;
}

std::shared_ptr<FieldDefinition> FieldLinkDefinition::getRelatedFD() const {
    return relatedFD;
}

std::shared_ptr<Relation> FieldLinkDefinition::getRelation() const {
    return relation;
}

Direction FieldLinkDefinition::getDirection() const {
    return direction;
}

int FieldLinkDefinition::getArgument() const {
    return argument.value_or(0); // Return 0 if no argument is set
}

std::string FieldLinkDefinition::toString() const {
    std::stringstream ss;
    ss << *originFD << " -- (" << *relation << ") -> " << *relatedFD;
    return ss.str();
}


FieldLinkDefinitionInputSide::FieldLinkDefinitionInputSide(
    std::shared_ptr<FieldDefinition> input,
    std::shared_ptr<FieldDefinition> output,
    std::shared_ptr<Relation> relation,
    Direction direction,
    std::optional<int> argument)
    : FieldLinkDefinition(input, output, relation, direction, argument) {}

FieldLinkDefinitionInputSide::FieldLinkDefinitionInputSide(
    std::shared_ptr<FieldDefinition> input,
    std::shared_ptr<FieldDefinition> output,
    std::shared_ptr<Relation> relation,
    Direction direction)
    : FieldLinkDefinition(input, output, relation, direction) {}

std::shared_ptr<FieldLinkDefinitionOutputSide> FieldLinkDefinitionInputSide::getOutputSide() const {
    return outputSide;
}

void FieldLinkDefinitionInputSide::setOutputSide(std::shared_ptr<FieldLinkDefinitionOutputSide> outputSide) {
    this->outputSide = outputSide;
}


FieldLinkDefinitionOutputSide::FieldLinkDefinitionOutputSide(
    std::shared_ptr<FieldDefinition> output,
    std::shared_ptr<FieldDefinition> input,
    std::shared_ptr<Relation> relation,
    Direction direction,
    std::optional<int> argument)
    : FieldLinkDefinition(output, input, relation, direction, argument) {}

FieldLinkDefinitionOutputSide::FieldLinkDefinitionOutputSide(
    std::shared_ptr<FieldDefinition> output,
    std::shared_ptr<FieldDefinition> input,
    std::shared_ptr<Relation> relation,
    Direction direction)
    : FieldLinkDefinition(output, input, relation, direction) {}

std::shared_ptr<Field> FieldLinkDefinitionOutputSide::getInputField(std::shared_ptr<Obj> obj) {
    auto rt = std::dynamic_pointer_cast<RelationOne>(getRelation());
    auto inputObj = rt->followOne(obj);
    return inputObj->getFieldOutput(getRelatedFD());
}

double FieldLinkDefinitionOutputSide::getInputValue(std::shared_ptr<Obj> obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getValue() : 0.0;
}

double FieldLinkDefinitionOutputSide::getUpdatedInputValue(std::shared_ptr<Obj> obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getUpdatedValue() : 0.0;
}

std::shared_ptr<FieldLinkDefinitionInputSide> FieldLinkDefinitionOutputSide::getInputSide() const {
    return inputSide;
}

void FieldLinkDefinitionOutputSide::setInputSide(std::shared_ptr<FieldLinkDefinitionInputSide> inputSide) {
    this->inputSide = inputSide;
}
