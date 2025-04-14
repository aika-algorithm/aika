/**
 * @file field_link_definition.h
 * @brief Defines the FieldLinkDefinition class, which represents connections between fields in the field graph.
 * 
 * FieldLinkDefinition instances form the edges of the field graph, connecting field definitions
 * and specifying how data flows between them. Each link has a direction, relation, and optional
 * argument position, enabling the construction of complex mathematical models through field connections.
 */

#ifndef FIELD_LINK_DEFINITION_H
#define FIELD_LINK_DEFINITION_H

#include <string>
#include <memory>

#include "fields/field_definition.h"
#include "fields/relation.h"
#include "fields/direction.h"
#include "fields/obj.h"

/**
 * @class FieldLinkDefinition
 * @brief Represents a connection between two fields in the field graph.
 * 
 * FieldLinkDefinition instances define the edges of the field graph, specifying:
 * - The origin and related field definitions
 * - The relation governing the connection
 * - The direction of data flow
 * - An optional argument position for ordered inputs
 * 
 * Links are created in pairs (input and output sides) to maintain bidirectional
 * connectivity in the field graph.
 */
class FieldLinkDefinition {
private:
    FieldLinkDefinition* oppositeSide;  ///< The paired link in the opposite direction
    FieldDefinition* originFD;          ///< The field definition at the origin of the link
    FieldDefinition* relatedFD;         ///< The field definition at the related end of the link
    Relation* relation;                 ///< The relation governing the connection
    Direction* direction;               ///< The direction of data flow
    std::optional<int> argument;        ///< Optional argument position for ordered inputs

public:
    /**
     * @brief Creates a pair of linked field definitions
     * 
     * Establishes bidirectional connections between input and output field definitions
     * using the specified relation and argument position.
     * 
     * @param input The input field definition
     * @param output The output field definition
     * @param relation The relation governing the connection
     * @param argument Optional argument position for ordered inputs
     */
    static void link(FieldDefinition* input,
                     FieldDefinition* output,
                     Relation* relation,
                     std::optional<int> argument);

    /**
     * @brief Constructs a new FieldLinkDefinition with an argument position
     * 
     * @param originFD The field definition at the origin
     * @param relatedFD The field definition at the related end
     * @param relation The relation governing the connection
     * @param direction The direction of data flow
     * @param argument The argument position for ordered inputs
     */
    FieldLinkDefinition(FieldDefinition* originFD,
                        FieldDefinition* relatedFD,
                        Relation* relation,
                        Direction* direction,
                        std::optional<int> argument);

    /**
     * @brief Constructs a new FieldLinkDefinition without an argument position
     * 
     * @param originFD The field definition at the origin
     * @param relatedFD The field definition at the related end
     * @param relation The relation governing the connection
     * @param direction The direction of data flow
     */
    FieldLinkDefinition(FieldDefinition* originFD,
                        FieldDefinition* relatedFD,
                        Relation* relation,
                        Direction* direction);

    /**
     * @brief Gets the paired link in the opposite direction
     * 
     * @return The opposite side link
     */
    FieldLinkDefinition* getOppositeSide() const;

    /**
     * @brief Sets the paired link in the opposite direction
     * 
     * @param os The opposite side link to set
     */
    void setOppositeSide(FieldLinkDefinition* os);

    /**
     * @brief Gets the field definition at the origin of the link
     * 
     * @return The origin field definition
     */
    FieldDefinition* getOriginFD() const;

    /**
     * @brief Gets the field definition at the related end of the link
     * 
     * @return The related field definition
     */
    FieldDefinition* getRelatedFD() const;

    /**
     * @brief Gets the relation governing the connection
     * 
     * @return The relation
     */
    Relation* getRelation() const;

    /**
     * @brief Gets the direction of data flow
     * 
     * @return The direction
     */
    Direction* getDirection() const;

    /**
     * @brief Gets the argument position for ordered inputs
     * 
     * @return The argument position
     */
     std::optional<int> getArgument() const;

     std::string getArgumentAsString() const;


    /**
     * @brief Gets the input field for a given object
     * 
     * @param obj The object to get the input field for
     * @return The input field
     */
    Field* getInputField(Obj* obj);

    /**
     * @brief Gets the current input value for a given object
     * 
     * @param obj The object to get the input value for
     * @return The current input value
     */
    double getInputValue(Obj* obj);

    /**
     * @brief Gets the updated input value for a given object
     * 
     * @param obj The object to get the updated input value for
     * @return The updated input value
     */
    double getUpdatedInputValue(Obj* obj);

    /**
     * @brief Converts the field link definition to a string representation
     * 
     * @return String representation
     */
    std::string toString() const;
};

#endif //FIELD_LINK_DEFINITION_H
