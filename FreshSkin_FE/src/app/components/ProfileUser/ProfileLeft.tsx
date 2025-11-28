"use client"

import { usePathname } from "next/navigation";
import Cookies from 'js-cookie';
import { useContext } from "react";
import { SettingProfileContext } from "@/app/(page)/layout";
import Link from "next/link";

export default function ProfileLeft() {
    const pathname = usePathname();
    const settingProfile = useContext(SettingProfileContext);
    
    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const handleClickLogout = async () => {
        Cookies.remove('tokenUser');
        location.href = "/"
    };

    return(
        <>
            <div className="w-[304px] px-[10px]">
                <div className="uppercase text-[19px] font-[400] text-[#212B25] mb-[7px]">Trang tài khoản</div>
                <div className="text-[14px] hover:text-secondary font-[700] text-[#212B25] mb-[28px]">Xin chào, <span className="text-primary">{profile.firstName} {profile.lastName}</span> !</div>
                <div className={`text-[#212B25] hover:text-secondary cursor-pointer text-[14px] font-[400] mb-[15px] ` + (pathname == "/user/profile" ? "text-secondary" : "")}>
                    <Link href="/user/profile">
                        Thông tin tài khoản
                    </Link>
                </div>
                <div className={`text-[#212B25] hover:text-secondary cursor-pointer text-[14px] font-[400] mb-[15px] ` + (pathname == "rountine" ? "text-secondary" : "")}>
                    <Link href="/rountine">
                        Lộ trình da của bạn
                    </Link>
                </div>
                <div className={`text-[#212B25] hover:text-secondary cursor-pointer text-[14px] font-[400] mb-[15px] ` + (pathname == "/user/orders" ? "text-secondary" : "")}>
                    <Link href="/user/orders">
                        Đơn hàng của bạn
                    </Link>
                </div>
                <div className={`text-[#212B25] hover:text-secondary cursor-pointer text-[14px] font-[400] mb-[15px] ` + (pathname == "/user/changePassword" ? "text-secondary" : "")}>
                    <Link href="/user/change-password">
                        Đổi mật khẩu
                    </Link></div>
                <button onClick={handleClickLogout} className="text-[#212B25] hover:text-secondary cursor-pointer text-[14px] font-[400] mb-[15px]">Đăng xuất</button>
            </div>
        </>
    )
}