package com.example.jwttutorial.service;

import com.example.jwttutorial.controller.MemberController;
import com.example.jwttutorial.dto.MemberDTO;
import com.example.jwttutorial.entity.Authority;
import com.example.jwttutorial.entity.Member;
import com.example.jwttutorial.entity.Role;
import com.example.jwttutorial.repository.MemberRepository;
import com.example.jwttutorial.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member signup(MemberDTO memberDTO) {
        logger.debug("service: signup...");


        // 회원 이름을 기준으로 데이터베이스에서 회원 정보를 조회하여 이미 가입되어 있는지 확인
        // 가입되어 있다면 RuntimeException 예외를 발생
        if (memberRepository.findOneWithAuthoritiesByMembername(memberDTO.getMembername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        // 회원 가입에 필요한 권한 객체를 생성
        Authority authority = Authority.builder()
                .authorityName(String.valueOf(Role.ROLE_MEMBER))
                .build();

        // 회원 정보를 생성하여 데이터베이스에 저장
        Member member = Member.builder()
                .membername(memberDTO.getMembername())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .nickname(memberDTO.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        logger.debug("service: Member: {}", member);
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMemberWithAuthorities(String membername) {
        // 회원 이름을 기준으로 데이터베이스에서 회원 정보와 권한 정보를 함께 조회
        return memberRepository.findOneWithAuthoritiesByMembername(membername);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMyMemberWithAuthorities() {
        // 현재 사용자의 이름을 기준으로 데이터베이스에서 회원 정보와 권한 정보를 함께 조회
        return SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByMembername);
    }
}

