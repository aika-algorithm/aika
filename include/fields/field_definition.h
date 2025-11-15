/**
 * @file field_definition.h
 * @brief Defines the FieldDefinition class, which forms the core of the field graph in AIKA's mathematical model.
 * 
 * The FieldDefinition class serves as the blueprint for fields in the field graph, which is a declarative,
 * graph-based representation of mathematical models. Each field definition specifies:
 * - The type of computation (through subclasses)
 * - Connections to other fields (inputs/outputs)
 * - Properties like tolerance and processing phase
 * - Association with object types in the object graph
 */

#ifndef FIELD_DEFINITION_H
#define FIELD_DEFINITION_H

#include <string>
#include <memory>
#include <vector>
#include <set>
#include <functional>
#include <iostream>
#include <optional>
#include <stdexcept>

class Field;
class FieldLinkDefinition;
class Type;
class Relation;
class FixedArgumentsFieldDefinition;
class VariableArgumentsFieldDefinition;
class ProcessingPhase;
class QueueInterceptor;

/**
 * @class FieldDefinition
 * @brief Base class for defining fields in the field graph.
 * 
 * Fields are the computational units in AIKA's mathematical model. Each field definition:
 * - Specifies a mathematical operation or computation
 * - Defines connections to other fields through inputs and outputs
 * - Associates with object types in the object graph
 * - Manages properties like tolerance and processing phase
 * 
 * The field graph forms a declarative representation where nodes are fields
 * and edges are field links, enabling event-driven updates and sparse activation.
 */
class FieldDefinition {
protected:
    int fieldId;                    ///< Unique identifier for the field definition
    std::string name;               ///< Name of the field definition
    std::vector<FieldLinkDefinition*> inputs;  ///< Input field links
    std::vector<FieldLinkDefinition*> outputs; ///< Output field links
    FieldDefinition* parent;        ///< Parent field definition in inheritance hierarchy
    std::vector<FieldDefinition*> children;    ///< Child field definitions
    Type* objectType;               ///< Associated object type in the object graph
    std::optional<double> tolerance;///< Tolerance threshold for updates
    ProcessingPhase* phase;         ///< Processing phase for this field
    bool isNextRound;               ///< Flag indicating if field should be processed in next round

public:
    /**
     * @brief Constructs a new FieldDefinition
     * @param objectType The type in the object graph this field is associated with
     * @param name The name of the field definition
     * @param numArgs The number of arguments this field expects
     * @param tolerance The tolerance threshold for updates
     */
    FieldDefinition(Type* objectType, const std::string& name, int numArgs, double tolerance);
    virtual ~FieldDefinition() = default;

    /**
     * @brief Sets the unique identifier for this field definition
     * @param fieldId The identifier to set
     */
    void setFieldId(int fieldId);

    /**
     * @brief Transmits an update to a target field through a field link
     * @param targetField The field receiving the update
     * @param fieldLink The link through which the update is transmitted
     * @param update The value of the update
     */
    virtual void transmit(Field* targetField, FieldLinkDefinition* fieldLink, double update) = 0;

    /**
     * @brief Receives an update from another field
     * @param field The field sending the update
     * @param update The value of the update
     */
    void receiveUpdate(Field* field, double update);

    /**
     * @brief Gets the parent field definition
     * @return The parent field definition
     */
    FieldDefinition* getParent() const;

    /**
     * @brief Sets the parent field definition
     * @param parent The parent to set
     * @return This field definition for method chaining
     */
    FieldDefinition* setParent(FieldDefinition* parent);

    /**
     * @brief Gets all child field definitions
     * @return Vector of child field definitions
     */
    std::vector<FieldDefinition*> getChildren() const;

    /**
     * @brief Checks if this field is required given a set of field definitions
     * @param fieldDefs The set of field definitions to check against
     * @return true if this field is required, false otherwise
     */
    bool isFieldRequired(const std::set<FieldDefinition*>& fieldDefs);

    /**
     * @brief Resolves the inherited field definition from a set of field definitions
     * @param fieldDefs The set of field definitions to resolve from
     * @return The resolved field definition
     */
    FieldDefinition* resolveInheritedFieldDefinition(const std::set<FieldDefinition*>& fieldDefs);

    /**
     * @brief Initializes a field instance based on this definition
     * @param field The field to initialize
     */
    virtual void initializeField(Field* field);

    /**
     * @brief Checks if this is a proxy field
     * @return true if this is a proxy field, false otherwise (default)
     */
    virtual bool isProxy() const { return false; }
    /**
     * @brief Adds an input field link
     * @param fl The field link to add
     */
    void addInput(FieldLinkDefinition* fl);

    /**
     * @brief Gets all input field links
     * @return Vector of input field links
     */
    std::vector<FieldLinkDefinition*> getInputs();

    /**
     * @brief Adds an output field link
     * @param fl The field link to add
     */
    void addOutput(FieldLinkDefinition* fl);

    /**
     * @brief Gets all output field links
     * @return Vector of output field links
     */
    std::vector<FieldLinkDefinition*> getOutputs();

    /**
     * @brief Creates an input connection with a relation
     * @param relation The relation to use
     * @param input The input field definition
     * @param arg The argument index
     * @return This field definition for method chaining
     */
    FieldDefinition& input(Relation& relation, FieldDefinition& input, int arg);

    /**
     * @brief Creates an output connection with a relation
     * @param relation The relation to use
     * @param output The output field definition
     * @param arg The argument index
     * @return This field definition for method chaining
     */
    FieldDefinition& output(Relation& relation, FieldDefinition& output, int arg);

    /**
     * @brief Sets the name of this field definition
     * @param name The name to set
     * @return This field definition for method chaining
     */
    FieldDefinition& setName(const std::string& name);

    /**
     * @brief Gets the name of this field definition
     * @return The name
     */
    std::string getName() const;

    /**
     * @brief Gets the associated object type
     * @return The object type
     */
    Type* getObjectType() const;

    /**
     * @brief Gets the field ID
     * @return The field ID
     */
    int getId() const;

    /**
     * @brief Sets the associated object type
     * @param objectType The object type to set
     * @return This field definition for method chaining
     */
    FieldDefinition& setObjectType(Type* objectType);

    /**
     * @brief Gets the tolerance threshold
     * @return The tolerance threshold
     */
    std::optional<double> getTolerance() const;

    /**
     * @brief Sets the tolerance threshold
     * @param tolerance The tolerance to set
     * @return This field definition for method chaining
     */
    FieldDefinition& setTolerance(std::optional<double> tolerance);

    /**
     * @brief Gets the processing phase
     * @return The processing phase
     */
    ProcessingPhase* getPhase() const;

    /**
     * @brief Sets the processing phase
     * @param phase The phase to set
     * @return This field definition for method chaining
     */
    FieldDefinition& setPhase(ProcessingPhase* phase);

    /**
     * @brief Gets the next round flag
     * @return true if field should be processed in next round
     */
    bool getIsNextRound() const;

    /**
     * @brief Sets the next round flag
     * @param nextRound The value to set
     * @return This field definition for method chaining
     */
    FieldDefinition& setNextRound(bool nextRound);

    /**
     * @brief Sets the field to be queued in a specific phase
     * @param phase The phase to queue in
     * @return This field definition for method chaining
     */
    FieldDefinition& setQueued(ProcessingPhase* phase);

    /**
     * @brief Converts the field definition to a string representation
     * @return String representation
     */
    std::string toString() const;

    /**
     * @brief Less-than comparison operator for sorting
     * @param fd The field definition to compare against
     * @return true if this field definition is less than the other
     */
    bool operator<(const FieldDefinition& fd) const;
};

#endif // FIELD_DEFINITION_H
