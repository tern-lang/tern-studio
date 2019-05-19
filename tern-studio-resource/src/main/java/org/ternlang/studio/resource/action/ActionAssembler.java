package org.ternlang.studio.resource.action;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ternlang.studio.resource.action.annotation.Intercept;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.build.ActionBuilder;
import org.ternlang.studio.resource.action.build.ComponentFinder;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MethodScanner;
import org.ternlang.studio.resource.action.build.MethodScannerResolver;
import org.ternlang.studio.resource.action.extract.BodyExtractor;
import org.ternlang.studio.resource.action.extract.CookieExtractor;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.HeaderExtractor;
import org.ternlang.studio.resource.action.extract.ModelExtractor;
import org.ternlang.studio.resource.action.extract.PartExtractor;
import org.ternlang.studio.resource.action.extract.PathExtractor;
import org.ternlang.studio.resource.action.extract.QueryExtractor;
import org.ternlang.studio.resource.action.extract.RequestExtractor;
import org.ternlang.studio.resource.action.extract.ResponseExtractor;
import org.ternlang.studio.resource.action.write.BodyWriter;
import org.ternlang.studio.resource.action.write.ByteArrayWriter;
import org.ternlang.studio.resource.action.write.CharacterArrayWriter;
import org.ternlang.studio.resource.action.write.ExceptionWriter;
import org.ternlang.studio.resource.action.write.JsonWriter;
import org.ternlang.studio.resource.action.write.ResponseWriter;
import org.ternlang.studio.resource.action.write.StringWriter;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;

public class ActionAssembler {
   
   public static Set<Class> classesWithAnnotation(Class<? extends Annotation> annotation) {
      Set<Class> types = new HashSet<Class>();
      Iterator<ClassInfo> iterator = new ClassGraph()
            .enableAllInfo()
            //.disableDirScanning()
            .whitelistPackages("org.ternlang.*")
            .whitelistPaths("..")
            //.verbose()
            .scan()
            .getAllClasses()
            .iterator();
      
     String name = annotation.getName();  
     while(iterator.hasNext()) {
        ClassInfo info = iterator.next();
        
        if(info.hasAnnotation(name)) {
           Class type = info.loadClass();
           types.add(type);
        }
     }
     return types;
   }
   
   public static ActionMatcher assemble(DependencySystem source) {
      //ResourceClassScanner resourceScanner = new ResourceClassScanner();
      //Set<Class> interceptors = resourceScanner.scan(Intercept.class);
      //Set<Class> services = resourceScanner.scan(Path.class);
      
      Set<Class> interceptors = classesWithAnnotation(Intercept.class);
      Set<Class> services = classesWithAnnotation(Path.class);
      
      List<Extractor> extractors = new LinkedList<Extractor>();
      List<BodyWriter> builders = new LinkedList<BodyWriter>();
      ComponentFinder interceptorFinder = new ComponentFinder(interceptors);
      ComponentFinder serviceFinder = new ComponentFinder(services);
      MethodScanner scanner = new MethodScanner(source, extractors);
      MethodScannerResolver interceptorResolver = new MethodScannerResolver(scanner, interceptorFinder);
      MethodScannerResolver serviceResolver = new MethodScannerResolver(scanner, serviceFinder);
      ActionResolver resolver = new ActionBuilder(serviceResolver, interceptorResolver);
      ContextBuilder builder = new ContextBuilder();
      ResponseWriter router = new ResponseWriter(builders);

      builders.add(new JsonWriter());
      builders.add(new ByteArrayWriter());
      builders.add(new CharacterArrayWriter());
      builders.add(new ExceptionWriter());
      builders.add(new StringWriter());

      extractors.add(new PathExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new BodyExtractor());
      
      return new ActionMatcher(resolver, builder, router);
   }
}
