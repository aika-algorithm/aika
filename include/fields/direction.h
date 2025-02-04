
#ifndef DIRECTION_H
#define DIRECTION_H

#include <memory>
#include <vector>
#include <functional>

#include "fields/field_link_definition.h"
#include "fields/field_definition.h"
#include "fields/flattened_type.h"
#include "fields/field.h"
#include "fields/obj.h"

class Direction {
public:
    static std::shared_ptr<Direction> INPUT;
    static std::shared_ptr<Direction> OUTPUT;

    virtual int getDirectionId() const = 0;
    virtual std::shared_ptr<Direction> invert() const = 0;
    virtual std::vector<std::shared_ptr<FieldLinkDefinition>> getFieldLinkDefinitions(std::shared_ptr<FieldDefinition> fd) const = 0;
    virtual std::shared_ptr<FlattenedType> getFlattenedType(std::shared_ptr<Type> type) const = 0;
    virtual void transmit(std::shared_ptr<Field> originField,
                          std::shared_ptr<FieldLinkDefinition> fl,
                          std::shared_ptr<Obj> relatedObject) const = 0;

    virtual ~Direction() = default; // Ensure proper cleanup for derived classes
};


class Input : public Direction {
public:
    int getDirectionId() const override;
    std::shared_ptr<Direction> invert() const override;
    std::vector<std::shared_ptr<FieldLinkDefinitionOutputSide>> getFieldLinkDefinitions(std::shared_ptr<FieldDefinition> fd) const override;
    std::shared_ptr<FlattenedType> getFlattenedType(std::shared_ptr<Type> type) const override;
    void transmit(std::shared_ptr<Field> originField,
                  std::shared_ptr<FieldLinkDefinition> fl,
                  std::shared_ptr<Obj> relatedObject) const override;
};


class Output : public Direction {
public:
    int getDirectionId() const override;
    std::shared_ptr<Direction> invert() const override;
    std::vector<std::shared_ptr<FieldLinkDefinitionInputSide>> getFieldLinkDefinitions(std::shared_ptr<FieldDefinition> fd) const override;
    std::shared_ptr<FlattenedType> getFlattenedType(std::shared_ptr<Type> type) const override;
    void transmit(std::shared_ptr<Field> originField,
                  std::shared_ptr<FieldLinkDefinition> fl,
                  std::shared_ptr<Obj> relatedObject) const override;
};


#endif //DIRECTION_H
