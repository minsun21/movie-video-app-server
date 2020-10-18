package com.video.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.video.domain.Member;
import com.video.domain.Subscribe;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long>{

	List<Subscribe> findByToUser(Member toUser);
	List<Subscribe> findByFromUser(Member fromUser);
	Subscribe findByToUserAndFromUser(Member toUser, Member fromUser);
	
}
