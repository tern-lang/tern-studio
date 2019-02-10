package com.zuooh.ldap.directory.service;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.schema.SchemaPartition;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.entry.ServerEntry;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.ldif.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;

public class DirectoryPartitionBuilder {
   
   private final DirectoryService service;
   
   public DirectoryPartitionBuilder(DirectoryService service) {
      this.service = service;
   }

   public Partition addPartition(String partitionId, String partitionDn) throws Exception {
      // Create a new partition named 'foo'.
      JdbmPartition partition = new JdbmPartition();
      partition.setId(partitionId);
      partition.setPartitionDir(new File(service.getWorkingDirectory(), partitionId));
      partition.setSuffix(partitionDn);
      service.addPartition(partition);

      return partition;
   }
   public void addIndex(Partition partition, String... attrs) {
      // Index some attributes on the apache partition
      HashSet<Index<?, ServerEntry, Long>> indexedAttributes = new HashSet<Index<?, ServerEntry, Long>>();

      for(String attribute : attrs) {
         indexedAttributes.add(new JdbmIndex<String, ServerEntry>(attribute));
      }

      ((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
   }
   
   public void initSchemaPartition() throws Exception {
      SchemaPartition schemaPartition = service.getSchemaService().getSchemaPartition();

      // Init the LdifPartition
      LdifPartition ldifPartition = new LdifPartition();
      String workingDirectory = service.getWorkingDirectory().getPath();
      ldifPartition.setWorkingDirectory(workingDirectory + "/schema");

      // Extract the schema on disk (a brand new one) and load the registries
      File schemaRepository = new File(workingDirectory, "schema");
      SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(new File(workingDirectory));
      extractor.extractOrCopy(true);

      schemaPartition.setWrappedPartition(ldifPartition);

      SchemaLoader loader = new LdifSchemaLoader(schemaRepository);
      SchemaManager schemaManager = new DefaultSchemaManager(loader);
      service.setSchemaManager(schemaManager);

      // We have to load the schema now, otherwise we won't be able
      // to initialize the Partitions, as we won't be able to parse
      // and normalize their suffix DN
      schemaManager.loadAllEnabled();

      schemaPartition.setSchemaManager(schemaManager);

      List<Throwable> errors = schemaManager.getErrors();

      if(errors.size() != 0) {
         throw new Exception("Schema load failed : " + errors);
      }
   }
}
