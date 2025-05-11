package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Depot;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.Order;
import com.farmdora.farmdorabuyer.entity.OrderOption;
import com.farmdora.farmdorabuyer.entity.OrderStatus;
import com.farmdora.farmdorabuyer.entity.Pay;
import com.farmdora.farmdorabuyer.entity.PayStatus;
import com.farmdora.farmdorabuyer.entity.Seller;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromBasketDTO;
import com.farmdora.farmdorabuyer.orders.dto.OrderRequestDTO.OrderFromOptionDTO;
import com.farmdora.farmdorabuyer.orders.exception.NotUserOfDepotException;
import com.farmdora.farmdorabuyer.orders.exception.OutOfStockException;
import com.farmdora.farmdorabuyer.orders.repository.BasketRepository;
import com.farmdora.farmdorabuyer.orders.repository.DepotRepository;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderOptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderRepository;
import com.farmdora.farmdorabuyer.orders.repository.OrderStatusRepository;
import com.farmdora.farmdorabuyer.orders.repository.PayRepository;
import com.farmdora.farmdorabuyer.orders.repository.PayStatusRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseService {
    private final UserRepository userRepository;
    private final DepotRepository depotRepository;
    private final BasketRepository basketRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final OrderOptionRepository orderOptionRepository;
    private final OptionRepository optionRepository;
    private final PayRepository payRepository;
    private final PayStatusRepository payStatusRepository;

    private final EntityManager em;

    public void orderFromBaskets(Integer userId, OrderFromBasketDTO orderRequest) {
        User user = userRepository.findByUserIdWithBankType(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Depot depot = depotRepository.findById(orderRequest.getDepotId())
                .orElseThrow(() -> new ResourceNotFoundException("Depot", orderRequest.getDepotId()));
        checkDepotOfUser(depot, user);

        List<Basket> baskets = basketRepository.findAllByIdIn(orderRequest.getBasketIds());
        Map<Seller, List<Basket>> groupedBaskets = groupBaskets(baskets);

        OrderStatus status = orderStatusRepository.findByName("배송준비")
                .orElseThrow(() -> new ResourceNotFoundException("OrderStatus", "배송준비"));

        saveOrders(user, depot, groupedBaskets, status);
    }

    private void checkDepotOfUser(Depot depot, User user) {
        if (!depot.getUser().equals(user)) {
            throw new NotUserOfDepotException();
        }
    }

    private Map<Seller, List<Basket>> groupBaskets(List<Basket> baskets) {
        return baskets
                .stream()
                .collect(Collectors.groupingBy(b -> b.getOption().getSale().getSeller()));
    }

    private void saveOrders(User user, Depot depot, Map<Seller, List<Basket>> groupedBaskets, OrderStatus status) {
        for (Map.Entry<Seller, List<Basket>> entry : groupedBaskets.entrySet()) {
            Order order = Order.createOrder(user, status, depot.getAddress());
            orderRepository.save(order);

            int purchaseAmount = saveOrderOptions(entry.getValue(), order);
            savePay(user, order, purchaseAmount);
        }
    }

    private void savePay(User user, Order order, int purchaseAmount) {
        PayStatus status = payStatusRepository.findByName("결제완료")
                .orElseThrow(() -> new ResourceNotFoundException("PayStatus", "결제완료"));

        Pay pay = Pay.builder()
                .order(order)
                .status(status)
                .method("카드")
                .amount(purchaseAmount)
                .payNum(UUID.randomUUID().toString().substring(0, 10))
                .card(user.getBankType().getName())
                .cardNumber("0130924-23094")
                .accountNum(user.getAccountNum())
                .bankName(user.getBankType().getName())
                .build();
        payRepository.save(pay);
    }

    private int saveOrderOptions(List<Basket> baskets, Order order) {
        int purchaseAmount = 0;
        for (Basket basket : baskets) {
            Integer optionId = basket.getOption().getId();

            em.clear();

            Option option = optionRepository.findByIdForUpdate(optionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Option", optionId));

            checkQuantity(option, basket.getQuantity());

            OrderOption orderOption = OrderOption.createOrderOption(option, order, basket.getQuantity(), option.getPrice());
            orderOptionRepository.save(orderOption);

            purchaseAmount += orderOption.getPrice();

            option.decreaseQuantity(basket.getQuantity());
        }
        return purchaseAmount;
    }

    private void checkQuantity(Option option, Integer quantity) {
        if (option.getQuantity() < quantity) {
            log.error("주문 불가");
            throw new OutOfStockException();
        }
    }

    public void orderFromOption(Integer userId, OrderFromOptionDTO orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Depot depot = depotRepository.findById(orderRequest.getDepotId())
                .orElseThrow(() -> new ResourceNotFoundException("Depot", orderRequest.getDepotId()));
        checkDepotOfUser(depot, user);

        OrderStatus status = orderStatusRepository.findByName("배송준비")
                .orElseThrow(() -> new ResourceNotFoundException("OrderStatus", "배송준비"));

        Order order = Order.createOrder(user, status, depot.getAddress());
        orderRepository.save(order);

        Option option = optionRepository.findByIdForUpdate(orderRequest.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Option", orderRequest.getOptionId()));
        checkQuantity(option, orderRequest.getQuantity());
        option.decreaseQuantity(orderRequest.getQuantity());

        OrderOption orderOption = OrderOption.createOrderOption(option, order, orderRequest.getQuantity(), option.getPrice());
        orderOptionRepository.save(orderOption);

        savePay(user, order, orderOption.getQuantity() * orderOption.getPrice());
    }
}
