package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.SaleFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleFileRepository extends JpaRepository<SaleFile, Integer> {

    List<SaleFile> findBySaleIdInAndIsMainTrue(List<Integer> saleId);
}
