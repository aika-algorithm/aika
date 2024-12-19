package network.aika.debug;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.typedefs.ActivationDefinition;
import network.aika.typedefs.LinkDefinition;
import network.aika.typedefs.NeuronDefinition;
import network.aika.typedefs.SynapseDefinition;

import java.util.stream.Collectors;

public class DumpTypes {

    public static String dumpModel(TypeRegistry model) {
        StringBuilder sb = new StringBuilder();
        model.getTypes().forEach(t ->
                dumpType(sb, t)
        );

        return sb.toString();
    }

    public static void dumpTypeDetails(StringBuilder sb, NeuronDefinition neuronDef) {
        sb.append("  activation: " + neuronDef.getActivation().getName() + "\n");
    }

    public static void dumpTypeDetails(StringBuilder sb, ActivationDefinition activationDefinition) {
        sb.append("  neuron: " + activationDefinition.getNeuron().getName() + "\n");
    }

    public static void dumpTypeDetails(StringBuilder sb, SynapseDefinition synapseDefinition) {
        sb.append("  synapse: " + synapseDefinition.getLink().getName() + "\n");

        if(synapseDefinition.getInput() != null)
            sb.append("  input: " + synapseDefinition.getInput().getName() + "\n");

        if(synapseDefinition.getOutput() != null)
            sb.append("  output: " + synapseDefinition.getOutput().getName() + "\n");
    }

    public static void dumpTypeDetails(StringBuilder sb, LinkDefinition linkDefinition) {
        sb.append("  synapse: " + linkDefinition.getSynapse().getName() + "\n");

        if(linkDefinition.getInput() != null)
            sb.append("  input: " + linkDefinition.getInput().getName() + "\n");

        if(linkDefinition.getOutput() != null)
            sb.append("  output: " + linkDefinition.getOutput().getName() + "\n");
    }

    public static void dumpFields(StringBuilder sb, Type<?, ?> type) {
        if(type.getFieldDefinitions().isEmpty())
            return;

        sb.append("  fields:\n");
        type.getFieldDefinitions()
                .forEach(fd ->
                    dumpInputFieldLinks(sb, fd)
                );
    }

    private static void dumpInputFieldLinks(StringBuilder sb, FieldDefinition<?, ?> fd) {
        if(fd.getInputs().findAny().isEmpty())
            return;

        sb.append(
                fd.getInputs()
                        .map(fl -> "      " + fl)
                        .collect(Collectors.joining("\n"))
        );
        sb.append("\n");
    }

    public static void dumpType(StringBuilder sb, Type<?, ?> type) {
        sb.append(type.getName() + "\n");
        sb.append("  class: " + type.getClazz().getSimpleName() + "\n");

        if(!type.getParents().isEmpty()) {
            sb.append("  parents: " + type.getParents().stream()
                    .map(Type::getName)
                    .collect(Collectors.joining(", ")) +
                    "\n"
            );
        }

        if(type instanceof NeuronDefinition) {
            dumpTypeDetails(sb, (NeuronDefinition) type);
        } else if(type instanceof ActivationDefinition) {
            dumpTypeDetails(sb, (ActivationDefinition) type);
        } else if(type instanceof SynapseDefinition) {
            dumpTypeDetails(sb, (SynapseDefinition) type);
        } else if(type instanceof LinkDefinition) {
            dumpTypeDetails(sb, (LinkDefinition) type);
        }

        dumpFields(sb, type);
        sb.append("\n");
    }
}
