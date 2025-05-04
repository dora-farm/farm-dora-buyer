package com.farmdora.farmdorabuyer.basket.service;

import com.farmdora.farmdorabuyer.basket.dto.BasketRequestDto;
import com.farmdora.farmdorabuyer.basket.dto.BasketResponseDto;
import com.farmdora.farmdorabuyer.basket.exception.QuantityOverLimitException;
import com.farmdora.farmdorabuyer.basket.exception.BasketOverLimitException;
import com.farmdora.farmdorabuyer.common.exception.AccessDeniedException;
import com.farmdora.farmdorabuyer.common.exception.ResourceAlreadyExistsException;
import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.common.response.PageResponseDTO;
import com.farmdora.farmdorabuyer.entity.Basket;
import com.farmdora.farmdorabuyer.entity.Option;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.orders.repository.BasketRepository;
import com.farmdora.farmdorabuyer.orders.repository.OptionRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;
    private final OptionRepository optionRepository;

    private static final int BASKET_LIMIT = 16;

    @Value("${ncp.image.path}")
    private String imagePath;

    @Value("${ncp.image.type}")
    private String imageType;

    public void addBasket(Integer userId, BasketRequestDto basketAddRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Option option = optionRepository.findById(basketAddRequest.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Option", basketAddRequest.getOptionId()));

        checkOptionQuantity(basketAddRequest.getQuantity(), option.getQuantity());
        checkBasketAlreadyExists(user, option);
        checkBasketLimit(user);

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

    private void checkBasketLimit(User user) {
        Long basketCount = basketRepository.countByUser(user);
        if (basketCount >= BASKET_LIMIT) {
            throw new BasketOverLimitException("장바구니 개수가 최대입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<BasketResponseDto> getBaskets(Integer userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Page<BasketResponseDto> baskets = basketRepository.findAllWithMainImageByUser(user, pageable);
        for (BasketResponseDto basket : baskets.getContent()) {
            if (basket.getImageUrl() != null) {
                basket.setImageUrl(imagePath + basket.getImageUrl() + imageType);
            }
        }

        return new PageResponseDTO<>(baskets, baskets.getContent());
    }

    public void removeBasket(Integer userId, Integer basketId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Basket basket = basketRepository.findByIdAndUser(basketId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Basket", basketId));

        basketRepository.delete(basket);
    }

    public void removeBaskets(Integer userId, List<Integer> basketIds) {
        List<Basket> baskets = basketRepository.findAllById(basketIds);

        for (Basket basket : baskets) {
            if (!basket.getUser().getUserId().equals(userId)) {
                throw new AccessDeniedException();
            }
        }

        basketRepository.deleteAll(baskets);
    }

    public void updateBasketQuantity(Integer userId, Integer basketId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Basket basket = basketRepository.findByIdAndUser(basketId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Basket", basketId));

        basket.updateQuantity(quantity);
    }
}
