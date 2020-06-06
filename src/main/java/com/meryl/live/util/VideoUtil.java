package com.meryl.live.util;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.lang.reflect.Field;

public class VideoUtil {

    /**
     * 推流器
     *
     * @param outputPath 录制的文件路径，也可以是rtsp或者rtmp等流媒体服务器发布地址
     * @param v_rs       帧率
     * @throws Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     * @throws InterruptedException
     */
    public static void recordPush(String outputPath, int v_rs, boolean isPreview) throws Exception, org.bytedeco.javacv.FrameRecorder.Exception, InterruptedException {

        Loader.load(opencv_objdetect.class);

        //创建采集器
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);  //本地摄像头默认为0

        //开启采集器
        try {
            grabber.start();
        } catch (Exception e) {
            try {
                grabber.restart();  //一次重启尝试
            } catch (Exception e2) {
                throw e;
            }
        }

        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();  //转换器
        Frame garbage = grabber.grab();  //获取一帧
        IplImage grabbedImage = null;
        if (garbage != null) {
            grabbedImage = converter.convert(garbage); //将这一帧转换为IplImage
        }

        //创建录制器
        int width = grabbedImage.width();
        int height = grabbedImage.height();

        FrameRecorder recorder;
        recorder = FrameRecorder.createDefault(outputPath, width, height);   //输出路径，画面高，画面宽
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);  //设置编码格式
        recorder.setFormat("flv");
        recorder.setFrameRate(v_rs);
        recorder.setGopSize(v_rs);

        //开启录制器
        try {
            recorder.start();
        } catch (java.lang.Exception e) {
            System.out.println("recorder开启失败");
            System.out.println(recorder);
            try {
                if (recorder != null) {  //尝试重启录制器
                    recorder.stop();
                    recorder.start();
                }
            } catch (java.lang.Exception e1) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (isPreview) {
            //直播效果展示窗口
            CanvasFrame frame = new CanvasFrame("直播效果", CanvasFrame.getDefaultGamma() / grabber.getGamma());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setAlwaysOnTop(true);

            //推流
            while (frame.isVisible() && (garbage = grabber.grab()) != null) {
                frame.showImage(garbage);   //展示直播效果
                grabbedImage = converter.convert(garbage);
                Frame rotatedFrame = converter.convert(grabbedImage);

                if (rotatedFrame != null) {
                    recorder.record(rotatedFrame);
                }

                Thread.sleep(50);  //50毫秒/帧
            }

            frame.dispose();
        } else {
            //推流
            while ((garbage = grabber.grab()) != null) {
                grabbedImage = converter.convert(garbage);
                Frame rotatedFrame = converter.convert(grabbedImage);

                if (rotatedFrame != null) {
                    recorder.record(rotatedFrame);
                }

                //Thread.sleep(10);  //50毫秒/帧
            }
        }

        recorder.close();
        grabber.close();
    }
}
