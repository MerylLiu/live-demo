package com.swy.live.controller;

import com.jds.core.actionResult.Json;
import com.jds.core.common.KendoResult;
import com.jds.core.controller.JdsController;
import com.jds.core.utils.ConvertUtil;
import com.swy.live.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/stream")
public class StreamController extends JdsController {
    @Autowired
    private StreamService streamService;

    @GetMapping("/index")
    public ModelAndView index() {
        return view("stream/index");
    }

    @GetMapping("pushedList")
    public Json pushedList(@RequestParam Map param) {
        KendoResult data = streamService.getPushedPaged(param);
        return json(data);
    }

    @GetMapping("/addPush")
    public ModelAndView addPush(@RequestParam Map param) {
        return view("stream/addPush");
    }

    @PostMapping("doAddPush")
    public Json doAddPush(@RequestBody Map param) {
        streamService.addPush(param);
        return json("操作成功");
    }

    @PostMapping("/startPush")
    public Json startPush(@RequestBody Map param) {
        Integer id = ConvertUtil.parseInt(param.get("id"));
        streamService.startPush(id);
        return json("操作成功");
    }

    @PostMapping("/stopPush")
    public Json stopPush(@RequestBody Map param) {
        Integer id = ConvertUtil.parseInt(param.get("id"));
        streamService.stopPush(id);
        return json("操作成功");
    }

    @PostMapping("/deletePush")
    public Json deletePush(@RequestBody Map param) {
        Integer id = ConvertUtil.parseInt(param.get("id"));
        streamService.deletePush(id);
        return json("操作成功");
    }
}
