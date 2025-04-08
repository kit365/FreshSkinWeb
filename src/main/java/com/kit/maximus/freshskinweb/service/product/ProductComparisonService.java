package com.kit.maximus.freshskinweb.service.product;

import com.kit.maximus.freshskinweb.dto.request.productcomparison.ProductComparisonDTO;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.productcomparison.ProductComparisonResponseDTO;
import com.kit.maximus.freshskinweb.entity.ProductComparisonEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.repository.ProductComparisonRepository;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProductComparisonService {

    UserRepository userRepository;

    ProductRepository productRepository;

    ProductService productService;

    ProductComparisonRepository productComparisonRepository;

    public void save(ProductComparisonDTO request) {
        UserEntity userEntity = userRepository.findById(request.getUserID())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        ProductEntity productEntity = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Tìm danh sách so sánh của user
        ProductComparisonEntity productComparison = productComparisonRepository.findByUser(userEntity);

        if (productComparison == null) {
            // Nếu chưa có danh sách so sánh -> tạo mới
            productComparison = new ProductComparisonEntity();
            productComparison.setUser(userEntity);
            productComparison.setProducts(new ArrayList<>());
        }

        // Kiểm tra xem sản phẩm đã có trong danh sách chưa
        if (!productComparison.getProducts().contains(productEntity)) {
            productComparison.getProducts().add(productEntity);
        }

        // Lưu lại danh sách so sánh
        productComparisonRepository.save(productComparison);
    }

    public void delete(ProductComparisonDTO request) {
        // Tìm ProductComparisonEntity theo id
        ProductComparisonEntity productComparison = productComparisonRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COMPARISON_NOT_FOUND));

        // Tìm ProductEntity theo productId
        ProductEntity productEntity = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Xóa sản phẩm khỏi danh sách nếu có
        if (productComparison.getProducts().contains(productEntity)) {
            productComparison.getProducts().remove(productEntity);
            productComparisonRepository.save(productComparison);
        } else {
            throw new AppException(ErrorCode.PRODUCT_NOT_IN_COMPARISON);
        }
    }

    public ProductComparisonResponseDTO findByID(Long id, Long userId) {
        ProductComparisonEntity productComparison = productComparisonRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COMPARISON_NOT_FOUND));


        if (!productComparison.getUser().getUserID().equals(userId)) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }


        ProductComparisonResponseDTO productComparisonResponseDTO = new ProductComparisonResponseDTO();
        productComparisonResponseDTO.setUserID(productComparison.getUser().getUserID());

        List<ProductResponseDTO> productResponseDTOS = productService.mapProductIndexResponsesDTO(productComparison.getProducts());
        productComparisonResponseDTO.setProducts(productResponseDTOS);


        return productComparisonResponseDTO;
    }

}
