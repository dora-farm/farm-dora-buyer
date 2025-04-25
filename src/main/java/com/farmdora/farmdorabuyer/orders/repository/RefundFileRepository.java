package com.farmdora.farmdorabuyer.orders.repository;

import com.farmdora.farmdorabuyer.entity.RefundFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundFileRepository extends JpaRepository<RefundFile, Integer> {

    List<RefundFile> findAllByRefundId(Integer refundId);


}
