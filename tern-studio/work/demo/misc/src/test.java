


@Path("/project/{project}")
class ProjectResource {

   
   @GET
   public void handle(@QueryParam("q") String q, @ParmParam("project") String project) {
      
   } 
}