#ifndef REL_OBJ_ITERATOR_H
#define REL_OBJ_ITERATOR_H

#include <vector>

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

class VectorObjectIterator : public RelatedObjectIterator {
    std::vector<Obj*> _objects;
    size_t _currentIndex;

public:
    VectorObjectIterator(const std::vector<Obj*>& objects) 
        : _objects(objects), _currentIndex(0) {}

    ~VectorObjectIterator() = default;

    bool hasNext() override {
        return _currentIndex < _objects.size();
    }

    Obj* next() override {
        if (!hasNext()) {
            return nullptr;
        }
        return _objects[_currentIndex++];
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

    SingleObjectIterator* iterator() const override {
        return new SingleObjectIterator(_obj);
    }
};

class VectorObjectIterable : public RelatedObjectIterable {
    std::vector<Obj*> _objects;

public:
    VectorObjectIterable(const std::vector<Obj*>& objects) : _objects(objects) {}

    VectorObjectIterator* iterator() const override {
        return new VectorObjectIterator(_objects);
    }
};

#endif //REL_OBJ_ITERATOR_H
