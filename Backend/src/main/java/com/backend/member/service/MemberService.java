package com.backend.member.service;

import com.backend.member.config.AESUtil;
import com.backend.member.entity.Member;
import com.backend.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AESUtil aesUtil;

    public MemberService(MemberRepository memberRepository) throws Exception {
        this.memberRepository = memberRepository;
        this.aesUtil = new AESUtil();  // 암호화 유틸리티 초기화
    }

    public Member registerMember(Member member) throws Exception {
        String encryptedPassword = aesUtil.encrypt(member.getUserPassword());
        member.setUserPassword(encryptedPassword);
        return memberRepository.save(member);
    }

    public Optional<Member> getMember(Long id) throws Exception {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            String decryptedPassword = aesUtil.decrypt(member.get().getUserPassword());
            member.get().setUserPassword(decryptedPassword);  // 복호화된 비밀번호 설정
        }
        return member;
    }
}
