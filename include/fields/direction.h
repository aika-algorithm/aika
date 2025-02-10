
#ifndef DIRECTION_H
#define DIRECTION_H

#include <vector>

class Obj;
class Field;
class FieldDefinition;
class FieldLinkDefinition;
class FlattenedType;
class Type;


class Direction {
public:
    static Direction* INPUT;
    static Direction* OUTPUT;

    virtual int getDirectionId() const = 0;
    virtual Direction* invert() const = 0;
    virtual std::vector<FieldLinkDefinition*> getFieldLinkDefinitions(FieldDefinition* fd) const = 0;
    virtual FlattenedType* getFlattenedType(Type* type) const = 0;
    virtual void transmit(Field* originField,
                          FieldLinkDefinition* fl,
                          Obj* relatedObject) const = 0;

    virtual ~Direction() = default; // Ensure proper cleanup for derived classes
};


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


#endif //DIRECTION_H
