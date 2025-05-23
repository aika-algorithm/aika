#ifndef OBJ_H
#define OBJ_H

#include <string>

#include <fields/type.h>
#include <fields/queue_provider.h>
#include <fields/rel_obj_iterator.h>


class Obj : public QueueProvider {
protected:
    Type* type;
    Field** fields;

public:
    explicit Obj(Type* type);

    void initFields();
    Type* getType() const;
    virtual RelatedObjectIterable* followManyRelation(Relation* rel) const = 0;
    virtual Obj* followSingleRelation(const Relation* rel) const = 0;
    bool isInstanceOf(Type* t) const;
    Field* getFieldInput(FieldDefinition* fd) const;
    Field* getFieldOutput(FieldDefinition* fd) const;
    Field* getOrCreateFieldInput(FieldDefinition* fd);
    Obj& setFieldValue(FieldDefinition* fd, double v);
    double getFieldValue(FieldDefinition* fd) const;
    double getFieldUpdatedValue(FieldDefinition* fd) const;
    Field** getFields() const;

    std::string toKeyString() const;
    std::string toString() const;
    std::string getFieldAsString(FieldDefinition* fd) const;
};

#endif // OBJ_H
