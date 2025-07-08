# fu-self-learning-app

Pattern gọi API:
network/APIClient:
- lớp quản lý kết nối tới server thông qua Retrofit
- cách dùng để tạo một service instance gọi API:
- AuthService authService = APIClient.getClient().create(AuthService.class);

models:
- định nghĩa các model của ứng dụng, bao gồm:
    + các model chung như UserInfo
    + các request/response model định nghĩa kiểu dữ liệu gửi đi / trả về

activities: quản lý các activity của ứng dụng
services: quản lý việc gọi API dựa theo từng feature khác nhau như auth, flashcard,...
utils: một vài hàm tiện ích như handleError nhận request bên ngoài và hiển thị lỗi (toast error) ra cho người dùng