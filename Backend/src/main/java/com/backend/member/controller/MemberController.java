package com.backend.member.controller;


import com.backend.member.entity.Member;
import com.backend.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Member> registerMember(@Validated @RequestBody Member member) {
        try {
            System.out.println(member.getUserid());
            Member savedMember = memberService.registerMember(member);
            return ResponseEntity.ok(savedMember);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        try {
            Optional<Member> member = memberService.getMember(id);
            return member.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
