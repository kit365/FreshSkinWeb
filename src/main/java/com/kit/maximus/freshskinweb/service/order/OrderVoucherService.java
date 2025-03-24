package com.kit.maximus.freshskinweb.service.order;

import com.kit.maximus.freshskinweb.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class OrderVoucherService {

    VoucherRepository voucherRepository;



}
