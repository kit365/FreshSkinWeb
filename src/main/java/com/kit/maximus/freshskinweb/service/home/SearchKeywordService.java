//package com.kit.maximus.freshskinweb.service.home;
//
//import com.kit.maximus.freshskinweb.dto.response.KeywordResponse;
//import com.kit.maximus.freshskinweb.entity.SearchKeywordEntity;
//import com.kit.maximus.freshskinweb.mapper.SearchKeywordMapper;
//import com.kit.maximus.freshskinweb.repository.KeywordRepository;
//import com.kit.maximus.freshskinweb.specification.KeywordSpecification;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Service
//public class SearchKeywordService {
//
//    KeywordRepository keywordRepository;
//    SearchKeywordMapper searchKeywordMapper;
//
//    // Biểu thức chính quy để lọc ký tự lạ (chỉ giữ lại chữ cái, số và khoảng trắng)
//    private static final Pattern VALID_KEYWORD_PATTERN = Pattern.compile("^[\\p{L}0-9 ]+$");
//
//    public boolean addKeyword(String keyword) {
//        if (!StringUtils.hasText(keyword)) return false;  // Kiểm tra từ khóa có trống không
//
//        String cleanedKeyword = keyword.trim().toLowerCase();  // Loại bỏ khoảng trắng thừa và chuyển về chữ thường
//        if (!VALID_KEYWORD_PATTERN.matcher(cleanedKeyword).matches()) return false;  // Kiểm tra ký tự đặc biệt
//
//        // Kiểm tra từ khóa đã tồn tại chưa
//        SearchKeywordEntity keywordEntity = keywordRepository.findByKeyword(cleanedKeyword);
//
//        if (keywordEntity != null) {
//            keywordEntity.setCount(keywordEntity.getCount() + 1);  // Tăng số lượng nếu đã tồn tại
//        } else {
//            keywordEntity = new SearchKeywordEntity();
//            keywordEntity.setKeyword(cleanedKeyword);
//            keywordEntity.setCount(1L);  // Thêm mới nếu chưa tồn tại
//        }
//
//        keywordRepository.save(keywordEntity);  // Lưu từ khóa vào database
//        return true;
//    }
//
//    public List<KeywordResponse> showKeyword() {
//        // Dùng Specification để lấy top 20 từ khóa được tìm kiếm nhiều nhất
//        PageRequest pageable = PageRequest.of(0, 20); // Top 20
//        List<SearchKeywordEntity> topKeywords = keywordRepository.findAll(KeywordSpecification.topKeywords(), pageable).getContent();
//
//        // Chuyển đổi sang DTO bằng setter
//        return topKeywords.stream().map(k -> {
//            KeywordResponse response = new KeywordResponse();
//            response.setKeyword(k.getKeyword());
//            return response;
//        }).collect(Collectors.toList());
//    }
//}
