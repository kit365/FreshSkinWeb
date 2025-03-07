package com.kit.maximus.freshskinweb.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.RoleRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.specification.UserSpecification;
import com.kit.maximus.freshskinweb.utils.Status;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements BaseService<UserResponseDTO, CreateUserRequest, UpdateUserRequest, Long> {

    UserRepository userRepository;

    UserMapper userMapper;

    OrderMapper orderMapper;

    OrderRepository orderRepository;

    RoleRepository roleRepository;

    Cloudinary cloudinary;


    @Override
    public boolean add(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        UserEntity userEntity = userMapper.toUserEntity(request);

        if (request.getRole() != null) {
            RoleEntity role = roleRepository.findById(request.getRole())
                    .orElse(null);
            userEntity.setRole(role);
        }

        encodePassword(userEntity);

        if (request.getAvatar() != null) {
            try {
                MultipartFile file = request.getAvatar();
                String slg = getSlug(request.getLastName() + " " + request.getFirstName());

                String img = uploadImageFromFile(file, slg);
                userEntity.setAvatar(img);
            } catch (IOException e) {
                log.error("Upload avatar error", e);
            }
        }

        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean delete(Long userId) {
        UserEntity userEntity = getUserEntityById(userId);

        if (userEntity == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        //Xóa ảnh từ cloud khi xóa user
        if (userEntity.getAvatar() != null) {
            try {
                deleteImageFromCloudinary(userEntity.getAvatar());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Delete user id:{}", userId);
        userRepository.delete(userEntity);
        return true;
    }

    @Override
    public boolean delete(List<Long> id) {
        userRepository.deleteAllById(id);
        List<UserEntity> userEntities = userRepository.findAllById(id);

        userEntities.forEach(UserEntity -> {
            if (UserEntity.getAvatar() != null) {
                try {
                    deleteImageFromCloudinary(UserEntity.getAvatar());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return true;
    }

    public boolean deleteAllUsers() {
        userRepository.deleteAll();
        return true;
    }

    //Method: Xóa tạm thời => deleted thành true
    @Override
    public boolean deleteTemporarily(Long id) {
        return true;
    }


    @Override
    public boolean restore(Long id) {
        return false;
    }


    @Override
    public UserResponseDTO showDetail(Long aLong) {
        return userMapper.toUserResponseDTO(userRepository.findById(aLong).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<UserEntity> userEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên User(LastName + FullName), không lọc theo status

//                userEntities = userRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên tên User(LastName + FullName) và status
                Status statusEnum = getStatus(status);
//                userEntities = userRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                userEntities = userRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                userEntities = userRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

//        Page<UserResponseDTO> list = userEntities.map(userMapper::toUserResponseDTO);


//        if (!list.hasContent()) {
//            return null;
//        }

//        map.put("products", list.getContent());
//        map.put("currentPage", list.getNumber() + 1);
//        map.put("totalItems", list.getTotalElements());
//        map.put("totalPages", list.getTotalPages());
//        map.put("pageSize", list.getSize());
        return map;
    }

    @Override
    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        return Map.of();
    }

    public boolean updatePassword(Long userId, UpdateUserRequest request) {
        UserEntity userEntity = getUserEntityById(userId);
        if (StringUtils.hasLength(request.getPassword())) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    public UserResponseDTO update(Long id, UpdateUserRequest userRequestDTO) {
        UserEntity userEntity = getUserEntityById(id);

        log.info("Tìm thấy user: {}", id);

        // Cập nhật thông tin từ request (trừ password)
        userMapper.updateUser(userEntity, userRequestDTO);

        userEntity.setUsername(userEntity.getUsername());
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.info("Email exist");
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Cập nhật Role
        //Vì role có rằng buộc != null, = null là báo lỗi => xét điều kiện cho Role trước khi set vào userEntity
        // nếu trong update ko cập nhật role => set lại role cũ chứ không phải set role = null như lúc đầu mất 2 tiếng để fix
        // @BeanMapping lo việc set lại role cũ cho User
        //CẬP NHẬT LẠI: Do User là cho Customer, nên không có role, nếu cố tình nhập role thì vẫn set là null
        log.info(userRequestDTO.getRole().toString());
        if (userRequestDTO.getRole() != null) {
            userEntity.setRole(null);
        }

        log.info("Cập nhật user id: {}", id);

        return userMapper.toUserResponseDTO(userRepository.save(userEntity));
    }

    @Override
    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<UserEntity> userEntities = userRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            userEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            userRepository.saveAll(userEntities);
            return "Cập nhật trạng thái USER thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            userEntities.forEach(productEntity -> productEntity.setDeleted(true));
            userRepository.saveAll(userEntities);
            return "Xóa mềm USER thành công";
        } else if (statusEnum == Status.RESTORED) {
            userEntities.forEach(productEntity -> productEntity.setDeleted(false));
            userRepository.saveAll(userEntities);
            return "Phục hồi USER thành công";
        }
        return "Cập nhật USER thất bại";
    }


    public UserResponseDTO addOrder(Long id, OrderRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderEntity order = orderMapper.toOrderEntity(request, user);
        user.createOrder(order); // Gọi phương thức thêm đơn hàng vào danh sách

        orderRepository.save(order);
        userRepository.save(user); // Lưu lại user với danh sách đơn hàng đã cập nhật
        return userMapper.toUserResponseDTO(user);
    }


    private void encodePassword(UserEntity user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }


    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> getUserByUsername(String username) {
        return userMapper.toUserResponseDTO(userRepository.searchByKeyword(username));
    }

    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public UserEntity getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    //    public boolean deleteProductVariants(Long id, ProductVariantEntity productVariantEntities) {
//        ProductEntity productEntity = getProductEntityById(id);
//
//        for (ProductVariantEntity request : productEntity.getVariants()) {
//            if (request.getId().equals(productVariantEntities.getId())) {
//                productEntity.removeProductVariant(productVariantEntities);
//                return true;
//            }
//        }
//        return false;
//    }

    @Transactional
    public Boolean deleteOrder(Long userId, Long orderId) {
        UserEntity user = getUserEntityById(userId);


        Iterator<OrderEntity> iterator = user.getOrders().iterator();
        while (iterator.hasNext()) {
            OrderEntity order = iterator.next();
            if (order.getOrderId().equals(orderId)) {
//                iterator.remove(); // Xóa khỏi danh sách
                order.setDeleted(true);
//                orderRepository.deleteById(orderId); // Xóa trong database
                return true;
            }
        }

        return false; // Không tìm thấy đơn hàng để xóa
    }

    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }

    //Xóa ảnh từ Cloud
    private void deleteImageFromCloudinary(String imageUrl) throws IOException {
        if (imageUrl != null) {
            Map options = ObjectUtils.asMap("invalidate", true);
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    //Tách publicId từ URL của ảnh trên Cloudinary (dùng khi cần xóa ảnh).
    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        return temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
    }


    //Lấy tên file
    private String getNameFile(String slug, int count) {
        String fileName;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        if (count <= 0) {
            return slug + "_" + timestamp;
        }
        return slug + "_" + timestamp + "_" + (count + 1);

    }

    //Up ảnh lên cloud
    private String uploadImageFromFile(MultipartFile file, String username) throws IOException {

        String fileName = getNameFile(username, 0);

        Map params = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true, //Cho phép ghi đè ảnh cũ
                "folder", "avatars",
                "public_id", fileName
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString(); // Trả về URL của ảnh
    }

                                /* PHẦN CHO ACCOUNT CỦA DŨNG */


    public UserResponseDTO showDetailByRole(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            if (userEntity.getRole() != null) {
                return userMapper.toUserResponseDTO(userEntity);
            } else {
                throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
            }
    }

    //Lọc Account theo status hoặc theo tên, đồng thời có them rằng buộc chỉ show Account role != null
    public Map<String, Object> getAll(String status, String keyword, Pageable pageable) {
        Specification<UserEntity> spec = (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("role"));

        // Nếu status không null, thêm điều kiện lọc theo status
        if (status != null) {
            Status statusEnum = getStatus(status.toUpperCase());
            spec = spec.and(UserSpecification.filterUsers(statusEnum, keyword));
        }

        // Thực hiện truy vấn
        Page<UserEntity> userEntityPage = userRepository.findAll(spec, pageable);

        // Chuyển đổi sang DTO
        Page<UserResponseDTO> userDTOPage = userEntityPage.map(userMapper::toUserResponseDTO);

        // Chuẩn bị response
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOPage.getContent());
        response.put("currentPage", userDTOPage.getNumber() + 1);
        response.put("totalItems", userDTOPage.getTotalElements());
        response.put("totalPages", userDTOPage.getTotalPages());
        response.put("pageSize", userDTOPage.getSize());

        return response;
    }



    public boolean deleteAccount(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole() != null) {
            userRepository.delete(user);
            return true;
        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }

    public UserResponseDTO updateAccount(Long id, UpdateUserRequest request) {
        UserEntity userEntity = getUserEntityById(id);

        if (userEntity.getRole() != null) {

            // Cập nhật thông tin từ request (trừ password)
            userMapper.updateUser(userEntity, request);

            userEntity.setUsername(userEntity.getUsername());
            if (userRepository.existsByEmail(request.getEmail())) {
                log.info("Email exist");
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }

            if (userEntity.getRole() != null) {
                RoleEntity role = roleRepository.findById(request.getRole())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            }

            return userMapper.toUserResponseDTO(userRepository.save(userEntity));

        } else {
            throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
        }
    }

    public String updateMulti(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<UserEntity> userEntities = userRepository.findAllById(id);
        for(UserEntity userEntity : userEntities) {
            if(userEntity.getRole() != null) {
                if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
                    userEntity.setStatus(statusEnum);
                    userRepository.save(userEntity);
                } else if (statusEnum == Status.SOFT_DELETED) {
                    userEntity.setDeleted(true);
                    userRepository.save(userEntity);
                } else if (statusEnum == Status.RESTORED) {
                    userEntity.setDeleted(false);
                    userRepository.save(userEntity);
                }
            }
        }
        return "Cập nhật Account thất bại";
    }

    public boolean deleteSelectedAccount(List<Long> id) {
        List<UserEntity> userEntities = userRepository.findAllById(id);
        for (UserEntity userEntity : userEntities) {
            if (userEntity.getRole() != null) {
                userRepository.delete(userEntity);
                if (userEntity.getAvatar() != null) {
                    try {
                        deleteImageFromCloudinary(userEntity.getAvatar());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                throw new AppException(ErrorCode.THIS_USER_NOT_ALLOWED_TO_DELETE);
            }
        }
    return true;
    }
}
