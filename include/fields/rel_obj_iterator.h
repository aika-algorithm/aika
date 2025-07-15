#ifndef REL_OBJ_ITERATOR_H
#define REL_OBJ_ITERATOR_H

#include <vector>

class Object;

class RelatedObjectIterator {
public:
    virtual ~RelatedObjectIterator() = default;

    virtual bool hasNext() = 0;

    virtual Object* next() = 0;
};

class SingleObjectIterator : public RelatedObjectIterator {
    Object* _obj;

public:
    SingleObjectIterator(Object* obj) : _obj(obj) {}

    ~SingleObjectIterator() = default;

    bool hasNext() {
        return _obj != nullptr;
    }

    Object* next() {
        auto obj = _obj;
        _obj = nullptr;
        return obj;
    }
};

class VectorObjectIterator : public RelatedObjectIterator {
    std::vector<Object*> _objects;
    size_t _currentIndex;

public:
    VectorObjectIterator(const std::vector<Object*>& objects)
        : _objects(objects), _currentIndex(0) {}

    ~VectorObjectIterator() = default;

    bool hasNext() override {
        return _currentIndex < _objects.size();
    }

    Object* next() override {
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

    Object& operator*() {
        return baseIt->second;
    }

    Object* operator->() {
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
    Object* _obj;

public:
    SingleObjectIterable(Object* obj) : _obj(obj) {}

    SingleObjectIterator* iterator() const override {
        return new SingleObjectIterator(_obj);
    }
};

class VectorObjectIterable : public RelatedObjectIterable {
    std::vector<Object*> _objects;

public:
    VectorObjectIterable(const std::vector<Object*>& objects) : _objects(objects) {}

    VectorObjectIterator* iterator() const override {
        return new VectorObjectIterator(_objects);
    }
};

#endif //REL_OBJ_ITERATOR_H
