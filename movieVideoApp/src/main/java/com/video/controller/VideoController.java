package com.video.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.video.domain.VIDEOAUTHORITY;
import com.video.domain.VIDEOCATEGORY;
import com.video.domain.Video;
import com.video.repository.VideoRepository;
import com.video.service.VideoService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
public class VideoController {
	private final VideoService videoService;
	private final VideoRepository videoRepository;

	@AllArgsConstructor
	class Result<T> {

		private T data;

		private String result;

	}

	@Data
	class VideoDTO {
		private String title;

		private String desc;

		private String path;

		private VIDEOAUTHORITY authority;

		private VIDEOCATEGORY category;

		private String member;

		private String viewCount;
		
		private LocalDate uploadDate;
		
		public VideoDTO(Video video) {
			this.title = video.getTitle();
			this.desc = video.getDesc();
			this.path = video.getPath();
			this.authority = video.getAuthority();
			this.category = video.getCategory();
			this.member = video.getMember().getName();
			this.viewCount = video.getViewCount();
			this.uploadDate = video.getUploadDate();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/all")
	public Result abc() {
		
		List<VideoDTO> list = videoRepository.findAll().stream().map(VideoDTO::new).collect(Collectors.toList());
		return new Result(list, "success");

	}

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
}
