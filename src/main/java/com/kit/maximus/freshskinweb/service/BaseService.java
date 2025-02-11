package com.kit.maximus.freshskinweb.service;

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
    T add(R request);
    T update(ID id, U request);
    List<T> update(List<U> listRequest);

    /** Operations that require only ID **/
    boolean delete(ID id);
    boolean delete(List<ID> ids);
    boolean deleteTemporarily(ID id);
    boolean deleteTemporarily(List<ID> ids);
    boolean restore(ID id);
    boolean restore(List<ID> ids);

    /** Fetching data **/
    Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection,String status, String keyword);
}
