package network.aika.fielddefs;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private List<FieldObjectDefinition> path = new ArrayList<>();

    public FieldObjectDefinition getFromObject() {
        return path.getFirst();
    }

    public FieldObjectDefinition getToObject() {
        return path.getLast();
    }

    public void add(FieldObjectDefinition o) {
        path.add(o);
    }
}
