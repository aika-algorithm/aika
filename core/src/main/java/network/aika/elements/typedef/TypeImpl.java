package network.aika.elements.typedef;

import network.aika.fields.FieldObject;
import network.aika.fields.FieldObjectImpl;


public abstract class TypeImpl<D extends TypeDefinition<D, T>, T extends Type<D, T> & FieldObject<T, D>> extends FieldObjectImpl<T, D> implements Type<D, T> {


}
