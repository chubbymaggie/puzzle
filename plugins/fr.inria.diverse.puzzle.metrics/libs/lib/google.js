if(!window['googleLT_']){window['googleLT_']=(new Date()).getTime();}if (!window['google']) {
window['google'] = {};
}
if (!window['google']['loader']) {
window['google']['loader'] = {};
google.loader.ServiceBase = 'https://www.google.com/uds';
google.loader.GoogleApisBase = 'https://ajax.googleapis.com/ajax';
google.loader.ApiKey = 'notsupplied';
google.loader.KeyVerified = true;
google.loader.LoadFailure = false;
google.loader.Secure = true;
google.loader.GoogleLocale = 'www.google.com';
google.loader.ClientLocation = {"latitude":48.083,"longitude":-1.683,"address":{"city":"Rennes","region":"Brittany","country":"France","country_code":"FR"}};
google.loader.AdditionalParams = '';
(function() {var d=window,g=encodeURIComponent,h=document;function l(a,b){return a.load=b}var m="join",n="name",q="prototype",r="ServiceBase",u="indexOf",v="setTimeout",w="replace",x="substring",y="charAt",z="push",A="loader",B="length",C="toLowerCase",D="getTime";function E(a){return a in F?F[a]:F[a]=-1!=navigator.userAgent[C]()[u](a)}var F={};function G(a,b){var c=function(){};c.prototype=b[q];a.ca=b[q];a.prototype=new c}
function H(a,b,c){var e=Array[q].slice.call(arguments,2)||[];return function(){return a.apply(b,e.concat(Array[q].slice.call(arguments)))}}function I(a){a=Error(a);a.toString=function(){return this.message};return a}function J(a,b){for(var c=a.split(/\./),e=d,f=0;f<c[B]-1;f++)e[c[f]]||(e[c[f]]={}),e=e[c[f]];e[c[c[B]-1]]=b}function K(a,b,c){a[b]=c}if(!L)var L=J;if(!M)var M=K;google[A].F={};L("google.loader.callbacks",google[A].F);var N={},O={};google[A].eval={};L("google.loader.eval",google[A].eval);
l(google,function(a,b,c){function e(a){var b=a.split(".");if(2<b[B])throw I("Module: '"+a+"' not found!");"undefined"!=typeof b[1]&&(f=b[0],c.packages=c.packages||[],c.packages[z](b[1]))}var f=a;c=c||{};if(a instanceof Array||a&&"object"==typeof a&&"function"==typeof a[m]&&"function"==typeof a.reverse)for(var k=0;k<a[B];k++)e(a[k]);else e(a);if(a=N[":"+f]){c&&!c.language&&c.locale&&(c.language=c.locale);c&&"string"==typeof c.callback&&(k=c.callback,k.match(/^[[\]A-Za-z0-9._]+$/)&&(k=d.eval(k),c.callback=
k));if((k=c&&null!=c.callback)&&!a.D(b))throw I("Module: '"+f+"' must be loaded before DOM onLoad!");k?a.u(b,c)?d[v](c.callback,0):a.load(b,c):a.u(b,c)||a.load(b,c)}else throw I("Module: '"+f+"' not found!");});L("google.load",google.load);
google.ba=function(a,b){b?(0==P[B]&&(Q(d,"load",R),!E("msie")&&!E("safari")&&!E("konqueror")&&E("mozilla")||d.opera?d.addEventListener("DOMContentLoaded",R,!1):E("msie")?h.write("<script defer onreadystatechange='google.loader.domReady()' src=//:>\x3c/script>"):(E("safari")||E("konqueror"))&&d[v](aa,10)),P[z](a)):Q(d,"load",a)};L("google.setOnLoadCallback",google.ba);
function Q(a,b,c){if(a.addEventListener)a.addEventListener(b,c,!1);else if(a.attachEvent)a.attachEvent("on"+b,c);else{var e=a["on"+b];a["on"+b]=null!=e?ba([c,e]):c}}function ba(a){return function(){for(var b=0;b<a[B];b++)a[b]()}}var P=[];google[A].W=function(){var a=d.event.srcElement;"complete"==a.readyState&&(a.onreadystatechange=null,a.parentNode.removeChild(a),R())};L("google.loader.domReady",google[A].W);var ca={loaded:!0,complete:!0};function aa(){ca[h.readyState]?R():0<P[B]&&d[v](aa,10)}
function R(){for(var a=0;a<P[B];a++)P[a]();P.length=0}google[A].f=function(a,b,c){if(c){var e;"script"==a?(e=h.createElement("script"),e.type="text/javascript",e.src=b):"css"==a&&(e=h.createElement("link"),e.type="text/css",e.href=b,e.rel="stylesheet");(a=h.getElementsByTagName("head")[0])||(a=h.body.parentNode.appendChild(h.createElement("head")));a.appendChild(e)}else"script"==a?h.write('<script src="'+b+'" type="text/javascript">\x3c/script>'):"css"==a&&h.write('<link href="'+b+'" type="text/css" rel="stylesheet"></link>')};
L("google.loader.writeLoadTag",google[A].f);google[A].Z=function(a){O=a};L("google.loader.rfm",google[A].Z);google[A].aa=function(a){for(var b in a)"string"==typeof b&&b&&":"==b[y](0)&&!N[b]&&(N[b]=new T(b[x](1),a[b]))};L("google.loader.rpl",google[A].aa);google[A].$=function(a){if((a=a.specs)&&a[B])for(var b=0;b<a[B];++b){var c=a[b];"string"==typeof c?N[":"+c]=new U(c):(c=new V(c[n],c.baseSpec,c.customSpecs),N[":"+c[n]]=c)}};L("google.loader.rm",google[A].$);google[A].loaded=function(a){N[":"+a.module].o(a)};
L("google.loader.loaded",google[A].loaded);google[A].V=function(){return"qid="+((new Date)[D]().toString(16)+Math.floor(1E7*Math.random()).toString(16))};L("google.loader.createGuidArg_",google[A].V);J("google_exportSymbol",J);J("google_exportProperty",K);google[A].a={};L("google.loader.themes",google[A].a);google[A].a.K="//www.google.com/cse/style/look/bubblegum.css";M(google[A].a,"BUBBLEGUM",google[A].a.K);google[A].a.M="//www.google.com/cse/style/look/greensky.css";M(google[A].a,"GREENSKY",google[A].a.M);
google[A].a.L="//www.google.com/cse/style/look/espresso.css";M(google[A].a,"ESPRESSO",google[A].a.L);google[A].a.O="//www.google.com/cse/style/look/shiny.css";M(google[A].a,"SHINY",google[A].a.O);google[A].a.N="//www.google.com/cse/style/look/minimalist.css";M(google[A].a,"MINIMALIST",google[A].a.N);google[A].a.P="//www.google.com/cse/style/look/v2/default.css";M(google[A].a,"V2_DEFAULT",google[A].a.P);function U(a){this.b=a;this.B=[];this.A={};this.l={};this.g={};this.s=!0;this.c=-1}
U[q].i=function(a,b){var c="";void 0!=b&&(void 0!=b.language&&(c+="&hl="+g(b.language)),void 0!=b.nocss&&(c+="&output="+g("nocss="+b.nocss)),void 0!=b.nooldnames&&(c+="&nooldnames="+g(b.nooldnames)),void 0!=b.packages&&(c+="&packages="+g(b.packages)),null!=b.callback&&(c+="&async=2"),void 0!=b.style&&(c+="&style="+g(b.style)),void 0!=b.noexp&&(c+="&noexp=true"),void 0!=b.other_params&&(c+="&"+b.other_params));if(!this.s){google[this.b]&&google[this.b].JSHash&&(c+="&sig="+g(google[this.b].JSHash));
var e=[],f;for(f in this.A)":"==f[y](0)&&e[z](f[x](1));for(f in this.l)":"==f[y](0)&&this.l[f]&&e[z](f[x](1));c+="&have="+g(e[m](","))}return google[A][r]+"/?file="+this.b+"&v="+a+google[A].AdditionalParams+c};U[q].H=function(a){var b=null;a&&(b=a.packages);var c=null;if(b)if("string"==typeof b)c=[a.packages];else if(b[B])for(c=[],a=0;a<b[B];a++)"string"==typeof b[a]&&c[z](b[a][w](/^\s*|\s*$/,"")[C]());c||(c=["default"]);b=[];for(a=0;a<c[B];a++)this.A[":"+c[a]]||b[z](c[a]);return b};
l(U[q],function(a,b){var c=this.H(b),e=b&&null!=b.callback;if(e)var f=new W(b.callback);for(var k=[],p=c[B]-1;0<=p;p--){var t=c[p];e&&f.R(t);if(this.l[":"+t])c.splice(p,1),e&&this.g[":"+t][z](f);else k[z](t)}if(c[B]){b&&b.packages&&(b.packages=c.sort()[m](","));for(p=0;p<k[B];p++)t=k[p],this.g[":"+t]=[],e&&this.g[":"+t][z](f);if(b||null==O[":"+this.b]||null==O[":"+this.b].versions[":"+a]||google[A].AdditionalParams||!this.s)b&&b.autoloaded||google[A].f("script",this.i(a,b),e);else{c=O[":"+this.b];
google[this.b]=google[this.b]||{};for(var S in c.properties)S&&":"==S[y](0)&&(google[this.b][S[x](1)]=c.properties[S]);google[A].f("script",google[A][r]+c.path+c.js,e);c.css&&google[A].f("css",google[A][r]+c.path+c.css,e)}this.s&&(this.s=!1,this.c=(new Date)[D](),1!=this.c%100&&(this.c=-1));for(p=0;p<k[B];p++)t=k[p],this.l[":"+t]=!0}});
U[q].o=function(a){-1!=this.c&&(da("al_"+this.b,"jl."+((new Date)[D]()-this.c),!0),this.c=-1);this.B=this.B.concat(a.components);google[A][this.b]||(google[A][this.b]={});google[A][this.b].packages=this.B.slice(0);for(var b=0;b<a.components[B];b++){this.A[":"+a.components[b]]=!0;this.l[":"+a.components[b]]=!1;var c=this.g[":"+a.components[b]];if(c){for(var e=0;e<c[B];e++)c[e].U(a.components[b]);delete this.g[":"+a.components[b]]}}};U[q].u=function(a,b){return 0==this.H(b)[B]};U[q].D=function(){return!0};
function W(a){this.T=a;this.v={};this.C=0}W[q].R=function(a){this.C++;this.v[":"+a]=!0};W[q].U=function(a){this.v[":"+a]&&(this.v[":"+a]=!1,this.C--,0==this.C&&d[v](this.T,0))};function V(a,b,c){this.name=a;this.S=b;this.w=c;this.G=this.j=!1;this.m=[];google[A].F[this[n]]=H(this.o,this)}G(V,U);l(V[q],function(a,b){var c=b&&null!=b.callback;c?(this.m[z](b.callback),b.callback="google.loader.callbacks."+this[n]):this.j=!0;b&&b.autoloaded||google[A].f("script",this.i(a,b),c)});V[q].u=function(a,b){return b&&null!=b.callback?this.G:this.j};V[q].o=function(){this.G=!0;for(var a=0;a<this.m[B];a++)d[v](this.m[a],0);this.m=[]};
var X=function(a,b){return a.string?g(a.string)+"="+g(b):a.regex?b[w](/(^.*$)/,a.regex):""};V[q].i=function(a,b){return this.X(this.I(a),a,b)};
V[q].X=function(a,b,c){var e="";a.key&&(e+="&"+X(a.key,google[A].ApiKey));a.version&&(e+="&"+X(a.version,b));b=google[A].Secure&&a.ssl?a.ssl:a.uri;if(null!=c)for(var f in c)a.params[f]?e+="&"+X(a.params[f],c[f]):"other_params"==f?e+="&"+c[f]:"base_domain"==f&&(b="http://"+c[f]+a.uri[x](a.uri[u]("/",7)));google[this[n]]={};-1==b[u]("?")&&e&&(e="?"+e[x](1));return b+e};V[q].D=function(a){return this.I(a).deferred};V[q].I=function(a){if(this.w)for(var b=0;b<this.w[B];++b){var c=this.w[b];if((new RegExp(c.pattern)).test(a))return c}return this.S};function T(a,b){this.b=a;this.h=b;this.j=!1}G(T,U);l(T[q],function(a,b){this.j=!0;google[A].f("script",this.i(a,b),!1)});T[q].u=function(){return this.j};T[q].o=function(){};T[q].i=function(a,b){if(!this.h.versions[":"+a]){if(this.h.aliases){var c=this.h.aliases[":"+a];c&&(a=c)}if(!this.h.versions[":"+a])throw I("Module: '"+this.b+"' with version '"+a+"' not found!");}return google[A].GoogleApisBase+"/libs/"+this.b+"/"+a+"/"+this.h.versions[":"+a][b&&b.uncompressed?"uncompressed":"compressed"]};
T[q].D=function(){return!1};var ea=!1,Y=[],fa=(new Date)[D](),ha=function(){ea||(Q(d,"unload",ga),ea=!0)},ia=function(a,b){ha();if(!(google[A].Secure||google[A].Options&&!1!==google[A].Options.csi)){for(var c=0;c<a[B];c++)a[c]=g(a[c][C]()[w](/[^a-z0-9_.]+/g,"_"));for(c=0;c<b[B];c++)b[c]=g(b[c][C]()[w](/[^a-z0-9_.]+/g,"_"));d[v](H(Z,null,"//gg.google.com/csi?s=uds&v=2&action="+a[m](",")+"&it="+b[m](",")),1E4)}},da=function(a,b,c){c?ia([a],[b]):(ha(),Y[z]("r"+Y[B]+"="+g(a+(b?"|"+b:""))),d[v](ga,5<Y[B]?0:15E3))},ga=function(){if(Y[B]){var a=
google[A][r];0==a[u]("http:")&&(a=a[w](/^http:/,"https:"));Z(a+"/stats?"+Y[m]("&")+"&nc="+(new Date)[D]()+"_"+((new Date)[D]()-fa));Y.length=0}},Z=function(a){var b=new Image,c=Z.Y++;Z.J[c]=b;b.onload=b.onerror=function(){delete Z.J[c]};b.src=a;b=null};Z.J={};Z.Y=0;J("google.loader.recordCsiStat",ia);J("google.loader.recordStat",da);J("google.loader.createImageForLogging",Z);

}) ();google.loader.rm({"specs":["visualization","payments",{"name":"annotations","baseSpec":{"uri":"http://www.google.com/reviews/scripts/annotations_bootstrap.js","ssl":null,"key":{"string":"key"},"version":{"string":"v"},"deferred":true,"params":{"country":{"string":"gl"},"callback":{"string":"callback"},"language":{"string":"hl"}}}},"language","gdata","wave","spreadsheets","search","orkut","feeds","annotations_v2","picker","identitytoolkit",{"name":"maps","baseSpec":{"uri":"http://maps.google.com/maps?file\u003dgoogleapi","ssl":"https://maps-api-ssl.google.com/maps?file\u003dgoogleapi","key":{"string":"key"},"version":{"string":"v"},"deferred":true,"params":{"callback":{"regex":"callback\u003d$1\u0026async\u003d2"},"language":{"string":"hl"}}},"customSpecs":[{"uri":"http://maps.googleapis.com/maps/api/js","ssl":"https://maps.googleapis.com/maps/api/js","version":{"string":"v"},"deferred":true,"params":{"callback":{"string":"callback"},"language":{"string":"hl"}},"pattern":"^(3|3..*)$"}]},{"name":"friendconnect","baseSpec":{"uri":"http://www.google.com/friendconnect/script/friendconnect.js","ssl":"https://www.google.com/friendconnect/script/friendconnect.js","key":{"string":"key"},"version":{"string":"v"},"deferred":false,"params":{}}},{"name":"sharing","baseSpec":{"uri":"http://www.google.com/s2/sharing/js","ssl":null,"key":{"string":"key"},"version":{"string":"v"},"deferred":false,"params":{"language":{"string":"hl"}}}},"ads",{"name":"books","baseSpec":{"uri":"http://books.google.com/books/api.js","ssl":"https://encrypted.google.com/books/api.js","key":{"string":"key"},"version":{"string":"v"},"deferred":true,"params":{"callback":{"string":"callback"},"language":{"string":"hl"}}}},"elements","earth","ima"]});
google.loader.rfm({":search":{"versions":{":1":"1",":1.0":"1"},"path":"/api/search/1.0/56f70d816baa48bdfe9284ebc883ad41/","js":"default+fr.I.js","css":"default+fr.css","properties":{":Version":"1.0",":NoOldNames":false,":JSHash":"56f70d816baa48bdfe9284ebc883ad41"}},":language":{"versions":{":1":"1",":1.0":"1"},"path":"/api/language/1.0/cddd1e0315f352539fc3732010bda57f/","js":"default+fr.I.js","properties":{":Version":"1.0",":JSHash":"cddd1e0315f352539fc3732010bda57f"}},":annotations":{"versions":{":1":"1",":1.0":"1"},"path":"/api/annotations/1.0/69e4c522bede40545878d7b96b100995/","js":"default+fr.I.js","properties":{":Version":"1.0",":JSHash":"69e4c522bede40545878d7b96b100995"}},":wave":{"versions":{":1":"1",":1.0":"1"},"path":"/api/wave/1.0/3b6f7573ff78da6602dda5e09c9025bf/","js":"default.I.js","properties":{":Version":"1.0",":JSHash":"3b6f7573ff78da6602dda5e09c9025bf"}},":earth":{"versions":{":1":"1",":1.0":"1"},"path":"/api/earth/1.0/db22e5693e0a8de1f62f3536f5a8d7d3/","js":"default.I.js","properties":{":Version":"1.0",":JSHash":"db22e5693e0a8de1f62f3536f5a8d7d3"}},":feeds":{"versions":{":1":"1",":1.0":"1"},"path":"/api/feeds/1.0/482f2817cdf8982edf2e5669f9e3a627/","js":"default+fr.I.js","css":"default+fr.css","properties":{":Version":"1.0",":JSHash":"482f2817cdf8982edf2e5669f9e3a627"}},":picker":{"versions":{":1":"1",":1.0":"1"},"path":"/api/picker/1.0/1c635e91b9d0c082c660a42091913907/","js":"default.I.js","css":"default.css","properties":{":Version":"1.0",":JSHash":"1c635e91b9d0c082c660a42091913907"}},":ima":{"versions":{":3":"1",":3.0":"1"},"path":"/api/ima/3.0/28a914332232c9a8ac0ae8da68b1006e/","js":"default.I.js","properties":{":Version":"3.0",":JSHash":"28a914332232c9a8ac0ae8da68b1006e"}}});
google.loader.rpl({":swfobject":{"versions":{":2.1":{"uncompressed":"swfobject_src.js","compressed":"swfobject.js"},":2.2":{"uncompressed":"swfobject_src.js","compressed":"swfobject.js"}},"aliases":{":2":"2.2"}},":chrome-frame":{"versions":{":1.0.0":{"uncompressed":"CFInstall.js","compressed":"CFInstall.min.js"},":1.0.1":{"uncompressed":"CFInstall.js","compressed":"CFInstall.min.js"},":1.0.2":{"uncompressed":"CFInstall.js","compressed":"CFInstall.min.js"}},"aliases":{":1":"1.0.2",":1.0":"1.0.2"}},":ext-core":{"versions":{":3.1.0":{"uncompressed":"ext-core-debug.js","compressed":"ext-core.js"},":3.0.0":{"uncompressed":"ext-core-debug.js","compressed":"ext-core.js"}},"aliases":{":3":"3.1.0",":3.0":"3.0.0",":3.1":"3.1.0"}},":webfont":{"versions":{":1.0.12":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.13":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.14":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.15":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.10":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.11":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.27":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.28":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.29":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.23":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.24":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.25":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.26":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.21":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.22":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.3":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.4":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.5":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.6":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.9":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.16":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.17":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.0":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.18":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.1":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.19":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"},":1.0.2":{"uncompressed":"webfont_debug.js","compressed":"webfont.js"}},"aliases":{":1":"1.0.29",":1.0":"1.0.29"}},":scriptaculous":{"versions":{":1.8.3":{"uncompressed":"scriptaculous.js","compressed":"scriptaculous.js"},":1.9.0":{"uncompressed":"scriptaculous.js","compressed":"scriptaculous.js"},":1.8.1":{"uncompressed":"scriptaculous.js","compressed":"scriptaculous.js"},":1.8.2":{"uncompressed":"scriptaculous.js","compressed":"scriptaculous.js"}},"aliases":{":1":"1.9.0",":1.8":"1.8.3",":1.9":"1.9.0"}},":jqueryui":{"versions":{":1.8.17":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.16":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.15":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.14":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.4":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.13":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.5":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.12":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.6":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.11":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.7":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.10":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.8":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.9":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.6.0":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.7.0":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.5.2":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.0":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.7.1":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.5.3":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.1":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.7.2":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.8.2":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"},":1.7.3":{"uncompressed":"jquery-ui.js","compressed":"jquery-ui.min.js"}},"aliases":{":1":"1.8.17",":1.8.3":"1.8.4",":1.5":"1.5.3",":1.6":"1.6.0",":1.7":"1.7.3",":1.8":"1.8.17"}},":mootools":{"versions":{":1.3.0":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.2.1":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.1.2":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.4.0":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.3.1":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.2.2":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.4.1":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.3.2":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.2.3":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.4.2":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.2.4":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.2.5":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"},":1.1.1":{"uncompressed":"mootools.js","compressed":"mootools-yui-compressed.js"}},"aliases":{":1":"1.1.2",":1.1":"1.1.2",":1.2":"1.2.5",":1.3":"1.3.2",":1.4":"1.4.2",":1.11":"1.1.1"}},":yui":{"versions":{":2.8.0r4":{"uncompressed":"build/yuiloader/yuiloader.js","compressed":"build/yuiloader/yuiloader-min.js"},":2.9.0":{"uncompressed":"build/yuiloader/yuiloader.js","compressed":"build/yuiloader/yuiloader-min.js"},":2.8.1":{"uncompressed":"build/yuiloader/yuiloader.js","compressed":"build/yuiloader/yuiloader-min.js"},":2.6.0":{"uncompressed":"build/yuiloader/yuiloader.js","compressed":"build/yuiloader/yuiloader-min.js"},":2.7.0":{"uncompressed":"build/yuiloader/yuiloader.js","compressed":"build/yuiloader/yuiloader-min.js"},":3.3.0":{"uncompressed":"build/yui/yui.js","compressed":"build/yui/yui-min.js"},":2.8.2r1":{"uncompressed":"build/yuiloader/yuiloader.js","compressed":"build/yuiloader/yuiloader-min.js"}},"aliases":{":2":"2.9.0",":3":"3.3.0",":2.8.2":"2.8.2r1",":2.8.0":"2.8.0r4",":3.3":"3.3.0",":2.6":"2.6.0",":2.7":"2.7.0",":2.8":"2.8.2r1",":2.9":"2.9.0"}},":prototype":{"versions":{":1.6.1.0":{"uncompressed":"prototype.js","compressed":"prototype.js"},":1.6.0.2":{"uncompressed":"prototype.js","compressed":"prototype.js"},":1.7.0.0":{"uncompressed":"prototype.js","compressed":"prototype.js"},":1.6.0.3":{"uncompressed":"prototype.js","compressed":"prototype.js"}},"aliases":{":1":"1.7.0.0",":1.6.0":"1.6.0.3",":1.6.1":"1.6.1.0",":1.7.0":"1.7.0.0",":1.6":"1.6.1.0",":1.7":"1.7.0.0"}},":jquery":{"versions":{":1.3.0":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.4.0":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.3.1":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.5.0":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.4.1":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.3.2":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.2.3":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.6.0":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.5.1":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.4.2":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.7.0":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.6.1":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.5.2":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.4.3":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.7.1":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.6.2":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.4.4":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.2.6":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.6.3":{"uncompressed":"jquery.js","compressed":"jquery.min.js"},":1.6.4":{"uncompressed":"jquery.js","compressed":"jquery.min.js"}},"aliases":{":1":"1.7.1",":1.2":"1.2.6",":1.3":"1.3.2",":1.4":"1.4.4",":1.5":"1.5.2",":1.6":"1.6.4",":1.7":"1.7.1"}},":dojo":{"versions":{":1.3.0":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.4.0":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.3.1":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.5.0":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.4.1":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.3.2":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.2.3":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.6.0":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.5.1":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.7.0":{"uncompressed":"dojo/dojo.js.uncompressed.js","compressed":"dojo/dojo.js"},":1.6.1":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.4.3":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.7.1":{"uncompressed":"dojo/dojo.js.uncompressed.js","compressed":"dojo/dojo.js"},":1.7.2":{"uncompressed":"dojo/dojo.js.uncompressed.js","compressed":"dojo/dojo.js"},":1.2.0":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"},":1.1.1":{"uncompressed":"dojo/dojo.xd.js.uncompressed.js","compressed":"dojo/dojo.xd.js"}},"aliases":{":1":"1.6.1",":1.1":"1.1.1",":1.2":"1.2.3",":1.3":"1.3.2",":1.4":"1.4.3",":1.5":"1.5.1",":1.6":"1.6.1",":1.7":"1.7.2"}}});
