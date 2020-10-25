package com.video.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.video.domain.Comment;
import com.video.service.CommentService;
import com.video.service.CommentService.CommentDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
	private final CommentService commentService;
	
	@PostMapping("/get")
	public List<Comment> allComment(@RequestBody Map<String, String> commentInfo){
		// 해당 비디오의 코멘트들 전부 가져옴
		Long videoId = Long.valueOf(commentInfo.get("videoId"));
		List<Comment> commentList = commentService.allComment(videoId);
		return commentList;
	}
	
	@PostMapping("/write")
	public Map<String,Object> writeComment(@RequestBody Map<String, String> commentInfo){
		Map<String,Object> result = new HashMap<String,Object>();
		for(String key : commentInfo.keySet()) {
			System.out.println(key);
			System.out.println(commentInfo.get(key));
		}
		// 코멘트 작성하기
		CommentDto comment = commentService.writeComment(commentInfo);
		result.put("comment", comment);
		result.put("result", "success");
		return result;
	}
}
