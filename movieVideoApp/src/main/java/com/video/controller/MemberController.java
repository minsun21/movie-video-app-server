package com.video.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.video.service.MemberService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class MemberController {
	private final MemberService memberService;
	
	@PostMapping("/register")
	public Map<String,String> registerMember(@RequestBody Map<String,Object> registerInfo){
		Map<String,String> result = new HashMap<String,String>();
		String resultMessage = memberService.registerMember(registerInfo);
		result.put("result", resultMessage);
		return result;
	}
	
	@PostMapping("/login")
	public Map<String,String> loginMember(@RequestBody Map<String,Object> loginInfo){
		 Map<String,String> result = memberService.loginMember(loginInfo);
		
		return result;
	}
}
