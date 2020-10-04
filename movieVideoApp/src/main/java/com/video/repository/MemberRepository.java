package com.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.domain.Member;

public interface MemberRepository extends JpaRepository<Member,Long>{
	Member findByEmail(String email);
}
