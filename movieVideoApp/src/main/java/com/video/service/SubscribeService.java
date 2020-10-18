package com.video.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.video.domain.Member;
import com.video.domain.Subscribe;
import com.video.repository.MemberRepository;
import com.video.repository.SubscribeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubscribeService {
	
	private final SubscribeRepository subscribeRepsitory;
	private final MemberRepository memberRepository;

	public String allSubscribedNumber(Long uploaderId) {
		Member uploader = memberRepository.findById(uploaderId).get();
		List<Subscribe> list = subscribeRepsitory.findByToUser(uploader);
		return String.valueOf(list.size());
	}
	
	public String isSubscribed(Long uploaderId, Long loginId) {
		Member uploader = memberRepository.findById(uploaderId).get();
		Member fromUser = memberRepository.findById(loginId).get();
		Subscribe subscribe = subscribeRepsitory.findByToUserAndFromUser(uploader, fromUser);
		if(subscribe != null)
			return "true";
		else
			return "false";
	}

	public String subscribe(Long uploaderId, Long loginUserId) {
		Member uploader = memberRepository.findById(uploaderId).get();
		Member fromUser = memberRepository.findById(loginUserId).get();
		
		Subscribe subscribe = Subscribe.builder().toUser(uploader).fromUser(fromUser).build();
		subscribeRepsitory.save(subscribe);
		return "success";
	}

	public String disSubscribe(Long uploaderId, Long loginUserId) {
		Member uploader = memberRepository.findById(uploaderId).get();
		Member fromUser = memberRepository.findById(loginUserId).get();
		Subscribe subscribe = subscribeRepsitory.findByToUserAndFromUser(uploader, fromUser);
		subscribeRepsitory.delete(subscribe);
		return "success";
	}
}
