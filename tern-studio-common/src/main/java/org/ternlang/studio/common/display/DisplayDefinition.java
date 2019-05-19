package org.ternlang.studio.common.display;

import java.util.Map;
import java.util.TreeMap;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
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

   @Path("font/available-fonts")
   @ElementMap(entry="font", key="style", attribute=true, inline=true, required=false)
   private Map<String, String> availableFonts;

   @Path("font/selected-font")
   @Element(name="font-family")
   private String fontName;

   @Path("font/selected-font")
   @Element(name="font-size")
   private int fontSize;

   public DisplayDefinition(){
      this(null, null, null, null, 0, 50000);
   }
   
   public DisplayDefinition(Map<String, String> availableFonts, String themeName, String logoImage, String fontName, int fontSize, int consoleCapacity) {
      this.availableFonts = availableFonts;
      this.consoleCapacity = consoleCapacity;
      this.logoImage = logoImage;
      this.themeName = themeName;
      this.fontName = fontName;
      this.fontSize = fontSize;
   }

   public Map<String, String> getAvailableFonts() {
      return availableFonts;
   }

   public void setAvailableFonts(Map<String, String> availableFonts) {
      this.availableFonts = availableFonts;
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
      Map<String, String> defaultFonts = new TreeMap<String, String>();
      DisplayDefinition definition = new DisplayDefinition(defaultFonts, DEFAULT_THEME, DEFAULT_LOGO, DEFAULT_FONT, DEFAULT_SIZE, DEFAULT_CAPACITY);

      defaultFonts.put("Consolas", "Consolas");
      defaultFonts.put("Courier", "Courier");
      defaultFonts.put("Courier New", "Courier New");
      defaultFonts.put("Deja Vu", "Deja Vu");
      defaultFonts.put("Hack", "Hack");
      defaultFonts.put("Lucida Console,Menlo", "Lucida Console");
      defaultFonts.put("Menlo,Lucida Console", "Menlo");
      defaultFonts.put("Monaco", "Monaco");
      defaultFonts.put("Oxygen Mono", "Oxygen Mono");
      defaultFonts.put("Roboto Mono", "Roboto Mono");
      defaultFonts.put("Source Code Pro", "Source Code Pro");
      defaultFonts.put("Ubuntu Mono", "Ubuntu Mono");

      if(!defaultFonts.containsKey(DEFAULT_FONT)) {
         throw new IllegalStateException("Default font '" + DEFAULT_FONT+ "' not available");
      }
      return definition;
   }
}