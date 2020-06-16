package com.swy.live.service.impl;

import com.extm.Db;
import com.google.common.collect.ImmutableMap;
import com.jds.core.common.BizException;
import com.jds.core.common.KendoResult;
import com.jds.core.service.impl.BaseServiceImpl;
import com.jds.core.utils.BaseUtil;
import com.jds.core.utils.DateUtil;
import com.jds.core.utils.QueryUtil;
import com.swy.live.common.Action;
import com.swy.live.service.StreamService;
import com.swy.live.util.ConvertVideoPakcet;
import com.swy.live.util.MediaUtil;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreamServiceImpl extends BaseServiceImpl implements StreamService {
    private static Logger logger = LoggerFactory.getLogger(MediaUtil.class);

    @Override
    public KendoResult getPushedPaged(Map param) {
        KendoResult data = QueryUtil.getRecordsPaged("stream.getPushedPaged", param);
        return data;
    }

    @Override
    public void addPush(Map param) {
        List errMsg = new ArrayList();

        if (BaseUtil.isNullOrEmpty(param.get("name"))) {
            errMsg.add("请输入名称");
        }
        if (BaseUtil.isNullOrEmpty(param.get("name"))) {
            errMsg.add("请输入RTSP地址");
        }

        if (errMsg.size() > 0) {
            throw new BizException(errMsg);
        }

        Integer nameCount = db("stream").where("name = #{name}", ImmutableMap.of("name", param.get("name"))).count();
        if (nameCount > 0) {
            throw new BizException("您输入的名称已存在");
        }

        Integer rtspCount = db("stream").where("rtsp = #{rtsp}", ImmutableMap.of("rtsp", param.get("rtsp"))).count();
        if (rtspCount > 0) {
            throw new BizException("您输入的RTSP地址已存在");
        }

        String path;
        if (BaseUtil.isNullOrEmpty(param.get("path"))) {
            try {
                URL url = new URL(param.get("rtsp").toString().replace("rtsp", "http"));
                path = url.getPath().replaceFirst("/", "");
            } catch (MalformedURLException e) {
                throw new BizException("RTSP地址格式不正确");
            }
        } else {
            path = param.get("path").toString().replaceFirst("/", "");
        }

        String rtmpAddr = ((Map) BaseUtil.getSysParam("RtmpAddr")).get("KeyValue").toString();
        String rtmp = String.format("rtmp://%s/hls/%s", rtmpAddr, path);
        param.put("rtmp", rtmp);
        param.put("create_date", DateUtil.getNow());
        param.put("status", 0);

        db("stream").insert(param);
    }

    @Override
    public void startPush(Integer id) {
        Map data = db("stream").fields("rtsp", "rtmp").find("id = #{id}", ImmutableMap.of("id", id));
        String input = data.get("rtsp").toString();
        String output = data.get("rtmp").toString();

        Map map = new HashMap();
        map.put("status", 1);
        db("stream").where("id = #{id}", ImmutableMap.of("id", id)).update(map);
        push(input, output, id);
    }

    @Override
    public void stopPush(Integer id) {
        Map map = new HashMap();
        map.put("status", 0);
        db("stream").where("id = #{id}", ImmutableMap.of("id", id)).update(map);
    }

    @Override
    public void deletePush(Integer id) {
        if (!BaseUtil.isNullOrEmpty(id)) {
            db("stream").delete("id = #{id}", id);
            stopPush(id);
        }
    }

    @Override
    public void push(String inputUrl, String outputUrl, Integer streamId) {
        Thread thread = new Thread(() -> {
            try {
                Map map = new HashMap();
                map.put("status", 2);
                db("stream").where("id = #{id}", ImmutableMap.of("id", streamId)).update(map);
//                new ConvertVideoPakcet().from(inputUrl).to(outputUrl).go();
                new MediaUtil().recordPush(inputUrl, outputUrl, 25);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
