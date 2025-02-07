#ifndef OBJ_H
#define OBJ_H

#include <string>
#include <memory>
#include <vector>
#include <iostream>

#include <fields/type.h>
#include <fields/queue_provider.h>


class Obj : public QueueProvider {
protected:
    Type* type;
    std::vector<Field*> fields;

public:
    explicit Obj(Type* type);

    void initFields();
    Type* getType() const;
    Obj* followManyRelation(Relation* rel);
    Obj* followSingleRelation(Relation* rel);
    bool isInstanceOf(Type* t);
    Field* getFieldOutput(FieldDefinition* fd);
    Field* getOrCreateFieldInput(FieldDefinition* fd);
    Obj& setFieldValue(FieldDefinition* fd, double v);
    double getFieldValue(FieldDefinition* fd);
    double getFieldUpdatedValue(FieldDefinition* fd);
    std::vector<Field*> getFields();
    Queue* getQueue();

    std::string toKeyString();
    std::string toString() const;
};

#endif // OBJ_H
