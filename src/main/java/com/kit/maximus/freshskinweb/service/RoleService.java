package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.role.CreateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.role.UpdateRoleRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.request.user.UpdateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.RoleResponseDTO;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;
import com.kit.maximus.freshskinweb.entity.RoleEntity;
import com.kit.maximus.freshskinweb.entity.UserEntity;
import com.kit.maximus.freshskinweb.exception.AppException;
import com.kit.maximus.freshskinweb.exception.ErrorCode;
import com.kit.maximus.freshskinweb.mapper.RoleMapper;
import com.kit.maximus.freshskinweb.repository.RoleRepository;
import com.kit.maximus.freshskinweb.utils.EnumUtils;
import com.kit.maximus.freshskinweb.utils.Status;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService implements BaseService<RoleResponseDTO, CreateRoleRequest, UpdateRoleRequest, Long>{

    RoleRepository roleRepository;
    RoleMapper roleMapper;

    @Override
    public RoleResponseDTO add(CreateRoleRequest request) {
        if(roleRepository.existsByRoleName(request.getRoleName())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        RoleEntity roleEntity = roleMapper.toRoleEntity(request);
        return roleMapper.toRoleResponseDTO(roleRepository.save(roleEntity));
    }

    @Override
    public RoleResponseDTO update(Long id, UpdateRoleRequest request) {
        RoleEntity roleEntity = getRoleEntityById(id);

        log.info("Tìm thấy role: {}", id);

        // Cập nhật thông tin từ request
        roleMapper.updateRole(roleEntity, request);
        log.info("Cập nhật role id: {}", id);
        return roleMapper.toRoleResponseDTO(roleRepository.save(roleEntity));
    }

    @Override
    public boolean update(List<Long> id, String status) {
        return false;
    }

    @Override
    public boolean delete(Long id) {
        RoleEntity roleEntity = getRoleEntityById(id);
        if(roleEntity == null){
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        if(roleEntity != null){
            log.info("Delete role id:{}",id);
            roleRepository.delete(roleEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(List<Long> longs) {
        return false;
    }

    @Override
    public boolean deleteTemporarily(Long id) {
        RoleEntity roleEntity = getRoleEntityById(id);
        if (roleEntity == null) {
            log.info("Role id not exist");
            return false;
        }

        log.info("Delete role id:{}", id);
        roleEntity.setDeleted(true);
        roleEntity.setStatus(Status.INACTIVE);
        roleRepository.save(roleEntity);
        return true;
    }

    @Override
    public boolean deleteTemporarily(List<Long> longs) {
        return false;
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

    private RoleEntity getRoleEntityById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }


//
//    //Method: Xóa tạm thời nhiều role => Status thành false, deleted thành true
//    @Override
//    public boolean deleteTemporarily(List<Long> request) {
//        List<RoleEntity> roles = new ArrayList<>();
//        for(RoleEntity list : roles ){
//            list.setDeleted(true);
//            list.setStatus(Status.INACTIVE);
//        }
//        roleRepository.saveAll(roles);
//
//        return true;
//    }
//
//    @Override
//    public boolean restore(Long id) {
//        return false;
//    }
//
//    @Override
//    public boolean restore(List<Long> id) {
//        return false;
//    }
//
//    @Override
//    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
//        return Map.of();
//    }
//
//    public RoleResponseDTO update(Long id, UpdateRoleRequest request) {
//        RoleEntity roleEntity = getRoleEntityById(id);
//
//        log.info("Tìm thấy user: {}", id);
//
//        // Cập nhật thông tin từ request (trừ password)
//        roleMapper.updateRole(roleEntity, request);
//
//        // Chỉ mã hóa mật khẩu nếu có thay đổi
//
//        log.info("Cập nhật role id: {}", id);
//        return roleMapper.toRoleResponseDTO(roleRepository.save(roleEntity));
//    }
//
//    @Override
//    public boolean update(List<Long> id, String status) {
//        return false;
//    }
//
//    public List<RoleResponseDTO> getAllRoles() {
//        return roleRepository.findAll().stream().map(roleMapper::toRoleResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<RoleResponseDTO> getRoleByRolename(String rolename) {
//        return roleMapper.toRoleResponseDTO(roleRepository.searchByKeyword(rolename));
//    }


}
