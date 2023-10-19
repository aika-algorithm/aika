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
package network.aika.text;

/**
 *
 * @author Lukas Molzberger
 */
public class TokenPositionKey implements Comparable<TokenPositionKey> {

    private long pos;
    private int actId;

    public TokenPositionKey(long pos, int actId) {
        this.pos = pos;
        this.actId = actId;
    }

    public long getPosition() {
        return pos;
    }

    public int getActId() {
        return actId;
    }

    @Override
    public int compareTo(TokenPositionKey k) {
        int r = Long.compare(pos, k.pos);
        if(r != 0) return r;
        return Integer.compare(actId, k.actId);
    }

    @Override
    public String toString() {
        return "Pos:" + pos + " Act-Id:" + actId;
    }
}
