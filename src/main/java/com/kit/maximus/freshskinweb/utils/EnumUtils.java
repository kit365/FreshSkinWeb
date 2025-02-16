package com.kit.maximus.freshskinweb.utils;

import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumUtils {

    private static  Status getStatus(String status)  {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }
}
