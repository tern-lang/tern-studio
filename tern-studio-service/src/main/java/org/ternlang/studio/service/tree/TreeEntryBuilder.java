package org.ternlang.studio.service.tree;

public class TreeEntryBuilder {
   
   private final TreeContext context;
   
   public TreeEntryBuilder(TreeContext context) {
      this.context = context;
   }

   public void buildFolder(StringBuilder builder, TreeNode node, String imageFolder) {
      String path = node.getPath();
      String prefix = node.getPrefix();
      boolean isLayout = context.isLayoutPath(prefix, path);
      
      builder.append(node.getIndent());
      builder.append("<li id=\"");
      builder.append(node.getPrefix());
      builder.append(node.getId());
      builder.append("\" title=\"");
      builder.append(path);

      if (node.isRoot()) {
         builder.append("\" data-icon=\"");
         builder.append(imageFolder);
         builder.append("/project_index.png\" class=\"expanded folder\">");
      } else if (node.isExpand()) {
         builder.append("\" data-icon=\"");
         builder.append(imageFolder);
         
         if(!isLayout) {
            builder.append("/file_directory.png");
         } else {
            builder.append("/layout_directory.png");
         }
         builder.append("\" class=\"expanded folder\">");
      } else {
         builder.append("\" data-icon=\"");
         builder.append(imageFolder);
         
         if(!isLayout) {
            builder.append("/file_directory.png");
         } else {
            builder.append("/layout_directory.png");
         }
         builder.append("\" class=\"folder\">");
      }
      if(node.isRoot()) {
         builder.append("<b>");
         builder.append(node.getName());
         builder.append("</b>");
      } else {
         builder.append(node.getName());
      }
      builder.append("\n");
   }

   public void buildFile(StringBuilder builder, TreeNode node, String imageFolder) {
      String name = node.getName();

      builder.append(node.getIndent());
      builder.append("<li ");

      if (name.endsWith(".gif")) {
         builder.append("data-icon=\"");
         builder.append(imageFolder);
         builder.append("/file_image.png\"");
      } else if (name.endsWith(".png")) {
         builder.append("data-icon=\"");
         builder.append(imageFolder);
         builder.append("/file_image.png\"");
      } else if (name.endsWith(".jpg")) {
         builder.append("data-icon=\"");
         builder.append(imageFolder);
         builder.append("/file_image.png\"");
      } else if (name.endsWith(".jar")) {
         builder.append("data-icon=\"");
         builder.append(imageFolder);
         builder.append("/file_zip.png\"");
      } else if (!name.endsWith(".tern")) {
         builder.append("data-icon=\"");
         builder.append(imageFolder);
         builder.append("/file_text.png\"");
      } else {
         builder.append("data-icon=\"");
         builder.append(imageFolder);
         builder.append("/file_code.png\"");
      }
      builder.append(" id=\"");
      builder.append(node.getPrefix());
      builder.append(node.getId());
      builder.append("\" title=\"");
      builder.append(node.getPath());
      builder.append("\">");
      builder.append(node.getName());
      builder.append("\n");
   }
}