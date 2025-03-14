package com.kit.maximus.freshskinweb.utils;

public enum SkinType {
    DRY("Da khô","Da khô – Cần dưỡng ẩm nhiều, dễ bong tróc, có thể kích ứng"),
    OILY("Da dầu","Da hỗn hợp – Dầu vùng chữ T, má khô, cần dưỡng ẩm cân bằng."),
    COMBINATION("Da hỗn hợp","Da dầu nhẹ – Dễ bị mụn đầu đen, cần kiểm soát dầu và giữ ẩm."),
    SENSITIVE("Da nhạy cảm","Da nhạy cảm - Dễ kích ứng, đỏ và ngứa, cần sản phẩm dịu nhẹ và không chứa chất gây kích ứng"),
    NORMAL("Da thường","Da thường - Cân bằng, ít nhạy cảm, ít gặp vấn đề về da"),
    ;
    String VNESEname;
    String message;

    public String getVNESEname() {
        return VNESEname;
    }

    public void setVNESEname(String VNESEname) {
        this.VNESEname = VNESEname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    SkinType(String VNESEname, String message) {
        this.VNESEname = VNESEname;
        this.message = message;
    }

    @Override
    public String toString() {
        return "SkinType{" +
                "VNESEname='" + VNESEname + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
