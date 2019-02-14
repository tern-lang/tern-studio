package org.ternlang.studio.project.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;

public class DependencyComparatorTest extends TestCase {

   public void testDependencyComparator() throws Exception {
      DependencyComparator comparator = new DependencyComparator();
      
      assertTrue(comparator.compare(new MockDependency("a", "b", "1.1"), new MockDependency("a", "b", "1.1")) == 0);
      assertTrue(comparator.compare(new MockDependency("a", "b", "1.1"), new MockDependency("a", "b", "1.1.1")) < 0);
      assertTrue(comparator.compare(new MockDependency("a", "b", "1.1"), new MockDependency("a", "b", "1.1.2")) < 0);
      assertTrue(comparator.compare(new MockDependency("a", "b", "1.1.3"), new MockDependency("a", "b", "1.1.1")) > 0);
   }
   
   public void testVersionComparator() throws Exception {
      DependencyComparator comparator = new DependencyComparator();
      
      assertTrue(comparator.compareVersion("1.1", "1.1") == 0);
      assertTrue(comparator.compareVersion("1.1", "1.0.0") > 0);
      assertTrue(comparator.compareVersion("1.1", "1.2.1") < 0);
      assertTrue(comparator.compareVersion("2.1", "1.1") > 0);
      assertTrue(comparator.compareVersion("1.1.0", "1.1.0") == 0);
   }
   
   public void testSortDependencyList() throws Exception {
      DependencyComparator comparator = new DependencyComparator();
      List<Dependency> list = new ArrayList<Dependency>();
      
      list.add(new MockDependency("a", "b", "1.1"));
      list.add(new MockDependency("a", "b", "1.1"));
      list.add(new MockDependency("a", "b", "1.1.0"));
      list.add(new MockDependency("a", "b", "3.1.2"));
      list.add(new MockDependency("a", "b", "4.0"));
      list.add(new MockDependency("a", "b", "1.1.14"));
      
      Collections.sort(list, comparator);
      
      assertEquals(list.get(0).getVersion(), "1.1");
      assertEquals(list.get(1).getVersion(), "1.1");
      assertEquals(list.get(2).getVersion(), "1.1.0");
      assertEquals(list.get(3).getVersion(), "1.1.14");
      assertEquals(list.get(4).getVersion(), "3.1.2");
      assertEquals(list.get(5).getVersion(), "4.0");
      
   }
   
   public void testReverseSortDependencyList() throws Exception {
      DependencyComparator comparator = new DependencyComparator(true);
      List<Dependency> list = new ArrayList<Dependency>();
      
      list.add(new MockDependency("a", "b", "1.1"));
      list.add(new MockDependency("a", "b", "1.1"));
      list.add(new MockDependency("a", "b", "1.1.0"));
      list.add(new MockDependency("a", "b", "3.1.2"));
      list.add(new MockDependency("a", "b", "4.0"));
      list.add(new MockDependency("a", "b", "1.1.14"));
      
      Collections.sort(list, comparator);
      
      assertEquals(list.get(0).getVersion(), "4.0");
      assertEquals(list.get(1).getVersion(), "3.1.2");
      assertEquals(list.get(2).getVersion(), "1.1.14");
      assertEquals(list.get(3).getVersion(), "1.1.0");
      assertEquals(list.get(4).getVersion(), "1.1");
      assertEquals(list.get(5).getVersion(), "1.1");
   }
   
   @Data
   @AllArgsConstructor
   public static class MockDependency extends Dependency {
      
      private final String groupId;
      private final String artifactId;
      private final String version;
   }
}
