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

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.text.Document;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static experiment.logger.ExperimentLogger.CSV_FORMAT;
import static experiment.logger.StatisticLogger.Key.*;

/**
 * @author Lukas Molzberger
 */
public class StatisticLogger  {

    CSVPrinter printer;

    Map<Key, int[]> counter = new TreeMap<>();

    public void open(File f)  {
        try {
            if(f.exists())
                f.delete();

            List<String> headerLabels = new ArrayList<>();

            headerLabels.add("Doc ID");
            headerLabels.add("Content");

            headerLabels.addAll(
                    Arrays.stream(values())
                            .map(k -> k.name())
                            .toList()
            );
            FileWriter fw = new FileWriter(f);
            printer = new CSVPrinter(
                    fw,
                    CSV_FORMAT.withHeader(headerLabels.toArray(new String[0]))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum Key {
        ACTS,
        FIRED_ACTS,
        INACTIVE_ACTS,
        STRONG_ACTS,
        SUPPRESSED_ACTS,
        PRIMARY_BINDING_ACTS,
        INACTIVE_SECONDARY_BINDING_ACTS,
        REL_ACTS,
        NEW_INSTANCES,
        ABSTRACT_ACTS,
    }

    private void count(Activation act) {
        count(ACTS);
        if(act.getNet().getValue() > 0.0)
            count(FIRED_ACTS);
        else
            count(INACTIVE_ACTS);

        if(act.getNet().getValue() > 0.9)
            count(STRONG_ACTS);

        if(act.getNetPreAnneal().getValue() > 0.0 && act.getNet().getValue() <= 0.0)
            count(SUPPRESSED_ACTS);

        Activation tAct = act.getTemplate();
        if(tAct != null) {
            if (tAct.getLabel().equalsIgnoreCase("Abstract (S) Pos:0"))
                count(PRIMARY_BINDING_ACTS);
            else if(act.getNet().getValue() <= 0.0)
                count(INACTIVE_SECONDARY_BINDING_ACTS);
        }

        if(act instanceof LatentRelationActivation)
            count(REL_ACTS);

        if(act.isNewInstance())
            count(NEW_INSTANCES);

        if(act.getNeuron().isAbstract())
            count(ABSTRACT_ACTS);
    }

    public void count(Key key) {
        int[] c = counter.computeIfAbsent(key, k -> new int[1]);
        c[0]++;
    }

    public void clearAllCounters() {
        counter.clear();
    }

    public void log(Document doc) {
        doc.getActivations()
                .forEach(act ->
                    count(act)
                );

        try {
            List<String> entry = new ArrayList<>();

            entry.add("" + doc.getId());
            entry.add(doc.getContent());

            entry.addAll(
                    Arrays.stream(values())
                    .map(k -> "" + getCount(k))
                    .toList()
            );

            printer.printRecord(entry.toArray());
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clearAllCounters();
    }

    private int getCount(Key k) {
        int[] c = counter.get(k);
        if(c == null)
            return 0;
        return c[0];
    }
}
