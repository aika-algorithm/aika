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
package network.aika.suspension;

import java.util.Objects;

/**
 *
 * @author Lukas Molzberger
 */
public class LabelKey implements Comparable<LabelKey> {

    private String label;
    private long templateId;

    public LabelKey(String label, long templateId) {
        this.templateId = templateId;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public long getTemplateId() {
        return templateId;
    }

    @Override
    public int compareTo(LabelKey lk) {
        int r = label.compareTo(lk.label);
        if(r != 0)
            return r;
        return Long.compare(templateId, lk.templateId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelKey labelKey = (LabelKey) o;
        return templateId == labelKey.templateId && Objects.equals(label, labelKey.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, templateId);
    }

    @Override
    public String toString() {
        return templateId + ":" + label;
    }
}
