package com.kit.maximus.freshskinweb.service.blog;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogCreationRequest;
import com.kit.maximus.freshskinweb.dto.request.blog.BlogUpdateRequest;
import com.kit.maximus.freshskinweb.dto.response.BlogCategoryResponse;
import com.kit.maximus.freshskinweb.dto.response.BlogResponse;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.BlogCategoryEntity;
import com.kit.maximus.freshskinweb.entity.BlogEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.BlogMapper;
import com.kit.maximus.freshskinweb.repository.BlogCategoryRepository;
import com.kit.maximus.freshskinweb.repository.BlogRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.repository.search.BlogSearchRepository;
import com.kit.maximus.freshskinweb.service.BaseService;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogService implements BaseService<BlogResponse, BlogCreationRequest, BlogUpdateRequest, Long> {

    BlogRepository blogRepository;
    BlogMapper blogMapper;
    BlogCategoryRepository blogCategoryRepository;
    Cloudinary cloudinary;
    BlogSearchRepository blogSearchRepository;
    UserRepository userRepository;

    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    @Override
    public boolean add(BlogCreationRequest request) {
        BlogEntity blogEntity = blogMapper.toBlogEntity(request);
        BlogCategoryEntity blogCategoryEntity = blogCategoryRepository.findById(request.getCategoryID()).orElse(null);

        if (request.getCategoryID() != null) {
            blogEntity.setBlogCategory(blogCategoryEntity);
        } else {
            blogEntity.setBlogCategory(null);
        }

        if (request.getUser() != null) {
            blogEntity.setUser(userRepository.findById(request.getUser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        }

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = blogRepository.findAll().size();
            blogEntity.setPosition(size + 1);
        }

        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {

            int count = 0;
            List<String> images = new ArrayList<>();

            for (MultipartFile file : request.getThumbnail()) {
                try {
                    String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                    images.add(url);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            blogEntity.setThumbnail(images);
        }

        blogEntity.setSlug(getSlug(request.getTitle()));

        BlogResponse blogResponse = blogMapper.toBlogResponse(blogRepository.save(blogEntity));
        UserEntity user = blogEntity.getUser();
        blogResponse.setAuthor(mapAuthor(user));
        blogSearchRepository.indexBlog(blogResponse);
        return true;
    }

    public List<BlogResponse> getAll() {
        return blogMapper.toBlogsResponseDTO(blogRepository.findAll());
    }

    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    @Override
    public BlogResponse update(Long id, BlogUpdateRequest request) {
        BlogEntity blogEntity = getBlogEntityById(id);


        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }

        //Check nếu không thay đổi BlogCategory thì sẽ ko set giá trị mới
        BlogCategoryEntity blogCategoryEntity = null;

        if (request.getCategoryID() != null) {
            blogCategoryEntity = blogCategoryRepository.findById(request.getCategoryID()).orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
            blogEntity.setBlogCategory(blogCategoryEntity);
        } else {
            blogEntity.setBlogCategory(null);
        }

        if (request.getUser() != null) {
            blogEntity.setUser(userRepository.findById(request.getUser()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            blogEntity.setSlug(getSlug(request.getTitle()));
        }


        if ((request.getNewImg() != null && !request.getNewImg().isEmpty()) ||
                (request.getThumbnail() != null && !request.getThumbnail().isEmpty())) {

            // Lấy danh sách ảnh cũ từ blogEntity
            List<String> oldImages = blogEntity.getThumbnail() != null ?
                    new ArrayList<>(blogEntity.getThumbnail()) : new ArrayList<>();

            // Danh sách ảnh mới từ FE
            List<String> newThumbnails = request.getThumbnail() != null ?
                    request.getThumbnail() : new ArrayList<>();

            // Xóa ảnh không còn trong danh sách mới
            for (String oldUrl : oldImages) {
                if (!newThumbnails.contains(oldUrl)) {
                    try {
                        deleteImageFromCloudinary(oldUrl); // Xóa khỏi Cloudinary
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete image: " + oldUrl, e);
                    }
                }
            }

            // Danh sách mới giữ lại các ảnh chưa bị xóa
            List<String> updatedThumbnails = new ArrayList<>(newThumbnails);

            // Thêm ảnh mới nếu có
            if (request.getNewImg() != null && !request.getNewImg().isEmpty()) {
                int count = 0;
                for (MultipartFile file : request.getNewImg()) {
                    if (file != null && !file.isEmpty()) {
                        try {
                            String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                            updatedThumbnails.add(url);
                        } catch (IOException e) {
                            log.error("Failed to upload image: {}", e.getMessage());
                            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
                        }
                    }
                }
            }

            // Cập nhật lại danh sách ảnh trong blogEntity
            blogEntity.setThumbnail(updatedThumbnails);

        } else {
            // Nếu FE không gửi gì => Xóa hết ảnh
            if (blogEntity.getThumbnail() != null && !blogEntity.getThumbnail().isEmpty()) {
                for (String oldUrl : blogEntity.getThumbnail()) {
                    try {
                        deleteImageFromCloudinary(oldUrl); // Xóa toàn bộ hình ảnh
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete image: " + oldUrl, e);
                    }
                }
            }
            blogEntity.setThumbnail(null); // Xóa danh sách ảnh trong DB
        }


        blogMapper.updateBlogEntity(blogEntity, request);
        BlogResponse blogResponse = blogMapper.toBlogResponse(blogRepository.save(blogEntity));
        blogResponse.setAuthor(mapAuthor(blogEntity.getUser()));
        blogSearchRepository.update(blogResponse);

        //Chỉ trả về 2 fields là ID và Title của thằng con, không trả hết
        BlogCategoryResponse blogCategoryResponse = new BlogCategoryResponse();
        if (blogEntity.getBlogCategory() != null) {
            blogCategoryResponse.setId(blogEntity.getBlogCategory().getId());
            blogCategoryResponse.setTitle(blogEntity.getBlogCategory().getTitle());
            blogResponse.setBlogCategory(blogCategoryResponse);
        }


        return blogResponse;
    }

    public BlogCategoryEntity getBlogCategoryEntityById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }


    //Cập nhật trạng thái, xóa mềm, khôi phục cho All ID được chọn
    //Dùng khi user tích vào nhiều ô phẩn tử, sau đó chọn thao tác, ẩn, xóa mềm, khôi phục
    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<BlogEntity> blogEntities = blogRepository.findAllById(id);

        if (blogEntities.isEmpty()) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }

        //CẬP NHẬT TRẠNG THÁI BLOG
        if (statusEnum == Status.INACTIVE || statusEnum == Status.ACTIVE) {
            blogEntities.forEach(blogEntity -> {
                blogEntity.setStatus(statusEnum);
                blogSearchRepository.update(blogEntity.getId(), status);
            });
            blogRepository.saveAll(blogEntities);

            return "Cập nhật trạng thái thành công";

            //CẬP NHẬT XÓA MỀM BLOG
        } else if (statusEnum == Status.SOFT_DELETED) {
            blogEntities.forEach(blogEntity -> {
                blogEntity.setDeleted(true);
                blogSearchRepository.update(blogEntity.getId(), true);
            });

            //CẬP NHẬT KHÔI PHỤC BLOG
            blogRepository.saveAll(blogEntities);
            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            blogEntities.forEach(blogEntity -> {
                blogEntity.setDeleted(false);
                blogSearchRepository.update(blogEntity.getId(), false);
            });
            blogRepository.saveAll(blogEntities);
            return "Phục hồi thành công";
        }
        return "Cập nhật Thất bại";
    }


    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    public String update(long id, String status, Integer position, String statusEdit) {
        BlogEntity blogEntity = blogRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));

        System.out.println("status: " + status + ", position: " + position + ", statusEdit: " + statusEdit);

        if ("editStatus".equalsIgnoreCase(statusEdit)) {
            if (status != null) { // Kiểm tra nếu status không bị null
                Status statusEnum = getStatus(status);
                blogEntity.setStatus(statusEnum);
                blogRepository.save(blogEntity);
                blogSearchRepository.update(id, status);
                return "Cập nhật trạng thái thành công";
            }
            return "Trạng thái không được để trống!";
        } else if ("editPosition".equalsIgnoreCase(statusEdit)) {
            if (position != 0) { // Kiểm tra null trước khi gán
                blogEntity.setPosition(position);
                blogRepository.save(blogEntity);
                blogSearchRepository.update(id, position);
                return "Cập nhật vị trí thành công";
            }
            return "Vị trí không được để trống!";
        }

        return "Cập nhật thất bại";
    }

    //XÓA CỨNG 1 BLOG
    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    @Override
    public boolean delete(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);

        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }

        if (blogEntity.getThumbnail() != null && !blogEntity.getThumbnail().isEmpty()) {
            blogEntity.getThumbnail().forEach(s -> {
                try {
                    deleteImageFromCloudinary(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        log.info("Delete: {}", id);
        blogRepository.delete(blogEntity);
        blogSearchRepository.deleteBlogs(id);
        return true;
    }

    //XÓA CỨNG NHIỀU BLOG
    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    @Override
    public boolean delete(List<Long> longs) {
        List<BlogEntity> blogEntities = blogRepository.findAllById(longs);

        for (BlogEntity blogEntity : blogEntities) {
            if (blogEntity.getThumbnail() != null && !blogEntity.getThumbnail().isEmpty()) {
                for (String url : blogEntity.getThumbnail()) {
                    try {
                        deleteImageFromCloudinary(url);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            blogSearchRepository.deleteBlogs(blogEntity.getId());
        }

        blogRepository.deleteAll(blogEntities);
        return true;
    }

    //XÓA MỀM 1 BLOG
    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    @Override
    public boolean deleteTemporarily(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", id);
        blogEntity.setDeleted(true);
        blogRepository.save(blogEntity);
        blogSearchRepository.update(id, true);
        return true;
    }


    @Override
    @CacheEvict(value = {"totalBlogs", "blogCategoryHome", "blogListAdmin"}, allEntries = true)
    public boolean restore(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", id);
        blogEntity.setDeleted(false);
        blogRepository.save(blogEntity);
        blogSearchRepository.update(id, false);
        return true;
    }

    @Override
    public BlogResponse showDetail(Long aLong) {
        BlogEntity blogEntity = getBlogEntityById(aLong);
        BlogResponse blogResponse = blogMapper.toBlogResponse(getBlogEntityById(aLong));
        if (blogEntity != null) {
            blogResponse.setAuthor(mapAuthor(blogEntity.getUser()));

            if (blogEntity.getBlogCategory() != null) {
                BlogCategoryResponse blogCategoryResponse = new BlogCategoryResponse();
                blogCategoryResponse.setTitle(blogEntity.getBlogCategory().getTitle());
                blogCategoryResponse.setId(blogEntity.getBlogCategory().getId());
                blogResponse.setBlogCategory(blogCategoryResponse);
            }
        }


        return blogResponse;
    }

    @Cacheable(value = "blogListAdmin",
            key = "#page + '_' + #size + '_' + (#sortKey ?: 'none') + '_' + (#sortDirection ?: 'none') + '_' + (#status ?: 'none') + '_' + (#keyword ?: 'none')")
    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogEntity> blogEntityPage;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogEntityPage = blogRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<BlogResponse> list = blogEntityPage.map(blogMapper::toBlogResponse);
        blogEntityPage.forEach(blogEntity -> {
            list.forEach(blogResponse -> {
                blogResponse.setAuthor(mapAuthor(blogEntity.getUser()));
            });
        });

        map.put("blogs", list.getContent());
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

        Page<BlogEntity> blogEntityPage;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, true, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, true);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogEntityPage = blogRepository.findAllByDeleted(true, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findAllByStatusAndDeleted(statusEnum, true, pageable);
            }
        }

        Page<BlogResponse> list = blogEntityPage.map(blogMapper::toBlogResponse);

        map.put("blogs", list.getContent());
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

    private String getNameFile(String slug, int count) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        if (count <= 0) {
            return slug + "_" + timestamp;
        }
        return slug + "_" + timestamp + "_" + (count + 1);

    }

    private String uploadImageFromFile(MultipartFile file, String slug, int count) throws IOException {

        String fileName = getNameFile(slug, count);


        Map params = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", false,
                "folder", "blog",
                "public_id", fileName
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    private void deleteImageFromCloudinary(String imageUrl) throws IOException {
        if (imageUrl != null) {
            Map options = ObjectUtils.asMap("invalidate", true);
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }


    //lấy hình từ ID
    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        String publicId = temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
        System.out.println(publicId);
        return publicId;
    }

    /*
      Home
     */

    public BlogResponse getBlogResponseBySlug(String slug) {

        // Lấy BlogResponse từ OpenSearch
        BlogResponse blogResponse = blogSearchRepository.searchBySlug(slug);

        // Lấy BlogEntity từ cơ sở dữ liệu
        BlogEntity blogEntity = blogRepository.searchBySlug(slug);

        // Nếu BlogEntity và UserEntity không null, thiết lập tác giả cho BlogResponse
        if (blogEntity != null) {
            if (blogEntity.getUser() != null) {
                String firstName = blogEntity.getUser().getFirstName() != null ? blogEntity.getUser().getFirstName() : "";
                String lastName = blogEntity.getUser().getLastName() != null ? blogEntity.getUser().getLastName() : "";
                blogResponse.setAuthor((firstName + " " + lastName).trim());
            } else {
                blogResponse.setAuthor("Unknown");
            }


            // Thiết lập ngày tạo và ngày cập nhật
//            if (blogEntity.getCreatedAt() != null) {
//                blogResponse.setCreatedAt(blogEntity.getCreatedAt());
//            }
//            if (blogEntity.getUpdatedAt() != null) {
//                blogResponse.setUpdatedAt(blogEntity.getUpdatedAt());
//            }
        }

        // Xóa các trường không cần thiết trong BlogResponse
        blogResponse.setDeleted(null);
        blogResponse.setStatus(null);
        blogResponse.setPosition(null);

        return blogResponse;
    }

    public List<BlogResponse> getBlogsByCategorySlug(String slug, String status, boolean deleted, int page, int size) {
        return blogSearchRepository.getBlogsByCategorySlug(slug, status, deleted, page, size);
    }

    public List<BlogResponse> getBlogsByCategorySlug(String slug, String status, boolean deleted) {
        return blogSearchRepository.getBlogsByCategorySlug(slug, status, deleted);
    }


    @Cacheable(value = "blogCategoryHome", key = "#page + '_' + #size + '_' + #slug")
    public Map<String, Object> getBlogCategories(int page, int size, String slug) {
        int p = (page > 0) ? page - 1 : 0;

        Map<String, Object> map = new HashMap<>();

        // Lấy danh sách blog từ OpenSearch hoặc cơ sở dữ liệu
        List<BlogResponse> blogResponsesPage = getBlogsByCategorySlug(slug, "ACTIVE", false, p, size);

        BlogCategoryResponse category = blogResponsesPage.getFirst().getBlogCategory();
        BlogCategoryResponse blogCategoryResponse = new BlogCategoryResponse();
        blogCategoryResponse.setSlug(category.getSlug());
        blogCategoryResponse.setId(category.getId());
        blogCategoryResponse.setTitle(category.getTitle());


        blogResponsesPage.forEach(blogResponse -> {
            // Xóa các trường không cần thiết
            blogResponse.setBlogCategory(null);


        });

        List<BlogResponse> blogResponseList = getBlogsByCategorySlug(slug, "ACTIVE", false);

        int totalItem = blogResponseList.size();
        int totalPages = (int) Math.ceil((double) totalItem / size);
        Map<String, Object> pageDetail = new HashMap<>();

        pageDetail.put("page", p + 1);
        pageDetail.put("totalItems", totalItem);
        pageDetail.put("totalPages", totalPages);
        pageDetail.put("pageSize", size);

        map.put("blogs", blogResponsesPage);
        map.put("blogCategory", blogCategoryResponse);
        map.put("pageDetail", pageDetail);
        return map;
    }

    private String mapAuthor(UserEntity user) {
        if (user == null) {
            return "Unknown";
        }
        String firstName = Optional.ofNullable(user.getFirstName()).orElse("");
        String lastName = Optional.ofNullable(user.getLastName()).orElse("");
        return String.join(" ", firstName, lastName).trim();
    }


    public boolean indexBlogs() {
        // Lấy tất cả các BlogEntity từ cơ sở dữ liệu
        List<BlogEntity> blogEntities = blogRepository.findAll();

        // Chuyển đổi BlogEntity thành BlogResponse
        List<BlogResponse> responseDTOS = blogMapper.toBlogsResponseDTO(blogEntities);

        // Thiết lập tác giả và ngày tạo/ cập nhật cho mỗi BlogResponse nếu có UserEntity và BlogEntity
        IntStream.range(0, Math.min(blogEntities.size(), responseDTOS.size()))
                .forEach(i -> {
                    BlogEntity blogEntity = blogEntities.get(i);
                    BlogResponse blogResponse = responseDTOS.get(i);

                    // Thiết lập tác giả
                    blogResponse.setAuthor(mapAuthor(blogEntity.getUser()));
                });

        // Index tất cả blog responses vào Elasticsearch hoặc hệ thống tìm kiếm của bạn
        responseDTOS.forEach(blogSearchRepository::indexBlog);
        return true;
    }

    //data dashboard
    @Cacheable(value = "totalBlogs")
    public long countBlogs() {
        return blogRepository.countByStatusAndDeleted(Status.ACTIVE, false);
    }


}
