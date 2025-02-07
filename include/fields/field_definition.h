#ifndef FIELDDDEFINITION_H
#define FIELDDDEFINITION_H

#include <string>
#include <memory>
#include <vector>
#include <set>
#include <functional>
#include <iostream>
#include <optional>
#include <stdexcept>

class Field;
class FieldLinkDefinitionInputSide;
class FieldLinkDefinitionOutputSide;
class Type;
class Relation;
class FixedArgumentsFieldDefinition;
class VariableArgumentsFieldDefinition;
class ProcessingPhase;
class QueueInterceptor;


class FieldDefinition {
protected:
    int fieldId;
    std::string name;
    std::vector<FieldLinkDefinitionInputSide*> outputs;
    FieldDefinition* parent;
    std::vector<FieldDefinition*> children;
    Type* objectType;
    std::optional<double> tolerance;
    ProcessingPhase* phase;
    bool isNextRound;

public:
    FieldDefinition(Type* objectType, const std::string& name);
    FieldDefinition(Type* objectType, const std::string& name, double tolerance);

    void setFieldId(int fieldId);
    void transmit(Field* targetField, FieldLinkDefinitionOutputSide* fieldLink, double update);
    void receiveUpdate(Field* field, double update);

    FieldDefinition* getParent() const;
    FieldDefinition* setParent(FieldDefinition* parent);
    std::vector<FieldDefinition*> getChildren() const;

    bool isFieldRequired(const std::set<FieldDefinition*>& fieldDefs);
    FieldDefinition* resolveInheritedFieldDefinition(const std::set<FieldDefinition*>& fieldDefs);

    void initializeField(Field* field);
    void addInput(FieldLinkDefinitionOutputSide* fl);
    std::vector<FieldLinkDefinitionOutputSide*> getInputs();
    void addOutput(FieldLinkDefinitionInputSide* fl);
    std::vector<FieldLinkDefinitionInputSide*> getOutputs();

    FieldDefinition& out(Relation* relation, FieldDefinition* output, int arg);

    FieldDefinition& setName(const std::string& name);
    std::string getName() const;
    Type* getObjectType() const;
    int getId() const;
    FieldDefinition& setObjectType(Type* objectType);
    std::optional<double> getTolerance() const;
    FieldDefinition& setTolerance(std::optional<double> tolerance);
    ProcessingPhase* getPhase() const;
    FieldDefinition& setPhase(ProcessingPhase* phase);
    bool getIsNextRound() const;
    FieldDefinition& setNextRound(bool nextRound);
    FieldDefinition& setQueued(ProcessingPhase* phase);

    std::string toString() const;

    bool operator<(const FieldDefinition& fd) const; // For sorting (compareTo)
};

#endif // FIELDDDEFINITION_H
