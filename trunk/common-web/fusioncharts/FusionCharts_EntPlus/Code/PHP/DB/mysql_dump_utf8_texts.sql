/*
SQLyog Community Edition- MySQL GUI v5.22a
Host - 5.1.36-community-log : Database - sales
*********************************************************************
Server version : 5.1.36-community-log
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

create database if not exists `sales`;

USE `sales`;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `monthly_utf8` */

DROP TABLE IF EXISTS `monthly_utf8`;

CREATE TABLE `monthly_utf8` (
  `month_name` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `amount` decimal(10,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `monthly_utf8` */

insert  into `monthly_utf8`(`month_name`,`amount`) values ('januÃ¡ri','17400'),('FevruÃ¡rios','19800'),('Ù…Ø§Ø±Ø³','2180'),('Ø£Ø¨Ø±ÙŠÙ„','23800'),('äº”æœˆ','29600'),('å…­æœˆ','27600'),('×ªÖ´Ö¼×©××¨Ö´×™','31800'),('MareÅ¡wÄn','39700'),('settÃ¨mbre','37800'),('ottÃ gono','21900'),('novÃ¨mbre','32900'),('dÃ©cembr','39800');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
