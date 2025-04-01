package com.kit.maximus.freshskinweb.service.skintest;

import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinTestRequest;
//import com.kit.maximus.freshskinweb.dto.request.skin_test.SkinResultSearchRequest;
import com.kit.maximus.freshskinweb.dto.response.SkinTestResponse;
import com.kit.maximus.freshskinweb.entity.*;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.SkinTestMapper;
import com.kit.maximus.freshskinweb.repository.*;
//import com.kit.maximus.freshskinweb.specification.SkinTestSpecification;
import com.kit.maximus.freshskinweb.service.users.EmailService;
import com.kit.maximus.freshskinweb.utils.SkinType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    EmailService emailService;

    SkinTypeScoreRangeRepository skinTypeScoreRangeRepository;

    //Trả loại da theo thang điểm
    // Lúc trước là fix cứng giá trị
    private String determineSkinType(Long totalScore) {
        log.info("Input score: {}", totalScore);

        // First check - database query
        SkinTypeScoreRangeEntity range = skinTypeScoreRangeRepository.findByScoreRange(totalScore)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_SCORE_RANGE));

        log.info("Found range: {}", range);

        // Get the type string
        String skinType = range.getSkinType().getType();
        log.info("Database skin type: {}", skinType);

        // Convert to enum value
        String result = switch (skinType) {
            case "Da khô" -> SkinType.DRY.name();
            case "Da thường" -> SkinType.NORMAL.name();
            case "Da hỗn hợp" -> SkinType.COMBINATION.name();
            case "Da nhạy cảm" -> SkinType.SENSITIVE.name();
            case "Da dầu" -> SkinType.OILY.name();
            default -> throw new AppException(ErrorCode.INVALID_SKIN_TYPE);
        };

        log.info("Result: {}", result);
        return result;
    }

    public String add(SkinTestRequest request) {
        log.info("Processing request: {}", request);

        UserEntity user = userRepository.findById(request.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        QuestionGroupEntity questionGroup = questionGroupRepository.findById(request.getQuestionGroup())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND));

        // Get skin type first to validate
        String skinTypeStr = determineSkinType(request.getTotalScore());
        log.info("Determined skin type: {}", skinTypeStr);

        // Convert to enum to get Vietnamese name
        SkinType skinTypeEnum = SkinType.valueOf(skinTypeStr);
        String vnName = skinTypeEnum.getVNESEname();
        log.info("Vietnamese name: {}", vnName);

        // Find skin type entity
        SkinTypeEntity skinTypeEntity = skinTypeRepository.findByType(vnName);
        if (skinTypeEntity == null) {
            log.error("Skin type not found in database: {}", vnName);
            throw new AppException(ErrorCode.SKIN_TYPE_NOT_FOUND);
        }

        // Create and save test result
        SkinTestEntity skinTest = new SkinTestEntity();
        skinTest.setUser(user);
        skinTest.setQuestionGroup(questionGroup);
        skinTest.setTotalScore(request.getTotalScore());
        skinTest.setSkinType(skinTypeEntity);
        skinTest.setNotes(skinTypeEnum.getMessage());

        // Update user's skin type
        user.setSkinType(vnName);
        userRepository.save(user);


        // Send email notification
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            try {
                emailService.sendSkinTypeResult(
                        user.getEmail(),
                        user.getFirstName() != null ? user.getFirstName() : user.getUsername(),
                        skinTypeEntity.getType(),
                        skinTypeEntity.getDescription(),
                        List.of() // Empty recommendations list
                );
                log.info("Skin type result email sent to: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send skin type result email: {}", e.getMessage());
            }
        }
        skinTestRepository.save(skinTest);
        return skinTest.getSkinType().getType();
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

    public Map<String, Object> getSkinTypeStatistics() {
        List<Object[]> statistics = skinTestRepository.countBySkinTypes();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : statistics) {
            data.add(Map.of(
                    "skinType", (String) row[0],
                    "count", ((Number) row[1]).longValue()
            ));
        }

        return Map.of("data", data);
    }

}