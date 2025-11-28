"use client"

import Link from "next/link";

export default function BlogItem(props: {
    image: string,
    day: string,
    title: string,
    description: string,
    link: string
}) {
    const { image = "", day = "", title = "", description = "", link = "" } = props;

    const formattedDate = day
        ? new Intl.DateTimeFormat("vi-VN", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
          })
              .format(new Date(day))
              .replaceAll("/", "-") 
        : "";

    return (
        <div className="">
            <Link href={link} className="relative">
                <div className="w-[303px] h-[190px] overflow-hidden rounded-t-[10px]">
                    <img src={image} className="w-full h-full object-cover image-blog-home" />
                </div>
                <p className="text-white absolute bottom-[0] rounded-tr-[40px] text-[14px] font-[500] bg-[#94C83D] w-[120px] h-[32px] flex items-center justify-center">
                    {formattedDate}
                </p>
            </Link>
            <div className="bg-[#F7F7F7] w-full h-[110px] p-[10px] rounded-b-[10px]">
                <Link href={link} className="text-[16px] hover:text-primary font-[600] mb-[10px] line-clamp-2">
                    {title}
                </Link>
                <div className="text-[14px] line-clamp-2 text-[#2f2f2f]" dangerouslySetInnerHTML={{ __html: description }} />
            </div>
        </div>
    );
}
