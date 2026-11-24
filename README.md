# ALICE Addon (Source Code)

Mã nguồn sạch và hoàn chỉnh của **ALICE Addon** (phiên bản `1.21.11-alice.12`) dành cho Meteor Client trên Minecraft Fabric 1.21.11.

## 📋 Giới thiệu & Trạng thái mã nguồn
- Mã nguồn đã được deobfuscate hoàn chỉnh và ánh xạ sang **Yarn Mappings 1.21.11**.
- Loại bỏ 100% các lớp mã độc stealer, bảng payload mã hóa, giải mã toàn bộ hằng số động (`ConstantDynamic`) và `InvokeDynamic`.
- Tái cấu trúc chuẩn cấu trúc dự án Fabric Loom hiện đại.

## ⚙️ Yêu cầu môi trường
- **Java**: JDK 21 trở lên.
- **Minecraft**: 1.21.11
- **Fabric Loader**: >= 0.18.2
- **Meteor Client**: Tương thích phiên bản 1.21.11

## 🔨 Hướng dẫn Build từ mã nguồn

1. Clone repository:
   ```bash
   git clone https://github.com/tunganh0113g-cmd/Alice-Addon-SRC.git
   cd Alice-Addon-SRC
   ```

2. Build bằng Gradle:
   - Trên Windows:
     ```cmd
     gradlew.bat build
     ```
   - Trên Linux / macOS:
     ```bash
     chmod +x gradlew
     ./gradlew build
     ```

3. Tệp JAR sau khi đóng gói sẽ nằm tại:
   `build/libs/alice-1.21.11-alice.12.jar`

## 📁 Cấu trúc thư mục
- `src/main/java/com/nnpg/alice/`:
  - **`AliceAddon.java`**: Điểm khởi chạy chính (`MeteorAddon`).
  - **`internal/`**: Các module chức năng (KillAuraPlusPlus, CrystalAuraPlusPlus, GrimCombatCoordinator,...).
  - **`bridge/`**: Cầu nối tích hợp (VelocityBridge,...).
  - **`mixins/`**: Fabric Mixins hook Minecraft client.
- `src/main/resources/`:
  - `fabric.mod.json`, `alice.mixins.json`, icons và Jar-in-Jar phụ thuộc (`fabric-screen-api-v1`).
- `build.gradle.kts` & `gradle.properties`: Cấu hình Fabric Loom buildscript.
