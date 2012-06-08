[#ftl encoding="UTF-8" strict_syntax="true" strip_whitespace="true"]
[#-- @ftlvariable name="requestContext" type="org.springframework.web.servlet.support.RequestContext" --]
[#-- @ftlvariable name="renderContext" type="org.qzerver.web.attribute.render.ExtendedRenderContext" --]
[#import "/com/gainmatrix/resources/freemarker/macro/web/system.ftl" as system]
[#import "/com/gainmatrix/resources/freemarker/macro/web/layout.ftl" as layout]
[#import "/com/gainmatrix/resources/freemarker/macro/core/helpers.ftl" as helpers]
[@layout.root_main titleCode="FreeMarker sample page"]
[#escape x as x?html]

    <p>{${helpers.getText(renderContext.now)}}</p>

    [#assign propName="web"]
    <p>${renderContext[propName]}</p>

    <p>[#if helpers.isEqualText(renderContext.timezone.ID, "UTC")]YES![/#if]</p>

    <p>[@system.msg "msg.test"/]</p>

    <p>[@system.msgParams code="msg.test2" params=["tail", "dog", 222]/]</p>

    <p>[@system.msgParams "msg.test"/]</p>

    <p>${requestContext.getContextUrl("/index")}</p>

    <p>${requestContext.getMessage("fefefe")}</p>

    <p>${requestContext.getDefaultHtmlEscape()?string}</p>

    <p>[@system.url "/index"/]</p>

    <p>[@system.urlParams "/member/{member}/picture/{picture}?mode={mode}"
        { "member": "ssss", "picture": "тест", "mode": "ура" } /]</p>

    <p>
        [#transform html_escape]
        a < b
        Romeo & Juliet
        [/#transform]
    </p>

    <p>${renderContext.now?datetime?string}</p>

    <p>${renderContext.businessModelVersion}</p>

    <p>${renderContext.businessModelVersion?c}</p>

    <p>timezone: ${renderContext.timezone.ID}</p>

    <p>locale: ${renderContext.locale.toString()}</p>

    <p>language: ${renderContext.locale.language}</p>

    <p>country: ${renderContext.locale.country}</p>

    <script type="text/javascript">
    //<![CDATA[
    function checkTwo(a, b) {
        if (a > b) {
            var div = document.createElement("div");
            var text = document.createTextNode("Hi there and greetings!");
            div.appendChild(text);
            document.body.appendChild(div);
        }
    }
    checkTwo(10, 5);
    //]]>
    </script>


[/#escape]
[/@layout.root_main]