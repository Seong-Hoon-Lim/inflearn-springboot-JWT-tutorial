package com.example.jwttutorial.repository;

import com.example.jwttutorial.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * username 을 기준으로 Member 정보를 가져올 때
     * 권한 정보도 같이 가져오는 메소드
     * @param membername
     * @membername
     */
    //@EntityGraph 는 쿼리가 수행될 때 Eager 조회로 권한정보를 가져옴
    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findOneWithAuthoritiesByMembername(String membername);

}
