# MySQL tables
#
#--------------------------------------------------------

CREATE DATABASE IF NOT EXISTS `factorydb`;
USE `factorydb`;
ALTER DATABASE DEFAULT CHARACTER SET = utf8;

#
# Table structure for table 'Japanese_Factory_Master'
#

DROP TABLE IF EXISTS `Japanese_Factory_Master`;

CREATE TABLE `Japanese_Factory_Master` (
  `FactoryId` INTEGER NOT NULL AUTO_INCREMENT, 
  `FactoryName` VARCHAR(50), 
  INDEX (`FactoryName`), 
  PRIMARY KEY (`FactoryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table 'Japanese_Factory_Master'
#

INSERT INTO `Japanese_Factory_Master` VALUES (1, '工場 1');
INSERT INTO `Japanese_Factory_Master` VALUES (2, '工場 2');
INSERT INTO `Japanese_Factory_Master` VALUES (3, '工場 3');
# 3 records