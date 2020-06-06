package com.meryl.live;

import com.meryl.live.util.VideoUtil;

public class JavavcCameraTest {
    public static void main(String[] args) throws Exception, InterruptedException {
        //设置rtmp服务器地址
        //String outputPath = "rtmp://127.0.0.1:1935/live/stream";
        String outputPath = "rtmp://127.0.0.1:1935/hls/stream";
        VideoUtil.recordPush(outputPath, 25,true);
    }
}
