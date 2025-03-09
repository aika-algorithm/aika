
#include <sstream>
#include <memory>

#include "fields/field_link_definition.h"

void FieldLinkDefinition::link(FieldDefinition* input,
                                FieldDefinition* output,
                                Relation* relation,
                                std::optional<int> argument) {
    auto flo = new FieldLinkDefinition(output, input, relation->getReverse(), Direction::OUTPUT, argument);
    auto fli = new FieldLinkDefinition(input, output, relation, Direction::INPUT, argument);

    output->addInput(flo);
    input->addOutput(fli);

    flo->setOppositeSide(fli);
    fli->setOppositeSide(flo);
   
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



Field* FieldLinkDefinition::getInputField(Obj* obj) {
    RelationOne* rt = (RelationOne*) getRelation();
    auto inputObj = rt->followOne(obj);
    return inputObj->getFieldOutput(getRelatedFD());
}

double FieldLinkDefinition::getInputValue(Obj* obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getValue() : 0.0;
}

double FieldLinkDefinition::getUpdatedInputValue(Obj* obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getUpdatedValue() : 0.0;
}

FieldLinkDefinition* FieldLinkDefinition::getOppositeSide() const {
    return oppositeSide;
}

void FieldLinkDefinition::setOppositeSide(FieldLinkDefinition* os) {
    oppositeSide = os;
}
