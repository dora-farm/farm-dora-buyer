package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.response.PageResponseDTO;
import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.OrderResponseDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderResponseDTO.*;
import com.farmdora.farmdorabuyer.orders.dto.OrderSearchDTO;
import com.farmdora.farmdorabuyer.orders.repository.OrderOptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderRepository;
import com.farmdora.farmdorabuyer.orders.repository.PayRepository;
import com.farmdora.farmdorabuyer.orders.repository.SaleFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final PayRepository payRepository;
    private final SaleFileRepository saleFileRepository;

    public OrderService(OrderRepository orderRepository, OrderOptionRepository orderOptionRepository, PayRepository payRepository, SaleFileRepository saleFileRepository) {
        this.orderRepository = orderRepository;
        this.orderOptionRepository = orderOptionRepository;
        this.payRepository = payRepository;
        this.saleFileRepository = saleFileRepository;
    }

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

        // 옵션에서 is_main이 true인 saleId값 추출
        List<Integer> saleIds = orderOptions.stream()
                .map(orderOption -> orderOption.getOption().getSale().getId())
                .distinct() // 중복 제거
                .toList();

        //saleId를 키로, 메인 이미지를 값으로 갖는 Map
        List<SaleFile> mainFiles = saleFileRepository.findBySaleIdInAndIsMainTrue(saleIds);

        Map<Integer, SaleFile> saleFileMap = mainFiles.stream()
                .collect(Collectors.toMap(file -> file.getSale().getId(), file -> file));

        // 가공된 데이터 담는 최종 리턴 List
        List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();

        for(Order order : orders.getContent()) {
            List<OrderOption> optionsForOrder = orderOptionMap.get(order.getId());

            if (optionsForOrder != null && !optionsForOrder.isEmpty()) {
                // 첫 번째 옵션을 통해 Sale 정보를 가져옴
                OrderOption firstOption = optionsForOrder.get(0);

                // LAZY 로딩 객체에 안전하게 접근
                Option option = firstOption.getOption();

                if (option != null) {
                    Sale sale = option.getSale();
                    if (sale != null) {

                        // OptionInfoDTO 생성
                        List<OptionInfoDTO> optionInfos = new ArrayList<>();

                        for(OrderOption orderOption : optionsForOrder) {
                            Option opt = orderOption.getOption();
                            if(opt != null) {
                                OrderResponseDTO.OptionInfoDTO dto = OrderResponseDTO.OptionInfoDTO.builder()
                                        .name(opt.getName())
                                        .quantity(orderOption.getQuantity())
                                        .build();
                                optionInfos.add(dto);
                            }
                        }

                        // OrderResponseDTO 생성
                        OrderResponseDTO responseDTO = OrderResponseDTO.builder()
                                .orderId(order.getId())
                                .createdDate(order.getCreatedDate())
                                .statusId(order.getStatus().getId())
                                .title(sale.getTitle())
                                .options(optionInfos)
                                .build();

                        // Payment 정보 설정
                        Pay payment = payMap.get(order.getId());
                        if (payment != null) {
                            responseDTO.setAmount(payment.getAmount()); // 주문ID에 해당하는 총 결제 금액
                        }

                        SaleFile saleFile = saleFileMap.get(sale.getId());
                        if(saleFile != null) {
                            responseDTO.setSaveFile(saleFile.getSaveFile());
                        }

                        orderResponseDTOList.add(responseDTO);
                    }
                }
            }
        }
        return new PageResponseDTO<>(orders, orderResponseDTOList);
    }
}
