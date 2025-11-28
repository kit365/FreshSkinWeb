import ButtonCategory from "@/app/components/Button/ButtonCategory";
import Title from "@/app/components/title/Title";
import Link from "next/link";

export default function Section4(props: any) {
    const { dataInit = [] } = props;

    const bannerImage: any = [
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356504/product-brand/dsa_20250319-035504_4.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356505/product-brand/dsa_20250319-035505_5.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356506/product-brand/dsa_20250319-035505_6.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356506/product-brand/dsa_20250319-035506_7.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356507/product-brand/dsa_20250319-035507_8.webp",
        "https://res.cloudinary.com/dr53sfboy/image/upload/v1742356508/product-brand/dsa_20250319-035507_9.webp",
    ]
    return (
        <>
            <div className="container mx-auto">
                <Title title="Tìm kiếm nhiều nhất" link="/products" />
                <div className="container mx-auto flex flex-wrap justify-center">
                    {dataInit.slice(0, 14).map((item: any, index: number) => (
                        <ButtonCategory key={index} text={item.title} slug={item.slug} />
                    ))}
                </div>
                <Link href="/products" className="container mx-auto mt-[40px] grid grid-cols-6 gap-[20px]">
                    {bannerImage.map((item: string, index: number) => (
                        <div className="w-[196px] h-[98px]" key={index}>
                            <img src={item} className="w-full h-full object-cover rounded-[5px]" />
                        </div>
                    ))}
                </Link>
            </div>
        </>
    )
}