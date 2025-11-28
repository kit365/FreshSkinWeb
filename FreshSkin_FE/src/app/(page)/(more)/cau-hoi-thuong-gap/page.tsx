"use client";

import { useContext } from "react";
import { SettingProfileContext } from "../../layout";
import Link from "next/link";
import { MdNavigateNext } from "react-icons/md";

export default function Support5() {
    const settingProfile = useContext(SettingProfileContext);

    if (!settingProfile) {
        return null;
    }

    const { setting } = settingProfile;
    return (
        <>
            <div className="container mx-auto mt-[10px]">
                <ul className="flex items-center container mx-auto mb-[30px]">
                    <li>
                        <Link href="/" className="flex items-center">
                            <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
                            <span><MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" /></span>
                        </Link>
                    </li>
                    <li className="text-secondary text-[15px] font-[400]">
                        Câu hỏi thường gặp
                    </li>
                </ul>
                <div className="uppercase mb-[15px] text-[16px] text-[#00090f] hover:text-secondary cursor-pointer font-[650]">Câu hỏi thường gặp</div>
                <div className="text-[14px]" dangerouslySetInnerHTML={{ __html: setting?.support5 || '' }} />
            </div>
        </>
    );
}