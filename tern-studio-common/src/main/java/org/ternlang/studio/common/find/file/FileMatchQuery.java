package org.ternlang.studio.common.find.file;

import java.io.File;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileMatchQuery {
   private File path;
   private String project;
   private String query;
}