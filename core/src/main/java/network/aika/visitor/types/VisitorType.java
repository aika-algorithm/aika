package network.aika.visitor.types;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.visitor.Visitor;

import static network.aika.enums.Scope.INPUT;
import static network.aika.enums.Scope.SAME;

public interface VisitorType {

    VisitorType PATTERN_VISITOR_TYPE = new PatternVisitor();

    VisitorType PATTERN_CAT_VISITOR_TYPE = new PatternCategoryVisitor();

    VisitorType BINDING_VISITOR_TYPE = new BindingVisitor();

    VisitorType INHIB_VISITOR_TYPE_SAME = new InhibitoryVisitor(SAME);
    VisitorType INHIB_VISITOR_TYPE_INPUT = new InhibitoryVisitor(INPUT);


    static VisitorType getInhibitoryVisitorType(Scope s) {
        return s == SAME ? INHIB_VISITOR_TYPE_SAME : INHIB_VISITOR_TYPE_INPUT;
    }

    void visit(Visitor v, Link l, int depth);

    void visit(Visitor v, Activation act, Link l, int depth);

    Scope getIdentityRef();

}
