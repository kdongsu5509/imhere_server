package com.kdongsu5509.imherenotificationservice.adapter.in;

import com.kdongsu5509.imherenotificationservice.adapter.in.dto.FcmTokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/api/v1/noti")
public class FcmTokenEnrollController {

    @PostMapping("/enroll")
    void enroll(@Validated @RequestBody FcmTokenInfo fcmTokenInfo) {

    }
}
