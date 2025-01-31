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


class Obj : public QueueProvider, public Writable<TypeRegistry> {
protected:
    std::shared_ptr<Type> type;
    std::vector<std::shared_ptr<Field>> fields;

public:
    explicit Obj(std::shared_ptr<Type> type);

    void initFields() override;
    std::shared_ptr<Type> getType() const override;
    std::shared_ptr<Obj> followManyRelation(std::shared_ptr<Relation> rel) override;
    std::shared_ptr<Obj> followSingleRelation(std::shared_ptr<Relation> rel) override;
    bool isInstanceOf(std::shared_ptr<Type> t) override;
    std::shared_ptr<Field> getFieldOutput(std::shared_ptr<FieldDefinition> fd) override;
    std::shared_ptr<Field> getOrCreateFieldInput(std::shared_ptr<FieldDefinition> fd) override;
    Obj& setFieldValue(std::shared_ptr<FieldDefinition> fd, double v) override;
    double getFieldValue(std::shared_ptr<FieldDefinition> fd) override;
    double getFieldUpdatedValue(std::shared_ptr<FieldDefinition> fd) override;
    std::vector<std::shared_ptr<Field>> getFields() override;
    std::shared_ptr<Queue> getQueue() override;
    void write(std::shared_ptr<DataOutput> out) override;
    void readFields(std::shared_ptr<DataInput> in, std::shared_ptr<TypeRegistry> m) override;
    std::string toKeyString() override;
    std::string toString() const override;
};

#endif // OBJ_H
