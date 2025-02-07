#ifndef FIELD_H
#define FIELD_H

#include <string>

#include "fields/field_definition.h"
#include "fields/obj.h"


class Queue;
class QueueInterceptor;
class ProcessingPhase;


class Field {
private:
    FieldDefinition* fieldDefinition;
    int id;
    Obj* object;
    double value;
    double updatedValue;
    bool withinUpdate;
    QueueInterceptor* interceptor;

public:
    Field(Obj* obj, FieldDefinition* fd, int id);

    int getId() const;
    bool isWithinUpdate();
    double getValue();
    double getUpdatedValue();
    Obj* getObject();

    Field& setQueued(Queue* q, ProcessingPhase* phase, bool isNextRound);

    FieldDefinition* getFieldDefinition();

    double getTolerance() const;
    std::string getName() const;

    QueueInterceptor* getInterceptor();
    void setInterceptor(QueueInterceptor* interceptor);

    void setValue(double v);
    void triggerUpdate(double u);
    double getUpdate() const;
    void propagateUpdate();

    void receiveUpdate(double u);

    std::string toString() const;
    std::string getValueString() const;

    static bool isTrue(Field* f);
    static bool isTrue(Field* f, bool updatedValue);
    static bool isTrue(double v);
};

#endif // FIELD_H
