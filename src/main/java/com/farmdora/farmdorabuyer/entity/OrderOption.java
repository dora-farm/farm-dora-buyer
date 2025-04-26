package com.farmdora.farmdorabuyer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OrderOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer quantity;

    private Integer price;

    public static OrderOption createOrderOption(Option option, Order order, Basket basket) {
        return OrderOption.builder()
                .option(option)
                .order(order)
                .quantity(basket.getQuantity())
                .price(option.getPrice() * basket.getQuantity())
                .build();
    }
}
