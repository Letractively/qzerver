package org.qzerver.web.controller;

import org.qzerver.web.map.SiteMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class RootController {

    @RequestMapping(value = SiteMap.ROOT, method = RequestMethod.GET)
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
