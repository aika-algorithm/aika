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
package network.aika.meta.topics;

import network.aika.Model;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.meta.sequences.PhraseModel;
import network.aika.text.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Lukas Molzberger
 */
public class TopicModel {
    private static final Logger log = LoggerFactory.getLogger(TopicModel.class);

    private PhraseModel phraseModel;

    protected Model model;

    protected NeuronProvider topicPatternN;

    protected NeuronProvider topicPatternCategory;


    public TopicModel(PhraseModel phraseModel) {
        this.phraseModel = phraseModel;
        model = phraseModel.getModel();
    }

    public void addTargetTopics(Document doc, Set<String> tsLabels) {
        log.info(doc.getContent() + " : " + tsLabels.stream().collect(Collectors.joining(", ")));
    }

    public void initTopicTemplates() {

    }
}
