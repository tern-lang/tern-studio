
function convertData(data) {
   var edges = [];
   var nodes = [];
   var nodesDone = [];
   var nodeIds = {};
   var index = 0;
   
   for(var i = 0; i < data.length; i++) {
      var source = data[i].source;
      var destination = data[i].destination;
      var list = [source, destination];
      
      for(var j = 0; j < list.length; j++) {
         var entry = list[j];
         
         if(nodesDone.indexOf(entry) == -1) {
            nodes.push({name: entry});
            nodesDone.push(entry);
            nodeIds[entry] = index++;
         }
      }
   }
   for(var i = 0; i < data.length; i++) {
      var source = data[i].source;
      var destination = data[i].destination;
      var type = data[i].type;
      
      edges.push({
         source: nodeIds[source],
         target: nodeIds[destination]
      });
      
      if(type == 3) { // inout connection
         edges.push({
            source: nodeIds[destination],
            target: nodeIds[source]
         });
      }
   }
   return {
     nodes: nodes,
     edges: edges
   };
}

function drawGraph(id, data, width, height) {
   var linkDistance=200;

   var colors = d3.scale.category20();
   //var colors = d3.scale.category10();
   //var colors = d3.scaleOrdinal(d3.schemeCategory20);
   
   var svg = d3.select("#" + id).append("svg").attr({"width":width,"height":height});

   var force = d3.layout.force()
       .nodes(data.nodes)
       .links(data.edges)
       .size([width,height])
       .linkDistance([linkDistance])
       .charge([-500])
       .theta(0.1)
       .gravity(0.05)
       .start();


   var edges = svg.selectAll("line")
     .data(data.edges)
     .enter()
     .append("line")
     .attr("id",function(d,i) {return 'edge'+i})
     .attr('marker-end','url(#arrowhead)')
     .style("stroke","#ccc")
     .style("pointer-events", "none");
   
   var nodes = svg.selectAll("circle")
     .data(data.nodes)
     .enter()
     .append("circle")
     .attr({"r":10})
     .style("fill",function(d,i){return colors(i);})
     .call(force.drag)


   var nodelabels = svg.selectAll(".nodelabel") 
      .data(data.nodes)
      .enter()
      .append("text")
      .attr({"x":function(d){return d.x;},
             "y":function(d){return d.y;},
             "class":"nodelabel",
             "stroke":"black"})
      .text(function(d){return d.name;});

   var edgepaths = svg.selectAll(".edgepath")
       .data(data.edges)
       .enter()
       .append('path')
       .attr({'d': function(d) {return 'M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y},
              'class':'edgepath',
              'fill-opacity':0,
              'stroke-opacity':0,
              'fill':'blue',
              'stroke':'red',
              'id':function(d,i) {return 'edgepath'+i}})
       .style("pointer-events", "none");

   var edgelabels = svg.selectAll(".edgelabel")
       .data(data.edges)
       .enter()
       .append('text')
       .style("pointer-events", "none")
       .attr({'class':'edgelabel',
              'id':function(d,i){return 'edgelabel'+i},
              'dx':80,
              'dy':0,
              'font-size':10,
              'fill':'#aaa'});

   //edgelabels.append('textPath')
   //    .attr('xlink:href',function(d,i) {return '#edgepath'+i})
   //    .style("pointer-events", "none")
   //    .text(function(d,i){return 'label '+i});


   svg.append('defs').append('marker')
       .attr({'id':'arrowhead',
              'viewBox':'-0 -5 10 10',
              'refX':25,
              'refY':0,
              //'markerUnits':'strokeWidth',
              'orient':'auto',
              'markerWidth':10,
              'markerHeight':10,
              'xoverflow':'visible'})
       .append('svg:path')
           .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
           .attr('fill', '#ccc')
           .attr('stroke','#ccc');
    

   force.on("tick", function(){

       edges.attr({"x1": function(d){return d.source.x;},
                   "y1": function(d){return d.source.y;},
                   "x2": function(d){return d.target.x;},
                   "y2": function(d){return d.target.y;}
       });

       nodes.attr({"cx":function(d){return d.x;},
                   "cy":function(d){return d.y;}
       });

       nodelabels.attr("x", function(d) { return d.x; }) 
                 .attr("y", function(d) { return d.y; });

       edgepaths.attr('d', function(d) { var path='M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y;
                                          //console.log(d)
                                          return path});       

       edgelabels.attr('transform',function(d,i){
           if (d.target.x<d.source.x){
               bbox = this.getBBox();
               rx = bbox.x+bbox.width/2;
               ry = bbox.y+bbox.height/2;
               return 'rotate(180 '+rx+' '+ry+')';
           }
           else {
               return 'rotate(0)';
           }
       });
   });
}