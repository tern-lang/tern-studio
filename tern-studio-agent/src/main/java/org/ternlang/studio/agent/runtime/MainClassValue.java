package org.ternlang.studio.agent.runtime;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.MAIN_CLASS;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class MainClassValue extends ManifestValue {

    public static final String APPLICATION_CLASS = "Main-Class";

    @Override
    public String getName() {
        return MAIN_CLASS.name;
    }

    @Override
    public String getValue() {
        Attributes.Name key = new Attributes.Name(APPLICATION_CLASS);
        Manifest manifest = getManifest();

        return (String)manifest.getMainAttributes().get(key);
    }
}
