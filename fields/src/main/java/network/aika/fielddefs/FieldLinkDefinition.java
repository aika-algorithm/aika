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
package network.aika.fielddefs;


import network.aika.enums.Direction;

/**
 * @author Lukas Molzberger
 */
public class FieldLinkDefinition {

    private Path objectPath;

    private Integer port;

    private Integer arg;

    private FieldOutputDefinition in;

    private FieldInputDefinition out;

    boolean propagateUpdates;

    public FieldLinkDefinition(Path path, Integer port, FieldOutputDefinition in, Integer arg, FieldInputDefinition out, boolean propagateUpdates) {
        this.objectPath = path;
        this.port = port;
        this.arg = arg;
        this.in = in;
        this.out = out;
        this.propagateUpdates = propagateUpdates;
    }

    public Path getObjectPath() {
        return objectPath;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getArg() {
        return arg;
    }

    public FieldOutputDefinition getIn() {
        return in;
    }

    public FieldInputDefinition getOut() {
        return out;
    }

    public boolean isPropagateUpdates() {
        return propagateUpdates;
    }
}
