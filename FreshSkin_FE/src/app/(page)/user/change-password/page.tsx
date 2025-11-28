"use client"

import ProfileLeft from "@/app/components/ProfileUser/ProfileLeft";
import Link from "next/link";
import { useContext, useState } from "react";
import { MdNavigateNext } from "react-icons/md";
import { SettingProfileContext } from "../../layout";
import { Alert } from "@mui/material";

export default function UserChangePassword() {
    const [alert, setAlert] = useState<any>();
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const handleSubmit = async (event: any) => {
        event.preventDefault();

        const oldPassword = event.target.oldPassword.value;
        const password = event.target.newPassword.value;
        const confirmPassword = event.target.confirmPassword.value;

        const response = await fetch(`https://freshskinweb.onrender.com/admin/account/change-password/${profile.userID}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                oldPassword: oldPassword,
                password: password,
                confirmPassword: confirmPassword
            })
        });
        const dataResponse = await response.json();

        setAlert({
            severity: "success",
            content: dataResponse.message
        });

        setTimeout(() => {
            setAlert({
                severity: "",
                content: ""
            })
        }, 3000)

    }

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
                    Thay đổi mật khẩu
                </li>
            </ul>
            <div className="flex container mx-auto px-[15px] mt-[40px]">
                <ProfileLeft />
                <form onSubmit={handleSubmit} className="px-[15px]">
                    {/* Alert */}
                    {alert && (
                        <Alert style={{ marginBottom: "10px" }} severity={alert.severity}>{alert.content}</Alert>
                    )}
                    <div className="uppercase text-[19px] font-[400] text-[#212B25] mb-[27px]">Đổi mật khẩu</div>
                    <div>
                        <label htmlFor="old-password" className="text-[14px] font-[400] text-[#333] cursor-pointer">Mật khẩu cũ *</label>
                        <input type="password" id="old-password" name="oldPassword" className="w-[466px] block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                    </div>
                    <div>
                        <label htmlFor="new-password" className="text-[14px] font-[400] text-[#333] cursor-pointer">Mật khẩu mới *</label>
                        <input type="password" id="new-password" name="newPassword" className="w-[466px] block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                    </div>
                    <div>
                        <label htmlFor="confirm-password" className="text-[14px] font-[400] text-[#333] cursor-pointer">Xác nhận lại mật khẩu *</label>
                        <input type="password" id="confirm-password" name="confirmPassword" className="w-[466px] block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                    </div>
                    <button type="submit" className="rounded-[3px] px-[15px] h-[40px] bg-primary hover:text-primary hover:bg-white hover:border-primary text-white font-bold text-[14px] mt-[10px] border border-solid border-transparent">Đặt lại mật khẩu </button>
                </form>
            </div>
        </>
    )
}