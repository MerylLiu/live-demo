package com.swy.live.config;

import com.extm.Db;
import com.google.common.collect.ImmutableMap;
import com.jds.core.utils.ConvertUtil;
import com.swy.live.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MyCommandLineRunner implements CommandLineRunner {
    @Autowired
    private StreamService streamService;

    @Override
    public void run(String... args) throws Exception {
        Db.table("stream").update(ImmutableMap.of("status", 0));

        List<Map> list = Db.table("stream").select();
        list.forEach(p -> {
            String inputUrl = p.get("rtsp").toString();
            String outputUrl = p.get("rtmp").toString();

            Integer id = ConvertUtil.parseInt(p.get("id"));

            Map map = new HashMap();
            map.put("status", 1);
            Db.table("stream").where("id = #{id}", ImmutableMap.of("id", id)).update(map);
            streamService.push(inputUrl, outputUrl, id);
        });
    }
}
