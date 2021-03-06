package com.video.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.video.service.VideoService;
import com.video.service.VideoService.VideoDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
public class VideoController {
	private final VideoService videoService;
	
	@ResponseBody
	@GetMapping("/all")
	public List<VideoDTO> getAllVideo() {
		List<VideoDTO> list = videoService.getAllVideo();
		return list;
	}

	@PostMapping("/submit")
	public Map<String, Object> submitVideo(@RequestBody Map<String, String> submitInfo) throws Exception, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : submitInfo.keySet()) {
			log.info("key : " + key);
			log.info("value : " + submitInfo.get(key));
		}
		videoService.submitVideo(submitInfo);
		result.put("result", "success");
		return result;
	}

	@PostMapping("/upload")
	public @ResponseBody Map<String, Object> uploadVideo(@RequestParam("file") MultipartFile file) throws Exception {
		try {
			// 1. 비디오를 서버에 저장
			// 2. thumnail 이미지 추출 후 반환
			Map<String, Object> result = videoService.uploadVideo(file);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@PostMapping("/getVideo")
	public @ResponseBody VideoDTO getDetailVideo(@RequestBody Map<String, String> videoId) {
		String id = videoId.get("videoId");
		VideoDTO video = videoService.getDetailVideo(Long.valueOf(id));
		return video;
	}
	
	@PostMapping("/getSideVideos")
	public List<VideoDTO> getSideViewVideos(@RequestBody Map<String, String> videoInfo) {
		// 현재 디테일 페이지 비디오 제외
		String id = videoInfo.get("videoId");
		List<VideoDTO> list = videoService.getAllVideo();
		List<VideoDTO> result = new ArrayList<VideoDTO>();
		int cnt = 0;
		// 4개만 리턴 하기
		for (VideoDTO videoDTO : list) {
			if(cnt == 4)
				break;
			if(!String.valueOf(videoDTO.getId()).equals(id)) {
				result.add(videoDTO);
				cnt++;
			}
		}
		return result;
	}
}
