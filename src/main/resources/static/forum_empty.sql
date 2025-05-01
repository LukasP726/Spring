-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Počítač: 127.0.0.1:3306
-- Vytvořeno: Čtv 01. kvě 2025, 10:18
-- Verze serveru: 8.3.0
-- Verze PHP: 8.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáze: `forum`
--

-- --------------------------------------------------------

--
-- Struktura tabulky `friendship`
--

DROP TABLE IF EXISTS `friendship`;
CREATE TABLE IF NOT EXISTS `friendship` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `friend_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_friendship` (`user_id`,`friend_id`),
  KEY `friend_id` (`friend_id`)
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `friendship`
--

INSERT INTO `friendship` (`id`, `user_id`, `friend_id`, `created_at`) VALUES
(23, 39, 44, '2025-04-28 15:18:02'),
(22, 44, 39, '2025-04-28 15:18:02');

-- --------------------------------------------------------

--
-- Struktura tabulky `friend_request`
--

DROP TABLE IF EXISTS `friend_request`;
CREATE TABLE IF NOT EXISTS `friend_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `from_user_id` bigint NOT NULL,
  `to_user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('PENDING','ACCEPTED','REJECTED') COLLATE utf8mb4_czech_ci DEFAULT 'PENDING',
  PRIMARY KEY (`id`),
  KEY `from_user_id` (`from_user_id`),
  KEY `to_user_id` (`to_user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

-- --------------------------------------------------------

--
-- Struktura tabulky `posts`
--

DROP TABLE IF EXISTS `posts`;
CREATE TABLE IF NOT EXISTS `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `id_user` int NOT NULL,
  `id_thread` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idUser` (`id_user`),
  KEY `idThread` (`id_thread`)
) ENGINE=MyISAM AUTO_INCREMENT=840 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

-- --------------------------------------------------------

--
-- Struktura tabulky `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `weight` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `role`
--

INSERT INTO `role` (`id`, `name`, `weight`) VALUES
(2, 'Admin', 10),
(3, 'Editor', 5),
(1, 'SuperAdmin', 20);

-- --------------------------------------------------------

--
-- Struktura tabulky `threads`
--

DROP TABLE IF EXISTS `threads`;
CREATE TABLE IF NOT EXISTS `threads` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `id_user` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idUser` (`id_user`)
) ENGINE=MyISAM AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

-- --------------------------------------------------------

--
-- Struktura tabulky `uploads`
--

DROP TABLE IF EXISTS `uploads`;
CREATE TABLE IF NOT EXISTS `uploads` (
  `id` int NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `id_user` int NOT NULL,
  `id_post` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idUser` (`id_user`),
  KEY `idPost` (`id_post`)
) ENGINE=MyISAM AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `uploads`
--

INSERT INTO `uploads` (`id`, `filename`, `id_user`, `id_post`, `created_at`) VALUES
(79, 'smiley-face-emoji-colorful-explosion-digital-art-4k-wallpaper-changed.jpg', 54, 836, '2025-04-29 12:27:27'),
(73, 'colorful-abstract-ai-art-4k-wallpaper-uhdpaper.com-16@0@i.jpg', 38, 195, '2024-11-19 18:12:12'),
(72, 'abstract-digital-art-uhdpaper.com-4K-8.2839.jpg', 38, 194, '2024-11-19 17:23:33'),
(71, 'abstract-paint-blue-yellow-digital-art-huawei-mate-background-4k-wallpaper-uhdpaper.com-262@0@f.jpg', 39, 193, '2024-11-19 15:06:43'),
(78, '4303035-3207430937.jpg', 48, 834, '2025-04-29 10:33:53'),
(77, 'monster-abstract-digital-art-4k-wallpaper-3840x2160-uhdpaper.com-530.0_a.jpg', 44, 828, '2025-04-13 20:10:14'),
(80, '4303035-3207430937.jpg', 55, 837, '2025-04-30 09:05:38'),
(81, 'abstract-paint-blue-yellow-digital-art-huawei-mate-background-4k-wallpaper-uhdpaper.com-262@0@f.jpg', 55, 838, '2025-04-30 09:31:36'),
(82, 'monster-abstract-digital-art-4k-wallpaper-3840x2160-uhdpaper.com-530.0_a.jpg', 66, 839, '2025-04-30 14:18:50');

-- --------------------------------------------------------

--
-- Struktura tabulky `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `lastName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `login` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `idRole` int DEFAULT NULL,
  `isBanned` tinyint(1) DEFAULT '0',
  `uid` varchar(36) COLLATE utf8mb4_czech_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `email` (`email`),
  KEY `idRole` (`idRole`)
) ENGINE=MyISAM AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
