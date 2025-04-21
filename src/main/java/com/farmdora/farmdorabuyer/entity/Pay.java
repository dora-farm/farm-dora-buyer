package com.farmdora.farmdorabuyer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pay extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private PayStatus status;

    @Column(length = 50)
    private String method;

    private Integer amount;

    private String payNum;

    @Column(length = 50)
    private String card;

    @Column(length = 20)
    private String cardNumber;

    @Column(length = 20)
    private String accountNum;

    @Column(length = 50)
    private String bankName;
}
