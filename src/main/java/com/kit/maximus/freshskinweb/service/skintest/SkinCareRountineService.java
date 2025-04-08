package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.rountine_step.CreationRountineStepRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.SkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.request.skin_care_rountine.UpdationSkinCareRountineRequest;
import com.kit.maximus.freshskinweb.dto.response.*;
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
import org.springframework.data.domain.Sort;
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
            SkinCareRoutineEntity skinCareRoutineEntity = skinCareRoutineMapper.toEntity(request);

            // check loại da có tồn tại hay không
            SkinTypeEntity skinType = skinTypeRepository.findById(request.getSkinType())
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));
            skinCareRoutineEntity.setSkinType(skinType);

            // kiểm tra loại da này đã có lộ trình trong database hay chưa
            if (skinCareRountineRepository.existsBySkinType(skinType)) {
                throw new AppException(ErrorCode.SKIN_CARE_ROUTINE_ALREADY_EXISTS);
            }

            // check user có nhập các step cho lộ trình da hay không ( không được để 1 lộ trình == null )
            if (request.getRountineStep() == null || request.getRountineStep().isEmpty()) {
                throw new AppException(ErrorCode.ROUTINE_STEP_NOT_FOUND);
            }

            // Lưu bảng lộ trình da trước, sau đó add thêm các field rountine step, tránh gom quá nhiều add cùng 1 lúc
            skinCareRoutineEntity = skinCareRountineRepository.save(skinCareRoutineEntity);

            List<RountineStepEntity> routineStepEntities = new ArrayList<>();
            List<Integer> usedPositions = new ArrayList<>();

            // Lấy max position hiện tại trong bảng rountine step, dùng cho việc tự động tăng position khi user không nhập
            Integer currentMaxPosition = 0;

            // Set giá trị có trong rountine step vào biến Object tạm thời, để tí nữa set cho bảng rountine
            for (CreationRountineStepRequest stepRequest : request.getRountineStep()) {
                RountineStepEntity routineStepEntity = new RountineStepEntity();
                routineStepEntity.setContent(stepRequest.getContent());
                routineStepEntity.setStep(stepRequest.getStep());
                routineStepEntity.setSkinCareRountine(skinCareRoutineEntity);

                // Xử lý position
                Integer position = stepRequest.getPosition();
                if (position != null) {
                    // check position trùng nhau
                    if (usedPositions.contains(position)) {
                        throw new AppException(ErrorCode.DUPLICATE_POSITION);
                    }
                    routineStepEntity.setPosition(position);
                    usedPositions.add(position);
                } else {
                    // Không nhập position thì tự động tăng ++
                    currentMaxPosition++;
                    routineStepEntity.setPosition(currentMaxPosition);
                    usedPositions.add(currentMaxPosition);
                }

                // Lưu lại lộ trình da trước, sau khi có data product thì cập nhật thêm vào bảng lộ trình, tránh gom quá nhiều field add cùng 1 lúc
                RountineStepEntity savedRoutineStep = rountineStepRepository.save(routineStepEntity);

                //PHẦN HAY: TỰ ĐỘNG LẤY PRODUCT THEO DANH MỤC SẢN PHẨM VÀ LOẠI DA
                // Danh mục sản phẩm sẽ dựa vào tên bước chăm sóc da mà user nhập vào ( chỉ cần trong step có chứa keyword: "toner", "serum", "tẩy trang",... là được )
                // Lấy theo top 5 sản phẩm theo loại da và danh mục sản phẩm, ở hàm này còn có thêm rằng buộc không lấy những sản phẩm có tên != loại da đang xét
                List<ProductEntity> products = productService.getTop5BestSellerProductBySkinTypeAndProductCategory(
                        skinType.getId(),
                        stepRequest.getStep()
                );

                // Lưu giá trị FK routine step vào bảng product
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

    @Transactional(readOnly = true)
    public SkinCareRountineResponse getById(Long id) {
        SkinCareRoutineEntity routineEntity = skinCareRountineRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKIN_CARE_ROUTINE_NOT_FOUND));

        SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(routineEntity);
        response.setSkinType(skinTypeMapper.toSkinTypeResponse(routineEntity.getSkinType()));

        List<RountineStepResponse> stepResponses = routineEntity.getRountineStep().stream()
                .sorted(Comparator.comparing(RountineStepEntity::getPosition, Comparator.nullsLast(Comparator.naturalOrder())))
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

                                ProductBrandResponse brandResponse = new ProductBrandResponse();
                                brandResponse.setId(product.getBrand().getId());
                                brandResponse.setTitle(product.getBrand().getTitle());
                                productDTO.setBrand(brandResponse);

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

    @Transactional(readOnly = true)
    public SkinCareRountineResponse getBySkinType(String skinType) {
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findByType(skinType);
        if(skinTypeEntity == null){
            throw new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND);
        }

        SkinCareRoutineEntity routineEntity = skinCareRountineRepository.findBySkinType(skinTypeEntity);
        SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(routineEntity);
        response.setSkinType(skinTypeMapper.toSkinTypeResponse(routineEntity.getSkinType()));

        List<RountineStepResponse> stepResponses = routineEntity.getRountineStep().stream()
                .sorted(Comparator.comparing(RountineStepEntity::getPosition, Comparator.nullsLast(Comparator.naturalOrder())))
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

                                ProductBrandResponse brandResponse = new ProductBrandResponse();
                                brandResponse.setId(product.getBrand().getId());
                                brandResponse.setTitle(product.getBrand().getTitle());
                                productDTO.setBrand(brandResponse);

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

    @Transactional(readOnly = true)
    public Page<SkinCareRountineResponse> getAllSkinCareRoutines(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<SkinCareRoutineEntity> routinePage = skinCareRountineRepository.findAll(pageable);

        return routinePage.map(routineEntity -> {
            SkinCareRountineResponse response = skinCareRoutineMapper.toResponse(routineEntity);
            response.setSkinType(skinTypeMapper.toSkinTypeResponse(routineEntity.getSkinType()));

            List<RountineStepResponse> stepResponses = routineEntity.getRountineStep().stream()
                    .sorted(Comparator.comparing(RountineStepEntity::getPosition, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(step -> {
                        RountineStepResponse stepResponse = rountineStepMapper.toRountineStepResponse(step);

                        List<ProductResponseDTO> productResponses = step.getProduct().stream()
                                .map(product -> {
                                    ProductResponseDTO productDTO = productMapper.productToProductResponseDTO(product);

                                    // map data từ variant qua product bằng stream
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

                                    // map data từ brand qua product
                                    ProductBrandResponse brandResponse = new ProductBrandResponse();
                                    brandResponse.setId(product.getBrand().getId());
                                    brandResponse.setTitle(product.getBrand().getTitle());
                                    productDTO.setBrand(brandResponse);
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
    public boolean update(Long id, UpdationSkinCareRountineRequest request) {
        try {
            SkinCareRoutineEntity existingRoutine = skinCareRountineRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_CARE_ROUTINE_NOT_FOUND));


            SkinTypeEntity skinType = skinTypeRepository.findById(existingRoutine.getSkinType().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND));

            existingRoutine.setSkinType(skinType);

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

            //Cập nhật lại position
            List<Integer> usedPositions = new ArrayList<>();

            Integer currentMaxPosition = 0;

            // Tạo và lưu các bước mới
            if (request.getRountineStep() != null) {
                for (CreationRountineStepRequest stepRequest : request.getRountineStep()) {
                    // Tạo bước mới
                    RountineStepEntity newStep = rountineStepMapper.toRountineStepEntity(stepRequest);

                    if(stepRequest.getPosition() != null) {
                        // check position trùng nhau
                        if (usedPositions.contains(stepRequest.getPosition())) {
                            throw new AppException(ErrorCode.DUPLICATE_POSITION);
                        }
                        newStep.setPosition(stepRequest.getPosition());
                        usedPositions.add(stepRequest.getPosition());
                    } else {
                        // Không nhập position thì tự động tăng ++
                        currentMaxPosition++;
                        newStep.setPosition(currentMaxPosition);
                        usedPositions.add(currentMaxPosition);
                    }

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
        SkinCareRoutineEntity routine = skinCareRountineRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKIN_CARE_ROUTINE_NOT_FOUND));

        // Clear product references first
        for (RountineStepEntity step : routine.getRountineStep()) {
            // Remove routine_step_id from products
            List<ProductEntity> products = step.getProduct();
            if (products != null) {
                products.forEach(product -> {
                    product.setRountineStep(null);
                    productRepository.save(product);
                });
            }
        }

        // Now safe to delete routine and its steps
        skinCareRountineRepository.delete(routine);
        return true;
    }

}