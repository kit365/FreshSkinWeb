"use client";

import Banner2 from "@/app/components/Banner/Banner2";
import Banner from "../../components/Banner/Banner";
import Section11 from "./Secion11";
import Section1 from "./Section1";
import Section10 from "./Section10";
import Section2 from "./Section2";
import Section3 from "./Section3";
import Section4 from "./Section4";
import Section5 from "./Section5";
import Section7 from "./Section7";
import Section8 from "./Section8";
import Section9 from "./Section9";
import Cookies from 'js-cookie';
import { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";

export default function HomePage() {
  const [dataFeaturedBlogCategory, setDataFeaturedBlogCategory] = useState([]);
  const [dataFeaturedProductCategory, setDataFeaturedProductCategory] = useState([]);
  const [dataTop7ProductFlashSale, setDataTop7ProductFlashSale] = useState([]);
  const [dataFreshSkinSlogan, setDataFreshSkinSlogan] = useState([]);
  const [dataTop_moisturizing_products, setDataTop_moisturizing_products] = useState([]);
  const [dataBeautyTrends, setDataBeautyTrends] = useState([]);
  const [dataTop3ProductFeature, setDataTop3ProductFeature] = useState([]);
  const [allCategory, setAllCategory] = useState([]);
  const [top10ProductSeller, setTop10ProductSeller] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  useEffect(() => {
    if (token) {
      Cookies.set('tokenUser', token);
      window.location.href = "/"; 
    }
  }, [token]);

  const fetchData = async () => {
    const response = await fetch(`https://freshskinweb.onrender.com/home`);
    const data = await response.json();

    setDataFeaturedBlogCategory(data.featuredBlogCategory);
    setDataFeaturedProductCategory(data.featuredProductCategory);
    setDataTop7ProductFlashSale(data.Top7ProductFlashSale);
    setDataFreshSkinSlogan(data.FreshSkinSlogan);
    setDataTop_moisturizing_products(data.Top_moisturizing_products);
    setDataBeautyTrends(data.BeautyTrends);
    setDataTop3ProductFeature(data.Top3ProductFeature);
    setAllCategory(data.AllCategory);
    setTop10ProductSeller(data.Top10ProductSeller);
    setIsLoading(false);
  };

  useEffect(() => {
    fetchData();
  }, []);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Banner />
      <Section1 dataInit={dataFeaturedProductCategory} />
      <Section2 dataInit={dataTop7ProductFlashSale} />
      <Section3 dataInit={top10ProductSeller} />
      <Section4 dataInit={allCategory} />
      <Section5 dataInit={dataFreshSkinSlogan} />
      <Banner2 />
      <Section7 dataInit={dataTop_moisturizing_products} />
      <Section8 dataInit={dataTop3ProductFeature} />
      <Section9 dataInit={dataBeautyTrends} />
      <Section10 />
      <Section11 dataInit={dataFeaturedBlogCategory} />
    </>
  );
}
