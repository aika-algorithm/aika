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

import network.aika.elements.activations.types.PatternActivation;
import network.aika.Document;
import org.apache.commons.csv.CSVFormat;

import java.io.File;
import java.util.Arrays;
import java.util.TreeMap;


/**
 * @author Lukas Molzberger
 */
public class ExperimentLogger {

    public static CSVFormat CSV_FORMAT = CSVFormat.EXCEL;

    File experimentPath = new File("experiments/src/main/resources/experiments/");

    TreeMap<Long, PatternLogger> patternLogger = new TreeMap<>();
    StatisticLogger statLogger = new StatisticLogger();

    AnnealingLogger annealingLogger = new AnnealingLogger();

    public ExperimentLogger() {
        if(experimentPath.exists()) {
            Arrays.stream(experimentPath.listFiles()).forEach(f ->
                    f.delete()
            );
            experimentPath.delete();
        }

        experimentPath.mkdir();

        statLogger.open(new File(experimentPath, "statistic.csv"));
    }

    public void annealingLogInit(Document doc) {
   /*     annealingLogger.open(
                new File(experimentPath, "annealing-" + doc.getId() + "-" + doc.getContent() + ".csv"),
                doc
        );*/
    }

    public void log(Document doc) {
     //   annealingLogger.close();

        statLogger.log(doc);

        doc.getActivations()
                .stream()
                .filter(act -> act instanceof PatternActivation)
                .map(act -> (PatternActivation)act)
                .forEach(act -> {
                    PatternLogger pl = patternLogger.computeIfAbsent(act.getNeuron().getId(), id ->
                            new PatternLogger(experimentPath, act)
                    );
                    if(pl != null)
                        pl.log(act);
                });
    }

    public void close() {
        statLogger.close();

        patternLogger.values()
                .forEach(pl -> pl.close());
    }
}
