package org.ternlang.studio.service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

import junit.framework.TestCase;

public class NamespaceTest extends TestCase{
   
   private static final String SOURCE =
   "<root xmlns:media='blah'>"+
   "  <item>"+
   "    <media:thumbnail>thumbnail_url</media:thumbnail>"+
   "  </item>"+
   "</root>";    
   
   @Root(name = "item")
   public static class ArticleSummary {

       @Element
       @Namespace(prefix="media", reference="blah") 
       private String thumbnail;
   }  
   
   @Root
   public static class Article{
      @Element
      private ArticleSummary item;
   }
   
   public void testXml() throws Exception {
      Persister persister = new Persister();
      Article article = persister.read(Article.class, SOURCE);
      System.err.println(article.item.thumbnail);
   }

}
