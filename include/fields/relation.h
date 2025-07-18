#ifndef RELATION_H
#define RELATION_H

#include <vector>
#include <string>

#include <fields/object.h>
#include "fields/rel_obj_iterator.h"

class Relation {
protected:
    int relationId;
    std::string relationName;
    Relation* reversed;

public:
    Relation(int relationId, const std::string& relationName);

    int getRelationId() const;
    void setReversed(Relation* reversed);
    virtual Relation* getReverse();
    virtual RelatedObjectIterable* followMany(Object* fromObj) = 0;
    virtual bool testRelation(Object* fromObj, Object* toObj) const = 0;
    virtual std::string getRelationLabel() const = 0;

    virtual ~Relation() = default; // Ensure proper cleanup for derived classes
};


class RelationMany : public Relation {
public:
    RelationMany(int relationId, const std::string& relationName);

    RelatedObjectIterable* followMany(Object* fromObj) override;
    bool testRelation(Object* fromObj, Object* toObj) const override;
    std::string getRelationLabel() const override;
};


class RelationOne : public Relation {
public:
    RelationOne(int relationId, const std::string& relationName);

    virtual Object* followOne(Object* fromObj) const;
    RelatedObjectIterable* followMany(Object* fromObj) override;
    bool testRelation(Object* fromObj, Object* toObj) const override;
    std::string getRelationLabel() const override;
};

class RelationSelf : public RelationOne {
public:
    RelationSelf(int relationId, const std::string& relationName);

    Object* followOne(Object* fromObj) const override;
    RelatedObjectIterable* followMany(Object* fromObj) override;
    bool testRelation(Object* fromObj, Object* toObj) const override;
    std::string getRelationLabel() const override;
};

#endif // RELATION_H