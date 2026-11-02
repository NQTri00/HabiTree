# 💡 Đề xuất dự án: Mầm Xanh (MamXanh)

- **Tên dự án:** Mầm Xanh (MamXanh) - Daily Quest & Living Tree Garden
- **Sứ mệnh:** Biến việc duy trì thói quen mỗi ngày (học tập, sức khỏe, nếp sống) thành một trò chơi nhẹ nhàng. Người dùng hoàn thành nhiệm vụ để nhận tiền tệ trong game, dùng tiền tệ mua hạt giống và vật phẩm để nuôi một khu vườn cây đẹp, độc đáo, thay đổi theo bốn mùa. Nhiệm vụ và cây có tầm quan trọng ngang nhau: nhiệm vụ tạo thói quen, cây là phần thưởng người dùng muốn nhìn thấy mỗi ngày.

---

## 1. Vấn đề & nỗi đau cần giải quyết

1. **Checklist nhàm chán:** App việc cần làm thông thường giống "bài tập về nhà", không tạo cảm giác muốn mở app mỗi ngày nên người dùng bỏ sau vài ngày.
2. **Khó duy trì thói quen nhỏ:** Học bài buổi tối, vận động, ăn đủ bữa, đánh răng, ngủ đúng giờ đều là việc nhỏ, dễ bị bỏ quên khi bận và không có động lực tức thì.
3. **App "nuôi cây" hiện có ít kiểu cây, ít cá tính:** Phần lớn chỉ cho một loại cây mặc định, không thay đổi theo thời gian, nên người dùng nhanh chán và không có lý do gắn bó lâu dài.
4. **Thiếu cảm giác tiến triển:** Tích dấu ✓ không cho thấy thành quả. Người dùng cần thấy một thứ gì đó lớn lên, thay đổi và đẹp hơn theo nỗ lực của mình.

## 2. Các giải pháp hiện có và khác biệt

Đã khảo sát nhanh các app cùng hướng (qua trang giới thiệu app, chưa dùng thử từng app):

| App | Cách làm | Điều chưa có / khác với MamXanh |
|---|---|---|
| Habits | Mỗi thói quen là một cây con, check-in để "tưới" | Chưa thấy vòng lặp tiền tệ - cửa hàng |
| Flora / Forest | Cây lớn theo thời gian tập trung | Tập trung vào hẹn giờ, không phải bộ nhiệm vụ hằng ngày |
| GrowDay, Aloe Bud, HabiTree, Eden, MeGrow | Một cây/hoa lớn khi hoàn thành thói quen | Ít loại cây, ít thay đổi theo mùa |
| Habitica | Nhiệm vụ + EXP + nhân vật RPG | Không phải vườn cây; nặng yếu tố game hơn |

**Điểm khác biệt đề xuất:**

- **Bộ sưu tập nhiều loại cây** có phong cách riêng, nuôi nhiều cây cùng lúc (thay vì một cây mặc định).
- **Cây thay đổi theo mùa ngoài đời thật**, mỗi loại cây có biểu hiện mùa khác nhau để tạo lý do sưu tầm.
- **Vòng lặp tiền tệ → cửa hàng → cây**, thay vì mỗi lần check-in là cây tự lớn.
- **Bộ nhiệm vụ có sẵn theo nhóm**, người dùng bắt đầu ngay mà không phải tự tạo từ đầu.

## 3. Nhóm người dùng

Học sinh, sinh viên và người đi làm trẻ.

## 4. Tính năng cốt lõi

### 4.1 Hệ thống nhiệm vụ (Daily Quests)

- Nhiệm vụ chia theo nhóm: **Học tập & Tập trung**, **Sức khỏe & Vận động**, **Vệ sinh & Nếp sống**. (Danh sách mẫu ở file `MamXanh-Tasks-And-Trees.md`.)
- Người dùng chọn bộ nhiệm vụ khi bắt đầu, có thể thêm, sửa, xóa nhiệm vụ riêng trong giới hạn của game.
- **Xác nhận hoàn thành chống bấm bừa:** đồng hồ đếm giờ với nhiệm vụ học/tập luyện, nút nhấn giữ với nhiệm vụ vệ sinh, một số nhiệm vụ chỉ mở trong khung giờ phù hợp.
- Thưởng **Daily Clear** khi hoàn thành toàn bộ nhiệm vụ trong ngày; **streak** theo chuỗi ngày liên tiếp; **vé bỏ qua một ngày** khi bất khả kháng.

### 4.2 Hệ thống cây (Tree Garden), trọng tâm ngang với nhiệm vụ

- Bộ sưu tập cây đa dạng, mỗi loại có phong cách hình ảnh riêng.
- Chọn 1 trong 3 cây khi bắt đầu; mua thêm cây bằng tiền tệ và nuôi nhiều cây cùng lúc.
- 4 giai đoạn phát triển: hạt mầm → cây non → cây trưởng thành → ra hoa/kết quả.
- **Cây lớn dần theo chiều cao:** mỗi cây bắt đầu từ một mầm nhỏ và cao thêm từng chút mỗi ngày nhờ vật phẩm chăm sóc và thời gian thực. Chiều cao hiển thị cho người dùng thấy cây đang lớn liên tục, không chỉ nhảy theo giai đoạn.
- **Giai đoạn chuyển theo ngưỡng chiều cao:** khi đạt một mức chiều cao, cây sang giai đoạn kế tiếp và đổi hình. Ngưỡng khởi điểm: cây non 10 cm, trưởng thành 50 cm, ra hoa/kết quả 120 cm.
- **Giới hạn tăng mỗi ngày:** mỗi cây chỉ cao thêm tối đa một mức nhất định mỗi ngày (khởi điểm 15 cm), nên không thể rút ngắn bằng cách dồn vật phẩm.
- **Chiều cao không giới hạn ở cuối game:** sau giai đoạn cuối cây vẫn tiếp tục cao thêm, nhưng tốc độ chậm dần. Các cột mốc chiều cao (ví dụ 1 m, 5 m, 10 m) mở huy hiệu hoặc vật phẩm trang trí, giúp người dùng đã có cây trưởng thành vẫn có mục tiêu dài hạn.
- **Cây thay đổi theo mùa** (xuân, hạ, thu, đông) theo ngày trong năm của người dùng; mỗi loại cây có biểu hiện mùa riêng.
- Chăm sóc bằng vật phẩm (nước, phân bón, chậu, đồ trang trí); cây cần cả vật phẩm lẫn **thời gian thực** để lớn, nên không thể "cày nhanh" trong một ngày.

### 4.3 Cửa hàng & thống kê

- Cửa hàng: hạt giống, vật phẩm chăm sóc, chậu, đồ trang trí.
- Thống kê tuần/tháng, tỷ lệ hoàn thành theo nhóm nhiệm vụ, bộ sưu tập cây đã mở khóa.
- Nhắc nhở theo khung giờ cho từng nhiệm vụ, có thể tùy chỉnh.

## 5. Cân bằng kinh tế game

- **Giới hạn tiền tệ kiếm được mỗi ngày:** dù bấm bừa cũng không thể tiến nhanh hơn người làm thật.
- **Cây lớn theo thời gian thực** kết hợp với vật phẩm.
- **Cây đầu tiên miễn phí**, giá cây tăng dần theo độ hiếm và độ đẹp để tạo mục tiêu dài hạn.
- Các con số thưởng/giá trong tài liệu là giá trị khởi điểm, cần chỉnh khi thử nghiệm.

## 6. Mục tiêu cần đạt

- Người dùng muốn mở app mỗi ngày, không coi nhiệm vụ là gánh nặng.
- Nhiệm vụ được hoàn thành đều đặn, có chuỗi ngày liên tiếp.
- Cây là phần thưởng đáng nhìn: đa dạng, đẹp, đổi theo mùa, khiến người dùng muốn sưu tầm thêm.
- Cảm giác tiến triển rõ ràng: chiều cao tăng dần, giai đoạn đổi hình, cột mốc mở khóa.
- Bấm hoàn thành bừa không giúp tiến nhanh hơn người làm thật.
- App chạy offline, dữ liệu riêng tư lưu trên máy.

## 7. Rủi ro & lưu ý

- **Chưa có dữ liệu người dùng thật.** Các mục tiêu ở trên là điều cần đạt, chưa được kiểm chứng.
- **Gian lận (bấm hoàn thành bừa):** không ngăn tuyệt đối được. Giảm thiểu bằng đồng hồ đếm giờ, nút nhấn giữ, khung giờ hợp lệ, giới hạn tiền tệ mỗi ngày và cây lớn theo thời gian thực. App dùng cá nhân, không có bảng xếp hạng, nên gian lận chỉ ảnh hưởng chính người dùng.
- **Khối lượng đồ họa lớn:** mỗi cây có 4 giai đoạn và 4 mùa, nên số lượng hình tăng nhanh theo số loại cây.
- **Mùa theo vùng:** miền Nam không có bốn mùa rõ rệt, nên hiệu ứng mùa cần có tùy chọn bật/tắt hoặc theo vùng.
- **Cạnh tranh:** hướng "habit + cây" đã có nhiều app. Giá trị của dự án nằm ở sự đa dạng cây, hiệu ứng mùa và vòng lặp cửa hàng.
- **Dữ liệu cá nhân:** toàn bộ dữ liệu lưu trên máy, không tài khoản, không gửi ra ngoài.
