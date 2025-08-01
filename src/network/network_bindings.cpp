#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <pybind11/functional.h>

#include "network/network_bindings.h"

// Network module includes
#include "network/neuron_definition.h"
#include "network/neuron.h"
#include "network/model.h"
#include "network/activation_definition.h"
#include "network/activation.h"
#include "network/synapse.h"
#include "network/synapse_definition.h"
#include "network/conjunctive_synapse.h"
#include "network/disjunctive_synapse.h"
#include "network/link_definition.h"
#include "network/link.h"
#include "network/direction.h"
#include "network/config.h"
#include "network/document.h"
#include "network/binding_signal.h"
#include "network/transition.h"
#include "network/bs_type.h"
#include "network/conjunctive_activation.h"
#include "network/disjunctive_activation.h"
#include "network/activation_key.h"
#include "network/fired.h"

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
        .def("getLowestDocumentId", &Model::getLowestDocumentId)
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
        .def("createDocumentId", &Model::createDocumentId)
        .def("getConfig", &Model::getConfig, py::return_value_policy::reference_internal)
        .def("setConfig", &Model::setConfig)
        .def("getTypeRegistry", &Model::getTypeRegistry, py::return_value_policy::reference_internal)
        .def("getActiveNeurons", &Model::getActiveNeurons, py::return_value_policy::reference_internal)
        .def("registerTokenId", &Model::registerTokenId)
        .def("__str__", [](const Model& m) {
            return m.toString();
        });

    // Bind ActivationDefinition class (inherits from Type)
    py::class_<ActivationDefinition, Type>(m, "ActivationType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &ActivationDefinition::getRelations, py::return_value_policy::reference_internal)
        .def("__str__", [](const ActivationDefinition& ad) {
            return ad.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &ActivationDefinition::SELF)
        .def_readonly_static("INPUT", &ActivationDefinition::INPUT)
        .def_readonly_static("OUTPUT", &ActivationDefinition::OUTPUT)
        .def_readonly_static("NEURON", &ActivationDefinition::NEURON);

    // Bind ActivationKey class
    py::class_<ActivationKey>(m, "ActivationKey")
        .def(py::init<long, int>())
        .def("getNeuronId", &ActivationKey::getNeuronId)
        .def("getActId", &ActivationKey::getActId);

    // Bind Document class (inherits from Queue, ModelProvider, QueueProvider)
    py::class_<Document, Queue>(m, "Document")
        .def(py::init<Model*>())
        .def("getId", &Document::getId)
        .def("getTimeout", &Document::getTimeout)
        .def("process", &Document::process)
        .def("getModel", &Document::getModel, py::return_value_policy::reference_internal)
        .def("getConfig", &Document::getConfig, py::return_value_policy::reference_internal)
        .def("getCurrentStep", &Document::getCurrentStep, py::return_value_policy::reference_internal)
        .def("addActivation", &Document::addActivation)
        .def("getActivations", &Document::getActivations, py::return_value_policy::reference_internal)
        .def("getActivationByNeuron", &Document::getActivationByNeuron, py::return_value_policy::reference_internal)
        .def("createActivationId", &Document::createActivationId)
        .def("disconnect", &Document::disconnect)
        .def("getQueue", &Document::getQueue, py::return_value_policy::reference_internal)
        .def("addToken", &Document::addToken, py::return_value_policy::reference_internal)
        .def("getOrCreateBindingSignal", &Document::getOrCreateBindingSignal, py::return_value_policy::reference_internal)
        .def("getBindingSignal", &Document::getBindingSignal, py::return_value_policy::reference_internal)
        .def("__str__", [](const Document& d) {
            return d.toString();
        });

    // Bind Link class (inherits from Object)
    py::class_<Link, Object>(m, "Link")
        .def(py::init<LinkDefinition*, Synapse*, Activation*, Activation*>())
        .def("getFired", &Link::getFired)
        .def("getCreated", &Link::getCreated)
        .def("getSynapse", &Link::getSynapse, py::return_value_policy::reference_internal)
        .def("setSynapse", &Link::setSynapse)
        .def("getInput", &Link::getInput, py::return_value_policy::reference_internal)
        .def("getOutput", &Link::getOutput, py::return_value_policy::reference_internal)
        .def("isCausal", py::overload_cast<>(&Link::isCausal, py::const_))
        .def_static("isCausalStatic", py::overload_cast<Activation*, Activation*>(&Link::isCausal))
        .def("getDocument", &Link::getDocument, py::return_value_policy::reference_internal)
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
        .def("hasConflictingBindingSignals", &Activation::hasConflictingBindingSignals)
        .def("isConflictingBindingSignal", &Activation::isConflictingBindingSignal)
        .def("hasNewBindingSignals", &Activation::hasNewBindingSignals)
        .def("branch", &Activation::branch, py::return_value_policy::reference_internal)
        .def("linkOutgoing", py::overload_cast<>(&Activation::linkOutgoing))
        .def("linkOutgoing", py::overload_cast<Synapse*>(&Activation::linkOutgoing))
        .def("propagate", &Activation::propagate)
        .def("collectLinkingTargets", &Activation::collectLinkingTargets, py::return_value_policy::reference_internal)
        .def("getId", &Activation::getId)
        .def("getCreated", &Activation::getCreated)
        .def("setCreated", &Activation::setCreated)
        .def("getFired", &Activation::getFired)
        .def("setFired", py::overload_cast<>(&Activation::setFired))
        .def("setFired", py::overload_cast<long>(&Activation::setFired))
        .def("updateFiredStep", &Activation::updateFiredStep)
        .def("getQueue", &Activation::getQueue, py::return_value_policy::reference_internal)
        .def("getNeuron", &Activation::getNeuron, py::return_value_policy::reference_internal)
        .def("getDocument", &Activation::getDocument, py::return_value_policy::reference_internal)
        .def("getModel", &Activation::getModel, py::return_value_policy::reference_internal)
        .def("getConfig", &Activation::getConfig, py::return_value_policy::reference_internal)
        .def("getCorrespondingInputLink", &Activation::getCorrespondingInputLink, py::return_value_policy::reference_internal)
        .def("getCorrespondingOutputLink", &Activation::getCorrespondingOutputLink, py::return_value_policy::reference_internal)
        .def("getInputLinks", py::overload_cast<LinkDefinition*>(&Activation::getInputLinks, py::const_), py::return_value_policy::reference_internal)
        .def("getOutputLinks", py::overload_cast<LinkDefinition*>(&Activation::getOutputLinks, py::const_), py::return_value_policy::reference_internal)
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

    // Bind ConjunctiveActivation (inherits from Activation)
    py::class_<ConjunctiveActivation, Activation>(m, "ConjunctiveActivation")
        .def(py::init<ActivationDefinition*, Activation*, int, Neuron*, Document*, std::map<BSType*, BindingSignal*>>())
        .def("linkIncoming", py::overload_cast<Activation*>(&ConjunctiveActivation::linkIncoming))
        .def("linkIncoming", py::overload_cast<Synapse*, Activation*>(&ConjunctiveActivation::linkIncoming))
        .def("addInputLink", &ConjunctiveActivation::addInputLink)
        .def("getInputLinks", &ConjunctiveActivation::getInputLinks, py::return_value_policy::reference_internal);

    // Bind DisjunctiveActivation (inherits from Activation)
    py::class_<DisjunctiveActivation, Activation>(m, "DisjunctiveActivation")
        .def(py::init<ActivationDefinition*, Activation*, int, Neuron*, Document*, std::map<BSType*, BindingSignal*>>())
        .def("linkIncoming", &DisjunctiveActivation::linkIncoming)
        .def("addInputLink", &DisjunctiveActivation::addInputLink)
        .def("getInputLinks", &DisjunctiveActivation::getInputLinks, py::return_value_policy::reference_internal);

    // Bind NeuronDefinition class (inherits from Type)
    py::class_<NeuronDefinition, Type>(m, "NeuronType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &NeuronDefinition::getRelations)
        .def("instantiate", &NeuronDefinition::instantiate, py::return_value_policy::reference_internal)
        .def("getActivation", &NeuronDefinition::getActivation, py::return_value_policy::reference_internal)
        .def("setActivation", &NeuronDefinition::setActivation, py::return_value_policy::reference_internal)
        .def("__str__", [](const NeuronDefinition& nd) {
            return nd.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &NeuronDefinition::SELF)
        .def_readonly_static("INPUT", &NeuronDefinition::INPUT)
        .def_readonly_static("OUTPUT", &NeuronDefinition::OUTPUT)
        .def_readonly_static("ACTIVATION", &NeuronDefinition::ACTIVATION);

    // Bind Neuron class (inherits from Object)
    py::class_<Neuron, Object>(m, "Neuron")
        .def(py::init<NeuronDefinition*, Model*, long>())
        .def(py::init<NeuronDefinition*, Model*>())
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

    // Bind LinkDefinition class (inherits from Type)
    py::class_<LinkDefinition, Type>(m, "LinkType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &LinkDefinition::getRelations, py::return_value_policy::reference_internal)
        .def("instantiate", &LinkDefinition::instantiate, py::return_value_policy::reference_internal)
        .def("getSynapse", &LinkDefinition::getSynapse, py::return_value_policy::reference_internal)
        .def("setSynapse", &LinkDefinition::setSynapse, py::return_value_policy::reference_internal)
        .def("getInput", &LinkDefinition::getInput, py::return_value_policy::reference_internal)
        .def("setInput", &LinkDefinition::setInput, py::return_value_policy::reference_internal)
        .def("getOutput", &LinkDefinition::getOutput, py::return_value_policy::reference_internal)
        .def("setOutput", &LinkDefinition::setOutput, py::return_value_policy::reference_internal)
        .def("__str__", [](const LinkDefinition& ld) {
            return ld.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &LinkDefinition::SELF)
        .def_readonly_static("INPUT", &LinkDefinition::INPUT)
        .def_readonly_static("OUTPUT", &LinkDefinition::OUTPUT)
        .def_readonly_static("SYNAPSE", &LinkDefinition::SYNAPSE)
        .def_readonly_static("PAIR_IN", &LinkDefinition::PAIR_IN)
        .def_readonly_static("PAIR_OUT", &LinkDefinition::PAIR_OUT);

    // Bind SynapseDefinition::SynapseSubType enum
    py::enum_<SynapseDefinition::SynapseSubType>(m, "SynapseSubType")
        .value("CONJUNCTIVE", SynapseDefinition::SynapseSubType::CONJUNCTIVE)
        .value("DISJUNCTIVE", SynapseDefinition::SynapseSubType::DISJUNCTIVE)
        .export_values();

    // Bind SynapseDefinition class (inherits from Type)
    py::class_<SynapseDefinition, Type>(m, "SynapseType")
        .def(py::init<TypeRegistry*, const std::string&>())
        .def("getRelations", &SynapseDefinition::getRelations)
        .def("instantiate", py::overload_cast<>(&SynapseDefinition::instantiate), py::return_value_policy::reference_internal)
        .def("instantiate", py::overload_cast<Neuron*, Neuron*>(&SynapseDefinition::instantiate), py::return_value_policy::reference_internal)
        .def("getSubType", &SynapseDefinition::getSubType)
        .def("setSubType", &SynapseDefinition::setSubType, py::return_value_policy::reference_internal)
        .def("getInput", &SynapseDefinition::getInput, py::return_value_policy::reference_internal)
        .def("setInput", &SynapseDefinition::setInput, py::return_value_policy::reference_internal)
        .def("getOutput", &SynapseDefinition::getOutput, py::return_value_policy::reference_internal)
        .def("setOutput", &SynapseDefinition::setOutput, py::return_value_policy::reference_internal)
        .def("getLink", &SynapseDefinition::getLink, py::return_value_policy::reference_internal)
        .def("setLink", &SynapseDefinition::setLink, py::return_value_policy::reference_internal)
        .def("isIncomingLinkingCandidate", &SynapseDefinition::isIncomingLinkingCandidate)
        .def("isOutgoingLinkingCandidate", &SynapseDefinition::isOutgoingLinkingCandidate)
        .def("mapTransitionForward", &SynapseDefinition::mapTransitionForward, py::return_value_policy::reference_internal)
        .def("mapTransitionBackward", &SynapseDefinition::mapTransitionBackward, py::return_value_policy::reference_internal)
        .def("getTransition", &SynapseDefinition::getTransition, py::return_value_policy::reference_internal)
        .def("setTransition", &SynapseDefinition::setTransition, py::return_value_policy::reference_internal)
        .def("getStoredAt", &SynapseDefinition::getStoredAt, py::return_value_policy::reference_internal)
        .def("setStoredAt", &SynapseDefinition::setStoredAt, py::return_value_policy::reference_internal)
        .def("setTrainingAllowed", &SynapseDefinition::setTrainingAllowed, py::return_value_policy::reference_internal)
        .def("getInstanceSynapseType", &SynapseDefinition::getInstanceSynapseType, py::return_value_policy::reference_internal)
        .def("setInstanceSynapseType", &SynapseDefinition::setInstanceSynapseType, py::return_value_policy::reference_internal)
        .def("__str__", [](const SynapseDefinition& sd) {
            return sd.toString();
        })
        // Static relation members
        .def_readonly_static("SELF", &SynapseDefinition::SELF)
        .def_readonly_static("INPUT", &SynapseDefinition::INPUT)
        .def_readonly_static("OUTPUT", &SynapseDefinition::OUTPUT)
        .def_readonly_static("LINK", &SynapseDefinition::LINK);

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
        .def("createLink", py::overload_cast<Activation*, const std::map<BSType*, BindingSignal*>&, Activation*>(&Synapse::createLink), py::return_value_policy::reference_internal)
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

    // Bind ConjunctiveSynapse (inherits from Synapse)
    py::class_<ConjunctiveSynapse, Synapse>(m, "ConjunctiveSynapse")
        .def(py::init<SynapseDefinition*>())
        .def(py::init<SynapseDefinition*, Neuron*, Neuron*>());

    // Bind DisjunctiveSynapse (inherits from Synapse)
    py::class_<DisjunctiveSynapse, Synapse>(m, "DisjunctiveSynapse")
        .def(py::init<SynapseDefinition*>())
        .def(py::init<SynapseDefinition*, Neuron*, Neuron*>())
        .def("link", &DisjunctiveSynapse::link);

    // Bind BSType base class
    py::class_<BSType>(m, "BSType")
        .def("__str__", [](const BSType& bs) {
            return std::string("BSType");
        });

    // Simple test BSType implementation for Python tests
    class TestBSType : public BSType {
    private:
        std::string name;
    public:
        TestBSType(const std::string& name) : name(name) {}
        const std::string& getName() const { return name; }
    };

    py::class_<TestBSType, BSType>(m, "TestBSType")
        .def(py::init<const std::string&>())
        .def("getName", &TestBSType::getName)
        .def("__str__", [](const TestBSType& bs) {
            return bs.getName();
        });

    // Bind BindingSignal class
    py::class_<BindingSignal>(m, "BindingSignal")
        .def(py::init<int, Document*>())
        .def("getTokenId", &BindingSignal::getTokenId)
        .def("getDocument", &BindingSignal::getDocument, py::return_value_policy::reference_internal)
        .def("addActivation", &BindingSignal::addActivation)
        .def("getActivations", py::overload_cast<Neuron*>(&BindingSignal::getActivations), py::return_value_policy::reference_internal)
        .def("getActivations", py::overload_cast<>(&BindingSignal::getActivations), py::return_value_policy::reference_internal)
        .def("__str__", [](const BindingSignal& bs) {
            return bs.toString();
        });
}