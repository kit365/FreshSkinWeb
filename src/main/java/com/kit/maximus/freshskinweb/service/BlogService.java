package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.BlogMapper;
import com.kit.maximus.freshskinweb.repository.BlogCategoryRepository;
import com.kit.maximus.freshskinweb.repository.BlogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class BlogService implements BaseService<BlogResponse, BlogCreationRequest, BlogUpdateRequest, Long>{

    BlogRepository blogRepository;
    BlogMapper blogMapper;

    @Override
    public BlogResponse add(BlogCreationRequest request) {
        BlogEntity blogEntity = blogMapper.toBlogEntity(request);
        blogEntity.setBlogCategory(request.getBlogCategoryEntity());
        return blogMapper.toBlogResponse(blogRepository.save(blogEntity));
    }

    @Override
    public BlogResponse update(Long id, BlogUpdateRequest request) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if(blogEntity == null){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        blogMapper.updateBlogEntity(blogEntity, request);
        if(request.getBlogCategoryEntity() != null){
            blogEntity.setBlogCategory(request.getBlogCategoryEntity());
        }
        return blogMapper.toBlogResponse(blogRepository.save(blogEntity));
    }

    @Override
    public boolean update(List<Long> id, String status) {
        return false;
    }

    @Override
    public boolean delete(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete: {}", id);
        blogRepository.delete(blogEntity);
        return true;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long aLong) {
        BlogEntity blogEntity = getBlogEntityById(aLong);
        if(blogEntity == null){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", aLong);
        blogEntity.setDeleted(true);
        blogRepository.save(blogEntity);
        return true;
    }

    @Override
    public boolean deleteTemporarily(List<Long> longs) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        BlogEntity blogEntity = getBlogEntityById(aLong);
        if(blogEntity == null){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", aLong);
        blogEntity.setDeleted(false);
        blogRepository.save(blogEntity);
        return true;
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

    private BlogEntity getBlogEntityById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }
}
