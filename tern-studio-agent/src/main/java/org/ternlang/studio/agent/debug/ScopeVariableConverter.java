package org.ternlang.studio.agent.debug;

import org.ternlang.agent.message.common.Variable;
import org.ternlang.agent.message.common.VariableArray;
import org.ternlang.agent.message.common.VariableArrayBuilder;
import org.ternlang.agent.message.common.VariableAttribute;
import org.ternlang.agent.message.common.VariableAttributeArray;
import org.ternlang.agent.message.common.VariableBuilder;
import org.ternlang.agent.message.common.VariableTree;
import org.ternlang.agent.message.common.VariableTreeCodec;
import org.ternlang.message.ByteArrayFrame;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ScopeVariableConverter {

    public static ScopeVariableTree convert(VariableTree variableTree) {
        Map<String, Map<String, String>> evaluation = new LinkedHashMap<String, Map<String, String>>();
        Map<String, Map<String, String>> local = new LinkedHashMap<String, Map<String, String>>();

        convert(evaluation, variableTree.local());
        convert(local, variableTree.evaluation());

        return new ScopeVariableTree.Builder(variableTree.change())
                .withEvaluation(evaluation)
                .withLocal(local)
                .build();
    }

    private static void convert(Map<String, Map<String, String>> variables, VariableArray data) {
        for(Variable local : data) {
            String path = local.path().path().toString();
            VariableAttributeArray attributes = local.attributes();
            int length = attributes.length();

            if (length > 0) {
                Map<String, String> container = new LinkedHashMap<String, String>();

                for (VariableAttribute attribute : attributes) {
                    String name = attribute.name().toString();
                    String value = attribute.value().toString();

                    container.put(name, value);
                }
                variables.put(path, container);
            }
        }
    }

    public static VariableTree convert(ScopeVariableTree variableTree) {
        ByteArrayFrame frame = new ByteArrayFrame();
        VariableTreeCodec codec = new VariableTreeCodec();

        codec.with(frame, 0, Integer.MAX_VALUE);
        frame.setCount(8);

        convert(codec.local(), variableTree.getLocal().entrySet());
        convert(codec.local(), variableTree.getEvaluation().entrySet());

        return codec.change(variableTree.getChange());
    }

    private static void convert(VariableArrayBuilder variables, Set<Map.Entry<String, Map<String, String>>> data) {
        for(Map.Entry<String, Map<String, String>> local : data) {
            Set<Map.Entry<String, String>> attributes = local.getValue().entrySet();
            VariableBuilder variable = variables.add();
            String path = local.getKey();

            variable.path().path(path);

            for(Map.Entry<String, String> attribute : attributes) {
                variable.attributes()
                        .add()
                        .name(attribute.getKey())
                        .value(attribute.getValue());
            }
        }
    }
}
