package com.swy.live.util;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_objdetect;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

public class MediaUtil {
    private static Logger logger = LoggerFactory.getLogger(MediaUtil.class);

    public static void recordPush(String inputFile, String outputFile, int v_rs) throws FrameGrabber.Exception, FrameRecorder.Exception {
        Loader.load(opencv_objdetect.class);
        long startTime = 0;

        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(inputFile);
        logger.info("开始获取FrameGrabber ：" + grabber);

        try {
            grabber.setImageHeight(grabber.getImageWidth());
            grabber.setImageWidth(grabber.getImageHeight());
            grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然會丟包很严重
            grabber.start();
        } catch (Exception e) {
            try {
                grabber.restart();
            } catch (Exception e1) {
                logger.info("出错了：" + grabber);
                throw e;
            }
        }


        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        logger.info("开始获取OpenCVFrameConverter.ToIplImage：" + converter);

        Frame grabframe = grabber.grab();
        IplImage grabbedImage = null;
        if (grabframe != null) {
            logger.info("取到第一帧");
            grabbedImage = converter.convert(grabframe);
        } else {
            logger.info("沒有取到第一帧");
        }

        //如果想要保存图片,可以使用 opencv_imgcodecs.cvSaveImage("hello.jpg", grabbedImage);來保存图片
        FFmpegFrameRecorder recorder;
        recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight());
        recorder.setGopSize(v_rs);
        recorder.setFrameRate(v_rs);
        recorder.setVideoBitrate(40000);
        AVFormatContext fc = null;
        if (outputFile.indexOf("rtmp") >= 0 || outputFile.indexOf("flv") > 0) {
            recorder.setFormat("flv");
            recorder.setAudioCodecName("aac");
            fc = grabber.getFormatContext();
        }

        //准备开始推流
        logger.info("准备开始推流...");
        try {
            recorder.start(fc);
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            try {
                logger.info("录制器启动失败，正在重新启动...");
                if (recorder != null) {
                    logger.info("尝试关闭录制器");
                    recorder.stop();
                    logger.info("尝试重新开启录制器");
                    recorder.start();
                }

            } catch (org.bytedeco.javacv.FrameRecorder.Exception e1) {
                throw e;
            }
        }

        logger.info("开始推流");
        long err_index = 0;//采集或推流导致的错误次数test
        //连续五次没有采集到帧则认为视频采集结束，程序错误次数超过1次即中断程序
        for (int no_frame_index = 0; no_frame_index < 5 || err_index > 1; ) {
            AVPacket pkt = null;
            try {
                //没有解码的音视频帧
                pkt = grabber.grabPacket();
                if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
                    //空包记录次数跳过
                    no_frame_index++;
                    continue;
                }
                //不需要编码直接把音视频帧推出去
                err_index += (recorder.recordPacket(pkt) ? 0 : 1);//如果失败err_index自增1
                av_packet_unref(pkt);
            } catch (FrameGrabber.Exception e) {//推流失败
                err_index++;
            } catch (IOException e) {
                err_index++;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        recorder.stop();
        recorder.release();
        grabber.stop();
    }
}
