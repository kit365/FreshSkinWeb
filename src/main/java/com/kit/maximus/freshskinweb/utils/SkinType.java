package com.kit.maximus.freshskinweb.utils;

public enum SkinType {
    DRY("Da khô", "Da của bạn thuộc loại da khô. Da khô thường thiếu độ ẩm và dầu tự nhiên, dễ bong tróc, nhăn nheo. Bạn nên sử dụng các sản phẩm dưỡng ẩm đậm đặc và tránh rửa mặt quá nhiều lần."),
    NORMAL("Da thường", "Da của bạn thuộc loại da thường. Da thường là loại da khỏe mạnh, cân bằng về độ ẩm và dầu. Bạn nên duy trì chế độ chăm sóc da đơn giản và sử dụng các sản phẩm dịu nhẹ."),
    COMBINATION("Da hỗn hợp", "Da của bạn thuộc loại da hỗn hợp. Da hỗn hợp có vùng chữ T (trán, mũi, cằm) dầu nhờn nhưng má lại khô. Bạn nên sử dụng các sản phẩm khác nhau cho từng vùng da."),
    SENSITIVE("Da nhạy cảm", "Da của bạn thuộc loại da nhạy cảm. Da nhạy cảm dễ bị kích ứng, đỏ và ngứa. Bạn nên sử dụng các sản phẩm không chứa hương liệu, không cồn và được thiết kế đặc biệt cho da nhạy cảm."),
    OILY("Da dầu", "Da của bạn thuộc loại da dầu. Da dầu thường bóng nhờn, dễ nổi mụn do tuyến bã nhờn hoạt động mạnh. Bạn nên sử dụng các sản phẩm không chứa dầu và có khả năng kiểm soát nhờn.");

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