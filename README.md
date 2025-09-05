# BookVerse - Aplikasi Perpustakaan Digital

BookVerse adalah aplikasi perpustakaan digital berbasis **Java Spring Boot** yang dirancang untuk memudahkan pengelolaan koleksi buku, peminjaman, dan pengembalian secara online.  
Aplikasi ini dilengkapi dengan autentikasi berbasis **JWT** dan manajemen pengguna berbasis **role** (Admin & User).  
Dengan BookVerse, proses manajemen perpustakaan menjadi lebih modern, efisien, dan terorganisir, baik untuk admin maupun pengguna.

---

## Teknologi yang Digunakan

- **Backend:** Java 21, Spring Boot 3.5.3, Spring Security 6, Spring Data JPA (Hibernate)  
- **Database:** MySQL 8 (mysql-connector-j)  
- **Frontend (UI):** Thymeleaf, HTML5, CSS3  
- **Keamanan:** JWT (jjwt), BCrypt (spring-security-crypto)  
- **Validasi & Utility:** Bean Validation, Lombok  
- **Dokumentasi API:** Springdoc OpenAPI (Swagger UI)  
- **Email Service:** Spring Boot Starter Mail (untuk verifikasi/reset password)  
- **Export Laporan:** OpenHTMLToPDF (PDFBox)  
- **Build Tools:** Maven  
- **Testing:** JUnit, Spring Boot Starter Test  

---

## Cara Menjalankan Projek

1. Clone repository dari GitHub  
2. Nyalakan XAMPP / MySQL Server  
3. Buat database baru, lalu konfigurasi di `application.properties`  
4. Konfigurasi email (untuk reset password / notifikasi) di `application.properties`  
5. Jalankan aplikasi dengan Maven / IDE Anda  

---

## Akun Default

Untuk login awal sebagai **Admin**, gunakan akun berikut:  
- **Username:** `admin`  
- **Password:** `12345678`  

---

## Fitur Utama

### Autentikasi & Otorisasi
- Register & Login berbasis **JWT**  
- Role: **Admin** & **User**  
- Logout & proteksi halaman dengan **Spring Security**  
- Validasi input saat registrasi  

---

### Admin

1. **Manajemen Kategori**
   - CRUD Kategori (oleh Admin)  
   - Kategori tidak dapat dihapus jika sudah dipakai oleh buku  
   - Pilihan kategori muncul di filter buku  

2. **Manajemen Buku**
   - CRUD Buku (oleh Admin)  
   - Buku tidak dapat dihapus jika sedang/pernah dipinjam oleh pengguna  
   - Upload cover buku (disimpan di folder `/uploads/covers`)  
   - Pencarian & filter berdasarkan judul dan kategori  

3. **Peminjaman Buku**
   - Melihat daftar peminjaman buku oleh user  
   - Status peminjaman: `BORROWED`, `RETURN_REQUEST`, `RETURNED`, `OVERDUE`  
   - Admin dapat mengonfirmasi pengembalian buku yang diajukan user  
   - Filter daftar peminjaman berdasarkan status  
   - Validasi deadline pengembalian otomatis  
   - Ekspor laporan peminjaman ke PDF  

---

### User

1. **Registrasi & Profil**
   - Registrasi dengan validasi data  
   - Melihat & mengedit profil  

2. **Koleksi Buku**
   - Melihat daftar koleksi buku  
   - Fitur search & filter berdasarkan judul dan kategori  
   - Meminjam buku yang tersedia  

3. **Peminjaman**
   - Melihat buku yang sedang dipinjam  
   - Mengajukan pengembalian ketika selesai membaca  
