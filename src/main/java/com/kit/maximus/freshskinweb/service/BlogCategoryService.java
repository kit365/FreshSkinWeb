package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.blogCategory.BlogCategoryCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blogCategory.BlogCategoryUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategory;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.BlogCategoryMapper;
import com.kit.maximus.freshskinweb.repository.BlogCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BlogCategoryService implements BaseService<BlogCategoryResponse, BlogCategoryCreationRequest, BlogCategoryUpdateRequest, Long>{

    BlogCategoryRepository blogCategoryRepository;

    BlogCategoryMapper blogCategoryMapper;

    @Override
    public BlogCategoryResponse add(BlogCategoryCreationRequest request) {
//        if(!blogCategoryRepository.existsByblogCategoryName(request.getBlogCategoryName())){
//            throw new AppException(ErrorCode.BLOG_CATEGORY_NAME_EXISTED);
//        }
        BlogCategory blogCategory = blogCategoryMapper.toBlogCategory(request);
        return blogCategoryMapper.toBlogCategoryResponse(blogCategoryRepository.save(blogCategory));
    }

    @Override
    public BlogCategoryResponse update(Long aLong, BlogCategoryUpdateRequest request) {
        return null;
    }

    @Override
    public boolean update(List<Long> id, String status) {
        return false;
    }

    @Override
    public boolean delete(Long aLong) {
        return false;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long aLong) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(List<Long> longs) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        return false;
    }

    @Override
    public boolean restore(List<Long> longs) {
        return false;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }
}
