[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#-- @ftlvariable name=".vars['javax.servlet.error.exception']" type="java.lang.Throwable" --]
[#import "/configuration/servlet/freemarker/macro/system.ftl" as system]
[#import "/configuration/servlet/freemarker/macro/layout.ftl" as layout]
[@layout.root_error titleCode="Exception"]
[#escape x as x?html]

<h1>Exception</h1>

<p>
    Exception!
</p>

[#if .vars["javax.servlet.error.exception"]??]
[@system.exceptionDump throwable=.vars["javax.servlet.error.exception"] /]
[/#if]

[/#escape]
[/@layout.root_error]