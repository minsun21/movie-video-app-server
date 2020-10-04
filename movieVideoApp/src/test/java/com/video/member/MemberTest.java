package com.video.member;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import com.video.domain.Member;
import com.video.domain.ROLE;
import com.video.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@Slf4j
@SpringBootTest
class MemberTest {
	@Autowired
	MemberRepository memberRepository;

	@Transactional
	@Rollback(false)
	@Test
	void register() {
		Member member = Member.builder().email("member1@google.com").name("hong").password("12345a").role(ROLE.USER).build();
		memberRepository.save(member);
		log.info("register : " + member.getEmail());
	}
	
	@Test
	void login() {
		String email = "member1@google.com";
		Member findMember = memberRepository.findByEmail(email);
		log.info("login : " + findMember.getEmail());
	}

}
