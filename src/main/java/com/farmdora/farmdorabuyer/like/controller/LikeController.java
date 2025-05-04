package com.farmdora.farmdorabuyer.like.controller;

import static com.farmdora.farmdorabuyer.common.response.SuccessMessage.ADD_LIKE_SUCCESS;

import com.farmdora.farmdorabuyer.common.response.HttpResponse;
import com.farmdora.farmdorabuyer.like.service.LikeService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/{saleId}")
    public ResponseEntity<?> addLike(Principal principal, @PathVariable("saleId") Integer saleId) {
        Integer userId = Integer.parseInt(principal.getName());
        likeService.updateLike(userId, saleId);
        return ResponseEntity.ok().body(new HttpResponse(HttpStatus.OK, ADD_LIKE_SUCCESS.getMessage(), null));
    }
}
