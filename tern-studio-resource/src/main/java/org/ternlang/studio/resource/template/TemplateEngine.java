package org.ternlang.studio.resource.template;

public interface TemplateEngine {
   String renderTemplate(TemplateModel model, String template) throws Exception;
   boolean validTemplate(String template) throws Exception;
}