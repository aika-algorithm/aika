#ifndef RELATION_H
#define RELATION_H

#include <memory>
#include <vector>
#include <string>
#include <functional>
#include "fields/obj.h"


class Relation {
protected:
    int relationId;
    std::string relationName;
    std::shared_ptr<Relation> reversed;

public:
    Relation(int relationId, const std::string& relationName);

    int getRelationId() const;
    void setReversed(std::shared_ptr<Relation> reversed);
    std::shared_ptr<Relation> getReverse();
    virtual std::vector<std::shared_ptr<Obj>> followMany(std::shared_ptr<Obj> fromObj) const = 0;
    virtual bool testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const = 0;
    virtual std::string getRelationLabel() const = 0;

    virtual ~Relation() = default; // Ensure proper cleanup for derived classes
};


class RelationMany : public Relation {
public:
    RelationMany(int relationId, const std::string& relationName);

    std::vector<std::shared_ptr<Obj>> followMany(std::shared_ptr<Obj> fromObj) const override;
    bool testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const override;
    std::string getRelationLabel() const override;
};


class RelationOne : public Relation {
public:
    RelationOne(int relationId, const std::string& relationName);

    std::shared_ptr<Obj> followOne(std::shared_ptr<Obj> fromObj) const;
    std::vector<std::shared_ptr<Obj>> followMany(std::shared_ptr<Obj> fromObj) const override;
    bool testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const override;
    std::string getRelationLabel() const override;
};


class RelationSelf : public RelationOne {
public:
    RelationSelf(int relationId, const std::string& relationName);

    void setReversed(std::shared_ptr<Relation> reversed) override;
    std::shared_ptr<Relation> getReverse() const override;
    std::shared_ptr<Obj> followOne(std::shared_ptr<Obj> fromObj) const override;
    std::vector<std::shared_ptr<Obj>> followMany(std::shared_ptr<Obj> fromObj) const override;
    bool testRelation(std::shared_ptr<Obj> fromObj, std::shared_ptr<Obj> toObj) const override;
};

#endif // RELATION_H
