package network.aika.fielddefs;

import network.aika.enums.Direction;
import network.aika.fields.FieldObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectPath {

    private Direction direction;

    private ArrayList<ObjectRelationDefinition> path = new ArrayList<>();

    public ObjectPath(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public ObjectDefinition getFromObject() {
        return path.getFirst().getRelatedObject();
    }

    public ObjectDefinition getToObject() {
        return path.getLast().getRelatedObject();
    }

    public void add(ObjectRelationDefinition rel) {
        path.add(rel);
    }

    public List<FieldObject> resolve(FieldObject startObject) {
        List<FieldObject> current = List.of(startObject);
        for(ObjectRelationDefinition e: path) {

            current = current.stream()
                    .flatMap(o -> e.followRelation(o).stream())
                    .toList();
        }

        return current;
    }
}
