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
package network.aika.debugger;


import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;

/**
 * @author Lukas Molzberger
 */
public class TokenRange {

    private int begin;

    private int end;

    public TokenRange(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public boolean within(Integer tokenPos) {
        return begin <= tokenPos && tokenPos < end;
    }

    public static boolean within(TokenRange tokenRange, Activation act) {
        if(tokenRange == null)
            return true;

        return tokenRange.within(act.getTokenPosRange());
    }

    public static boolean within(TokenRange tokenRange, Link l) {
        if(tokenRange == null)
            return true;

        return tokenRange.within(l.getInput().getTokenPosRange()) &&
                tokenRange.within(l.getOutput().getTokenPosRange());
    }
}
