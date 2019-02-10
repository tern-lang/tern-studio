{
   "Workspace": {
      "class": "org.snapscript.develop.Workspace",
      "init-method": "create",
      "constructor-arguments": [
         "${directory}"
      ]
   },
   
   "BackupManager": {
      "class": "org.snapscript.develop.BackupManager",
      "constructor-arguments": [
         "ConsoleLogger",
         "Workspace"
      ]
   }
}

eval('develop.json');


for(var entry in data){
   
}

module Boot {
   create(data) {
      for(var entry in data){
            
      }
   }
   
   create(data, registry, name) {
      var obj = registry[name];
      
      if(obj != null) {
         var def = data[name];
         
         if(def != null) {
            var args = data['constructor-args'];
            var type = data['class'];
            var init = data['init-method'];
            var func = eval();
            var list = [];
            
            for(var arg in args) {
               var value = create(data, registry, arg);
               list.add(value);
            }
            return registry[name] = load(type, name, args); // save the reference
         }
         return def;
      }
      return obj;
   }
   
   load(type, name, args) {
      var package = Boot.class.getModule();
      var context = package.getContext();
      var loader = context.getLoader();
      var manager = package.getManager();
      var data = manager.addImport(type, name); // import <type> as <name>
      var builder = new StringBuilder();
      
      builder.append("new ");
      builder.append(name);
      builder.append("(");
      
      for(var arg in args) {
         builder.append(arg);
         builder.append(",");
      }
      builder.append(")");
      return eval(builder;
   }
}