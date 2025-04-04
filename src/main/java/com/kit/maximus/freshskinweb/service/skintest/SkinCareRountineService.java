package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.rountine_step.CreationRountineStepRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.ProductResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.ProductVariantResponse;
import com.kit.maximus.freshskinweb.dto.response.RountineStepResponse;
import com.kit.maximus.freshskinweb.dto.response.SkinCareRountineResponse;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.ProductMapper;
import com.kit.maximus.freshskinweb.mapper.RountineStepMapper;
import com.kit.maximus.freshskinweb.mapper.SkinCareRoutineMapper;
import com.kit.maximus.freshskinweb.mapper.SkinTypeMapper;
import com.kit.maximus.freshskinweb.repository.ProductRepository;
import com.kit.maximus.freshskinweb.repository.RountineStepRepository;
import com.kit.maximus.freshskinweb.repository.SkinCareRountineRepository;
import com.kit.maximus.freshskinweb.repository.SkinTypeRepository;
import com.kit.maximus.freshskinweb.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class SkinCareRountineService {

    SkinCareRountineRepository skinCareRountineRepository;
    SkinCareRoutineMapper skinCareRoutineMapper;
    SkinTypeRepository skinTypeRepository;
    SkinTypeMapper skinTypeMapper;
    RountineStepMapper rountineStepMapper;
    RountineStepRepository rountineStepRepository;
    ProductService productService;
    ProductRepository productRepository;
    ProductMapper productMapper;


    @Transactional
    public boolean add(SkinCareRountineRequest request) {
        try {
            // Map data từ request sang entity
            SkinCareRoutineEntity skinCareRoutineEntity = skinCareRoutineMapper.toEntity(request);

            // check loại da có tồn tại hay không
            SkinTypeEntity skinType = skinTypeRepository.findById(request.getSkinType())
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));
            skinCareRoutineEntity.setSkinType(skinType);

            // kiểm tra loại da này đã có lộ trình trong database hay chưa
            if (skinCareRountineRepository.existsBySkinType(skinType)) {
                throw new AppException(ErrorCode.SKIN_CARE_ROUTINE_ALREADY_EXISTS);
            }

            // check user có nhập các bước trong lộ trình da hay không ( không được để 1 lộ trình mà null )
            if (request.getRountineStep() == null || request.getRountineStep().isEmpty()) {
                throw new AppException(ErrorCode.ROUTINE_STEP_NOT_FOUND);
            }

            // Lưu bảng lộ trình da trước, sau đó add thêm các field rountine step, tránh gom quá nhiều add cùng 1 lúc
            skinCareRoutineEntity = skinCareRountineRepository.save(skinCareRoutineEntity);

            List<RountineStepEntity> routineStepEntities = new ArrayList<>();
            List<Integer> usedPositions = new ArrayList<>();

            // Lấy max position hiện tại trong bảng rountine step, dùng cho việc tự động tăng position khi user không nhập
            Integer currentMaxPosition = rountineStepRepository.findMaxPosition().orElse(0);

            // Set giá trị có trong rountine step vào biến Object tạm thời, để tí nữa set cho bảng rountine
            for (CreationRountineStepRequest stepRequest : request.getRountineStep()) {
                RountineStepEntity routineStepEntity = new RountineStepEntity();
                routineStepEntity.setContent(stepRequest.getContent());
                routineStepEntity.setStep(stepRequest.getStep());
                routineStepEntity.setSkinCareRountine(skinCareRoutineEntity);

                // Xử lý position
                Integer position = stepRequest.getPosition();
                if (position != null) {
                    // Nếu vị trí trùng với những vị trí đã có thì báo lỗi
                    if (usedPositions.contains(position)) {
                        throw new AppException(ErrorCode.DUPLICATE_POSITION);
                    }
                    routineStepEntity.setPosition(position);
                } else {
                    // Nếu không có position thì tự động tăng lên 1 theo số bước đã có trong lộ trình da
                    currentMaxPosition++;
                    routineStepEntity.setPosition(currentMaxPosition);
                }
                usedPositions.add(routineStepEntity.getPosition());

                // Lưu lại lộ trình da trước, sau khi có data thì cập nhật thêm vào bảng lộ trình, tránh gom quá nhiều field add cùng 1 lúc
                RountineStepEntity savedRoutineStep = rountineStepRepository.save(routineStepEntity);

                // Lấy theo top 5 sản phẩm theo loại da và danh mục sản phẩm, ở hàm này còn có thêm rằng buộc không lấy những sản phẩm có tên != loại da đang xét
                List<ProductEntity> products = productService.getTop5BestSellerProductBySkinTypeAndProductCategory(
                        skinType.getId(),
                        stepRequest.getStep()
                );

                // Lưu cột khóa ngoại rountine step cho sản phẩm đó
                products.forEach(product -> {
                    product.setRountineStep(savedRoutineStep);
                    productRepository.save(product);
                });

                savedRoutineStep.setProduct(products);
                routineStepEntities.add(savedRoutineStep);
            }

            // Add tiếp các thông tin của bảng rountine step còn lại vào bảng lộ trình da
            skinCareRoutineEntity.setRountineStep(routineStepEntities);
            skinCareRountineRepository.save(skinCareRoutineEntity);

            return true;

        } catch (AppException e) {
            log.error("Application error in add routine: {}", e.getMessage());
            throw e;
        }
    }

    public SkinCareRountineResponse getById(Long id) {
            SkinCareRoutineEntity routineEntity = skinCareRountineRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_CARE_ROUTINE_NOT_FOUND));

            SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(routineEntity);

            List<RountineStepResponse> stepResponses = routineEntity.getRountineStep().stream()
                    .map(step -> {
                        RountineStepResponse stepResponse = rountineStepMapper.toRountineStepResponse(step);

                        List<ProductResponseDTO> productResponses = step.getProduct().stream()
                                .map(product -> {
                                    ProductResponseDTO productDTO = productMapper.productToProductResponseDTO(product);
                                    productDTO.setVariants(product.getVariants().stream()
                                            .map(variant -> {
                                                ProductVariantResponse variantResponse = new ProductVariantResponse();
                                                variantResponse.setId(variant.getId());
                                                variantResponse.setPrice(variant.getPrice());
                                                variantResponse.setUnit(variant.getUnit());
                                                variantResponse.setVolume(variant.getVolume());
                                                return variantResponse;
                                            })
                                            .collect(Collectors.toList()));
                                    return productDTO;
                                })
                                .collect(Collectors.toList());

                        stepResponse.setProduct(productResponses);
                        return stepResponse;
                    })
                    .collect(Collectors.toList());

            response.setRountineStep(stepResponses);
            return response;
    }

    public Page<SkinCareRountineResponse> getAllSkinCareRoutines(int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<SkinCareRoutineEntity> routinePage = skinCareRountineRepository.findAll(pageable);

            return routinePage.map(routineEntity -> {
                SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(routineEntity);

                // Sắp xết các bước theo position
                List<RountineStepResponse> stepResponses = routineEntity.getRountineStep().stream()
                        .sorted(Comparator.comparing(RountineStepEntity::getPosition))
                        .map(step -> {
                            RountineStepResponse stepResponse = rountineStepMapper.toRountineStepResponse(step);

                            List<ProductResponseDTO> productResponses = step.getProduct().stream()
                                    .map(product -> {
                                        ProductResponseDTO productDTO = productMapper.productToProductResponseDTO(product);
                                        productDTO.setVariants(product.getVariants().stream()
                                                .map(variant -> {
                                                    ProductVariantResponse variantResponse = new ProductVariantResponse();
                                                    variantResponse.setId(variant.getId());
                                                    variantResponse.setPrice(variant.getPrice());
                                                    variantResponse.setUnit(variant.getUnit());
                                                    variantResponse.setVolume(variant.getVolume());
                                                    return variantResponse;
                                                })
                                                .collect(Collectors.toList()));
                                        return productDTO;
                                    })
                                    .collect(Collectors.toList());

                            stepResponse.setProduct(productResponses);
                            return stepResponse;
                        })
                        .collect(Collectors.toList());

                response.setRountineStep(stepResponses);
                return response;
            });
    }

    @Transactional
    public boolean update(Long id, SkinCareRountineRequest request) {
        try {
            SkinCareRoutineEntity existingRoutine = skinCareRountineRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_CARE_ROUTINE_NOT_FOUND));

            SkinTypeEntity skinType = skinTypeRepository.findById(request.getSkinType())
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));

            // Gỡ bỏ liên kết sản phẩm từ các bước cũ
            for (RountineStepEntity oldStep : existingRoutine.getRountineStep()) {
                if (oldStep.getProduct() != null) {
                    for (ProductEntity product : oldStep.getProduct()) {
                        product.setRountineStep(null);
                        productRepository.save(product);
                    }
                    oldStep.getProduct().clear();
                }
            }

            // Xóa các bước cũ
            existingRoutine.getRountineStep().clear();

            // Cập nhật thông tin cơ bản
            existingRoutine.setSkinType(skinType);
            existingRoutine.setTitle(request.getTitle());
            existingRoutine.setDescription(request.getDescription());

            // Lưu routine để có ID
            existingRoutine = skinCareRountineRepository.save(existingRoutine);

            // Tạo và lưu các bước mới
            if (request.getRountineStep() != null) {
                for (CreationRountineStepRequest stepRequest : request.getRountineStep()) {
                    // Tạo bước mới
                    RountineStepEntity newStep = rountineStepMapper.toRountineStepEntity(stepRequest);
                    newStep.setSkinCareRountine(existingRoutine);

                    // Lưu bước mới để có ID
                    RountineStepEntity savedStep = rountineStepRepository.save(newStep);

                    // Lấy và liên kết sản phẩm
                    List<ProductEntity> products = productService.getTop5BestSellerProductBySkinTypeAndProductCategory(
                            skinType.getId(),
                            stepRequest.getStep()
                    );

                    if (products != null) {
                        for (ProductEntity product : products) {
                            product.setRountineStep(savedStep);
                            productRepository.save(product);
                        }
                        savedStep.setProduct(products);
                        rountineStepRepository.save(savedStep);
                    }

                    existingRoutine.getRountineStep().add(savedStep);
                }
            }

            // Lưu lại toàn bộ thay đổi
            skinCareRountineRepository.save(existingRoutine);
            return true;

        } catch (AppException e) {
            log.error("Lỗi ứng dụng khi cập nhật lộ trình: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi cập nhật lộ trình: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public boolean delete(Long id) {
        SkinCareRoutineEntity skinCareRoutineEntity = skinCareRountineRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKIN_CARE_ROUTINE_NOT_FOUND));
        skinCareRountineRepository.delete(skinCareRoutineEntity);
            return true;
    }

                // Get top 5 products
//
//    public boolean delete(Long id) {
//        SkinCareRoutineEntity skinCareRoutineEntity = skinCareRountineRepository.findById(id).orElse(null);
//        if(skinCareRoutineEntity != null) {
//            skinCareRoutineEntity.setDeleted(true);
//            skinCareRountineRepository.save(skinCareRoutineEntity);
//            return true;
//        }
//        return false;
//    }
//
//    public SkinCareRountineResponse get(Long id) {
//        SkinCareRoutineEntity skinCareRoutineEntity = skinCareRountineRepository.findById(id).orElse(null);
//        if (skinCareRoutineEntity != null) {
//            SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(skinCareRoutineEntity);
//            if (skinCareRoutineEntity.getSkinType() != null) {
//                response.setSkinTypeEntity(skinTypeMapper.toSkinTypeResponse(skinCareRoutineEntity.getSkinType()));
//            }
//            return response;
//        }
//        return null;
//    }
//
//    public SkinCareRountineResponse toResponse(SkinCareRoutineEntity entity) {
//        if (entity == null) {
//            return null;
//        }
//        SkinCareRountineResponse response = new SkinCareRountineResponse();
//        response.setId(entity.getId());
//        response.setRountine(entity.getRountine());
//        response.setSkinTypeEntity( skinTypeMapper.toSkinTypeResponse(entity.getSkinType()));
//        // Các trường khác
//        return response;
//    }
//
//
//
//    public Page<SkinCareRountineResponse> getFilteredSkinCareRoutines(Status status, String keyword, Pageable pageable) {
//        Specification<SkinCareRoutineEntity> spec = Specification.where(null);
//
//        if (status != null) {
//            spec = spec.and(SkinCareRoutineSpecification.filterByStatus(status));
//        }
//
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            spec = spec.and(SkinCareRoutineSpecification.filterByKeyword(keyword.trim()));
//        }
//
//        spec = spec.and(SkinCareRoutineSpecification.sortByUpdatedAt());
//        spec = spec.and(SkinCareRoutineSpecification.isNotDeleted());
//
//        Page<SkinCareRoutineEntity> entities = skinCareRountineRepository.findAll(spec, pageable);
//        return entities.map(entity -> {
//            if (entity.getSkinType() == null) {
//                entity.setSkinType(new SkinTypeEntity()); // Hoặc giá trị mặc định khác
//            }
//            return toResponse(entity);
//        });
//    }
//
//    public SkinCareRountineResponse getSkinCareRoutineByType(String skinType) {
//        SkinTypeEntity findBySkinType = skinTypeRepository.findByType(skinType);
//        SkinTypeResponse skinTypeResponse = skinTypeMapper.toSkinTypeResponse(findBySkinType);
//        SkinCareRountineResponse response = null;
//        if(findBySkinType != null) {
//            SkinCareRoutineEntity skinCareRoutine = skinCareRountineRepository.findBySkinType(findBySkinType);
//               response = skinCareRoutineMapper.toResponse(skinCareRoutine);
//               response.setSkinTypeEntity(skinTypeResponse);
//        }
//        return response;
//    }
//
//    //Show lộ trình cho loại da cụ thể và sản phẩm liên quan
//
//    public SkinCareRoutineResponseDTO getRoutineAndProducts(String skinType, Integer page, Integer size) {
//        // Lấy thông tin skin care routine
//        SkinCareRountineResponse routineInfo = getSkinCareRoutineByType(skinType);
//
//        // Lấy danh sách sản phẩm theo loại da
//        Page<ProductRoutineDTO> products = productService.getProductsBySkinTypeAndCategories(skinType, page, size);
//
//        // Kết hợp kết quả
//        return SkinCareRoutineResponseDTO.builder()
//                .skinCareRoutine(routineInfo)
//                .products(products)
//                .build();
//    }
}