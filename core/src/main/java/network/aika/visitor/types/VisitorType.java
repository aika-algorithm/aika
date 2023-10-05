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
package network.aika.visitor.types;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.visitor.Visitor;

import static network.aika.enums.Scope.INPUT;
import static network.aika.enums.Scope.SAME;

/**
 * @author Lukas Molzberger
 */
public interface VisitorType {

    VisitorType PATTERN_VISITOR_TYPE = new PatternVisitor();
    VisitorType PATTERN_CAT_VISITOR_TYPE = new PatternCategoryVisitor();
    VisitorType BINDING_VISITOR_TYPE = new BindingVisitor();
    VisitorType INHIB_VISITOR_TYPE_SAME = new InhibitoryVisitor(SAME);
    VisitorType INHIB_VISITOR_TYPE_INPUT = new InhibitoryVisitor(INPUT);


    static VisitorType getInhibitoryVisitorType(Scope s) {
        return s == SAME ? INHIB_VISITOR_TYPE_SAME : INHIB_VISITOR_TYPE_INPUT;
    }

    void visit(Visitor v, Link l, int depth);

    void visit(Visitor v, Activation act, Link l, int depth);

    Scope getIdentityRef();
}
