package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.blog_category.BlogCategoryCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.BlogCategoryUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
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
        BlogCategoryEntity blogCategoryEntity = blogCategoryMapper.toBlogCategory(request);
        request.getBlog().forEach(blogCategoryEntity::createBlog);

        return blogCategoryMapper.toBlogCategoryResponse(blogCategoryRepository.save(blogCategoryEntity));
    }

    @Override
    public BlogCategoryResponse update(Long id, BlogCategoryUpdateRequest request) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);
        if (blogCategoryEntity == null) {
            throw new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND);
        }
        blogCategoryMapper.updateBlogCategory(blogCategoryEntity, request);

        return blogCategoryMapper.toBlogCategoryResponse(blogCategoryRepository.save(blogCategoryEntity));
    }

    @Override
    public boolean update(List<Long> id, String status) {
        return false;
    }

    @Override
    public boolean delete(Long id) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);
        if (blogCategoryEntity == null) {
            throw new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND);
        }
        log.info("Delete: {}", id);
        blogCategoryEntity.getBlog().forEach(blog -> blog.setBlogCategory(null));
        blogCategoryRepository.delete(blogCategoryEntity);
        return true;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return true;
    }

    @Override
    public boolean deleteTemporarily(Long id) {
//        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);
//
//        log.info("Delete temporarily : {}", id);
//        List<BlogEntity> blogEntities = blogCategoryEntity.getBlog();
//        for (BlogEntity blogEntity : blogEntities) {
//            blogEntity.setBlogCategory(null);
//        }
//        blogCategoryEntity.setDeleted(true);
//        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }

    @Override
    public boolean deleteTemporarily(List<Long> longs) {
        return false;
    }

    @Override
    public boolean restore(Long aLong) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(aLong);
        if(blogCategoryEntity == null){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", aLong);
        blogCategoryEntity.setDeleted(false);
        blogCategoryRepository.save(blogCategoryEntity);
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

    private BlogCategoryEntity getBlogCategoryEntityById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
    }
}
