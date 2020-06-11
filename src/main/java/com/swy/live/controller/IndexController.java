package com.swy.live.controller;

import com.jds.core.controller.JdsController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController extends JdsController {

    @RequestMapping("/")
    public ModelAndView index() {
        return view("index/index");
    }

    @RequestMapping("/home")
    public ModelAndView home() {
        return view("index/home");
    }
}
