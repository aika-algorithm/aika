#ifndef RELATION_H
#define RELATION_H

#include <vector>
#include <string>

#include <fields/obj.h>
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
    Relation* getReverse();
    virtual RelatedObjectIterable& followMany(Obj* fromObj) = 0;
    virtual bool testRelation(Obj* fromObj, Obj* toObj) const = 0;
    virtual std::string getRelationLabel() const = 0;
    std::string Relation::toString() const;

    virtual ~Relation() = default; // Ensure proper cleanup for derived classes
};


class RelationMany : public Relation {
public:
    RelationMany(int relationId, const std::string& relationName);

    RelatedObjectIterable& followMany(Obj* fromObj) override;
    bool testRelation(Obj* fromObj, Obj* toObj) const override;
    std::string getRelationLabel() const override;
};


class RelationOne : public Relation {
public:
    RelationOne(int relationId, const std::string& relationName);

    Obj* followOne(Obj* fromObj) const;
    RelatedObjectIterable& followMany(Obj* fromObj) override;
    bool testRelation(Obj* fromObj, Obj* toObj) const override;
    std::string getRelationLabel() const override;
};


class RelationSelf : public RelationOne {
public:
    RelationSelf(int relationId, const std::string& relationName);

    void setReversed(Relation* reversed);
    Relation* getReverse() const;
    Obj* followOne(Obj* fromObj) const;
    RelatedObjectIterable& followMany(Obj* fromObj) override;
    bool testRelation(Obj* fromObj, Obj* toObj) const override;
};

#endif // RELATION_H
