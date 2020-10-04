package com.video.service;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.video.domain.Member;
import com.video.domain.ROLE;
import com.video.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
	private final MemberRepository memberRepository;

	@Transactional
	public String registerMember(Map<String, Object> registerInfo) {
		String email = (String) registerInfo.get("email");
		try {
			duplicateInfo(email);
			String password = (String) registerInfo.get("password");
			String name = (String) registerInfo.get("name");
			Member member = Member.builder().email(email).password(password).name(name).role(ROLE.USER).build();
			memberRepository.save(member);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "success";
	}

	public Map<String, String> loginMember(Map<String, Object> loginInfo) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			String email = (String) loginInfo.get("email");
			Member findMember = memberRepository.findByEmail(email);
			if (findMember != null) {
				String passsword = (String) loginInfo.get("password");
				if (findMember.getPassword().equals(passsword)) {
					result.put("email", email);
					result.put("loginSuccess", "success");
				}else {
					result.put("loginSuccess", "비밀번호가 맞지 않습니다");
				}
			}else {
				result.put("loginSuccess", "해당 회원이 존재하지 않습니다");
			}
		} catch (Exception e) {
			log.info(">>>>>>>>>>여기");
			result.put("loginSuccess", e.getMessage());
		}
		return result;
	}

	public void duplicateInfo(String email) throws Exception {
		Member findMember = memberRepository.findByEmail(email);
		if (findMember != null)
			throw new Exception("중복 회원이 있습니다");
	}
}
