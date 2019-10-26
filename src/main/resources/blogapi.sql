UNLOCK TABLES;

DROP TABLE IF EXISTS `post_tag`;
DROP TABLE IF EXISTS `tags`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `posts`;
DROP TABLE IF EXISTS `photos`;
DROP TABLE IF EXISTS `albums`;
DROP TABLE IF EXISTS `todos`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `address`;
DROP TABLE IF EXISTS `company`;
DROP TABLE IF EXISTS `geo`;

CREATE TABLE `tags` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned NOT NULL,
  `updated_by` bigint(19) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `geo` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `lat` varchar(255),
  `lng` varchar(255),
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `company` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255),
  `catch_phrase` varchar(255),
  `bs` varchar(255),
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `address` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `street` varchar(255),
  `suite` varchar(255),
  `city` varchar(255),
  `zipcode` varchar(255),
  `geo_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_geo` (`geo_id`),
  CONSTRAINT `fk_geo` FOREIGN KEY (`geo_id`) REFERENCES `geo` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `users` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `address_id` bigint(19) unsigned DEFAULT NULL,
  `phone` varchar(255),
  `website` varchar(255),
  `company_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_address` (`address_id`),
  KEY `fk_company` (`company_id`),
  CONSTRAINT `fk_address` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `todos` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `completed` boolean default false,
  `user_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_todos` (`user_id`),
  CONSTRAINT `fk_user_todos` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `albums` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `user_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_album` (`user_id`),
  CONSTRAINT `fk_user_album` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `photos` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `thumbnail_url` varchar(255) NOT NULL,
  `album_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_album` (`album_id`),
  CONSTRAINT `fk_album` FOREIGN KEY (`album_id`) REFERENCES `albums` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `posts` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `user_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned DEFAULT NULL,
  `updated_by` bigint(19) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_post` (`user_id`),
  CONSTRAINT `fk_user_post` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `post_tag` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `post_id` bigint(19) unsigned NOT NULL,
  `tag_id` bigint(19) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_posttag_post_id` (`post_id`),
  KEY `fk_posttag_tag_id` (`tag_id`),
  CONSTRAINT `fk_posttag_post_id` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `fk_posttag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `comments` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `post_id` bigint(19) unsigned DEFAULT NULL,
  `user_id` bigint(19) unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(19) unsigned NOT NULL,
  `updated_by` bigint(19) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_comment_post` (`post_id`),
  KEY `fk_comment_user` (`user_id`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `roles` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `user_role` (
  `id` bigint(19) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(19) unsigned NOT NULL,
  `role_id` bigint(19) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_security_user_id` (`user_id`),
  KEY `fk_security_role_id` (`role_id`),
  CONSTRAINT `fk_security_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_security_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

LOCK TABLES `roles` WRITE;
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER');
UNLOCK TABLES;
