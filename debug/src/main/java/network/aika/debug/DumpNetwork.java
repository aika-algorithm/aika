package network.aika.debug;

import network.aika.Document;
import network.aika.activations.Activation;
import network.aika.activations.Link;
import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FieldLinkDefinition;
import network.aika.fields.field.Field;
import network.aika.type.ObjImpl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.aika.utils.StringUtils.depthToSpace;

@SuppressWarnings("rawtypes")
public class DumpNetwork {

    public static String dumpActivations(OutputConfig oc, Document doc) {
        StringBuilder sb = new StringBuilder();
        doc.getActivations().forEach(act ->
                sb.append(dumpActivation(oc, act, 2))
        );
        return sb.toString();
    }

    public static String dumpActivation(OutputConfig oc, Activation act, int depth) {
        StringBuilder sb = new StringBuilder();
        String space = depthToSpace(depth);
        sb.append(space).append(act.toString()).append("\n");
        if(act.getParent() != null) {
            sb.append(space).append("  Parent: ").append(act.getParent()).append("\n");
        }
        sb.append(dumpBindingSignals(act, depth));
        if(oc.isShowActivationFields())
            sb.append(dumpFields(oc, act, depth + 2)).append("\n");

        if(oc.isShowInputLinks())
            sb.append(dumpInputLinks(oc, act, depth)).append("\n");

        return sb.toString();
    }

    public static String dumpBindingSignals(Activation act, int depth) {
        String bsStr = act.getBindingSignals().entrySet().stream()
                .map(e ->
                        depthToSpace(depth + 2) + "Binding-Signal: " + e.getKey() + " = " + e.getValue()
                )
                .collect(Collectors.joining("\n"));
        return !bsStr.isBlank() ? bsStr + "\n" : "";
    }

    public static String dumpInputLinks(OutputConfig oc, Activation act, int depth) {
        String linksStr = act.getInputLinks()
                .map(l ->
                        dumpLink(oc, l, depth + 2)
                )
                .collect(Collectors.joining("\n"));
        return !linksStr.isBlank() ? linksStr + "\n" : "";
    }

    public static String dumpLink(OutputConfig oc, Link l, int depth) {
        return depthToSpace(depth) + l + "\n" +
                (oc.isShowLinkFields() ? dumpFields(oc, l, depth + 2) : "");
    }

    public static String dumpFields(OutputConfig oc, ObjImpl obj, int depth) {
        Stream<Field> fields = obj.getFields();
        return fields
                .map(f -> dumpField(oc, f, depth))
                .collect(Collectors.joining("\n"));
    }

    public static String dumpField(OutputConfig oc, Field f, int depth) {
        return depthToSpace(depth) + f +
                dumpFieldLinks(oc, f, depth + 2);
    }

    public static String dumpFieldLinks(OutputConfig oc, Field f, int depth) {
        FieldDefinition<?, ?> fd = f.getFieldDefinition();
        if(f.getFieldDefinition().getInputs().findAny().isEmpty())
            return "";

        return fd.getInputs().map(fl ->
                        dumpFieldLink(oc, fl, depth)
                )
                .collect(Collectors.joining("\n", "\n", ""));
    }

    public static String dumpFieldLink(OutputConfig oc, FieldLinkDefinition fl, int depth) {
        return depthToSpace(depth) + fl.toString();
    }
}
