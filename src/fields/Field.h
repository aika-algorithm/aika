#ifndef FIELD_H
#define FIELD_H

#include <string>
#include <memory>
#include <iostream>
#include <sstream>
#include <optional>

class FieldInput {
public:
    virtual bool isWithinUpdate() = 0;
    virtual double getValue() = 0;
    virtual double getUpdatedValue() = 0;
    virtual std::shared_ptr<Obj> getObject() = 0;
};

class FieldOutput {
public:
    virtual double getValue() = 0;
    virtual double getUpdatedValue() = 0;
};

class FieldWritable {
public:
    virtual void write(std::shared_ptr<DataOutput> out) = 0;
    virtual void readFields(std::shared_ptr<DataInput> in) = 0;
};

class Field : public FieldInput, public FieldOutput, public FieldWritable {
private:
    std::shared_ptr<FieldDefinition> fieldDefinition;
    short id;
    std::shared_ptr<Obj> object;
    double value;
    double updatedValue;
    bool withinUpdate;
    std::shared_ptr<QueueInterceptor> interceptor;

public:
    Field(std::shared_ptr<Obj> obj, std::shared_ptr<FieldDefinition> fd, short id);

    short getId() const;
    bool isWithinUpdate() override;
    double getValue() override;
    double getUpdatedValue() override;
    std::shared_ptr<Obj> getObject() override;

    Field& setQueued(std::shared_ptr<Queue> q, std::shared_ptr<ProcessingPhase> phase, bool isNextRound);

    std::shared_ptr<FieldDefinition> getFieldDefinition() override;

    double getTolerance() const;
    std::string getName() const;

    std::shared_ptr<QueueInterceptor> getInterceptor();
    void setInterceptor(std::shared_ptr<QueueInterceptor> interceptor);

    void setValue(double v);
    void triggerUpdate(double u);
    double getUpdate() const;
    void propagateUpdate();

    void receiveUpdate(double u) override;
    void write(std::shared_ptr<DataOutput> out) override;
    void readFields(std::shared_ptr<DataInput> in) override;
    std::string toString() const override;
    std::string getValueString() const;

    static bool isTrue(std::shared_ptr<FieldOutput> f);
    static bool isTrue(std::shared_ptr<FieldOutput> f, bool updatedValue);
    static bool isTrue(double v);
};

#endif // FIELD_H
