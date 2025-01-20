-- MariaDB dump 10.19  Distrib 10.4.27-MariaDB, for Win64 (AMD64)
--
-- Host: booking-trips-do-user-16095558-0.h.db.ondigitalocean.com    Database: movie
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bill_status`
--

DROP TABLE IF EXISTS `bill_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bill_status` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bills`
--

DROP TABLE IF EXISTS `bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bills` (
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `expire_at` datetime(6) NOT NULL,
  `failure` bit(1) DEFAULT NULL,
  `failure_at` datetime(6) DEFAULT NULL,
  `failure_reason` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `payment_at` datetime(6) DEFAULT NULL,
  `payment_url` text COLLATE utf8mb4_general_ci NOT NULL,
  `total` bigint NOT NULL,
  `status_id` int NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7s2dh8hdb6rthogn42qb1a3aw` (`status_id`),
  KEY `FKk8vs7ac9xknv5xp18pdiehpp1` (`user_id`),
  CONSTRAINT `FK7s2dh8hdb6rthogn42qb1a3aw` FOREIGN KEY (`status_id`) REFERENCES `bill_status` (`id`),
  CONSTRAINT `FKk8vs7ac9xknv5xp18pdiehpp1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cinema_status`
--

DROP TABLE IF EXISTS `cinema_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cinema_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cinemas`
--

DROP TABLE IF EXISTS `cinemas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cinemas` (
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `last_modified_by` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `phone_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `slug` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `status_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKl5gn91utvkprdgfnvrnbb991j` (`status_id`),
  CONSTRAINT `FKl5gn91utvkprdgfnvrnbb991j` FOREIGN KEY (`status_id`) REFERENCES `cinema_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `formats`
--

DROP TABLE IF EXISTS `formats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `formats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `caption` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `version` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `slug` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS `genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `genres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hall_status`
--

DROP TABLE IF EXISTS `hall_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hall_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `halls`
--

DROP TABLE IF EXISTS `halls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `halls` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `last_modified_by` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `available_seats` int NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `total_seats` int NOT NULL,
  `cinema_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `status_id` bigint NOT NULL,
  `number_of_rows` int NOT NULL,
  `cols_per_row` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpst04yq0t1iyprvitond7ly34` (`cinema_id`),
  KEY `FKrmxgfnhq19xh2gytthrffwh` (`status_id`),
  CONSTRAINT `FKpst04yq0t1iyprvitond7ly34` FOREIGN KEY (`cinema_id`) REFERENCES `cinemas` (`id`),
  CONSTRAINT `FKrmxgfnhq19xh2gytthrffwh` FOREIGN KEY (`status_id`) REFERENCES `hall_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `extension` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `path` varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
  `movie_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKahtg85eq9yp0v26hmtfr7pts0` (`movie_id`),
  CONSTRAINT `FKahtg85eq9yp0v26hmtfr7pts0` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movie_format`
--

DROP TABLE IF EXISTS `movie_format`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `movie_format` (
  `movie_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `format_id` bigint NOT NULL,
  PRIMARY KEY (`movie_id`,`format_id`),
  KEY `FKfinrg5ol8dp6c6ypemnsmqupn` (`format_id`),
  CONSTRAINT `FKfinrg5ol8dp6c6ypemnsmqupn` FOREIGN KEY (`format_id`) REFERENCES `formats` (`id`),
  CONSTRAINT `FKoxc87pycbnla9pa59kub6li39` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movie_genre`
--

DROP TABLE IF EXISTS `movie_genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `movie_genre` (
  `movie_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `genre_id` bigint NOT NULL,
  PRIMARY KEY (`movie_id`,`genre_id`),
  KEY `FK3pdaf1ai9eafeypc7qe401l07` (`genre_id`),
  CONSTRAINT `FK3pdaf1ai9eafeypc7qe401l07` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`),
  CONSTRAINT `FKg7f38h6umffo51no9ywq91438` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movies`
--

DROP TABLE IF EXISTS `movies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `movies` (
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `last_modified_by` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age_restriction` int NOT NULL,
  `description` text COLLATE utf8mb4_general_ci NOT NULL,
  `director` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `end_date` date NOT NULL,
  `horizontal_poster` varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
  `language` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `number_of_ratings` int NOT NULL,
  `performers` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `poster` varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
  `producer` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `release_date` date NOT NULL,
  `running_time` int NOT NULL,
  `slug` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `sub_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `sum_of_ratings` int NOT NULL,
  `trailer` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `status_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp1vkgxjs3ifhxelqlmt4hkhod` (`status_id`),
  CONSTRAINT `FKp1vkgxjs3ifhxelqlmt4hkhod` FOREIGN KEY (`status_id`) REFERENCES `movies_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movies_status`
--

DROP TABLE IF EXISTS `movies_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `movies_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `slug` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seats`
--

DROP TABLE IF EXISTS `seats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `last_modified_by` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `hall_id` bigint NOT NULL,
  `seat_type_id` int NOT NULL,
  `name` varchar(3) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `curr_row` int NOT NULL,
  `curr_col` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3jtfe0f60bcpbavj4mctjeasw` (`hall_id`),
  KEY `FKa9mhphe6vkamn88019uw9gnnj` (`seat_type_id`),
  CONSTRAINT `FK3jtfe0f60bcpbavj4mctjeasw` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`id`),
  CONSTRAINT `FKa9mhphe6vkamn88019uw9gnnj` FOREIGN KEY (`seat_type_id`) REFERENCES `seats_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18341 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seats_type`
--

DROP TABLE IF EXISTS `seats_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seats_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `price` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shows`
--

DROP TABLE IF EXISTS `shows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shows` (
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `create_by` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `last_modified_by` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `running_time` int NOT NULL,
  `start_date` date NOT NULL,
  `start_time` time(6) NOT NULL,
  `status` bit(1) NOT NULL,
  `format_id` bigint NOT NULL,
  `hall_id` bigint NOT NULL,
  `movie_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfnrxho6ff6l02ebv09cu95udg` (`format_id`),
  KEY `FK1qa9jl0gpbx6070ugpi5l6653` (`hall_id`),
  KEY `FKqdpwhiv5r3lx844pct0eudapk` (`movie_id`),
  CONSTRAINT `FK1qa9jl0gpbx6070ugpi5l6653` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`id`),
  CONSTRAINT `FKfnrxho6ff6l02ebv09cu95udg` FOREIGN KEY (`format_id`) REFERENCES `formats` (`id`),
  CONSTRAINT `FKqdpwhiv5r3lx844pct0eudapk` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tickets` (
  `ticket_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `bill_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `seat` bigint NOT NULL,
  `showtime_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `FKaqpjta99er6ahhyyq5689cyw4` (`bill_id`),
  KEY `FK1yk28jf26hn14kr1pdy3y1oeu` (`seat`),
  KEY `FK3xiadh8augoyhs3hodhtgciac` (`showtime_id`),
  CONSTRAINT `FK1yk28jf26hn14kr1pdy3y1oeu` FOREIGN KEY (`seat`) REFERENCES `seats` (`id`),
  CONSTRAINT `FK3xiadh8augoyhs3hodhtgciac` FOREIGN KEY (`showtime_id`) REFERENCES `shows` (`id`),
  CONSTRAINT `FKaqpjta99er6ahhyyq5689cyw4` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `create_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `gender` enum('FEMALE','MALE','UNKNOWN') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(500) COLLATE utf8mb4_general_ci NOT NULL,
  `phone_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `verify` bit(1) NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-06 23:20:46
