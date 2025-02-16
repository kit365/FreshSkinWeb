package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.UserMapper;
import com.kit.maximus.freshskinweb.repository.UserRepository;
import com.kit.maximus.freshskinweb.utils.Status;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements BaseService<UserResponseDTO, CreateUserRequest, UpdateUserRequest, Long> {

   UserRepository userRepository;

    UserMapper userMapper;


    @Override
    public UserResponseDTO add(CreateUserRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        UserEntity userEntity = userMapper.toUserEntity(request);
        encodePassword(userEntity);
        return userMapper.toUserResponseDTO(userRepository.save(userEntity));
    }


    @Override
    public boolean delete(Long userId) {
        UserEntity userEntity = getUserEntityById(userId);
        if(userEntity == null){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if(userEntity != null){
            log.info("Delete user id:{}",userId);
            userRepository.delete(userEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(List<Long> id) {
        return false;
    }

    //Method: Xóa tạm thời => Status thành false, deleted thành true
    @Override
    public boolean deleteTemporarily(Long id) {
        UserEntity userEntity = getUserEntityById(id);

        if (userEntity == null) {
            log.info("User id not exist");
            return false;
        }

        log.info("Delete user id:{}", id);
        userEntity.setDeleted(true);
        userEntity.setStatus(Status.INACTIVE);
        userRepository.save(userEntity);
        return true;
    }

    //Method: Xóa tạm thời nhiều users => Status thành false, deleted thành true
    @Override
    public boolean deleteTemporarily(List<Long> request) {
        List<UserEntity> users = request.stream() //chuyển List<> thành Stream<>
                .map(this::getUserEntityById)     //lay user tu ID
                .filter(Objects::nonNull)        //loai bo user khong tim thay
                .peek(user -> {        //thuc hiện thao tác(set)
                    log.info("Delete user id:{}", user.getId());
                    user.setDeleted(true);
                    user.setStatus(Status.INACTIVE);
                }).toList(); //chuyển thành List
        userRepository.saveAll(users);

        return true;
    }

    @Override
    public boolean restore(Long id) {
        return false;
    }

    @Override
    public boolean restore(List<Long> id) {
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

        log.info("Cập nhật user id: {}", id);
        return userMapper.toUserResponseDTO(userRepository.save(userEntity));
    }

    @Override
    public boolean update(List<Long> id, String status) {
        return false;
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

    public UserEntity getUser(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


}
