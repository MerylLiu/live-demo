package com.swy.live.task;

import com.swy.live.util.MediaUtil;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

public class MediaThread extends Thread {
    private int frameRate;
    private String inputUrl;
    private String outputUrl;

    public MediaThread(String inputUrl, String outputUrl, int frameRate) {
        this.inputUrl = inputUrl;
        this.outputUrl = outputUrl;
        this.frameRate = frameRate;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                MediaUtil.recordPush(inputUrl, outputUrl, frameRate);
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
                break;
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
