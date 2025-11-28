"use client"

import MenuUnShow from "./MenuUnShow";
import MenuShowBrand from "./MenuShowBrand";
import MenuShowMainCategory from "./MenuShowMainCategory";

export default function Section2() {
    return(
        <>
            <div className="container mx-auto flex items-center justify-center py-[10px]">
                <MenuUnShow text="Trang chủ" link="/"/>
                <MenuShowBrand text="Thương hiệu" link='/product-category/thuong-hieu'/>
                <MenuShowMainCategory text="Chăm sóc da mặt" link='/product-category/cham-soc-da-mat'/>
                <MenuUnShow text="Khuyến mãi HOT" image="https://res.cloudinary.com/dr53sfboy/image/upload/v1742357763/product-brand/dsa_20250319-041603_2.png" link="/product-category/khuyen-mai-hot"/>  
                <MenuUnShow text="Sản phẩm mới" link="/product-category/san-pham-moi"/>
                <MenuUnShow text="Top bán chạy" link="/product-category/top-ban-chay"/>  
                <MenuUnShow text="Câu hỏi thường gặp" link="/cau-hoi-thuong-gap"/>  
            </div>
        </>
    )
}