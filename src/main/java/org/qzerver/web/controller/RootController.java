package org.qzerver.web.controller;

import org.qzerver.web.map.SiteMap;
import org.qzerver.web.map.SiteViews;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class RootController {

    @RequestMapping(value = SiteMap.ROOT, method = RequestMethod.GET)
    public String handle(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        return SiteViews.INDEX;
    }

}
