[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]

[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.RenderContext" --]

[#import "/configuration/servlet/freemarker/macro/system.ftl" as system]

[#setting locale=renderContext.locale.toString()]
[#setting time_zone=renderContext.timezone.ID]
[#setting date_format="full"]
[#setting time_format="full"]
[#setting datetime_format="full"]
[#setting url_escaping_charset="UTF-8"]

[#-- Document declaration
--]
[#macro document]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${renderContext.locale.language}" lang="${renderContext.locale.language}">
[#nested]
</html>
[/#macro]

[#-- HEAD section
 * titleCode - title message code
 * cssIncludes - list of CSS resources
 * jsIncludes - list of JS resources
--]
[#macro head titleCode="" cssIncludes=[] jsIncludes=[]]
<head>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=utf-8" />
    <link rel="author" href="http://${renderContext.web}" />
    <link rel="shortcut icon" href="[@system.resource "/favicon.ico"/]" type="image/x-icon" />
    <link href="[@system.resource "/css/common.css"/]" rel="stylesheet" type="text/css" media="all"/>
    [#list cssIncludes as cssInclude]
    <link href="[@system.resource "${cssInclude}"/]" rel="stylesheet" type="text/css" media="all"/>
    [/#list]
    [#list jsIncludes as jsInclude]
    <script src="[@system.resource "${jsInclude}"/]"></script>
    [/#list]
    [#nested]
    [#if titleCode?has_content]<title>[@system.msg titleCode/]</title>[/#if]
</head>
[/#macro]

[#-- BODY section
--]
[#macro body]
<body>
[#nested]
</body>
[/#macro]

[#-- Root template for common pages
 * titleCode - title message code
 * cssIncludes - list of CSS resources
 * jsIncludes - list of JS resources
--]
[#macro root_main titleCode="" cssIncludes=[] jsIncludes=[]]
[@document]
[@head titleCode cssIncludes jsIncludes]
[/@head]
[@body]
[#nested]
[/@body]
[/@document]
[/#macro]

[#-- Root template for error pages
 * titleCode - title message code
 * cssIncludes - list of CSS resources
 * jsIncludes - list of JS resources
--]
[#macro root_error titleCode="" cssIncludes=[] jsIncludes=[]]
[@document]
[@head titleCode cssIncludes jsIncludes]
[/@head]
[@body]
[#nested]
[/@body]
[/@document]
[/#macro]

