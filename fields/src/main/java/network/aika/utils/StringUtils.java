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
package network.aika.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static network.aika.queue.keys.QueueKey.MAX_ROUND;

/**
 *
 * @author Lukas Molzberger
 */
public class StringUtils {

    public static double TOLERANCE = 0.001;


    public static String doubleToString(Double x) {
        if(x == null)
            return "--";
        return doubleToString(x, "#.######");
    }

    public static String floatToString(Float d, String format) {
        if(d == null)
            return "--";
        DecimalFormat formatter = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode( RoundingMode.DOWN );
        return formatter.format(d);
    }

    public static String doubleToString(Double d, String format) {
        if(d == null)
            return "--";
        DecimalFormat formatter = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode( RoundingMode.DOWN );
        return formatter.format(d);
    }

    public static String roundToString(int r) {
        return r == MAX_ROUND ? "MAX" : "" + r;
    }

    public static String depthToSpace(int depth) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < depth; i++)
            sb.append(" ");
        return sb.toString();
    }
}
