package com.farmdora.farmdorabuyer.orders.controller;

import com.farmdora.farmdorabuyer.common.response.HttpResponse;
import com.farmdora.farmdorabuyer.orders.dto.RefundDTO.*;
import com.farmdora.farmdorabuyer.orders.repository.RefundTypeRepository;
import com.farmdora.farmdorabuyer.orders.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/my/user")
@RequiredArgsConstructor
@Slf4j
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/order/refund")
    public ResponseEntity<?> createRefund(
            RefundRequest request,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {

        Integer userId = 1;

        List<MultipartFile> imageList = images != null ? Arrays.asList(images) : new ArrayList<>();
        RefundResponse response = refundService.createRefund(userId, request, imageList);

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, "환불 요청이 성공적으로 등록되었습니다.", response));
    }

    @GetMapping("order/refundTypes")
    public ResponseEntity<?> getRefundTypes() {
        List<RefundTypeInfo> refundTypes = refundService.getRefundTypes();

        return ResponseEntity.ok()
                .body(new HttpResponse(HttpStatus.OK, "환불 유형 목록을 성공적으로 조회했습니다.", refundTypes));
    }
}
