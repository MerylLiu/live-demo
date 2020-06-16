package com.swy.live;

import com.swy.live.util.MediaUtil;

public class JavavcCameraTest {
    public static void main(String[] args) throws Exception, InterruptedException {
        //设置rtmp服务器地址
        //String outputPath = "rtmp://127.0.0.1:1935/live/stream";
        //String outputPath = "rtmp://127.0.0.1:1935/hls/stream";
        //String outputPath = "rtmp://192.168.8.222:1935/hls/stream";
        String outputPath = "rtmp://192.168.8.222:1935/hls/test";
//        String outputPath = "rtmp://127.0.0.1:10085/hls/stream";
//        VideoUtil.recordPush(outputPath, 25,true);

        String inputFile = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
//        String inputFile = "rtsp://admin:admin123@192.168.8.2:554/cam/realmonitor?channel=1&subtype=0";

//        new ConvertVideoPakcet().from(inputFile).to(outputPath).go();
        MediaUtil.recordPush(inputFile, outputPath, 28);
    }
}
