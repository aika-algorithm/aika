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
package network.aika.debugger.activations.layout;

import network.aika.debugger.Edge;
import network.aika.debugger.activations.ActivationGraphManager;
import network.aika.elements.links.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Lukas Molzberger
 */
public class LinkEdge<L extends Link> extends Edge<L> {

    boolean initialized;

    protected static Map<Class<? extends Link>, Class<? extends LinkEdge>> typeMap = new HashMap<>();

    static {
        typeMap.put(InputPatternLink.class, BindingLinkEdge.class);
        typeMap.put(BindingCategoryInputLink.class, BindingLinkEdge.class);
        typeMap.put(PositiveFeedbackLink.class, BindingLinkEdge.class);
        typeMap.put(NegativeFeedbackLink.class, BindingLinkEdge.class);
        typeMap.put(SamePatternLink.class, BindingLinkEdge.class);
        typeMap.put(RelationInputLink.class, BindingLinkEdge.class);
        typeMap.put(ReversePatternLink.class, BindingLinkEdge.class);
        typeMap.put(PatternLink.class, PatternLinkEdge.class);
        typeMap.put(PatternCategoryInputLink.class, PatternCategoryInputLinkEdge.class);
        typeMap.put(InhibitoryLink.class, InhibitoryLinkEdge.class);
        typeMap.put(InhibitoryCategoryInputLink.class, InhibitoryLinkEdge.class);
        typeMap.put(BindingCategoryLink.class, LinkEdge.class);
        typeMap.put(PatternCategoryLink.class, LinkEdge.class);
        typeMap.put(InhibitoryCategoryLink.class, LinkEdge.class);
    }

    public static LinkEdge create(Link l) {
        Class<? extends LinkEdge> clazz = typeMap.get(l.getClass());

        try {
            return clazz.getDeclaredConstructor(Link.class, ActivationGraphManager.class)
                    .newInstance(l);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public LinkEdge(L l) {
        super(l);
    }

/*
    public void calculateForce(Vector3 delta, Point3 pos, Direction dir, ActivationParticle other) {
        double targetDistance = getInitialYDistance();

        Point3 opos = other.getPosition();
        double dy = 0.0;

        if(dir == INPUT) {
            dy = (opos.y + targetDistance) - pos.y;
            dy = Math.max(0.0, dy);
        } else if(dir == OUTPUT) {
            dy = opos.y - (pos.y + targetDistance);
            dy = Math.min(0.0, dy);
        }

        delta.set(0.0, dy, 0.0);
    }
*/
 /*   @Override
    public void processLayout() {
        Integer tp = getTokenPos();
        if(tp == null)
            return;

        if (initialized)
            return;
        initialized = true;

        outputNode.setAttribute(
                "x",
                STANDARD_DISTANCE_X * tp
        );
    }
*/
    private Integer getTokenPos() {
        Integer tp = getElement().getInput().getTokenPos();
        return tp != null ?
                tp :
                getElement().getOutput().getTokenPos();
    }
}
