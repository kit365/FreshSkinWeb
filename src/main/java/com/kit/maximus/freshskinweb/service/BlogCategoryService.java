package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.blog_category.CreateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.ProductCategoryResponse;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import com.kit.maximus.freshskinweb.entity.ProductCategoryEntity;
import com.kit.maximus.freshskinweb.entity.ProductEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.BlogCategoryMapper;
import com.kit.maximus.freshskinweb.repository.BlogCategoryRepository;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BlogCategoryService implements BaseService<BlogCategoryResponse, CreateBlogCategoryRequest, UpdateBlogCategoryRequest, Long>{

    BlogCategoryRepository blogCategoryRepository;

    BlogCategoryMapper blogCategoryMapper;

    @Override
    public boolean add(CreateBlogCategoryRequest request) {
        log.info("Request JSON: {}", request);

//        if(!blogCategoryRepository.existsByblogCategoryName(request.getBlogCategoryName())){
//            throw new AppException(ErrorCode.BLOG_CATEGORY_NAME_EXISTED);
//        }
        BlogCategoryEntity blogCategoryEntity = blogCategoryMapper.toBlogCategory(request);

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = blogCategoryRepository.findAll().size();
            blogCategoryEntity.setPosition(size + 1);
        }

        blogCategoryEntity.setSlug(getSlug(request.getTitle()));

        List<BlogEntity> blogEntities = request.getBlog();
        if(blogEntities == null){
            blogCategoryEntity.setBlog(null);
        } else {
            request.getBlog().forEach(blogCategoryEntity::createBlog);
        }
        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }

    public List<BlogCategoryResponse> getAll() {
        return blogCategoryMapper.toBlogCateroiesResponseDTO(blogCategoryRepository.findAll());
    }

    @Override
    public BlogCategoryResponse update(Long id, UpdateBlogCategoryRequest request) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);
        if (blogCategoryEntity == null) {
            throw new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND);
        }

        if (StringUtils.hasLength(request.getStatus())) {
            blogCategoryEntity.setStatus(getStatus(request.getStatus()));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            blogCategoryEntity.setSlug(getSlug(request.getTitle()));
        }
        blogCategoryMapper.updateBlogCategory(blogCategoryEntity, request);

        return blogCategoryMapper.toBlogCategoryResponse(blogCategoryRepository.save(blogCategoryEntity));
    }

    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<BlogCategoryEntity> blogCategoryEntities = blogCategoryRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            blogCategoryRepository.saveAll(blogCategoryEntities);
        return "Cặp nhật trạng thái thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setDeleted(true));
            blogCategoryRepository.saveAll(blogCategoryEntities);
            return "Xóa mềm danh mục blog thành công";
        } else if (statusEnum == Status.RESTORED) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setDeleted(false));
            blogCategoryRepository.saveAll(blogCategoryEntities);
            return "Phục hồi danh mục blog thành công";
        }

        return "Cập nhật danh mục blog thất bại";
    }

//    @Override
//    public UserResponseDTO addOrder(Long id, CreateUserRequest request) {
//        return null;
//    }

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
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);

        log.info("Delete temporarily : {}", id);
        List<BlogEntity> blogEntities = blogCategoryEntity.getBlog();
        for (BlogEntity blogEntity : blogEntities) {
            blogEntity.setBlogCategory(null);
        }
        blogCategoryEntity.setDeleted(true);
        List<BlogEntity> blogEntityList = blogCategoryEntity.getBlog();
        for (BlogEntity blogEntity : blogEntityList) {
            blogEntity.setStatus(Status.INACTIVE);
        }
        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }



    @Override
    public boolean restore(Long aLong) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(aLong);
        if(blogCategoryEntity == null){
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", aLong);
        blogCategoryEntity.setDeleted(false);

        List<BlogEntity> blogEntityList = blogCategoryEntity.getBlog();
        for (BlogEntity blogEntity : blogEntityList) {
            blogEntity.setStatus(Status.ACTIVE);
        }

        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }

    @Override
    public BlogCategoryResponse showDetail(Long id) {
        return blogCategoryMapper.toBlogCategoryResponse(getBlogCategoryEntityById(id));
    }

    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogCategoryEntity> blogCategoryEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogCategoryEntities = blogCategoryRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<BlogCategoryResponse> list = blogCategoryEntities.map(blogCategoryMapper::toBlogCategoryResponse);

        map.put("blog_category", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogCategoryEntity> blogCategoryEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogCategoryEntities = blogCategoryRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<BlogCategoryResponse> list = blogCategoryEntities.map(blogCategoryMapper::toBlogCategoryResponse);

        map.put("product_category", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
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

//    @Override
//    public UserResponseDTO addOrder(Long id, CreateOrderRequest request) {
//        return null;
//    }

    private BlogCategoryEntity getBlogCategoryEntityById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
    }
}
