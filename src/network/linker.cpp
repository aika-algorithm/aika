#include "network/linker.h"



void Linker::linkOutgoing(Activation* act) {
    Neuron* neuron = act->getNeuron();
    neuron->wakeupPropagable();

    for (auto& s : neuron->getOutputSynapsesAsStream()) {
        linkOutgoing(act, s);
    }
}

void Linker::linkOutgoing(Activation* act, Synapse* outputSyn) {
    // Transition the input activation's binding signals forward through the synapse
    std::map<int, BindingSignal*> inputBindingSignals = act->getBindingSignals();
    std::map<int, BindingSignal*> outputBindingSignals = outputSyn->transitionForward(inputBindingSignals);

    // Check if the synapse type has allowLatentLinking enabled
    SynapseType* synapseType = static_cast<SynapseType*>(outputSyn->getType());

    if (synapseType && synapseType->getAllowLatentLinking()) {
        // Use pair linking for synapses with allowLatentLinking enabled
        // This is needed for paired synapses like dot-product synapses
        pairLinking(act, outputSyn, outputBindingSignals);
    } else {
        // Find output activations that match the transitioned binding signals
        std::set<Activation*> outputActs = collectLinkingTargets(outputBindingSignals, outputSyn->getOutput(act->getModel()));

        for (auto& outputAct : outputActs) {
            // Verify no binding signal conflicts before creating the link
            if (matchBindingSignals(outputAct, outputBindingSignals)) {
                outputSyn->createLink(act, outputAct);
            }
        }

        if (outputActs.empty() && outputSyn->isPropagable()) {
            propagate(act, outputSyn, outputBindingSignals);
        }
    }
}

void Linker::propagate(Activation* act, Synapse* targetSyn, const std::map<int, BindingSignal*>& outputBindingSignals) {
    // Use normal propagation for regular synapses
    Activation* oAct = targetSyn->getOutput(act->getModel())->createActivation(nullptr, act->getContext(), outputBindingSignals);

    targetSyn->createLink(act, oAct);

    linkIncoming(oAct, act);
}


// The purpose of PairLinking is just to optimize the linking of paired InputSynapses. The functionality should be identical
// to the propagate + linkIncoming calls. It just avoids actually instantiating the output activation if one of the
// two input activations is missing or is not yet fired.
// PairLinking avoids having to deal with incomplete Activations where some of the Binding-Signals are still missing.
void Linker::pairLinking(Activation* firstInputAct, Synapse* firstSynapse, const std::map<int, BindingSignal*>& outputBindingSignals) {
    if (!firstInputAct) {
        throw std::invalid_argument("linkLatent: firstInputAct cannot be null");
    }
    
    Model* model = firstInputAct->getModel();

    Neuron* outputNeuron = firstSynapse->getOutput(model);

    // Output activation candidates given the forward BS view.
    std::set<Activation*> outputActCandidates = collectLinkingTargets(outputBindingSignals, outputNeuron);

    // Paired synapse for the dot-product inner multiplication.
    Synapse* secondSynapse = firstSynapse->getPairedSynapse();
    if (!secondSynapse) {
        // If your neuron supports disjunctive single-leg commits, you could
        // optionally realize an output link here using only (a1, s1).
        return;
    }
    Neuron* secondInputNeuron = secondSynapse->getInput(model);

    // Backward transition over the paired synapse (secondaryInputBindingSignals)
    std::map<int, BindingSignal*> secondaryInputBindingSignals = secondSynapse->transitionBackward(outputBindingSignals);

    // Find partner input activations a2 at src(s2) matching beta2.
    std::set<Activation*> secondInputCandidates = collectLinkingTargets(secondaryInputBindingSignals, secondInputNeuron);
    if (secondInputCandidates.empty()) {
        // No partner yet; higher layers may register wakeups.
        return;
    }

    // Pairwise commit for each admissible a2 (avoid degenerate a1==a2 case).
    for (Activation* secondInputAct : secondInputCandidates) {
        if (!secondInputAct || secondInputAct == firstInputAct) continue;

        // Select or realize an output activation compatible with beta1.
        Activation* outputAct = nullptr;
            
        // First, try to find existing output activation with matching binding signals
        for (Activation* existingOutputAct : outputActCandidates) {
            if (matchBindingSignals(existingOutputAct, outputBindingSignals)) {
                outputAct = existingOutputAct;
                break;
            }
        }
            
        // If no existing activation found, create a new one
        if (!outputAct) {
            outputAct = outputNeuron->createActivation(nullptr, firstInputAct->getContext(), outputBindingSignals);
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

