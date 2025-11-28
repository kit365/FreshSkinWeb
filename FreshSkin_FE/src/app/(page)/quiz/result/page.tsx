"use client";

import { useContext } from "react";
import { SettingProfileContext } from "../../layout";
import { FaCheckCircle } from "react-icons/fa";
import Link from "next/link";

export default function QuizResultPage() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { profile } = settingProfile;

    return (
        <div className="flex items-center justify-center h-screen bg-gradient-to-br from-green-200 to-white-400 text-white">
            <div className="bg-white p-8 rounded-2xl shadow-lg text-center text-gray-800 max-w-md w-full">
                <div className="text-green-500 text-6xl flex justify-center items-center mx-auto">
                    <FaCheckCircle />
                </div>
                <h1 className="text-3xl font-bold mt-4">Chúc mừng</h1>
                <p className="text-lg mt-2">Bạn đã hoàn thành bài trắc nghiệm.</p>
                <p className="text-xl font-semibold mt-4 text-blue-500">
                    Loại da của bạn là: <span className="text-green-600">{profile.skinType}</span>
                </p>
                <button
                    className="mt-6 mb-2 bg-green-500 hover:bg-green-600 text-white py-2 px-6 rounded-lg text-lg shadow-md transition"
                >
                    <Link href="/rountine">
                        Khám phá lộ trình và sản phẩm phù hợp
                    </Link>
                </button>
                <Link
                    href="/"
                    className="mt-4 inline-block text-primary hover:text-secondary text-lg font-semibold transition duration-200 ease-in-out"
                >
                    ← Trở về trang chủ
                </Link>
            </div>
        </div>
    );
}
