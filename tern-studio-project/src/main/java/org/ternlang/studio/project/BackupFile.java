package org.ternlang.studio.project;

import java.io.File;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackupFile {
   private final File file;
   private final String path;
   private final Date date;
   private final String timeStamp;
   private final String project;
}