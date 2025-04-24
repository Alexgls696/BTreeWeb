package org.example.btreeweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PagesController {

    @RequestMapping(value = {"index","/"})
    public String index() {
        return "index";
    }

}
