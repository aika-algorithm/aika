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

class FieldDefinition : public std::enable_shared_from_this<FieldDefinition> {
protected:
    std::optional<int> fieldId;
    std::string name;
    std::vector<std::shared_ptr<FieldLinkDefinitionInputSide>> outputs;
    std::shared_ptr<FieldDefinition> parent;
    std::vector<std::shared_ptr<FieldDefinition>> children;
    std::shared_ptr<Type> objectType;
    std::optional<double> tolerance;
    std::shared_ptr<ProcessingPhase> phase;
    bool isNextRound;

public:
    FieldDefinition(std::shared_ptr<Type> objectType, const std::string& name);
    FieldDefinition(std::shared_ptr<Type> objectType, const std::string& name, double tolerance);

    void setFieldId(int fieldId);
    void transmit(std::shared_ptr<Field> targetField, std::shared_ptr<FieldLinkDefinitionOutputSide> fieldLink, double update);
    void receiveUpdate(std::shared_ptr<Field> field, double update);

    std::shared_ptr<FieldDefinition> getParent() const;
    std::shared_ptr<FieldDefinition> setParent(std::shared_ptr<FieldDefinition> parent);
    std::vector<std::shared_ptr<FieldDefinition>> getChildren() const;

    bool isFieldRequired(const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs);
    std::shared_ptr<FieldDefinition> resolveInheritedFieldDefinition(const std::set<std::shared_ptr<FieldDefinition>>& fieldDefs);

    void initializeField(std::shared_ptr<Field> field);
    void addInput(std::shared_ptr<FieldLinkDefinitionOutputSide> fl);
    std::vector<std::shared_ptr<FieldLinkDefinitionOutputSide>> getInputs();
    void addOutput(std::shared_ptr<FieldLinkDefinitionInputSide> fl);
    std::vector<std::shared_ptr<FieldLinkDefinitionInputSide>> getOutputs();

    FieldDefinition& out(std::shared_ptr<Relation> relation, std::shared_ptr<FixedArgumentsFieldDefinition> output, int arg);
    FieldDefinition& out(std::shared_ptr<Relation> relation, std::shared_ptr<VariableArgumentsFieldDefinition> output);

    FieldDefinition& setName(const std::string& name);
    std::string getName() const;
    std::shared_ptr<Type> getObjectType() const;
    std::optional<int> getId() const;
    FieldDefinition& setObjectType(std::shared_ptr<Type> objectType);
    std::optional<double> getTolerance() const;
    FieldDefinition& setTolerance(std::optional<double> tolerance);
    std::shared_ptr<ProcessingPhase> getPhase() const;
    FieldDefinition& setPhase(std::shared_ptr<ProcessingPhase> phase);
    bool getIsNextRound() const;
    FieldDefinition& setNextRound(bool nextRound);
    FieldDefinition& setQueued(std::shared_ptr<ProcessingPhase> phase);

    std::string toString() const;

    bool operator<(const FieldDefinition& fd) const; // For sorting (compareTo)
};

#endif // FIELDDDEFINITION_H
