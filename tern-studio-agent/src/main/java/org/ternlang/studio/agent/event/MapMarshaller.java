package org.ternlang.studio.agent.event;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MapMarshaller {
   
   public Map<String, Map<String, String>> readMap(DataInput input) throws IOException {
      Map<String, Map<String, String>> map = new TreeMap<String, Map<String, String>>();
      int count = input.readInt();
      
      for(int i = 0; i < count; i++) {
         Map<String, String> criteria = new HashMap<String, String>();
         String name = input.readUTF();
         int size = input.readInt();
         
         for(int j = 0; j < size; j++) {
            String key = input.readUTF();
            String value = input.readUTF();
            
            criteria.put(key, value);
         }
         map.put(name, criteria);
      }
      return map;
   }
   
   public void writeMap(DataOutput output, Map<String, Map<String, String>> map) throws IOException {
      Set<String> names = map.keySet();
      int count = map.size();
      
      output.writeInt(count);
      
      for(String name : names) {
         Map<String, String> criteria = map.get(name);
         Set<String> keys = criteria.keySet();
         int size = criteria.size();
         
         output.writeUTF(name);
         output.writeInt(size);
         
         for(String key : keys) {
            String value = criteria.get(key);
            
            output.writeUTF(key);
            output.writeUTF(value);
         }
      }
   }
}