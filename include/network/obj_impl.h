#ifndef NETWORK_OBJ_IMPL_H
#define NETWORK_OBJ_IMPL_H

#include "network/element.h"
#include "network/model_provider.h"
#include "network/queue_provider.h"
#include "network/timestamp.h"
#include <string>

class ObjImpl : public Element, public ModelProvider, public QueueProvider {
public:
    virtual ~ObjImpl() = default;
    virtual Timestamp getCreated() override;
    virtual Timestamp getFired() override;
    virtual Queue* getQueue() override;
    virtual Model* getModel() override;
    virtual std::string toString() const;
    virtual std::string toKeyString() const;
};

#endif // NETWORK_OBJ_IMPL_H 