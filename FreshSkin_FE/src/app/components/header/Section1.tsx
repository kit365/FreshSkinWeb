"use client";

import Link from "next/link";
import { useContext, useEffect, useRef, useState } from "react";
import { FaRegCircleUser } from "react-icons/fa6";
import MenuMoreItem from "./MenuMoreItem";
import Cart from "../Cart/Cart";
import { useRouter } from "next/navigation";
import { IoSearchOutline } from "react-icons/io5";
import { SettingProfileContext } from "@/app/(page)/layout";
import { FaRegUserCircle } from "react-icons/fa";
import Compare from "../Compare/Compare";
import FormButton from "@/app/components/Form/FormButton";
import FormFaceGoogle from "@/app/components/Form/FormFaceGoogle";
import FormInput from "@/app/components/Form/FormInput";
import Cookies from 'js-cookie';
import { Alert } from "@mui/material";

export default function Section1() {
    const [openMore, setOpenMore] = useState(false);
    const [contentSearch, setContentSearch] = useState("Vui Lòng Nhập Từ Khóa Vào Ô Tìm Kiếm");
    const [subContentSearch, setSubContentSearch] = useState("Đừng bỏ lỡ");
    const [showSuggest, setShowSuggest] = useState(false);
    const [data, setData] = useState([
        {
            "id": 5,
            "brand": {
                "title": "Cocoon"
            },
            "title": "Nước Tẩy Trang Bí Đao Cocoon Winter Melon Micellar Water",
            "slug": "nuoc-tay-trang-bi-ao-cocoon-winter-melon-micellar-water",
            "thumbnail": [
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741026949/product/nuoc-tay-trang-bi-ao-cocoon-winter-melon-micellar-water_20250303-183549.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741026950/product/nuoc-tay-trang-bi-ao-cocoon-winter-melon-micellar-water_20250303-183550_2.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741026951/product/nuoc-tay-trang-bi-ao-cocoon-winter-melon-micellar-water_20250303-183551_3.webp",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741026952/product/nuoc-tay-trang-bi-ao-cocoon-winter-melon-micellar-water_20250303-183551_4.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741026953/product/nuoc-tay-trang-bi-ao-cocoon-winter-melon-micellar-water_20250303-183553_5.webp"
            ],
            "variants": [
                {
                    "id": 7,
                    "price": 116000.0,
                    "volume": 140,
                    "unit": "ML"
                },
                {
                    "id": 6,
                    "price": 179000.0,
                    "volume": 500,
                    "unit": "ML"
                }
            ],
            "discountPercent": 30,
            "benefits": "Hỗ trợ làm sạch da",
            "deleted": false
        },
        {
            "id": 6,
            "brand": {
                "title": "Cocoon"
            },
            "title": "Nước dưỡng tóc tinh dầu bưởi",
            "slug": "nuoc-duong-toc-tinh-dau-buoi",
            "thumbnail": [
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027644/product/nuoc-duong-toc-tinh-dau-buoi_20250303-184723.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027645/product/nuoc-duong-toc-tinh-dau-buoi_20250303-184724_2.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027646/product/nuoc-duong-toc-tinh-dau-buoi_20250303-184726_3.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027648/product/nuoc-duong-toc-tinh-dau-buoi_20250303-184727_4.jpg"
            ],
            "variants": [
                {
                    "id": 8,
                    "price": 165000.0,
                    "volume": 140,
                    "unit": "ML"
                }
            ],
            "discountPercent": 30,
            "benefits": "Kích thích mọc tóc, giảm gãy rụng. Kiểm soát dầu nhờn, giúp tóc chắc khỏe. Cấp ẩm nhẹ, giảm khô da đầu.",
            "deleted": false
        },
        {
            "id": 7,
            "brand": {
                "title": "SVR"
            },
            "title": "Gel Rửa Mặt SVR Không Chứa Xà Phòng Cho Da Dầu",
            "slug": "gel-rua-mat-svr-khong-chua-xa-phong-cho-da-dau",
            "thumbnail": [
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027716/product/gel-rua-mat-svr-khong-chua-xa-phong-cho-da-dau_20250303-184835.webp",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027717/product/gel-rua-mat-svr-khong-chua-xa-phong-cho-da-dau_20250303-184836_2.webp",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027717/product/gel-rua-mat-svr-khong-chua-xa-phong-cho-da-dau_20250303-184837_3.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027718/product/gel-rua-mat-svr-khong-chua-xa-phong-cho-da-dau_20250303-184838_4.jpg",
                "https://res.cloudinary.com/dr53sfboy/image/upload/v1741027719/product/gel-rua-mat-svr-khong-chua-xa-phong-cho-da-dau_20250303-184839_5.jpg"
            ],
            "variants": [
                {
                    "id": 9,
                    "price": 100000.0,
                    "volume": 55,
                    "unit": "ML"
                },
                {
                    "id": 10,
                    "price": 265000.0,
                    "volume": 200,
                    "unit": "ML"
                },
                {
                    "id": 11,
                    "price": 408000.0,
                    "volume": 400,
                    "unit": "ML"
                }
            ],
            "discountPercent": 10,
            "benefits": "Hỗ trợ làm sạch da",
            "deleted": false
        }
    ]);

    const router = useRouter();
    const formRef = useRef<HTMLFormElement | null>(null);

    const handleKeyUpSearch = async (event: any) => {
        const response = await fetch(`https://freshskinweb.onrender.com/home/suggest`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            body: event.target.value
        });
        const dataResponse = await response.json();
        setData(dataResponse.data);
        setSubContentSearch("Đề xuất tìm kiếm");
        setContentSearch(`Tìm Kiếm: ${event.target.value}`)
    }

    const handleSubmitSearch = async (event: any) => {
        event.preventDefault();

        const response = await fetch(`https://freshskinweb.onrender.com/home/search?keyword=${event.target.keyword.value}`);
        const dataResponse = await response.json();

        setShowSuggest(false);

        if (dataResponse.code === 200) {
            router.push(`/products/search?keyword=${dataResponse.data.title}`)
        }
    }

    const handleSearchInSuggest = async (keyword: string) => {
        const response = await fetch(`https://freshskinweb.onrender.com/home/search?keyword=${keyword}`);
        const dataResponse = await response.json();

        setShowSuggest(false);
        if (dataResponse.code === 200) {
            router.push(`/products/search?keyword=${dataResponse.data.title}`)
        }
    }

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (formRef.current && !formRef.current.contains(event.target as Node)) {
                setShowSuggest(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile, setting } = settingProfile;

    //Popup
    const [isPopupLoginOpen, setIsPopupLoginOpen] = useState(false);

    const handleOpenPopup = () => {
        setIsPopupLoginOpen(true);
    };

    const handleClosePopup = () => {
        setIsPopupLoginOpen(false);
    };

    const [resetPassword, setResetPassword] = useState(false);
    const [alert, setAlert] = useState<any>();

    const handleSubmitLogin = async (event: any) => {
        event.preventDefault();

        if (!event.target.username.value) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập tên người dùng"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        if (!event.target.password.value) {
            setAlert({
                severity: "error",
                content: "Vui lòng nhập mật khẩu"
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000);
            return;
        }

        const response = await fetch('https://freshskinweb.onrender.com/auth/login', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: 'include',
            body: JSON.stringify({
                username: event.target.username.value,
                password: event.target.password.value
            })
        });


        const dataResponse = await response.json();

        if (dataResponse.code == 200) {
            const token = dataResponse.data.token;
            Cookies.set('tokenUser', token);
            window.location.href = "/quiz"
        }
        else {
            setAlert({
                severity: "error",
                content: dataResponse.message
            });

            setTimeout(() => {
                setAlert({
                    severity: "",
                    content: ""
                })
            }, 3000)
        }
    }

    const handleForgotPassword = async (event: any) => {
        event.preventDefault();

        const response = await fetch('https://freshskinweb.onrender.com/admin/forgot-password/request', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                email: event.target.email.value,
            })
        });
        const dataResponse = await response.json();

        if (dataResponse.code == 500) {

        }
        if (dataResponse.code == 200) {
            location.href = `/user/otp?email=${event.target.email.value}`
        }
    }

    return (
        <div className="container mx-auto flex items-center py-[10px]">
            {/* Logo */}
            <Link href="/">
                <div className="mr-[23px] w-[190px] h-[60px]">
                    <img src={setting?.logo || 'https://res.cloudinary.com/dr53sfboy/image/upload/v1742010540/product-brand/test_20250315-034900_4.png'} className="w-full h-full object-cover" />
                </div>
            </Link>
            {/* Search */}
            <form ref={formRef} onSubmit={handleSubmitSearch} autoComplete="off" className={`bg-[#F6F6F6] flex items-center px-[20px] w-[412px] mr-[120px] relative ${showSuggest ? "rounded-tl-[25px] rounded-tr-[25px]" : "rounded-[40px]"}`}>
                <input
                    type="text"
                    name="keyword"
                    placeholder="Bạn muốn tìm gì?"
                    onKeyUp={handleKeyUpSearch}
                    onClick={() => setShowSuggest(true)}
                    className="h-[50px] outline-none order-2 bg-transparent" />
                <button
                    type="submit"
                    className="order-1 text-[25px] text-[#4E4E4E] mr-[20px] font-[100]"
                >
                    <IoSearchOutline />
                </button>
                {showSuggest && (
                    <div className="bg-[#F6F6F6] rounded-bl-[25px] rounded-br-[25px] pt-[20px] w-[412px] px-[20px] absolute top-[80%] left-[0%] z-[100]">
                        <div className="flex items-center">
                            <div className="icon-search-suggest relative">
                                <IoSearchOutline className="text-white text-[16px] absolute left-[6px] top-[7px]" />
                            </div>
                            <span className="text-[14px] font-[550] ml-[10px]">{contentSearch}</span>
                        </div>
                        <div className="uppercase text-[16px] font-[500] text-[#000] my-[15px]">{subContentSearch}</div>
                        {data && data.map((item: any, index: number) => (
                            <div key={index}>
                                <div className="flex items-center mb-[15px]">
                                    <div className="flex-1">
                                        <Link onClick={() => setShowSuggest(false)} href={`/detail/${item.slug}`} className="text-[14px] font-[400] text-[#000] hover:text-secondary cursor-pointer mb-[5px] line-clamp-2">{item.title}</Link>
                                        <div className="flex items-center">
                                            <span className="text-[14px] font-[500] pr-[10px] text-primary">{((item.variants[0].price) * (1 - item.discountPercent / 100)).toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                                            <span className="text-[12px] font-[300] text-[#9e9e9e] line-through">{(item.variants[0].price).toLocaleString("en-US")}<sup className="underline">đ</sup></span>
                                        </div>
                                    </div>
                                    <div className="w-[50px] aspect-square ml-[5px]">
                                        <img src={item.thumbnail[0]} className="w-full h-full object-cover" />
                                    </div>
                                </div>
                            </div>
                        ))}
                        {data && data.length > 3 && (
                            <div
                                className="text-[#000] py-[5px] text-[14px] font-[550] text-center cursor-pointer hover:text-secondary"
                                onClick={() => handleSearchInSuggest(contentSearch.split("Tìm Kiếm: ")[1])}
                            >
                                Xem tất cả
                            </div>
                        )}
                    </div>
                )}
            </form>
            {/* Menu */}
            <div className="flex-1 flex items-center">
                {profile.username !== "" ? (
                    <Link href="/quiz">
                        <div className="flex items-center">
                            <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742357768/product-brand/dsa_20250319-041608_6.png" width={28} height={28} />
                            <span className="text-[12px] font-[600] ml-[4px] hover:text-primary">Kiểm Tra Loại Da</span>
                        </div>
                    </Link>
                ) : (
                    <div onClick={() => { handleOpenPopup() }} className="flex items-center cursor-pointer">
                        <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742357768/product-brand/dsa_20250319-041608_6.png" width={28} height={28} />
                        <span className="text-[12px] font-[600] ml-[4px] hover:text-primary">Kiểm Tra Loại Da</span>
                    </div>
                )}

                <Link href="/blogs/tin-tuc">
                    <div className="flex items-center ml-[15px]">
                        <img src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742357767/product-brand/dsa_20250319-041606_5.png" width={28} height={28} />
                        <span className="text-[12px] font-[600] ml-[4px] hover:text-primary">Tạp Chí Làm Đẹp</span>
                    </div>
                </Link>
                <button className="ml-[15px] flex items-center">
                    {openMore == false && (
                        <img
                            src="https://res.cloudinary.com/dr53sfboy/image/upload/v1742357763/product-brand/dsa_20250319-041602.png"
                            width={28}
                            height={28}
                            onClick={() => setOpenMore(!openMore)}
                        />
                    )}
                    {openMore && (
                        <div className="relative">
                            <span
                                className="text-[14px] font-[600] flex justify-center items-center w-[28px] h-[28px] mb-[2px]"
                                onClick={() => setOpenMore(false)}
                            >
                                X
                            </span>
                            <ul className="menu-more p-0 m-0 bg-[#fff] rounded-[10px] w-[200px] absolute top-[35px] right-[5px] z-[999]">
                                <MenuMoreItem
                                    text="Trung tâm hỗ trợ"
                                    icon="https://res.cloudinary.com/dr53sfboy/image/upload/v1742359281/product-brand/dsadas_20250319-044121_8.webp"
                                    link="/contact"
                                />
                                <MenuMoreItem
                                    text="Tra cứu đơn hàng"
                                    icon="https://res.cloudinary.com/dr53sfboy/image/upload/v1742359284/product-brand/dsadas_20250319-044124_13.webp"
                                    link="/search-order"
                                />
                            </ul>
                        </div>
                    )}
                    <span className="ml-[15px]">|</span>
                </button>
                {profile.firstName !== "" ? (
                    <Link href="/user/profile">
                        <div className="flex items-center ml-[15px]">
                            <FaRegUserCircle className="text-[28px]" />
                            <span className="text-[12px] font-[600] ml-[3px] hover:text-primary">Tài khoản</span>
                        </div>
                    </Link>
                ) : (
                    <Link href="/user/login">
                        <div className="flex items-center ml-[15px]">
                            <FaRegCircleUser className="text-[28px]" />
                            <span className="text-[12px] font-[600] ml-[3px] hover:text-primary">Đăng Nhập</span>
                        </div>
                    </Link>
                )}
                <div className="relative">
                    <Compare />
                </div>
                <div className="relative">
                    <Cart />
                </div>
            </div>

            {isPopupLoginOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 z-[99999999] flex justify-center items-start pt-[8%]" onClick={handleClosePopup}>
                    <div className="container mx-auto w-[432px] bg-[#fff] p-[10px] rounded-[10px]" onClick={(e) => e.stopPropagation()}>
                        <Alert icon={false} severity="success">
                            Bạn cần đăng nhập để thực hiện chức năng này
                        </Alert>
                        <form onSubmit={handleSubmitLogin} className=" mt-[15px] text-center rounded-[10px] relative">
                            <h1 className="text-primary text-[26px] font-[400] uppercase mb-[35px] mt-[10px] login">Đăng nhập</h1>
                            <FormInput
                                placeholder="Tên tài khoản"
                                name="username"
                            />
                            <FormInput
                                type="password"
                                placeholder="Mật khẩu"
                                name="password"
                            />
                            {/* Alert */}
                            {alert && (
                                <Alert style={{ marginBottom: "10px" }} severity={alert.severity}>{alert.content}</Alert>
                            )}
                            <FormButton text="Đăng nhập" />
                        </form>
                        <div className="flex items-center justify-between mb-[15px]">
                            <span
                                className="text-[#333] text-[14px] hover:text-primary cursor-pointer"
                                onClick={() => setResetPassword(!resetPassword)}
                            >
                                Quên mật khẩu?
                            </span>
                            <Link onClick={() => setIsPopupLoginOpen(false)} href="/user/register" className="text-[#333] text-[14px] hover:text-primary">Đăng ký tại đây</Link>
                        </div>
                        <form onSubmit={handleForgotPassword} className={(resetPassword ? "block" : "hidden")}>
                            <FormInput
                                type="email"
                                placeholder="Email"
                                name="email"
                            />
                            <FormButton text="Lấy lại mật khẩu" />
                        </form>
                        <FormFaceGoogle info="hoặc đăng nhập qua" />
                    </div>
                </div>
            )}
        </div>

    );
}
