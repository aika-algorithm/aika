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
package network.aika.meta;

import network.aika.Context;
import network.aika.text.TextReference;

import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class TestContext implements Context {

    String textSectionType;
    List<TextReference> candidateRanges;

    public TestContext(String textSectionType, List<TextReference> candidateRanges) {
        this.textSectionType = textSectionType;
        this.candidateRanges = candidateRanges;
    }

    public String getTextSectionType() {
        return textSectionType;
    }

    public List<TextReference> getCandidateRanges() {
        return candidateRanges;
    }
}
