#include "network/linker.h"
#include "network/activations_per_context.h"
#include <stdexcept>



void Linker::linkOutgoing(Activation* act) {
    if (!act) {
        throw std::invalid_argument("Activation cannot be null");
    }
    
    Neuron* neuron = act->getNeuron();
    neuron->wakeupPropagable();

    for (auto& s : neuron->getOutputSynapsesAsStream()) {
        linkOutgoing(act, s);
    }
}

void Linker::linkOutgoing(Activation* act, Synapse* outputSyn) {
    // Check if the synapse type has allowLatentLinking enabled
    SynapseType* synapseType = static_cast<SynapseType*>(outputSyn->getType());

    if (synapseType && synapseType->getAllowLatentLinking()) {
        // Use pair linking for synapses with allowLatentLinking enabled
        // This is needed for paired synapses like dot-product synapses
        pairLinking(act, outputSyn);
    } else {
        // Transition the input activation's binding signals forward through the synapse
        BindingSignal** inputBindingSignals = act->getBindingSignalsArray();
        BindingSignal** outputBindingSignals = outputSyn->transitionForward(const_cast<const BindingSignal**>(inputBindingSignals));

        // Find output activations that match the transitioned binding signals
        std::set<Activation*> outputActs = collectLinkingTargets(outputBindingSignals, outputSyn->getOutput(act->getModel()));

        for (auto& outputAct : outputActs) {
            outputSyn->createLink(act, outputAct);
        }

        if (outputActs.empty() && outputSyn->isPropagable()) {
            propagate(act, outputSyn, outputBindingSignals);
        }
    }
}

void Linker::propagate(Activation* act, Synapse* targetSyn, BindingSignal** outputBindingSignals) {
    // Use normal propagation for regular synapses
    Activation* oAct = targetSyn->getOutput(act->getModel())->createActivation(nullptr, act->getContext(), outputBindingSignals);

    targetSyn->createLink(act, oAct);

    linkIncoming(oAct, act);
}


// The purpose of PairLinking is just to optimize the linking of paired InputSynapses. The functionality should be identical
// to the propagate + linkIncoming calls. It just avoids actually instantiating the output activation if one of the
// two input activations is missing or is not yet fired.
// PairLinking avoids having to deal with incomplete Activations where some of the Binding-Signals are still missing.
void Linker::pairLinking(Activation* firstInputAct, Synapse* firstSynapse) {
    if (!firstInputAct) {
        throw std::invalid_argument("linkLatent: firstInputAct cannot be null");
    }
    
    Model* model = firstInputAct->getModel();

    SynapseType* firstSynapseType = static_cast<SynapseType*>(firstSynapse->getType());
    PairingConfig config = firstSynapseType->getPairingConfig();
    int pairBindingSignalSlot = config.bindingSignalSlot;

    // Paired synapse for the dot-product inner multiplication.
    Synapse* secondSynapse = firstSynapse->getPairedSynapseOutputSide();
    if (!secondSynapse) {
        // optionally realize an output link here using only (a1, s1).
        return;
    }

    Neuron* secondInputNeuron = secondSynapse->getInput(model);

// TODO: use a pointer to the set instead of holding and overwriting the entire set datatype
    std::set<Activation*> secondInputActs;
    if(pairBindingSignalSlot != -1) {
        // Ensure matching second input neuron and paired binding-signal slot.
        int firstFromSlot = firstSynapseType->mapTransitionBackward(pairBindingSignalSlot);
        BindingSignal* bs = firstInputAct->getBindingSignal(firstFromSlot);

        SynapseType* secondSynapseType = static_cast<SynapseType*>(secondSynapse->getType());
        int secondFromSlot = secondSynapseType->mapTransitionBackward(pairBindingSignalSlot);

        secondInputActs = bs->getActivations(secondInputNeuron);
    } else {
        // BS Wildcard case, only ensure matching second input neuron.
        Context* ctx = firstInputAct->getContext();
        ActivationsPerContext* actPerContext = secondInputNeuron->getActivationsPerContext(ctx);

        if (actPerContext) {
            secondInputActs = actPerContext->getActivations();
        }
    }

    Neuron* outputNeuron = firstSynapse->getOutput(model);

    // Pairwise commit for each admissible a2 (avoid degenerate a1==a2 case).
    for (Activation* secondInputAct : secondInputActs) {
        if (!secondInputAct || secondInputAct == firstInputAct) continue;

        // Calculate output binding signals by transitioning first input forward through first synapse
        BindingSignal** firstInputBindingSignals = firstInputAct->getBindingSignalsArray();
        BindingSignal** outputBindingSignals = firstSynapse->transitionForward(const_cast<const BindingSignal**>(firstInputBindingSignals));

        // Calculate output binding signals by transitioning second input forward through second synapse
        BindingSignal** secondInputBindingSignals = secondInputAct->getBindingSignalsArray();
        BindingSignal** secondOutputBindingSignals = secondSynapse->transitionForward(const_cast<const BindingSignal**>(secondInputBindingSignals));
        
        // Merge binding signals instead of overwriting them
        if (secondOutputBindingSignals && outputBindingSignals) {
            Neuron* outputNeuron = firstSynapse->getOutput(firstInputAct->getModel());
            NeuronType* outputNeuronType = static_cast<NeuronType*>(outputNeuron->getType());
            int outputBSSlots = outputNeuronType->getNumberOfBSSlots();
            
            for (int slot = 0; slot < outputBSSlots; slot++) {
                if (secondOutputBindingSignals[slot] != nullptr) {
                    outputBindingSignals[slot] = secondOutputBindingSignals[slot];
                }
            }
        }
        
        // Clean up the second output binding signals array since we merged it
        delete[] secondOutputBindingSignals;

        // Select or realize an output activation compatible with beta1.
        Activation* outputAct = nullptr;

        // TODO: lookup existing outputAct using the outputBindingSignals
            
        // If no existing activation found, create a new one
        if (!outputAct) {
            outputAct = outputNeuron->createActivation(nullptr, firstInputAct->getContext(), outputBindingSignals);
        }

        // Avoid duplicate links if already present.
        bool l1Exists = firstSynapse->hasLink(firstInputAct, outputAct);
        bool l2Exists = secondSynapse->hasLink(secondInputAct, outputAct);

        if (!l1Exists) {
            firstSynapse->createLink(firstInputAct, outputAct);
        }
        if (!l2Exists) {
            secondSynapse->createLink(secondInputAct, outputAct);
        }
        
        // Clean up the allocated output binding signals array
        delete[] outputBindingSignals;
    }
}

void Linker::linkIncoming(Activation* act, Activation* excludedInputAct) {
    for (auto& s : act->getNeuron()->getInputSynapsesAsStream()) {
//        if (static_cast<SynapseType*>(s->getType())->isIncomingLinkingCandidate(bsKeys)) {
            linkIncoming(act, s, excludedInputAct);
//        }
    }
}

void Linker::linkIncoming(Activation* oAct, Synapse* inputSyn, Activation* excludedInputAct) {
    // Get the output activation's binding signals and transition them backward through the synapse
    BindingSignal** outputBindingSignals = oAct->getBindingSignalsArray();
    BindingSignal** inputBindingSignals = inputSyn->transitionBackward(const_cast<const BindingSignal**>(outputBindingSignals));
    
    // Find input activations that match the transitioned binding signals
    for (auto& iAct : collectLinkingTargets(inputBindingSignals, inputSyn->getInput(oAct->getModel()))) {
        if (iAct != excludedInputAct) {
            bool linkExists = inputSyn->hasLink(iAct, oAct);
            if (!linkExists) {
                inputSyn->createLink(iAct, oAct);
            }
        }
    }
    
    // Clean up the allocated input binding signals array
    delete[] inputBindingSignals;
}

std::set<Activation*> Linker::collectLinkingTargets(BindingSignal** bindingSignals, Neuron* n) {
    std::set<Activation*> result;
    // TODO: Verify that the resulting Activations contain all provided binding-signals.
    
    if (!bindingSignals || !n) {
        return result;
    }
    
    NeuronType* neuronType = static_cast<NeuronType*>(n->getType());
    int numberOfBSSlots = neuronType->getNumberOfBSSlots();
    
    for (int slot = 0; slot < numberOfBSSlots; slot++) {
        BindingSignal* bs = bindingSignals[slot];
        if (!bs) continue;
        auto acts = bs->getActivations(n);
        result.insert(acts.begin(), acts.end());
    }
    return result;
}
