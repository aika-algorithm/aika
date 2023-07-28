/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package experiment;

import network.aika.meta.AbstractTemplateModel;
import network.aika.meta.LabelUtil;
import network.aika.meta.SyllableTemplateModel;
import network.aika.Model;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.*;
import network.aika.parser.Context;
import network.aika.parser.ParserPhase;
import network.aika.parser.TrainingParser;
import network.aika.text.Document;
import network.aika.tokenizer.SimpleCharTokenizer;
import network.aika.tokenizer.Tokenizer;
import org.apache.commons.io.IOUtils;
import experiment.logger.ExperimentLogger;
import experiment.logger.LoggingListener;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static network.aika.meta.LabelUtil.generateTemplateInstanceLabels;
import static network.aika.parser.ParserPhase.COUNTING;
import static network.aika.parser.ParserPhase.TRAINING;
import static network.aika.utils.Utils.doubleToString;


/**
 * @author Lukas Molzberger
 */
public class SyllablesExperiment extends TrainingParser<Context> {

    Model model;
    AbstractTemplateModel syllableModel;

    Tokenizer charTokenizer;

    ExperimentLogger experimentLogger;

    int[] counter;

    public SyllablesExperiment() {
        model = new Model();

        syllableModel = new SyllableTemplateModel(model);
        syllableModel.initStaticNeurons();

        model.setN(0);

        charTokenizer = new SimpleCharTokenizer(syllableModel);
    }

    @Override
    protected Document initDocument(String txt, Context context, ParserPhase phase) {
        Document doc = super.initDocument(txt, context, phase);

        doc.getConfig()
                .setAlpha(null)
                .setLearnRate(0.01);

        doc.setInstantiationCallback(act -> {
            generateTemplateInstanceLabels(act);
            logInstantiation(act, act.getLabel());
        });

        return doc;
    }

    @Override
    protected AbstractTemplateModel getTemplateModel() {
        return syllableModel;
    }

    @Override
    public Tokenizer getTokenizer() {
        return charTokenizer;
    }

    public static void main(String[] args) throws IOException {
        new SyllablesExperiment()
                .testTraining();
    }

    public void testTraining() throws IOException {
        List<String> inputs = new ArrayList<>();
        inputs.add("der");
        inputs.add("der");

        inputs.addAll(getInputs());
        train(inputs);
    }

    private void train(List<String> inputs) {
        syllableModel.initStaticNeurons();

        // Counting letters loop
        inputs.forEach(w ->
            super.process(w, null, COUNTING)
        );

        syllableModel.initTemplates();

        counter = new int[1];

        experimentLogger = new ExperimentLogger();

        inputs.forEach(w ->
            process(w,  null, TRAINING)
        );

        experimentLogger.close();
    }

    @Override
    public Document process(String txt, Context context, ParserPhase phase) {
        System.out.println(counter[0] + " " + txt);

        Document doc = initDocument(txt, context, phase);

        if(counter[0] >= 0) {// 3, 6, 11, 18, 100, 39, 49
            debugger = AIKADebugger.createAndShowGUI(doc);
        }
        if(txt.equalsIgnoreCase("kleider")) {
//            debugger = AIKADebugger.createAndShowGUI(doc);
        }
        if(doc.getId() == 49289l) {
     //       debugger = AIKADebugger.createAndShowGUI(doc);
        }

        LoggingListener logger = null;

        if(debugger != null) {
            logger = new LoggingListener();
            doc.addEventListener(logger);
        }

        infer(doc, context, phase);

        experimentLogger.annealingLogInit(doc);
        anneal(doc);

        train(doc);

        logPatternMatches(doc);
        experimentLogger.log(doc);

        if(logger != null)
            doc.removeEventListener(logger);

        doc.disconnect();

        counter[0]++;

        return doc;
    }


    private static void logPatternMatches(Document doc) {
        doc.getActivations()
                .stream()
                .filter(act -> act instanceof PatternActivation)
                .filter(act -> !(act instanceof TokenActivation))
                .forEach(act ->
                        logPatternMatch((PatternActivation) act)
                );
    }

    private static void logPatternMatch(PatternActivation act) {
        if(act.getNetUnsuppressed().getValue() <= 0.0 || act.isAbstract())
            return;

        System.out.println("   " +
                (act.isFired() ? "Matching " : "Inactive Match ") +
                (act.isAbstract() ? "abstract " : "") +
                act.getClass().getSimpleName() +
                " '" + act.getLabel() + "'" +
                (!act.isAbstract() ? " '" + LabelUtil.generateLabel(act.getNeuron()) + "'" : "") +
                " nId:" + act.getNeuron().getId() +
                " r:" + act.getRange() +
                " grad:" + doubleToString(act.getGradient().getValue(), "#.######")
        );
    }

    private static void logInstantiation(Activation instanceAct, String label) {
        if(instanceAct instanceof BindingActivation)
            return;

        System.out.println("   Instantiating " +
                instanceAct.getClass().getSimpleName() +
                " '" + label + "'" +
                " nId:" + instanceAct.getNeuron().getId() +
                " r:" + instanceAct.getRange()
        );
    }



    private List<String> getInputs() throws IOException {
        String[] files = new String[]{
                "Aschenputtel",
                "BruederchenUndSchwesterchen",
                "DasTapfereSchneiderlein",
                "DerFroschkoenig",
                "DerGestiefelteKater",
                "DerGoldeneSchluessel",
                "DerSuesseBrei",
                "DerTeufelMitDenDreiGoldenenHaaren",
                "DerWolfUndDieSiebenJungenGeisslein",
                "DieBremerStadtmusikanten",
                "DieDreiFedern",
                "DieSterntaler",
                "DieWeisseSchlange",
                "DieZwoelfBrueder",
                "Dornroeschen",
                "FrauHolle",
                "HaenselUndGretel",
                "HansImGlueck",
                "JorindeUndJoringel",
                "KatzeUndMausInGesellschaft",
                "MaerchenVonEinemDerAuszogDasFuerchtenZuLernen",
                "Marienkind",
                "Rapunzel",
                "Rotkaeppchen",
                "Rumpelstilzchen",
                "SchneeweisschenUndRosenrot",
                "Schneewitchen",
                "TischleinDeckDich",
                "VonDemFischerUndSeinerFrau"
        };

        ArrayList<String> inputs = new ArrayList<>();

        for (String fn : files) {
            InputStream is = getClass().getResourceAsStream("../corpora/public-domain-txt/" + fn + ".txt");
            assert is != null;

            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String txt = writer.toString();

            txt = txt.replace('.', ' ');
            txt = txt.replace(',', ' ');
            txt = txt.replace('?', ' ');
            txt = txt.replace('!', ' ');
            txt = txt.replace('"', ' ');
            txt = txt.replace('-', ' ');
            txt = txt.replace(':', ' ');
            txt = txt.replace(';', ' ');
            txt = txt.replace('\n', ' ');
            txt = txt.replace("  ", " ");
            txt = txt.replace("  ", " ");

            for(String word: txt.split(" ")) {
                String w = word.toLowerCase().trim();
                if(w.isBlank())
                    continue;

                inputs.add(w);
            }
        }
        return inputs;
    }
}
