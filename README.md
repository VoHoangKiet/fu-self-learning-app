# FU Self Learning App 📚

> **Ứng dụng học tập trực tuyến với giao diện giống Udemy, tích hợp API thực tế và video player**

## 📖 Tổng quan

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

FU Self Learning App là một ứng dụng Android học tập trực tuyến được phát triển với:
- **UI/UX Design**: Thiết kế theo phong cách Udemy với màu sắc chuyên nghiệp
- **API Integration**: Tích hợp với backend thực tế thông qua REST API
- **Video Streaming**: Hỗ trợ phát video intro từ Cloudinary
- **Authentication**: Hệ thống đăng nhập/đăng ký với JWT token
- **Modern Architecture**: Sử dụng Retrofit, Picasso, RecyclerView

## 🚀 Tính năng chính

### ✅ Đã hoàn thành

#### 🔐 **Authentication System**
- **Login/Register**: Giao diện đăng nhập và đăng ký
- **JWT Token**: Tự động inject Bearer token vào API calls
- **Session Management**: Lưu trữ token trong SharedPreferences

#### 📋 **Course Listing**
- **Danh sách khóa học**: Hiển thị courses từ API thực tế
- **Search & Filter**: Tìm kiếm theo tên, instructor, category
- **Udemy-style UI**: Cards với thumbnail, rating, price
- **Instructor Avatar**: Hiển thị avatar instructor từ API
- **Loading States**: Loading indicator và error handling

#### 🔍 **Course Detail**
- **Chi tiết khóa học**: Thông tin đầy đủ từ API
- **Video Intro**: Play video từ Cloudinary URL
- **Course Info**: Title, description, instructor, categories
- **Topics Curriculum**: Danh sách bài học với RecyclerView
- **Interactive Elements**: Share, enroll, topic click

#### 🎬 **Video Player**
- **Dedicated Player**: Activity riêng cho video playback
- **Fullscreen Support**: Landscape orientation và custom controls
- **Multiple Options**: In-app player hoặc external player
- **WebView Integration**: HTML5 video với JavaScript support

### 🛠 **Technical Stack**

#### **Frontend (Android)**
```
- Language: Java
- Min SDK: 24 (Android 7.0)
- Target SDK: 34
- UI: Material Design Components
- Image Loading: Picasso
- HTTP Client: Retrofit + OkHttp
- JSON Parsing: Gson
```

#### **Backend Integration**
```
- API Base URL: https://fu-self-learning-api-22235821035.asia-southeast1.run.app
- Authentication: Bearer Token (JWT)
- Image Storage: Cloudinary
- Video Storage: Cloudinary
```

## 📁 Project Structure

```
app/src/main/java/com/example/fu_self_learning_app/
├── activities/          # UI Activities
│   ├── auth/           # Login/Register activities
│   │   ├── LoginActivity.java
│   │   └── RegisterActivity.java
│   ├── MainActivity.java
│   ├── CoursesActivity.java
│   ├── CourseDetailActivity.java
│   └── VideoPlayerActivity.java
├── adapters/           # RecyclerView Adapters
│   ├── CourseAdapter.java
│   └── TopicAdapter.java
├── models/             # Data Models
│   ├── Course.java
│   ├── Instructor.java
│   ├── Category.java
│   ├── Topic.java
│   ├── UserInfo.java
│   ├── request/        # API Request models
│   └── response/       # API Response models
├── network/            # Network layer
│   ├── APIClient.java
│   └── AuthInterceptor.java
├── services/           # API Service interfaces
│   ├── AuthService.java
│   └── CourseService.java
└── utils/              # Utility classes
    └── APIErrorUtils.java

app/src/main/res/
├── layout/             # XML Layouts
│   ├── activity_*.xml
│   ├── item_*.xml
│   └── auth_*.xml
├── values/             # Resources
│   ├── colors.xml      # Udemy color scheme
│   ├── strings.xml
│   └── themes.xml
└── drawable/           # Background drawables
    ├── chip_*.xml
    └── button_*.xml
```

## 🎨 UI/UX Design

### **Color Scheme (Udemy-inspired)**
```xml
<!-- Primary Colors -->
<color name="udemy_purple">#A435F0</color>
<color name="udemy_dark_purple">#8710D8</color>

<!-- Text Colors -->
<color name="udemy_text_primary">#1C1D1F</color>
<color name="udemy_text_secondary">#6A6F73</color>
<color name="udemy_text_muted">#9DA3A7</color>

<!-- Accent Colors -->
<color name="udemy_rating">#F3CA52</color>
<color name="udemy_success">#5CB85C</color>
```

### **Key UI Components**

#### **Course Cards**
- Large thumbnail với duration overlay
- Instructor avatar (24dp circular)
- Star rating và enrollment count
- Vietnamese price formatting
- Bestseller badges

#### **Course Detail Screen**
- Hero image header với play button overlay
- Course stats (rating, duration, lessons)
- Category chips
- Instructor section với avatar
- Topics curriculum với numbered list
- Price và enroll button

#### **Video Player**
- Fullscreen WebView với custom controls
- Progress indicator
- Orientation change support
- Back button handling

## 🔌 API Integration

### **Authentication Flow**
```java
// 1. User login
LoginRequest request = new LoginRequest(email, password);
authService.login(request).enqueue(callback);

// 2. Store token
SharedPreferences.Editor editor = prefs.edit();
editor.putString("access_token", token);
editor.apply();

// 3. Auto-inject token (AuthInterceptor)
@Override
public Response intercept(Chain chain) throws IOException {
    Request original = chain.request();
    String token = getTokenFromPrefs();
    
    Request.Builder requestBuilder = original.newBuilder()
        .header("Authorization", "Bearer " + token);
    
    return chain.proceed(requestBuilder.build());
}
```

### **API Endpoints**

#### **Authentication**
```
POST /api/v1/auth/login
POST /api/v1/auth/register
```

#### **Courses**
```
GET /api/v1/courses              # Danh sách courses
GET /api/v1/courses/{id}         # Chi tiết course
GET /api/v1/courses/search       # Tìm kiếm
GET /api/v1/courses/category/{category}  # Filter theo category
```

### **Data Models**

#### **Course Response**
```java
public class Course {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String price;
    private Instructor instructor;
    private List<Category> categories;
    // ... getters/setters
}
```

#### **Course Detail Response**
```java
public class CourseDetailResponse {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String videoIntroUrl;    // Cloudinary video URL
    private int totalDuration;
    private int totalLessons;
    private String price;
    private String createdAt;
    private Instructor instructor;
    private List<Category> categories;
    private List<Topic> topics;      // Course curriculum
}
```

## 🎬 Video Integration

### **Video Player Implementation**

#### **1. VideoPlayerActivity**
```java
// Dedicated activity cho video playback
public class VideoPlayerActivity extends AppCompatActivity {
    private WebView webView;
    private FrameLayout customViewContainer;
    
    // Setup WebView với video support
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        // ...
    }
}
```

#### **2. HTML5 Video Player**
```java
private String createVideoHtml(String videoUrl) {
    return "<!DOCTYPE html>" +
        "<html>" +
        "<head>" +
        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
        "<style>video { width: 100vw; height: 100vh; object-fit: contain; }</style>" +
        "</head>" +
        "<body>" +
        "<video controls autoplay playsinline>" +
        "<source src='" + videoUrl + "' type='video/mp4'>" +
        "</video>" +
        "</body>" +
        "</html>";
}
```

#### **3. Fullscreen Support**
```java
webView.setWebChromeClient(new WebChromeClient() {
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        customView = view;
        customViewContainer.addView(customView);
        customViewContainer.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }
});
```

## 📱 Setup và Installation

### **Prerequisites**
```
- Android Studio Arctic Fox hoặc mới hơn
- Android SDK 24+ (Android 7.0+)
- JDK 8 hoặc mới hơn
- Internet connection cho API calls
```

### **Installation Steps**

#### **1. Clone Repository**
```bash
git clone <repository-url>
cd FU_SELF_LEARNING_APP
```

#### **2. Open trong Android Studio**
```
File > Open > Select project folder
Sync project với Gradle files
```

#### **3. Configure API**
```java
// Trong APIClient.java, update base URL nếu cần
private static final String BASE_URL = "https://fu-self-learning-api-22235821035.asia-southeast1.run.app/api/";

// Trong CoursesActivity.java, thêm Bearer token thực tế
editor.putString("access_token", "your-actual-bearer-token-here");
```

#### **4. Build và Run**
```bash
./gradlew assembleDebug
# Hoặc click Run button trong Android Studio
```

### **Testing với Mock Data**

App sử dụng mock data cho các fields không có trong API:
```java
private void addMockUIData(Course course) {
    // Mock price, duration, rating, enrolled count
    double[] prices = {0, 299000, 499000, 699000};
    course.setPrice(prices[id % prices.length]);
    
    // Mock instructor avatars từ pravatar.cc
    String[] avatarUrls = {
        "https://i.pravatar.cc/150?img=1",
        "https://i.pravatar.cc/150?img=2"
    };
}
```

## 🔧 Development Patterns

### **1. Repository Pattern**
```java
// APIClient - Singleton retrofit instance
public class APIClient {
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .build();
                
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
}
```

### **2. Adapter Pattern**
```java
// CourseAdapter với ViewHolder pattern
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    
    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }
    
    public class CourseViewHolder extends RecyclerView.ViewHolder {
        // Bind data và setup click listeners
        public void bind(Course course) {
            // Load images với Picasso
            Picasso.get()
                .load(course.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(imageView);
        }
    }
}
```

### **3. Intent Factory Pattern**
```java
// Static factory methods cho navigation
public static Intent createIntent(Context context, int courseId) {
    Intent intent = new Intent(context, CourseDetailActivity.class);
    intent.putExtra(EXTRA_COURSE_ID, courseId);
    return intent;
}
```

## 🐛 Common Issues & Solutions

### **1. API Token Issues**
```java
// Problem: 401 Unauthorized
// Solution: Update token trong CoursesActivity.addMockTokenForTesting()
editor.putString("access_token", "actual-jwt-token-from-login");
```

### **2. Video Playback Issues**
```java
// Problem: Video không play
// Solution: Check WebView settings
webSettings.setMediaPlaybackRequiresUserGesture(false);
webSettings.setDomStorageEnabled(true);
```

### **3. Image Loading Issues**
```java
// Problem: Images không load
// Solution: Check network permissions và Picasso config
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### **4. Deprecated API Warnings**
```java
// Problem: getResources().getColor() deprecated
// Solution: Sử dụng ContextCompat
ContextCompat.getColor(context, R.color.udemy_purple);
```

## 🚀 Future Enhancements

### **Phase 1: Core Features**
- [ ] User profile management
- [ ] Course enrollment system
- [ ] Progress tracking
- [ ] Offline course download

### **Phase 2: Advanced Features**
- [ ] Push notifications
- [ ] Social features (reviews, ratings)
- [ ] Payment integration
- [ ] Course creation tools

### **Phase 3: Performance**
- [ ] Database caching (Room)
- [ ] Image optimization
- [ ] Background sync
- [ ] Analytics integration

## 📚 Learning Resources

### **Android Development**
- [Android Developer Guides](https://developer.android.com/guide)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Picasso Image Loading](https://square.github.io/picasso/)

### **API Integration**
- [RESTful API Best Practices](https://restfulapi.net/)
- [JWT Token Authentication](https://jwt.io/)
- [OkHttp Interceptors](https://square.github.io/okhttp/interceptors/)

### **UI/UX Design**
- [Material Design Guidelines](https://material.io/design)
- [Android UI Patterns](https://www.androiduipatterns.com/)
- [Color Theory for Apps](https://material.io/design/color/)

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Developer**: Vo Hoang Kiet
- **Project**: FU Self Learning App
- **Version**: 1.0.0
- **Last Updated**: 2025

---

> **Ghi chú**: Project này được phát triển cho mục đích học tập và demo. API endpoints và credentials chỉ dùng cho testing.

## 📞 Support

Nếu gặp vấn đề hoặc có câu hỏi:
1. Check Common Issues section
2. Review API documentation
3. Create issue trên GitHub repository
4. Contact team developer

**Happy Coding! 🎉**