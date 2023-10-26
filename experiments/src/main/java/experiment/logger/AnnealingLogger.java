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
package experiment.logger;

import network.aika.elements.links.outerinhibitoryloop.NegativeFeedbackLink;
import network.aika.fields.Field;
import network.aika.Document;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static experiment.logger.ExperimentLogger.CSV_FORMAT;
import static network.aika.utils.Utils.doubleToString;

/**
 * @author Lukas Molzberger
 */
public class AnnealingLogger {

    CSVPrinter printer;

    public void open(File f, Document doc)  {
        try {
            if(f.exists())
                f.delete();

            List<String> headerLabels = new ArrayList<>();
            headerLabels.add("Anneal Value");

            headerLabels.addAll(createHeader(doc));


            FileWriter fw = new FileWriter(f);
            printer = new CSVPrinter(
                    fw,
                    CSV_FORMAT.withHeader(headerLabels.toArray(new String[0]))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        doc.getAnnealing().addListener("Annealing Logger",
                (l, nr, u) ->
                        log(doc),
                true);
    }

    private List<String> createHeader(Document doc) {
        return getNegativeFeedbackLinks(doc)
                .flatMap(l ->
                        Stream.of(
                                getLinkLabel(l) + "-x",
                                getLinkLabel(l) + "-w",
                                getLinkLabel(l) + "-net"
                        )
                )
                .toList();
    }

    private static String getLinkLabel(NegativeFeedbackLink l) {
        return l.getInput().toKeyString() + " -> " + l.getOutput().toKeyString();
    }

    private List<String> createEntry(Document doc) {
        return getNegativeFeedbackLinks(doc)
                .flatMap(l ->
                        Stream.of(
                                doubleToString(l.getInputValue().getValue()),
                                doubleToString(l.getSynapse().getWeight().getValue()),
                                doubleToString(l.getOutput().getNet().getValue())
                        )
                )
                .toList();
    }

    public Stream<NegativeFeedbackLink> getNegativeFeedbackLinks(Document doc) {
        return doc.getAnnealing()
                .getReceivers()
                .stream()
                .filter(fl -> fl.getOutput() instanceof Field)
                .map(fl ->  (Field) fl.getOutput())
                .map(f -> (NegativeFeedbackLink) f.getReference());
    }

    public void close() {
        try {
            if(printer != null)
                printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(Document doc) {
        try {
            List<String> entry = new ArrayList<>();
            entry.add("" + doubleToString(doc.getAnnealing().getValue()));
            entry.addAll(createEntry(doc));

            printer.printRecord(entry.toArray());
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
