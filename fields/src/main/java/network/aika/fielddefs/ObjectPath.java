package network.aika.fielddefs;

import network.aika.enums.Direction;
import network.aika.fields.FieldObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectPath {

    private Direction direction;

    private List<ObjectRelationDefinition> path = new ArrayList<>();

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

    public FieldObject resolve(FieldObject fo) {
        for(ObjectRelationDefinition e: path) {
            fo = e.followRelation(fo);
        }
        return fo;
    }
}
