package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.common.util.NcpImageProperties;
import com.farmdora.farmdorabuyer.entity.*;
import com.farmdora.farmdorabuyer.orders.dto.OrderPayDetailDTO;
import com.farmdora.farmdorabuyer.orders.dto.SaleInfoDTO;
import com.farmdora.farmdorabuyer.orders.dto.OptionInfoDTO;
import com.farmdora.farmdorabuyer.orders.dto.SellerInfoDTO;
import com.farmdora.farmdorabuyer.orders.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayService {

    private final SaleFileRepository saleFileRepository;
    private final OrderRepository orderRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final PayRepository payRepository;
    private final NcpImageProperties imageProperties;

    @Transactional(readOnly = true)
    public OrderPayDetailDTO getOrderPayDetail(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("order", orderId));

        Pay pay = payRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pay", orderId));

        // 주문에 포함된 모든 주문 옵션 조회
        List<OrderOption> orderOptions = orderOptionRepository.findByOrder(order);
        if (orderOptions.isEmpty()) {
            throw new ResourceNotFoundException("orderOptions", orderId);
        }

        // 주문 옵션에서 상품 정보 그룹화
        Map<Sale, List<Option>> salesWithOptions = new HashMap<>();
        for (OrderOption orderOption : orderOptions) {
            Option option = orderOption.getOption();
            Sale sale = option.getSale();

            if (!salesWithOptions.containsKey(sale)) {
                salesWithOptions.put(sale, new ArrayList<>());
            }
            salesWithOptions.get(sale).add(option);
        }

        // 상품 ID 목록 추출하여 이미지 조회
        List<Integer> saleIds = salesWithOptions.keySet().stream()
                .map(Sale::getId)
                .collect(Collectors.toList());

        // 상품별 대표 이미지 조회
        Map<Integer, SaleFile> saleFileMap = new HashMap<>();
        List<SaleFile> mainFiles = saleFileRepository.findBySaleIdInAndIsMainFalse(saleIds);
        for (SaleFile file : mainFiles) {
            saleFileMap.put(file.getSale().getId(), file);
        }

        // 판매 상품 정보 생성
        List<SaleInfoDTO> salesInfo = new ArrayList<>();
        for (Map.Entry<Sale, List<Option>> entry : salesWithOptions.entrySet()) {
            Sale sale = entry.getKey();
            List<Option> options = entry.getValue();

            // 판매자 정보 가져오기
            Seller seller = sale.getSeller();
            SellerInfoDTO sellerInfo = SellerInfoDTO.builder()
                    .sellerId(seller.getId())
                    .companyName(seller.getName())
                    .addr(seller.getAddress() != null ? seller.getAddress().getAddr() : null)
                    .detailAddr(seller.getAddress() != null ? seller.getAddress().getDetailAddr() : null)
                    .companyNum(seller.getCompanyNum())
                    .phoneNum(seller.getPhoneNum())
                    .postNum(seller.getAddress() != null ? seller.getAddress().getPostNum() : null)
                    .build();

            // 각 상품의 옵션 정보 생성
            List<OptionInfoDTO> optionInfos = options.stream()
                    .map(option -> {
                        // 해당 주문 옵션 정보 찾기
                        OrderOption orderOption = orderOptions.stream()
                                .filter(oo -> oo.getOption().getId().equals(option.getId()))
                                .findFirst()
                                .orElse(null);

                        return OptionInfoDTO.builder()
                                .name(option.getName())
                                .quantity(orderOption != null ? orderOption.getQuantity() : 1)
                                .price(option.getPrice())
                                .build();
                    })
                    .collect(Collectors.toList());

            SaleFile mainFile = saleFileMap.get(sale.getId());

            salesInfo.add(SaleInfoDTO.builder()
                    .saleId(sale.getId())
                    .title(sale.getTitle())
                    .saveFile(mainFile != null ? imageProperties.getProduct().createImageUrl(mainFile.getSaveFile()) : null)
                    .options(optionInfos)
                    .seller(sellerInfo)  // 각 상품에 해당 판매자 정보 추가
                    .build());
        }

        // OrderPayDetailDTO 생성 - 첫 번째 판매자를 주문 대표 판매자로 설정
        Seller firstSeller = salesWithOptions.keySet().iterator().next().getSeller();
        return OrderPayDetailDTO.fromEntityWithSales(order, pay, salesInfo, firstSeller);
    }
}