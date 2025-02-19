package com.kit.maximus.freshskinweb.service;

import com.kit.maximus.freshskinweb.dto.request.order.CreateOrderRequest;
import com.kit.maximus.freshskinweb.dto.request.user.CreateUserRequest;
import com.kit.maximus.freshskinweb.dto.response.UserResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * BaseService sử dụng Generics để tái sử dụng cho các entity khác nhau.
 *
 * @param <T>  Loại Response DTO (ví dụ: UserResponseDTO, ProductResponseDTO)
 * @param <R>  Loại Request DTO khi tạo mới (ví dụ: CreateUserRequest, ProductRequestDTO)
 * @param <U>  Loại Request DTO khi cập nhật (ví dụ: UpdateUserRequest, ProductRequestDTO)
 * @param <ID> Kiểu dữ liệu của ID (Long, String, UUID,...)
 */
public interface BaseService<T, R, U, ID> {

    /** CRUD operations with request objects **/
    boolean add(R request);
    T update(ID id, U request);
    String update(List<ID> id , String status);

//    UserResponseDTO addOrder(Long id, CreateUserRequest request);

    /** Operations that require only ID **/
    boolean delete(ID id);
    boolean delete(List<ID> ids);
    boolean deleteTemporarily(ID id);
    boolean restore(ID id);

    T showDetail(ID id);

    /** Fetching data **/
    Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection,String status, String keyword);

    Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection,String status, String keyword);

//    UserResponseDTO addOrder(Long id, CreateOrderRequest request);
}
