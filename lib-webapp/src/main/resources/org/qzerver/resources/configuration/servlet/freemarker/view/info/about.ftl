[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#import "/com/gainmatrix/resources/freemarker/macro/web/system.ftl" as system]
[#import "/com/gainmatrix/resources/freemarker/macro/web/layout.ftl" as layout]
[@layout.root_main titleCode="FreeMarker sample page"]
[#escape x as x?html]

<h1>About</h1>

[/#escape]
[/@layout.root_main]