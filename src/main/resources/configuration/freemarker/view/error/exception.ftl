[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.render.attribute.RenderContext" --]
[#import "/configuration/freemarker/macro/system.ftl" as system]
[#import "/configuration/freemarker/macro/layout.ftl" as layout]
[@layout.root_error titleCode="Exception"]
[#escape x as x?html]

<h1>Exception</h1>

<p>
    Exception!
</p>

[/#escape]
[/@layout.root_error]