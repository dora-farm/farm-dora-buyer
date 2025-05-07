package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.BankType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTypeRepository extends JpaRepository<BankType, Short> {
}
