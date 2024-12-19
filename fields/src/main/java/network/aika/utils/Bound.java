package network.aika.utils;

import org.apache.commons.math3.distribution.BetaDistribution;

public enum Bound {
    UPPER,
    LOWER;

    public static double BETA_THRESHOLD = 0.95;

    public double probability(double f, double n) {
        assert n > 0.0 && Double.isFinite(n);

        BetaDistribution dist;

        if(this == UPPER) {
            dist = new BetaDistribution(
                    f + 1,
                    (n - f) + 1
            );
        } else {
            dist = new BetaDistribution(
                    (n - f) + 1,
                    f + 1
            );
        }

        double p = dist.inverseCumulativeProbability(
                BETA_THRESHOLD
        );

        return this == UPPER ? p : 1.0 - p;
    }
}
