package com.backend.member.service;

import com.backend.member.config.AESUtil;
import com.backend.member.entity.Member;
import com.backend.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AESUtil aesUtil;

    // AESUtil을 생성자 주입으로 받도록 변경
    public MemberService(MemberRepository memberRepository, AESUtil aesUtil) {
        this.memberRepository = memberRepository;
        this.aesUtil = aesUtil;  // 주입된 AESUtil 사용
    }

    public Member registerMember(Member member) throws Exception {
        String encryptedPassword = aesUtil.encrypt(member.getUserPassword());
        member.setUserPassword(encryptedPassword);
        return memberRepository.save(member);
    }

    public Optional<Member> getMember(Long id) throws Exception {
        return memberRepository.findById(id);
    }

    public List<Member> getAllMembers() {
        // findAll()을 사용하여 모든 Member 엔티티를 조회
        return memberRepository.findAll();
    }
}
