-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Počítač: 127.0.0.1:3306
-- Vytvořeno: Čtv 28. lis 2024, 09:36
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
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `friendship`
--

INSERT INTO `friendship` (`id`, `user_id`, `friend_id`, `created_at`) VALUES
(12, 39, 38, '2024-11-19 16:40:19'),
(13, 38, 39, '2024-11-19 16:40:19');

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
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `friend_request`
--

INSERT INTO `friend_request` (`id`, `from_user_id`, `to_user_id`, `created_at`, `status`) VALUES
(10, 39, 35, '2024-11-19 12:50:51', 'PENDING'),
(2, 30, 32, '2024-10-24 16:06:19', 'PENDING'),
(5, 30, 35, '2024-11-02 15:53:04', 'PENDING');

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
) ENGINE=MyISAM AUTO_INCREMENT=196 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `posts`
--

INSERT INTO `posts` (`id`, `content`, `id_user`, `id_thread`, `created_at`, `updated_at`) VALUES
(195, 'pic', 38, 15, '2024-11-19 18:12:11', '2024-11-19 18:12:11'),
(194, 'gagag', 38, 15, '2024-11-19 17:23:33', '2024-11-19 17:23:33'),
(193, 'a', 39, 16, '2024-11-19 15:06:43', '2024-11-19 15:06:42'),
(192, 'a', 39, 16, '2024-11-19 14:19:32', '2024-11-19 14:19:31'),
(191, 'test', 38, 15, '2024-11-11 16:57:22', '2024-11-11 16:57:22');

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
-- Struktura tabulky `sessions`
--

DROP TABLE IF EXISTS `sessions`;
CREATE TABLE IF NOT EXISTS `sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_accessed` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT NULL,
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_czech_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `session_id` (`session_id`(191)),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=148 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `sessions`
--

INSERT INTO `sessions` (`id`, `session_id`, `user_id`, `created_at`, `last_accessed`, `expires_at`, `ip_address`, `user_agent`) VALUES
(146, 'E1AF90A3FE12DDAC0F2E14045FAD5B02', 38, '2024-11-19 16:40:13', '2024-11-19 18:01:10', '2024-11-19 19:01:10', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0'),
(147, '660F4F8F54DE4072400FE4DEEB9E6FDA', 39, '2024-11-22 07:04:14', '2024-11-22 07:04:14', '2024-11-22 07:34:15', '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0');

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
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `threads`
--

INSERT INTO `threads` (`id`, `name`, `id_user`, `created_at`) VALUES
(15, 'test', 38, '2024-11-11 16:57:12'),
(16, 'asdfaf', 39, '2024-11-19 14:19:23');

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
) ENGINE=MyISAM AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `uploads`
--

INSERT INTO `uploads` (`id`, `filename`, `id_user`, `id_post`, `created_at`) VALUES
(73, 'colorful-abstract-ai-art-4k-wallpaper-uhdpaper.com-16@0@i.jpg', 38, 195, '2024-11-19 18:12:12'),
(72, 'abstract-digital-art-uhdpaper.com-4K-8.2839.jpg', 38, 194, '2024-11-19 17:23:33'),
(71, 'abstract-paint-blue-yellow-digital-art-huawei-mate-background-4k-wallpaper-uhdpaper.com-262@0@f.jpg', 39, 193, '2024-11-19 15:06:43');

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
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `email` (`email`),
  KEY `idRole` (`idRole`)
) ENGINE=MyISAM AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `users`
--

INSERT INTO `users` (`id`, `firstName`, `lastName`, `login`, `password`, `email`, `idRole`, `isBanned`) VALUES
(36, 'test2', 'test2', 'test2', '$2a$10$NpozsYLew6/BCdpQGcaOWegjMbugYa5YbsyiF9B06ktHQAMKUuSlC', 'test2@test', 3, 0),
(39, 'asdfa', 'asdfa', 'asdfa', '$2a$10$sAngU5nG4l0Dd0WxsRdIfOwYf1FdvYHHXQiXbnT3xq5V99nEAMbtG', 'asdfa@asdfa', 1, 0),
(38, 'test', 'test', 'test', '$2a$10$cPcGThM85TYiruEPTay5pOk7iqAnBCbLHmm6BG3fSH6/oAcurpZNS', 'test@test', 2, 0),
(35, 'ljalskdjflůakj', 'afgasfgasgdasdga', 'chanukaděsmandloň', '$2a$10$PbKLdRBgXNzZWxlbWcOP7uiXMnTi5rVnCEHXySp6x4DnRw5mNE7bG', 'hej@hey', 3, 0);

DELIMITER $$
--
-- Události
--
DROP EVENT IF EXISTS `delete_expired_sessions`$$
CREATE DEFINER=`root`@`localhost` EVENT `delete_expired_sessions` ON SCHEDULE EVERY 1 HOUR STARTS '2024-09-07 15:46:55' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM sessions WHERE expires_at < NOW()$$

DELIMITER ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
