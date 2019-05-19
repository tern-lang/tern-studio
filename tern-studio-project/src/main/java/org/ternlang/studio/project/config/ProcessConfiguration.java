package org.ternlang.studio.project.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@org.ternlang.studio.resource.action.annotation.Component
@Component
public class ProcessConfiguration {

   private Map<String, String> variables;
   private List<String> arguments;
   private String host;
   private int port;
   
   public ProcessConfiguration() {
      this.variables = new HashMap<String, String>();
      this.arguments = new ArrayList<String>();
   }
   
   public List<String> getArguments() {
      return arguments;
   }
   
   public void setArguments(List<String> arguments) {
      this.arguments = arguments;
   }
   
   public Map<String, String> getVariables() {
      return variables;
   }
   
   public void setVariables(Map<String, String> variables) {
      this.variables = variables;
   }
   
   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }
   
   public int getPort() {
      return port;
   }

   public void setPort(int port) {
      this.port = port;
   }
}