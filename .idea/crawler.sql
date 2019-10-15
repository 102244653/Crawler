/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : 127.0.0.1:3306
 Source Schema         : crawler

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 12/09/2019 19:00:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `websource` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '网站来源',
  `newsid` int(8) NULL DEFAULT NULL COMMENT '原网站文章id',
  `arturl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章链接url',
  `arthot` int(8) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '原文章热度',
  `tagname` int(4) NULL DEFAULT NULL COMMENT '脉果儿标签id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章标题',
  `olddetail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '原文章详情',
  `newdetail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '格式化之后的文章详情',
  `creattime` datetime(0) NULL DEFAULT NULL COMMENT '添加时间',
  `status` int(1) UNSIGNED ZEROFILL NOT NULL COMMENT '文章状态:0-爬取中，1-待发布[未格式化]，2-待发布[发布失败重发],3-已发布',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for crawlerrecords
-- ----------------------------
DROP TABLE IF EXISTS `crawlerrecords`;
CREATE TABLE `crawlerrecords`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `websource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '网站来源',
  `updatetime` date NOT NULL COMMENT '上一次爬取时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of crawlerrecords
-- ----------------------------
INSERT INTO `crawlerrecords` VALUES (1, '币世界', '2019-09-12');
INSERT INTO `crawlerrecords` VALUES (2, '金色财经', '2019-09-12');
INSERT INTO `crawlerrecords` VALUES (3, '56财经', '2019-09-12');
INSERT INTO `crawlerrecords` VALUES (4, '投资界', '2019-09-12');
INSERT INTO `crawlerrecords` VALUES (5, '投中网', '2019-09-12');
INSERT INTO `crawlerrecords` VALUES (6, '大数据中国', '2019-09-12');

-- ----------------------------
-- Table structure for ischeck
-- ----------------------------
DROP TABLE IF EXISTS `ischeck`;
CREATE TABLE `ischeck`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `websource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '网站来源',
  `newsid` int(11) NOT NULL COMMENT '原文章id',
  `updatetime` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `status` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '文章状态：0-爬取成功，1-爬取失败',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
