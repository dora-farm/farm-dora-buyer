package com.farmdora.farmdorabuyer.orders.service;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Depot;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.Order;
import com.farmdora.farmdorabuyer.entity.OrderOption;
import com.farmdora.farmdorabuyer.entity.OrderStatus;
import com.farmdora.farmdorabuyer.entity.Sale;
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
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
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

    private final EntityManager em;

    public void orderFromBaskets(Integer userId, OrderFromBasketDTO orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Depot depot = depotRepository.findById(orderRequest.getDepotId())
                .orElseThrow(() -> new ResourceNotFoundException("Depot", orderRequest.getDepotId()));
        checkDepotOfUser(depot, user);

        List<Basket> baskets = basketRepository.findAllByIdIn(orderRequest.getBasketIds());
        Map<Sale, List<Basket>> groupedBaskets = groupBaskets(baskets);

        OrderStatus status = orderStatusRepository.findByName("배송준비")
                .orElseThrow(() -> new ResourceNotFoundException("OrderStatus", "배송준비"));

        saveOrders(user, depot, groupedBaskets, status);

        // TODO Pay 진행
    }

    private void checkDepotOfUser(Depot depot, User user) {
        if (!depot.getUser().equals(user)) {
            throw new NotUserOfDepotException();
        }
    }

    private Map<Sale, List<Basket>> groupBaskets(List<Basket> baskets) {
        return baskets
                .stream()
                .collect(Collectors.groupingBy(b -> b.getOption().getSale()));
    }

    private void saveOrders(User user, Depot depot, Map<Sale, List<Basket>> groupedBaskets, OrderStatus status) {
        for (Map.Entry<Sale, List<Basket>> entry : groupedBaskets.entrySet()) {
            Order order = Order.createOrder(user, status, depot.getAddress());
            orderRepository.save(order);
            saveOrderOptions(entry.getValue(), order);
        }
    }

    private void saveOrderOptions(List<Basket> baskets, Order order) {
        for (Basket basket : baskets) {
            Integer optionId = basket.getOption().getId();

            em.clear();

            Option option = optionRepository.findByIdForUpdate(optionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Option", optionId));

            checkQuantity(option, basket.getQuantity());

            OrderOption orderOption = OrderOption.createOrderOption(option, order, basket.getQuantity(), option.getPrice());
            orderOptionRepository.save(orderOption);

            option.decreaseQuantity(basket.getQuantity());
        }
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
    }
}
