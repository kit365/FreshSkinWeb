"use client"

import Link from "next/link"

export default function Banner2() {
    const data: string[] = [
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356508/product-brand/dsa_20250319-035508_10.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356509/product-brand/dsa_20250319-035509_11.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356510/product-brand/dsa_20250319-035510_12.webp"
    ]
    return (
        <>
            <div className="container mx-auto flex justify-between mt-[40px]">
                {data.map((item: string, index: number) => (
                    <div className="w-[412px] h-[196px]" key={index}>
                        <Link href="/products">
                            <img src={item} className="w-full h-full object-cover" />
                        </Link>
                    </div>
                ))}

            </div>
        </>
    )
}