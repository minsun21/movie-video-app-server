package com.video.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.video.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoService {
	private final VideoRepository videoRepository;
	private final String FILE_PATH = "src/main/resources/static/videos/";
	private final String THUMBNAIL_PATH = "src/main/resources/static/thumbnail/";
	private final String JPGE = "jpg";

	public byte[] uploadVideo(MultipartFile file) throws IOException, Exception {
		// 1. video 서버에 저장
		File targetFile = new File(FILE_PATH + file.getOriginalFilename());
		BufferedInputStream fileStream = new BufferedInputStream(file.getInputStream());
		FileUtils.copyInputStreamToFile(fileStream, targetFile);

		// 2. thumnail 이미지 추출
		return getThumbnail(targetFile);
	}

	public byte[] getThumbnail(File targetFile) throws Exception, IOException {
		long frameTime = 5000;
		FFmpegFrameGrabber video = new FFmpegFrameGrabber(targetFile);
		video.start();
		if (frameTime > 0) {
			video.setTimestamp(frameTime * 1000l); // microseconds
		}
		String thumbnail = getRemoveExtension(targetFile.getName());
		File thumnailFile = new File(THUMBNAIL_PATH + thumbnail + "." + JPGE);
		ImageIO.write(video.grab().getBufferedImage(), JPGE, thumnailFile);
		try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(thumnailFile))){
			return IOUtils.toByteArray(in);
		}
	}

	// 확장자 제거
	public String getRemoveExtension(String fileName) {
		int idx = fileName.lastIndexOf(".");
		String removeExtensionName = fileName.substring(0, idx);
		return removeExtensionName;
	}
}
