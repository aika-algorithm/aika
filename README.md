# Project status
During the past one and a half years there have been many changes to the architecture of the core algorithm. Most notably the introduction of binding-signals as the underlying mechanism for the linking process and the introduction of updatable fields which allow for a more descriptive way to implement the mathematics within the project. These changes are still work-in-progress, but will hopefully soon converge to a more stable state.

# About the AIKA Neural Network
AIKA (**A**rtificial **I**ntelligence for **K**nowledge **A**cquisition) is a new neuronType of artificial neural network designed to mimic the behavior of a biological brain more closely and to bridge the gap to classical AI. A key design decision in the AIKA network is to conceptually separate the activations from their neurons, meaning that there are two separate graphs. One graph consisting of neurons and synapses representing the knowledge the network has already acquired and another graph consisting of activations and links describing the information the network was able to infer about a concrete input data set. There is a one-to-many relation between the neurons and the activations. For example, there might be a neuron representing a word or a specific meaning of a word, but there might be several activations of this neuron, each representing an occurrence of this word within the input data set. A consequence of this decision is that we must give up on the idea of a fixed layered topology for the network, since the sequence in which the activations are fired depends on the input data set. Within the activation network, each activation is grounded within the input data set, even if there are several activations in between. This means links between activations server multiple purposes:
- They propagate the activation value.
- They propagate the binding-signal, that is used for the linking process.
- They establish an approximate causal relation through the fired timestamps of their input and output activations.
- They allow the training gradient to be propagated backwards.
- Negative feedback links create mutually exclusive branches within the activations network.
- Positive feedback links allow the binding neurons of a pattern neuron ensemble to support each other, by feeding the activation value of the patten neuron back to its input binding-neurons.

The AIKA network uses four different types of neurons:
- Pattern-Neurons (PN)
- Binding-Neurons (BN)
- Inhibitory-Neurons (IN)
- Category-Neurons (CN)

The pattern-neurons and the binding-neurons are both conjunctive in nature while the inhibitory-neurons and the 
category-neurons are disjunctive. The binding-neurons are kind of the glue code of the whole network. On the one hand, 
they bind the input-features of a pattern to the pattern-neuron and on the other hand receive negative feedback synapses 
from the inhibitory neurons which allow them to either be suppressed by an opposing pattern or allow themselves to 
suppress another conflicting pattern. Like the neuron types there are also several types of synapses, depending on 
which types of neurons they connect. For example, the input synapses of an inhibitory neuron are always linked to 
binding-neurons, while the input synapses of category-neurons are always linked to pattern-neurons.

The following types of synapses exist within the **AIKA** network:

- **PrimaryInputSynapse** ((PN|CN) -> BN)
- **RelatedInputSynapse** (BN -> BN)
- **SamePatternSynapse** (BN -> BN)
- **PositiveFeedbackSynapse** (PN -> BN)
- **NegativeFeedbackSynapse** (IN -> BN)
- **ReversePatternSynapse** (BN -> BN)
- **PatternSynapse** (BN -> PN)
- **CategorySynapse** (PN -> CN)
- **InhibitorySynapse** (BN -> IN)

The binding-signal that is propagated along linked synapses carries a state consisting of either of these three values: **SAME**, **INPUT**, **BRANCH** 

**SAME** indicates that the binding signal has not yet left its originating neuron pattern ensemble and is used to bind the binding-activations to the current pattern. **INPUT** indicates that the binding 
signal was propagated to a dependant pattern neuron ensemble, for instance through the PrimaryInputSynapse or the RelatedInputSynapse.
**BRANCH** indicates, that the binding signal originated from a binding activation instead of a pattern activation.

As already mentioned, the binding-neurons of a pattern neuron ensemble are used to bind this pattern to its input 
features. To verify that all the input-features occurred in the correct relation to each other the SamePatternSynapse 
is used. The **SamePatternSynapse** connects two binding-neurons within the same pattern neuron ensemble. 
The **SamePatternSynapse** connects two binding-neurons
within the same pattern neuron ensemble and is only linked both ends of the synapse have been reached
by the same binding-signal. Therefore, the SamePatternSynapse is used to avoid what
is called the superposition catastrophe.

Since the category-neuron passes on the binding-signal of its input pattern-neuron, it can act as a 
category slot, therefore allowing the network great flexibility in abstracting concepts.

Initially, the network starts out empty and is then gradually populated during training. The induction of new neurons 
and synapses is guided by a network of template neurons and synapses.

