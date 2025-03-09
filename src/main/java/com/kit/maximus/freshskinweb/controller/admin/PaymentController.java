package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.response.CreateMomoResponse;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.service.MomoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/momo")
public class PaymentController {


    MomoService momoService;

    @PostMapping("create")
    ResponseAPI<CreateMomoResponse> createQR() {
        String message = "Tạo mã QR thành công";
        CreateMomoResponse response = momoService.createQR();
        return ResponseAPI.<CreateMomoResponse>builder().code(HttpStatus.OK.value()).message(message).data(response).build();
    }


}

