#include "network/linker.h"



bool Linker::matchBindingSignals(Activation* act, std::map<int, BindingSignal*> latentBindingSignals) {
    for (const auto& e : latentBindingSignals) {
     //   if (isConflictingBindingSignal(e.first, e.second)) {
            return false;
    //    }
    }
    return true;
}

/*
bool Linker::isConflictingBindingSignal(Activation* act, int s, BindingSignal* targetBS) const {
    auto it = bindingSignals.find(s);
    BindingSignal* bs = (it != bindingSignals.end()) ? it->second : nullptr;
    return bs != nullptr && targetBS != bs;
}
*/

void Linker::linkIncoming(Activation* act, Activation* excludedInputAct) {
    for (auto& s : act->getNeuron()->getInputSynapsesAsStream()) {
        std::set<int> bsKeys;
        for (const auto& pair : act->getBindingSignals()) {
            bsKeys.insert(pair.first);
        }
//        if (static_cast<SynapseType*>(s->getType())->isIncomingLinkingCandidate(bsKeys)) {
            linkIncoming(act, s, excludedInputAct);
//        }
    }
}

void Linker::linkIncoming(Activation* act, Synapse* targetSyn, Activation* excludedInputAct) {
    for (auto& iAct : collectLinkingTargets(act, targetSyn->getInput(act->getModel()))) {
        if (iAct != excludedInputAct) {
            targetSyn->createLink(iAct, act);
        }
    }
}

void Linker::linkOutgoing(Activation* act) {
    Neuron* neuron = act->getNeuron();
    neuron->wakeupPropagable();

    for (auto& s : neuron->getOutputSynapsesAsStream()) {
        std::set<int> bindingSignalKeys;
        for (const auto& bs : act->getBindingSignals()) {
            bindingSignalKeys.insert(bs.first);
        }
//        if (static_cast<SynapseType*>(s->getType())->isOutgoingLinkingCandidate(bindingSignalKeys)) {
            linkOutgoing(act, s);
//        }
    }
}

void Linker::linkOutgoing(Activation* act, Synapse* targetSyn) {
    std::set<Activation*> targets = collectLinkingTargets(act, targetSyn->getOutput(act->getModel()));

    for (auto& targetAct : targets) {
        targetSyn->createLink(act, targetAct);
    }

    if (targets.empty() && targetSyn->isPropagable()) {
        propagate(act, targetSyn);
    }
}

void Linker::propagate(Activation* act, Synapse* targetSyn) {
    std::map<int, BindingSignal*> bindingSignals = targetSyn->transitionForward(act->getBindingSignals());
    Activation* oAct = targetSyn->getOutput(act->getModel())->createActivation(nullptr, act->getContext(), bindingSignals);

    targetSyn->createLink(act, bindingSignals, oAct);

    linkIncoming(oAct, act);
}

std::set<Activation*> Linker::collectLinkingTargets(Activation* act, Neuron* n) {
    std::set<Activation*> result;
    for (auto& bs : act->getBindingSignals()) {
        auto activations = bs.second->getActivations(n);
        result.insert(activations.begin(), activations.end());
    }
    return result;
}
