package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.blog_category.BlogCategoryCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.BlogCategoryUpdateRequest;
import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.BlogCategoryMapper;
import com.kit.maximus.freshskinweb.repository.BlogCategoryRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
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

        List<BlogEntity> blogEntities = request.getBlog();
        if(blogEntities == null){
            blogCategoryEntity.setBlog(null);
        } else {
            request.getBlog().forEach(blogCategoryEntity::createBlog);
        }
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
        Status statusEnum = getStatus(status);
        List<BlogCategoryEntity> blogCategoryEntities = blogCategoryRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            blogCategoryRepository.saveAll(blogCategoryEntities);

        } else if (statusEnum == Status.SOFT_DELETED) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setDeleted(true));
            blogCategoryRepository.saveAll(blogCategoryEntities);

        } else if (statusEnum == Status.RESTORED) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setDeleted(false));
            blogCategoryRepository.saveAll(blogCategoryEntities);

        }

        return true;
    }

    @Override
    public UserResponseDTO addOrder(Long id, CreateUserRequest request) {
        return null;
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
        List<BlogCategoryEntity> blogCategoryEntities = blogCategoryRepository.findAllById(longs);
        blogCategoryRepository.deleteAll(blogCategoryEntities);
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

    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }


    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }


    //CHECK VIẾT HOA STATUS KHI TRUYỀN VÀO (ACTIVE, INACTIVE)
    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
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

    @Override
    public UserResponseDTO addOrder(Long id, CreateOrderRequest request) {
        return null;
    }

    private BlogCategoryEntity getBlogCategoryEntityById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
    }
}
