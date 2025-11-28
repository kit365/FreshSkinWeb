"use client";

import { useEffect, useState, useRef } from "react";
import { StatCard } from "@/app/components/StatCard/StatCard";
import { Chart, registerables } from "chart.js";
import Link from "next/link";
Chart.register(...registerables);

import {
  ShoppingCart,
  Boxes,
  CheckCircle,
  Star,
  Banknote,
  ShoppingBag,
  User2Icon,
  FileText,
  Clock,
  CheckSquare,
} from "lucide-react";

export default function DashboardAdminPage() {
  const [stats, setStats] = useState({
    totalOrder: "0",
    totalOrderCompleted: "0",
    totalOrderPending: "0",
    totalOrderCanceled: "0",
    totalRevenue: "0",
    totalProducts: "0",
    totalUsers: "0",
    totalFeedbacks: "0",
    totalBlogs: "0",
    totalOrderShipping: "0",
  });
  interface RevenueData {
    category: string;
    date: string;
    revenue: number;
  }

  interface WebSocketData {
    revenueByCategories: {
      data: RevenueData[];
    };
  }

  const [top10ProductSelling, setTop10ProductSelling] = useState([]);
  const [chartData, setChartData] = useState<{
    labels: string[];
    values: number[];
  }>({
    labels: [],
    values: [],
  });
  const [ratingChartData, setRatingChartData] = useState<{
    labels: string[];
    values: number[];
  }>({
    labels: [],
    values: [],
  });
  const [revenueChartData, setRevenueChartData] = useState<{
    labels: string[];
    values: number[];
  }>({
    labels: [],
    values: [],
  });
  const [chartPieData, setChartPieData] = useState<{
    labels: string[];
    values: number[];
  }>({
    labels: [],
    values: [],
  });
  const [voucherChartData, setVoucherChartData] = useState<{
    labels: string[];
    values: number[];
    values1: number[];
  }>({
    labels: [],
    values: [],
    values1: [],
  });
  const StackedChartRef = useRef<HTMLCanvasElement | null>(null);
  const chartInstanceRef = useRef<Chart | null>(null);
  const [wsData, setWsData] = useState<WebSocketData | null>(null);
  const [stackedChartData, setStackedChartData] = useState<{
    labels: string[];
    datasets: any[];
  }>({
    labels: [],
    datasets: [],
  });

  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const chartRef = useRef<HTMLCanvasElement | null>(null);
  const ratingChartRef = useRef<HTMLCanvasElement | null>(null);
  const revenueChartRef = useRef<HTMLCanvasElement | null>(null);
  const voucherChartRef = useRef<HTMLCanvasElement | null>(null);
  const chartPieRef = useRef<HTMLCanvasElement | null>(null);
  const scrollToRevenueChart = () => {
    if (revenueChartRef.current) {
      const offset = 250; // Điều chỉnh độ cuộn (100px phía trên phần tử)
      const elementPosition =
        revenueChartRef.current.getBoundingClientRect().top + window.scrollY;
      window.scrollTo({
        top: elementPosition - offset,
        behavior: "smooth",
      });
    }
  };
  const scrollToRating = () => {
    if (ratingChartRef.current) {
      const offset = 300; // Điều chỉnh độ cuộn (100px phía trên phần tử)
      const elementPosition =
        ratingChartRef.current.getBoundingClientRect().top + window.scrollY;
      window.scrollTo({
        top: elementPosition - offset,
        behavior: "smooth",
      });
    }
  };
  useEffect(() => {
    function connectWebSocket() {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) return;

      wsRef.current = new WebSocket(
        "wss://freshskinweb.onrender.com/ws/dashboard"
      );

      wsRef.current.onopen = () => {
        if (reconnectTimeoutRef.current)
          clearTimeout(reconnectTimeoutRef.current);
      };

      wsRef.current.onmessage = (event) => {
        const data = JSON.parse(event.data);
        setWsData(data);
        console.log(data);

        // Cập nhật thống kê
        setStats((prevStats) => ({
          ...prevStats,
          totalOrder: data.totalOrder || prevStats.totalOrder,
          totalOrderCompleted:
            data.totalOrderCompleted || prevStats.totalOrderCompleted,
          totalOrderPending:
            data.totalOrderPending || prevStats.totalOrderPending,
          totalOrderCanceled:
            data.totalOrderCanceled || prevStats.totalOrderCanceled,
          totalRevenue: data.totalRevenue || prevStats.totalRevenue,
          totalProducts: data.totalProducts || prevStats.totalProducts,
          totalUsers: data.totalUsers || prevStats.totalUsers,
          totalFeedbacks: data.totalFeedbacks || prevStats.totalFeedbacks,
          totalBlogs: data.totalBlogs || prevStats.totalBlogs,
          totalOrderShipping:
            data.totalOrderShipping || prevStats.totalOrderShipping,
        }));

        // Cập nhật danh sách sản phẩm bán chạy
        if (Array.isArray(data.top10ProductSelling?.data)) {
          setTop10ProductSelling(
            data.top10ProductSelling.data.map((product: any) => ({
              title: product.title || "Không có tiêu đề",
              soldQuantity: product.soldQuantity || "Không có sp",
            }))
          );
        }
        //cập nhật dữ liệu voucher
        if (data?.discountDetail) {
          setVoucherChartData({
            labels: data.discountDetail.map((item: any) => item.name),
            values: data.discountDetail.map((item: any) => item.usageLimit),
            values1: data.discountDetail.map((item: any) => item.used),
          });
        }

        // Cập nhật dữ liệu cho PieChart
        if (data?.Top5CategoryHaveTopProduct?.data) {
          const categories = data.Top5CategoryHaveTopProduct.data;
          setChartData({
            labels: categories.map((item: any) => item.title),
            values: categories.map((item: any) => item.total),
          });
        }

        // cập nhật biểu đồ SkinType
        if (data?.statisticSkinTest?.data) {
          const skinType = data.statisticSkinTest.data;
          setChartPieData({
            labels: skinType.map((item: any) => item.skinType),
            values: skinType.map((item: any) => item.count),
          });
        }
        //cập nhật dữ liệu rating

        if (data?.ratingStartsByDate) {
          const sortedData = data.ratingStartsByDate.sort(
            (a: any, b: any) =>
              new Date(a.date).getTime() - new Date(b.date).getTime()
          );

          setRatingChartData({
            labels: sortedData.map((item: any) => item.date),
            values: sortedData.map(
              (item: any) => Math.round(item.avr * 10) / 10
            ),
          });
        }
        if (data?.revenueByDate) {
          setRevenueChartData({
            labels: data.revenueByDate.map((item: any) => item.orderDate),
            values: data.revenueByDate.map((item: any) => item.totalAmount),
          });
        }
      };

      wsRef.current.onclose = () => {
        wsRef.current = null;
        reconnectTimeoutRef.current = setTimeout(connectWebSocket, 5000);
      };
    }

    connectWebSocket();

    return () => {
      if (wsRef.current) wsRef.current.close();
      if (reconnectTimeoutRef.current)
        clearTimeout(reconnectTimeoutRef.current);
    };
  }, []);

  useEffect(() => {
    if (!chartRef.current || chartData.labels.length === 0) return;

    const ctx = chartRef.current.getContext("2d");
    if (!ctx) return;

    const myChart = new Chart(ctx, {
      type: "pie",
      data: {
        labels: chartData.labels,
        datasets: [
          {
            backgroundColor: [
              "#b91d47",
              "#00aba9",
              "#2b5797",
              "#e8c3b9",
              "#1e7145",
              "#c76c01",
            ],
            data: chartData.values,
          },
        ],
      },

      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: "left",
            labels: {
              padding: 5,
              boxWidth: 20,
              generateLabels: function (chart: any) {
                const data = chart.data;
                if (data.labels.length && data.datasets.length) {
                  return data.labels.map((label: any, i: any) => {
                    const value = data.datasets[0].data[i];
                    const total = data.datasets[0].data.reduce(
                      (acc: any, cur: any) => acc + cur,
                      0
                    );
                    const percentage = ((value / total) * 100).toFixed(1);

                    return {
                      text: `${percentage}% ${label}`,
                      fillStyle: data.datasets[0].backgroundColor[i],
                      hidden: chart.getDatasetMeta(0).data[i].hidden || false, // Kiểm tra trạng thái
                      datasetIndex: 0,
                      index: i,
                    };
                  });
                }
                return [];
              },
            },
            onClick: (e: any, legendItem: any, legend: any) => {
              const index = legendItem.index;
              const ci = legend.chart;
              const meta = ci.getDatasetMeta(0);

              // Toggle trạng thái hiển thị của dataset
              meta.data[index].hidden = !meta.data[index].hidden;

              // **🔹 Quan trọng: Cập nhật lại trạng thái hidden của legend**
              ci.options.plugins.legend.labels.generateLabels(ci);

              ci.update(); // Cập nhật lại biểu đồ sau khi thay đổi trạng thái
            },
          },
          title: {
            display: true,
            position: "top",
            text: "Top 5 danh mục có nhiều sản phẩm nhất",
            font: { size: 16, weight: "bold" },
            color: "#333",
          },
        },
      },
    });

    return () => myChart.destroy();
  }, [chartData]);
  useEffect(() => {
    if (!ratingChartRef.current || ratingChartData.labels.length === 0) return;

    const ctx = ratingChartRef.current.getContext("2d");
    if (!ctx) return;

    const existingChart = Chart.getChart(ctx);
    if (existingChart) existingChart.destroy();

    const gradient = ctx.createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, "rgba(0, 123, 255, 0.4)");
    gradient.addColorStop(1, "rgba(0, 123, 255, 0)");

    new Chart(ctx, {
      type: "line",
      data: {
        labels: ratingChartData.labels,
        datasets: [
          {
            label: "Đánh giá trung bình",
            data: ratingChartData.values,
            borderColor: "#007bff",
            backgroundColor: gradient,
            pointBackgroundColor: "#007bff",
            pointBorderColor: "#fff",
            pointRadius: 4,
            pointHoverRadius: 8,
            fill: true,
            tension: 0.3, // Đường cong vừa phải
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: "Xu hướng Rating trung bình theo ngày",
            font: { size: 16, weight: "bold" },
            color: "#333",
            padding: 4,
          },
          legend: { display: false },
        },
        scales: {
          x: {
            title: { display: true, text: "Ngày", font: { size: 16 } },
            ticks: { color: "#666" },
          },
          y: {
            title: { display: true, text: "Rating", font: { size: 16 } },
            min: 1,
            max: 5.5,
            ticks: { stepSize: 0.5, color: "#666" },
          },
        },
      },
    });
  }, [ratingChartData]);
  useEffect(() => {
    const warmColors = [
      "#b91d47",
      "#c76c01",
      "#c7d110",
      "#588e28",
      "#288e6c",
      "#28528e",
      "#8f409c",
      "#ff6c82",
      "#5e1e0d",
      "#a086a5",
      "#183e21",
    ];
    const categoryColors: { [key: string]: string } = {};

    const getWarmColor = (category: string, index: number) => {
      if (!categoryColors[category]) {
        categoryColors[category] = warmColors[index % warmColors.length];
      }
      return categoryColors[category];
    };
    if (!wsData || !Array.isArray(wsData.revenueByCategories?.data)) return;

    const revenueData = wsData.revenueByCategories.data;

    // Nhóm dữ liệu theo ngày
    const groupedData: { [date: string]: { [category: string]: number } } = {};
    revenueData.forEach(({ date, category, revenue }) => {
      if (!groupedData[date]) groupedData[date] = {};
      groupedData[date][category] = revenue;
    });

    const labels = Object.keys(groupedData);
    const categories = [...new Set(revenueData.map((item) => item.category))];

    const datasets = categories.map((category, index) => ({
      label: category,
      data: labels.map((date) => groupedData[date][category] || 0),
      backgroundColor: getWarmColor(category, index),
    }));

    setStackedChartData({ labels, datasets });
  }, [wsData]);

  useEffect(() => {
    if (!StackedChartRef.current || stackedChartData.labels.length === 0)
      return; 
    const last7Labels = stackedChartData.labels.slice(-7);
    const trimmedDatasets = stackedChartData.datasets.map((dataset) => ({
      ...dataset,
      data: dataset.data.slice(-7),
    }));  
    if (chartInstanceRef.current) {
      chartInstanceRef.current.destroy();
    }
    const ctx = StackedChartRef.current.getContext("2d");
    if (!ctx) return;

    chartInstanceRef.current = new Chart(ctx, {
      type: "bar",
      data: {
        labels: last7Labels,
        datasets: trimmedDatasets,
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: "top" },
          title: {
            display: true,
            text: "So sánh doanh thu các danh mục",
            font: { size: 16, weight: "bold" },
            color: "#333",
            padding: 4,
          },
        },
        scales: { x: { stacked: true }, y: { stacked: true } },
      },
    });
    return () => {
      if (chartInstanceRef.current) {
        chartInstanceRef.current.destroy();
      }
    };
  }, [stackedChartData]);
  useEffect(() => {
    if (!revenueChartRef.current || revenueChartData.labels.length === 0)
      return;

    const ctx = revenueChartRef.current.getContext("2d");
    if (!ctx) return;

    const existingChart = Chart.getChart(ctx);
    if (existingChart) existingChart.destroy();

    new Chart(ctx, {
      type: "bar",
      data: {
        labels: revenueChartData.labels,
        datasets: [
          {
            label: "Doanh thu (VNĐ)",
            data: revenueChartData.values,
            backgroundColor: "rgba(12, 64, 228, 0.8)",
            borderColor: "rgba(12, 64, 228, 0.8)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: "Doanh thu theo ngày",
            font: { size: 16, weight: "bold" },
            color: "#333",
            padding: 4,
          },
        },
        scales: {
          y: { beginAtZero: true },
        },
      },
    });
  }, [revenueChartData]);
  useEffect(() => {
    if (!voucherChartRef.current || voucherChartData.labels.length === 0)
      return;

    const ctx = voucherChartRef.current.getContext("2d");
    if (!ctx) return;

    const existingChart = Chart.getChart(ctx);
    if (existingChart) existingChart.destroy();

    new Chart(ctx, {
      type: "bar",
      data: {
        labels: voucherChartData.labels,
        datasets: [
          {
            label: "Số lượt",
            data: voucherChartData.values,
            backgroundColor: "rgba(12, 64, 228, 0.8)",
            borderColor: "rgba(12, 64, 228, 0.8)",
            borderWidth: 1,
          },
          {
            label: "Đã sử dụng",
            data: voucherChartData.values1,
            backgroundColor: "rgba(255, 0, 0,0.6)",
            borderColor: "rgba(255, 0, 0, 0.6)",
            borderWidth: 1,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: "Thống kê Voucher",
            font: { size: 16, weight: "bold" },
            color: "#333",
            padding: 4,
          },
        },
        scales: {
          y: { beginAtZero: true },
        },
      },
    });
  }, [voucherChartData]);

  useEffect(() => {
    if (!chartPieRef.current || chartData.labels.length === 0) return;

    const ctx = chartPieRef.current.getContext("2d");
    if (!ctx) return;

    const myChart = new Chart(ctx, {
      type: "pie",
      data: {
        labels: chartPieData.labels,
        datasets: [
          {
            backgroundColor: [
              "#b91d47",
              "#00aba9",
              "#2b5797",
              "#e8c3b9",
              "#1e7145",
            ],
            data: chartPieData.values,
          },
        ],
      },

      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: "left",
            labels: {
              padding: 5,
              boxWidth: 20,
              generateLabels: function (chart: any) {
                const data = chart.data;
                if (data.labels.length && data.datasets.length) {
                  return data.labels.map((label: any, i: any) => {
                    const value = data.datasets[0].data[i];
                    const total = data.datasets[0].data.reduce(
                      (acc: any, cur: any) => acc + cur,
                      0
                    );
                    const percentage = ((value / total) * 100).toFixed(1);

                    return {
                      text: `${percentage}% ${label}`,
                      fillStyle: data.datasets[0].backgroundColor[i],
                      hidden: chart.getDatasetMeta(0).data[i].hidden || false, // Kiểm tra trạng thái
                      datasetIndex: 0,
                      index: i,
                    };
                  });
                }
                return [];
              },
            },
            onClick: (e: any, legendItem: any, legend: any) => {
              const index = legendItem.index;
              const ci = legend.chart;
              const meta = ci.getDatasetMeta(0);

              // Toggle trạng thái hiển thị của dataset
              meta.data[index].hidden = !meta.data[index].hidden;

              ci.options.plugins.legend.labels.generateLabels(ci);

              ci.update();
            },
          },
          title: {
            display: true,
            position: "top",
            text: "Biểu đồ phân loại làm quiz",
            font: { size: 16, weight: "bold" },
            color: "#333",
          },
        },
      },
    });

    return () => myChart.destroy();
  }, [chartPieData]);

  return (
    <div className="p-4 md:p-6 bg-gray-100 max-w-screen-xl mx-auto">
      {stats.totalOrder === "0" && stats.totalUsers === "0" ? (
        <p className="text-lg font-semibold text-gray-500">
          Đang tải dữ liệu...
        </p>
      ) : (
        <>
          <h1 className="text-2xl mb-4">Dashboard</h1>
          {/* Thống kê dạng thẻ */}
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3 mb-5 font-bold text-[#374785]">
            <Link href="/admin/products" className="block">
              <StatCard
                value={stats.totalProducts}
                label="Số sản phẩm"
                icon={<Boxes className="text-indigo-500" />}
              />
            </Link>
            <StatCard
              value={stats.totalRevenue}
              label="D.số "
              icon={<Banknote className="text-yellow-500" />}
              onClick={scrollToRevenueChart}
              style={{ cursor: "pointer" }}
            />
            <Link href="http://localhost:3000/admin/users" className="block">
              <StatCard
                value={stats.totalUsers}
                label="Tổng người dùng"
                icon={<User2Icon className="text-indigo-500" />}
              />
            </Link>
            <StatCard
              value={stats.totalFeedbacks}
              label="Đánh giá"
              icon={<Star className="text-purple-500" />}
              onClick={scrollToRating}
              style={{ cursor: "pointer" }}
            />
            <Link href="/admin/blogs" className="block">
              <StatCard
                value={stats.totalBlogs}
                label="Bài viết"
                icon={<FileText className="text-gray-500" />}
              />
            </Link>
            <Link href="/admin/orders" className="block">
              <StatCard
                value={stats.totalOrder}
                label="Tổng đơn hàng"
                icon={<ShoppingCart className="text-green-500" />}
              />
            </Link>
            <Link href="/admin/orders?status=PENDING" className="block">
              <StatCard
                value={stats.totalOrderPending}
                label="Đơn chờ duyệt"
                icon={<Clock className="text-orange-500" />}
              />
            </Link>
            <Link href="/admin/orders?status=DELIVERING" className="block">
              <StatCard
                value={stats.totalOrderShipping}
                label="Đơn đã duyệt"
                icon={<CheckSquare className="text-orange-500" />}
              />
            </Link>
            <Link href="/admin/orders?page=1&status=CANCELED" className="block">
              <StatCard
                value={stats.totalOrderCanceled}
                label="Đơn đã hủy"
                icon={<ShoppingBag className="text-red-500" />}
              />
            </Link>
            <Link href="/admin/orders?status=COMPLETED" className="block">
              <StatCard
                value={stats.totalOrderCompleted}
                label="Hoàn thành"
                icon={<CheckCircle className="text-blue-500" />}
              />
            </Link>
          </div>

          {/* Biểu đồ & Bảng sản phẩm bán chạy */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white shadow-md rounded-lg p-4 h-80 flex items-center justify-center w-full">
              <canvas ref={chartRef} />
            </div>
            <div className="bg-white shadow-md rounded-lg p-4 h-80 flex items-center justify-center w-full">
              <canvas ref={StackedChartRef} />
            </div>
          </div>
          <div className="bg-white shadow-md rounded-lg p-4 mt-4 h-80">
            <canvas ref={revenueChartRef} />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white shadow-md rounded-lg p-4 mt-4 mb-4 h-120">
              <canvas ref={ratingChartRef} />
            </div>
            <div className="bg-white shadow-md rounded-lg p-4 mb-4 mt-4 h-120">
              <h2 className="text-base font-bold text-gray-800 text-center">
                Top 10 Sản Phẩm Bán Chạy
              </h2>
              <div className="overflow-y-auto max-h-80 scrollbar-thin scrollbar-thumb-blue-400 scrollbar-track-gray-100">
                <table className="w-full border border-gray-300 rounded-lg">
                  <thead className="sticky top-0 bg-blue-600 text-white shadow-md z-20">
                    <tr>
                      <th className="p-3 bg-blue-600 text-left">STT</th>
                      <th className="p-3  bg-blue-600 text-left">Sản phẩm</th>
                      <th className="px-3 py-2 pr-4 bg-blue-600 text-center text-white whitespace-nowrap">
                        Đã bán
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {top10ProductSelling.map((product: any, index: number) => {
                      return (
                        <tr key={index} className="border border-gray-300">
                          <td className="p-2 text-center">{index + 1}</td>
                          <td className="p-2">{product.title}</td>
                          <td className="p-7">{product.soldQuantity}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-white shadow-md rounded-lg p-4 h-80 flex items-center justify-center w-full">
              <canvas ref={chartPieRef} />
            </div>
            <div className="bg-white shadow-md rounded-lg p-4 h-80 flex items-center justify-center w-full">
              <canvas ref={voucherChartRef} />
            </div>
          </div>
        </>
      )}
    </div>
  );
}
