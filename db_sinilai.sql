-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: db_sinilai
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dosen`
--

DROP TABLE IF EXISTS `dosen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dosen` (
  `nidn` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `keahlian` varchar(100) NOT NULL,
  `no_hp` varchar(20) NOT NULL,
  PRIMARY KEY (`nidn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dosen`
--

LOCK TABLES `dosen` WRITE;
/*!40000 ALTER TABLE `dosen` DISABLE KEYS */;
INSERT INTO `dosen` VALUES ('0011223344','Dr. Budi Santoso','Pemrograman','081234567890'),('0022334455','Ir. Sari Dewi M.T','Basis Data','082345678901'),('0033445566','Prof. Andi Wijaya','Jaringan Komputer','083456789012'),('0044556677','Dr. Rina Marlina','Kecerdasan Buatan','084567890123'),('0055667788','M. Rizki M.Kom','Web Programming','085678901234');
/*!40000 ALTER TABLE `dosen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `krs`
--

DROP TABLE IF EXISTS `krs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `krs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nim` varchar(20) NOT NULL,
  `kode_mk` varchar(20) NOT NULL,
  `score` double DEFAULT '0',
  `semester` int NOT NULL,
  `tahun_ajaran` varchar(20) NOT NULL,
  `nidn_dosen` varchar(20) DEFAULT NULL,
  `nilai_sikap` double DEFAULT '0',
  `nilai_uts` double DEFAULT '0',
  `nilai_uas` double DEFAULT '0',
  `grade` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nim` (`nim`),
  KEY `kode_mk` (`kode_mk`),
  KEY `nidn_dosen` (`nidn_dosen`),
  CONSTRAINT `krs_ibfk_1` FOREIGN KEY (`nim`) REFERENCES `mahasiswa` (`nim`) ON DELETE CASCADE,
  CONSTRAINT `krs_ibfk_2` FOREIGN KEY (`kode_mk`) REFERENCES `mata_kuliah` (`kode`) ON DELETE CASCADE,
  CONSTRAINT `krs_ibfk_3` FOREIGN KEY (`nidn_dosen`) REFERENCES `dosen` (`nidn`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `krs`
--

LOCK TABLES `krs` WRITE;
/*!40000 ALTER TABLE `krs` DISABLE KEYS */;
INSERT INTO `krs` VALUES (2,'2021001','MK001',80,2,'2024/2025','0022334455',80,80,80,'B'),(3,'2021001','MK002',90,2,'2024/2025','0055667788',90,90,90,'A'),(4,'2021003','MK004',73,3,'2024/2025','0044556677',80,70,70,'C'),(5,'2021003','MK004',90,3,'2024/2025','0033445566',90,90,90,'A');
/*!40000 ALTER TABLE `krs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mahasiswa`
--

DROP TABLE IF EXISTS `mahasiswa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mahasiswa` (
  `nim` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `prodi` varchar(100) NOT NULL,
  `angkatan` varchar(10) NOT NULL,
  PRIMARY KEY (`nim`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mahasiswa`
--

LOCK TABLES `mahasiswa` WRITE;
/*!40000 ALTER TABLE `mahasiswa` DISABLE KEYS */;
INSERT INTO `mahasiswa` VALUES ('2021001','Andi Pratama','Ilmu Komputer','2021'),('2021002','Sari Dewi','Sistem Informasi','2021'),('2021003','Budi Santoso','Ilmu Komputer','2021'),('2021004','Rina Marlina','Teknik Informatika','2021'),('2021005','Doni Setiawan','Sistem Informasi','2021'),('2022001','Fitri Handayani','Ilmu Komputer','2022'),('2022002','Agus Salim','Teknik Informatika','2022'),('2022003','Dewi Rahayu','Sistem Informasi','2022'),('2022004','Hendra Kusuma','Ilmu Komputer','2022'),('2022005','Lina Susanti','Teknik Informatika','2022'),('2023001','Muhammad Rizki','Ilmu Komputer','2023'),('2023002','Sari Dewi','Sistem Informasi','2023'),('2023003','Randi Firmansyah','Teknik Informatika','2023'),('2023004','Siti Nurhaliza','Ilmu Komputer','2023'),('2023005','Toni Wijaya','Sistem Informasi','2023'),('2024001','Umar Bakri','Teknik Informatika','2024'),('2024002','Vina Oktaviani','Ilmu Komputer','2024'),('2024003','Wahyu Hidayat','Sistem Informasi','2024'),('2024004','Xena Putri','Teknik Informatika','2024'),('2024005','Yogi Prasetyo','Ilmu Komputer','2024'),('221501059','biang','Teknik Informatika','2022'),('2515101040','I Ketut Dharmawan','Ilmu Komputer','2025');
/*!40000 ALTER TABLE `mahasiswa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mata_kuliah`
--

DROP TABLE IF EXISTS `mata_kuliah`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mata_kuliah` (
  `kode` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `sks` int NOT NULL,
  `semester` int NOT NULL,
  PRIMARY KEY (`kode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mata_kuliah`
--

LOCK TABLES `mata_kuliah` WRITE;
/*!40000 ALTER TABLE `mata_kuliah` DISABLE KEYS */;
INSERT INTO `mata_kuliah` VALUES ('MK001','Pemrograman OOP',3,3),('MK002','Basis Data',3,2),('MK003','Pemrograman Web',3,4),('MK004','Jaringan Komputer',3,3),('MK005','Kecerdasan Buatan',3,5),('MK006','Sistem Operasi',2,2),('MK007','Matematika Diskrit',2,1),('MK008','Algoritma Pemrograman',3,1);
/*!40000 ALTER TABLE `mata_kuliah` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT 'admin',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin123','admin','2026-06-17 04:30:47');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-28 14:54:35
