/**
 * @file direction.h
 * @brief Defines the Direction interface and its implementations for field connections.
 * 
 * The Direction interface and its implementations (Input and Output) provide
 * directionality for field connections in the field graph, enabling proper
 * update propagation and graph traversal.
 */

#ifndef DIRECTION_H
#define DIRECTION_H

#include <vector>

class Obj;
class Field;
class FieldDefinition;
class FieldLinkDefinition;
class FlattenedType;
class Type;

class Field;
class FieldLinkDefinition;

/**
 * @class Direction
 * @brief Interface for field connection directionality.
 * 
 * The Direction interface defines the contract for handling directionality
 * in field connections. It provides:
 * - Direction identification
 * - Link traversal
 * - Update transmission
 * - Direction inversion
 * 
 * This abstraction enables flexible and extensible field graph structures
 * with proper update propagation.
 */
class Direction {
public:
    static Direction* INPUT;  ///< Singleton instance for input direction
    static Direction* OUTPUT; ///< Singleton instance for output direction

    virtual ~Direction() = default;

    /**
     * @brief Gets the direction identifier
     * 
     * @return The direction ID
     */
    virtual int getDirectionId() const = 0;

    /**
     * @brief Gets the inverse direction
     * 
     * @return The opposite direction
     */
    virtual Direction* invert() const = 0;

    /**
     * @brief Gets the link definition for a field
     * 
     * @param field The field to get the link for
     * @return The link definition
     */
    virtual std::vector<FieldLinkDefinition*> getFieldLinkDefinitions(FieldDefinition* fd) const = 0;
    
    /**
     * @brief Gets the flattened type for a given type in this direction
     * 
     * Retrieves the flattened type representation associated with the given type
     * in the current direction (input or output). Flattened types are used to
     * optimize type hierarchy traversal during runtime.
     *
     * @param type The type to get the flattened representation for
     * @return The flattened type representation
     */
    virtual FlattenedType* getFlattenedType(Type* type) const = 0;

    /**
     * @brief Transmits an update through the connection
     * 
     * @param field The field being updated
     * @param link The link definition
     * @param update The update value
     */
    virtual void transmit(Field* originField,
                          FieldLinkDefinition* fl,
                          Obj* relatedObject) const = 0;
};

/**
 * @class Input
 * @brief Represents an input direction in field connections.
 * 
 * The Input class implements the Direction interface for input connections,
 * handling update reception and propagation from source to target fields.
 */
class Input : public Direction {
public:
    int getDirectionId() const override;
    Direction* invert() const override;
    std::vector<FieldLinkDefinition*> getFieldLinkDefinitions(FieldDefinition* fd) const override;
    FlattenedType* getFlattenedType(Type* type) const override;
    void transmit(Field* originField,
                  FieldLinkDefinition* fl,
                  Obj* relatedObject) const override;
};

/**
 * @class Output
 * @brief Represents an output direction in field connections.
 * 
 * The Output class implements the Direction interface for output connections,
 * handling update propagation from target to source fields.
 */
class Output : public Direction {
public:
    int getDirectionId() const override;
    Direction* invert() const override;
    std::vector<FieldLinkDefinition*> getFieldLinkDefinitions(FieldDefinition* fd) const override;
    FlattenedType* getFlattenedType(Type* type) const override;
    void transmit(Field* originField,
                  FieldLinkDefinition* fl,
                  Obj* relatedObject) const override;
};

#endif // DIRECTION_H
