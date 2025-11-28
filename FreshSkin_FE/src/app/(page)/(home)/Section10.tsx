import Link from "next/link"

export default function Section10() {
    const data: string[] = [
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357768/product-brand/dsa_20250319-041608_7.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357769/product-brand/dsa_20250319-041609_8.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357770/product-brand/dsa_20250319-041610_9.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357771/product-brand/dsa_20250319-041611_10.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357772/product-brand/dsa_20250319-041612_11.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357773/product-brand/dsa_20250319-041612_12.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357773/product-brand/dsa_20250319-041613_13.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742357774/product-brand/dsa_20250319-041614_14.webp"
    ]
    return (
        <>
            <Link href="/products" className="container mx-auto grid grid-cols-4 gap-[20px] mt-[60px] mb-[10px]">
                {data.map((item: string, index: number) => (
                    <div className="w-[300px] aspect-square" key={index}>
                        <img src={item} className="w-full h-full object-cover rounded-[10px]" />
                    </div>
                ))}
            </Link>
        </>
    )
}