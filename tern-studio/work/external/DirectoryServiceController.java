package com.zuooh.ldap.directory.service;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.entry.ServerEntry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.name.DN;

public class DirectoryServiceController {
   
   private final DirectoryService service;
   private final DirectoryPartitionBuilder builder;
   private final DirectoryStructure structure;
   private final TcpTransport transport;
   private final LdapServer server;
   private final File directory;
   
   public DirectoryServiceController(DirectoryService service, DirectoryStructure structure, File directory, int port) throws Exception {
      this.builder = new DirectoryPartitionBuilder(service);
      this.transport = new TcpTransport(port);
      this.server = new LdapServer();
      this.structure = structure;
      this.directory = directory;
      this.service = service;
   }

   public void start() throws Exception {
      service.setWorkingDirectory(directory);
      builder.initSchemaPartition();

      // then the system partition
      // this is a MANDATORY partition
      Partition systemPartition = builder.addPartition("system", ServerDNConstants.SYSTEM_DN);
      service.setSystemPartition(systemPartition);

      // Disable the ChangeLog system
      service.getChangeLog().setEnabled(false);
      service.setDenormalizeOpAttrsEnabled(true);
      
      Map<String, String> parts = structure.getPartitions();
      Set<String> partitions = parts.keySet();
      for(String partition : partitions) {
         String value = parts.get(partition);
         // Now we can create as many partitions as we need
         // Create some new partitions named 'foo', 'bar' and 'apache'.
         Partition fooPartition = builder.addPartition(partition, value);

         // Index some attributes on the apache partition
         builder.addIndex(fooPartition, "objectClass", "ou", "uid");

         // And start the service
         service.startup();

         // Inject the foo root entry if it does not already exist
         try {
            service.getAdminSession().lookup(fooPartition.getSuffixDn());
         } catch(LdapException lnnfe) {
            DN dnFoo = new DN(value);
            ServerEntry entryFoo = service.newEntry(dnFoo);
            entryFoo.add("objectClass", "top", "domain", "extensibleObject");
            entryFoo.add("dc", partition);
            service.getAdminSession().add(entryFoo);
         }

      }
      server.setTransports(transport);
      server.setDirectoryService(service);
      server.start();
   }

}
