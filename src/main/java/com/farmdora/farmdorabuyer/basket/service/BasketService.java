package com.farmdora.farmdorabuyer.basket.service;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto;
import com.farmdora.farmdorabuyer.basket.exception.QuantityOverLimitException;
import com.farmdora.farmdorabuyer.basket.repository.BasketRepository;
import com.farmdora.farmdorabuyer.common.exception.ResourceAlreadyExistsException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;

    public void addBasket(Integer userId, BasketRequestDto basketAddRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Option option = optionRepository.findById(basketAddRequest.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Option", basketAddRequest.getOptionId()));

        checkOptionQuantity(basketAddRequest.getQuantity(), option.getQuantity());
        checkBasketAlreadyExists(user, option);

        Basket basket = Basket.builder()
                .user(user)
                .option(option)
                .quantity(basketAddRequest.getQuantity())
                .build();
        basketRepository.save(basket);
    }

    private void checkOptionQuantity(int basketQuantity, int optionQuantity) {
        if (basketQuantity > optionQuantity) {
            throw new QuantityOverLimitException();
        }
    }

    private void checkBasketAlreadyExists(User user, Option option) {
        Optional<Basket> existsBasket = basketRepository.findByUserAndOption(user, option);
        if (existsBasket.isPresent()) {
            throw new ResourceAlreadyExistsException("Basket", existsBasket.get().getId());
        }
    }

    @Transactional(readOnly = true)
    public List<BasketResponseDto> getBaskets(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        List<Basket> baskets = basketRepository.findAllByUser(user);
        return baskets.stream()
                .map(BasketResponseDto::fromEntity)
                .toList();
    }

    public void removeBasket(Integer userId, Integer basketId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Basket basket = basketRepository.findByIdAndUser(basketId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Basket", basketId));

        basketRepository.delete(basket);
    }

    public void updateBasketQuantity(Integer userId, Integer basketId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Basket basket = basketRepository.findByIdAndUser(basketId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Basket", basketId));

        basket.updateQuantity(quantity);
    }
}
