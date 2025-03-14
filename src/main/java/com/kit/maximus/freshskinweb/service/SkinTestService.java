package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinTestRequest;
//import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinResultSearchRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinTestMapper;
import com.kit.maximus.freshskinweb.repository.*;
//import com.kit.maximus.freshskinweb.specification.SkinTestSpecification;
import com.kit.maximus.freshskinweb.utils.SkinType;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SkinTestService {

    SkinTestRepository skinTestRepository;
    SkinTestMapper skinTestMapper;
    UserRepository userRepository;
    SkinTypeRepository skinTypeRepository;
    SkinQuestionsRepository skinQuestionsRepository;
    QuestionGroupRepository questionGroupRepository;

    private String determineSkinType(Long totalScore) {
        if (totalScore >= 0 && totalScore <= 7) {
            return "Da thường";
        } else if (totalScore >= 8 && totalScore <= 14) {
            return "DRY";
        } else if (totalScore >= 15 && totalScore <= 21) {
            return "COMBINATION";
        } else if (totalScore >= 22 && totalScore <= 28) {
            return "OILY";
        } else if (totalScore >= 29 && totalScore <= 36) {
            return "SENSITIVE";
        } else {
            throw new AppException(ErrorCode.INVALID_SCORE_RANGE);
        }
    }

    public boolean add(SkinTestRequest request) {
        // Validate input
        UserEntity user = userRepository.findById(request.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        QuestionGroupEntity questionGroup = questionGroupRepository.findById(request.getQuestionGroup())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND));

        // Xóa kết quả test cũ nếu có
        SkinTestEntity existingTest = skinTestRepository.findByUser(user);
        if (existingTest != null) {
            skinTestRepository.delete(existingTest);
        }

        // Tạo entity mới
        SkinTestEntity skinTest = new SkinTestEntity();
        skinTest.setUser(user);
        skinTest.setQuestionGroup(questionGroup);
        skinTest.setTotalScore(request.getTotalScore());

        // Xác định loại da dựa trên điểm số
        try {
            String skinTypeStr = determineSkinType(request.getTotalScore());
            SkinType skinType = SkinType.valueOf(skinTypeStr);
            String getVNname = skinType.getVNESEname();
            log.info("getVNname:{}", getVNname);

            // Log để debug
            log.info("Điểm số: {}, Loại da xác định: {}", request.getTotalScore(), skinTypeStr);

            // Tìm entity loại da tương ứng
            SkinTypeEntity skinTypeEntity = skinTypeRepository.findByType(getVNname);
            log.info(" lấy skin type theo trong repo: {}", skinTypeEntity);
            if (skinTypeEntity == null) {
                log.error("Không tìm thấy loại da {} trong database", skinTypeStr);
                throw new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND);
            }

            // Cập nhật thông tin
            skinTest.setSkinType(skinTypeEntity);
            skinTest.setNotes(skinType.getMessage());

            // Cập nhật loại da cho user
            user.setSkinType(skinType.getVNESEname());
            userRepository.save(user);

            // Lưu kết quả test
            skinTestRepository.save(skinTest);

            return true;

        } catch (IllegalArgumentException e) {
            log.error("Lỗi xác định loại da: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_SKIN_TYPE);
        }
    }

    public SkinTestResponse getDetail(Long id){
        SkinTestEntity skinTestEntity = skinTestRepository.findById(id).orElse(null);
        SkinTestResponse skinTestResponse = skinTestMapper.toSkinTestResponse(skinTestEntity);
        skinTestResponse.setId(skinTestEntity.getId());
        skinTestResponse.setSkinType(skinTestEntity.getSkinType().getType());
        skinTestResponse.setTotalScore(skinTestEntity.getTotalScore());
        skinTestResponse.setNotes(skinTestEntity.getNotes());
        skinTestResponse.setUser(skinTestEntity.getUser().getUsername());
        skinTestResponse.setQuestionGroup(skinTestEntity.getQuestionGroup().getTitle());
        return skinTestResponse;
    }

//    public Page<SkinTestResponse> getAll(SkinResultSearchRequest request) {
//        int page = request.getPage() != null ? request.getPage() : 0;
//        int size = request.getSize() != null ? request.getSize() : 10;
//        String sortDir = request.getSortDirection() != null ? request.getSortDirection() : "desc";
//
//        // Tạo Pageable với sắp xếp theo updatedAt
//        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), "updatedAt");
//        PageRequest pageRequest = PageRequest.of(page, size, sort);
//
//        // Thực hiện truy vấn với Specification
//        Page<SkinTestEntity> skinTests = skinTestRepository.findAll(
//                SkinTestSpecification.withFilters(request),
//                pageRequest
//        );
//
//        // Chuyển đổi kết quả sang DTO
//        return skinTests.map(skinTestMapper::toSkinTestResponse);
//    }

}
