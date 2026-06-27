# SiNILAI - Sistem Informasi Nilai Mahasiswa 🎓

Aplikasi desktop berbasis Java (Swing) untuk mempermudah pengelolaan data akademik. Dibuat dengan antarmuka yang modern, bersih, dan rapi menggunakan tema FlatLaf.

---

## 🎥 Demo Aplikasi
Preview singkat bagaimana aplikasi ini berjalan saat digunakan. 

![Demo Aplikasi SiNILAI](masukkan-link-atau-nama-file-gif-kamu-disini.gif)
*(Keterangan: Navigasi antar menu dan contoh penginputan data)*

---

## 📸 Tampilan Antarmuka (Screenshots)

Berikut adalah beberapa halaman utama yang ada di dalam aplikasi SiNILAI:

### 1. Halaman Dashboard
Halaman pertama yang menyambut pengguna setelah login. Menampilkan kartu ringkasan jumlah data (Mahasiswa, Dosen, Mata Kuliah, KRS) dan tabel riwayat nilai terbaru yang baru masuk.

![Halaman Dashboard](masukkan-link-foto-dashboard-kamu.jpg)

### 2. Kelola Data Mahasiswa
Menu untuk mengatur data induk mahasiswa. Form input sengaja diletakkan di bagian atas agar rapi, dan tabel di bawahnya akan otomatis menyesuaikan ukuran layar (responsif) untuk memudahkan pencarian data.

![Kelola Data Mahasiswa](masukkan-link-foto-mahasiswa-kamu.jpg)

### 3. Kelola Data Dosen & Mata Kuliah
Struktur visual yang seragam diterapkan di semua menu agar pengguna tidak bingung. Di sini admin bisa menambah, mengedit, atau menghapus data dosen pengampu dan daftar mata kuliah.

![Kelola Dosen](masukkan-link-foto-dosen-kamu.jpg)

### 4. Form Input Nilai
Halaman krusial tempat admin memasukkan dan merekap nilai akhir mahasiswa berdasarkan mata kuliah yang diambil.

![Halaman Input Nilai](masukkan-link-foto-input-nilai-kamu.jpg)

---

## Teknologi yang Dipakai
* **Bahasa:** Java
* **IDE:** Apache NetBeans
* **Database:** MySQL
* **UI/Tema:** FlatLaf

## Cara Menjalankan Project
1. Buat database baru di MySQL/phpMyAdmin dengan nama `db_sinilai`.
2. Import file database `.sql` yang ada di folder project ini.
3. Buka project menggunakan NetBeans.
4. Pastikan library `mysql-connector-j` dan `flatlaf` sudah ditambahkan ke dalam *Libraries* project.
5. Jalankan file utama (Main Class) projectnya.
