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
package network.aika.fields.softmax;

import network.aika.type.Obj;
import network.aika.type.ObjImpl;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;

import static network.aika.fields.softmax.SoftmaxInputType.CORRESPONDING_OUTPUT_LINK;
import static network.aika.fields.softmax.SoftmaxInputType.INPUT_TO_NORM;
import static network.aika.fields.softmax.SoftmaxOutputType.CORRESPONDING_INPUT_LINK;
import static network.aika.fields.softmax.SoftmaxOutputType.OUTPUT_TO_NORM;

/**
 *
 * @author Lukas Molzberger
 */
public class SoftmaxOutputObj extends ObjImpl {

    SoftmaxNormObj normObj;
    Integer bsId;

    public SoftmaxOutputObj(SoftmaxOutputType type, Integer bsId) {
        super(type);
        this.bsId = bsId;
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        if(rel == OUTPUT_TO_NORM)
            return getNormObj();
        else if(rel == CORRESPONDING_INPUT_LINK)
            return getCorrespondingInputLink();
        else
            throw new RuntimeException("Invalid Relation");
    }

    public SoftmaxNormObj getNormObj() {
        return normObj;
    }

    public static void linkObjects(SoftmaxNormObj normObj, SoftmaxOutputObj[] outputObjs) {
        for(int i = 0; i < outputObjs.length; i++) {
            SoftmaxOutputObj outputObj = outputObjs[i];
            normObj.outputs.add(outputObj);
            outputObj.normObj = normObj;
        }
    }

    public SoftmaxInputObj getCorrespondingInputLink() {
        if(normObj == null)
            return null;

        return normObj.getInput(bsId);
    }
}
