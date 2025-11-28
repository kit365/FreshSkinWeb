"use client"

import Link from "next/link";
import { useContext } from "react";
import { MdNavigateNext } from "react-icons/md";
import { SettingProfileContext } from "../../layout";
import ProfileLeft from "@/app/components/ProfileUser/ProfileLeft";

export default function ProfileUser() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    return (
        <>
            <ul className="flex items-center container mx-auto px-3">
                <li>
                    <Link href="/" className="flex items-center">
                        <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                        <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                    </Link>
                </li>
                <li className="text-secondary text-[15px] font-[400]">
                    Trang khách hàng
                </li>
            </ul>
            <div className="flex container mx-auto px-[15px] mt-[40px]">
                <ProfileLeft/>
                <div className="px-[15px]">
                    <div className="uppercase text-[19px] font-[400] text-[#212B25] mb-[27px]">Thông tin tài khoản</div>
                    {profile.firstName !== "" && profile.lastName !== "" && (
                        <div className="text-[14px] text-[#00090f] mb-[15px]"><strong>Họ tên: </strong>{profile.firstName} {profile.lastName}</div>
                    )}
                    {profile.email && (
                        <div className="text-[14px] text-[#00090f] mb-[15px]"><strong>Email: </strong>{profile.email}</div>
                    )}
                    {profile.phone && (
                        <div className="text-[14px] text-[#00090f] mb-[15px]"><strong>Số điện thoại: </strong>{profile.phone}</div>
                    )}
                    {profile.skinType && (
                        <div className="text-[14px] text-[#00090f] mb-[15px]"><strong>Loại da: </strong>{profile.skinType}</div>
                    )}
                    <Link href="/user/edit" className="bg-secondary border border-solid border-secondary cursor-pointer hover:bg-white hover:text-secondary text-white px-[10px] py-[5px] text-[14px] text-center rounded-[5px]">Chỉnh sửa thông tin cá nhân</Link>
                </div>
            </div>
        </>
    )
}