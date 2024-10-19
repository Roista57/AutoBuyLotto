package com.backend.lotto.entity;

import com.backend.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "lotto_result")
public class LottoResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @OneToOne
    @JoinColumn(name = "lottoIdx", nullable = false)
    private Lotto lotto;

    @ManyToOne
    @JoinColumn(name = "memberIdx", nullable = false)
    private Member member;

    @ColumnDefault("0")
    private Long money;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
