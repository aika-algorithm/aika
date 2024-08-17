package network.aika.elements.typedef;

import network.aika.fielddefs.FieldTag;

public enum FieldTags implements FieldTag {
    NEGATION,
    NET,
    FIRED,
    VALUE,
    BIAS,
    WEIGHT,
    INPUT_VALUE,
    INPUT_IS_FIRED,
    NEG_INPUT_IS_FIRED,
    WEIGHTED_INPUT,
    INPUT_SLOT,
    OUTPUT_SLOT,
    INITIAL_CATEGORY_SYNAPSE_WEIGHT,
    NET_OUTER_GRADIENT,
    GRADIENT,
    UPDATE_VALUE,
    NEG_UPDATE_VALUE,
    NEGATIVE_WEIGHT,
    WEIGHT_UPDATE,
    ACT_GRADIENT,
    AVERAGE_COVERED_SPACE,
    STATISTIC;

    @Override
    public Integer getId() {
        return ordinal();
    }
}
