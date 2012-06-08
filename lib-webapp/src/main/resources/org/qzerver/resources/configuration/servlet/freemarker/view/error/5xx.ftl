[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name="exception" type="java.lang.Exception" --]
[#import "/com/gainmatrix/resources/freemarker/macro/web/system.ftl" as system]
[#import "/com/gainmatrix/resources/freemarker/macro/web/layout.ftl" as layout]
[@layout.root_error titleCode="Exception"]
[#escape x as x?html]

<h1>Ooopss!</h1>

<p>
    Server error!
</p>

[#if exception??]
[@system.exceptionDump throwable=exception /]
[/#if]

[/#escape]
[/@layout.root_error]