package com.kit.maximus.freshskinweb.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum ErrorCode {

    //util
    KEY_INVALID(400, "Key Invalid"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception"),
    STATUS_INVALID(400, "Trạng thái chỉ được là 'ACTIVE' hoặc 'INACTIVE"),
    SKINTYPE_INVALID(400, "SkinType Invalid"),

    //User
    USER_NOT_FOUND(404, "User Not Found"),
    USER_NOT_NULL(400, "User Not Null"),
    USERNAME_NOT_NULL(400, "Tên người dùng không được để trống"),
    USERNAME_NOT_BLANK(400, "Tên đăng nhập không được để trống"),
    USERNAME_INVALID(400, "Tên đăng nhập phải từ 5 đến 20 ký tự"),
    PASSWORD_NOT_NULL(400, "Password Not Null"),
    PASSWORD_NOT_BLANK(400, "Mật khẩu không được để trống"),
    PASSWORD_INVALID(400, "Mật khẩu phải từ 8 đến 20 ký tự"),
    PASSWORD_REGEXP_INVALID(400, "Mật khẩu phải chứa ít nhất một chữ cái và một số"),
    USER_EXISTED(400, "User already existed"),
    EMAIL_EXISTED(400, "Email already existed"),
    EMAIL_NOT_BLANK(400, "Email không được để trống"),
    EMAIL_INVALID(400, "Email không hợp lệ"),
    PHONE_NUMBER_EXISTED(400, "Phone Number already existed"),
    PHONE_NUMBER_NOT_BLANK(400, "Số điện thoại không được để trống"),
    PHONE_NUMBER_INVALID(400, "Số điện thoại không hợp lệ"),
    FIRSTNAME_NOT_BLANK(400, "Họ không được để trống"),
    FIRSTNAME_INVALID(400, "Họ phải từ 2 đến 50 ký tự"),
    LASTNAME_NOT_BLANK(400, "Tên không được để trống"),
    LASTNAME_INVALID(400, "Tên phải từ 2 đến 50 ký tự"),
    ADDRESS_NOT_BLANK(400, "Địa chỉ không được để trống"),
    ADDRESS_INVALID(400, "Địa chỉ phải từ 5 đến 255 ký tự"),
    AVATAR_NOT_NULL(400, "Ảnh đại diện không được để trống"),
    AVATAR_NOT_BLANK(400, "Ảnh đại diện không được để trống"),
    STATUS_NOT_BLANK(400, "Trạng thái không được để trống"),
    TYPE_USER_NOT_BLANK(400, "Loại tài khoản không được để trống"),
    TYPE_USER_INVALID(400, "Loại tài khoản chỉ được là 'NORMAL' hoặc 'VIP'"),
    ROLE_ID_NOT_BLANK(400, "Quyền người dùng không được để trống"),

    //Authentication
    UNAUTHENTICATED(401, "Chưa xác thực"),

    //Product
    SORT_DIRECTION_INVALID(400, "Hướng sắp xếp không hợp lệ [ASC hoặc DESC]"),
    PRODUCT_NOT_FOUND(404, "Không tìm thấy sản phẩm"),
    INVALID_REQUEST_PRODUCTID(400, "Yêu cầu không hợp lệ: Thiếu 'id' trong nội dung yêu cầu"),
    VOLUME_EXISTED(400, "Dung tích đã tồn tại"),

    //ProductCategory
    PRODUCT_CATEGORY_NOT_FOUND(404, "Không tìm thấy danh mục sản phẩm"),

    //ProductVariant
    PRODUCT_VARIANT_NOT_FOUND(404, "Không tìm thấy biến thể sản phẩm"),

    //ProductBrand
    PRODUCT_BRAND_NOT_FOUND(404, "Không tìm thấy thương hiệu sản phẩm"),

    //Role
    ROLE_NOT_FOUND(404, "Không tìm thấy vai trò"),
    ROLE_EXISTED(400, "Vai trò đã tồn tại"),

    //ORDER
    ORDER_NOT_FOUND(404, "Không tìm thấy đơn hàng"),
    TOTAL_AMOUNT_NOT_NULL(400, "Tổng số lượng sản phẩm không được để trống"),
    TOTAL_AMOUNT_INVALID(400, "Tổng số lượng sản phẩm phải lớn hơn hoặc bằng 1"),
    TOTAL_PRICE_NOT_NULL(400, "Tổng giá trị đơn hàng không được để trống"),
    TOTAL_PRICE_INVALID(400, "Tổng giá trị đơn hàng phải lớn hơn 0"),
    PAYMENT_METHOD_NOT_NULL(400, "Phương thức thanh toán không được để trống"),
    ORDER_DATE_NOT_NUL(400, "Ngày đặt hàng không được để trống"),

    //BLOG CATEGORY
    BLOG_CATEGORY_NAME_EXISTED(404, "Tên danh mục blog đã tồn tại"),
    BLOG_CATEGORY_NOT_FOUND(404, "Không tìm thấy danh mục blog"),

    //BLOG
    BLOG_NAME_EXISTED(404, "Tên blog đã tồn tại"),
    BLOG_NOT_FOUND(404, "Không tìm thấy blog"),
    INVALID_REQUEST_BLOGID(400, "Yêu cầu không hợp lệ: BlogId không hợp lệ"),

    //ORDER ITEMS
    ORDER_ITEM_NOT_FOUND(404, "Không tìm thấy đơn hàng"),

    //SKIN_TYPE
    SKIN_TYPE_NOT_FOUND(404, "Không tìm thấy loại da"),

    //REVIEW
    RATING_INVALID(400, "Giá trị phải từ 1 đến 5"),
    REVIEW_NOT_FOUND(404, "Không tìm thấy đánh giá"),


    //SKIN QUESTIONS
    SKIN_QUESTIONS_NOT_FOUND(404, "Không tìm thấy bộ đề")
    ;





    long code;
    String message;


    ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
