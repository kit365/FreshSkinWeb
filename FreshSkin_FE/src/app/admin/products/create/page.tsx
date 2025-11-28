"use client"
import dynamic from 'next/dynamic';
const TinyEditor = dynamic(() => import('../../../../../TinyEditor'), {
    ssr: false
});
import React, { useContext, useEffect, useState } from 'react';
import { Box, Typography, TextField, FormControl, Button, Paper, RadioGroup, FormControlLabel, Radio, FormGroup, Checkbox, InputLabel, Select, MenuItem, Divider } from '@mui/material';
import { MdDeleteForever } from 'react-icons/md';
import UploadImage from '@/app/components/Upload/UploadImage';
import SubCategory from '@/app/components/Sub-Category/SubCategory';
import { ProfileAdminContext } from '../../layout';
import Alert from '@mui/material/Alert';

interface InputField {
    price: number;
    volume: number;
    unit: string;
    stock: number;
}

export default function CreateProductAdminPage() {
    const dataProfile = useContext(ProfileAdminContext);
    const permissions = dataProfile?.permissions;
    const [alertMessage, setAlertMessage] = useState<string>("");
    const [alertSeverity, setAlertSeverity] = useState<"success" | "error" | "info" | "warning">("info");
    const [description, setDescription] = useState('');
    const [ingredients, setIngredients] = useState('');
    const [usageInstructions, setUsageInstructions] = useState('');
    const [listCategory, setListCategory] = useState([]);
    const [brandCurrent, setBrandCurrent] = useState('');
    const [listBrand, setListBrand] = useState([]);
    const [listSkinType, setListSkinType] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchCategories = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/products/category/show');
            const data = await response.json();
            setListCategory(data.data);
        };
        const fetchBrands = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/products/brand/show');
            const data = await response.json();
            setListBrand(data.data);
        };

        const fetchSkintypes = async () => {
            const response = await fetch('https://freshskinweb.onrender.com/admin/skintypes/show');
            const data = await response.json();
            setListSkinType(data.data);
        }

        fetchCategories();
        fetchBrands();
        fetchSkintypes();
    }, []);

    const [inputCheckedCategory, setInputCheckedCategory] = useState<number[]>([]);

    const handleCheckedChange = (checkedIds: number[]) => {
        setInputCheckedCategory(prev => {
            const newCheckedIds = Array.from(new Set([...prev, ...checkedIds]));
            return newCheckedIds;
        });
    };

    const [images, setImages] = useState<File[]>([]);
    const handleImageChange = (newImages: File[]) => {
        setImages(newImages);
    };

    const handleChangeBrand = (event: any) => {
        setBrandCurrent(event.target.value);
    };

    const handleSubmit = async (event: any) => {
        event.preventDefault();
        setLoading(true);

        const title = event.target.title.value;
        const discountPercent = parseInt(event.target.discount.value);
        const position = event.target.position.value;
        const origin = event.target.origin.value;
        const benefits = event.target.benefits.value;
        const skinIssues = event.target.skinIssues.value;
        const featured = event.target.featured.value === "true";
        const status = event.target.status.value;

        if (!title) {
            setAlertMessage("Tên sản phẩm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (title.length <= 10) {
            setAlertMessage("Tên sản phẩm phải trên 10 ký tự.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false); 
            return;
        }

        if (inputCheckedCategory.length === 0) {
            setAlertMessage("Danh mục sản phẩm không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!brandCurrent) {
            setAlertMessage("Thương hiệu không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (images.length < 5) {
            setAlertMessage("Phải chọn tối thiểu 5 ảnh cho sản phẩm.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!description) {
            setAlertMessage("Mô tả không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        for (const input of inputs) {
            if (input.volume <= 0) {
                setAlertMessage("Dung tích / Số lượng không được nhỏ hơn hoặc bằng 0ml/g.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
                setLoading(false);
                return;
            }
            if (input.volume > 0 && input.price <= 0) {
                setAlertMessage("Giá không được để trống.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
                setLoading(false);
                return;
            }
            if (input.volume > 10000) {
                setAlertMessage("Dung tích không được lớn hơn 10.000ml/g.");
                setAlertSeverity("error");
                setTimeout(() => setAlertMessage(""), 5000);
                setLoading(false);
                return;
            }
        }

        if (!discountPercent) {
            setAlertMessage("Phần trăm giảm giá không được để trống.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (discountPercent >= 100) {
            setAlertMessage("Phần trăm giảm giá không được vượt quá 100%.");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (checkedSkinType.length === 0) {
            setAlertMessage("Sản phẩm dành cho loại da không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!benefits) {
            setAlertMessage("Lợi ích sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!ingredients) {
            setAlertMessage("Thành phần sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!usageInstructions) {
            setAlertMessage("Hướng dẫn sử dụng sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!origin) {
            setAlertMessage("Xuất sứ sản phẩm không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        if (!skinIssues) {
            setAlertMessage("Vấn đề da không được để trống");
            setAlertSeverity("error");
            setTimeout(() => setAlertMessage(""), 5000);
            setLoading(false);
            return;
        }

        const formData = new FormData();

        const request = {
            categoryId: inputCheckedCategory,
            brandId: brandCurrent,
            title: title,
            description: description,
            variants: inputs.map(input => ({
                price: Number(input.price),
                volume: input.volume ? Number(input.volume) : 0,
                unit: input.unit,
                stock: input.stock ? Number(input.stock) : 0
            })),
            skinTypes: checkedSkinType,
            discountPercent: discountPercent,
            position: position,
            origin: origin,
            ingredients: ingredients,
            usageInstructions: usageInstructions,
            benefits: benefits,
            skinIssues: skinIssues,
            featured: featured,
            status: status,
        };

        formData.append("request", JSON.stringify(request));

        images.forEach((image) => formData.append("thumbnail", image));

        const response = await fetch('https://freshskinweb.onrender.com/admin/products/create', {
            method: 'POST',
            body: formData,
        });

        const dataResponse = await response.json();
        if (dataResponse.code === 200) {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("success");
            setTimeout(() => location.reload(), 2000);
        } else {
            setAlertMessage(dataResponse.message);
            setAlertSeverity("error");
            setLoading(false);
            setTimeout(() => {
                setAlertMessage(""); 
            }, 2000);
        }        
    }


    // Variants
    const [inputs, setInputs] = useState<InputField[]>([
        { price: 0, volume: 0, unit: "ML", stock: 0 }
    ]);
    const handleAddInput = () => {
        setInputs([...inputs, { price: 0, volume: 0, unit: "ML", stock: 0 }]);
    };
    const handleRemoveInput = (indexRemove: number) => {
        const newInputs = inputs.filter((_, index) => index !== indexRemove);
        setInputs(newInputs);
    };

    const handleInputChange = (index: number, field: 'volume' | 'price' | 'unit' | 'stock', value: string) => {
        const newInputs = [...inputs];
        if (field === "unit") {
            newInputs[index][field] = value ? value : "ML";
        } else {
            const numericValue = parseFloat(value);
            if (field === "price" && numericValue > 100000000) {
                setAlertMessage("Giá không được vượt quá 100.000.000đ");
                setAlertSeverity("warning");
                setTimeout(() => setAlertMessage(""), 5000);
                return;
            }
            newInputs[index][field] = numericValue ? numericValue : 0;
        }
        setInputs(newInputs);
    };

    // SkinType
    const [checkedSkinType, setCheckedSkinType] = useState<number[]>([]);
    const handleChangeCheckedSkinType = (event: any) => {
        const id = parseInt(event.target.name);
        setCheckedSkinType((prev) => {
            if (prev.includes(id)) {
                return prev.filter((item) => item !== id);
            } else {
                return [...prev, id];
            }
        });
    };

    return (
        <>
            {alertMessage && (
                <Alert severity={alertSeverity} sx={{ position: "fixed", width: "600px", height: "60px", right: "5%", top: "5%", fontSize: "16px", zIndex: "999999" }}>
                    {alertMessage}
                </Alert>
            )}
            <Box sx={{ padding: 3, backgroundColor: '#F5F5F5' }}>
                <Typography variant="h5" gutterBottom>
                    Trang tạo mới sản phẩm
                </Typography>

                {permissions?.includes("products_create") && (
                    <Paper elevation={3} sx={{ padding: 3, marginBottom: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Tên sản phẩm"
                                name='title'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 3 }}
                            />
                            <div className='mb-6 sub-menu'>
                                <Typography className='border border-solid border-[#BFBFBF] rounded-[5px] p-[10px]'>Chọn danh mục sản phẩm</Typography>
                                <SubCategory items={listCategory} onCheckedChange={handleCheckedChange} />
                            </div>
                            <FormControl fullWidth variant="outlined" sx={{ marginBottom: 3 }}>
                                <InputLabel shrink={true}>-- Chọn thương hiệu --</InputLabel>
                                <Select
                                    value={brandCurrent}
                                    onChange={handleChangeBrand}
                                    label=" Chọn thương hiệu --"
                                    displayEmpty
                                >
                                    <MenuItem value="">
                                        -- Chọn thương hiệu --
                                    </MenuItem>
                                    {listBrand.map((item: any, index: number) => (
                                        <MenuItem key={index} value={item.id}>{item.title}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup defaultValue={false} name="featured" row>
                                    <FormControlLabel value={true} control={<Radio />} label="Nổi bật" />
                                    <FormControlLabel value={false} control={<Radio />} label="Không nổi bật" />
                                </RadioGroup>
                            </FormControl>
                            <UploadImage label='Chọn ảnh' id="images" name="images" onImageChange={handleImageChange} />

                            <h4>Mô tả</h4>
                            <TinyEditor value={description} onEditorChange={(content: string) => setDescription(content)} />
                            <Box sx={{ marginTop: "20px", marginBottom: "20px" }}>
                                {inputs.map((input, index: number) => (
                                    <Box key={index} sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                        <TextField
                                            label="Dung tích"
                                            type='number'
                                            variant="outlined"
                                            size="small"
                                            value={input.volume}
                                            onChange={(e) => handleInputChange(index, 'volume', e.target.value)}
                                            sx={{ mr: 1, width: "150px" }}
                                        />
                                        <Typography variant="h6">:</Typography>
                                        <TextField
                                            label="Giá"
                                            type='number'
                                            variant="outlined"
                                            size="small"
                                            value={input.price}
                                            onChange={(e) => handleInputChange(index, 'price', e.target.value)}
                                            sx={{ ml: 1, width: "150px" }}
                                        />
                                        <TextField
                                            label="Số lượng"
                                            type='number'
                                            variant="outlined"
                                            size="small"
                                            value={input.stock}
                                            onChange={(e) => handleInputChange(index, 'stock', e.target.value)}
                                            sx={{ ml: 1, width: "150px" }}
                                        />
                                        <Select
                                            value={input.unit}
                                            onChange={(e) => handleInputChange(index, "unit", e.target.value)}
                                            size="small"
                                            sx={{ ml: 1, width: "150px" }}
                                        >
                                            <MenuItem value="ML">ML</MenuItem>
                                            <MenuItem value="G">G</MenuItem>
                                        </Select>
                                        <MdDeleteForever onClick={() => handleRemoveInput(index)} className='text-red-400 text-[25px] ml-[10px] cursor-pointer' />
                                    </Box>
                                ))}
                                <Button variant="contained" onClick={handleAddInput}>
                                    Thêm
                                </Button>
                            </Box>
                            <TextField
                                label="% Giảm giá"
                                name='discount'
                                variant="outlined"
                                fullWidth
                                type="number"
                                sx={{ marginBottom: 2 }}
                            />
                            <FormGroup row>
                                {listSkinType.map((item: any, index: number) => (
                                    <FormControlLabel
                                        key={index}
                                        control={<Checkbox checked={checkedSkinType.includes(item.id)} onChange={handleChangeCheckedSkinType} name={item.id} />}
                                        label={item.type}
                                    />
                                ))}
                            </FormGroup>
                            <TextField
                                label="Xuất sứ"
                                name='origin'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 2, marginTop: 2 }}
                            />
                            <h4>Thành phần</h4>
                            <TinyEditor value={ingredients} onEditorChange={(ingredients: string) => setIngredients(ingredients)} />
                            <Divider sx={{ marginBottom: 2 }} />
                            <h4>Hướng dẫn sử dụng</h4>
                            <TinyEditor value={usageInstructions} onEditorChange={(usageInstructions: string) => setUsageInstructions(usageInstructions)} />
                            <TextField
                                label="Lợi ích"
                                name='benefits'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 2, marginTop: 2 }}
                            />
                            <TextField
                                label="Vấn đề da"
                                name='skinIssues'
                                variant="outlined"
                                fullWidth
                                sx={{ marginBottom: 2, marginTop: 2 }}
                            />
                            <TextField
                                label="Vị trí (tự động tăng)"
                                name='position'
                                variant="outlined"
                                fullWidth
                                type="number"
                                sx={{ marginBottom: 2, marginTop: 2 }}
                            />
                            <FormControl fullWidth sx={{ marginBottom: 3 }}>
                                <RadioGroup defaultValue="ACTIVE" name="status" row>
                                    <FormControlLabel value="ACTIVE" control={<Radio />} label="Hoạt động" />
                                    <FormControlLabel value="INACTIVE" control={<Radio />} label="Dừng hoạt động" />
                                </RadioGroup>
                            </FormControl>
                            <Button type='submit' variant="contained" color="primary" sx={{ width: '100%' }} disabled={loading}>
                                {loading ? "Đang tạo sản phẩm..." : "Tạo sản phẩm"}
                            </Button>
                        </form>
                    </Paper>
                )}
            </Box >
        </>
    );
}