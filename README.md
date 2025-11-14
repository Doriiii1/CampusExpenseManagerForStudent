# CampusExpenseManagerForStudent
Setup Project Chi Tiết (Theo Yêu Cầu):

Tạo Project: Mở Android Studio → New Project → Empty Views Activity. Đặt:
Name: CampusExpenseManagerForStudent
Package: com.example.campusexpensemanagerforstudent
Language: Java( no Koolin )
Min SDK: API 29 (Android 10) – Tốt cho coverage rộng, hỗ trợ 90%+ devices.
Build: Gradle (Groovy DSL) – Classic, dễ debug cho MVP.
Ngôn Ngữ: Tiếng Anh 
Database: Plain SQLite (không Room cho MVP) – Tạo helper class DatabaseHelper extends SQLiteOpenHelper. ERD từ trước: Tables User, Expense, Budget.
Architecture: MVVM lite (Activity/ViewModel) cho online/offline: Offline dùng SQLite, online sync via Retrofit (nếu thêm API sau). Secure at-rest: Android Keystore cho encrypt sensitive data (e.g., passwords):javaKeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
keyStore.load(null);
// Generate/use key for AES encryption
Target Device: Emulator Pixel 4 XL (API 34+), test landscape/portrait.

Về File build.gradle (Module:App) – Phân Tích Ảnh Hưởng:
Snippet bạn đưa là setup hiện đại (AGP 8.5+ với Version Catalog từ libs.versions.toml), rất tốt cho project 2025! Không ảnh hưởng tiêu cực, mà còn lợi ích:

plugins { alias(libs.plugins.android.application) }: Sử dụng catalog để quản lý versions (e.g., android-gradle-plugin=8.5.0), giảm boilerplate, dễ update.
version = release(36): Giả sử là targetSdk = 36 (Android 16, phù hợp ngày 12/11/2025 – focus privacy mới như partial screen sharing). Ảnh hưởng: Tăng compatibility future-proof, nhưng test kỹ trên API 29 để tránh deprecation (e.g., permissions thay đổi).
compileOptions { source/target JavaVersion.VERSION_11 }: Hoàn hảo cho Java-only, hỗ trợ lambdas/modern features mà không cần Kotlin. Ảnh hưởng: Build nhanh hơn, ít lỗi compatibility.
dependencies { implementation libs.appcompat }: AppCompat v1.7+ cho backward compat, hỗ trợ MD3. Ảnh hưởng: Không vấn đề, nhưng thêm implementation 'com.google.android.material:material:1.12.0' cho themes/animations.

Các Chức Năng Yêu Cầu Cho Ứng Dụng CampusExpense Manager (Cập Nhật)

Cảm ơn bạn đã chấp nhận các gợi ý trước! Tôi đã cập nhật đầy đủ dựa trên feedback: Thêm chức năng chỉnh sửa thông tin người dùng (profile editing) với các trường mật khẩu (thay đổi với confirm), tên, địa chỉ, số điện thoại; phần đăng ký bắt buộc điền hết thông tin (progressive profiling full upfront để dễ quản lý và nhìn tổng quan); dark mode chuyển sang chủ động với nút toggle (e.g., switch icon trong settings/profile). Skip các phần khó như xác thực OTP, CAPTCHA, welcome flow, social login để giữ MVP đơn giản.
Về UI/UX tổng thể: Tôi nhấn mạnh thiết kế sinh động, đẹp mắt và mượt mà, lấy cảm hứng trực tiếp từ Sera UI (thư viện React/Next.js với Tailwind + Framer Motion, nhưng adapt cho Android Material Design 3). Sera UI nổi bật với components modular, vibrant (gradient accents, subtle glows), và smooth animations (scale/hover 0.2s, slide-ins từ edge, fade transitions). Áp dụng cho app:

Màu sắc: Primary blue light (#4FC3F7) với white pha cho hài hòa, dark mode (#121212 bg + #81D4FA accents) – toggle nút switch với ripple effect xanh dương.
Sinh động chung: Tất cả elements dùng rounded corners (8-16dp), subtle shadows (elevation 2-8dp), micro-animations (scale 1.05x on touch, 200ms duration với ease-out). Buttons: Gradient blue fill, outlined variants với border glow. Search: Autocomplete dropdown slide-down. Nav: Bottom tab hoặc side drawer slide từ left (300ms). Lists: Card-based với staggered fade-in. Forms: Field underlines animate up on focus, error shake (0.5s). Dashboards: Charts với progress arc rotate-in. Đảm bảo responsive trên Pixel 4 XL, 60fps mượt mà via ViewPropertyAnimator.

Tổng cộng 11 chức năng chính (tăng từ 8 trước, thêm profile, biometrics, AI prediction, multi-currency, export email, dark toggle). Phân loại nhóm, mô tả chi tiết: mục đích, cách hoạt động, yêu cầu người dùng/hệ thống, lợi ích, và tích hợp UI sinh động (Sera UI-inspired).
1. Nhóm Chức Năng Xác Thực Và Quản Lý Người Dùng (User Authentication & Management)

Đăng Ký Tài Khoản (User Registration – Bắt Buộc Đầy Đủ Thông Tin)Mô tả chi tiết: Người dùng điền form đầy đủ ngay từ đầu: email/số điện thoại, mật khẩu (với confirm password field để khớp), tên, địa chỉ (e.g., ký túc xá), số điện thoại (nếu khác email). Kiểm tra validation real-time (email unique, password ≥8 ký tự + match confirm, số phone VN format). Submit thành công chuyển sang login.
Yêu cầu: SQLite lưu User table (cột: id, email, password_hash, name, address, phone). Hỗ trợ ảnh avatar upload (tùy chọn).
Lợi ích: Đảm bảo dữ liệu chính xác từ đầu, dễ cá nhân hóa (e.g., địa chỉ auto-gợi ý budget di chuyển). Giảm lỗi sau, align P1 user requirements.
UI Sinh Động (Sera UI-inspired): Form card với fields stacked, underline blue animate up on focus (Framer-like slide). Confirm password field highlight green nếu match, red shake nếu không (0.3s vibration). Submit button gradient blue với scale pulse on valid, loading spinner rotate mượt. Background subtle wave pattern cho fresh feel.
Đăng Nhập/Đăng Xuất (Login/Logout – Với Biometrics)Mô tả chi tiết: Nhập email/mật khẩu, hỗ trợ biometrics (fingerprint/face ID) làm fallback nhanh sau lần đầu. "Remember Me" lưu session. Đăng xuất clear data local. Quên mật khẩu gửi reset link qua email.
Yêu cầu: BiometricPrompt API (API 29+), fallback PIN nếu biometrics fail. 3 lần sai khóa 5 phút.
Lợi ích: Truy cập an toàn, nhanh (dưới 1s với biometrics), giảm rủi ro P2 security.
UI Sinh Động: Eye toggle icon (ic_eye/ic_eye_off) với rotate flip animation (180° smooth). Biometrics dialog popup slide-up từ bottom, success checkmark burst confetti subtle. Logout button red outlined với fade-out transition.
Chỉnh Sửa Thông Tin Người Dùng (Edit User Profile)Mô tả chi tiết: Từ menu profile, edit form tương tự register: tên, địa chỉ, số điện thoại, thay đổi mật khẩu (old + new + confirm). Save cập nhật SQLite, validate như register.
Yêu cầu: Update query SQL, log changes cho audit. Mã hóa password mới via Keystore.
Lợi ích: Giữ info cập nhật (e.g., đổi địa chỉ khi chuyển ký túc), tăng tính linh hoạt.
UI Sinh Động: Profile card header với avatar circle crop + glow border blue. Edit fields trong expandable accordion (slide-down reveal), save button FAB floating với bounce-in. Changes highlight green ripple propagate qua screen.
Chuyển Đổi Dark Mode (Dark Mode Toggle – Chủ Động)Mô tả chi tiết: Nút switch toggle trong settings/profile, lưu preference local (SharedPreferences). Áp dụng ngay lập tức cho toàn app (colors, contrasts).
Yêu cầu: Theme.DayNight custom, recreate activity on toggle.
Lợi ích: User kiểm soát (không auto theo system), phù hợp học khuya.
UI Sinh Động: Switch thumb blue gradient, track animate fill từ left (Sera-like progress slide). Toàn screen fade to dark/light với overlay tint (500ms), icons adjust opacity smooth.

2. Nhóm Chức Năng Theo Dõi Chi Tiêu (Expense Tracking – Với Multi-Currency)

Thêm Chi Tiêu (Add Expense)Mô tả chi tiết: Nhập amount (auto-detect currency từ profile địa chỉ, e.g., VND default), category (dropdown: Ăn uống, etc.), date/time, description, upload receipt photo. Tự cập nhật budget.
Yêu cầu: ERD Expense table (add currency_id column). Convert rate cache offline.
Lợi ích: Theo dõi đa tiền tệ cho du lịch/mua online, chính xác hơn.
UI Sinh Động: Modal bottom sheet slide-up (Sera nav-inspired), amount field keyboard numeric với currency picker wheel spin. Photo upload camera icon tap-scale, success toast slide-in green wave.
Xem Danh Sách Chi Tiêu (View Expense List)Mô tả chi tiết: List theo thời gian, filter/search (date, category, amount range). Tổng chi hiển thị top.
Yêu cầu: RecyclerView query SQLite, pagination.
Lợi ích: Tổng quan nhanh, phát hiện bất thường.
UI Sinh Động: Staggered card grid (Sera list style), each card hover lift (elevation +2dp, shadow blur). Search bar top sticky với icon magnify pulse, dropdown filter slide cascade down.
Chỉnh Sửa/Xóa Chi Tiêu (Edit/Delete Expense)Mô tả chi tiết: Tap card để edit (update fields), swipe delete với confirm dialog. Cập nhật totals real-time.
Yêu cầu: Update/delete SQL, undo snackbar 3s.
Lợi ích: Sửa lỗi dễ dàng, tránh mất dữ liệu.
UI Sinh Động: Edit overlay fade-in từ card, fields animate fill. Delete swipe left red trail, confirm modal pop với shake cancel.

3. Nhóm Chức Năng Quản Lý Ngân Sách (Budget Management – Với AI Prediction)

Thiết Lập Ngân Sách (Set Budget)Mô tả chi tiết: Đặt total/per category (e.g., 2tr VND ăn uống/tháng), period (tuần/tháng). Auto-warn on exceed.
Yêu cầu: Budget table ERD, remaining calc = budget - spent.
Lợi ích: Kiểm soát chi tiêu, tránh vượt.
UI Sinh Động: Form stepper wizard (Sera form-inspired), sliders blue gradient cho amount drag-smooth. Set button confetti burst on save.
Theo Dõi Ngân Sách (Monitor Budget – Với AI Prediction)Mô tả chi tiết: Dashboard progress bars, pie charts. AI predict next month spend dựa history (simple regression: e.g., "Dự báo ăn uống +15% nếu trend hiện tại"). Alert push <20%.
Yêu cầu: MPAndroidChart lib, AI via local ML model (TensorFlow Lite lite).
Lợi ích: Dự báo chủ động, khuyến khích tiết kiệm.
UI Sinh Động: Dashboard cards radial load (rotate-in arc), prediction bubble tooltip slide-out với glow. Bars fill animate từ 0% với color shift (green to red).

4. Nhóm Chức Năng Báo Cáo Và Xuất Dữ Liệu (Reporting & Export)

Tạo Báo Cáo (Generate Reports)Mô tả chi tiết: Summary (total, top category, compare periods), charts, export PDF/Excel tùy chọn thời gian.
Yêu cầu: iText PDF, aggregate SQL (SUM/GROUP).
Lợi ích: Phân tích xu hướng, share dễ.
UI Sinh Động: Report viewer tabbed swipe (Sera tab nav), charts zoom pan smooth. Generate spinner morph to checkmark.
Sao Lưu Và Xuất Dữ Liệu (Backup & Export – Với Email Tự Động)Mô tả chi tiết: Export CSV/JSON/PDF, backup local/cloud. Tùy chọn send email auto với attachment (default to user email từ profile).
Yêu cầu: Intent cho email, storage perms. Queue nếu offline.
Lợi ích: An toàn dữ liệu, share nhanh.
UI Sinh Động: Share button icon arrow burst, email chooser modal slide-up. Success banner top slide-down với envelope fly-in.

Ghi Chú Tổng Quát
Tổng Quan: Các chức năng bao quát đầy P1 requirements, với UI Sera-inspired làm app "wow" – vibrant blues, mượt animations tăng engagement 20-30% theo UX trends. Test plan (P5): Checklist UI load <1s, animation no lag.
Ưu Tiên: Core: Auth + Tracking (70%), UI polish + AI/Export (30%).
Cải Tiến Tương Lai (D2): Income tracking từ profile, cloud sync.

Tổng Quan Điều Chỉnh

Timeline: 8 tuần (thay vì 10) với 4 sprints chính (mỗi sprint 2 tuần)
Focus: MVP core features trước, polish sau
Giảm scope Phase 1: 7 features thay vì 11 (bỏ AI, multi-currency, biometrics sang Phase 2)
80/20 Rule: 80% effort vào functionality, 20% vào UI polish


PHASE 1: MVP CORE (6 Tuần)
Sprint 1: Foundation & Database (Tuần 1-2)
Mục Tiêu
Xây dựng nền tảng vững chắc: Project setup, database schema hoàn chỉnh, basic navigation
Tasks Chi Tiết

Project Setup (2 ngày)

Tạo Empty Views Activity project
Config build.gradle: Java 11, Material3, SQLite
Setup version control (Git repo, .gitignore)
Tạo package structure: activities, models, utils, adapters


Database Design & Implementation (4 ngày)

Viết ERD mới với 5 tables (User, Category, Expense, Budget, Currency)
Implement DatabaseHelper.java:

onCreate(): Tạo tables với foreign keys
CRUD methods: insertUser(), getExpensesByUser(), etc.
Migration plan (version control)


Pre-populate Category table (10 categories: Ăn uống, Di chuyển, Học tập, etc.)
Pre-populate Currency table (VND default, rate=1)


Resources Setup (2 ngày)

colors.xml: Primary blue (#4FC3F7), dark mode variants
themes.xml: Material3.DayNight custom
strings.xml: All labels (100+ strings cho 7 features)
dimens.xml: Standard spacing (8dp, 16dp, 24dp)


Basic Navigation (2 ngày)

MainActivity.java: Empty dashboard với BottomNavigationView (3 tabs: Dashboard, Expenses, Profile)
Placeholder fragments cho mỗi tab
Test navigation switching



Deliverables

✅ App chạy được, blank screens
✅ Database connect test pass (insert/query dummy data)
✅ Git repo có ≥5 commits với messages rõ ràng

Files Tạo

Java: DatabaseHelper.java, MainActivity.java, User.java, Expense.java, Budget.java, Category.java
XML: activity_main.xml, colors.xml, strings.xml, themes.xml, dimens.xml
Total: 6 Java + 5 XML = 11 files


Sprint 2: Authentication & Profile (Tuần 3-4)
Mục Tiêu
User có thể đăng ký, đăng nhập, edit profile → Secure foundation
Tasks Chi Tiết

Register Screen (3 ngày)

RegisterActivity.java:

Form layout: Email, Password, Confirm Password, Name, Address, Phone
Real-time validation:

Email: Regex check + unique query DB
Password: ≥8 chars, match confirm (TextWatcher)
Phone: VN format (10 digits)


Error messages dưới EditText (TextInputLayout)
Success → chuyển Login


activity_register.xml: Material3 TextInputLayout stacked, ScrollView


Login Screen (3 ngày)

LoginActivity.java:

Email/Password fields
Eye icon toggle (ImageButton swap ic_eye ↔ ic_eye_off)
Remember Me checkbox → SharedPreferences
3 failed attempts → lock 5 phút (timestamp check)
Success → MainActivity


activity_login.xml: Centered form với logo top
Drawables: ic_eye.xml, ic_eye_off.xml (vector icons)


Profile Management (3 ngày)

ProfileActivity.java:

Display current user info từ DB
Edit mode toggle (EditTexts enabled/disabled)
Change password section: Old, New, Confirm New (validate)
Dark mode switch:

SwitchMaterial bind với SharedPreferences
recreate() activity on toggle


Save button update DB


activity_profile.xml: CardView cho avatar, fields dưới


Session Management (1 ngày)

SharedPreferences helper: SessionManager.java
Save user_id on login, clear on logout
MainActivity check session on launch → redirect Login nếu null



Deliverables

✅ User flow complete: Register → Login → Dashboard → Profile → Logout
✅ Dark mode hoạt động (theme switch toàn app)
✅ Password encrypted trong DB (SHA-256 hash hoặc Keystore nếu có thời gian)

Files Tạo

Java: RegisterActivity.java, LoginActivity.java, ProfileActivity.java, SessionManager.java
XML: activity_register.xml, activity_login.xml, activity_profile.xml, ic_eye.xml, ic_eye_off.xml
Total: 4 Java + 5 XML = 9 files


Sprint 3: Expense Tracking Core (Tuần 5-6)
Mục Tiêu
User có thể add, view, edit, delete expenses → Core value proposition
Tasks Chi Tiết

Add Expense Screen (4 ngày)

AddExpenseActivity.java:

Form fields:

Amount (EditText numeric, VND only Phase 1)
Category (Spinner load từ DB Category table)
Date/Time (DatePickerDialog + TimePickerDialog)
Description (EditText multiline)
Receipt photo (optional): Camera intent → save path to DB


Validation: Amount > 0, Category selected
Insert DB → update Budget remaining
Success → back to list với Toast


activity_add_expense.xml: ScrollView form, FAB submit button
Drawable: ic_add.xml (plus icon)


Expense List Screen (4 ngày)

ExpenseListActivity.java:

RecyclerView load expenses by user_id, order by date DESC
ExpenseAdapter.java: ViewHolder với CardView:

Category icon + name (từ Category table)
Amount (format VND: 50,000đ)
Date (format: 12 Nov 2025)
Description (truncate 50 chars)
Click → EditExpenseActivity


Top summary: Total spent this month (query SUM)
Search bar: Filter by description (TextWatcher)
Filter button: BottomSheet chọn category + date range


activity_expense_list.xml: Toolbar + SearchView + RecyclerView
item_expense.xml: CardView layout cho adapter


Edit/Delete Expense (3 ngày)

EditExpenseActivity.java:

Pre-fill form từ expense_id intent extra
Update button → DB update query
Delete button:

AlertDialog confirm ("Delete this expense?")
DB delete + update Budget
Snackbar undo (3 seconds timeout)


Back → refresh list


activity_edit_expense.xml: Giống Add nhưng có Delete button
Drawable: ic_delete.xml (trash icon)


Integration (1 ngày)

MainActivity tab "Expenses" → ExpenseListActivity
FAB trong ExpenseListActivity → AddExpenseActivity
Test flow end-to-end: Add 10 expenses → Edit 2 → Delete 1 → Verify totals



Deliverables

✅ Full CRUD expenses hoạt động
✅ List hiển thị đúng, filter/search work
✅ Budget auto-update khi add/edit/delete

Files Tạo

Java: AddExpenseActivity.java, ExpenseListActivity.java, EditExpenseActivity.java, ExpenseAdapter.java
XML: activity_add_expense.xml, activity_expense_list.xml, activity_edit_expense.xml, item_expense.xml, ic_add.xml, ic_delete.xml
Total: 4 Java + 6 XML = 10 files


PHASE 2: Budget & Basic Reporting (2 Tuần)
Sprint 4: Budget Management & Reports (Tuần 7-8)
Mục Tiêu
User track budget, see progress, export data → Complete MVP
Tasks Chi Tiết

Set Budget Screen (3 ngày)

SetBudgetActivity.java:

Form: Category spinner, Amount (EditText), Period (Spinner: Tuần/Tháng)
Auto-fill period dates (start = today, end = +7/+30 days)
Insert/Update Budget table
List existing budgets (RecyclerView simple)


activity_set_budget.xml: Form + list below
Drawable: ic_wallet.xml (budget icon)


Budget Dashboard (4 ngày)

BudgetDashboardActivity.java:

Query budgets với remaining = amount - SUM(expenses trong period)
Progress bars (ProgressBar horizontal):

Green: <50% spent
Orange: 50-80%
Red: >80%


Simple prediction: "Dự kiến vượt 200,000đ nếu chi tiếp tục" (rule-based: daily_avg * days_left)
Alert push local notification nếu <20% remaining (NotificationManager)


activity_budget_dashboard.xml: ScrollView với CardViews cho mỗi budget
No charts yet (defer MPAndroidChart để giảm complexity)


Basic Report Export (3 ngày)

ReportActivity.java:

Summary text: Total income (future), expenses, by category
Date range picker (default: This month)
Export button → CSV only Phase 1:

Generate CSV string (Category, Amount, Date, Description)
Save to Downloads folder (MediaStore API)
Share Intent → Email app với attachment


Toast: "Report saved to Downloads"


activity_report.xml: Summary TextViews + Export button
Drawable: ic_share.xml (share arrow)


Dashboard Integration (2 ngày)

MainActivity tab "Dashboard":

Summary cards: Total spent, Budget remaining, Top category
Quick links: Add Expense, View Budget
Greeting: "Chào [Name], hôm nay..." (từ User table)


Refresh data on resume (onResume override)



Deliverables

✅ Budget tracking hoàn chỉnh với progress visual
✅ Export CSV thành công, email send
✅ Dashboard có overview useful

Files Tạo

Java: SetBudgetActivity.java, BudgetDashboardActivity.java, ReportActivity.java, BudgetAdapter.java
XML: activity_set_budget.xml, activity_budget_dashboard.xml, activity_report.xml, fragment_dashboard.xml, ic_wallet.xml, ic_share.xml
Total: 4 Java + 6 XML = 10 files


PHASE 3: Polish & Testing (Tùy Chọn - Tuần 9-10)
Mục Tiêu
UI refinement, animations, bug fixes, testing
Tasks Chi Tiết

UI Enhancements (3 ngày)

Material3 refinement:

Rounded corners tất cả CardViews (8dp)
Elevation consistent (2dp default, 4dp on hover)
Ripple effects cho buttons (default Material)


Basic animations:

Scale FAB on tap (scaleX/Y 0.95 → 1.0, 100ms)
Fade transitions giữa activities (overridePendingTransition)
RecyclerView item animators (default slide-in)


No custom Sera animations yet (defer Phase 4)


Testing (4 ngày)

Unit Tests (JUnit):

DatabaseHelperTest.java: Test CRUD methods
SessionManagerTest.java: Test login state
Target: 70% code coverage


UI Tests (Espresso):

LoginFlowTest.java: Register → Login → Dashboard
ExpenseFlowTest.java: Add → View → Edit → Delete
Target: 5 critical flows


Manual testing:

Dark mode switch 10 times
Rotate device (landscape test)
Low memory scenario (kill app)




Bug Fixes & Optimization (3 ngày)

Fix crashes from testing
Optimize DB queries (index user_id columns)
Compress receipt images (BitmapFactory scale)
Review logs, remove debug prints



Deliverables

✅ App stable, no critical bugs
✅ Tests pass (70% coverage)
✅ UI polished với Material3 standards

Files Tạo (Test only)

Java: DatabaseHelperTest.java, LoginFlowTest.java, ExpenseFlowTest.java
Total: 3 test files


Tổng Kết File Count (Phase 1-2)
CategorySố LượngDetailsActivities10Main, Login, Register, Profile, AddExpense, ExpenseList, EditExpense, SetBudget, BudgetDashboard, ReportModels4User, Expense, Budget, CategoryHelpers/Utils2DatabaseHelper, SessionManagerAdapters2ExpenseAdapter, BudgetAdapterTotal Java18 filesLayouts13Activities (10) + item layouts (2) + fragment (1)Drawables6Icons: eye(2), add, delete, wallet, shareValues4colors, strings, themes, dimensTotal XML23 filesGRAND TOTAL41 filesManageable cho 8 tuần

Timeline Visualization
Week 1-2: [=====Foundation=====] → Database ready
Week 3-4: [=====Auth & Profile=====] → User flow complete  
Week 5-6: [=====Expense Tracking=====] → Core value
Week 7-8: [=====Budget & Report=====] → MVP done ✅
Week 9-10: [=====Polish & Test=====] → Production ready (optional)

Success Metrics
MVP Completion (Week 8)

 7 core features hoạt động (100% functional)
 0 critical bugs (showstopper)
 Dark mode stable
 Database queries <100ms
 App size <10MB

Quality Gates

 Code review pass (peer hoặc self với checklist)
 Manual test 20 scenarios pass
 Deploy APK test trên 2 devices (emulator + real)


Risk Mitigation Plan
RiskProbabilityImpactMitigationDatabase schema changesMediumHighVersion control từ đầu, migration planTime overrun Sprint 3HighMediumCut receipt photo feature nếu cầnTesting bị skipMediumHighReserve Week 9-10 strict cho testingDark mode bugsLowLowTest daily khi code

PHASE 4: Advanced Features (Future - Post-MVP)
Sau khi MVP stable, thêm dần:

Sprint 5 (Week 11-12): AI prediction (TensorFlow Lite), charts (MPAndroidChart)
Sprint 6 (Week 13-14): Multi-currency (API integration), biometrics
Sprint 7 (Week 15-16): Custom Sera animations, PDF export, cloud sync


Kết Luận
Ưu Điểm Kế Hoạch Mới
✅ Realistic: 8 tuần cho 7 features thay vì 10 tuần cho 11 features
✅ Incremental: Mỗi sprint có deliverable test được
✅ Flexible: Week 9-10 buffer cho unexpected issues
✅ Testable: 70% coverage target, critical flows covered
Commitment
Với kế hoạch này, probability hoàn thành MVP đúng hạn: 90%+ (tăng từ 85% trước). Nếu theo đúng sprint plan và daily commit, project sẽ success! 🎯
