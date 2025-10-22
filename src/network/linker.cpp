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
            Activation* outputAct = nullptr;
            
            // First, try to find existing output activation with matching binding signals
            for (Activation* existingOutputAct : outputActCandidates) {
                if (matchBindingSignals(existingOutputAct, beta1)) {
                    outputAct = existingOutputAct;
                    break;
                }
            }
            
            // If no existing activation found, create a new one
            if (!outputAct) {
                outputAct = outputNeuron->createActivation(nullptr, firstInputAct->getContext(), beta1);
                outputActCandidates.insert(outputAct);  // Add to candidates for reuse
            }
            
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
    if (!act) {
        return true; // No existing activation, no conflicts possible
    }
    
    // Get existing binding signals from the activation
    std::map<int, BindingSignal*> existingBindingSignals = act->getBindingSignals();
    
    // Check for conflicts: same binding signal type but different BindingSignal objects (different tokens)
    for (const auto& [type, latentBS] : latentBindingSignals) {
        if (!latentBS) continue;
        
        auto it = existingBindingSignals.find(type);
        if (it != existingBindingSignals.end()) {
            BindingSignal* existingBS = it->second;
            if (existingBS && existingBS != latentBS) {
                // Conflict detected: same type, different BindingSignal objects (different tokens)
                return false;
            }
        }
    }
    
    return true; // No conflicts found
}

void Linker::linkIncoming(Activation* act, Activation* excludedInputAct) {
    for (auto& s : act->getNeuron()->getInputSynapsesAsStream()) {
//        if (static_cast<SynapseType*>(s->getType())->isIncomingLinkingCandidate(bsKeys)) {
            linkIncoming(act, s, excludedInputAct);
//        }
    }
}

void Linker::linkIncoming(Activation* act, Synapse* targetSyn, Activation* excludedInputAct) {
    // Get the output activation's binding signals and transition them backward through the synapse
    std::map<int, BindingSignal*> outputBindingSignals = act->getBindingSignals();
    std::map<int, BindingSignal*> inputBindingSignals = targetSyn->transitionBackward(outputBindingSignals);
    
    // Find input activations that match the transitioned binding signals
    for (auto& iAct : collectLinkingTargets(inputBindingSignals, targetSyn->getInput(act->getModel()))) {
        if (iAct != excludedInputAct) {
            // Verify no binding signal conflicts before creating the link
            std::map<int, BindingSignal*> forwardTransitioned = targetSyn->transitionForward(iAct->getBindingSignals());
            if (matchBindingSignals(act, forwardTransitioned)) {
                targetSyn->createLink(iAct, act);
            }
        }
    }
}

void Linker::linkOutgoing(Activation* act, Synapse* targetSyn) {
    // Transition the input activation's binding signals forward through the synapse
    std::map<int, BindingSignal*> inputBindingSignals = act->getBindingSignals();
    std::map<int, BindingSignal*> outputBindingSignals = targetSyn->transitionForward(inputBindingSignals);
    
    // Find output activations that match the transitioned binding signals
    std::set<Activation*> targets = collectLinkingTargets(outputBindingSignals, targetSyn->getOutput(act->getModel()));

    for (auto& targetAct : targets) {
        // Verify no binding signal conflicts before creating the link
        if (matchBindingSignals(targetAct, outputBindingSignals)) {
            targetSyn->createLink(act, targetAct);
        }
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

