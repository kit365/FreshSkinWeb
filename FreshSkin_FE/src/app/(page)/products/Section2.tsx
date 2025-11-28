"use client";

import Aside from "@/app/components/Aside/Aside";
import CardItem from "@/app/components/Card/CardItem";
import Pagination from "@/app/components/Pagination/Pagination";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { MdNavigateNext } from "react-icons/md";

export default function Section2() {
  const { slug } = useParams();

  const [dataSkinTypes, setDataSkinTypes] = useState([]);
  const [dataProductTypes, setDataProductTypes] = useState([]);
  const [title, setTitle] = useState("");
  const [products, setProducts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [page, setPage] = useState(1);

  const linkApi = `https://freshskinweb.onrender.com/home/tat-ca-san-pham`;

  useEffect(() => {
    const fetchData = async () => {
      // Kiểm tra xem có dữ liệu cache từ sessionStorage không
      const cachedData = sessionStorage.getItem(`productsData-${slug}-${page}`);
      if (cachedData) {
        const data = JSON.parse(cachedData);
        setDataSkinTypes(data.skinTypes);
        setDataProductTypes(data.categories);
        setTitle(data.title);
        setProducts(data.products);
        setTotalPages(data.page.totalPages);
        setCurrentPage(data.page.page);
        setIsLoading(false);
        return;
      }

      const urlCurrent = new URL(location.href);
      const api = new URL(linkApi);

      // Phân trang
      const pageCurrent = urlCurrent.searchParams.get("page");
      setPage(pageCurrent ? parseInt(pageCurrent) : 1);

      if (pageCurrent) {
        api.searchParams.set("page", pageCurrent);
      } else {
        api.searchParams.delete("page");
      }
      // Hết Phân trang
      const response = await fetch(api.href);
      const data = await response.json();

      // Lưu dữ liệu vào sessionStorage
      sessionStorage.setItem(`productsData-${slug}-${page}`, JSON.stringify(data.data));

      setDataSkinTypes(data.data.skinTypes);
      setDataProductTypes(data.data.categories);
      setTitle(data.data.title);
      setProducts(data.data.products);
      setTotalPages(data.data.page.totalPages);
      setCurrentPage(data.data.page.page);
      setIsLoading(false);
    };

    fetchData();
  }, [slug, page]);

  const finalSkinTypes: string[] = [];
  const finalCategories: string[] = [];

  dataProductTypes.forEach((item: any) => finalCategories.push(item.title));
  dataSkinTypes.forEach((item: any) => finalSkinTypes.push(item.type));

  const data: any = [
    {
      title: "Loại sản phẩm",
      data: finalCategories,
    },
    {
      title: "Loại da",
      data: finalSkinTypes,
    },
  ];

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <div className="container mx-auto">
        <ul className="flex items-center mt-[17px]">
          <li>
            <Link href="/" className="flex items-center">
              <span className="text-[#333] text-[15px] font-[400] hover:text-secondary">Trang chủ</span>
              <span>
                <MdNavigateNext className="ml-[10px] text-[18px] mr-[10px]" />
              </span>
            </Link>
          </li>
          <li className="text-secondary text-[15px] font-[400]">{title}</li>
        </ul>
        <div className="uppercase text-[26px] font-[600] mt-[30px]">{title}</div>
        <div className="flex items-start">
          <Aside data={data} />
          <div className="flex-1 ml-[40px] mt-[15px]">
            <div className="font-[400] text-[14px] text-right">
              Sắp xếp: <span className="text-[14px] font-[600]">Mặc định</span>
            </div>
            <div className="grid grid-cols-4 gap-[20px] mt-[20px]">
              {products.map((item: any, index: number) => (
                <CardItem
                  key={index}
                  image={item.thumbnail}
                  brand={item.brand.title}
                  title={item.title}
                  banner={item.banner}
                  deal={item.deal}
                  link={`/detail/${item.slug}`}
                  priceByVolume={item.variants}
                  discount={item.discountPercent}
                />
              ))}
            </div>
            <Pagination totalPages={totalPages} currentPage={currentPage} />
          </div>
        </div>
      </div>
    </>
  );
}
