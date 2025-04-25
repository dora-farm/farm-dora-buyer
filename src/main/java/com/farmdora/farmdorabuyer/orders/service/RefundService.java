package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.exception.FileException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.RefundDTO.*;
import com.farmdora.farmdorabuyer.orders.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final RefundFileRepository refundFileRepository;
    private final RefundTypeRepository refundTypeRepository;
    private final OrderRepository orderRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final PayRepository payRepository;
    private final PayStatusRepository payStatusRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final SaleFileRepository saleFileRepository;
    private final NcpImageService ncpImageService;

    @Transactional
    public RefundResponse createRefund(
            Integer userId,
            RefundRequest request,
            List<MultipartFile> files) throws IOException {
        // 주문조회
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("order", request.getOrderId()));

        // 환불 타입 조회
        RefundType refundType = refundTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("refundType", request.getTypeId()));

        // 환불 요청 생성 및 저장
        Refund refund = Refund.builder()
                .order(order)
                .type(refundType)
                .content(request.getContent())
                .isProcess(false)
                .build();

        Refund savedRefund = refundRepository.save(refund);

        // 주문 상태 변경
        OrderStatus orderStatus = orderStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("orderStatus", request.getOrderId()));
        order.setStatus(orderStatus);
        orderRepository.save(order);

        // 파일 저장할 list 생성
        List<RefundFile> savedFiles = new ArrayList<>();

        // 파일 처리 로직
        for(MultipartFile file : files) {
            try {
                String originalFileName = file.getOriginalFilename();
                String savedFileName = ncpImageService.uploadImage(file);

                RefundFile refundFile = RefundFile.builder()
                        .refund(savedRefund)
                        .originFile(originalFileName)
                        .saveFile(savedFileName)
                        .build();

                savedFiles.add(refundFileRepository.save(refundFile));
            } catch (Exception e) {
                throw new FileException("파일을 저장할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // 주문 옵션 조회 및 Sale 정보 획득
        List<OrderOption> orderOptions = orderOptionRepository.findByOrderId(order.getId());
        if(orderOptions.isEmpty()) {
            throw new ResourceNotFoundException("orderOptions", order.getId());
        }

        Sale sale = orderOptions.get(0).getOption().getSale();

        Optional<SaleFile> mainImage = saleFileRepository.findBySaleIdAndIsMainTrue(sale.getId());

        return RefundResponse.fromEntity(
                savedRefund,
                savedFiles,
                orderOptions,
                sale,
                mainImage.orElse(null),
                ncpImageService
        );
    }

    @Transactional(readOnly = true)
    public List<RefundTypeInfo> getRefundTypes() {
        return refundTypeRepository.findAll().stream()
                .map(RefundTypeInfo::fromEntity)
                .collect(Collectors.toList());
    }
}
