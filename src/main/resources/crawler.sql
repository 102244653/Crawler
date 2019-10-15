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

 Date: 15/10/2019 11:10:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appversion
-- ----------------------------
DROP TABLE IF EXISTS `appversion`;
CREATE TABLE `appversion`  (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appversion
-- ----------------------------
INSERT INTO `appversion` VALUES (1, 'login', '5.12.4');

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `websource` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '网站来源',
  `newsid` int(8) NULL DEFAULT NULL COMMENT '原网站文章id',
  `arturl` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文章链接url',
  `arthot` int(10) UNSIGNED NULL DEFAULT NULL COMMENT '原文章热度',
  `tagname` int(4) NULL DEFAULT NULL COMMENT '脉果儿标签id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章标题',
  `olddetail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '原文章详情',
  `newdetail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '格式化之后的文章详情',
  `creattime` datetime(0) NULL DEFAULT NULL COMMENT '添加时间',
  `status` int(1) UNSIGNED ZEROFILL NOT NULL COMMENT '文章状态:0-爬取中，1-待发布[未格式化]，2-待发布[发布失败重发],3-已发布',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of articles
-- ----------------------------
INSERT INTO `articles` VALUES (1, '币世界', 57622, 'https://www.bishijie.com/shendu_57622', 1613, 77, '【分析师看后市】稳中求进，BTC与主流币', '今日依旧有下探8400寻求反弹发力的机会，短线可做好8450-8550的区间高抛低吸。\n[mgeimg:https://img.bishijie.com/news_157075431836904.png?imageView2/1/w/460/h/280/format/jpg/q/75%7Cimageslim]\n非常感谢大家对币世界原创栏目【分析师看后市】的喜爱，现已推出同名深度系列原创栏目。\n\r如果你有喜欢的分析师，请在快讯、深度文章、B圈进行留言，点名你喜欢的分析师。\n\r今天【分析师看后市】为大家请到的分析师是丁佳永，他将为大家讲解未来24小时盘面走势，做出预测，并且给大家操作建议。\n\r夜间BTC领衔主流币震荡蓄力，走势没有发生太大变化，夜间走势没有发生进一步回落，最低在收线前给到了8450附近的多单入场机会，目前走势在震荡中尝试企稳8500一线，日线级别中走势已经回到上行空间，五日均线上扬，与布林带中轨交于8400一线形成支撑，接下来的震荡过程中只要不跌破8400位置，走势就有机会保持向上运行，目前来看今日依旧有下探8400寻求反弹发力的机会，短线可做好8450-8550的区间高抛低吸，去到8400-8450位置后及时多单跟进。\n\r[mgeimg:https://img.bishijie.com/157075431819417.png?imageView2/0/format/png/q/75]\n小时图上来看，走势从昨晚开始窄幅震荡，布林带形成的走势空间较为狭小，MA60均线上扬将要来到8400保护多头的震荡蓄力，后续走势跌破布林带下轨后很可能将触及MA60均线形成反弹，所以不必担心出现较深的回落，上方布林带上轨在8600处形成阻力，震荡中并未对该阻力形成攻势，所以很大可能需要反弹的力量才能完成突破，MACD有低位金叉趋势，保证后续的拉升出现后打开一定的上行空间，四小时级别中布林带开口向上，目前走势受支撑于MA10均线8500，总的来看多头防守较为集中，并且空头并未形成攻势，这种情况下只有在试探至8600附近阻力后才有回落机会，建议空单布局在8550-8600范围，多单布局在8400-8450范围，控制仓位带好止损止盈。\n\r[mgeimg:https://img.bishijie.com/157075431949278.png?imageView2/0/format/png/q/75]\nETH继续围绕190一线整理，近期主流币的反弹较为明显，BTC却保持在低位很长时间才终于有所行动，现阶段将重点由BTC补涨，其他主流币则在整理中企稳关键点位，所以ETH的整理还将持续，操作上重点参考区间震荡，四小时级别走势上行遇阻于195一线，前期阻力185在企稳之前也是通过回落并寻求反弹力量才得以拉升，现阶段前期阻力185一线已经转变为支撑，阻力关注195，近期走势将在该范围内进行调整，展望ETH200关口的朋友需要耐心等待，在这之前可在区间内高抛低吸积累收益，ETH的200关键程度类似于现阶段BTC的9000美元位置，需要一步步完成攻坚，不可操之过急。\n\r', NULL, '2019-10-11 09:51:13', 1);
INSERT INTO `articles` VALUES (42, '大数据中国', 3438, 'https://www.bigdatas.cn/article-3438-1.html', 816, 81, '在马来西亚总理马哈蒂尔见证下，华为技术(', '  \n\r[mgeimg:https://www.bigdatas.cn/data/attachment/portal/201910/08/211523yoixxomoee65ll55.jpg]\n在马来西亚总理马哈蒂尔见证下，华为技术(马来西亚)有限公司与马来西亚明讯(MAXIS)于3日签署了一项在马来西亚建设5G网络的合作协议。\n\r\n\r据介绍，华为将为明讯供应相关设备和服务，双方将通过合作，令5G在马来西亚能更易于部署、运营及引入新的应用。\n\r\n\r明讯此前已于今年2月和华为签署了合作备忘录，并于今年3月开始在马来西亚进行5G网络测试。\n\r\n\r马哈蒂尔对明讯和华为合作支持马来西亚数字经济发展表示高兴。他说，5G技术将连接每个马来西亚人，并成为驱动制造业、农业、医疗保健等关键行业改变的动力，从而使马来西亚继续保持全球竞争力。\n\r\n\r此前，马哈蒂尔今年5月在日本出席“亚洲的未来”国际交流会议时曾表示，华为的技术已大幅领先，马来西亚希望尽可能多采用华为的技术。\n\r', NULL, '2019-10-11 09:57:42', 1);
INSERT INTO `articles` VALUES (52, '今日头条', 1570788203, 'https://www.toutiao.com/group/6746470752200950285/', 711, 61, '臊子面做法，好吃又美味，学会就可以在家自', '臊子面首先是要做出美味可口的臊子呀 ，\n\r1.猪肉肥肉放到锅中开火出油，等油出到差不多和肥肉一样多的时候放入瘦肉。\n\r2.搅拌一会继续出油，差不多十分钟左右，放醋，八角桂皮香叶，酱油，然后再炖差不多二十分钟，能够问道臊子的肉香，然后放盐，后放盐不然瘦煮不烂。\n\r3.出锅臊子就做好了，然后锅中留臊子，倒入水煮开放切好的豆腐，葱，蒜苗以及爱吃的配菜等，放盐调味，煮出香味。\n\r[mgeimg:http://p1.pstatp.com/large/pgc-image/2e7e49d7767548de882f8d696899f8b7]\n4.煮面条面条煮熟后过凉水，捞出碗中，然后，浇上香气腾腾的臊子汤，一碗色香味俱全的臊子面就做好了。\n\r[mgeimg:http://p3.pstatp.com/large/pgc-image/6021308b6d5e4644acd143cbd9408973]\n[mgeimg:http://p1.pstatp.com/large/pgc-image/a5c5614f43ca435294fd37f650622298]\n', NULL, '2019-10-11 18:13:23', 1);

-- ----------------------------
-- Table structure for crawlerrecords
-- ----------------------------
DROP TABLE IF EXISTS `crawlerrecords`;
CREATE TABLE `crawlerrecords`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `websource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '网站来源',
  `tagid` int(11) NOT NULL COMMENT '标签id',
  `status` int(255) NOT NULL COMMENT '是否爬取：0-爬取，1-不爬取',
  `updatetime` date NOT NULL COMMENT '上一次爬取时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of crawlerrecords
-- ----------------------------
INSERT INTO `crawlerrecords` VALUES (1, '今日头条美食', 61, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (2, '金色财经热度', 77, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (3, '56财经', 77, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (4, '金色财经技术', 81, 1, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (5, '投中网', 79, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (6, '今日头条养生', 65, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (7, '金色财经行情', 77, 1, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (8, '金色财经头条', 77, 1, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (9, '金色财经新闻', 77, 1, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (10, '中国数谷', 81, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (11, '金色财经政策', 77, 1, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (12, '投资界', 79, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (13, 'IT199', 81, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (14, '和讯理财', 79, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (15, '慢钱头条', 79, 1, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (16, '天气加', 8, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (17, '今日头条娱乐', 59, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (18, '今日头条科技', 81, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (19, '今日头条游戏', 65, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (20, '今日头条体育', 65, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (21, '今日头条财经', 79, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (22, '今日头条时尚', 63, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (23, '币世界', 77, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (24, '美妆', 40, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (25, '今日头条旅游', 8, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (26, '今日头条军事', 65, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (27, '大数据中国', 81, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (28, '今日头条育儿', 65, 0, '2019-10-10');
INSERT INTO `crawlerrecords` VALUES (29, '糗事百科', 55, 0, '2019-10-10');

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
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ischeck
-- ----------------------------
INSERT INTO `ischeck` VALUES (1, '币世界', 57622, '2019-10-11 09:51:17', 0);
INSERT INTO `ischeck` VALUES (2, '币世界', 57652, '2019-10-11 09:51:19', 0);


SET FOREIGN_KEY_CHECKS = 1;
