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
package network.aika.queue.steps;

import network.aika.debugger.EventType;
import network.aika.elements.Timestamp;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.enums.direction.Direction;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.keys.LinkUpdateQueueKey;

import static network.aika.fields.AbstractFieldLink.updateConnected;


/**
 *
 * @author Lukas Molzberger
 */
public class LinkUpdate extends ElementStep<ConjunctiveLink> {

    private Direction dir;

    private boolean state;

    public static void add(ConjunctiveLink l, Direction dir, boolean state) {
        Step.add(new LinkUpdate(l, dir, state));
    }

    public LinkUpdate(ConjunctiveLink l, Direction dir, boolean state) {
        super(l);

        this.state = state;
        this.dir = dir;
    }

    @Override
    public void createQueueKey(Timestamp timestamp) {
        queueKey = new LinkUpdateQueueKey(
                getRound(),
                getPhase(),
                getElement(),
                dir,
                timestamp
        );
    }

    @Override
    public void process() {
        ConjunctiveLink l = getElement();
        if(dir == Direction.INPUT) {
            updateConnected(l.getInputValueLink(), state, true);
            //updateConnected(outputSlotFL, state, true);

            l.retrieveAndConnectBindingSignals(state);
        } else {
            //  updateConnected(inputSlotFL, state, true);
        }

        boolean oppositeState = dir == Direction.INPUT ?
                l.isOutputSideActive() :
                l.isInputSideActive();

        if(state == oppositeState)
            l.getDocument().onElementEvent(EventType.UPDATE, l);
    }

    @Override
    public Phase getPhase() {
        return Phase.LINK_UPDATE;
    }

    @Override
    public String toString() {
        return super.toString() + " " + dir + " state:" + state;
    }
}
