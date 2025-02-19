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
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class BlogService implements BaseService<BlogResponse, BlogCreationRequest, BlogUpdateRequest, Long> {

    BlogRepository blogRepository;
    BlogMapper blogMapper;
    BlogCategoryRepository blogCategoryRepository;

    @Override
    public boolean add(BlogCreationRequest request) {
        System.out.println(request.getCategoryID());
        BlogEntity blogEntity = blogMapper.toBlogEntity(request);
        BlogCategoryEntity blogCategoryEntity = blogCategoryRepository.findById(request.getCategoryID()).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        if (blogCategoryEntity != null) {
            blogEntity.setBlogCategory(blogCategoryEntity);
        }
        blogRepository.save(blogEntity);
        return true;
    }

    @Override
    public BlogResponse update(Long id, BlogUpdateRequest request) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        BlogCategoryEntity blogCategoryEntity = blogCategoryRepository.findById(request.getCategoryID()).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
        if (blogCategoryEntity != null) {
            blogEntity.setBlogCategory(blogCategoryEntity);
        }
        blogMapper.updateBlogEntity(blogEntity, request);

        return blogMapper.toBlogResponse(blogRepository.save(blogEntity));
    }

    //Cập nhật trạng thái, xóa mềm, khôi phục cho All ID được chọn
    //Dùng khi user tích vào nhiều ô phẩn tử, sau đó chọn thao tác, ẩn, xóa mềm, khôi phục
    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<BlogEntity> blogEntities = blogRepository.findAllById(id);

        //CẬP NHẬT TRẠNG THÁI BLOG
        if (statusEnum == Status.INACTIVE || statusEnum == Status.ACTIVE) {
            blogEntities.forEach(blogEntity -> blogEntity.setStatus(statusEnum));
            blogRepository.saveAll(blogEntities);
            return "Cập nhật trạng thái thành công";

            //CẬP NHẬT XÓA MỀM BLOG
        } else if (statusEnum == Status.SOFT_DELETED) {
            blogEntities.forEach(blogEntity -> blogEntity.setDeleted(true));

            //CẬP NHẬT KHÔI PHỤC BLOG
            blogRepository.saveAll(blogEntities);
            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            blogEntities.forEach(blogEntity -> blogEntity.setDeleted(false));

            blogRepository.saveAll(blogEntities);
            return "Phục hồi thành công";
        }
        return "Cập nhật hất bại";
    }


    //XÓA CỨNG 1 BLOG
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

    //XÓA CỨNG NHIỀU BLOG
    @Override
    public boolean delete(List<Long> longs) {
        List<BlogEntity> blogEntities = blogRepository.findAllById(longs);
        blogRepository.deleteAll(blogEntities);
        return false;
    }

    //XÓA MỀM 1 BLOG
    @Override
    public boolean deleteTemporarily(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", id);
        blogEntity.setDeleted(true);
        blogRepository.save(blogEntity);
        return true;
    }


    @Override
    public boolean restore(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", id);
        blogEntity.setDeleted(false);
        blogRepository.save(blogEntity);
        return true;
    }

    @Override
    public BlogResponse showDetail(Long aLong) {
        return null;
    }


    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
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

    private BlogEntity getBlogEntityById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }

}
