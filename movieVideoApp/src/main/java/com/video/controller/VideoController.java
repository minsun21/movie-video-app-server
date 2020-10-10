package com.video.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.RegEx;

import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@PostMapping("/submit")
	public Map<String, Object> submitVideo(@RequestBody Map<String, String> submitInfo) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : submitInfo.keySet()) {
			log.info("key : " + key);
			log.info("value : " + submitInfo.get(key));
		}
		videoService.submitVideo(submitInfo);
		result.put("result", "success");
		return result;
	}

//	@PostMapping("/upload")
//	public @ResponseBody byte[] uploadVideo(@RequestParam("file") MultipartFile file) throws Exception {
//		try {
//			// 1. 비디오를 서버에 저장
//			// 2. thumnail 이미지 추출 후 반환
//			byte[] thumbnail = videoService.uploadVideo(file);
//			return thumbnail;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	@PostMapping("/upload")
	public @ResponseBody Map<String, Object> uploadVideo(@RequestParam("file") MultipartFile file) throws Exception {
		try {
			// 1. 비디오를 서버에 저장
			// 2. thumnail 이미지 추출 후 반환
			Map<String, Object> result= videoService.uploadVideo(file);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
