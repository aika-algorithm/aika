package network.aika.fields.link;

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;

import java.util.Objects;


public class FixedFieldLink extends FieldLink {

    private int arg;

    public FixedFieldLink(FieldOutput input, int arg, FieldInput output) {
        super(input, output);

        this.arg = arg;
    }

    public int getArgument() {
        return arg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedFieldLink fieldLink = (FixedFieldLink) o;
        return arg == fieldLink.arg && Objects.equals(input, fieldLink.input) && Objects.equals(output, fieldLink.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, arg);
    }

    @Override
    public String toString() {
        return input + " --" + arg + "-->" + output;
    }
}
