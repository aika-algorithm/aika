/**
 * @file direction.cpp
 * @brief Implements the Direction interface and its Input/Output implementations.
 * 
 * This file contains the implementation of the Direction interface and its
 * concrete implementations (Input and Output) for managing field connection
 * directionality in the field graph.
 */

#include <sstream>
#include "fields/direction.h"
#include "fields/field_definition.h"
#include "fields/field_link_definition.h"
#include "fields/relation.h"
#include "fields/obj.h"
#include "fields/flattened_type.h"

// Initialize static members
Direction* Direction::INPUT = new Input();
Direction* Direction::OUTPUT = new Output();


int Input::getDirectionId() const {
    return 0; // Return 0 for the "input" direction
}

Direction* Input::invert() const {
    return Direction::OUTPUT; // Return the OUTPUT direction
}

std::vector<FieldLinkDefinition*> Input::getFieldLinkDefinitions(FieldDefinition* fd) const {
    return fd->getInputs(); // Return the input field link definitions of the given FieldDefinition
}

FlattenedType* Input::getFlattenedType(Type* type) const {
    return type->getFlattenedTypeInputSide(); // Return the flattened type for the input direction
}

void Input::transmit(Field* originField,
                     FieldLinkDefinition* fl,
                     Obj* relatedObject) const {
    double inputFieldValue = relatedObject->getFieldValue(fl->getRelatedFD()); // Get the field value from the related object
    fl->getOriginFD()->transmit(originField, fl->getOppositeSide(), inputFieldValue); // Transmit the value to the output field
}


int Output::getDirectionId() const {
    return 1; // Return 1 for the "output" direction
}

Direction* Output::invert() const {
    return Direction::INPUT; // Return the INPUT direction as the inverse
}

std::vector<FieldLinkDefinition*> Output::getFieldLinkDefinitions(FieldDefinition* fd) const {
    return fd->getOutputs(); // Return the output field link definitions for the given FieldDefinition
}

FlattenedType* Output::getFlattenedType(Type* type) const {
    return type->getFlattenedTypeOutputSide(); // Return the flattened type for the output direction
}

void Output::transmit(Field* originField,
                      FieldLinkDefinition* fl,
                      Obj* relatedObject) const {
    // Transmit the related field value using the related field definition
    fl->getRelatedFD()->transmit(
        relatedObject->getOrCreateFieldInput(fl->getRelatedFD()), // Create or get the input field
        fl->getOppositeSide(),
        originField->getUpdate() // Pass the update of the origin field
    );
}
