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
package network.aika.misc.utils;


import network.aika.activations.Activation;

/**
 *
 * @author Lukas Molzberger
 */
public class Utils {

    public static double TOLERANCE = 0.001;

    public static double[] add(double[] a, double[] b) {
        if(a == null)
            return b;
        if(b == null)
            return a;

        double[] r = new double[a.length];
        for(int i = 0; i < r.length; i++)
            r[i] = a[i] + b[i];
        return r;
    }

    public static double[] scale(double[] a, double s) {
        double[] r = new double[a.length];
        for(int i = 0; i < r.length; i++)
            r[i] = a[i] * s;
        return r;
    }

    public static double sum(double[] a) {
        double sum = 0;
        for(int i = 0; i < a.length; i++)
            sum += a[i];
        return sum;
    }

    public static boolean belowTolerance(Double tolerance, double[] x) {
        if(x == null)
            return true;

        if(tolerance == null)
            return false;

        return Math.abs(sum(x)) < tolerance;
    }

    public static boolean belowTolerance(Double tolerance, double x) {
        if(x == 0.0)
            return true;

        if(tolerance == null)
            return false;

        return Math.abs(x) < tolerance;
    }

    public static String depthToSpace(int depth) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < depth; i++)
            sb.append(" ");
        return sb.toString();
    }

    public static String idToString(Activation act) {
        return act != null ? "" + act.getId() : "--";
    }
}
