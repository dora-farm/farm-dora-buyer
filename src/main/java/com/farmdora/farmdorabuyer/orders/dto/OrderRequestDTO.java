package com.farmdora.farmdorabuyer.orders.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Integer depotId;
    private List<Integer> basketIds;

    // TODO 결제요청정보

}
