/**
 * @file proxy_field.h
 * @brief Defines the ProxyField class, which represents a field that proxies to another field.
 *
 * The ProxyField class provides a mechanism for field aliasing, where multiple field definitions
 * from different parent types can reference the same underlying field. This is particularly useful
 * for merging fields from multiple inheritance hierarchies.
 *
 * Key characteristics:
 * - Does not appear in the input side flattening map
 * - Forwards all operations to its target field
 * - Enables bidirectional data flow (inference and backpropagation)
 */

#ifndef PROXY_FIELD_H
#define PROXY_FIELD_H

#include "fields/field_definition.h"
#include "fields/type.h"
#include "fields/object.h"

/**
 * @class ProxyField
 * @brief A field definition that acts as a proxy to another field.
 *
 * ProxyField enables field merging across multiple inheritance hierarchies.
 * For example, when a link type inherits from both an input base type and
 * an output base type, each with an input_value field, one can be a ProxyField
 * that references the other, resulting in a single merged field.
 *
 * Example use case:
 * - BaseInputType has input_value field (real field)
 * - BaseOutputType has input_value field (proxy field → BaseInputType's input_value)
 * - ConcreteLinkType inherits from both
 * - After flattening, only one input_value field exists
 */
class ProxyField : public FieldDefinition {
private:
    FieldDefinition* targetField;  ///< The actual field this proxy references

public:
    /**
     * @brief Constructs a new ProxyField
     * @param objectType The type in the object graph this field is associated with
     * @param name The name of the field definition
     * @param targetField The actual field this proxy references
     */
    ProxyField(Type* objectType, const std::string& name, FieldDefinition* targetField);

    /**
     * @brief Gets the target field this proxy references
     * @return The target field definition
     */
    FieldDefinition* getTargetField() const;

    /**
     * @brief Sets the target field this proxy references
     * @param target The target field definition
     */
    void setTargetField(FieldDefinition* target);

    /**
     * @brief Checks if this is a proxy field
     * @return Always returns true for ProxyField instances
     */
    bool isProxy() const override;

    /**
     * @brief Transmits an update by forwarding to the target field
     *
     * This method forwards the transmission to the target field, ensuring
     * that the proxy behaves identically to the real field.
     *
     * @param targetField The field receiving the update
     * @param fieldLink The link through which the update is transmitted
     * @param update The value of the update
     */
    void transmit(Field* targetField, FieldLinkDefinition* fieldLink, double update) override;
};

#endif // PROXY_FIELD_H