/**
 * @file field_link_definition.cpp
 * @brief Implementation of the FieldLinkDefinition class, which represents connections between fields.
 * 
 * This file implements the functionality defined in field_link_definition.h, providing the concrete
 * implementation of field links that form the edges of the field graph. The implementation handles
 * link creation, value propagation, and bidirectional connectivity between fields.
 */

#include <sstream>
#include <memory>

#include "fields/field_link_definition.h"

/**
 * @brief Creates a pair of linked field definitions
 * 
 * Establishes bidirectional connections between input and output field definitions by:
 * 1. Creating two FieldLinkDefinition instances (input and output sides)
 * 2. Adding the links to their respective field definitions
 * 3. Setting up the opposite side references
 * 
 * @param input The input field definition
 * @param output The output field definition
 * @param relation The relation governing the connection
 * @param argument Optional argument position for ordered inputs
 */
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

/**
 * @brief Constructs a new FieldLinkDefinition with an argument position
 * 
 * @param originFD The field definition at the origin
 * @param relatedFD The field definition at the related end
 * @param relation The relation governing the connection
 * @param direction The direction of data flow
 * @param argument The argument position for ordered inputs
 */
FieldLinkDefinition::FieldLinkDefinition(FieldDefinition* originFD,
                                         FieldDefinition* relatedFD,
                                         Relation* relation,
                                         Direction* direction,
                                         std::optional<int> argument)
    : originFD(originFD), relatedFD(relatedFD), relation(relation), direction(direction), argument(argument) {}

/**
 * @brief Constructs a new FieldLinkDefinition without an argument position
 * 
 * @param originFD The field definition at the origin
 * @param relatedFD The field definition at the related end
 * @param relation The relation governing the connection
 * @param direction The direction of data flow
 */
FieldLinkDefinition::FieldLinkDefinition(FieldDefinition* originFD,
                                         FieldDefinition* relatedFD,
                                         Relation* relation,
                                         Direction* direction)
    : FieldLinkDefinition(originFD, relatedFD, relation, direction, std::nullopt) {}

/**
 * @brief Gets the field definition at the origin of the link
 * 
 * @return The origin field definition
 */
FieldDefinition* FieldLinkDefinition::getOriginFD() const {
    return originFD;
}

/**
 * @brief Gets the field definition at the related end of the link
 * 
 * @return The related field definition
 */
FieldDefinition* FieldLinkDefinition::getRelatedFD() const {
    return relatedFD;
}

/**
 * @brief Gets the relation governing the connection
 * 
 * @return The relation
 */
Relation* FieldLinkDefinition::getRelation() const {
    return relation;
}

/**
 * @brief Gets the direction of data flow
 * 
 * @return The direction
 */
Direction* FieldLinkDefinition::getDirection() const {
    return direction;
}

/**
 * @brief Gets the argument position for ordered inputs
 * 
 * Returns 0 if no argument position is set.
 * 
 * @return The argument position
 */
int FieldLinkDefinition::getArgument() const {
    return argument.value_or(0);
}

/**
 * @brief Converts the field link definition to a string representation
 * 
 * @return String representation
 */
std::string FieldLinkDefinition::toString() const {
    // TODO: Implement proper string representation
    return "";
}

/**
 * @brief Gets the input field for a given object
 * 
 * Follows the relation to get the input object and retrieves its field.
 * 
 * @param obj The object to get the input field for
 * @return The input field
 */
Field* FieldLinkDefinition::getInputField(Obj* obj) {
    RelationOne* rt = (RelationOne*) getRelation();
    auto inputObj = rt->followOne(obj);
    return inputObj->getFieldOutput(getRelatedFD());
}

/**
 * @brief Gets the current input value for a given object
 * 
 * @param obj The object to get the input value for
 * @return The current input value, or 0.0 if no field exists
 */
double FieldLinkDefinition::getInputValue(Obj* obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getValue() : 0.0;
}

/**
 * @brief Gets the updated input value for a given object
 * 
 * @param obj The object to get the updated input value for
 * @return The updated input value, or 0.0 if no field exists
 */
double FieldLinkDefinition::getUpdatedInputValue(Obj* obj) {
    auto f = getInputField(obj);
    return (f != nullptr) ? f->getUpdatedValue() : 0.0;
}

/**
 * @brief Gets the paired link in the opposite direction
 * 
 * @return The opposite side link
 */
FieldLinkDefinition* FieldLinkDefinition::getOppositeSide() const {
    return oppositeSide;
}

/**
 * @brief Sets the paired link in the opposite direction
 * 
 * @param os The opposite side link to set
 */
void FieldLinkDefinition::setOppositeSide(FieldLinkDefinition* os) {
    oppositeSide = os;
}
