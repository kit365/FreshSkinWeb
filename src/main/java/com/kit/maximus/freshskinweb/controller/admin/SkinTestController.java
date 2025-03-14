package com.kit.maximus.freshskinweb.controller.admin;

import com.kit.maximus.freshskinweb.dto.request.skin_test.CreationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinResultSearchRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_test.UpdationSkinTestRequest;
import com.kit.maximus.freshskinweb.dto.response.ResponseAPI;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.service.SkinTestService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "*")
@Slf4j
@RequestMapping("admin/skintest")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkinTestController {

    SkinTestService skinTestService;

    @PostMapping("/create")
    public ResponseAPI<SkinTestResponse> create(@RequestBody CreationSkinTestRequest request) {
        String message = "Tạo bài test da thành công";
        var result = skinTestService.add(request);
        if(!result){
            message = "Tạo bài test da thất bại";
            return ResponseAPI.<SkinTestResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message(message)
                    .build();
        }
        log.info("CREATE QUESTION REQUEST SUCCESS");

        return ResponseAPI.<SkinTestResponse>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    @PatchMapping("edit/{id}")
    public ResponseAPI<SkinTestResponse> update(@PathVariable("id") Long id, @Valid @RequestBody UpdationSkinTestRequest request) {
        String message = "Update skin type successfully";
        var result = skinTestService.update(id, request);
        if(!result){
            message = "Update skin type failed";
            return ResponseAPI.<SkinTestResponse>builder().code(HttpStatus.BAD_REQUEST.value()).message(message).build();
        }
        return ResponseAPI.<SkinTestResponse>builder().code(HttpStatus.OK.value()).message(message).build();
    }

//    @GetMapping
//    public ResponseAPI<Page<SkinTestResponse>> getAll(
//            @RequestParam(required = false) Integer page,
//            @RequestParam(required = false) Integer size,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String firstName,
//            @RequestParam(required = false) String lastName,
//            @RequestParam(required = false) String sortDirection
//    ) {
//        SkinResultSearchRequest request = SkinResultSearchRequest.builder()
//                .page(page)
//                .size(size)
//                .status(status)
//                .firstName(firstName)
//                .lastName(lastName)
//                .sortDirection(sortDirection)
//                .build();
//
//        var result = skinTestService.getAll(request);
//        return ResponseAPI.<Page<SkinTestResponse>>builder()
//                .code(HttpStatus.OK.value())
//                .message("Get all skin tests successfully")
//                .data(result)
//                .build();
//    }

    @DeleteMapping("/delete/{id}")
    public ResponseAPI<String> delete(@PathVariable("id") Long id) {
        String message = "Delete skin type successfully";
        skinTestService.delete(id);
        return ResponseAPI.<String>builder().code(HttpStatus.OK.value()).message(message).build();
    }

    @GetMapping("/{id}")
    public ResponseAPI<SkinTestResponse> getDetail(@PathVariable("id") Long id ) {
        var result = skinTestService.get(id);
        return ResponseAPI.<SkinTestResponse>builder().data(result).build();
    }
}
