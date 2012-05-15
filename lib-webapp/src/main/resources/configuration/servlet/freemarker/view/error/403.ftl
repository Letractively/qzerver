[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name="exception" type="java.lang.Exception" --]
[#import "/configuration/servlet/freemarker/macro/system.ftl" as system]
[#import "/configuration/servlet/freemarker/macro/layout.ftl" as layout]
[@layout.root_error titleCode="Access denied"]
[#escape x as x?html]

<h1>Access denied</h1>

<p>
    Сделать локальную форму логина
</p>

[#if exception??]
[@system.exceptionDump throwable=exception /]
[/#if]

[/#escape]
[/@layout.root_error]