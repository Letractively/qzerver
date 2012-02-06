[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.RenderContext" --]
[#import "/configuration/servlet/freemarker/macro/system.ftl" as system]
[#import "/configuration/servlet/freemarker/macro/layout.ftl" as layout]
[@layout.root_error titleCode="Exception"]
[#escape x as x?html]

<h1>Ooopss!</h1>

<p>
    No such page!
</p>

[/#escape]
[/@layout.root_error]