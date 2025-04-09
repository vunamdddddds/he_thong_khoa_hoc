package com.example.online_courses.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Controller
public class NewsController {

    @Value("${news.storage.path}")
    private String newsStoragePath; // Đường dẫn lưu trữ tin tức (được cấu hình trong application.properties)

    @Value("${news.images.path}")
    private String newsImagesPath; // Đường dẫn lưu trữ ảnh (được cấu hình trong application.properties)

    // Hiển thị trang tạo tin tức (Admin)
    @GetMapping("/admin/news/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateNewsForm() {
        return "admin/create-news";
    }

    // Xử lý tạo tin tức (Admin)
    @PostMapping("/admin/news/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createNews(
            @RequestParam("title") String title,
            @RequestParam("date") String date,
            @RequestParam("image") MultipartFile image,
            @RequestParam("excerpt") String excerpt,
            @RequestParam("content") String content,
            RedirectAttributes redirectAttributes) {
        try {
            // Tạo slug từ tiêu đề
            String slug = createSlug(title);

            // Lưu ảnh đại diện
            String imageFileName = slug + "-" + image.getOriginalFilename();
            Path imagePath = Paths.get(newsImagesPath, imageFileName);
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, image.getBytes());
            String imageUrl = "/news-images/" + imageFileName;

            // Tạo nội dung HTML cho tin tức
            String newsHtmlContent = generateNewsHtmlContent(title, date, imageUrl, excerpt, content);

            // Lưu nội dung tin tức thành file HTML
            Path newsFilePath = Paths.get(newsStoragePath, slug + ".html");
            Files.createDirectories(newsFilePath.getParent());
            Files.write(newsFilePath, newsHtmlContent.getBytes());

            redirectAttributes.addFlashAttribute("success", "Tạo tin tức thành công!");
            return "redirect:/admin/news";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo tin tức: " + e.getMessage());
            return "redirect:/admin/news/create";
        }
    }

    // Hiển thị danh sách tin tức (Admin)
    @GetMapping("/admin/news")
    @PreAuthorize("hasRole('ADMIN')")
    public String listNews(Model model) {
        List<NewsSummary> newsList = new ArrayList<>();
        File newsDir = new File(newsStoragePath);
        if (newsDir.exists() && newsDir.isDirectory()) {
            for (File file : newsDir.listFiles(f -> f.getName().endsWith(".html"))) {
                String slug = file.getName().replace(".html", "");
                String content = readFileContent(file);
                String title = extractTitleFromContent(content);
                String date = extractDateFromContent(content);
                newsList.add(new NewsSummary(title, date, slug));
            }
        }
        model.addAttribute("newsList", newsList);
        return "admin/news-list";
    }

    // Xóa tin tức (Admin)
    @PostMapping("/admin/news/delete/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteNews(@PathVariable("slug") String slug, RedirectAttributes redirectAttributes) {
        try {
            Path newsFilePath = Paths.get(newsStoragePath, slug + ".html");
            Files.deleteIfExists(newsFilePath);

            // Xóa ảnh liên quan (nếu có)
            File imagesDir = new File(newsImagesPath);
            for (File imageFile : imagesDir.listFiles(f -> f.getName().startsWith(slug + "-"))) {
                Files.deleteIfExists(imageFile.toPath());
            }

            redirectAttributes.addFlashAttribute("success", "Xóa tin tức thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa tin tức: " + e.getMessage());
        }
        return "redirect:/admin/news";
    }

    // Hiển thị danh sách tin tức (Người dùng)
    @GetMapping("/news")
    public String showNewsPage(Model model) {
        List<NewsSummary> newsList = new ArrayList<>();
        File newsDir = new File(newsStoragePath);
        if (newsDir.exists() && newsDir.isDirectory()) {
            for (File file : newsDir.listFiles(f -> f.getName().endsWith(".html"))) {
                String slug = file.getName().replace(".html", "");
                String content = readFileContent(file);
                String title = extractTitleFromContent(content);
                String date = extractDateFromContent(content);
                String excerpt = extractExcerptFromContent(content);
                String imageUrl = findImageForSlug(slug);
                newsList.add(new NewsSummary(title, date, excerpt, imageUrl, slug));
            }
        }
        model.addAttribute("newsList", newsList);
        return "news";
    }

    // Hiển thị chi tiết tin tức (Người dùng)
    @GetMapping("/news/{slug}")
    public String showNewsDetail(@PathVariable("slug") String slug) {
        return "/news/" + slug; // Trỏ trực tiếp đến file HTML tĩnh
    }

    // Tạo slug từ tiêu đề
    private String createSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("")
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        return slug;
    }

    // Tạo nội dung HTML cho tin tức
    private String generateNewsHtmlContent(String title, String date, String imageUrl, String excerpt, String content) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                    <style>
                        body { font-family: 'Montserrat', sans-serif; padding: 20px; }
                        .container { max-width: 800px; margin: 0 auto; }
                        .news-title { color: #258e01; font-size: 2rem; font-weight: 700; }
                        .news-date { color: #666; font-size: 0.9rem; margin: 10px 0; }
                        .news-image img { width: 100%%; height: auto; border-radius: 10px; margin-bottom: 20px; }
                        .news-content { line-height: 1.6; color: #333; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1 class="news-title">%s</h1>
                        <p class="news-date"><i class="far fa-calendar-alt"></i> %s</p>
                        <div class="news-image">
                            <img src="%s" alt="%s">
                        </div>
                        <p class="news-excerpt">%s</p>
                        <div class="news-content">
                            %s
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(title, title, date, imageUrl, title, excerpt, content);
    }

    // Đọc nội dung file
    private String readFileContent(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Trích xuất tiêu đề từ nội dung HTML
    private String extractTitleFromContent(String content) {
        String titleTag = "<h1 class=\"news-title\">";
        int start = content.indexOf(titleTag) + titleTag.length();
        int end = content.indexOf("</h1>", start);
        return content.substring(start, end);
    }

    // Trích xuất ngày từ nội dung HTML
    private String extractDateFromContent(String content) {
        String dateTag = "<p class=\"news-date\"><i class=\"far fa-calendar-alt\"></i> ";
        int start = content.indexOf(dateTag) + dateTag.length();
        int end = content.indexOf("</p>", start);
        return content.substring(start, end);
    }

    // Trích xuất mô tả ngắn từ nội dung HTML
    private String extractExcerptFromContent(String content) {
        String excerptTag = "<p class=\"news-excerpt\">";
        int start = content.indexOf(excerptTag) + excerptTag.length();
        int end = content.indexOf("</p>", start);
        return content.substring(start, end);
    }

    // Tìm URL ảnh dựa trên slug
    private String findImageForSlug(String slug) {
        File imagesDir = new File(newsImagesPath);
        for (File imageFile : imagesDir.listFiles(f -> f.getName().startsWith(slug + "-"))) {
            return "/news-images/" + imageFile.getName();
        }
        return "/images/news1.jpg"; // Ảnh mặc định nếu không tìm thấy
    }

    // Class để lưu thông tin tóm tắt của tin tức
    public static class NewsSummary {
        private String title;
        private String date;
        private String excerpt;
        private String imageUrl;
        private String slug;

        public NewsSummary(String title, String date, String slug) {
            this.title = title;
            this.date = date;
            this.slug = slug;
        }

        public NewsSummary(String title, String date, String excerpt, String imageUrl, String slug) {
            this.title = title;
            this.date = date;
            this.excerpt = excerpt;
            this.imageUrl = imageUrl;
            this.slug = slug;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }

        public String getExcerpt() {
            return excerpt;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getSlug() {
            return slug;
        }
    }
}