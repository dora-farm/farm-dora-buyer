package com.farmdora.farmdorabuyer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "type_id")
    private OptionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "sale_id")
    private Sale sale;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private boolean isStop;
}
