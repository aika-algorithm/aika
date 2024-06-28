package network.aika.fielddefs;

import network.aika.enums.Direction;
import network.aika.fields.FieldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Path {

    private Direction direction;

    private List<FieldObjectRelationDefinition> path = new ArrayList<>();

    public Path(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public FieldObjectDefinition getFromObject() {
        return path.getFirst().getRelatedObject();
    }

    public FieldObjectDefinition getToObject() {
        return path.getLast().getRelatedObject();
    }

    public void add(FieldObjectRelationDefinition rel) {
        path.add(rel);
    }

    public FieldObject resolve(FieldObject fo) {
        for(FieldObjectRelationDefinition e: path) {
            fo = e.followRelation(fo);
        }
        return fo;
    }
}
