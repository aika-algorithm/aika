#ifndef REL_OBJ_ITERATOR_H
#define REL_OBJ_ITERATOR_H

class Obj;

class RelatedObjectIterator {
public:
    virtual ~RelatedObjectIterator() = default;

    virtual bool hasNext() = 0;

    virtual Obj* next() = 0;
};

class SingleObjectIterator : public RelatedObjectIterator {
    Obj* _obj;

public:
    SingleObjectIterator(Obj* obj) : _obj(obj) {}

    ~SingleObjectIterator() = default;

    bool hasNext() {
        return _obj != nullptr;
    }

    Obj* next() {
        auto obj = _obj;
        _obj = nullptr;
        return obj;
    }
};


template <typename MapType>
class MapRelObjIterator : public RelatedObjectIterator {
public:
    using BaseIterator = typename MapType::const_iterator;

    MapRelObjIterator(BaseIterator base) : baseIt(base) {}

    MapRelObjIterator& operator++() {
        ++baseIt;
        return *this;
    }

    Obj& operator*() {
        return baseIt->second;
    }

    Obj* operator->() {
        return &baseIt->second;
    }

    bool operator==(const MapRelObjIterator& other) const {
        return baseIt == other.baseIt;
    }

    bool operator!=(const MapRelObjIterator& other) const {
        return baseIt != other.baseIt;
    }

private:
    BaseIterator baseIt;
};


class RelatedObjectIterable {
public:
    virtual ~RelatedObjectIterable() = default;

    virtual RelatedObjectIterator* iterator() const = 0;

};

class SingleObjectIterable : public RelatedObjectIterable {
    Obj* _obj;

public:
    SingleObjectIterable(Obj* obj) : _obj(obj) {}

    SingleObjectIterator* iterator() const {
        return new SingleObjectIterator(_obj);
    }
};

#endif //REL_OBJ_ITERATOR_H
