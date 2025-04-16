package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.response.PageResponseDTO;
import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.OrderResponseDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderResponseDTO.*;
import com.farmdora.farmdorabuyer.orders.dto.OrderSearchDTO;
import com.farmdora.farmdorabuyer.orders.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final PayRepository payRepository;
    private final SaleFileRepository saleFileRepository;
    private final ReviewRepositry reviewRepositry;

    @Transactional(readOnly = true)
    public PageResponseDTO<OrderResponseDTO> getOrderList(Integer userId, OrderSearchDTO orderSearchDTO, Pageable pageable) {
        // 날짜에 맞는 사용자의 주문 리스트 내림차순
        Page<Order> orders = orderRepository.
                findAllByUserUserIdAndCreatedDateBetweenOrderByCreatedDateDesc(
                        userId,
                        orderSearchDTO.getStartDate(),
                        orderSearchDTO.getEndDate(),
                        pageable
                );

        // 각 주문에 연관된 옵션 값(옵션A, 옵션B, 각각의 개수)
        List<OrderOption> orderOptions = orderOptionRepository.findAllByOrderIn(orders.getContent());

        // 각 주문에 해당하는 결제 정보 조회
        List<Pay> payments = payRepository.findByOrderIn(orders.getContent());

        // orderId를 키로, 해당 주문의 모든 옵션 리스트를 값으로 갖는 Map
        Map<Integer, List<OrderOption>> orderOptionMap = orderOptions.stream()
                .collect(Collectors.groupingBy(option -> option.getOrder().getId()));

        // orderId를 키로, 결제 정보를 값으로 갖는 Map
        Map<Integer, Pay> payMap = payments.stream()
                .collect(Collectors.toMap(pay -> pay.getOrder().getId(), pay -> pay));

        // 옵션에서 saleId값 추출
        List<Integer> saleIds = orderOptions.stream()
                .map(orderOption -> orderOption.getOption().getSale().getId())
                .distinct() // 중복 제거
                .toList();

        //saleId를 키로, 메인 이미지를 값으로 갖는 Map
        List<SaleFile> mainFiles = saleFileRepository.findBySaleIdInAndIsMainTrue(saleIds);

        Map<Integer, SaleFile> saleFileMap = mainFiles.stream()
                .collect(Collectors.toMap(file -> file.getSale().getId(), file -> file));

        // 리뷰가 작성된 주문 ID 목록 조회
        Set<Integer> reviewSalesIds = reviewRepositry.findSaleIdsWithReviewsBySaleIds(saleIds);

        // 가공된 데이터 담는 최종 리턴 List
        List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();

        for(Order order : orders.getContent()) {
            List<OrderOption> optionsForOrder = orderOptionMap.get(order.getId());

            if (optionsForOrder != null && !optionsForOrder.isEmpty()) {
                // 주문 정보 기본 세팅
                OrderResponseDTO responseDTO = OrderResponseDTO.builder()
                        .orderId(order.getId())
                        .createdDate(order.getCreatedDate())
                        .build();

                // Payment 정보 설정
                Pay payment = payMap.get(order.getId());
                if (payment != null) {
                    responseDTO.setAmount(payment.getAmount());
                }

                // Sale별로 옵션을 그룹화
                Map<Integer, List<OrderOption>> optionsBySale = optionsForOrder.stream()
                        .collect(Collectors.groupingBy(opt -> opt.getOption().getSale().getId()));

                // Sale별로 정보 생성
                List<SaleInfoDTO> salesList = new ArrayList<>();

                for (Map.Entry<Integer, List<OrderOption>> entry : optionsBySale.entrySet()) {
                    Integer saleId = entry.getKey();
                    List<OrderOption> saleOptions = entry.getValue();

                    if (!saleOptions.isEmpty()) {
                        // 첫 번째 옵션을 통해 Sale 정보를 가져옴
                        Sale sale = saleOptions.get(0).getOption().getSale();

                        // 옵션 정보 생성
                        List<OptionInfoDTO> optionInfos = new ArrayList<>();
                        for (OrderOption orderOption : saleOptions) {
                            Option opt = orderOption.getOption();
                            OptionInfoDTO optionDto = OptionInfoDTO.builder()
                                    .name(opt.getName())
                                    .quantity(orderOption.getQuantity())
                                    .price(orderOption.getPrice())
                                    .build();
                            optionInfos.add(optionDto);
                        }

                        // Sale 정보 생성
                        SaleInfoDTO saleInfo = SaleInfoDTO.builder()
                                .saleId(saleId)
                                .title(sale.getTitle())
                                .statusId(order.getStatus().getId())
                                .reviewCompleted(reviewSalesIds.contains(saleId))
                                .options(optionInfos)
                                .build();

                        // 이미지 정보 설정
                        SaleFile saleFile = saleFileMap.get(saleId);
                        if (saleFile != null) {
                            saleInfo.setSaveFile(saleFile.getSaveFile());
                        }

                        salesList.add(saleInfo);
                    }
                }

                responseDTO.setSales(salesList);
                orderResponseDTOList.add(responseDTO);
            }
        }
        return new PageResponseDTO<>(orders, orderResponseDTOList);
    }
}
