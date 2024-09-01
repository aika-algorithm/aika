package network.aika.fielddefs;

import network.aika.enums.Direction;
import network.aika.fields.Obj;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static network.aika.fielddefs.ObjectRelationType.ONE_TO_MANY;
import static network.aika.fielddefs.ObjectRelationType.ONE_TO_ONE;

public class ObjectPath {

    private Direction direction;

    private ObjectRelationType relType = ONE_TO_ONE;

    private ArrayList<ObjectRelationDefinition> path = new ArrayList<>();

    public ObjectPath(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public Type getFromObject() {
        return path.getFirst().getToObject();
    }

    public Type getToObject() {
        return path.getLast().getToObject();
    }

    public ObjectRelationType getRelType() {
        return relType;
    }

    public void add(ObjectRelationDefinition rel) {
        if(rel.getRelationType() == ONE_TO_MANY)
            relType = ONE_TO_MANY;

        path.add(rel);
    }

    public ArrayList<ObjectRelationDefinition> getPath() {
        return path;
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

    public ObjectPath getReversed() {
        if(relType != ONE_TO_ONE)
            return null;

        ObjectPath invertedPath = new ObjectPath(direction.invert());
        for(ObjectRelationDefinition e: path.reversed()) {
            invertedPath.add(e.getReversed());
        }
        return invertedPath;
    }

    public String toString() {
        return "[" + path.stream()
                .map(ObjectRelationDefinition::toString)
                .collect(Collectors.joining(", ")) +
                "]";
    }
}
