package network.aika.fielddefs;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private List<FieldObjectRelationDefinition> path = new ArrayList<>();

    public FieldObjectDefinition getFromObject() {
        return path.getFirst().getRelatedObject();
    }

    public FieldObjectDefinition getToObject() {
        return path.getLast().getRelatedObject();
    }

    public void add(Integer relId, FieldObjectDefinition o) {
        path.add(new FieldObjectRelationDefinition(relId, o));
    }
}
