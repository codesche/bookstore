package com.bookmanager.domain.book.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;

// 재고 수량 변경

@Getter
@Builder
public class StockUpdateRequest {

    @NotNull(message = "변경할 재고 수량은 필수입니다.")
    @PositiveOrZero(message = "재고 수량은 0 또는 양수여야 합니다.")
    private Integer stockQuantity;

}
