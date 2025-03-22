package com.kit.maximus.freshskinweb.service.users;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kit.maximus.freshskinweb.dto.request.order.OrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserPasswordRequest;
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
import com.kit.maximus.freshskinweb.specification.AccountSpecification;
import com.kit.maximus.freshskinweb.specification.UserSpecification;
import com.kit.maximus.freshskinweb.utils.Status;

import com.kit.maximus.freshskinweb.utils.TypeUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    @NonFinal
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    UserRepository userRepository;

    UserMapper userMapper;

    OrderMapper orderMapper;

    OrderRepository orderRepository;

    RoleRepository roleRepository;

    Cloudinary cloudinary;

    public boolean add(CreateUserRequest request) {
        UserEntity userEntity = userMapper.toUserEntity(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        } else {
            userEntity.setUsername(request.getUsername());
        }

        if (request.getEmail() == null) {
            userEntity.setEmail(request.getEmail());
        } else if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        } else if (request.getEmail() != null && !userRepository.existsByEmail(request.getEmail())) {
            userEntity.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            RoleEntity role = roleRepository.findById(request.getRole())
                    .orElse(null);
            userEntity.setRole(role);
        } else {
            userEntity.setRole(null);
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
    public boolean deleteTemporarily(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setDeleted(true);
        userRepository.save(userEntity);
        return true;
    }


    public boolean restore(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setDeleted(false);
        userRepository.save(userEntity);
        return true;
    }


    public UserResponseDTO showDetail(Long aLong) {
        return userMapper.toUserResponseDTO(userRepository.findById(aLong).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    public Map<String, Object> getAllUser(int page, int size, String sortKey, String sortDirection, String status, String type, String keyword) {
        // Tạo Pageable với sắp xếp mặc định theo updatedAt
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDirection.toLowerCase()), sortKey)
        );

        // Bắt đầu với specification để lấy user có role là null và deleted = false
        Specification<UserEntity> spec = (root, query, builder) ->
                builder.and(
                        builder.isNull(root.get("role")),
                        builder.isFalse(root.get("deleted"))
                );

        // Chỉ áp dụng các bộ lọc nếu có tham số truyền vào
        if (StringUtils.hasText(status) || StringUtils.hasText(type) || StringUtils.hasText(keyword)) {
            try {
                // Thêm điều kiện lọc theo status nếu có
                if (StringUtils.hasText(status)) {
                    try {
                        Status statusEnum = Status.valueOf(status.toUpperCase());
                        spec = spec.and(UserSpecification.filterByStatus(statusEnum));
                    } catch (IllegalArgumentException e) {
                        // Trả về danh sách rỗng nếu status không hợp lệ
                        return createEmptyResponse(pageable);
                    }
                }

                // Thêm điều kiện lọc theo type nếu có
                if (StringUtils.hasText(type)) {
                    try {
                        TypeUser typeUser = TypeUser.valueOf(type.toUpperCase());
                        spec = spec.and(UserSpecification.filterByType(typeUser));
                    } catch (IllegalArgumentException e) {
                        // Trả về danh sách rỗng nếu type không hợp lệ
                        return createEmptyResponse(pageable);
                    }
                }

                // Thêm điều kiện tìm kiếm nếu có keyword
                if (StringUtils.hasText(keyword)) {
                    spec = spec.and(UserSpecification.searchByKeyword(keyword));
                }
            } catch (Exception e) {
                // Trả về danh sách rỗng nếu có lỗi xử lý các tham số
                return createEmptyResponse(pageable);
            }
        }

        // Thực hiện truy vấn với specification
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);
        Page<UserResponseDTO> userDTOPage = userPage.map(userMapper::toUserResponseDTO);

        // Tạo response
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOPage.getContent());
        response.put("currentPage", userDTOPage.getNumber() + 1);
        response.put("totalItems", userDTOPage.getTotalElements());
        response.put("totalPages", userDTOPage.getTotalPages());
        response.put("pageSize", userDTOPage.getSize());

        return response;
    }

    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        // Tạo Pageable với sắp xếp
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDirection.toLowerCase()), sortKey)
        );

        // Bắt đầu với specification để lấy user có deleted = true và role là null
        Specification<UserEntity> spec = (root, query, builder) ->
                builder.and(
                        builder.isTrue(root.get("deleted")),
                        builder.isNull(root.get("role"))
                );

        // Thêm các điều kiện tìm kiếm nếu có
        if (StringUtils.hasText(status) || StringUtils.hasText(keyword)) {
            try {
                // Thêm điều kiện lọc theo status nếu có
                if (StringUtils.hasText(status)) {
                    try {
                        Status statusEnum = Status.valueOf(status.toUpperCase());
                        spec = spec.and(UserSpecification.filterByStatus(statusEnum));
                    } catch (IllegalArgumentException e) {
                        return createEmptyResponse(pageable);
                    }
                }

                // Thêm điều kiện tìm kiếm nếu có keyword
                if (StringUtils.hasText(keyword)) {
                    spec = spec.and(UserSpecification.searchByKeyword(keyword));
                }
            } catch (Exception e) {
                log.error("Error while filtering trash users", e);
                return createEmptyResponse(pageable);
            }
        }

        // Thực hiện truy vấn với specification
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);
        Page<UserResponseDTO> userDTOPage = userPage.map(userMapper::toUserResponseDTO);

        // Tạo response
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOPage.getContent());
        response.put("currentPage", userDTOPage.getNumber() + 1);
        response.put("totalItems", userDTOPage.getTotalElements());
        response.put("totalPages", userDTOPage.getTotalPages());
        response.put("pageSize", userDTOPage.getSize());

        return response;
    }


    // Phương thức hỗ trợ tạo response rỗng
    private Map<String, Object> createEmptyResponse(Pageable pageable) {
        Map<String, Object> response = new HashMap<>();
        response.put("users", Collections.emptyList());
        response.put("currentPage", pageable.getPageNumber() + 1);
        response.put("totalItems", 0L);
        response.put("totalPages", 0);
        response.put("pageSize", pageable.getPageSize());
        return response;
    }


    public boolean updateAccountPassword(Long userId, UpdateUserRequest request) {
        UserEntity userEntity = getUserEntityById(userId);
        if (StringUtils.hasLength(request.getPassword())) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    public UserResponseDTO getUserByToken(String token) throws ParseException, JOSEException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verify = signedJWT.verify(jwsVerifier);
        if(verify && expirationDate.after(new Date())) {
            String username = signedJWT.getJWTClaimsSet().getSubject();
            UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            return userMapper.toUserResponseDTO(user);
        }
        return null;
    }

    public boolean updateAccountPasswordWithToken(String token, String newPassword, String confirmPassword) throws ParseException, JOSEException {
        UserResponseDTO userResponseDTO = getUserByToken(token);
        if (userResponseDTO == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        UserEntity userEntity = userRepository.findById(userResponseDTO.getUserID())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // Check if the new password and confirm password match
        if (!StringUtils.hasLength(newPassword) || !newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        // Update the new password
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        return true;
    }

    public boolean updateUserPassword(Long userId, UpdateUserPasswordRequest request) {
        UserEntity userEntity = getUserEntityById(userId);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu mới có khớp không
        if (!StringUtils.hasLength(request.getPassword()) || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        // Cập nhật mật khẩu mới
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(userEntity);
        return true;
    }

    public UserResponseDTO update(Long id, UpdateUserRequest userRequestDTO) {
        UserEntity userEntity = getUserEntityById(id);

        log.info("Tìm thấy user: {}", id);

        // Cập nhật thông tin từ request (trừ password)
        userMapper.updateUser(userEntity, userRequestDTO);

        userEntity.setUsername(userEntity.getUsername());

        if (userRequestDTO.getEmail() == null) {
            userEntity.setEmail(userRequestDTO.getEmail());
        } else if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        } else if (userRequestDTO.getEmail() != null && !userRepository.existsByEmail(userRequestDTO.getEmail())) {
            userEntity.setEmail(userRequestDTO.getEmail());
        }

        // Cập nhật Role
        //Vì role có rằng buộc != null, = null là báo lỗi => xét điều kiện cho Role trước khi set vào userEntity
        // nếu trong update ko cập nhật role => set lại role cũ chứ không phải set role = null như lúc đầu mất 2 tiếng để fix
        // @BeanMapping lo việc set lại role cũ cho User
        //CẬP NHẬT LẠI: Do User là cho Customer, nên không có role, nếu cố tình nhập role thì vẫn set là null
        if (userRequestDTO.getRole() != null) {
            RoleEntity role = roleRepository.findById(userRequestDTO.getRole())
                    .orElse(null);
            userEntity.setRole(role);
        } else {
            userEntity.setRole(null);
        }

        log.info("Cập nhật user id: {}", id);

        return userMapper.toUserResponseDTO(userRepository.save(userEntity));
    }

    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);

        if (statusEnum == null) {
            return "Trạng thái không hợp lệ";
        }

        List<UserEntity> userEntities = userRepository.findAllById(id);
        if (userEntities.isEmpty()) {
            return "Không tìm thấy người dùng";
        }

        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            userEntities.forEach(userEntity -> userEntity.setStatus(statusEnum));
            userRepository.saveAll(userEntities);
            return "Cập nhật trạng thái USER thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            userEntities.forEach(userEntity -> userEntity.setDeleted(true));
            userRepository.saveAll(userEntities);
            return "Xóa mềm USER thành công";
        } else if (statusEnum == Status.RESTORED) {
            userEntities.forEach(userEntity -> userEntity.setDeleted(false));
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


    public Map<String, Object> getAllAccount(String status, String keyword, Pageable pageable) {
        Specification<UserEntity> spec = AccountSpecification.hasRole();

        if (keyword != null && !keyword.trim().isEmpty()) {
            Specification<UserEntity> keywordSpec = AccountSpecification.filterAccounts(keyword);
            if (keywordSpec != null) {
                spec = spec.and(keywordSpec);
            }
        }

        if (status != null && !status.trim().isEmpty()) {
            Status statusEnum = getStatus(status.toUpperCase());
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), statusEnum)
            );
        }

        Page<UserEntity> userEntityPage = userRepository.findAll(spec, pageable);

        if (userEntityPage.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("accounts", Collections.emptyList());
            emptyResponse.put("currentPage", 0);
            emptyResponse.put("totalItems", 0);
            emptyResponse.put("totalPages", 0);
            emptyResponse.put("pageSize", pageable.getPageSize());
            return emptyResponse;
        }

        Page<UserResponseDTO> userDTOPage = userEntityPage.map(userMapper::toUserResponseDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("accounts", userDTOPage.getContent());
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
        try {
            if (userRepository.findAllById(id) == null) {
                return "Không tìm thấy người dùng nào để cập nhật";
            }
            Status statusEnum = Status.valueOf(status);
            if (statusEnum == null) {
                return "Trạng thái không hợp lệ";
            }

            List<UserEntity> userEntities = userRepository.findAllById(id);

            if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
                userEntities.forEach(userEntity -> userEntity.setStatus(statusEnum));
                userRepository.saveAll(userEntities);
                return "Cập nhật trạng thái người dùng thành công";
            }
            if (statusEnum == Status.SOFT_DELETED) {
                userEntities.forEach(userEntity -> userEntity.setDeleted(true));
                userRepository.saveAll(userEntities);
                return "Cập nhật trạng thái người dùng thành công";
            }
            if (statusEnum == Status.RESTORED) {
                userEntities.forEach(userEntity -> userEntity.setDeleted(false));
                userRepository.saveAll(userEntities);
                return "Cập nhật trạng thái người dùng thành công";
            }
            return "Không tìm thấy người dùng nào để cập nhật";
        } catch (IllegalArgumentException e) {
            return e.getMessage(); // Hiển thị lỗi rõ ràng hơn
        }
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

    //dashboard data
    public long countUser() {
        return userRepository.countByStatusAndDeleted(Status.ACTIVE, false);
    }
}
