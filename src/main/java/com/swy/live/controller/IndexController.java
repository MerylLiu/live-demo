package com.swy.live.controller;

import com.jds.core.actionResult.Json;
import com.jds.core.controller.JdsController;
import com.swy.live.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController extends JdsController {
    @Autowired
    private CameraService cameraService;

    @RequestMapping("/")
    public Json index() {
        List data = cameraService.getCameraList();
        return json(data);
    }
}
