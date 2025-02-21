package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SkinTypeService{

    SkinTypeRepository skinTypeRepository;


}
