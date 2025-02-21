package com.kit.maximus.freshskinweb.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum ErrorCode {

    //util
    KEY_INVALID(400, "Key Invalid"),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception"),
    STATUS_INVALID(400, "Status Invalid[Active || Inactive || SOFT_DELETED || RESTORED]"),
    SKINTYPE_INVALID(400, "SkinType Invalid"),

    //User
    USER_NOT_FOUND(404, "User Not Found"),
    USER_NOT_NULL(400, "User Not Null"),
    USER_NOT_BLANK(400, "User Not Blank"),
    USERNAME_INVALID(400, "Username must be more than 5 and less than 20 characters"),
    PASSWORD_NOT_NULL(400, "Password Not Null"),
    PASSWORD_NOT_BLANK(400, "Password Not Blank"),
    PASSWORD_INVALID(400, "Password must be more than 5 and less than 20 characters"),
    USER_EXISTED(400, "User already existed"),
    EMAIL_EXISTED(400, "Email already existed"),
    PHONE_NUMBER_EXISTED(400, "Phone Number already existed"),
    //Authentication
    UNAUTHENTICATED(401, "Unauthenticated"),

   //Product
    SORT_DIRECTION_INVALID(400, "Sort Direction Invalid[ASC or DESC]"),
    PRODUCT_NOT_FOUND(404, "Product Not Found"),
    INVALID_REQUEST_PRODUCTID(400, "Invalid Request ProductId[Missing 'id' key in request body]"),
    VOLUME_EXISTED(400, "Volume already existed"),

    //ProductCategory
    PRODUCT_CATEGORY_NOT_FOUND(404, "Product Category Not Found"),
    //ProductBrand
    PRODUCT_BRAND_NOT_FOUND(404, "Product Brand Not Found"),

    //Role
    ROLE_NOT_FOUND(404, "Role Not Found"),

    //ORDER
    ORDER_NOT_FOUND(404, "Order Not Found"),

    //BLOG CATEGORY
    BLOG_CATEGORY_NAME_EXISTED(404, "Blog Category Name already existed"),
    BLOG_CATEGORY_NOT_FOUND(404, "Blog Category Not Found"),

    //BLOG
    BLOG_NAME_EXISTED(404, "Blog Category Name already existed"),
    BLOG_NOT_FOUND(404, "Blog Category Not Found"),
    INVALID_REQUEST_BLOGID(400, "Invalid Request BlogId"),
    ;




    long code;
    String message;


    ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

}
