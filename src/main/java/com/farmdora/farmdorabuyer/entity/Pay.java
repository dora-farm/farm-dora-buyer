package com.farmdora.farmdorabuyer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pay extends BaseTimeEntity {

    @Id
    @Column(name = "pay_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private PayStatus status;

    @Column(nullable = false, length = 50)
    private String method;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String payNum;

    @Column(nullable = false, length = 50)
    private String card;

    @Column(nullable = false, length = 20)
    private String cardNumber;

    @Column(length = 20)
    private String accountNum;

    @Column(length = 50)
    private String bankName;
}
