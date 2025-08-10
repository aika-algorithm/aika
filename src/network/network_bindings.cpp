#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <pybind11/functional.h>

#include "network/network_bindings.h"

// Network module includes
#include "network/neuron_type.h"
#include "network/neuron.h"
#include "network/model.h"
#include "network/activation_type.h"
#include "network/activation.h"
#include "network/synapse.h"
#include "network/synapse_type.h"
#include "network/link_type.h"
#include "network/link.h"
#include "network/direction.h"
#include "network/config.h"
#include "network/context.h"
#include "network/binding_signal.h"
#include "network/transition.h"
#include "network/activation_key.h"
#include "network/fired.h"

// Builder classes
#include "network/builders/neuron_type_builder.h"
#include "network/builders/synapse_type_builder.h"
#include "network/builders/activation_type_builder.h"
#include "network/builders/link_type_builder.h"

// Fields module includes (for base classes)
#include "fields/type.h"
#include "fields/object.h"
#include "fields/type_registry.h"
#include "fields/relation.h"

namespace py = pybind11;

void bind_network(py::module_& m) {
    // Bind Config class
    py::class_<Config>(m, "Config")
        .def(py::init<>())
        .def("__str__", [](const Config& c) {
            return c.toString();
        });

    // Bind Model class (inherits from Queue)
    py::class_<Model>(m, "Model")
        .def(py::init<TypeRegistry*>())
        .def("createNeuronId", &Model::createNeuronId)
        .def("getLowestContextId", &Model::getLowestContextId)
        .def("addToN", &Model::addToN)
        .def("getN", &Model::getN)
        .def("setN", &Model::setN)
        .def("getTimeout", &Model::getTimeout)
        .def("canBeSuspended", &Model::canBeSuspended)
        .def("getNeuron", &Model::getNeuron, py::return_value_policy::reference_internal)
        .def("registerNeuron", &Model::registerNeuron)
        .def("unregister", &Model::unregister)
        .def("open", &Model::open)
        .def("close", &Model::close)
        .def("createContextId", &Model::createContextId)
        .def("getConfig", &Model::getConfig, py::return_value_policy::reference_internal)
        .def("setConfig", &Model::setConfig)
        .def("getTypeRegistry", &Model::getTypeRegistry, py::return_value_policy::reference_internal)
        .def("getActiveNeurons", &Model::getActiveNeurons, py::return_value_policy::reference_internal)
        .def("registerTokenId", &Model::registerTokenId)
        .def("__str__", [](const Model& m) {
            return m.toString();
        });

    // Bind ActivationType class (inherits from Type)
    py::class_<ActivationType, Type>(m, "ActivationType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &ActivationType::getRelations, py::return_value_policy::reference_internal)
        .def("__str__", [](const ActivationType& ad) {
            return ad.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &ActivationType::SELF)
        .def_readonly_static("INPUT", &ActivationType::INPUT)
        .def_readonly_static("OUTPUT", &ActivationType::OUTPUT)
        .def_readonly_static("NEURON", &ActivationType::NEURON);

    // Bind ActivationKey class
    py::class_<ActivationKey>(m, "ActivationKey")
        .def(py::init<long, int>())
        .def("getNeuronId", &ActivationKey::getNeuronId)
        .def("getActId", &ActivationKey::getActId);

    // Bind Context class (inherits from Queue, ModelProvider, QueueProvider)
    py::class_<Context, Queue>(m, "Context")
        .def(py::init<Model*>())
        .def("getId", &Context::getId)
        .def("getTimeout", &Context::getTimeout)
        .def("process", &Context::process)
        .def("getModel", &Context::getModel, py::return_value_policy::reference_internal)
        .def("getConfig", &Context::getConfig, py::return_value_policy::reference_internal)
        .def("getCurrentStep", &Context::getCurrentStep, py::return_value_policy::reference_internal)
        .def("addActivation", &Context::addActivation)
        .def("getActivations", &Context::getActivations, py::return_value_policy::reference_internal)
        .def("getActivationByNeuron", &Context::getActivationByNeuron, py::return_value_policy::reference_internal)
        .def("createActivationId", &Context::createActivationId)
        .def("disconnect", &Context::disconnect)
        .def("getQueue", &Context::getQueue, py::return_value_policy::reference_internal)
        .def("addToken", &Context::addToken, py::return_value_policy::reference_internal)
        .def("getOrCreateBindingSignal", &Context::getOrCreateBindingSignal, py::return_value_policy::reference_internal)
        .def("getBindingSignal", &Context::getBindingSignal, py::return_value_policy::reference_internal)
        .def("__str__", [](const Context& d) {
            return d.toString();
        });

    // Bind Link class (inherits from Object)
    py::class_<Link, Object>(m, "Link")
        .def(py::init<LinkType*, Synapse*, Activation*, Activation*>())
        .def("getFired", &Link::getFired)
        .def("getCreated", &Link::getCreated)
        .def("getSynapse", &Link::getSynapse, py::return_value_policy::reference_internal)
        .def("setSynapse", &Link::setSynapse)
        .def("getInput", &Link::getInput, py::return_value_policy::reference_internal)
        .def("getOutput", &Link::getOutput, py::return_value_policy::reference_internal)
        .def("isCausal", py::overload_cast<>(&Link::isCausal, py::const_))
        .def_static("isCausalStatic", py::overload_cast<Activation*, Activation*>(&Link::isCausal))
        .def("getContext", &Link::getContext, py::return_value_policy::reference_internal)
        .def("getQueue", &Link::getQueue, py::return_value_policy::reference_internal)
        .def("getModel", &Link::getModel, py::return_value_policy::reference_internal)
        .def("getConfig", &Link::getConfig, py::return_value_policy::reference_internal)
        .def("getInputKeyString", &Link::getInputKeyString)
        .def("getOutputKeyString", &Link::getOutputKeyString)
        .def("toKeyString", &Link::toKeyString)
        .def("__str__", [](const Link& l) {
            return l.toString();
        });

    // Bind Activation base class (inherits from Object)
    py::class_<Activation, Object>(m, "Activation")
        .def("getKey", &Activation::getKey)
        .def("getParent", &Activation::getParent, py::return_value_policy::reference_internal)
        .def("addOutputLink", &Activation::addOutputLink)
        .def("getBindingSignal", &Activation::getBindingSignal, py::return_value_policy::reference_internal)
        .def("getBindingSignals", &Activation::getBindingSignals, py::return_value_policy::reference_internal)
        .def("getId", &Activation::getId)
        .def("getCreated", &Activation::getCreated)
        .def("setCreated", &Activation::setCreated)
        .def("getFired", &Activation::getFired)
        .def("setFired", py::overload_cast<>(&Activation::setFired))
        .def("setFired", py::overload_cast<long>(&Activation::setFired))
        .def("updateFiredStep", &Activation::updateFiredStep)
        .def("getQueue", &Activation::getQueue, py::return_value_policy::reference_internal)
        .def("getNeuron", &Activation::getNeuron, py::return_value_policy::reference_internal)
        .def("getContext", &Activation::getContext, py::return_value_policy::reference_internal)
        .def("getModel", &Activation::getModel, py::return_value_policy::reference_internal)
        .def("getConfig", &Activation::getConfig, py::return_value_policy::reference_internal)
        .def("getInputLinks", py::overload_cast<LinkType*>(&Activation::getInputLinks, py::const_), py::return_value_policy::reference_internal)
        .def("getOutputLinks", py::overload_cast<LinkType*>(&Activation::getOutputLinks, py::const_), py::return_value_policy::reference_internal)
        .def("getOutputLinks", py::overload_cast<>(&Activation::getOutputLinks, py::const_), py::return_value_policy::reference_internal)
        .def("getOutputLink", &Activation::getOutputLink, py::return_value_policy::reference_internal)
        .def("getOutputLinks", py::overload_cast<Synapse*>(&Activation::getOutputLinks, py::const_), py::return_value_policy::reference_internal)
        .def("compareTo", &Activation::compareTo)
        .def("equals", &Activation::equals)
        .def("hashCode", &Activation::hashCode)
        .def("toKeyString", &Activation::toKeyString)
        .def("__str__", [](const Activation& a) {
            return a.toString();
        })
        .def_readonly_static("ID_COMPARATOR", &Activation::ID_COMPARATOR);

    // Bind NeuronType class (inherits from Type)
    py::class_<NeuronType, Type>(m, "NeuronType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &NeuronType::getRelations)
        .def("instantiate", &NeuronType::instantiate, py::return_value_policy::reference_internal)
        .def("getActivationType", &NeuronType::getActivationType, py::return_value_policy::reference_internal)
        .def("setActivationType", &NeuronType::setActivationType, py::return_value_policy::reference_internal)
        .def("__str__", [](const NeuronType& nd) {
            return nd.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &NeuronType::SELF)
        .def_readonly_static("INPUT", &NeuronType::INPUT)
        .def_readonly_static("OUTPUT", &NeuronType::OUTPUT)
        .def_readonly_static("ACTIVATION", &NeuronType::ACTIVATION);

    // Bind Neuron class (inherits from Object)
    py::class_<Neuron, Object>(m, "Neuron")
        .def(py::init<NeuronType*, Model*, long>())
        .def(py::init<NeuronType*, Model*>())
        .def("getId", &Neuron::getId)
        .def("updatePropagable", &Neuron::updatePropagable)
        .def("addPropagable", &Neuron::addPropagable)
        .def("removePropagable", &Neuron::removePropagable)
        .def("wakeupPropagable", &Neuron::wakeupPropagable)
        .def("getPropagable", &Neuron::getPropagable, py::return_value_policy::reference_internal)
        .def("getNewSynapseId", &Neuron::getNewSynapseId)
        .def("deleteNeuron", &Neuron::deleteNeuron)
        .def("getModel", &Neuron::getModel, py::return_value_policy::reference_internal)
        .def("getConfig", &Neuron::getConfig, py::return_value_policy::reference_internal)
        .def("setModified", &Neuron::setModified)
        .def("resetModified", &Neuron::resetModified)
        .def("isModified", &Neuron::isModified)
        .def("getSynapseBySynId", &Neuron::getSynapseBySynId, py::return_value_policy::reference_internal)
        .def("addInputSynapse", &Neuron::addInputSynapse)
        .def("removeInputSynapse", &Neuron::removeInputSynapse)
        .def("addOutputSynapse", &Neuron::addOutputSynapse)
        .def("removeOutputSynapse", &Neuron::removeOutputSynapse)
        .def("getInputSynapses", &Neuron::getInputSynapses, py::return_value_policy::reference_internal)
        .def("getOutputSynapses", &Neuron::getOutputSynapses, py::return_value_policy::reference_internal)
        .def("getInputSynapsesAsStream", &Neuron::getInputSynapsesAsStream, py::return_value_policy::reference_internal)
        .def("getOutputSynapsesAsStream", &Neuron::getOutputSynapsesAsStream, py::return_value_policy::reference_internal)
        .def("getOutputSynapse", &Neuron::getOutputSynapse, py::return_value_policy::reference_internal)
        .def("getInputSynapsesStoredAtOutputSide", &Neuron::getInputSynapsesStoredAtOutputSide, py::return_value_policy::reference_internal)
        .def("getOutputSynapsesStoredAtInputSide", &Neuron::getOutputSynapsesStoredAtInputSide, py::return_value_policy::reference_internal)
        .def("getInputSynapse", &Neuron::getInputSynapse, py::return_value_policy::reference_internal)
        .def("getInputSynapseByType", &Neuron::getInputSynapseByType, py::return_value_policy::reference_internal)
        .def("getInputSynapsesByType", &Neuron::getInputSynapsesByType, py::return_value_policy::reference_internal)
        .def("getOutputSynapseByType", &Neuron::getOutputSynapseByType, py::return_value_policy::reference_internal)
        .def("getOutputSynapsesByType", &Neuron::getOutputSynapsesByType, py::return_value_policy::reference_internal)
        .def("getCreated", &Neuron::getCreated)
        .def("getFired", &Neuron::getFired)
        .def("getQueue", &Neuron::getQueue, py::return_value_policy::reference_internal)
        .def("getRefCount", &Neuron::getRefCount)
        .def("isReferenced", &Neuron::isReferenced)
        .def("getLastUsed", &Neuron::getLastUsed)
        .def("updateLastUsed", &Neuron::updateLastUsed)
        .def("save", &Neuron::save)
        .def("toKeyString", &Neuron::toKeyString)
        .def("__str__", [](const Neuron& n) {
            return n.toString();
        })
        .def("__eq__", [](const Neuron& a, const Neuron& b) {
            return a == b;
        })
        .def("__ne__", [](const Neuron& a, const Neuron& b) {
            return a != b;
        });

    // Bind NetworkDirection
    py::class_<NetworkDirection>(m, "NetworkDirection")
        .def("invert", &NetworkDirection::invert, py::return_value_policy::reference_internal)
        .def("getNeuron", &NetworkDirection::getNeuron, py::return_value_policy::reference_internal)
        .def("getActivation", &NetworkDirection::getActivation, py::return_value_policy::reference_internal)
        .def("getOrder", &NetworkDirection::getOrder)
        .def("transition", &NetworkDirection::transition, py::return_value_policy::reference_internal)
        .def_readonly_static("INPUT", &NetworkDirection::INPUT)
        .def_readonly_static("OUTPUT", &NetworkDirection::OUTPUT);

    // Bind LinkType class (inherits from Type)
    py::class_<LinkType, Type>(m, "LinkType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &LinkType::getRelations, py::return_value_policy::reference_internal)
        .def("instantiate", &LinkType::instantiate, py::return_value_policy::reference_internal)
        .def("getSynapseType", &LinkType::getSynapseType, py::return_value_policy::reference_internal)
        .def("setSynapseType", &LinkType::setSynapseType, py::return_value_policy::reference_internal)
        .def("getInputType", &LinkType::getInputType, py::return_value_policy::reference_internal)
        .def("setInputType", &LinkType::setInputType, py::return_value_policy::reference_internal)
        .def("getOutputType", &LinkType::getOutputType, py::return_value_policy::reference_internal)
        .def("setOutputType", &LinkType::setOutputType, py::return_value_policy::reference_internal)
        .def("__str__", [](const LinkType& ld) {
            return ld.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &LinkType::SELF)
        .def_readonly_static("INPUT", &LinkType::INPUT)
        .def_readonly_static("OUTPUT", &LinkType::OUTPUT)
        .def_readonly_static("SYNAPSE", &LinkType::SYNAPSE)
        .def_readonly_static("PAIR_IN", &LinkType::PAIR_IN)
        .def_readonly_static("PAIR_OUT", &LinkType::PAIR_OUT);

    // Bind SynapseType class (inherits from Type)
    py::class_<SynapseType, Type>(m, "SynapseType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &SynapseType::getRelations)
        .def("instantiate", py::overload_cast<>(&SynapseType::instantiate), py::return_value_policy::reference_internal)
        .def("instantiate", py::overload_cast<Neuron*, Neuron*>(&SynapseType::instantiate), py::return_value_policy::reference_internal)
        .def("getInputType", &SynapseType::getInputType, py::return_value_policy::reference_internal)
        .def("setInputType", &SynapseType::setInputType, py::return_value_policy::reference_internal)
        .def("getOutputType", &SynapseType::getOutputType, py::return_value_policy::reference_internal)
        .def("setOutputType", &SynapseType::setOutputType, py::return_value_policy::reference_internal)
        .def("getLinkType", &SynapseType::getLinkType, py::return_value_policy::reference_internal)
        .def("setLinkType", &SynapseType::setLinkType, py::return_value_policy::reference_internal)
        .def("getTransitions", &SynapseType::getTransitions, py::return_value_policy::reference_internal)
        .def("setTransitions", &SynapseType::setTransitions, py::return_value_policy::reference_internal)
        .def("getStoredAt", &SynapseType::getStoredAt, py::return_value_policy::reference_internal)
        .def("setStoredAt", &SynapseType::setStoredAt, py::return_value_policy::reference_internal)
        .def("getInstanceSynapseType", &SynapseType::getInstanceSynapseType, py::return_value_policy::reference_internal)
        .def("setInstanceSynapseType", &SynapseType::setInstanceSynapseType, py::return_value_policy::reference_internal)
        .def("__str__", [](const SynapseType& sd) {
            return sd.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &SynapseType::SELF)
        .def_readonly_static("INPUT", &SynapseType::INPUT)
        .def_readonly_static("OUTPUT", &SynapseType::OUTPUT)
        .def_readonly_static("LINK", &SynapseType::LINK);

    // Bind Synapse base class (inherits from Object)
    py::class_<Synapse, Object>(m, "Synapse")
        .def("getSynapseId", &Synapse::getSynapseId)
        .def("setSynapseId", &Synapse::setSynapseId)
        .def("transitionForward", &Synapse::transitionForward)
        .def("setPropagable", &Synapse::setPropagable, py::return_value_policy::reference_internal)
        .def("isPropagable", &Synapse::isPropagable)
        .def("setModified", &Synapse::setModified)
        .def("setInput", &Synapse::setInput)
        .def("setOutput", &Synapse::setOutput)
        .def("link", py::overload_cast<Model*, Neuron*, Neuron*>(&Synapse::link), py::return_value_policy::reference_internal)
        .def("link", py::overload_cast<Model*>(&Synapse::link))
        .def("unlinkInput", &Synapse::unlinkInput)
        .def("unlinkOutput", &Synapse::unlinkOutput)
        .def("createLink", py::overload_cast<Activation*, Activation*>(&Synapse::createLink), py::return_value_policy::reference_internal)
        .def("createLink", py::overload_cast<Activation*, const std::map<int, BindingSignal*>&, Activation*>(&Synapse::createLink), py::return_value_policy::reference_internal)
        .def("getStoredAt", &Synapse::getStoredAt, py::return_value_policy::reference_internal)
        .def("getInputRef", &Synapse::getInputRef, py::return_value_policy::reference_internal)
        .def("getOutputRef", &Synapse::getOutputRef, py::return_value_policy::reference_internal)
        .def("getInput", py::overload_cast<>(&Synapse::getInput, py::const_), py::return_value_policy::reference_internal)
        .def("getInput", py::overload_cast<Model*>(&Synapse::getInput, py::const_), py::return_value_policy::reference_internal)
        .def("getOutput", py::overload_cast<>(&Synapse::getOutput, py::const_), py::return_value_policy::reference_internal)
        .def("getOutput", py::overload_cast<Model*>(&Synapse::getOutput, py::const_), py::return_value_policy::reference_internal)
        .def("getCreated", &Synapse::getCreated)
        .def("getFired", &Synapse::getFired)
        .def("deleteSynapse", &Synapse::deleteSynapse)
        .def("getQueue", &Synapse::getQueue, py::return_value_policy::reference_internal)
        .def("toKeyString", &Synapse::toKeyString)
        .def("__str__", [](const Synapse& s) {
            return s.toString();
        });

    // Bind BindingSignal class
    py::class_<BindingSignal>(m, "BindingSignal")
        .def(py::init<int, Context*>())
        .def("getTokenId", &BindingSignal::getTokenId)
        .def("getContext", &BindingSignal::getContext, py::return_value_policy::reference_internal)
        .def("addActivation", &BindingSignal::addActivation)
        .def("getActivations", py::overload_cast<Neuron*>(&BindingSignal::getActivations), py::return_value_policy::reference_internal)
        .def("getActivations", py::overload_cast<>(&BindingSignal::getActivations), py::return_value_policy::reference_internal)
        .def("__str__", [](const BindingSignal& bs) {
            return bs.toString();
        });

    // Bind Builder classes
    py::class_<NeuronTypeBuilder>(m, "NeuronTypeBuilder")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("setActivation", &NeuronTypeBuilder::setActivation, py::return_value_policy::reference_internal)
        .def("getActivation", &NeuronTypeBuilder::getActivation, py::return_value_policy::reference_internal)
        .def("build", &NeuronTypeBuilder::build, py::return_value_policy::reference_internal)
        .def("getRelations", &NeuronTypeBuilder::getRelations, py::return_value_policy::reference_internal)
        .def("__str__", [](const NeuronTypeBuilder& nb) {
            return nb.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &NeuronTypeBuilder::SELF)
        .def_readonly_static("INPUT", &NeuronTypeBuilder::INPUT)
        .def_readonly_static("OUTPUT", &NeuronTypeBuilder::OUTPUT)
        .def_readonly_static("ACTIVATION", &NeuronTypeBuilder::ACTIVATION);

    py::class_<SynapseTypeBuilder>(m, "SynapseTypeBuilder")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("setInput", &SynapseTypeBuilder::setInput, py::return_value_policy::reference_internal)
        .def("setOutput", &SynapseTypeBuilder::setOutput, py::return_value_policy::reference_internal)
        .def("setLink", &SynapseTypeBuilder::setLink, py::return_value_policy::reference_internal)
        .def("getInput", &SynapseTypeBuilder::getInput, py::return_value_policy::reference_internal)
        .def("getOutput", &SynapseTypeBuilder::getOutput, py::return_value_policy::reference_internal)
        .def("getLink", &SynapseTypeBuilder::getLink, py::return_value_policy::reference_internal)
        .def("build", &SynapseTypeBuilder::build, py::return_value_policy::reference_internal)
        .def("getRelations", &SynapseTypeBuilder::getRelations, py::return_value_policy::reference_internal)
        .def("__str__", [](const SynapseTypeBuilder& sb) {
            return sb.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &SynapseTypeBuilder::SELF)
        .def_readonly_static("INPUT", &SynapseTypeBuilder::INPUT)
        .def_readonly_static("OUTPUT", &SynapseTypeBuilder::OUTPUT)
        .def_readonly_static("LINK", &SynapseTypeBuilder::LINK);

    py::class_<ActivationTypeBuilder>(m, "ActivationTypeBuilder")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("setNeuron", &ActivationTypeBuilder::setNeuron, py::return_value_policy::reference_internal)
        .def("getNeuron", &ActivationTypeBuilder::getNeuron, py::return_value_policy::reference_internal)
        .def("build", &ActivationTypeBuilder::build, py::return_value_policy::reference_internal)
        .def("getRelations", &ActivationTypeBuilder::getRelations, py::return_value_policy::reference_internal)
        .def("__str__", [](const ActivationTypeBuilder& ab) {
            return ab.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &ActivationTypeBuilder::SELF)
        .def_readonly_static("INPUT", &ActivationTypeBuilder::INPUT)
        .def_readonly_static("OUTPUT", &ActivationTypeBuilder::OUTPUT)
        .def_readonly_static("NEURON", &ActivationTypeBuilder::NEURON);

    py::class_<LinkTypeBuilder>(m, "LinkTypeBuilder")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("setSynapse", &LinkTypeBuilder::setSynapse, py::return_value_policy::reference_internal)
        .def("setInput", &LinkTypeBuilder::setInput, py::return_value_policy::reference_internal)
        .def("setOutput", &LinkTypeBuilder::setOutput, py::return_value_policy::reference_internal)
        .def("getSynapse", &LinkTypeBuilder::getSynapse, py::return_value_policy::reference_internal)
        .def("getInput", &LinkTypeBuilder::getInput, py::return_value_policy::reference_internal)
        .def("getOutput", &LinkTypeBuilder::getOutput, py::return_value_policy::reference_internal)
        .def("build", &LinkTypeBuilder::build, py::return_value_policy::reference_internal)
        .def("getRelations", &LinkTypeBuilder::getRelations, py::return_value_policy::reference_internal)
        .def("__str__", [](const LinkTypeBuilder& lb) {
            return lb.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &LinkTypeBuilder::SELF)
        .def_readonly_static("INPUT", &LinkTypeBuilder::INPUT)
        .def_readonly_static("OUTPUT", &LinkTypeBuilder::OUTPUT)
        .def_readonly_static("SYNAPSE", &LinkTypeBuilder::SYNAPSE)
        .def_readonly_static("PAIR_IN", &LinkTypeBuilder::PAIR_IN)
        .def_readonly_static("PAIR_OUT", &LinkTypeBuilder::PAIR_OUT);
}