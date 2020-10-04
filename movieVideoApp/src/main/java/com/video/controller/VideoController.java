package com.video.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	private final String FILE_PATH = "src/main/resources/static/videos/";

	@PostMapping("/upload")
	public Map<String, String> uploadVideo(@RequestParam("file") MultipartFile file) {
		Map<String, String> result = new HashMap<String, String>();
		File targetFile = new File(FILE_PATH + file.getOriginalFilename());
		try {
			InputStream fileStream = file.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);
			result.put("path", targetFile.getName());
			result.put("result", "success");
		} catch (IOException e) {
			e.printStackTrace();
			FileUtils.deleteQuietly(targetFile);
			result.put("result", e.getMessage());
		}
		return result;
	}
	
//	@PostMapping("/upload")
//	public Map<String, String> uploadVideo(@RequestParam("file") MultipartFile file) {
//		Map<String, String> result = new HashMap<String, String>();
//		File targetFile = new File(FILE_PATH + file.getOriginalFilename());
//		try {
//			InputStream fileStream = file.getInputStream();
//			FileUtils.copyInputStreamToFile(fileStream, targetFile);
//			result.put("path", targetFile.getName());
//			log.info("path: " + targetFile.getName());
//			result.put("result", "success");
//		} catch (IOException e) {
//			e.printStackTrace();
//			FileUtils.deleteQuietly(targetFile);
//			result.put("result", e.getMessage());
//		}
//		return result;
//	}
	
}
