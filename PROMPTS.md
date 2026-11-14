# Project Prompts Specification: Ad Performance Aggregator

This document records the two structured prompts used to guide the architectural design and subsequent implementation of the high-performance Java CSV processing application.

---

## Prompt 1: Architectural Design & Risk Analysis

```text
Đóng vai trò là một Senior Software Architect chuyên xây dựng hệ thống xử lý dữ liệu lớn bằng Java Core thuần túy.

Tôi có một bài toán lập trình yêu cầu xử lý một tệp CSV chứa dữ liệu hiệu suất quảng cáo dung lượng tối thiểu 1GB. Dưới đây là các yêu cầu chi tiết của bài toán:

1. Cấu trúc tệp CSV đầu vào (Schema):
- Có tiêu đề (header) gồm các cột theo đúng thứ tự: campaign_id (string), date (YYYY-MM-DD), impressions (int), clicks (int), spend (float), conversions (int).
2. Yêu cầu nghiệp vụ:
- Gom nhóm và tổng hợp dữ liệu theo từng campaign_id: tổng impressions, tổng clicks, tổng spend, tổng conversions.
- Tính các chỉ số: CTR = tổng clicks / tổng impressions; CPA = tổng spend / tổng conversions.
- Lưu ý nghiệp vụ: Nếu conversions = 0, CPA phải trả về null hoặc bỏ qua, không tính toán gây lỗi.
- Kết quả cuối cùng: Xuất ra 2 tệp CSV gồm Top 10 chiến dịch có CTR cao nhất và Top 10 chiến dịch có CPA thấp nhất (với CPA, bắt buộc loại trừ các chiến dịch có conversions = 0).
3. Yêu cầu phi chức năng:
- Xử lý hiệu quả tệp có dung lượng tối thiểu 1GB, tối ưu bộ nhớ tuyệt đối (tránh tràn RAM/OOM).
- Tối ưu tốc độ, giảm áp lực lên Garbage Collector.

NHIỆM VỤ CỦA BẠN TRONG BƯỚC NÀY:
Dựa trên các yêu cầu trên, hãy tự suy nghĩ và đưa ra thiết kế kỹ thuật hoàn chỉnh bao gồm:
1. Luồng chạy chi tiết của chương trình (từ lúc nhận tham số đầu vào đến khi ghi file kết quả). Hãy tự vạch ra các bước xử lý (ví dụ: cách đọc stream, cách parse dòng, cách lưu trữ trong RAM, cách dùng thuật toán để tìm Top 10).
2. Phân tích rủi ro & xử lý lỗi ở từng bước trong luồng chạy do chính bạn tự suy luận (Ví dụ: file không tồn tại, dòng CSV bị sai lệch cấu trúc cột, giá trị số âm, lỗi định dạng dữ liệu, chia cho 0, v.v.) và đề xuất phương án xử lý tương ứng.

Chưa viết mã nguồn ở bước này. Hãy tập trung toàn bộ vào tư duy thiết kế, luồng xử lý và phân tích rủi ro. Viết ngắn gọn, xúc tích, mạch lạc, không lan man
```

---

## Prompt 2: Production-Grade Implementation & Testing

```text
Dựa trên thiết kế kiến trúc, luồng xử lý và phương án xử lý rủi ro mà bạn đã phân tích ở bước trước (được trích dẫn dưới đây):

---
[Output from Prompt 1 Architecture Phase]
---

NHIỆM VỤ CỦA BẠN TRONG BƯỚC NÀY:
Hãy hiện thực hóa toàn bộ thiết kế trên thành mã nguồn Java Core thuần túy (Pure Java, KHÔNG dùng Spring Framework/Spring Boot), sẵn sàng cho môi trường production. 
Cấu trúc thư mục và package bắt buộc của dự án:
- Source code chính đặt tại: src/main/java/com/flinters/test (Package: com.flinters.test)
- Test code đặt tại: src/test/java/com/flinters/test (Package: com.flinters.test)
Hãy cung cấp các thành phần sau với mã nguồn chi tiết, sạch sẽ và có comment đầy đủ:

1. Cấu hình dự án:
   - Tệp pom.xml cho Maven (Java 17 hoặc 21, kèm dependency cho JUnit 5). Không dùng thư viện ngoài nào khác không cần thiết.

2. Các lớp mã nguồn chính (Package: com.flinters.aggregator):
   - CampaignStats.java (Model cộng dồn và tính CTR/CPA an toàn, xử lý triệt để các trường hợp chia cho 0).
   - CsvLineParser.java (Parse dòng hiệu suất cao dùng indexOf/substring, tích hợp cơ chế bắt lỗi dòng sai định dạng/thiếu cột/giá trị âm như đã thiết kế ở bước 1).
   - AdAggregator.java (Đọc stream qua BufferedReader, tuyệt đối không load toàn bộ file 1GB vào RAM; gom nhóm bằng HashMap và dùng PriorityQueue để tìm Top 10 CTR và CPA).
   - CsvResultWriter.java (Xuất file top10_ctr.csv và top10_cpa.csv chuẩn định dạng số %.4f, %.2f, dùng Locale.US).
   - App.java (Điểm vào CLI xử lý tham số --input, --output và đo đạc benchmark thời gian, RAM).

3. Unit Test (JUnit 5):
   - Viết các test case kiểm thử tính đúng đắn của logic tính toán, các trường hợp biên và khả năng chịu lỗi của parser đối với dữ liệu không đúng chuẩn.

4. Triển khai & Tài liệu:
   - Tệp Dockerfile và README.md hướng dẫn build, chạy chương trình và tóm tắt các quyết định thiết kế.

Hãy trình bày rõ ràng từng tệp kèm theo đường dẫn/tên tệp tương ứng.
```