#ifndef REL_OBJ_ITERATOR_H
#define REL_OBJ_ITERATOR_H

class Obj;

class RelatedObjectIterator {
public:
    virtual ~RelatedObjectIterator() = default;

    virtual bool hasNext() = 0;

    virtual Obj* next() = 0;
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

#endif //REL_OBJ_ITERATOR_H
