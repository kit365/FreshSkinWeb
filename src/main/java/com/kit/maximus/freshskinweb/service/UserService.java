package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.OrderEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.OrderMapper;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.OrderRepository;
import com.kit.maximus.freshskinweb.repository.RoleRepository;
import com.kit.maximus.freshskinweb.repository.UserRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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


    @Override
    public boolean add(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        UserEntity userEntity = userMapper.toUserEntity(request);
        userEntity.setRole(roleRepository.findById(request.getRoleId()).orElse(null));
        encodePassword(userEntity);
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean delete(Long userId) {
        UserEntity userEntity = getUserEntityById(userId);

        if (userEntity == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        log.info("Delete user id:{}", userId);
        userRepository.delete(userEntity);
        return true;


    }

    @Override
    public boolean delete(List<Long> id) {
        userRepository.deleteAllById(id);
        return true;
    }

    //Method: Xóa tạm thời => deleted thành true
    @Override
    public boolean deleteTemporarily(Long id) {
        UserEntity userEntity = getUserEntityById(id);

        if (userEntity == null) {
            log.info("User id not exist");
            return false;
        }

        log.info("Delete user id:{}", id);
        userEntity.setDeleted(true);
        userRepository.save(userEntity);
        return true;
    }


    @Override
    public boolean restore(Long id) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setDeleted(false);
        userRepository.save(userEntity);
        return false;
    }

    @Override
    public UserResponseDTO showDetail(Long aLong) {
        return null;
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

        // Chỉ mã hóa mật khẩu nếu có thay đổi
        if (StringUtils.hasLength(userRequestDTO.getPassword())) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            userEntity.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        // Cập nhật Role
        userEntity.setRole(roleRepository.findById(userRequestDTO.getRoleId()).orElse(null));
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


    public UserResponseDTO addOrder(Long id, CreateUserRequest request) {
        return null;
    }


    public UserResponseDTO addOrder(Long id, CreateOrderRequest request) {
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
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    private UserEntity getUserEntityById(Long id) {
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


}
