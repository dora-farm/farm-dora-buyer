package com.farmdora.farmdorabuyer.like.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.farmdora.farmdorabuyer.entity.Like;
import com.farmdora.farmdorabuyer.entity.Sale;
import com.farmdora.farmdorabuyer.entity.User;
import com.farmdora.farmdorabuyer.like.repository.LikeRepository;
import com.farmdora.farmdorabuyer.orders.repository.SaleRepository;
import com.farmdora.farmdorabuyer.orders.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("찜 등록 서비스 레이어 테스트")
    void testDeleteLike() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Sale mockSale = Sale.builder()
                .id(1)
                .build();
        when(saleRepository.findById(anyInt())).thenReturn(Optional.of(mockSale));

        Like mockLike = Like.builder()
                .user(mockUser)
                .sale(mockSale)
                .build();
        when(likeRepository.findByUserAndSale(any(User.class), any(Sale.class))).thenReturn(Optional.of(mockLike));

        // when
        likeService.updateLike(1, 1);

        // then
        verify(likeRepository, times(1)).delete(any(Like.class));
    }

    @Test
    @DisplayName("찜 취소 서비스 레이어 테스트")
    void testAddLike() {
        // given
        User mockUser = User.builder()
                .userId(1)
                .build();
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        Sale mockSale = Sale.builder()
                .id(1)
                .build();
        when(saleRepository.findById(anyInt())).thenReturn(Optional.of(mockSale));

        when(likeRepository.findByUserAndSale(any(User.class), any(Sale.class))).thenReturn(Optional.empty());

        // when
        likeService.updateLike(1, 1);

        // then
        verify(likeRepository, times(1)).save(any(Like.class));
    }
}