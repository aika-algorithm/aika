#include "network/linker.h"


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

void Linker::linkLatent(Activation* firstInputAct) {
    if (!firstInputAct) {
        throw std::invalid_argument("linkLatent: firstInputAct cannot be null");
    }
    
    Model* model = firstInputAct->getModel();
    Neuron* firstInputNeuron = firstInputAct->getNeuron();
    firstInputNeuron->wakeupPropagable();

    const std::map<int, BindingSignal*>& a1BS = firstInputAct->getBindingSignals();

    // Traverse each outgoing synapse of the first input neuron.
    for (Synapse* firstSynapse : firstInputNeuron->getOutputSynapsesAsStream()) {
        if (!firstSynapse) continue;

        // Forward transition (beta1)
        std::map<int, BindingSignal*> beta1 = firstSynapse->transitionForward(a1BS);
        if (beta1.empty())
            continue; // no forward-admissible signals

        Neuron* outputNeuron = firstSynapse->getOutput(model);

        // Output activation candidates given the forward BS view.
        std::set<Activation*> outputActCandidates = collectLinkingTargets(beta1, outputNeuron);

        // Paired synapse for the dot-product inner multiplication.
        Synapse* secondSynapse = firstSynapse->getPairedSynapse();
        if (!secondSynapse) {
            // If your neuron supports disjunctive single-leg commits, you could
            // optionally realize an output link here using only (a1, s1).
            continue;
        }

        Neuron* secondInputNeuron = secondSynapse->getInput(model);

        // Backward transition over the paired synapse (beta2)
        std::map<int, BindingSignal*> beta2 = secondSynapse->transitionBackward(beta1);

        // Find partner input activations a2 at src(s2) matching beta2.
        std::set<Activation*> secondInputCandidates = collectLinkingTargets(beta2, secondInputNeuron);
        if (secondInputCandidates.empty()) {
            // No partner yet; higher layers may register wakeups.
            continue;
        }

        // Pairwise commit for each admissible a2 (avoid degenerate a1==a2 case).
        for (Activation* secondInputAct : secondInputCandidates) {
            if (!secondInputAct || secondInputAct == firstInputAct) continue;

            // Select or realize an output activation compatible with beta1.
            Activation* outputAct = nullptr; // TODO: Implement selectOrRealizeOutputActivation
            if (!outputAct)
                continue;

            // Avoid duplicate links if already present.
            bool l1Exists = firstSynapse->hasLink(firstInputAct, outputAct);
            bool l2Exists = secondSynapse->hasLink(secondInputAct, outputAct);

            if (!l1Exists) {
                firstSynapse->createLink(firstInputAct, outputAct);
            }
            if (!l2Exists) {
                secondSynapse->createLink(secondInputAct, outputAct);
            }

            // Update candidate set to include the selected materialized output (helps reuse).
            outputActCandidates.insert(outputAct);
        }
    }
}

std::set<Activation*> Linker::collectLinkingTargets(std::map<int, BindingSignal*> bindingSignals, Neuron* n) {
    std::set<Activation*> result;
    for (const auto& [ch, bs] : bindingSignals) {
        if (!bs) continue;
        auto acts = bs->getActivations(n);
        result.insert(acts.begin(), acts.end());
    }
    return result;
}

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
    for (auto& iAct : collectLinkingTargets(act->getBindingSignals(), targetSyn->getInput(act->getModel()))) {
        if (iAct != excludedInputAct) {
            targetSyn->createLink(iAct, act);
        }
    }
}

void Linker::linkOutgoing(Activation* act, Synapse* targetSyn) {
    std::set<Activation*> targets = collectLinkingTargets(act->getBindingSignals(), targetSyn->getOutput(act->getModel()));

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

