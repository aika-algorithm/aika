
#include "fields/direction.h"
#include "fields/field_definition.h"
#include "fields/field_link_definition.h"
#include "fields/relation.h"
#include "fields/obj.h"
#include "fields/flattened_type.h"

std::shared_ptr<Direction> Direction::INPUT = std::make_shared<Input>();
std::shared_ptr<Direction> Direction::OUTPUT = std::make_shared<Output>();


int Input::getDirectionId() const {
    return 0; // Return 0 for the "input" direction
}

std::shared_ptr<Direction> Input::invert() const {
    return Direction::OUTPUT; // Return the OUTPUT direction
}

std::vector<std::shared_ptr<FieldLinkDefinitionOutputSide>> Input::getFieldLinkDefinitions(std::shared_ptr<FieldDefinition> fd) const {
    return fd->getInputs(); // Return the input field link definitions of the given FieldDefinition
}

std::shared_ptr<FlattenedType> Input::getFlattenedType(std::shared_ptr<Type> type) const {
    return type->getFlattenedTypeInputSide(); // Return the flattened type for the input direction
}

void Input::transmit(std::shared_ptr<Field> originField,
                     std::shared_ptr<FieldLinkDefinition> fl,
                     std::shared_ptr<Obj> relatedObject) const {
    double inputFieldValue = relatedObject->getFieldValue(fl->getRelatedFD()); // Get the field value from the related object
    auto outputSide = std::dynamic_pointer_cast<FieldLinkDefinitionOutputSide>(fl); // Cast the field link to output side
    fl->getOriginFD()->transmit(originField, outputSide, inputFieldValue); // Transmit the value to the output field
}


int Output::getDirectionId() const {
    return 1; // Return 1 for the "output" direction
}

std::shared_ptr<Direction> Output::invert() const {
    return Direction::INPUT; // Return the INPUT direction as the inverse
}

std::vector<std::shared_ptr<FieldLinkDefinitionInputSide>> Output::getFieldLinkDefinitions(std::shared_ptr<FieldDefinition> fd) const {
    return fd->getOutputs(); // Return the output field link definitions for the given FieldDefinition
}

std::shared_ptr<FlattenedType> Output::getFlattenedType(std::shared_ptr<Type> type) const {
    return type->getFlattenedTypeOutputSide(); // Return the flattened type for the output direction
}

void Output::transmit(std::shared_ptr<Field> originField,
                      std::shared_ptr<FieldLinkDefinition> fl,
                      std::shared_ptr<Obj> relatedObject) const {
    // Cast the FieldLinkDefinition to the InputSide type
    auto flo = std::dynamic_pointer_cast<FieldLinkDefinitionOutputSide>(fl);

    // Transmit the related field value using the related field definition
    fl->getRelatedFD()->transmit(
        relatedObject->getOrCreateFieldInput(fl->getRelatedFD()), // Create or get the input field
        flo, // Use the output side of the link
        originField->getUpdate() // Pass the update of the origin field
    );
}
