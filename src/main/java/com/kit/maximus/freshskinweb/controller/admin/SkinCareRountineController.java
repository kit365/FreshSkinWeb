package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;
import com.kit.maximus.freshskinweb.entity.SkinCareRoutineEntity;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import com.kit.maximus.freshskinweb.service.skintest.SkinCareRountineService;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/skin-care-routines")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class SkinCareRountineController {

    SkinCareRountineService skinCareRountineService;

    ProductService productService;

    @PostMapping("create")
    public ResponseEntity<Boolean> addSkinCareRoutine(@RequestBody SkinCareRountineRequest request) {
        boolean result = skinCareRountineService.add(request);
        return ResponseEntity.ok(result);
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<SkinCareRountineResponse> updateSkinCareRoutine(@PathVariable Long id, @RequestBody SkinCareRountineRequest request) {
        SkinCareRountineResponse response = skinCareRountineService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Boolean> deleteSkinCareRoutine(@PathVariable Long id) {
        boolean result = skinCareRountineService.delete(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkinCareRountineResponse> getSkinCareRoutine(@PathVariable Long id) {
        SkinCareRountineResponse response = skinCareRountineService.get(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Page<SkinCareRoutineEntity> getFilteredSkinCareRoutines(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return skinCareRountineService.getFilteredSkinCareRoutines(status, keyword, PageRequest.of(page, size));
    }

}
