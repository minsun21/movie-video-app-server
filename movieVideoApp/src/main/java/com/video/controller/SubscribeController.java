package com.video.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bytedeco.javacv.FrameGrabber.Exception;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.video.service.SubscribeService;
import com.video.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

	private final SubscribeService subscribeService;

	@PostMapping("/get")
	public Map<String, Object> getSubstribe(@RequestBody Map<String, String> subscribeInfo)
			throws Exception, IOException {
		Map<String, Object> result = new HashMap<String, Object>();
		Long uploaderId = Long.valueOf(subscribeInfo.get("uploader"));
		Long loginUserId = Long.valueOf(subscribeInfo.get("loginInfo"));

		String subscribedNumber = subscribeService.allSubscribedNumber(uploaderId);
		result.put("subscribedNumber", subscribedNumber);

		String isSubscribed = subscribeService.isSubscribed(uploaderId, loginUserId);
		result.put("isSubscribed", isSubscribed);
		result.put("result", "success");
		return result;
	}

	@PostMapping("/subs")
	public Map<String, String> subscribe(@RequestBody Map<String, String> subscribeInfo) {
		Map<String, String> result = new HashMap<String, String>();
		Long uploaderId = Long.valueOf(subscribeInfo.get("uploader"));
		Long loginUserId = Long.valueOf(subscribeInfo.get("loginInfo"));

		String isSuccess = subscribeService.subscribe(uploaderId, loginUserId);
		result.put("result", isSuccess);
		return result;
	}

	@PostMapping("/dis-subscribe")
	public Map<String, String> disSubscribe(@RequestBody Map<String, String> subscribeInfo) {
		Map<String, String> result = new HashMap<String, String>();
		Long uploaderId = Long.valueOf(subscribeInfo.get("uploader"));
		Long loginUserId = Long.valueOf(subscribeInfo.get("loginInfo"));

		String isSuccess = subscribeService.disSubscribe(uploaderId, loginUserId);
		result.put("result", isSuccess);
		return result;
	}
}
