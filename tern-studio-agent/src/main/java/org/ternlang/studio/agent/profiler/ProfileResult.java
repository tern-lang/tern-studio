package org.ternlang.studio.agent.profiler;

public class ProfileResult implements Comparable<ProfileResult>{
   
   private String resource;
   private Integer line;
   private Integer count;
   private Long time;
   
   public ProfileResult() {
      super();
   }
   
   public ProfileResult(String resource, Long time, Integer count, Integer line) {
      this.resource = resource;
      this.time = time;
      this.line = line;
      this.count = count;
   }
   
   @Override
   public int compareTo(ProfileResult other) {
      int compare = other.time.compareTo(time);
      
      if(compare == 0) {
         return other.line.compareTo(line);
      }
      return compare;
   }
   
   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public int getLine(){
      return line;
   }
   
   public void setLine(Integer line) {
      this.line = line;
   }
   
   public long getTime(){
      return time;
   }
   
   public void setTime(Long time) {
      this.time = time;
   }
}