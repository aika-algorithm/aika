#ifndef OBJ_H
#define OBJ_H

#include <string>
#include <memory>
#include <vector>
#include <iostream>
#include <functional>
#include <optional>
#include <type_traits>
#include <sstream>

#include <fields/type.h>
#include <fields/queue.h>


class Obj : public QueueProvider {
protected:
    std::shared_ptr<Type> type;
    std::vector<std::shared_ptr<Field>> fields;

public:
    explicit Obj(std::shared_ptr<Type> type);

    void initFields();
    std::shared_ptr<Type> getType() const;
    std::shared_ptr<Obj> followManyRelation(std::shared_ptr<Relation> rel);
    std::shared_ptr<Obj> followSingleRelation(std::shared_ptr<Relation> rel);
    bool isInstanceOf(std::shared_ptr<Type> t);
    std::shared_ptr<Field> getFieldOutput(std::shared_ptr<FieldDefinition> fd);
    std::shared_ptr<Field> getOrCreateFieldInput(std::shared_ptr<FieldDefinition> fd);
    Obj& setFieldValue(std::shared_ptr<FieldDefinition> fd, double v);
    double getFieldValue(std::shared_ptr<FieldDefinition> fd);
    double getFieldUpdatedValue(std::shared_ptr<FieldDefinition> fd);
    std::vector<std::shared_ptr<Field>> getFields();
    std::shared_ptr<Queue> getQueue();

    std::string toKeyString();
    std::string toString() const;
};

#endif // OBJ_H
