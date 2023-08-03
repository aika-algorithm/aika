package network.aika.meta.textsections;

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.BeforeRelationNeuron;
import network.aika.elements.neurons.relations.ContainsRelationNeuron;
import network.aika.elements.synapses.*;
import network.aika.meta.PhraseTemplateModel;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static network.aika.meta.NetworkMotivs.*;

public class HeadlineModel {

    protected Model model;

    private PhraseTemplateModel phraseModel;

    protected NeuronProvider headlineTargetInput;

    protected NeuronProvider relContains;

    protected NeuronProvider headlinePrimaryInputBN;

    protected NeuronProvider headlineBN;

    protected NeuronProvider headlinePattern;

    protected double headlineInputPatternNetTarget = 5.0;


    public HeadlineModel(PhraseTemplateModel phraseModel) {
        this.phraseModel = phraseModel;
        this.model = phraseModel.getModel();
    }

    protected void initHeadlineTemplates() {
        headlineTargetInput = model.lookupNeuronByLabel("Abstract TS Headline Target Input", l ->
                new BindingNeuron()
                        .init(model, l)
        ).getProvider(true);

        headlinePattern = instantiatePatternWithBindingNeurons()
                .getProvider(true);

        double netTarget = 2.5;

        headlineBN = addBindingNeuron(
                headlinePattern.getNeuron(),
                "Text-Section-Headline",
                10.0,
                headlineInputPatternNetTarget,
                netTarget
        ).getProvider(true);

        relContains = ContainsRelationNeuron.lookupRelation(model, true)
                .getProvider(true);

        addRelation(
                headlineTargetInput.getNeuron(),
                headlinePrimaryInputBN.getNeuron(),
                relContains.getNeuron(),
                5.0,
                10.0);
    }

    private PatternNeuron instantiatePatternWithBindingNeurons() {
        Map<NeuronProvider, Neuron> templateMapping = new TreeMap<>();

        PatternNeuron tpn = phraseModel.patternN.getNeuron();
        PatternNeuron pn = instantiatePatternNeuron(tpn);
        templateMapping.put(tpn.getProvider(), pn);

        OuterInhibitoryNeuron tInhibN = phraseModel.inhibitoryN.getNeuron();
        OuterInhibitoryNeuron inhibN = instantiateInhibitoryNeuron(tInhibN);
        templateMapping.put(tInhibN.getProvider(), inhibN);

        List<BindingNeuron> templateBindingNeurons = tpn.getInputSynapsesByType(PatternSynapse.class)
                .map(Synapse::getInput)
                .toList();

        templateBindingNeurons.forEach(tn -> {
                    BindingNeuron[] bn = instantiateBindingNeuron(tn);
                    templateMapping.put(bn[0].getProvider(), bn[1]);
                }
        );

        templateBindingNeurons.forEach(tbn -> {
                    BindingNeuron bn = (BindingNeuron) templateMapping.get(tbn.getProvider());
                    instantiatePatternSynapse(tpn, pn, tbn, bn);
                    instantiateInhibitorySynapse(tInhibN, inhibN, tbn, bn);
                    instantiateBindingNeuronSynapses(np -> templateMapping.get(np), tbn.getProvider(), bn);
                }
        );
        return pn;
    }


    private PatternNeuron instantiatePatternNeuron(PatternNeuron tpn) {
        PatternNeuron pn;
        pn = tpn
                .instantiateTemplate()
                .init(model, "Text-Section-Headline");

        makeAbstract(pn);
        return pn;
    }

    private OuterInhibitoryNeuron instantiateInhibitoryNeuron(OuterInhibitoryNeuron tInhibN) {
        OuterInhibitoryNeuron inhibN;
        inhibN = tInhibN
                .instantiateTemplate()
                .init(model, "Inhib. TS-Headline");

        makeAbstract(inhibN);
        return inhibN;
    }

    private static void instantiatePatternSynapse(PatternNeuron tpn, PatternNeuron pn, BindingNeuron tbn, BindingNeuron bn) {
        PatternSynapse ps = (PatternSynapse) tpn.getInputSynapse(tbn.getProvider());
        ps.instantiateTemplate(bn, pn);
    }

    private static void instantiateInhibitorySynapse(OuterInhibitoryNeuron tInhibN, OuterInhibitoryNeuron inhibN, BindingNeuron tbn, BindingNeuron bn) {
        OuterInhibitorySynapse inhibS = (OuterInhibitorySynapse) tInhibN.getInputSynapse(tbn.getProvider());
        inhibS.instantiateTemplate(bn, inhibN);
    }

    private static void instantiateBindingNeuronSynapses(Function<NeuronProvider, Neuron> resolver, NeuronProvider tbn, BindingNeuron bn) {
        tbn.getInputSynapses()
                .filter(ts -> !(ts instanceof BindingCategoryInputSynapse))
                .forEach(ts -> {
                    Neuron<?> in = resolver.apply(ts.getPInput());
                    ts.instantiateTemplate(
                            in != null ?
                                    in :
                                    ts.getInput(),
                            bn
                    );
                });

        tbn.getOutputSynapses()
                .filter(ts -> !(ts instanceof BindingCategorySynapse))
                .forEach(ts -> {
                    Neuron<?> out = resolver.apply(ts.getPOutput());
                    ts.instantiateTemplate(
                            bn,
                            out != null ?
                                    out :
                                    ts.getOutput()
                    );
                });
    }

    private BindingNeuron[] instantiateBindingNeuron(BindingNeuron tbn) {
        BindingNeuron bn = tbn
                .instantiateTemplate()
                .init(model, tbn.getLabel() + " TS-Headline");

        makeAbstract(bn);

        setHeadlinePrimaryInput(tbn, bn);

        return new BindingNeuron[] {tbn, bn};
    }


    public void setHeadlinePrimaryInput(BindingNeuron tbn, BindingNeuron bn) {
        if(tbn.getId().longValue() == phraseModel.primaryBN.getId().longValue())
            headlinePrimaryInputBN = bn.getProvider(true);
    }

    public boolean isHeadlinePrimaryInput(BindingNeuron bn) {
        return headlinePrimaryInputBN.getId().longValue() ==
                bn.getId().longValue();
    }
}
