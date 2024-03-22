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
package network.aika.debugger.activations.renderer;

import network.aika.Document;
import network.aika.debugger.ConsoleRenderer;
import network.aika.debugger.Visible;
import network.aika.fields.FieldObject;
import network.aika.queue.Step;
import network.aika.queue.Timestamp;
import network.aika.queue.keys.QueueKey;
import network.aika.utils.StringUtils;

import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * @author Lukas Molzberger
 */
public class StepConsoleRenderer implements ConsoleRenderer {

    private Document doc;
    private boolean selected;

    private Visible sortKey;

    public StepConsoleRenderer(Document doc, Step step, FieldObject highlightedElement, Visible sortKey) {
        this.doc = doc;
        this.sortKey = sortKey;
        this.selected = step.getElement() == highlightedElement;
    }

    public void render(StyledDocument sDoc, Step s) {
        Timestamp currentTimestamp = doc.getTimestampOnProcess();

        Color c = new Color(
                selected ? 150 : 0,
                s.getQueueKey() != null &&
                        currentTimestamp.compareTo(s.getQueueKey().getCurrentTimestamp()) <= 0
                        ? 150 : 0,
                0
        );

        appendEntry(
                sDoc,
                stepToPrefix(s),
                s.toString(),
                c
        );
    }

    private String stepToPrefix(Step s) {
        QueueKey qk = s.getQueueKey();
        return s.getStepName() +
                    " " + StringUtils.roundToString(qk.getRound()) + " " + qk.getPhase() + " " +
                (sortKey == Visible.SHOW ? " " + s.getQueueKey() : "");
    }
}
