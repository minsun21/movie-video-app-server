package com.video;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.video.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	@Test
	void 바이트_테스트() {
		File thumnailFile = new File(Constants.MEMBER_IMAGE_PATH + "48f5265c-51b9-4061-8475-0df6b9fcafb7.png");
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumnailFile))) {
			String bytes = Base64.encodeBase64String(IOUtils.toByteArray(in));
			log.info("bytes : " + bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
