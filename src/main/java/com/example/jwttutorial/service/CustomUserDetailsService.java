package com.example.jwttutorial.service;

import com.example.jwttutorial.entity.Member;
import com.example.jwttutorial.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 사용자 이름을 기반으로 데이터베이스에서 사용자 정보를 조회하여 UserDetails 객체를 생성하는 메소드
    // 사용자가 존재하지 않을 경우 UsernameNotFoundException 예외를 던짐
    @Override
    public UserDetails loadUserByUsername(String membername) throws UsernameNotFoundException {
        return memberRepository.findOneWithAuthoritiesByMembername(membername)
                .map(member -> createUser(membername, member))
                .orElseThrow(() -> new UsernameNotFoundException(membername + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    // 회원 객체를 기반으로 Spring Security의 UserDetails 객체를 생성하는 메소드
    // 활성화 상태인지 확인하고, 권한 정보를 이용하여 UserDetails 객체를 생성
    private User createUser(String membername, Member member) {
        if (!member.isActivated()) {
            throw new RuntimeException(membername + " -> 활성화되어 있지 않습니다.");
        }
        // 권한 정보를 Spring Security에서 사용하는 GrantedAuthority로 변환하여 리스트로 저장
        List<GrantedAuthority> grantedAuthorities = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        // UserDetails 인터페이스를 구현하는 User 객체를 생성하여 반환
        return new User(member.getMembername(),
                member.getPassword(),
                grantedAuthorities);
    }
}
