package tern.studio.common.resource.display;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root
public class DisplayDefinition {
   
   private static final String DEFAULT_THEME = "eclipse";
   private static final String DEFAULT_FONT = "Consolas";
   private static final String DEFAULT_LOGO = null; //"/img/logo_grey_shade.png";
   private static final int DEFAULT_SIZE = 14;
   private static final int DEFAULT_CAPACITY = 50000;
   
   @Element(name="theme-name", required=false)
   private String themeName;
   
   @Element(name="logo-image", required=false)
   private String logoImage;
   
   @Element(name="console-capacity", required=false)
   private int consoleCapacity;
   
   @Path("font")
   @Element(name="font-family")
   private String fontName;

   @Path("font")
   @Element(name="font-size")
   private int fontSize;
   
   public DisplayDefinition(){
      this(null, null, null, 0, 50000);
   }
   
   public DisplayDefinition(String themeName, String logoImage, String fontName, int fontSize, int consoleCapacity) {
      this.consoleCapacity = consoleCapacity;
      this.logoImage = logoImage;
      this.themeName = themeName;
      this.fontName = fontName;
      this.fontSize = fontSize;
   }

   public String getThemeName() {
      return themeName;
   }

   public void setThemeName(String themeName) {
      this.themeName = themeName;
   }

   public String getLogoImage() {
      return logoImage;
   }

   public void setLogoImage(String logoImage) {
      this.logoImage = logoImage;
   }

   public int getConsoleCapacity() {
      return consoleCapacity;
   }

   public void setConsoleCapacity(int consoleCapacity) {
      this.consoleCapacity = consoleCapacity;
   }

   public String getFontName() {
      return fontName;
   }

   public void setFontName(String fontName) {
      this.fontName = fontName;
   }

   public int getFontSize() {
      return fontSize;
   }

   public void setFontSize(int fontSize) {
      this.fontSize = fontSize;
   }
   
   public static DisplayDefinition getDefault() {
      return new DisplayDefinition(DEFAULT_THEME, DEFAULT_LOGO, DEFAULT_FONT, DEFAULT_SIZE, DEFAULT_CAPACITY);
   }
}