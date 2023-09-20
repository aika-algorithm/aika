package network.aika.meta.textsections;

import network.aika.InstantiationUtil;
import network.aika.Model;
import network.aika.TemplateModel;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.meta.TargetInput;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import network.aika.text.GroundRef;
import network.aika.text.Range;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.neurons.Neuron.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.meta.LabelUtil.getAbstractPatternLabel;
import static network.aika.meta.textsections.TextSectionModel.TEXT_SECTION_LABEL;
import static network.aika.meta.textsections.TypedTextSectionModel.*;
import static network.aika.queue.Phase.ANNEAL;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.queue.keys.QueueKey.MAX_ROUND;

public class TextSectionInstance extends InstantiationUtil implements Writable {

    TypedTextSectionModel tsModel;

    PatternNeuron headlineTargetInputPN;
    PatternNeuron targetInputPN;

    public TextSectionInstance(TypedTextSectionModel tsModel) {
        this.tsModel = tsModel;
    }

    @Override
    public TemplateModel getTemplateModel() {
        return tsModel;
    }

    public Model getModel() {
        return tsModel.getModel();
    }

    public PhraseModel getPhraseModel() {
        return tsModel.phraseModel;
    }

    public TargetInput getTargetInput() {
        return tsModel.targetInput;
    }

    public TextSectionInstance instantiate(String label, boolean makeAbstract) {
        getTemplateModel().prepareInstantiation();

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        String headline = label + " " + HEADLINE_LABEL;
        String textSection = label + " " + TEXT_SECTION_LABEL;

        Document doc = new Document(getModel(), headline + " " + textSection);

//        AIKADebugger.createAndShowGUI()
//                .setDocument(doc);

        doc.setInstantiationCallback((tAct, iAct) -> {
            generateLabel(tAct, iAct, label);

            if(isPartOfHeadline(tAct) || isHint(tAct)) {
                ConjunctiveSynapse s = (ConjunctiveSynapse) iAct.getNeuron().makeAbstract();

                if (getTargetInput().getTargetInput() == tAct.getNeuron()) {
                    s.setWeight(2.0);
                    s.adjustBias();
                } else
                    s.setWeight(PASSIVE_SYNAPSE_WEIGHT);
            }
        });

        try {
            doc.setFeedbackTriggerRound();

            getTemplateModel().prepareExampleDoc(doc, label);

            doc.process(MAX_ROUND, INFERENCE);
            doc.anneal();
            doc.process(MAX_ROUND, ANNEAL);
            doc.instantiateTemplates();

            TargetInput.setTemplateOnly(
                    lookupInstance(doc, getTargetInput().getTargetInput()),
                    lookupInstance(doc, getTargetInput().getTargetInputBN()),
                    true
            );

            TargetInput.setTemplateOnly(
                    lookupInstance(doc, tsModel.headlineTargetInput),
                    lookupInstance(doc, tsModel.headlineTargetInputBN),
                    true
            );

            getPhraseModel().getPatternNeuron().setTemplateOnly(false);

            headlineTargetInputPN = lookupInstance(doc, tsModel.headlineTargetInput);
            targetInputPN = lookupInstance(doc, getTargetInput().getTargetInput());
        } catch(Exception e) {
            throw new FailedInstantiationException("entity", e);
        } finally {
            doc.disconnect();
        }

        return this;
    }


    private void generateLabel(Activation tAct, Activation iAct, String label) {
        iAct.getNeuron().setLabel(
                tAct.getLabel()
                        .replace(HEADLINE_LABEL, getHeadlineLabel(label))
                        .replace(TEXT_SECTION_LABEL, getTextSectionLabel(label))
        );
    }

    public PatternNeuron getHeadlinePattern(String tsType) {
        return getModel().getInputNeuron(
                getAbstractPatternLabel(
                        getHeadlineLabel(tsType)
                ),
                tsModel.headlinePattern
        );
    }

    public PatternNeuron getTextSectionPattern(String tsType) {
        return getModel().getInputNeuron(
                getAbstractPatternLabel(
                        getTextSectionLabel(tsType)
                ),
                tsModel.patternN
        );
    }


    private boolean isPartOfHeadline(Activation tAct) {
        String l = tAct.getLabel();
        return l.contains(HEADLINE_LABEL) && !l.contains(TEXT_SECTION_LABEL);
    }

    private boolean isHint(Activation tAct) {
        return tAct.getLabel().contains("Hint");
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(headlineTargetInputPN.getId());
        out.writeLong(targetInputPN.getId());
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        headlineTargetInputPN = m.lookupNeuronProvider(in.readLong()).getNeuron();
        targetInputPN = m.lookupNeuronProvider(in.readLong()).getNeuron();
    }
}
