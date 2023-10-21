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
package network.aika.elements;


import network.aika.elements.activations.Activation;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkKey implements Comparable<LinkKey> {

    private final long nId;
    private final Integer actId;

    private final int synapseId;

    public LinkKey(Activation act, int synapseId) {
        this(act.getNeuron().getId(), act.getId(), synapseId);
    }

    public LinkKey(long nId, Integer actId, int synapseId) {
        this.nId = nId;
        this.actId = actId;
        this.synapseId = synapseId;
    }


    public static LinkKey getFromLinkKey(long nId) {
        return new LinkKey(nId, null, Integer.MIN_VALUE);
    }

    public static LinkKey getToLinkKey(long nId) {
        return new LinkKey(nId, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public int compareTo(LinkKey lk) {
        int r = Long.compare(nId, lk.nId);
        if(r != 0)
            return r;

        r = compareActId(lk);
        if(r != 0)
            return r;

        return Integer.compare(synapseId, lk.synapseId);
    }

    private int compareActId(LinkKey lk) {
        if(actId == lk.actId)
            return 0;

        if(actId == null)
            return -1;

        if(lk.actId == null)
            return 1;

        return Integer.compare(actId, lk.actId);
    }

    public String toString() {
        return "[" + nId + "]:" + actId + ":" + synapseId;
    }
}
