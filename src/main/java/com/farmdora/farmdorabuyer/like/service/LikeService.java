package com.farmdora.farmdorabuyer.like.service;

import com.farmdora.farmdorabuyer.common.exception.ResourceNotFoundException;
import com.farmdora.farmdorabuyer.entity.Like;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.like.repository.LikeRepository;
import com.farmdora.farmdorabuyer.orders.repository.SaleRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final SaleRepository saleRepository;

    @Transactional
    public void updateLike(Integer userId, Integer saleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", saleId));

        deleteOrAddLike(user, sale);
    }

    private void deleteOrAddLike(User user, Sale sale) {
        Optional<Like> savedLike = likeRepository.findByUserAndSale(user, sale);
        if (savedLike.isPresent()) {
            likeRepository.delete(savedLike.get());
        } else {
            likeRepository.save(Like.builder()
                    .user(user)
                    .sale(sale)
                    .build());
        }
    }
}
