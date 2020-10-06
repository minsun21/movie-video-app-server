package com.video.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.video.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
public class VideoController {
	private final VideoService videoService;

	@PostMapping("/upload")
	public @ResponseBody byte[] uploadVideo(@RequestParam("file") MultipartFile file) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 1. 비디오를 서버에 저장
			// 2. thumnail 이미지 추출 후 반환
			byte[] thumbnail = videoService.uploadVideo(file);
			return thumbnail;
//			result.put("result", "success");
//			result.put("thumbnail", thumbnail);
//			log.info("Rmx");
		} catch (IOException e) {
			e.printStackTrace();
//			FileUtils.deleteQuietly(targetFile);
			result.put("result", e.getMessage());
		}
		return null;
	}
}
