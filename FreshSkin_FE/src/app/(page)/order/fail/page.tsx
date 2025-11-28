"use client"

import { useContext } from "react";
import { SettingProfileContext } from "../../layout";
import Link from "next/link";
import { XCircle } from "lucide-react";

export default function FailOrder() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { setting } = settingProfile;

    return (
        <div className="container mx-auto pt-10 flex flex-col items-center text-center">
            <Link href="/" className="w-[206px] h-[82px] mb-5">
                <img 
                    src={setting?.logo || 'https://res.cloudinary.com/dr53sfboy/image/upload/v1742010540/product-brand/test_20250315-034900_4.png'} 
                    className="w-full h-full object-cover" 
                    alt="Logo"
                />
            </Link>
            
            <XCircle className="w-20 h-20 text-red-500" />
            
            <h1 className="text-2xl font-bold mt-4 text-gray-800">Thanh toán thất bại</h1>
            <p className="text-gray-600 mt-2">Có vẻ như đã có sự cố khi xử lý thanh toán của bạn. Vui lòng thử lại.</p>
            
            <Link 
                href="/" 
                className="mt-6 px-6 py-3 bg-blue-500 text-white rounded-lg shadow-md hover:bg-blue-600 transition"
            >
                Quay lại trang chủ
            </Link>
        </div>
    );
}
