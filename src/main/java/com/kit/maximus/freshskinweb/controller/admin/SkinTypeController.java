package com.kit.maximus.freshskinweb.controller.admin;
import com.kit.maximus.freshskinweb.dto.request.skin_type.CreateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_type.UpdateSkinTypeRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinTypeResponse;
import com.kit.maximus.freshskinweb.service.skintest.SkinTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = "*")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/admin/skintypes")
public class SkinTypeController {

    SkinTypeService skinTypeService;

    @PostMapping("create")
    public ResponseAPI<SkinTypeResponse> add(@Valid @RequestBody CreateSkinTypeRequest request) {
        String message = "Create skin type successfully";
        var result = skinTypeService.add(request);
        return ResponseAPI.<SkinTypeResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<SkinTypeResponse> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateSkinTypeRequest request) {
        String message = "Update skin type successfully";
        var result = skinTypeService.update(id, request);
        return ResponseAPI.<SkinTypeResponse>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @GetMapping("/show")
    public ResponseAPI<List<SkinTypeResponse>> getAll() {
        String message = "Get all skin type successfully";
        var result = skinTypeService.showAll();
        return ResponseAPI.<List<SkinTypeResponse>>builder().code(HttpStatus.OK.value()).message(message).data(result).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<String> delete(@PathVariable("id") Long id) {
        String message = "Delete skin type successfully";
        skinTypeService.delete(id);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<SkinTypeResponse> update(@PathVariable("id") Long id ) {
        var result = skinTypeService.searchById(id);
        return ResponseAPI.<SkinTypeResponse>builder().data(result).build();
    }
}
