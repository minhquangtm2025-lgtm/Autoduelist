# AutoDuelist - AI PvP Master Mod for Minecraft Fabric 1.21.11

Tác giả: Minh Quang
Phiên bản hiện tại: 1.2.0
Phiên bản tiếp theo: 1.3.0
Nền tảng: Fabric 1.21.11
Ngôn ngữ: Java 21

## Giới thiệu

AutoDuelist là một mod AI PvP thông minh dành cho Minecraft Fabric 1.21.11. Mod này tự động điều khiển người chơi trong chiến đấu với khả năng phân tích đối thủ, đổi vũ khí chiến thuật, né đạn, tự động hồi máu và thực hiện các combo cao cấp như W-Tap và Crit Chain.

Chỉ cần nhấn một phím, AI sẽ chiến đấu thay bạn.

## Cài đặt

Yêu cầu:
- Minecraft 1.21.11 (Java Edition)
- Fabric Loader 0.19.2 trở lên
- Fabric API 0.141.4 trở lên
- Java 21 trở lên

Các bước cài đặt:
1. Tải và cài đặt Fabric Loader cho Minecraft 1.21.11
2. Tải Fabric API cho Minecraft 1.21.11
3. Đặt file AutoDuelist-1.2.0.jar vào thư mục .minecraft/mods/
4. Khởi động Minecraft với profile Fabric
5. Vào game, nhấn phím Right Shift để bật AI

Cách tự build từ source:
- Mở terminal trong thư mục dự án
- Chạy lệnh: gradlew build (Windows) hoặc ./gradlew build (Mac/Linux)
- File .jar sẽ nằm trong thư mục build/libs/

## Cách sử dụng

Điều khiển:
- Right Shift (Shift phải): Bật hoặc tắt AI PvP

Khi bật AI, bạn sẽ thấy thông báo màu xanh: [AutoDuelist] Enabled
Khi tắt AI, bạn sẽ thấy thông báo màu đỏ: [AutoDuelist] Disabled

AI sẽ tự động:
- Tìm mục tiêu gần nhất trong bán kính 32 blocks
- Phân tích vũ khí, giáp và khiên của đối thủ
- Chọn chiến thuật phù hợp
- Tấn công, né tránh, dùng khiên, đổi vũ khí
- Tự động ăn thức ăn khi máu thấp
- Tự động ném thuốc khi cần

## Hệ thống class

Mod bao gồm 15 class chính:

1. AutoDuelistAI.java - Quản lý AI chính, tìm mục tiêu, điều phối chiến đấu
2. AutoDuelistClient.java - Điểm vào mod, khởi tạo client
3. ClientEventHandler.java - Xử lý phím Right Shift và sự kiện game tick
4. KeyBindings.java - Định nghĩa phím tắt
5. CombatAnalyzer.java - Phân tích đối thủ, đánh giá mức độ nguy hiểm
6. CombatStrategy.java - Chiến thuật chiến đấu, điều khiển tấn công
7. ComboAttack.java - W-Tap, Crit Chain, hệ thống combo
8. HealingManager.java - Tự động ăn thức ăn và táo vàng
9. ShieldManager.java - Tự động dùng khiên
10. WeaponSwitcher.java - Đổi vũ khí thông minh
11. ProjectileDodger.java - Phát hiện và né đạn
12. RangedCombat.java - Chiến đấu tầm xa (cung, nỏ, đinh ba)
13. MaceCombat.java - Chiến đấu với Mace
14. WindChargeCombat.java - Ném Wind Charge
15. PotionManager.java - Tự động ném thuốc

## Chiến thuật AI

W-Tap: Nhả phím W sau mỗi đòn đánh để reset knockback, giữ đối thủ trong tầm tay.

Crit Chain: Nhảy liên tục để tạo critical hits, tăng sát thương lên 150%.

Shield Counter: Tự động giơ khiên khi đối thủ tấn công, giảm 100% sát thương vật lý.

Weapon Counter: Tự động đổi rìu khi gặp khiên để phá vỡ phòng thủ của đối thủ.

## Tùy chỉnh nâng cao

Bạn có thể sửa các thông số trong file AutoDuelistAI.java:
- DETECTION_RANGE: Bán kính tìm mục tiêu (mặc định 32)
- RETARGET_INTERVAL: Tần suất tìm lại mục tiêu (mặc định 10 tick)

Trong file CombatAnalyzer.java:
- Ngưỡng máu để chạy trốn (mặc định 5)
- Ngưỡng máu để né tránh (mặc định 10)
- Ngưỡng máu để sprint attack (mặc định 12)

Trong file HealingManager.java:
- HEAL_COOLDOWN_TICKS: Cooldown ăn (mặc định 40 tick = 2 giây)
- Ngưỡng phần trăm máu để ăn: 30%, 50%, 70%

## Xử lý lỗi

Nếu AI không bật: Hãy kiểm tra xem bạn đã nhấn Right Shift chưa. Khi bật thành công sẽ có thông báo màu xanh trong chat.

Nếu AI không tấn công: Hãy đảm bảo có monster hoặc player khác trong bán kính 32 blocks.

Nếu build bị lỗi: Hãy kiểm tra đã cài Fabric API chưa. Chạy lệnh gradlew clean build --stacktrace để xem lỗi chi tiết.

Nếu game bị crash: Kiểm tra file log trong .minecraft/logs/latest.log để biết nguyên nhân.

## Lịch sử phát triển

Phiên bản 1.0.0:
- Bật/tắt AI bằng phím Right Shift
- Tự động tìm mục tiêu
- Phân tích đối thủ cơ bản

Phiên bản 1.1.0:
- Đổi vũ khí thông minh
- Combo W-Tap và Crit Chain

Phiên bản 1.2.0 (HIỆN TẠI):
- Tự động ăn uống và hồi máu
- Tự động dùng khiên
- Né đạn
- Chiến đấu tầm xa (cung, nỏ, đinh ba)
- Chiến đấu với Mace
- Ném Wind Charge
- Tự động ném thuốc

Phiên bản 1.3.0 (TIẾP THEO):
- File cấu hình (config) cho phép người dùng tùy chỉnh
- Giao diện đồ họa (GUI) đơn giản
- Hỗ trợ thêm nhiều loại vũ khí mới
- Tối ưu hóa hiệu suất AI

Phiên bản 1.4.0 (dự kiến):
- Hỗ trợ nhiều mục tiêu cùng lúc
- Chế độ PvP team

Phiên bản 2.0.0 (dự kiến):
- AI học máy (Machine Learning)
- Tự động thích nghi với phong cách chiến đấu của đối thủ

## Giấy phép

Dự án được phân phối dưới giấy phép CC0-1.0 (Public Domain). Bạn có thể tự do sử dụng, sửa đổi và phân phối.

## Liên hệ

Tác giả: Minh Quang
Dự án: AutoDuelist - AI PvP Master Mod

## Lưu ý

Sử dụng mod này một cách có trách nhiệm, đặc biệt trên các server công cộng. Một số server có thể cấm auto-PvP mods. Hãy kiểm tra quy định của server trước khi sử dụng.

---

Made by Minh Quang for Minecraft Fabric 1.21.11
Phiên bản hiện tại: 1.2.0 | Phiên bản tiếp theo: 1.3.0