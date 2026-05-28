# AutoDuelist - AI PvP Master Mod for Minecraft Fabric 1.21.11

Tác giả: Minh Quang
Phiên bản hiện tại: 1.3.0
Phiên bản tiếp theo: 1.4.0
Nền tảng: Fabric 1.21.11
Ngôn ngữ: Java 21

## Giới thiệu

AutoDuelist là một mod AI PvP thông minh dành cho Minecraft Fabric 1.21.11. Mod này tự động điều khiển người chơi trong chiến đấu với khả năng phân tích đối thủ, đổi vũ khí chiến thuật, né đạn, tự động hồi máu, mặc giáp, phân tích và kết hợp vũ khí trong inventory, và thực hiện các combo cao cấp như W-Tap và Crit Chain.

Chỉ cần nhấn một phím, AI sẽ chiến đấu thay bạn với 10+ loại vũ khí khác nhau.

## Cài đặt

Yêu cầu:
- Minecraft 1.21.11 (Java Edition)
- Fabric Loader 0.19.2 trở lên
- Fabric API 0.141.4 trở lên
- Java 21 trở lên

Các bước cài đặt:
1. Tải và cài đặt Fabric Loader cho Minecraft 1.21.11
2. Tải Fabric API cho Minecraft 1.21.11
3. Đặt file AutoDuelist-1.3.0.jar vào thư mục .minecraft/mods/
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
- Phân tích toàn bộ inventory và sắp xếp vũ khí vào hotbar
- Tự động mặc giáp tốt nhất
- Chọn vũ khí phù hợp theo khoảng cách và tình huống
- Tấn công, né tránh, dùng khiên, đổi vũ khí
- Tự động ăn thức ăn khi máu thấp
- Tự động ném thuốc khi cần
- Chiến đấu tầm xa với bow/crossbow/trident
- Sử dụng mace, wind charge và các vũ khí đặc biệt

## Hệ thống class

Mod bao gồm 17 class chính:

1. AutoDuelistAI.java - Quản lý AI chính, tìm mục tiêu, điều phối chiến đấu
2. AutoDuelistClient.java - Điểm vào mod, khởi tạo client
3. ClientEventHandler.java - Xử lý phím Right Shift và sự kiện game tick
4. KeyBindings.java - Định nghĩa phím tắt
5. CombatAnalyzer.java - Phân tích đối thủ, đánh giá mức độ nguy hiểm
6. CombatStrategy.java - Chiến thuật chiến đấu, kết hợp vũ khí thông minh
7. ComboAttack.java - W-Tap, Crit Chain, hệ thống combo
8. HealingManager.java - Tự động ăn thức ăn và táo vàng
9. ShieldManager.java - Tự động dùng khiên
10. WeaponSwitcher.java - Đổi vũ khí thông minh, sắp xếp hotbar tự động
11. ProjectileDodger.java - Phát hiện và né đạn
12. RangedCombat.java - Chiến đấu tầm xa (cung, nỏ, đinh ba)
13. MaceCombat.java - Chiến đấu với Mace
14. WindChargeCombat.java - Ném Wind Charge
15. PotionManager.java - Tự động ném thuốc
16. ArmorManager.java - Tự động mặc giáp tốt nhất
17. InventoryAnalyzer.java - Phân tích và xếp hạng vũ khí trong inventory

## Vũ khí được hỗ trợ

### Cận chiến:
1. Sword (Kiếm) - Tốc độ cao, tấn công nhanh
2. Axe (Rìu) - Phá shield, sát thương cao
3. Mace (Chùy) - Sát thương tăng theo độ cao rơi
4. Tay không - Khi không có vũ khí

### Tầm xa:
5. Bow (Cung) - Tự động kéo dây và bắn
6. Crossbow (Nỏ) - Tự động nạp và bắn
7. Trident (Đinh ba) - Phóng tầm xa

### Đặc biệt:
8. Wind Charge (Cầu Gió) - Đẩy lùi đối thủ
9. Splash Potion (Thuốc ném) - Gây sát thương hoặc hồi máu
10. Shield (Khiên) - Block tự động

## Chiến thuật AI

### Kết hợp vũ khí thông minh:
- Khoảng cách xa (>10 blocks): Bow → Crossbow → Trident
- Khoảng cách gần (<4 blocks): Mace → Axe (nếu enemy có shield) → Sword
- Tự động chọn vũ khí tốt nhất cùng loại (Netherite > Diamond > Iron > Stone > Wood)

### Combo nâng cao:
- W-Tap: Nhả phím W sau mỗi đòn đánh để reset knockback
- Crit Chain: Nhảy liên tục để tạo critical hits (+50% sát thương)
- Shield Counter: Tự động giơ khiên khi bị tấn công
- Weapon Counter: Đổi rìu khi gặp shield để phá vỡ phòng thủ

### Quản lý inventory:
- Tự động phân tích 36 slots trong inventory
- Sắp xếp vũ khí vào hotbar theo độ ưu tiên
- Tự động mặc giáp tốt nhất (Netherite > Diamond > Iron > Chain > Gold > Leather)
- Ưu tiên vũ khí có enchant

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

Trong file ArmorManager.java:
- EQUIP_COOLDOWN_TICKS: Cooldown mặc giáp (mặc định 40 tick = 2 giây)

## Xử lý lỗi

Nếu AI không bật: Kiểm tra đã nhấn Right Shift chưa. Khi bật thành công sẽ có thông báo màu xanh.

Nếu AI không tấn công: Đảm bảo có monster hoặc player khác trong bán kính 32 blocks.

Nếu AI không đổi vũ khí: Đảm bảo có nhiều loại vũ khí trong hotbar (9 slot đầu).

Nếu build bị lỗi: Kiểm tra đã cài Fabric API chưa. Chạy: gradlew clean build --stacktrace

Nếu game crash: Kiểm tra file log trong .minecraft/logs/latest.log

## Lịch sử phát triển

Phiên bản 1.0.0:
- Bật/tắt AI bằng phím Right Shift
- Tự động tìm mục tiêu
- Phân tích đối thủ cơ bản
- Tấn công và di chuyển cơ bản

Phiên bản 1.1.0:
- Đổi vũ khí thông minh (Sword/Axe)
- Combo W-Tap và Crit Chain
- Shield blocking cơ bản

Phiên bản 1.2.0:
- Tự động ăn uống và hồi máu thông minh
- Tự động dùng khiên nâng cao
- Né đạn (arrow, snowball, egg)
- Chiến đấu tầm xa (bow, crossbow, trident)
- Chiến đấu với Mace (fall damage)
- Ném Wind Charge
- Tự động ném thuốc (splash potion)

Phiên bản 1.3.0 (HIỆN TẠI):
- Tự động mặc giáp tốt nhất
- Phân tích và sắp xếp vũ khí trong inventory
- Kết hợp vũ khí thông minh theo tình huống
- Hỗ trợ 10+ loại vũ khí

Phiên bản 1.4.0 (TIẾP THEO):
- File cấu hình (config) cho phép người dùng tùy chỉnh
- Giao diện đồ họa (GUI) đơn giản
- Tối ưu hóa hiệu suất AI
- Hỗ trợ TNT và Flint & Steel

Phiên bản 1.5.0 (dự kiến):
- Hỗ trợ nhiều mục tiêu cùng lúc
- Chế độ PvP team
- Tự động craft vật phẩm cần thiết

Phiên bản 2.0.0 (dự kiến):
- AI học máy (Machine Learning)
- Tự động thích nghi với phong cách chiến đấu của đối thủ
- Phân tích pattern và dự đoán hành động

## Giấy phép

Dự án được phân phối dưới giấy phép CC0-1.0 (Public Domain). Bạn có thể tự do sử dụng, sửa đổi và phân phối.

## Liên hệ

Tác giả: Minh Quang
Dự án: AutoDuelist - AI PvP Master Mod

## Lưu ý

Sử dụng mod này một cách có trách nhiệm, đặc biệt trên các server công cộng. Một số server có thể cấm auto-PvP mods. Hãy kiểm tra quy định của server trước khi sử dụng.

---

Made by Minh Quang for Minecraft Fabric 1.21.11
Phiên bản hiện tại: 1.3.0 | Phiên bản tiếp theo: 1.4.0
