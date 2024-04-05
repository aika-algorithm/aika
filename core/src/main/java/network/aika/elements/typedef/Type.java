package network.aika.elements.typedef;

import network.aika.fields.FieldObject;

public interface Type<D extends TypeDefinition<D, T>, T extends Type<D, T> & FieldObject<T, D>> extends FieldObject<T, D> {

    void setTypeDefinition(D typeDef);
}
