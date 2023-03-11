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
package network.aika.debugger.activations.particles;

import network.aika.debugger.Node;
import network.aika.elements.activations.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukas Molzberger
 */
public class ActivationNode<E extends Activation> extends Node<E> {

    protected static Map<Class<? extends Activation>, Class<? extends ActivationNode>> typeMap = new HashMap<>();

    static {
        typeMap.put(BindingActivation.class, BindingActivationNode.class);
        typeMap.put(PatternActivation.class, PatternActivationNode.class);
        typeMap.put(TokenActivation.class, TokenActivationNode.class);
        typeMap.put(LatentRelationActivation.class, LatentRelationActivationNode.class);
        typeMap.put(InhibitoryActivation.class, ActivationNode.class);
        typeMap.put(CategoryActivation.class, ActivationNode.class);
    }

    public ActivationNode(E act) {
        super(act);
    }

    public static ActivationNode create(Activation act) {
        Class<? extends ActivationNode> clazz = typeMap.get(act.getClass());

        try {
            return clazz.getDeclaredConstructor(Activation.class)
                    .newInstance(act);

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


    /*
    private String getActivationStrokeColor(Activation act) {
        if(act.isFired())
            return "stroke-color: black;";

        return "stroke-color: rgb(200, 200, 200);";
    }
*/

    /*
    @Override
    protected void attraction(Vector3 delta) {
        SpringBox box = (SpringBox) this.box;
        Energies energies = box.getEnergies();

        computeTargetAttraction(delta, energies);
        computeEdgeAttraction(delta, energies);
    }
*/
    /*
    private void computeTargetAttraction(Vector3 delta, Energies energies) {
        if(targetX != null || targetY != null) {
            delta.set(
                    targetX != null ? targetX - pos.x : 0.0,
                    targetY != null ? targetY - pos.y : 0.0,
                    0.0
            );

            disp.add(delta);
            attE += K1;
            energies.accumulateEnergy(K1);
        }
    }*/


/*
    private void computeEdgeAttraction(Vector3 delta, Energies energies) {
        for (EdgeSpring edge : neighbours) {
            if (!edge.ignored) {
                ActivationParticle other = (ActivationParticle) edge.getOpposite(this);

                Link link = getLink(other.act, act);
                if(link == null)
                    continue;

                Direction dir = getDirection(link);

                if(dir == OUTPUT) // Apply forces only in one direction
                    continue;

                ParticleLink pl = getParticleLink(link, dir);
                if(pl == null)
                    continue;

                pl.calculateForce(delta, pos, dir, other);

                delta.mult(new Vector2(0.0, K1));

                disp.add(delta);
                attE += K1;
                energies.accumulateEnergy(K1);
            }
        }
    }
*/
}
