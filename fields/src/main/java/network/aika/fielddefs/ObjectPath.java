package network.aika.fielddefs;

import network.aika.enums.Direction;
import network.aika.fields.Obj;

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

    public Type getFromObject() {
        return path.getFirst().getRelatedObject();
    }

    public Type getToObject() {
        return path.getLast().getRelatedObject();
    }

    public void add(ObjectRelationDefinition rel) {
        path.add(rel);
    }

    public List<Obj> resolve(Obj startObject) {
        List<Obj> current = List.of(startObject);
        for(ObjectRelationDefinition e: path) {

            current = current.stream()
                    .flatMap(o ->
                            e.followRelation(o).stream()
                    )
                    .toList();
        }

        return current;
    }
}
