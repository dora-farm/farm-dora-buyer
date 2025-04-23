package com.farmdora.farmdorabuyer.basket.service;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.repository.BasketRepository;
import com.farmdora.farmdorabuyer.common.exception.ResourceAlreadyExistsException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
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

        Optional<Basket> existsBasket = basketRepository.findByUserAndOption(user, option);
        if (existsBasket.isPresent()) {
            throw new ResourceAlreadyExistsException("Basket", existsBasket.get().getId());
        }

        Basket basket = Basket.builder()
                .user(user)
                .option(option)
                .quantity(basketAddRequest.getQuantity())
                .build();
        basketRepository.save(basket);
    }
}
