"use client"

import Link from "next/link";
import { useContext, useState } from "react";
import { MdNavigateNext } from "react-icons/md";
import { SettingProfileContext } from "../../layout";
import ProfileLeft from "@/app/components/ProfileUser/ProfileLeft";

export default function EditProfileUser() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    const [selectedImage, setSelectedImage] = useState(profile?.avatar || "/default-avatar.png");
    const handleImageChange = (event: any) => {
        const file = event.target.files[0];
        if (file) {
            const imageUrl = URL.createObjectURL(file);
            setSelectedImage(imageUrl);
        }
    };

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
                    Trang chỉnh sửa thông tin cá nhân
                </li>
            </ul>
            <div className="flex container mx-auto px-[15px] mt-[40px]">
                <ProfileLeft />
                <div className="px-[15px]">
                    <div className="uppercase text-[19px] font-[400] text-[#212B25] mb-[27px]">Chỉnh sửa thông tin</div>
                    <div className="w-full flex items-center mb-[20px]">
                        <div className="relative w-[100px] h-[100px] rounded-full overflow-hidden border border-[#e1e1e1]">
                            <img
                                src={profile?.avatar || "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAJQAlAMBIgACEQEDEQH/xAAbAAEAAgMBAQAAAAAAAAAAAAAAAwUBAgQGB//EADAQAAICAQIEBQIFBQEAAAAAAAABAgMEESEFEjFBEzJRYXEiwRRCUoGxYpGh0fCC/8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhEDEQA/APuIAAAEWRfXjwc7ZKK/kCU5cnOx8fVTnrL9Md2VWXxK2/WNesIe3V/ucIFlbxiyWqprjBest2ck8zKs818/22/ggAG0pSl5pOXyzVagASRvuh5LZx+JM6KuJ5VfWamv6kcYAuqOL1S0V0XW/VbosK7IWRUq5KUfVM8qSU3WUS5qpuL76AepBXYXE4XaQu0hN9H2ZYgAAAAAAAiyLo49UrJvRL/IGmZlV4tXNPeT8se7PPZF9mRY52PV+3RDIunkWuyxvV9PZehGAAAAAAAAAAAAAACz4dxFwaqyJax6Rk+3yVgA9atwVHCM3pj2vV/kf2LcAAADKDiuT41/JB/RB6fLLXiN/wCHxZyXmf0x+WecAAAAAAABJTTZc9Ko66dX2QEYO+HDJNfXak/RLU2lwz0tf7xArgdF2HdUm2uaK7x3OcAAAAAAym0009Guh6Lh+T+Jx4yfnW0vk84dvCb/AAcpRb+mzZ+z7AegAQApON2811dS6RWr+WVpPnz8TNul/Vp/bYgAAAAAEm5JR6voB04WL+IlrLVVp7+/sW8YxjHlikorokjWmtU1RrXZbv1ZuAAAGSvzsNNO2lbrzR9fc7wB55PUHRnVKnIkl5XujnAAAAZ1a3Wz7GAB6jHs8WiFn6opg4+EXL8Hyyflk19/uAKWcuacperbNRpowAAAAmxFrlVJ/qISTHnyZFcn0TAvjAAAAAAABXcXX1VP5+xXndxaetlcF+VN/wBzhAAAAAH0A6ca91waXrqYIYRclqjAG+RHkyLY+kn/ACRnZxavw82T7TXMjjAAAAGgALnBv8ala7zjs/8AZ0FDVZKqxTrejRa42bVckm+Sf6WB0gAAYsnGuDnN6RSNLr66VrZJL27lVl5csiSSTUF0QEV1jttlN93qaAAAAAAAFnwvH8THcmvzv+ECw4ZX4WFWn1a5n+4A5uN081MbUt63p+z/AORSnqrYRsrlCa1jJaM8zfVKm2Vc+sXoBGAAAW70SbbN665XTVcFrJlvi4sKI7fVPvIDip4dZNKVsuRemmrZ2V4WPWvJzP1k9ToABJJJJaIAAQ2YtFmvNWtX3WxyXcN03pn/AOZf7LEAUE4TrnyTi4y9Gal9dTC+HJYtV2foU+TjTxpJN6xfSQEIAAEmPU774VJeZ7/HcjLjgmPpGV8l12j8dwLRbLRAyABXcWxPGh4sFrOC6eqLEAeSMpNtKKbb20LLieA4N30r6XvKK7e5FwunmnK2XljsvkDtxMdY9aWzm1rJ/YmAAAAAAAAAAGttcbYOE1rFmwAor6pUWyrl1Xf1RGWvE6eenxF5odfgraKbMi1QrTbf+AJcPGllXqK2it5P0R6OEIwgoxWiS0SIsPGhi1cker8z9WTgAAAAAB7nN+GhWn4UdFrq0jpAHH7A6ZVqXUilVKPTcCMGQBgAAAAABmMXJ7IljT3kBF4TsTjps1oyTExa8WvlrW76t9WTJJLYyAAAAAAAAAAAAAAYcU+qNHVD3MACKUUjCAAkjXF9dSRVxXYADZGQAAAAAAAAAP/Z"}
                                className="w-full h-full object-cover"
                            />
                            <input
                                type="file"
                                id="avatarUpload"
                                className="absolute inset-0 opacity-0 cursor-pointer"
                                accept="image/*"
                                onChange={handleImageChange}
                            />
                        </div>
                        <label htmlFor="avatarUpload" className="ml-[15px] text-[14px] text-secondary cursor-pointer">
                            Chọn ảnh
                        </label>
                    </div>
                    <div className="flex items-center">
                        <div className="w-[150px]">
                            <label htmlFor="firstName" className="text-[14px] font-[400] text-[#333] cursor-pointer">Họ *</label>
                            <input defaultValue={profile.firstName} id="firstName" name="firstName" className="w-full block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                        </div>
                        <div className="w-[290px] ml-[26px]">
                            <label htmlFor="lastName" className="text-[14px] font-[400] text-[#333] cursor-pointer">Tên *</label>
                            <input defaultValue={profile.lastName} id="lastName" name="lastName" className="w-full block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                        </div>
                    </div>
                    <div>
                        <label htmlFor="email" className="text-[14px] font-[400] text-[#333] cursor-pointer">Email *</label>
                        <input defaultValue={profile.email} type="email" id="email" name="email" className="w-[466px] block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                    </div>
                    <div>
                        <label htmlFor="phoneNumber" className="text-[14px] font-[400] text-[#333] cursor-pointer">Số điện thoại *</label>
                        <input defaultValue={profile.phone} id="phoneNumber" name="phoneNumber" className="w-[466px] block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                    </div>
                    <div>
                        <label htmlFor="phoneNumber" className="text-[14px] font-[400] text-[#333] cursor-pointer">Địa chỉ *</label>
                        <input defaultValue={profile.address} id="phoneNumber" name="phoneNumber" className="w-[466px] block h-[45px] px-[20px] text-[#333] outline-none border border-solid border-[#e1e1e1] bg-white mb-[15px] rounded-[4px]" />
                    </div>
                    <div className="bg-secondary w-[300px] border border-solid border-secondary cursor-pointer hover:bg-white hover:text-secondary text-white px-[10px] py-[5px] text-[14px] text-center rounded-[5px]">Cập nhật thông tin cá nhân</div>
                </div>
            </div>
        </>
    )
}