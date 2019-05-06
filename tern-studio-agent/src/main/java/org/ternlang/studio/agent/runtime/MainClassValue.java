package org.ternlang.studio.agent.runtime;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.MAIN_CLASS;

public class MainClassValue extends ManifestValue {

    public static final String APPLICATION_CLASS = "Main-Class";

    @Override
    public String getName() {
        return MAIN_CLASS.name;
    }

    @Override
    public String getValue() {
        Manifest manifest = getManifest();
        return getValue(manifest);
    }

    public static String getValue(Manifest manifest) {
        Attributes.Name key = new Attributes.Name(APPLICATION_CLASS);

        if(manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            return (String) attributes.get(key);
        }
        return null;
    }
}
