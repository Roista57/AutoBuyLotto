package com.backend.lotto.entity;

import com.backend.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "lotto")
public class Lotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "memberIdx", nullable = false)
    private Member member;
    private Integer round;
    private Integer num1;
    private Integer num2;
    private Integer num3;
    private Integer num4;
    private Integer num5;
    private Integer num6;
    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    @JsonIgnore
    @OneToOne(mappedBy = "lotto", cascade = CascadeType.ALL)
    private LottoResult lottoResult;
}
