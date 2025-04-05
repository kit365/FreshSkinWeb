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
    IMAGE_UPLOAD_FAILED(400,"Lỗi upload hình"),

    //User
    USER_NOT_FOUND(404, "Không tìm thấy tên người dùng"),
    USER_NOT_NULL(400, "User Not Null"),
    USERNAME_NOT_NULL(400, "Tên đăng nhập không được để trống"),
    USERNAME_NOT_BLANK(400, "Tên đăng nhập không được để trống"),
    USERNAME_INVALID(400, "Tên đăng nhập phải từ 5 đến 20 ký tự"),
    USERNAME_NOT_FOUND(400, "Không tìm thấy tên đăng nhập"),
    PASSWORD_NOT_NULL(400, "Password Not Null"),
    PASSWORD_NOT_BLANK(400, "Mật khẩu không được để trống"),
    PASSWORD_INVALID(400, "Mật khẩu phải từ 8 đến 20 ký tự"),
    PASSWORD_REGEXP_INVALID(400, "Mật khẩu phải chứa ít nhất một chữ cái và một số"),
    USER_EXISTED(400, "Người dùng này đã tồn tại"),
    EMAIL_NOT_FOUND(400, "Không tìm thấy email"),
    EMAIL_EXISTED(400, "Email already existed"),
    EMAIL_NOT_BLANK(400, "Email không được để trống"),
    EMAIL_INVALID(400, "Email không hợp lệ"),
    EMAIL_SEND_ERROR(400, "Lỗi gửi email"),
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
    ROLE_NAME_NOT_BLANK(400, "Quyền người dùng không được để trống"),
    THIS_USER_NOT_ALLOWED_TO_DELETE(400, "Đây là tài khoản của khách hàng, không được xóa"),
    THIS_USER_NOT_ALLOWED_TO_UPDATE(400, "Đây là tài khoản của khách hàng, không được cập nhật"),
    THIS_ACCOUNT_CAN_NOT_SHOW(400, "Đây là tài khoản của khách hàng, không thể xem"),
    INVALID_EMAIL(400, "Email không hợp lệ"),
    INVALID_OLD_PASSWORD(400, "Mật khẩu cũ không chính xác"),
    INVALID_NEW_PASSWORD(400, "Mật khẩu mới không hợp lệ"),
    PASSWORDS_DO_NOT_MATCH(400, "Mật khẩu không khớp"),
    INVALID_TOKEN(400, "Token không hợp lệ"),

    //Authentication
    UNAUTHENTICATED(401, "Chưa xác thực"),
    PASSWORD_INCORRECT(400, "Mật khẩu không chính xác"),
    ACCOUNT_LOCKED(400, "Tài khoản đã bị khóa"),

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

    //ProductCompare
    PRODUCT_COMPARISON_NOT_FOUND(404, "Không tìm thấy bảng so sánh"),
    PRODUCT_NOT_IN_COMPARISON(404, "Sảh phẩm không nằm trong bảng so sánh này"),

    //Role
    ROLE_NOT_FOUND(404, "Không tìm thấy vai trò"),
    ROLE_EXISTED(400, "Vai trò đã tồn tại"),
    ROLE_ACCESS_DENIED(400, "Vai trò không được phép truy cập"),

    //ORDER
    ORDER_NOT_FOUND(404, "Không tìm thấy đơn hàng"),
    TOTAL_AMOUNT_NOT_NULL(400, "Tổng số lượng sản phẩm không được để trống"),
    TOTAL_AMOUNT_INVALID(400, "Tổng số lượng sản phẩm phải lớn hơn hoặc bằng 1"),
    TOTAL_PRICE_NOT_NULL(400, "Tổng giá trị đơn hàng không được để trống"),
    TOTAL_PRICE_INVALID(400, "Tổng giá trị đơn hàng phải lớn hơn 0"),
    PAYMENT_METHOD_NOT_NULL(400, "Phương thức thanh toán không được để trống"),
    ORDER_DATE_NOT_NUL(400, "Ngày đặt hàng không được để trống"),
    ORDER_INVALID(400, "Đơn hàng không hợp lệ"),

    //BLOG CATEGORY
    BLOG_CATEGORY_NAME_EXISTED(404, "Tên danh mục blog đã tồn tại"),
    BLOG_CATEGORY_NOT_FOUND(404, "Không tìm thấy danh mục blog"),

    //BLOG
    BLOG_NAME_EXISTED(404, "Tên blog đã tồn tại"),
    BLOG_NOT_FOUND(404, "Không tìm thấy blog"),
    INVALID_REQUEST_BLOGID(400, "Yêu cầu không hợp lệ: BlogId không hợp lệ"),

    //ORDER ITEMS
    ORDER_ITEM_NOT_FOUND(404, "Không tìm thấy đơn hàng"),

    //SKIN CARE ROUNTINE
    SKIN_CARE_ROUTINE_NOT_FOUND(404, "Không tìm thấy lộ trình chăm sóc da"),
    SKIN_CARE_ROUTINE_ALREADY_EXISTS(400, "Lộ trình chăm sóc da đã tồn tại"),

    //SKIN TYPE
    SKIN_TYPE_NOT_FOUND(404, "Không tìm thấy loại da"),
    INVALID_SKIN_TYPE(404, "Loại da không hợp lệ"),
    SEND_EMAIL_SKIN_TYPE_ERROR(404, "Lỗi gửi email kết quả phân tích da"),

    //SKIN TEST
    SKIN_TEST_NOT_FOUND(404, "Không tìm thấy bài đánh giá loại da nào"),

    //ROUTINE STEP
    ROUTINE_STEP_NOT_FOUND(404, "Không tìm thấy bước chăm sóc da nào"),
    DUPLICATE_POSITION(404, "Duplicate position in routine steps"),
    //REVIEW
    RATING_INVALID(400, "Giá trị phải từ 1 đến 5"),
    REVIEW_NOT_FOUND(404, "Không tìm thấy đánh giá"),


    //STATUS
    STATUS_NOT_FOUND(404, "Không tìm thấy trạng thái"),
    INVALID_STATUS(400, "Trạng thái không hợp lệ"),

    //TYPE USER
    TYPE_USER_NOT_FOUND(404, "Không tìm thấy loại người dùng"),
    INVALID_TYPE_USER(400, "Loại người dùng không hợp lệ"),

    //SKIN QUESTIONS
    SKIN_QUESTIONS_NOT_FOUND(404, "Không tìm thấy câu hỏi"),
    QUESTION_GROUP_NOT_EXISTED(404, "Không tìm thấy nhóm câu hỏi nào"),

    //PERMISSION
    PERMISSION_NOT_FOUND(404, "Không tìm thấy quyền"),

    //NOTIFICATION
    NOTIFICATION_NOT_FOUND(404, "Không tìm thấy thông báo"),

    //ORDERSTATUS
    ORDER_STATUS_INVALID(400, "Không tìm thấy trạng thái đơn hàng"),


    //PAYMENT METHOD
    INVALID_PAYMENT_METHOD(404,"Phương thức thanh toán không hợp lệ"),

    //DISCOUNT
    DISCOUNT_NOT_FOUND(404, "Không tìm thấy mã giảm giá"),
    DISCOUNT_NOT_STARTED(404, "Mã giảm giá chưa bắt đầu"),
    DISCOUNT_IS_EXISTED(404, "Mã giảm giá đã tồn tại trong hệ thống"),
    DISCOUNT_LIMIT_EXCEEDED(404, "Số lần sử dụng mã giảm giá đã vượt quá giới hạn"),
    PRODUCT_ALREADY_HAS_DISCOUNT(404, "Sản phẩm đã có mã giảm giá"),
    DISCOUNT_EXPIRED(404, "Mã giảm giá đã hết hạn"),
    DISCOUNT_INACTIVE(404, "Mã giảm giá không hoạt động"),
    WRONG_DISCOUNT_TYPE(400, "Loại giảm giá không đúng"),
    DISCOUNT_TYPE_NOT_BE_NULL(400, "Loại mã lỗi không được null"),
    DISCOUNT_NOT_NULL(400, "Mã giảm giá không được để trống"),
    NAME_INVALID(400, "Tên mã giảm giá không được vượt quá 255 ký tự"),
    DISCOUNT_PERCENTAGE_INVALID_MIN(400, "Phần trăm giảm giá phải lớn hơn 0"),
    DISCOUNT_PERCENTAGE_INVALID_MAX(400, "Phần trăm giảm giá không được vượt quá 100%"),
    DISCOUNT_AMOUNT_INVALID(400, "Số tiền giảm giá phải lớn hơn 0"),
    MAX_DISCOUNT_INVALID(400, "Giảm giá tối đa không được nhỏ hơn 0"),
    START_DATE_INVALID(400, "Ngày bắt đầu phải từ hiện tại trở đi"),
    END_DATE_INVALID(400, "Ngày kết thúc phải ở tương lai"),
    USAGE_LIMIT_INVALID(400, "Số lần sử dụng tối thiểu là 1"),







    //IMAGE
    IMAGE_PROCESSING_ERROR(400, "Lỗi xử lý ảnh"),
    FILE_NOT_FOUND(400, "Không tìm thấy tệp"),

    //SETTING
    SETTING_NOT_FOUND(404, "Không tìm thấy cài đặt"),

    //SERVER
    INTERNAL_SERVER_ERROR(500, "Lỗi máy chủ"),

    //OTP
    OTP_EXPIRED(400, "OTP đã hết hạn"),
    INVALID_OTP(400, "OTP không hợp lệ"),

    //QUESTION_GROUP
    QUESTION_GROUP_NOT_FOUND(404, "Không tìm thấy nhóm câu hỏi"),
    QUESTION_GROUP_INVALID(400, "Nhóm câu hỏi không hợp lệ"),

    //SCORE RANGE
    INVALID_SCORE_RANGE(404,"Điểm nằm ngoài phạm vi điểm số "),
    SCORE_RANGE_NOT_FOUND(404, "Không tồn tại bản ghi cho mức điểm xét loại da"),
    INVALID_REQUEST_SCORE_RANGE_ID(400, "Yêu cầu không hợp lệ: Thiếu 'id' trong nội dung yêu cầu"),

    //Review
    //ReviewVote
    CANNOT_VOTE_OWN_REVIEW(400, "Không thể tự vote cho chính mình"),
    ALREADY_VOTED(400, "Đã vote!"),
    VOTE_NOT_FOUND(400, "Lượt vote không tồn tại"),
    VOTE_STATUS_INVALID(404, "Lượt vote không hợp lệ[-1(dislike),0(unvote),1(like)]"),PARENT_REVIEW_NOT_FOUND(404, "Bình luận không tìm thấy, không thể trả lời."),

    //Voucher
    VOUCHER_NOT_FOUND(404, "Không tìm thấy voucher"),
    VOUCHER_IS_EXISTED(400, "Voucher đã tồn tại"),
    VOUCHER_EXPIRED(400, "Voucher đã hết hạn"),
    VOUCHER_NOT_VALID_YET(400, "Voucher chưa có hiệu lực"),
    VOUCHER_IS_USED_UP(400, "Voucher đã được sử dụng hết"),
    ORDER_NOT_ELIGIBLE(400, "Đơn hàng không đủ điều kiện để sử dụng voucher"),
    VOUCHER_INVALID(400, "Voucher không hợp lệ"),
    ENDDATE_INVALID(400, "Ngày kết thúc phải ở tương lai"),
    STARTDATE_INVALID(400, "Ngày bắt đầu phải từ hiện tại trở đi"),
    ENDDATE_INVALID_MUST_AFTER_STARTDATE(400, "Ngày kết thúc phải sau ngày bắt đầu"),


    //system
        INVALID_INPUT(400,"Tham số truyền vào không đúng");




    long code;
    String message;


    ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
