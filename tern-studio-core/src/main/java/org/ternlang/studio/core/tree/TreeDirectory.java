package org.ternlang.studio.core.tree;

import static org.ternlang.studio.core.tree.TreeConstants.INDENT;
import static org.ternlang.studio.core.tree.TreeConstants.PREFIX;
import static org.ternlang.studio.core.tree.TreeConstants.ROOT;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.simpleframework.module.resource.template.TemplateModel;
import org.ternlang.studio.common.display.DisplayKey;

public class TreeDirectory {
   
   private final TreeEntryBuilder entryBuilder;
   private final TemplateModel theme;
   private final TreeContext context;
   private final boolean foldersOnly;
   private final int folderDepth;
   
   public TreeDirectory(TreeContext context, TemplateModel theme) {
      this(context, theme, false);
   }
   
   public TreeDirectory(TreeContext context, TemplateModel theme, boolean foldersOnly) {
      this(context, theme, foldersOnly, Integer.MAX_VALUE);
   }
   
   public TreeDirectory(TreeContext context, TemplateModel theme, boolean foldersOnly, int folderDepth) {
      this.entryBuilder = new TreeEntryBuilder(context);
      this.foldersOnly = foldersOnly;
      this.folderDepth = folderDepth;
      this.context = context;
      this.theme = theme;
   }
   
   public void buildTree(StringBuilder builder) throws Exception {
      File root = context.getRoot();
      String project = context.getProject();
      Set<String> folders = context.getExpandFolders();
      TreeNode node = new TreeNode.Builder(root)
         .withPath(ROOT + project)
         .withIndent(INDENT)
         .withPrefix(PREFIX)
         .withId("/" + project)
         .withDepth(folderDepth)
         .withExpand(!folders.isEmpty())
         .withRoot(true)
         .build();
      
      buildTree(builder, node);
   }
   
   private void buildTree(StringBuilder builder, TreeNode node) throws Exception {
      String themeKey = DisplayKey.IMAGE_FOLDER.name();
      String imageFolder = String.valueOf(theme.getAttribute(themeKey));
      String name = node.getName();
      int folderDepth = node.getDepth();
      
      if(folderDepth > 0) {
         if(node.isDirectory()) {
            if(!name.startsWith(".")) { // ignore directories starting with "."
               entryBuilder.buildFolder(builder, node, imageFolder);
               
               List<File> list = node.getFiles();
               
               if(!list.isEmpty()) {
                  String prefix = node.getPrefix() + node.getId() + "/";
                  
                  builder.append(node.getIndent());
                  builder.append("<ul>\n");
                  
                  for(File entry : list) {
                     String title = entry.getName();
                     String nextPath = node.getPath() + "/" + title;
                     
                     if(context.isVisiblePath(nextPath)) {
                        TreeNode next = new TreeNode.Builder(entry)
                              .withPath(nextPath)
                              .withIndent(node.getIndent() + INDENT)
                              .withPrefix(prefix)
                              .withId(title)
                              .withDepth(folderDepth -1)
                              .withExpand(context.expand(nextPath))
                              .build();
                        
                        buildTree(builder, next);
                     }
                  }
                  builder.append(node.getIndent());
                  builder.append("</ul>\n");
               }
            }
         } else {
            if(!foldersOnly) {
               entryBuilder.buildFile(builder, node, imageFolder);
            }
         }
      }
   }   
}