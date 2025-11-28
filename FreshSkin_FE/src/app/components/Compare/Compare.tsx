"use client"

import { SettingProfileContext } from "@/app/(page)/layout";
import Link from "next/link";
import { useContext, useState } from "react";
import { IoIosGitCompare } from "react-icons/io";
import { MdOutlineCompare } from "react-icons/md";
import FormButton from "@/app/components/Form/FormButton";
import FormFaceGoogle from "@/app/components/Form/FormFaceGoogle";
import FormInput from "@/app/components/Form/FormInput";
import Cookies from 'js-cookie';
import { Alert } from "@mui/material";

export default function Compare() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    // Hover Item Cart
    const [isHover, setIsHover] = useState(false);

    const handleMouseEnter = () => setIsHover(true);
    const handleMouseLeave = () => setIsHover(false);
    // End Hover Item Cart

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
            window.location.href = "/compare"
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
        <>
            {profile.firstName !== "" ? (
                <Link href="/compare">
                    <div className="flex items-center ml-[13px] mb-[-30px] pb-[30px] relative" onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
                        <IoIosGitCompare className="text-[32px]" />
                        {profile.productComparisonId?.products && profile.productComparisonId.products?.length > 0 ? (
                            <span className="h-[16px] w-[16px] rounded-full flex items-center justify-center absolute bg-primary text-white text-[10px] top-[1px] left-[18px]">{profile?.productComparisonId?.products?.length}</span>
                        ) : (
                            <span className="h-[16px] w-[16px] rounded-full flex items-center justify-center absolute bg-primary text-white text-[10px] top-[1px] left-[18px]">0</span>
                        )}
                    </div>
                </Link>
            ) : (
                <div onClick={() => { handleOpenPopup() }} className="flex cursor-pointer items-center ml-[13px] mb-[-30px] pb-[30px] relative" onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
                    <IoIosGitCompare className="text-[32px]" />
                    {profile.productComparisonId?.products && profile.productComparisonId.products?.length > 0 ? (
                        <span className="h-[16px] w-[16px] rounded-full flex items-center justify-center absolute bg-primary text-white text-[10px] top-[1px] left-[18px]">{profile?.productComparisonId?.products?.length}</span>
                    ) : (
                        <span className="h-[16px] w-[16px] rounded-full flex items-center justify-center absolute bg-primary text-white text-[10px] top-[1px] left-[18px]">0</span>
                    )}
                </div>
            )}

            {isHover && profile.productComparisonId?.products?.length == 0 && (
                <div
                    className="cart-hover-show w-[340px] py-[15px] px-[10px] flex flex-wrap justify-center items-center rounded-[5px] bg-[#fff] overflow-hidden z-[999] absolute top-[46px] right-[0px]"
                    onMouseEnter={handleMouseEnter}
                    onMouseLeave={handleMouseLeave}
                >
                    <MdOutlineCompare className="text-[32px]" />
                    <p className="mt-[15px] text-[14px] text-textColor text-center">Không có sản phẩm nào trong danh sách so sánh của bạn</p>
                </div>
            )}
            {isHover && profile.productComparisonId?.products?.length != 0 && (
                <div
                    className="cart-hover-show w-[340px] rounded-[5px] bg-[#fff] overflow-hidden z-[999] absolute top-[46px] right-[0px]"
                    onMouseEnter={handleMouseEnter}
                    onMouseLeave={handleMouseLeave}
                >
                    <div className="px-[10px]">
                        {profile.productComparisonId?.products?.map((item: any, index: number) => (
                            <div key={index} className="p-[10px] max-h-[360px] overflow-y-auto flex border-b border-solid boder-[#ddd]">
                                <div className="w-[18%]">
                                    <div className="w-[60px] aspect-square">
                                        <Link href={`/detail/${item.slug}`}>
                                            <img src={item.thumbnail[0]} className="w-full h-full object-cover" />
                                        </Link>
                                    </div>
                                </div>
                                <div className="flex-1 ml-[35px]">
                                    <Link href={`/detail/${item.slug}`} className="text-[13px] font-[400] text-textColor pr-[5px] line-clamp-2 hover:text-secondary cursor-pointer">{item.title}</Link>
                                    <span className="text-[12px] text-primary">{item.brand?.title}</span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

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
        </>
    )
}