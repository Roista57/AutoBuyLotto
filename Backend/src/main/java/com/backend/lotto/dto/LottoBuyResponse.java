package com.backend.lotto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LottoBuyResponse {
    private int state;
    private String msg;
}
