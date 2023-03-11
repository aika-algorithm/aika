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

import network.aika.elements.activations.TokenActivation;



/**
 * @author Lukas Molzberger
 */
public class TokenActivationNode extends PatternActivationNode<TokenActivation> {

    public TokenActivationNode(TokenActivation act) {
        super(act);
    }

/*    @Override
    public void update(LayoutState ls, EventType et) {
//        if(!act.getNeuron().isAbstract())
//            node.setAttribute("layout.frozen");

        Double x = getActivationXCoordinate(ls.getLastInputAct());
        if(x == null) {
            x = ls.getLastInputActXPos();
        }

        if(x != null) {
            x += STANDARD_DISTANCE_X;
        } else {
            x = 0.0;
        }

//        node.setAttribute("x", x);

        ls.setLastInputActXPos(x);
        ls.setLastInputAct(act);
    }
*/
    /*
    private Double getActivationXCoordinate(Activation act) {
        if(act == null)
            return null;

        ActivationParticle originParticle = graphManager.getParticle(act.getId());
        if(originParticle == null)
            return null;

        return originParticle.getPosition().x;
    }*/
}
