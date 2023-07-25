package com.example.jwttutorial.controller;

import com.example.jwttutorial.dto.MemberDTO;
import com.example.jwttutorial.entity.Member;
import com.example.jwttutorial.jwt.JwtFilter;
import com.example.jwttutorial.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Member> signup(
            @Valid @RequestBody MemberDTO memberDTO
    ) {
       return ResponseEntity.ok(memberService.signup(memberDTO));
    }

    @GetMapping("/member")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<Member> getMyMemberInfo() {
        return ResponseEntity.ok(memberService.getMyMemberWithAuthorities().get());
    }

    @GetMapping("/member/{membername}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Member> getMemberInfo(@PathVariable String membername) {
        logger.debug("요청받은 회원명: {}", membername);
        return ResponseEntity.ok(memberService.getMemberWithAuthorities(membername).get());
    }
}
