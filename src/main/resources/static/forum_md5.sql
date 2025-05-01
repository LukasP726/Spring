-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Počítač: 127.0.0.1:3306
-- Vytvořeno: Čtv 01. kvě 2025, 11:14
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
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `friendship`
--

INSERT INTO `friendship` (`id`, `user_id`, `friend_id`, `created_at`) VALUES
(23, 39, 44, '2025-04-28 15:18:02'),
(22, 44, 39, '2025-04-28 15:18:02'),
(25, 74, 76, '2025-05-01 10:27:22'),
(24, 76, 74, '2025-05-01 10:27:22');

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
) ENGINE=MyISAM AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `friend_request`
--

INSERT INTO `friend_request` (`id`, `from_user_id`, `to_user_id`, `created_at`, `status`) VALUES
(22, 76, 75, '2025-05-01 10:25:26', 'PENDING'),
(21, 75, 74, '2025-05-01 10:23:31', 'PENDING');

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
) ENGINE=MyISAM AUTO_INCREMENT=843 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `posts`
--

INSERT INTO `posts` (`id`, `content`, `id_user`, `id_thread`, `created_at`, `updated_at`) VALUES
(842, 'SuperAdmin-post', 76, 40, '2025-05-01 10:26:12', '2025-05-01 10:26:11'),
(841, 'editor-post', 75, 39, '2025-05-01 10:23:54', '2025-05-01 10:23:54'),
(840, 'admin-post', 74, 38, '2025-05-01 10:21:28', '2025-05-01 10:21:27');

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
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `threads`
--

INSERT INTO `threads` (`id`, `name`, `id_user`, `created_at`) VALUES
(40, 'SuperAdmin-thread', 76, '2025-05-01 10:25:49'),
(39, 'editor-thread', 75, '2025-05-01 10:23:40'),
(38, 'admin-thread', 74, '2025-05-01 10:21:05');

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
) ENGINE=MyISAM AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `uploads`
--

INSERT INTO `uploads` (`id`, `filename`, `id_user`, `id_post`, `created_at`) VALUES
(83, 'colorful-abstract-ai-art-4k-wallpaper-uhdpaper.com-16@0@i.jpg', 74, 840, '2025-05-01 10:21:28'),
(84, 'smiley-face-emoji-colorful-explosion-digital-art-4k-wallpaper-changed.jpg', 75, 841, '2025-05-01 10:23:54'),
(85, 'colorful-abstract-ai-art-4k-wallpaper-uhdpaper.com-16@0@i.jpg', 76, 842, '2025-05-01 10:26:12');

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
) ENGINE=MyISAM AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_czech_ci;

--
-- Vypisuji data pro tabulku `users`
--

INSERT INTO `users` (`id`, `firstName`, `lastName`, `login`, `password`, `email`, `idRole`, `isBanned`, `uid`) VALUES
(76, 'SuperAdmin', 'SuperAdmin', 'SuperAdmin', '0b28a5799a32c687dad2c5183718ceac', 'SuperAdmin@SuperAdmin', 1, 0, NULL),
(75, 'editor', 'editor', 'editor', '5aee9dbd2a188839105073571bee1b1f', 'editor@editor', 3, 0, NULL),
(74, 'admin', 'admin', 'admin', '21232f297a57a5a743894a0e4a801fc3', 'admin@admin', 2, 0, NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
