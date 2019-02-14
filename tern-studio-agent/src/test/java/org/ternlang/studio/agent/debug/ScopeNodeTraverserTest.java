package org.ternlang.studio.agent.debug;

import static org.ternlang.core.ModifierType.CLASS;
import static org.ternlang.studio.agent.debug.ValueData.DEPTH_KEY;
import static org.ternlang.studio.agent.debug.ValueData.EXPANDABLE_KEY;
import static org.ternlang.studio.agent.debug.ValueData.NAME_KEY;
import static org.ternlang.studio.agent.debug.ValueData.TYPE_KEY;
import static org.ternlang.studio.agent.debug.ValueData.VALUE_KEY;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.ternlang.common.store.ClassPathStore;
import org.ternlang.compile.StoreContext;
import org.ternlang.core.Context;
import org.ternlang.core.function.AccessorProperty;
import org.ternlang.core.property.Property;
import org.ternlang.core.scope.MapModel;
import org.ternlang.core.scope.Model;
import org.ternlang.core.scope.ModelScope;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.scope.instance.Instance;
import org.ternlang.core.scope.instance.PrimitiveInstance;
import org.ternlang.core.type.Type;
import org.ternlang.core.type.index.ScopeType;
import org.ternlang.core.variable.Reference;

public class ScopeNodeTraverserTest extends TestCase {
   
   private static class ExampleClass1 {
      private final ExampleClass2 example;
      private final String text;
      private final String[] array;
      private final int value;
      public ExampleClass1(ExampleClass2 example, String text, String[] array, int value) {
         this.example = example;
         this.text = text;
         this.array = array;
         this.value = value;
      }
   }
   
   private static class ExampleClass2 {
      public static final String SOME_STATIC_FIELD = "BLAH_BLAH";
      private final double[][] list;
      private final String value;
      public ExampleClass2(double[][] list, String value) {
         this.list = list;
         this.value = value;
      }
   }
   
   public void testScopeNode() throws Exception {
      Map<String, Object> rootValues = new HashMap<String, Object>();
      Map<String, Object> instanceValues1 = new HashMap<String, Object>();
      Map<String, Object> instanceValues2 = new HashMap<String, Object>();
      Map<String, Object> instanceValues3 = new HashMap<String, Object>();
      
      rootValues.put("i", 12);
      rootValues.put("text", "some text");
      rootValues.put("STATIC_VARIABLE", 44d);
      
      instanceValues1.put("name", "John Doe");
      instanceValues1.put("address", "Some Street");
      instanceValues1.put("age", 34);
      
      Instance instance1 = createInstanceScope(instanceValues1, "Person");
      
      instanceValues2.put("companyName", "Some Company");
      instanceValues2.put("companyAddress", "12 Fleet Street, London, UK");
      instanceValues2.put("companyOwner", instance1);
      
      Instance instance2 = createInstanceScope(instanceValues2, "Company");
      
      instanceValues3.put("invoiceReference", "234437145B");
      instanceValues3.put("invoiceAmount", 12355.55d);
      instanceValues3.put("invoiceCompany", instance2);
      
      ExampleClass2 example2 = new ExampleClass2(new double[][]{{33.d, 44.d}, {121.112d, 132.5d}}, "example2");
      ExampleClass1 example1 = new ExampleClass1(example2, "example1", new String[]{"arrayValue1", "arrayValue2", "arrayValue3"}, 44);
      
      instanceValues3.put("invoiceDetails", example1);
      
      Instance instance3 = createInstanceScope(instanceValues3, "CompanyInvoice");
      
      rootValues.put("invoice", instance3);
      rootValues.put("example1", example1);
      rootValues.put("example2", example2);
      
      Scope root = createRootScope(rootValues);
      
      ClassPathStore store = new ClassPathStore();
      Context context = new StoreContext(store);
      ScopeNodeTraverser traverser = new ScopeNodeTraverser(context, root);
      TreeSet<String> expand = new TreeSet<String>();
      
      expand.add("invoice.invoiceCompany.invoiceDetails.*");
      expand.add("example1.*");
      expand.add("example1.example.*");
      expand.add("example1.example.list.*");
      
      Map<String, Map<String, String>> variables = traverser.expand(expand);
      Set<String> keys = variables.keySet();
      SortedSet<String> sorted = new TreeSet<String>(keys);
      
      for(String key : sorted) {
         Map<String, String> value = variables.get(key);
         System.err.println("key=["+key+"] value=["+
               value.get(VALUE_KEY)+
               "] type=["+
               value.get(TYPE_KEY)+
               "] expandable=["+
               value.get(EXPANDABLE_KEY)+
               "] name=[" +
               value.get(NAME_KEY)+
               "] depth=[" +
               value.get(DEPTH_KEY)+
               "]");
      }
      
   }
   
   private static Scope createRootScope(Map<String, Object> values) {
      Model model = new MapModel(Collections.EMPTY_MAP);
      Scope scope = new ModelScope(model, null);
      ScopeState state = scope.getState();
      Set<String> keys = values.keySet();
      
      for(String key : keys) {
         Object value = values.get(key);
         Reference reference = new Reference(value);
         state.addValue(key, reference);
      }
      return scope;
   }
   
   private static Instance createInstanceScope(Map<String, Object> values, String name) {
      Model model = new MapModel(Collections.EMPTY_MAP);
      Scope scope = new ModelScope(model, null);
      Type type = new ScopeType(null, null, name, CLASS.mask, 0);
      Instance instance = new PrimitiveInstance(null, scope, type, type);
      List<Property> properties = type.getProperties();
      ScopeState state = instance.getState();
      Set<String> keys = values.keySet();
      
      for(String key : keys) {
         Object value = values.get(key);
         Property property = new AccessorProperty(key, key, null, null, null, 0);
         Reference reference = new Reference(value);
         properties.add(property);
         state.addValue(key, reference);
      }
      return instance;
   }
}
