function removeElement(evt) {
var element = evt.target;
element.parentNode.removeChild(element);
}
function changeElement(evt) {
var element = evt.target;
var colour = element.getAttribute("fill");
var red = "red";
if (colour == red){
element.setAttribute("fill", "blue");
}
else{
element.setAttribute("fill", "red");
}
var el = evt.target;
if (el.getStyle().getPropertyValue("stroke")=="rgb(255, 0, 0)"){
el.getStyle().setProperty("stroke", "rgb(99,99,99)");
}
else{
el.getStyle().setProperty("stroke", "rgb(255,0,0)");
}
}
function removeElement(evt) {
var element = evt.target;
element.parentNode.removeChild(element);
}

var svgDoc;
function startup(evt){
  svgDoc=evt.target.ownerDocument;
}

function testmethod(extradata) {
//alert (extradata);
a="visibility";
e=svgDoc.getElementById(extradata+"extra");
ega=e.getAttribute(a)
if (ega=="hidden") e.setAttribute(a,"visible");
else e.setAttribute(a,"hidden");

e=svgDoc.getElementById(extradata);
ega=e.getAttribute(a)
if (ega=="hidden") e.setAttribute(a,"visible");
else e.setAttribute(a,"hidden");
}