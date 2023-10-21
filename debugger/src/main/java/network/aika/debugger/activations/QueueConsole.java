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
package network.aika.debugger.activations;

import network.aika.Document;
import network.aika.debugger.AbstractConsole;
import network.aika.debugger.Visible;
import network.aika.debugger.activations.renderer.QueueRenderer;
import network.aika.elements.Element;

import javax.swing.text.StyledDocument;

/**
 * @author Lukas Molzberger
 */
public class QueueConsole extends AbstractConsole {

    ActivationConsoleManager actConsoleManager;
            
    Visible sortKeyVisible = Visible.HIDE;

    Element highlightedElement;

    public QueueConsole(ActivationConsoleManager actConsoleManager) {
        this.actConsoleManager = actConsoleManager;
        
        addMouseListener(new QueueConsoleMouseManager(this));
    }

    public Visible getSortKeyVisible() {
        return sortKeyVisible;
    }

    public void setSortKeyVisible(Visible sortKeyVisible) {
        this.sortKeyVisible = sortKeyVisible;
    }

    public void renderQueue(StyledDocument sDoc, Document doc) {
        QueueRenderer queueRenderer = new QueueRenderer(doc, sortKeyVisible);

        queueRenderer.render(sDoc, highlightedElement);
    }

    public void update(Element highlightedElement) {
        this.highlightedElement = highlightedElement;

        update();
    }

    public void update() {
        render(sDoc ->
                renderQueue(sDoc, actConsoleManager.getThought())
        );
    }
}
