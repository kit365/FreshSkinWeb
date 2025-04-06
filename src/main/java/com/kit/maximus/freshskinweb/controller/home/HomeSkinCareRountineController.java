package com.kit.maximus.freshskinweb.controller.home;

import com.kit.maximus.freshskinweb.service.skintest.SkinCareRountineService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home/skincare/rountine")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeSkinCareRountineController {

    SkinCareRountineService skinCareRountineService;


}
