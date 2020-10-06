package com.video;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MovieVideoAppApplicationTests {
	@Test
	void contextLoads2() throws Exception, IOException, InterruptedException {
		long frameTime = 5000;
		FFmpegFrameGrabber video = new FFmpegFrameGrabber("d://A Thousand Miles.mp4");
		video.start();
		if (frameTime > 0) {
			video.setTimestamp(frameTime * 1000l); // microseconds
		}
		ImageIO.write(video.grab().getBufferedImage(), "png",
				new File("d://A Thousand Miles-" + System.currentTimeMillis() + ".png"));
	}
}
