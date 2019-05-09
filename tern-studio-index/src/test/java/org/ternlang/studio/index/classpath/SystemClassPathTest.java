package org.ternlang.studio.index.classpath;

import junit.framework.TestCase;
import org.ternlang.studio.index.IndexNode;

import java.util.Map;

public class SystemClassPathTest extends TestCase {

    public void testSystemClassPath() throws Exception {
        Map<String, IndexNode> nodes = SystemClassPath.getDefaultNodesByName();
        IndexNode node = nodes.get("StringBuilder");

        assertNotNull(node);
        assertNotNull(node.getResource());
        assertNotNull(node.getAbsolutePath());

        System.err.println(node.getResource());
        System.err.println(node.getAbsolutePath());
    }
}
