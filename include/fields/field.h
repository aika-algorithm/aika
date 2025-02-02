#ifndef FIELD_H
#define FIELD_H

#include <string>
#include <memory>
#include <iostream>
#include <sstream>
#include <optional>

class Obj;
class Field;
class FieldDefinition;
class Queue;
class QueueInterceptor;
class ProcessingPhase;



class Field {
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
    bool isWithinUpdate();
    double getValue();
    double getUpdatedValue();
    std::shared_ptr<Obj> getObject();

    Field& setQueued(std::shared_ptr<Queue> q, std::shared_ptr<ProcessingPhase> phase, bool isNextRound);

    std::shared_ptr<FieldDefinition> getFieldDefinition();

    double getTolerance() const;
    std::string getName() const;

    std::shared_ptr<QueueInterceptor> getInterceptor();
    void setInterceptor(std::shared_ptr<QueueInterceptor> interceptor);

    void setValue(double v);
    void triggerUpdate(double u);
    double getUpdate() const;
    void propagateUpdate();

    void receiveUpdate(double u);

    std::string toString() const;
    std::string getValueString() const;

    static bool isTrue(std::shared_ptr<Field> f);
    static bool isTrue(std::shared_ptr<Field> f, bool updatedValue);
    static bool isTrue(double v);
};

#endif // FIELD_H
