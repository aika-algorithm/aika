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
import network.aika.elements.links.Link;
import network.aika.enums.direction.Direction;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;
import network.aika.queue.keys.LinkUpdateQueueKey;

import static network.aika.elements.links.BSLinkEvent.ON_STATE_CHANGE;
import static network.aika.fields.link.AbstractFieldLink.updateConnected;


/**
 *
 * @author Lukas Molzberger
 */
public class LinkUpdate extends ElementStep<Link> {

    private Direction dir;

    private boolean currentState;
    private boolean targetState;


    public LinkUpdate(Link l, Direction dir) {
        super(l);

        this.dir = dir;
    }

    public void setTargetState(boolean targetState) {
        this.targetState = targetState;

        if(!isQueued && currentState != targetState)
            Step.add(this);
    }

    @Override
    public void createQueueKey(Timestamp timestamp, int round) {
        queueKey = new LinkUpdateQueueKey(
                round,
                getPhase(),
                getElement(),
                dir,
                timestamp
        );
    }

    @Override
    public void process() {
        currentState = targetState;

        Link l = getElement();
        if(dir == Direction.INPUT) {
            updateConnected(l.getInputValueLink(), targetState, true);

            l.checkPrimarySuppression();
        }

        l.updateBindingSignals(ON_STATE_CHANGE, targetState);

        l.getDocument().onElementEvent(EventType.UPDATE, l);
    }

    @Override
    public Phase getPhase() {
        return Phase.LINK_UPDATE;
    }

    public boolean getCurrentState() {
        return currentState;
    }

    public boolean getTargetState() {
        return targetState;
    }

    @Override
    public String toString() {
        return super.toString() + " " + dir + " state:" + targetState;
    }
}
