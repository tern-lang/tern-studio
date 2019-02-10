package com.zuooh.ldap.directory.service;

import java.util.Map;

public class DirectoryStructure {

   private final Map<String, String> partitions;
   
   public DirectoryStructure(Map<String, String> partitions){
      this.partitions = partitions;
   }
   
   public Map<String, String> getPartitions() {
      return partitions;
   }
}


