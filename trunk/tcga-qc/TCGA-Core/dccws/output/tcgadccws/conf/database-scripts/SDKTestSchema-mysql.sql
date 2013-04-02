/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema cacoresdk
--

CREATE DATABASE IF NOT EXISTS cacoresdk;
USE cacoresdk;

--
-- Definition of table `address`
--

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` int(8) NOT NULL,
  `zip` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `address`
--

/*!40000 ALTER TABLE `address` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `address` (`id`,`zip`) VALUES 
 (1,'Zip1'),
 (2,'Zip2'),
 (3,'Zip3'),
 (4,'Zip4'),
 (5,'Zip5');
COMMIT;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;


--
-- Definition of table `album`
--

DROP TABLE IF EXISTS `album`;
CREATE TABLE `album` (
  `id` decimal(8,2) NOT NULL,
  `title` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `album`
--

/*!40000 ALTER TABLE `album` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `album` (`id`,`title`) VALUES 
 ('1.00','Venetian Oboe Concertos'),
 ('2.00','The Cello');
COMMIT;
/*!40000 ALTER TABLE `album` ENABLE KEYS */;


--
-- Definition of table `album_song`
--

DROP TABLE IF EXISTS `album_song`;
CREATE TABLE `album_song` (
  `album_id` int(8) NOT NULL,
  `song_id` int(8) NOT NULL,
  PRIMARY KEY  (`album_id`,`song_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `album_song`
--

/*!40000 ALTER TABLE `album_song` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `album_song` (`album_id`,`song_id`) VALUES 
 (1,1),
 (1,2),
 (1,3),
 (1,4),
 (1,5),
 (1,6),
 (2,7),
 (2,8),
 (2,9),
 (2,10),
 (2,11),
 (3,14),
 (4,15),
 (6,17),
 (7,18);
COMMIT;
/*!40000 ALTER TABLE `album_song` ENABLE KEYS */;


--
-- Definition of table `all_data_type`
--

DROP TABLE IF EXISTS `all_data_type`;
CREATE TABLE `all_data_type` (
  `id` int(8) NOT NULL,
  `int_value` int(8) default NULL,
  `string_value` varchar(50) default NULL,
  `double_value` decimal(8,2) default NULL,
  `float_value` decimal(8,2) default NULL,
  `date_value` datetime default NULL,
  `boolean_value` varchar(1) default NULL,
  `clob_value` longtext,
  `character_value` char(1) default NULL,
  `long_value` decimal(38,0) default NULL,
  `double_primitive_value` decimal(8,2) default NULL,
  `int_primitive_value` int(8) default NULL,
  `date_primitive_value` datetime default NULL,
  `string_primitive_value` varchar(50) default NULL,
  `float_primitive_value` decimal(8,2) default NULL,
  `boolean_primitive_value` varchar(1) default NULL,
  `character_primitive_value` char(1) default NULL,
  `long_primitive_value` decimal(38,0) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `all_data_type`
--

/*!40000 ALTER TABLE `all_data_type` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `all_data_type` (`id`,`int_value`,`string_value`,`double_value`,`float_value`,`date_value`,`boolean_value`,`clob_value`,`character_value`,`long_value`,`double_primitive_value`,`int_primitive_value`,`date_primitive_value`,`string_primitive_value`,`float_primitive_value`,`boolean_primitive_value`,`character_primitive_value`,`long_primitive_value`) VALUES 
 (1,-1,',./-+/*&&()||==\'\"%\"!\\','-1.10','1.10','2011-11-11 00:00:00','1','0123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340112340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012','a','1000001','10001.00',11,'2007-10-01 00:00:00','abc','10.01','1','a','1000001'),
 (2,0,'\'Steve\'s Test\'','0.00','222.22','2012-12-12 00:00:00','0','0123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340112340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012','b','1000002','10002.00',12,'2007-10-02 00:00:00','def','10.02','0','b','1000002'),
 (3,1,'~!@#$%^&*()_+-={}|:\"<>?[]\\;\',./-+/*&&()||==\'\"%\"!\\\'','1.10','333.33','2003-03-03 00:00:00','1','0123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340112340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012','c','1000003','10003.00',13,'2007-10-03 00:00:00','ghi','10.03','1','c','1000003'),
 (4,10000,'01234567890123456789012345678901234567890123456789','10000.00','444.44','2004-04-04 00:00:00','0','0123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340112340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012','d','1000004','10004.00',14,'2007-10-04 00:00:00','jkl','10.04','0','d','1000004'),
 (5,5,'String_Value5','555.55','555.55','2005-05-05 00:00:00','1','0123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340112340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012340123456789012','e','1000005','10005.00',15,'2007-10-05 00:00:00','mno','10.05','1','e','1000005');
COMMIT;
/*!40000 ALTER TABLE `all_data_type` ENABLE KEYS */;


--
-- Definition of table `all_data_type_string_coll`
--

DROP TABLE IF EXISTS `all_data_type_string_coll`;
CREATE TABLE `all_data_type_string_coll` (
  `all_data_type_id` int(8) NOT NULL,
  `string_value` varchar(50) default NULL,
  KEY `fk_all_data_type_all_data_type` (`all_data_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `all_data_type_string_coll`
--

/*!40000 ALTER TABLE `all_data_type_string_coll` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `all_data_type_string_coll` (`all_data_type_id`,`string_value`) VALUES 
 (1,'String_Collection 1'),
 (1,'String_Collection 2'),
 (1,'String_Collection 3');
COMMIT;
/*!40000 ALTER TABLE `all_data_type_string_coll` ENABLE KEYS */;


--
-- Definition of table `all_validation_type`
--

DROP TABLE IF EXISTS `all_validation_type`;
CREATE TABLE `all_validation_type` (
  `id` int(8) NOT NULL,
  `email` varchar(50) default NULL,
  `future` datetime default NULL,
  `length` varchar(50) default NULL,
  `max_numeric` decimal(22,0) default NULL,
  `past` datetime default NULL,
  `max_string` varchar(50) default NULL,
  `min_numeric` decimal(22,0) default NULL,
  `min_string` varchar(50) default NULL,
  `not_empty` varchar(50) default NULL,
  `not_null` varchar(50) default NULL,
  `range_string` varchar(50) default NULL,
  `range_numeric` decimal(22,0) default NULL,
  `pattern` varchar(50) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `sys_c0068335` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `all_validation_type`
--

/*!40000 ALTER TABLE `all_validation_type` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `all_validation_type` (`id`,`email`,`future`,`length`,`max_numeric`,`past`,`max_string`,`min_numeric`,`min_string`,`not_empty`,`not_null`,`range_string`,`range_numeric`,`pattern`) VALUES 
 (1,'name@mail.nih.gov','2008-05-15 00:00:00','123','999',NULL,'999','1','1','abc','abc','3','3','cat'),
 (10,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'asfdasdf'),
 (11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'asfdasdf',NULL,'asfdasdf'),
 (12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Invalid Value',NULL,'DEPT');
COMMIT;
/*!40000 ALTER TABLE `all_validation_type` ENABLE KEYS */;


--
-- Definition of table `assistant`
--

DROP TABLE IF EXISTS `assistant`;
CREATE TABLE `assistant` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `professor_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_assistant_professor` (`professor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assistant`
--

/*!40000 ALTER TABLE `assistant` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `assistant` (`id`,`name`,`professor_id`) VALUES 
 (1,'Assistant_Name1',1),
 (2,'Assistant_Name2',2),
 (3,'Assistant_Name3',3),
 (4,'Assistant_Name4',6),
 (5,'Assistant_Name5',7),
 (6,'Assistant_Name6',7),
 (7,'Assistant_Name7',11),
 (8,'Assistant_Name8',12),
 (9,'Assistant_Name9',12);
COMMIT;
/*!40000 ALTER TABLE `assistant` ENABLE KEYS */;


--
-- Definition of table `assistant_professor`
--

DROP TABLE IF EXISTS `assistant_professor`;
CREATE TABLE `assistant_professor` (
  `professor_id` int(4) NOT NULL,
  `joining_year` int(4) default NULL,
  PRIMARY KEY  (`professor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `assistant_professor`
--

/*!40000 ALTER TABLE `assistant_professor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `assistant_professor` (`professor_id`,`joining_year`) VALUES 
 (11,11),
 (12,12),
 (13,13),
 (14,14),
 (15,15);
COMMIT;
/*!40000 ALTER TABLE `assistant_professor` ENABLE KEYS */;


--
-- Definition of table `associate_professor`
--

DROP TABLE IF EXISTS `associate_professor`;
CREATE TABLE `associate_professor` (
  `professor_id` int(8) NOT NULL,
  `years_served` int(4) default NULL,
  PRIMARY KEY  (`professor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `associate_professor`
--

/*!40000 ALTER TABLE `associate_professor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `associate_professor` (`professor_id`,`years_served`) VALUES 
 (6,6),
 (7,7),
 (8,8),
 (9,9),
 (10,10);
COMMIT;
/*!40000 ALTER TABLE `associate_professor` ENABLE KEYS */;


--
-- Definition of table `author`
--

DROP TABLE IF EXISTS `author`;
CREATE TABLE `author` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `author`
--

/*!40000 ALTER TABLE `author` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `author` (`id`,`name`) VALUES 
 (1,'Author1'),
 (2,'Author2'),
 (3,'Author3'),
 (4,'Author4'),
 (5,'Author5');
COMMIT;
/*!40000 ALTER TABLE `author` ENABLE KEYS */;


--
-- Definition of table `author_book`
--

DROP TABLE IF EXISTS `author_book`;
CREATE TABLE `author_book` (
  `author_id` int(8) NOT NULL,
  `book_id` int(8) NOT NULL,
  PRIMARY KEY  (`author_id`,`book_id`),
  KEY `fk_author_book_book` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `author_book`
--

/*!40000 ALTER TABLE `author_book` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `author_book` (`author_id`,`book_id`) VALUES 
 (1,1),
 (2,2),
 (3,3);
COMMIT;
/*!40000 ALTER TABLE `author_book` ENABLE KEYS */;


--
-- Definition of table `bag`
--

DROP TABLE IF EXISTS `bag`;
CREATE TABLE `bag` (
  `id` int(8) NOT NULL,
  `style` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bag`
--

/*!40000 ALTER TABLE `bag` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `bag` (`id`,`style`) VALUES 
 (1,'Baguettes'),
 (2,'Barrel'),
 (3,'Beach'),
 (4,'Bowler'),
 (5,'Bucket'),
 (6,'Duffel'),
 (7,'Evening'),
 (8,'Flap'),
 (9,'Hobos'),
 (10,'Pochettes'),
 (11,'Satchel');
COMMIT;
/*!40000 ALTER TABLE `bag` ENABLE KEYS */;


--
-- Definition of table `bag_handle`
--

DROP TABLE IF EXISTS `bag_handle`;
CREATE TABLE `bag_handle` (
  `bag_id` int(8) NOT NULL,
  `handle_id` int(8) NOT NULL,
  PRIMARY KEY  (`bag_id`,`handle_id`),
  UNIQUE KEY `uq_bag_handle_bag_id` (`bag_id`),
  UNIQUE KEY `uq_bag_handle_handle_id` (`handle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bag_handle`
--

/*!40000 ALTER TABLE `bag_handle` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `bag_handle` (`bag_id`,`handle_id`) VALUES 
 (1,1),
 (2,2),
 (3,3),
 (4,4),
 (5,5),
 (6,6),
 (7,7),
 (8,8),
 (9,9),
 (10,10);
COMMIT;
/*!40000 ALTER TABLE `bag_handle` ENABLE KEYS */;


--
-- Definition of table `bank`
--

DROP TABLE IF EXISTS `bank`;
CREATE TABLE `bank` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bank`
--

/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `bank` (`id`,`name`) VALUES 
 (1,'Bank1'),
 (2,'Bank2'),
 (3,'Bank3'),
 (4,'Bank4');
COMMIT;
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;


--
-- Definition of table `book`
--

DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `book`
--

/*!40000 ALTER TABLE `book` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `book` (`id`,`name`) VALUES 
 (1,'Book1'),
 (2,'Book2'),
 (3,'Book3'),
 (4,'Book4'),
 (5,'Book5');
COMMIT;
/*!40000 ALTER TABLE `book` ENABLE KEYS */;


--
-- Definition of table `bride`
--

DROP TABLE IF EXISTS `bride`;
CREATE TABLE `bride` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bride`
--

/*!40000 ALTER TABLE `bride` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `bride` (`id`,`name`) VALUES 
 (1,'Bride 1'),
 (2,'Bride 2'),
 (3,'Bride 3'),
 (4,'Bride 4');
COMMIT;
/*!40000 ALTER TABLE `bride` ENABLE KEYS */;


--
-- Definition of table `bride_father_in_law`
--

DROP TABLE IF EXISTS `bride_father_in_law`;
CREATE TABLE `bride_father_in_law` (
  `bride_id` int(8) NOT NULL,
  `in_law_id` int(8) NOT NULL,
  PRIMARY KEY  (`bride_id`,`in_law_id`),
  UNIQUE KEY `uq_bride_father_in_l_bride_id` (`bride_id`),
  UNIQUE KEY `uq_bride_father_in__in_law_id` (`in_law_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bride_father_in_law`
--

/*!40000 ALTER TABLE `bride_father_in_law` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `bride_father_in_law` (`bride_id`,`in_law_id`) VALUES 
 (1,1),
 (2,3);
COMMIT;
/*!40000 ALTER TABLE `bride_father_in_law` ENABLE KEYS */;


--
-- Definition of table `bride_mother_in_law`
--

DROP TABLE IF EXISTS `bride_mother_in_law`;
CREATE TABLE `bride_mother_in_law` (
  `bride_d` int(8) NOT NULL,
  `in_law_id` int(8) NOT NULL,
  PRIMARY KEY  (`bride_d`,`in_law_id`),
  UNIQUE KEY `uq_bride_mother_in_la_bride_d` (`bride_d`),
  UNIQUE KEY `uq_bride_mother_in__in_law_id` (`in_law_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `bride_mother_in_law`
--

/*!40000 ALTER TABLE `bride_mother_in_law` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `bride_mother_in_law` (`bride_d`,`in_law_id`) VALUES 
 (1,2),
 (3,5);
COMMIT;
/*!40000 ALTER TABLE `bride_mother_in_law` ENABLE KEYS */;


--
-- Definition of table `button`
--

DROP TABLE IF EXISTS `button`;
CREATE TABLE `button` (
  `id` int(8) NOT NULL,
  `holes` int(8) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `button`
--

/*!40000 ALTER TABLE `button` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `button` (`id`,`holes`) VALUES 
 (1,2),
 (2,4);
COMMIT;
/*!40000 ALTER TABLE `button` ENABLE KEYS */;


--
-- Definition of table `calculator`
--

DROP TABLE IF EXISTS `calculator`;
CREATE TABLE `calculator` (
  `id` int(8) NOT NULL,
  `discriminator` varchar(50) default NULL,
  `brand` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `calculator`
--

/*!40000 ALTER TABLE `calculator` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `calculator` (`id`,`discriminator`,`brand`) VALUES 
 (1,'financial','NCR'),
 (2,'scientific','Texas Instruments'),
 (3,'graphics','HP');
COMMIT;
/*!40000 ALTER TABLE `calculator` ENABLE KEYS */;


--
-- Definition of table `card`
--

DROP TABLE IF EXISTS `card`;
CREATE TABLE `card` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `suit_id` int(8) default NULL,
  `image` longtext,
  PRIMARY KEY  (`id`),
  KEY `fk_card_suit` (`suit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `card`
--

/*!40000 ALTER TABLE `card` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `card` (`id`,`name`,`suit_id`,`image`) VALUES 
 (1,'Ace',1,'My Ace'),
 (2,'Two',1,NULL),
 (3,'Three',1,NULL),
 (4,'Four',1,NULL),
 (5,'Five',1,NULL),
 (6,'Six',1,NULL),
 (7,'Seven',1,NULL),
 (8,'Eight',1,NULL),
 (9,'Nine',1,NULL),
 (10,'Ten',1,NULL),
 (11,'Jack',1,NULL),
 (12,'Queen',1,NULL),
 (13,'King',1,NULL),
 (14,'Ace',2,NULL),
 (15,'Two',2,NULL),
 (16,'Three',2,NULL),
 (17,'Four',2,NULL),
 (18,'Five',2,NULL),
 (19,'Six',2,NULL),
 (20,'Seven',2,NULL),
 (21,'Eight',2,NULL),
 (22,'Nine',2,NULL),
 (23,'Ten',2,NULL),
 (24,'Jack',2,NULL),
 (25,'Queen',2,NULL),
 (26,'King',2,NULL),
 (27,'Ace',3,NULL),
 (28,'Two',3,NULL),
 (29,'Three',3,NULL),
 (30,'Four',3,NULL),
 (31,'Five',3,NULL),
 (32,'Six',3,NULL),
 (33,'Seven',3,NULL),
 (34,'Eight',3,NULL),
 (35,'Nine',3,NULL),
 (36,'Ten',3,NULL),
 (37,'Jack',3,NULL),
 (38,'Queen',3,NULL),
 (39,'King',3,NULL),
 (40,'Ace',4,NULL),
 (41,'Two',4,NULL),
 (42,'Three',4,NULL),
 (43,'Four',4,NULL),
 (44,'Five',4,NULL),
 (45,'Six',4,NULL),
 (46,'Seven',4,NULL),
 (47,'Eight',4,NULL),
 (48,'Nine',4,NULL),
 (49,'Ten',4,NULL),
 (50,'Jack',4,NULL),
 (51,'Queen',4,NULL),
 (52,'King',4,NULL),
 (53,'Joker',NULL,NULL);
COMMIT;
/*!40000 ALTER TABLE `card` ENABLE KEYS */;


--
-- Definition of table `cash`
--

DROP TABLE IF EXISTS `cash`;
CREATE TABLE `cash` (
  `payment_id` int(8) NOT NULL,
  PRIMARY KEY  (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cash`
--

/*!40000 ALTER TABLE `cash` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `cash` (`payment_id`) VALUES 
 (1),
 (2);
COMMIT;
/*!40000 ALTER TABLE `cash` ENABLE KEYS */;


--
-- Definition of table `chain`
--

DROP TABLE IF EXISTS `chain`;
CREATE TABLE `chain` (
  `id` int(8) NOT NULL,
  `metal` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chain`
--

/*!40000 ALTER TABLE `chain` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `chain` (`id`,`metal`) VALUES 
 (1,'Gold'),
 (2,'Silver'),
 (3,'Bronze');
COMMIT;
/*!40000 ALTER TABLE `chain` ENABLE KEYS */;


--
-- Definition of table `chain_pendant`
--

DROP TABLE IF EXISTS `chain_pendant`;
CREATE TABLE `chain_pendant` (
  `chain_id` int(8) NOT NULL,
  `pendant_id` int(8) NOT NULL,
  PRIMARY KEY  (`chain_id`,`pendant_id`),
  UNIQUE KEY `uq_chain_pendant_chain_id` (`chain_id`),
  UNIQUE KEY `uq_chain_pendant_pendant_id` (`pendant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chain_pendant`
--

/*!40000 ALTER TABLE `chain_pendant` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `chain_pendant` (`chain_id`,`pendant_id`) VALUES 
 (1,1),
 (2,2);
COMMIT;
/*!40000 ALTER TABLE `chain_pendant` ENABLE KEYS */;


--
-- Definition of table `character_key`
--

DROP TABLE IF EXISTS `character_key`;
CREATE TABLE `character_key` (
  `id` char(1) NOT NULL default '',
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `character_key`
--

/*!40000 ALTER TABLE `character_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `character_key` (`id`,`name`) VALUES 
 ('9','CharacterKey_Name 9'),
 (';','CharacterKey _Name;'),
 ('a','CharacterKey_Name a'),
 ('B','CharacterKey_Name B');
COMMIT;
/*!40000 ALTER TABLE `character_key` ENABLE KEYS */;


--
-- Definition of table `character_primitive_key`
--

DROP TABLE IF EXISTS `character_primitive_key`;
CREATE TABLE `character_primitive_key` (
  `id` char(1) NOT NULL default '',
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `character_primitive_key`
--

/*!40000 ALTER TABLE `character_primitive_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `character_primitive_key` (`id`,`name`) VALUES 
 ('6','Character_Primitive_Key_Name 6'),
 ('d','Character_Primitive_Key_Name d'),
 ('L','Character_Primitive_Key_Name L'),
 ('[','Character_Primitive_Key_Name [');
COMMIT;
/*!40000 ALTER TABLE `character_primitive_key` ENABLE KEYS */;


--
-- Definition of table `chef`
--

DROP TABLE IF EXISTS `chef`;
CREATE TABLE `chef` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `restaurant_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_chef_restaurant` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `chef`
--

/*!40000 ALTER TABLE `chef` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `chef` (`id`,`name`,`restaurant_id`) VALUES 
 (1,'Chef1',1),
 (2,'Chef2',2),
 (3,'Chef3',2),
 (4,'Chef4',NULL);
COMMIT;
/*!40000 ALTER TABLE `chef` ENABLE KEYS */;


--
-- Definition of table `child`
--

DROP TABLE IF EXISTS `child`;
CREATE TABLE `child` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `father_id` int(8) default NULL,
  `mother_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uq_child_father_id` (`father_id`),
  UNIQUE KEY `uq_child_mother_id` (`mother_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `child`
--

/*!40000 ALTER TABLE `child` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `child` (`id`,`name`,`father_id`,`mother_id`) VALUES 
 (1,'Child_Name1',1,2),
 (2,'Child_Name2',3,4),
 (3,'Child_Name3',5,NULL),
 (4,'Child_Name4',NULL,6),
 (5,'Child_Name5',NULL,NULL);
COMMIT;
/*!40000 ALTER TABLE `child` ENABLE KEYS */;


--
-- Definition of table `computer`
--

DROP TABLE IF EXISTS `computer`;
CREATE TABLE `computer` (
  `id` int(8) NOT NULL,
  `type` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `computer`
--

/*!40000 ALTER TABLE `computer` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `computer` (`id`,`type`) VALUES 
 (1,'Computer_Type1'),
 (2,'Computer_Type2'),
 (3,'Computer_Type3'),
 (4,'Computer_Type4'),
 (5,'Computer_Type5');
COMMIT;
/*!40000 ALTER TABLE `computer` ENABLE KEYS */;


--
-- Definition of table `credit`
--

DROP TABLE IF EXISTS `credit`;
CREATE TABLE `credit` (
  `payment_id` int(8) NOT NULL,
  `card_number` varchar(50) default NULL,
  `bank_id` int(8) default NULL,
  PRIMARY KEY  (`payment_id`),
  KEY `fk_credit_bank` (`bank_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `credit`
--

/*!40000 ALTER TABLE `credit` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `credit` (`payment_id`,`card_number`,`bank_id`) VALUES 
 (3,'3',3),
 (4,'4',4);
COMMIT;
/*!40000 ALTER TABLE `credit` ENABLE KEYS */;


--
-- Definition of table `crt_monitor`
--

DROP TABLE IF EXISTS `crt_monitor`;
CREATE TABLE `crt_monitor` (
  `monitor_id` int(8) NOT NULL,
  `refresh_rate` int(8) default NULL,
  PRIMARY KEY  (`monitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `crt_monitor`
--

/*!40000 ALTER TABLE `crt_monitor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `crt_monitor` (`monitor_id`,`refresh_rate`) VALUES 
 (1,45);
COMMIT;
/*!40000 ALTER TABLE `crt_monitor` ENABLE KEYS */;

--
-- Definition of table `currency`
--

DROP TABLE IF EXISTS `currency`;
CREATE TABLE `currency` (
  `id` int(8) NOT NULL,
  `discriminator` varchar(50) default NULL,
  `country` varchar(50) default NULL,
  `value` int(8) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `currency`
--

/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `currency` (`id`,`discriminator`,`country`,`value`) VALUES 
 (1,'Note','USA',1),
 (2,'Note','Germany',2),
 (3,'Note','Spain',3);
COMMIT;
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;


--
-- Definition of table `deck`
--

DROP TABLE IF EXISTS `deck`;
CREATE TABLE `deck` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `deck`
--

/*!40000 ALTER TABLE `deck` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `deck` (`id`,`name`) VALUES 
 (1,'My Deck 1');
COMMIT;
/*!40000 ALTER TABLE `deck` ENABLE KEYS */;


--
-- Definition of table `designer`
--

DROP TABLE IF EXISTS `designer`;
CREATE TABLE `designer` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `designer`
--

/*!40000 ALTER TABLE `designer` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `designer` (`id`,`name`) VALUES 
 (1,'Gucci'),
 (2,'Prada'),
 (3,'Sergio Rossi');
COMMIT;
/*!40000 ALTER TABLE `designer` ENABLE KEYS */;


--
-- Definition of table `dessert`
--

DROP TABLE IF EXISTS `dessert`;
CREATE TABLE `dessert` (
  `id` int(8) NOT NULL,
  `topping` varchar(50) default NULL,
  `filling` varchar(50) default NULL,
  `discriminator` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dessert`
--

/*!40000 ALTER TABLE `dessert` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `dessert` (`id`,`topping`,`filling`,`discriminator`) VALUES 
 (1,'Peanuts',NULL,'IceCream'),
 (2,'Sprinkles',NULL,'IceCream'),
 (3,NULL,'Apples','Pie'),
 (4,NULL,'Cherries','Pie');
COMMIT;
/*!40000 ALTER TABLE `dessert` ENABLE KEYS */;


--
-- Definition of table `dessert_utensil`
--

DROP TABLE IF EXISTS `dessert_utensil`;
CREATE TABLE `dessert_utensil` (
  `dessert_id` int(8) NOT NULL,
  `utensil_id` int(8) NOT NULL,
  PRIMARY KEY  (`dessert_id`,`utensil_id`),
  KEY `fk_dessert_utensil_utensil` (`utensil_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dessert_utensil`
--

/*!40000 ALTER TABLE `dessert_utensil` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `dessert_utensil` (`dessert_id`,`utensil_id`) VALUES 
 (1,1),
 (3,1),
 (2,2),
 (3,2),
 (4,2),
 (1,3),
 (4,3);
COMMIT;
/*!40000 ALTER TABLE `dessert_utensil` ENABLE KEYS */;


--
-- Definition of table `display`
--

DROP TABLE IF EXISTS `display`;
CREATE TABLE `display` (
  `id` int(8) NOT NULL,
  `width` int(8) default NULL,
  `height` int(8) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `display`
--

/*!40000 ALTER TABLE `display` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `display` (`id`,`width`,`height`) VALUES 
 (1,1,1),
 (2,2,2),
 (3,3,3),
 (4,4,4),
 (5,5,5);
COMMIT;
/*!40000 ALTER TABLE `display` ENABLE KEYS */;


--
-- Definition of table `dog`
--

DROP TABLE IF EXISTS `dog`;
CREATE TABLE `dog` (
  `id` int(8) NOT NULL,
  `breed` varchar(50) default NULL,
  `gender` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dog`
--

/*!40000 ALTER TABLE `dog` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `dog` (`id`,`breed`,`gender`) VALUES 
 (1,'Poodle','Male'),
 (2,'Chihuahua','Female'),
 (3,'St. Bernard','Male');
COMMIT;
/*!40000 ALTER TABLE `dog` ENABLE KEYS */;


--
-- Definition of table `double_key`
--

DROP TABLE IF EXISTS `double_key`;
CREATE TABLE `double_key` (
  `id` decimal(8,2) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `double_key`
--

/*!40000 ALTER TABLE `double_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `double_key` (`id`,`name`) VALUES 
 ('1.10','Double_Key_Name1.1'),
 ('2.20','Double_Key_Name2.2'),
 ('3.30','Double_Key_Name3.3'),
 ('4.40','Double_Key_Name4.4'),
 ('5.50','Double_Key_Name5.5');
COMMIT;
/*!40000 ALTER TABLE `double_key` ENABLE KEYS */;


--
-- Definition of table `double_primitive_key`
--

DROP TABLE IF EXISTS `double_primitive_key`;
CREATE TABLE `double_primitive_key` (
  `id` decimal(8,2) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `double_primitive_key`
--

/*!40000 ALTER TABLE `double_primitive_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `double_primitive_key` (`id`,`name`) VALUES 
 ('1.10','Double_Primitive_Key 1.1'),
 ('2.20','Double_Primitive_Key 2.2');
COMMIT;
/*!40000 ALTER TABLE `double_primitive_key` ENABLE KEYS */;


--
-- Definition of table `element`
--

DROP TABLE IF EXISTS `element`;
CREATE TABLE `element` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `parent_element_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uq_element_parent_element_id` (`parent_element_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `element`
--

/*!40000 ALTER TABLE `element` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `element` (`id`,`name`,`parent_element_id`) VALUES 
 (1,'Name1',NULL),
 (2,'Name2',1),
 (3,'Element',NULL),
 (4,'Element',NULL);
COMMIT;
/*!40000 ALTER TABLE `element` ENABLE KEYS */;


--
-- Definition of table `employee`
--

DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `employee`
--

/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `employee` (`id`,`name`) VALUES 
 (1,'Employee_Name1'),
 (2,'Employee_Name2'),
 (3,'Employee_Name3'),
 (4,'Employee_Name4'),
 (5,'Employee_Name5'),
 (6,'Employee_Name6'),
 (7,'Employee_Name7'),
 (8,'Employee_Name8'),
 (9,'Employee_Name9'),
 (10,'Employee_Name10');
COMMIT;
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;


--
-- Definition of table `employee_project`
--

DROP TABLE IF EXISTS `employee_project`;
CREATE TABLE `employee_project` (
  `employee_id` int(8) NOT NULL,
  `project_id` int(8) NOT NULL,
  PRIMARY KEY  (`employee_id`,`project_id`),
  KEY `fk_employee_project_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `employee_project`
--

/*!40000 ALTER TABLE `employee_project` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `employee_project` (`employee_id`,`project_id`) VALUES 
 (1,1),
 (2,2),
 (3,2),
 (4,4),
 (4,5),
 (6,5);
COMMIT;
/*!40000 ALTER TABLE `employee_project` ENABLE KEYS */;


--
-- Definition of table `fish`
--

DROP TABLE IF EXISTS `fish`;
CREATE TABLE `fish` (
  `id` int(8) NOT NULL,
  `genera` varchar(50) default NULL,
  `primary_color` varchar(50) default NULL,
  `fin_size` int(8) default NULL,
  `discriminator` varchar(50) default NULL,
  `tank_id` int(8) default NULL,
  `tank_discriminator` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fish`
--

/*!40000 ALTER TABLE `fish` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `fish` (`id`,`genera`,`primary_color`,`fin_size`,`discriminator`,`tank_id`,`tank_discriminator`) VALUES 
 (1,'Hemichromis','blue',NULL,'DiscusFish',1,'FreshwaterFishTank'),
 (2,'Hemichromis','red',NULL,'DiscusFish',2,'FreshwaterFishTank'),
 (3,'Pterophyllum',NULL,3,'AngelFish',3,'SaltwaterFishTank'),
 (4,'Pterophyllum',NULL,5,'AngelFish',4,'SaltwaterFishTank');
COMMIT;
/*!40000 ALTER TABLE `fish` ENABLE KEYS */;


--
-- Definition of table `flight`
--

DROP TABLE IF EXISTS `flight`;
CREATE TABLE `flight` (
  `id` int(8) NOT NULL,
  `destination` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flight`
--

/*!40000 ALTER TABLE `flight` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `flight` (`id`,`destination`) VALUES 
 (1,'Baltimore, MD'),
 (2,'San Francisco, CA'),
 (3,'Maui, HI');
COMMIT;
/*!40000 ALTER TABLE `flight` ENABLE KEYS */;


--
-- Definition of table `flight_passanger`
--

DROP TABLE IF EXISTS `flight_passanger`;
CREATE TABLE `flight_passanger` (
  `flight_id` int(8) NOT NULL,
  `passanger_id` int(8) NOT NULL,
  PRIMARY KEY  (`flight_id`,`passanger_id`),
  KEY `fk_flight_passanger_passanger` (`passanger_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `flight_passanger`
--

/*!40000 ALTER TABLE `flight_passanger` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `flight_passanger` (`flight_id`,`passanger_id`) VALUES 
 (1,1),
 (1,2);
COMMIT;
/*!40000 ALTER TABLE `flight_passanger` ENABLE KEYS */;


--
-- Definition of table `float_key`
--

DROP TABLE IF EXISTS `float_key`;
CREATE TABLE `float_key` (
  `id` decimal(8,2) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `float_key`
--

/*!40000 ALTER TABLE `float_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `float_key` (`id`,`name`) VALUES 
 ('1.10','Float_Key_Name1.1'),
 ('2.20','Float_Key_Name2.2'),
 ('3.30','Float_Key_Name3.3'),
 ('4.40','Float_Key_Name4.4'),
 ('5.50','Float_Key_Name5.5');
COMMIT;
/*!40000 ALTER TABLE `float_key` ENABLE KEYS */;


--
-- Definition of table `float_primitive_key`
--

DROP TABLE IF EXISTS `float_primitive_key`;
CREATE TABLE `float_primitive_key` (
  `id` decimal(8,2) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `float_primitive_key`
--

/*!40000 ALTER TABLE `float_primitive_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `float_primitive_key` (`id`,`name`) VALUES 
 ('1.10','Float_Key_Name 1.1'),
 ('2.20','Float_Key_Name 2.2'),
 ('3.30','Float_Key_Name 3.3');
COMMIT;
/*!40000 ALTER TABLE `float_primitive_key` ENABLE KEYS */;


--
-- Definition of table `freshwater_fish_tank`
--

DROP TABLE IF EXISTS `freshwater_fish_tank`;
CREATE TABLE `freshwater_fish_tank` (
  `id` int(8) NOT NULL,
  `shape` varchar(50) default NULL,
  `num_gallons` int(8) default NULL,
  `filter_model` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `freshwater_fish_tank`
--

/*!40000 ALTER TABLE `freshwater_fish_tank` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `freshwater_fish_tank` (`id`,`shape`,`num_gallons`,`filter_model`) VALUES 
 (1,'Rectangular',10,'350B Penguin Bio-Wheel Filter'),
 (2,'Hexagonal',7,'200B Penguin Bio-Wheel Filter');
COMMIT;
/*!40000 ALTER TABLE `freshwater_fish_tank` ENABLE KEYS */;


--
-- Definition of table `goverment`
--

DROP TABLE IF EXISTS `goverment`;
CREATE TABLE `goverment` (
  `id` int(8) NOT NULL,
  `discriminator` varchar(50) default NULL,
  `country` varchar(50) default NULL,
  `prime_minister` varchar(50) default NULL,
  `president` varchar(50) default NULL,
  `democratic_discriminator` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `goverment`
--

/*!40000 ALTER TABLE `goverment` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `goverment` (`id`,`discriminator`,`country`,`prime_minister`,`president`,`democratic_discriminator`) VALUES 
 (1,'PresidentialGovt','USA',NULL,'George W. Bush',NULL),
 (2,'ParliamantaryGovt','England','Tony Blair',NULL,NULL),
 (3,'CommunistGovt','Cuba',NULL,NULL,NULL);
COMMIT;
/*!40000 ALTER TABLE `goverment` ENABLE KEYS */;


--
-- Definition of table `graduate_student`
--

DROP TABLE IF EXISTS `graduate_student`;
CREATE TABLE `graduate_student` (
  `student_id` int(8) NOT NULL,
  `project_name` varchar(50) default NULL,
  PRIMARY KEY  (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `graduate_student`
--

/*!40000 ALTER TABLE `graduate_student` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `graduate_student` (`student_id`,`project_name`) VALUES 
 (6,'Project_Name6'),
 (7,'Project_Name7'),
 (8,'Project_Name8'),
 (9,'Project_Name9'),
 (10,'Project_Name10');
COMMIT;
/*!40000 ALTER TABLE `graduate_student` ENABLE KEYS */;


--
-- Definition of table `graphic_calculator`
--

DROP TABLE IF EXISTS `graphic_calculator`;
CREATE TABLE `graphic_calculator` (
  `calculator_id` int(8) NOT NULL,
  PRIMARY KEY  (`calculator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `graphic_calculator`
--

/*!40000 ALTER TABLE `graphic_calculator` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `graphic_calculator` (`calculator_id`) VALUES 
 (3);
COMMIT;
/*!40000 ALTER TABLE `graphic_calculator` ENABLE KEYS */;


--
-- Definition of table `hand`
--

DROP TABLE IF EXISTS `hand`;
CREATE TABLE `hand` (
  `id` int(8) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hand`
--

/*!40000 ALTER TABLE `hand` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `hand` (`id`) VALUES 
 (1),
 (2),
 (3),
 (4),
 (5);
COMMIT;
/*!40000 ALTER TABLE `hand` ENABLE KEYS */;


--
-- Definition of table `hand_card`
--

DROP TABLE IF EXISTS `hand_card`;
CREATE TABLE `hand_card` (
  `hand_id` int(8) NOT NULL,
  `card_id` int(8) NOT NULL,
  PRIMARY KEY  (`hand_id`,`card_id`),
  KEY `fk_hand_card_card` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hand_card`
--

/*!40000 ALTER TABLE `hand_card` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `hand_card` (`hand_id`,`card_id`) VALUES 
 (1,1),
 (2,2),
 (2,3),
 (2,5),
 (3,6),
 (3,14),
 (3,15),
 (1,25),
 (4,26),
 (4,27),
 (4,30),
 (5,39),
 (5,40),
 (5,41),
 (1,52);
COMMIT;
/*!40000 ALTER TABLE `hand_card` ENABLE KEYS */;


--
-- Definition of table `handle`
--

DROP TABLE IF EXISTS `handle`;
CREATE TABLE `handle` (
  `id` int(8) NOT NULL,
  `color` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `handle`
--

/*!40000 ALTER TABLE `handle` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `handle` (`id`,`color`) VALUES 
 (1,'Black'),
 (2,'Brown'),
 (3,'Blue'),
 (4,'White'),
 (5,'Red'),
 (6,'Yellow'),
 (7,'Green'),
 (8,'Beige'),
 (9,'Purple'),
 (10,'Orange'),
 (11,'Teal'),
 (12,'Burgundy');
COMMIT;
/*!40000 ALTER TABLE `handle` ENABLE KEYS */;


--
-- Definition of table `hard_drive`
--

DROP TABLE IF EXISTS `hard_drive`;
CREATE TABLE `hard_drive` (
  `id` int(8) NOT NULL,
  `drive_size` int(8) default NULL,
  `computer_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_hard_drive_computer` (`computer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hard_drive`
--

/*!40000 ALTER TABLE `hard_drive` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `hard_drive` (`id`,`drive_size`,`computer_id`) VALUES 
 (1,1,1),
 (2,2,2),
 (3,3,2);
COMMIT;
/*!40000 ALTER TABLE `hard_drive` ENABLE KEYS */;


--
-- Definition of table `hi_value`
--

DROP TABLE IF EXISTS `hi_value`;
CREATE TABLE `hi_value` (
  `next_value` decimal(22,0) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hi_value`
--

/*!40000 ALTER TABLE `hi_value` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `hi_value` (`next_value`) VALUES 
 ('8');
COMMIT;
/*!40000 ALTER TABLE `hi_value` ENABLE KEYS */;


--
-- Definition of table `hl7_data_type`
--

DROP TABLE IF EXISTS `hl7_data_type`;
CREATE TABLE `hl7_data_type` (
  `id` int(8) NOT NULL,
  `root` varchar(50) default NULL,
  `extension` varchar(50) default NULL,
  `xml` varchar(512) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hl7_data_type`
--

/*!40000 ALTER TABLE `hl7_data_type` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `hl7_data_type` (`id`,`root`,`extension`,`xml`) VALUES 
 (1,'My Root','My Ext',NULL),
 (2,'My Root 2','My Ext 2',NULL);
COMMIT;
/*!40000 ALTER TABLE `hl7_data_type` ENABLE KEYS */;


--
-- Definition of table `human`
--

DROP TABLE IF EXISTS `human`;
CREATE TABLE `human` (
  `mammal_id` int(8) NOT NULL,
  `diet` varchar(50) default NULL,
  PRIMARY KEY  (`mammal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `human`
--

/*!40000 ALTER TABLE `human` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `human` (`mammal_id`,`diet`) VALUES 
 (1,'DIET1'),
 (2,'DIET2'),
 (3,'DIET3'),
 (4,'DIET4');
COMMIT;
/*!40000 ALTER TABLE `human` ENABLE KEYS */;


--
-- Definition of table `in_law`
--

DROP TABLE IF EXISTS `in_law`;
CREATE TABLE `in_law` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `in_law`
--

/*!40000 ALTER TABLE `in_law` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `in_law` (`id`,`name`) VALUES 
 (1,' Father-in-Law Bride 1'),
 (2,'Mother-in-Law Bride 1'),
 (3,'Father-in-Law Bride 2'),
 (5,'Mother-in-Law Bride 3');
COMMIT;
/*!40000 ALTER TABLE `in_law` ENABLE KEYS */;


--
-- Definition of table `integer_key`
--

DROP TABLE IF EXISTS `integer_key`;
CREATE TABLE `integer_key` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `integer_key`
--

/*!40000 ALTER TABLE `integer_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `integer_key` (`id`,`name`) VALUES 
 (1,'Integer_Key_Name1'),
 (2,'Integer_Key_Name2'),
 (3,'Integer_Key_Name3'),
 (4,'Integer_Key_Name4'),
 (5,'Integer_Key_Name5');
COMMIT;
/*!40000 ALTER TABLE `integer_key` ENABLE KEYS */;


--
-- Definition of table `integer_primitive_key`
--

DROP TABLE IF EXISTS `integer_primitive_key`;
CREATE TABLE `integer_primitive_key` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `integer_primitive_key`
--

/*!40000 ALTER TABLE `integer_primitive_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `integer_primitive_key` (`id`,`name`) VALUES 
 (1,'Integer_Primitive_Key_Name 1'),
 (2,'Integer_Primitive_Key_Name 2');
COMMIT;
/*!40000 ALTER TABLE `integer_primitive_key` ENABLE KEYS */;


--
-- Definition of table `key`
--

DROP TABLE IF EXISTS `key`;
CREATE TABLE `key` (
  `id` int(8) NOT NULL default '0',
  `type` varchar(50) default NULL,
  `keychain_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_key_keychain` (`keychain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `key`
--

/*!40000 ALTER TABLE `key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `key` (`id`,`type`,`keychain_id`) VALUES 
 (1,'Key_Type1',1),
 (2,'Key_Type2',2),
 (3,'Key_Type3',2);
COMMIT;
/*!40000 ALTER TABLE `key` ENABLE KEYS */;


--
-- Definition of table `keychain`
--

DROP TABLE IF EXISTS `keychain`;
CREATE TABLE `keychain` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `keychain`
--

/*!40000 ALTER TABLE `keychain` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `keychain` (`id`,`name`) VALUES 
 (1,'Keychain_Name1'),
 (2,'Keychain_Name2'),
 (3,'Keychain_Name3'),
 (4,'Keychain_Name4'),
 (5,'Keychain_Name5');
COMMIT;
/*!40000 ALTER TABLE `keychain` ENABLE KEYS */;


--
-- Definition of table `latch_key`
--

DROP TABLE IF EXISTS `latch_key`;
CREATE TABLE `latch_key` (
  `id` int(8) NOT NULL,
  `type` varchar(50) default NULL,
  `keychain_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_latch_key_keychain` (`keychain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `latch_key`
--

/*!40000 ALTER TABLE `latch_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `latch_key` (`id`,`type`,`keychain_id`) VALUES 
 (1,'Key_Type1',1),
 (2,'Key_Type2',2),
 (3,'Key_Type3',2);
COMMIT;
/*!40000 ALTER TABLE `latch_key` ENABLE KEYS */;


--
-- Definition of table `lcd_monitor`
--

DROP TABLE IF EXISTS `lcd_monitor`;
CREATE TABLE `lcd_monitor` (
  `monitor_id` int(8) NOT NULL,
  `dpi_supported` int(8) default NULL,
  PRIMARY KEY  (`monitor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `lcd_monitor`
--

/*!40000 ALTER TABLE `lcd_monitor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `lcd_monitor` (`monitor_id`,`dpi_supported`) VALUES 
 (2,2323),
 (3,1212);
COMMIT;
/*!40000 ALTER TABLE `lcd_monitor` ENABLE KEYS */;


--
-- Definition of table `log_message`
--

DROP TABLE IF EXISTS `log_message`;
CREATE TABLE `log_message` (
  `LOG_ID` bigint(200) NOT NULL auto_increment,
  `APPLICATION` varchar(25) default NULL,
  `SERVER` varchar(50) default NULL,
  `CATEGORY` varchar(255) default NULL,
  `THREAD` varchar(255) default NULL,
  `USERNAME` varchar(255) default NULL,
  `SESSION_ID` varchar(255) default NULL,
  `MSG` text,
  `THROWABLE` text,
  `NDC` text,
  `CREATED_ON` bigint(20) NOT NULL default '0',
  `OBJECT_ID` varchar(255) default NULL,
  `OBJECT_NAME` varchar(255) default NULL,
  `ORGANIZATION` varchar(255) default NULL,
  `OPERATION` varchar(50) default NULL,
  PRIMARY KEY  (`LOG_ID`),
  KEY `APPLICATION_LOGTAB_INDX` (`APPLICATION`),
  KEY `SERVER_LOGTAB_INDX` (`SERVER`),
  KEY `THREAD_LOGTAB_INDX` (`THREAD`),
  KEY `CREATED_ON_LOGTAB_INDX` (`CREATED_ON`),
  KEY `LOGID_LOGTAB_INDX` (`LOG_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1883 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `log_message`
--

/*!40000 ALTER TABLE `log_message` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `log_message` (`LOG_ID`,`APPLICATION`,`SERVER`,`CATEGORY`,`THREAD`,`USERNAME`,`SESSION_ID`,`MSG`,`THROWABLE`,`NDC`,`CREATED_ON`,`OBJECT_ID`,`OBJECT_NAME`,`ORGANIZATION`,`OPERATION`) VALUES 
 (892,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591277571,'','','',''),
 (893,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591277930,'','','',''),
 (894,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278008,'','','',''),
 (895,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278070,'','','',''),
 (896,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278117,'','','',''),
 (897,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278164,'','','',''),
 (898,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278211,'','','',''),
 (899,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278258,'','','',''),
 (900,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278304,'','','',''),
 (901,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278336,'','','',''),
 (902,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278382,'','','',''),
 (903,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278445,'','','',''),
 (904,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278476,'','','',''),
 (905,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278507,'','','',''),
 (906,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278523,'','','',''),
 (907,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278648,'','','',''),
 (908,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278694,'','','',''),
 (909,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278741,'','','',''),
 (910,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278788,'','','',''),
 (911,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278835,'','','',''),
 (912,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591278866,'','','',''),
 (913,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591278913,'','','',''),
 (914,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591300176,'','','',''),
 (915,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591300581,'','','',''),
 (916,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591300628,'','','',''),
 (917,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591300675,'','','',''),
 (918,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591300722,'','','',''),
 (919,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591300768,'','','',''),
 (920,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591300800,'','','',''),
 (921,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591300846,'','','',''),
 (922,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591300893,'','','',''),
 (923,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591300940,'','','',''),
 (924,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591300971,'','','',''),
 (925,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591301002,'','','',''),
 (926,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591301034,'','','',''),
 (927,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591301065,'','','',''),
 (928,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591301221,'','','',''),
 (929,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591301283,'','','',''),
 (930,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591301330,'','','',''),
 (931,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591301361,'','','',''),
 (932,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591301392,'','','',''),
 (933,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591301439,'','','',''),
 (934,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591311610,'','','',''),
 (935,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591311938,'','','',''),
 (936,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337148,'','','',''),
 (937,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591337491,'','','',''),
 (938,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337538,'','','',''),
 (939,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591337600,'','','',''),
 (940,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337647,'','','',''),
 (941,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591337694,'','','',''),
 (942,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337740,'','','',''),
 (943,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591337787,'','','',''),
 (944,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337834,'','','',''),
 (945,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591337865,'','','',''),
 (946,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337912,'','','',''),
 (947,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591337959,'','','',''),
 (948,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591337990,'','','',''),
 (949,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591338021,'','','',''),
 (950,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591338037,'','','',''),
 (951,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591338162,'','','',''),
 (952,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591338208,'','','',''),
 (953,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591338240,'','','',''),
 (954,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591338286,'','','',''),
 (955,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591338318,'','','',''),
 (956,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591338396,'','','',''),
 (957,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591338427,'','','',''),
 (958,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591360329,'','','',''),
 (959,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591360672,'','','',''),
 (960,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591360719,'','','',''),
 (961,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591360782,'','','',''),
 (962,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591360813,'','','',''),
 (963,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591360875,'','','',''),
 (964,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591360906,'','','',''),
 (965,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591360953,'','','',''),
 (966,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361000,'','','',''),
 (967,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591361047,'','','',''),
 (968,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361094,'','','',''),
 (969,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591361140,'','','',''),
 (970,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361172,'','','',''),
 (971,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361187,'','','',''),
 (972,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361343,'','','',''),
 (973,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591361390,'','','',''),
 (974,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361421,'','','',''),
 (975,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591361468,'','','',''),
 (976,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591361499,'','','',''),
 (977,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591361530,'','','',''),
 (978,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591371733,'','','',''),
 (979,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591372060,'','','',''),
 (980,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591475328,'','','',''),
 (981,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591475438,'','','',''),
 (982,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591475984,'','','',''),
 (983,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591475999,'','','',''),
 (984,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476077,'','','',''),
 (985,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476093,'','','',''),
 (986,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476171,'','','',''),
 (987,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476202,'','','',''),
 (988,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476264,'','','',''),
 (989,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476296,'','','',''),
 (990,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476358,'','','',''),
 (991,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476374,'','','',''),
 (992,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476436,'','','',''),
 (993,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476452,'','','',''),
 (994,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476514,'','','',''),
 (995,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476545,'','','',''),
 (996,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476608,'','','',''),
 (997,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476639,'','','',''),
 (998,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476717,'','','',''),
 (999,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476732,'','','',''),
 (1000,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476795,'','','',''),
 (1001,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476810,'','','',''),
 (1002,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476888,'','','',''),
 (1003,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591476904,'','','',''),
 (1004,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476966,'','','',''),
 (1005,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591476982,'','','',''),
 (1006,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477029,'','','',''),
 (1007,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477044,'','','',''),
 (1008,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477076,'','','',''),
 (1009,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477107,'','','',''),
 (1010,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477154,'','','',''),
 (1011,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477294,'','','',''),
 (1012,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477341,'','','',''),
 (1013,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477372,'','','',''),
 (1014,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477419,'','','',''),
 (1015,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477434,'','','',''),
 (1016,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477497,'','','',''),
 (1017,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477528,'','','',''),
 (1018,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477575,'','','',''),
 (1019,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477590,'','','',''),
 (1020,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477637,'','','',''),
 (1021,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477668,'','','',''),
 (1022,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477715,'','','',''),
 (1023,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477746,'','','',''),
 (1024,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477793,'','','',''),
 (1025,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477809,'','','',''),
 (1026,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1214591477871,'','','',''),
 (1027,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1214591477965,'','','',''),
 (1028,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591481319,'','','',''),
 (1029,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591481366,'','','',''),
 (1030,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591481662,'','','',''),
 (1031,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591481693,'','','',''),
 (1032,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591481880,'','','',''),
 (1033,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591481912,'','','',''),
 (1034,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591481974,'','','',''),
 (1035,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591481990,'','','',''),
 (1036,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482161,'','','',''),
 (1037,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482177,'','','',''),
 (1038,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591482239,'','','',''),
 (1039,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591482255,'','','',''),
 (1040,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482426,'','','',''),
 (1041,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482458,'','','',''),
 (1042,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591482504,'','','',''),
 (1043,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591482520,'','','',''),
 (1044,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482692,'','','',''),
 (1045,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482723,'','','',''),
 (1046,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591482770,'','','',''),
 (1047,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591482801,'','','',''),
 (1048,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482957,'','','',''),
 (1049,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591482972,'','','',''),
 (1050,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591483035,'','','',''),
 (1051,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591483050,'','','',''),
 (1052,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483222,'','','',''),
 (1053,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483253,'','','',''),
 (1054,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483284,'','','',''),
 (1055,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483331,'','','',''),
 (1056,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483456,'','','',''),
 (1057,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483487,'','','',''),
 (1058,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483550,'','','',''),
 (1059,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483565,'','','',''),
 (1060,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591483612,'','','',''),
 (1061,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591483643,'','','',''),
 (1062,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483815,'','','',''),
 (1063,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591483830,'','','',''),
 (1064,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591483877,'','','',''),
 (1065,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591483908,'','','',''),
 (1066,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591484064,'','','',''),
 (1067,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591484080,'','','',''),
 (1068,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591484127,'','','',''),
 (1069,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591484158,'','','',''),
 (1070,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591484314,'','','',''),
 (1071,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591484361,'','','',''),
 (1072,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591486014,'','','',''),
 (1073,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591486061,'','','',''),
 (1074,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591486436,'','','',''),
 (1075,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591486451,'','','',''),
 (1076,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591486576,'','','',''),
 (1077,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676178,'','','',''),
 (1078,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676241,'','','',''),
 (1079,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676537,'','','',''),
 (1080,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676553,'','','',''),
 (1081,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676615,'','','',''),
 (1082,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676631,'','','',''),
 (1083,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676678,'','','',''),
 (1084,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676709,'','','',''),
 (1085,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676756,'','','',''),
 (1086,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676787,'','','',''),
 (1087,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676818,'','','',''),
 (1088,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676834,'','','',''),
 (1089,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676880,'','','',''),
 (1090,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591676912,'','','',''),
 (1091,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676958,'','','',''),
 (1092,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591676990,'','','',''),
 (1093,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677021,'','','',''),
 (1094,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677068,'','','',''),
 (1095,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677099,'','','',''),
 (1096,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677130,'','','',''),
 (1097,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677161,'','','',''),
 (1098,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677192,'','','',''),
 (1099,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677224,'','','',''),
 (1100,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677255,'','','',''),
 (1101,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677286,'','','',''),
 (1102,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677317,'','','',''),
 (1103,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677333,'','','',''),
 (1104,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677348,'','','',''),
 (1105,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677380,'','','',''),
 (1106,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677395,'','','',''),
 (1107,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677442,'','','',''),
 (1108,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677520,'','','',''),
 (1109,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677567,'','','',''),
 (1110,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677582,'','','',''),
 (1111,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677645,'','','',''),
 (1112,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677676,'','','',''),
 (1113,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677723,'','','',''),
 (1114,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677738,'','','',''),
 (1115,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677785,'','','',''),
 (1116,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677801,'','','',''),
 (1117,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677848,'','','',''),
 (1118,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677863,'','','',''),
 (1119,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677910,'','','',''),
 (1120,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591677926,'','','',''),
 (1121,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677972,'','','',''),
 (1122,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591677988,'','','',''),
 (1123,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591678035,'','','',''),
 (1124,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591678082,'','','',''),
 (1125,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591679564,'','','',''),
 (1126,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591679610,'','','',''),
 (1127,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591679860,'','','',''),
 (1128,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591679876,'','','',''),
 (1129,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680063,'','','',''),
 (1130,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680094,'','','',''),
 (1131,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591680156,'','','',''),
 (1132,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591680188,'','','',''),
 (1133,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680328,'','','',''),
 (1134,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680344,'','','',''),
 (1135,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591680390,'','','',''),
 (1136,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591680406,'','','',''),
 (1137,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680749,'','','',''),
 (1138,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680780,'','','',''),
 (1139,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591680812,'','','',''),
 (1140,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591680843,'','','',''),
 (1141,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591680983,'','','',''),
 (1142,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681030,'','','',''),
 (1143,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591681061,'','','',''),
 (1144,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591681077,'','','',''),
 (1145,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681233,'','','',''),
 (1146,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681264,'','','',''),
 (1147,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591681295,'','','',''),
 (1148,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591681326,'','','',''),
 (1149,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681467,'','','',''),
 (1150,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681498,'','','',''),
 (1151,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681514,'','','',''),
 (1152,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681576,'','','',''),
 (1153,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681670,'','','',''),
 (1154,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681716,'','','',''),
 (1155,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681763,'','','',''),
 (1156,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681779,'','','',''),
 (1157,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591681826,'','','',''),
 (1158,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591681841,'','','',''),
 (1159,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591681982,'','','',''),
 (1160,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591682013,'','','',''),
 (1161,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591682044,'','','',''),
 (1162,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591682075,'','','',''),
 (1163,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591682231,'','','',''),
 (1164,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591682247,'','','',''),
 (1165,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591682294,'','','',''),
 (1166,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591682309,'','','',''),
 (1167,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591682450,'','','',''),
 (1168,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591682496,'','','',''),
 (1169,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591684103,'','','',''),
 (1170,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591684134,'','','',''),
 (1171,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591684478,'','','',''),
 (1172,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591684493,'','','',''),
 (1173,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591684602,'','','',''),
 (1174,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702090,'','','',''),
 (1175,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702418,'','','',''),
 (1176,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702480,'','','',''),
 (1177,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702527,'','','',''),
 (1178,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702574,'','','',''),
 (1179,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702620,'','','',''),
 (1180,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702667,'','','',''),
 (1181,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702730,'','','',''),
 (1182,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702776,'','','',''),
 (1183,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702823,'','','',''),
 (1184,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702854,'','','',''),
 (1185,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702901,'','','',''),
 (1186,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702932,'','','',''),
 (1187,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591702964,'','','',''),
 (1188,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591702979,'','','',''),
 (1189,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591703120,'','','',''),
 (1190,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591703166,'','','',''),
 (1191,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591703213,'','','',''),
 (1192,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591703244,'','','',''),
 (1193,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591703291,'','','',''),
 (1194,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591703338,'','','',''),
 (1195,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591703369,'','','',''),
 (1196,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214591703432,'','','',''),
 (1197,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214591703478,'','','',''),
 (1198,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591714383,'','','',''),
 (1199,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591714742,'','','',''),
 (1200,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591714788,'','','',''),
 (1201,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591714835,'','','',''),
 (1202,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591714882,'','','',''),
 (1203,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591714944,'','','',''),
 (1204,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591714991,'','','',''),
 (1205,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591715038,'','','',''),
 (1206,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715069,'','','',''),
 (1207,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591715116,'','','',''),
 (1208,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715147,'','','',''),
 (1209,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591715178,'','','',''),
 (1210,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715225,'','','',''),
 (1211,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715241,'','','',''),
 (1212,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715381,'','','',''),
 (1213,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591715428,'','','',''),
 (1214,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715475,'','','',''),
 (1215,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591715522,'','','',''),
 (1216,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214591715553,'','','',''),
 (1217,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591715600,'','','',''),
 (1218,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214591715646,'','','',''),
 (1219,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214591715693,'','','',''),
 (1220,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591725927,'','','',''),
 (1221,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214591726254,'','','',''),
 (1222,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592949895,'','','',''),
 (1223,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950207,'','','',''),
 (1224,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950239,'','','',''),
 (1225,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950270,'','','',''),
 (1226,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950285,'','','',''),
 (1227,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950317,'','','',''),
 (1228,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950348,'','','',''),
 (1229,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950379,'','','',''),
 (1230,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950410,'','','',''),
 (1231,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950426,'','','',''),
 (1232,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950457,'','','',''),
 (1233,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950488,'','','',''),
 (1234,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950519,'','','',''),
 (1235,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950535,'','','',''),
 (1236,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950551,'','','',''),
 (1237,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950629,'','','',''),
 (1238,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950660,'','','',''),
 (1239,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950691,'','','',''),
 (1240,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950707,'','','',''),
 (1241,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950738,'','','',''),
 (1242,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592950769,'','','',''),
 (1243,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592950785,'','','',''),
 (1244,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592950831,'','','',''),
 (1245,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592950878,'','','',''),
 (1246,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592957851,'','','',''),
 (1247,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958163,'','','',''),
 (1248,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958179,'','','',''),
 (1249,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958210,'','','',''),
 (1250,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958226,'','','',''),
 (1251,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958257,'','','',''),
 (1252,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958288,'','','',''),
 (1253,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958319,'','','',''),
 (1254,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958335,'','','',''),
 (1255,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958366,'','','',''),
 (1256,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958382,'','','',''),
 (1257,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958413,'','','',''),
 (1258,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958444,'','','',''),
 (1259,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958460,'','','',''),
 (1260,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958553,'','','',''),
 (1261,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958585,'','','',''),
 (1262,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958600,'','','',''),
 (1263,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958631,'','','',''),
 (1264,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214592958647,'','','',''),
 (1265,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592958663,'','','',''),
 (1266,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592958709,'','','',''),
 (1267,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592958756,'','','',''),
 (1268,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592965761,'','','',''),
 (1269,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214592966073,'','','',''),
 (1270,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592980643,'','','',''),
 (1271,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592980690,'','','',''),
 (1272,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981017,'','','',''),
 (1273,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981033,'','','',''),
 (1274,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981095,'','','',''),
 (1275,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981127,'','','',''),
 (1276,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981189,'','','',''),
 (1277,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981205,'','','',''),
 (1278,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981267,'','','',''),
 (1279,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981283,'','','',''),
 (1280,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981329,'','','',''),
 (1281,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981361,'','','',''),
 (1282,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981407,'','','',''),
 (1283,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981423,'','','',''),
 (1284,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981470,'','','',''),
 (1285,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981501,'','','',''),
 (1286,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981548,'','','',''),
 (1287,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981579,'','','',''),
 (1288,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981626,'','','',''),
 (1289,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981641,'','','',''),
 (1290,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981688,'','','',''),
 (1291,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981704,'','','',''),
 (1292,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981751,'','','',''),
 (1293,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592981766,'','','',''),
 (1294,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981813,'','','',''),
 (1295,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592981829,'','','',''),
 (1296,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592981860,'','','',''),
 (1297,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592981875,'','','',''),
 (1298,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592981922,'','','',''),
 (1299,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592981938,'','','',''),
 (1300,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982000,'','','',''),
 (1301,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982094,'','','',''),
 (1302,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982141,'','','',''),
 (1303,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982172,'','','',''),
 (1304,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982219,'','','',''),
 (1305,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982250,'','','',''),
 (1306,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982297,'','','',''),
 (1307,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982312,'','','',''),
 (1308,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982359,'','','',''),
 (1309,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982390,'','','',''),
 (1310,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982421,'','','',''),
 (1311,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982453,'','','',''),
 (1312,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982484,'','','',''),
 (1313,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982515,'','','',''),
 (1314,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982562,'','','',''),
 (1315,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982577,'','','',''),
 (1316,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1214592982624,'','','',''),
 (1317,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592982671,'','','',''),
 (1318,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592984091,'','','',''),
 (1319,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592984169,'','','',''),
 (1320,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592984418,'','','',''),
 (1321,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592984449,'','','',''),
 (1322,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592984621,'','','',''),
 (1323,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592984652,'','','',''),
 (1324,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592984699,'','','',''),
 (1325,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592984730,'','','',''),
 (1326,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592984886,'','','',''),
 (1327,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592984902,'','','',''),
 (1328,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592984949,'','','',''),
 (1329,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592984980,'','','',''),
 (1330,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985151,'','','',''),
 (1331,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985183,'','','',''),
 (1332,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592985229,'','','',''),
 (1333,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592985261,'','','',''),
 (1334,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985417,'','','',''),
 (1335,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985448,'','','',''),
 (1336,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592985479,'','','',''),
 (1337,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592985510,'','','',''),
 (1338,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985666,'','','',''),
 (1339,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985697,'','','',''),
 (1340,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592985729,'','','',''),
 (1341,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592985744,'','','',''),
 (1342,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985916,'','','',''),
 (1343,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985931,'','','',''),
 (1344,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985963,'','','',''),
 (1345,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592985994,'','','',''),
 (1346,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986103,'','','',''),
 (1347,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986150,'','','',''),
 (1348,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986228,'','','',''),
 (1349,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986259,'','','',''),
 (1350,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592986306,'','','',''),
 (1351,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592986321,'','','',''),
 (1352,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986477,'','','',''),
 (1353,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986493,'','','',''),
 (1354,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592986540,'','','',''),
 (1355,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592986555,'','','',''),
 (1356,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986696,'','','',''),
 (1357,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986727,'','','','');
INSERT INTO `log_message` (`LOG_ID`,`APPLICATION`,`SERVER`,`CATEGORY`,`THREAD`,`USERNAME`,`SESSION_ID`,`MSG`,`THROWABLE`,`NDC`,`CREATED_ON`,`OBJECT_ID`,`OBJECT_NAME`,`ORGANIZATION`,`OPERATION`) VALUES 
 (1358,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592986758,'','','',''),
 (1359,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592986789,'','','',''),
 (1360,'example','Dan-PC','INFO','http-8080-Processor22','user1','','Successful Login attempt for user user1','','',1214592986945,'','','',''),
 (1361,'example','Dan-PC','INFO','http-8080-Processor22','user2','','Successful Login attempt for user user2','','',1214592987008,'','','',''),
 (1362,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592988693,'','','',''),
 (1363,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592988724,'','','',''),
 (1364,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592989067,'','','',''),
 (1365,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592989098,'','','',''),
 (1366,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1214592989223,'','','',''),
 (1367,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597184704,'','','',''),
 (1368,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185078,'','','',''),
 (1369,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185109,'','','',''),
 (1370,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185125,'','','',''),
 (1371,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185156,'','','',''),
 (1372,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185187,'','','',''),
 (1373,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185218,'','','',''),
 (1374,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185250,'','','',''),
 (1375,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185281,'','','',''),
 (1376,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185296,'','','',''),
 (1377,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185328,'','','',''),
 (1378,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185359,'','','',''),
 (1379,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185390,'','','',''),
 (1380,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185406,'','','',''),
 (1381,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185421,'','','',''),
 (1382,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185499,'','','',''),
 (1383,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185530,'','','',''),
 (1384,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185562,'','','',''),
 (1385,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185577,'','','',''),
 (1386,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185608,'','','',''),
 (1387,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597185624,'','','',''),
 (1388,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597185655,'','','',''),
 (1389,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597194625,'','','',''),
 (1390,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597194937,'','','',''),
 (1391,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597194968,'','','',''),
 (1392,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195000,'','','',''),
 (1393,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195015,'','','',''),
 (1394,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195046,'','','',''),
 (1395,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195078,'','','',''),
 (1396,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195109,'','','',''),
 (1397,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195156,'','','',''),
 (1398,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195187,'','','',''),
 (1399,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195218,'','','',''),
 (1400,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195249,'','','',''),
 (1401,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195265,'','','',''),
 (1402,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195296,'','','',''),
 (1403,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195390,'','','',''),
 (1404,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195421,'','','',''),
 (1405,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195452,'','','',''),
 (1406,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195468,'','','',''),
 (1407,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597195499,'','','',''),
 (1408,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597195514,'','','',''),
 (1409,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597204500,'','','',''),
 (1410,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597204812,'','','',''),
 (1411,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597330531,'','','',''),
 (1412,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597330843,'','','',''),
 (1413,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597330874,'','','',''),
 (1414,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597330905,'','','',''),
 (1415,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597330936,'','','',''),
 (1416,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597330952,'','','',''),
 (1417,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597330983,'','','',''),
 (1418,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331014,'','','',''),
 (1419,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331045,'','','',''),
 (1420,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331061,'','','',''),
 (1421,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331092,'','','',''),
 (1422,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331123,'','','',''),
 (1423,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331155,'','','',''),
 (1424,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331170,'','','',''),
 (1425,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331201,'','','',''),
 (1426,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331279,'','','',''),
 (1427,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331295,'','','',''),
 (1428,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331326,'','','',''),
 (1429,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331357,'','','',''),
 (1430,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331373,'','','',''),
 (1431,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597331404,'','','',''),
 (1432,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597331420,'','','',''),
 (1433,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340296,'','','',''),
 (1434,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597340608,'','','',''),
 (1435,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340639,'','','',''),
 (1436,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597340671,'','','',''),
 (1437,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340686,'','','',''),
 (1438,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597340733,'','','',''),
 (1439,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340749,'','','',''),
 (1440,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597340780,'','','',''),
 (1441,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340811,'','','',''),
 (1442,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597340827,'','','',''),
 (1443,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340858,'','','',''),
 (1444,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597340889,'','','',''),
 (1445,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340905,'','','',''),
 (1446,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597340920,'','','',''),
 (1447,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597341014,'','','',''),
 (1448,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597341045,'','','',''),
 (1449,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597341061,'','','',''),
 (1450,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597341092,'','','',''),
 (1451,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597341107,'','','',''),
 (1452,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597341123,'','','',''),
 (1453,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597349890,'','','',''),
 (1454,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597350202,'','','',''),
 (1455,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597559833,'','','',''),
 (1456,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560145,'','','',''),
 (1457,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560192,'','','',''),
 (1458,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560207,'','','',''),
 (1459,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560238,'','','',''),
 (1460,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560270,'','','',''),
 (1461,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560285,'','','',''),
 (1462,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560316,'','','',''),
 (1463,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560348,'','','',''),
 (1464,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560379,'','','',''),
 (1465,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560426,'','','',''),
 (1466,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560457,'','','',''),
 (1467,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560488,'','','',''),
 (1468,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560504,'','','',''),
 (1469,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560519,'','','',''),
 (1470,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560597,'','','',''),
 (1471,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560628,'','','',''),
 (1472,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560660,'','','',''),
 (1473,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560675,'','','',''),
 (1474,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560691,'','','',''),
 (1475,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597560722,'','','',''),
 (1476,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597560753,'','','',''),
 (1477,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597569598,'','','',''),
 (1478,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597569910,'','','',''),
 (1479,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597569926,'','','',''),
 (1480,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597569957,'','','',''),
 (1481,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597569988,'','','',''),
 (1482,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570004,'','','',''),
 (1483,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570035,'','','',''),
 (1484,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570066,'','','',''),
 (1485,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570098,'','','',''),
 (1486,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570113,'','','',''),
 (1487,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570144,'','','',''),
 (1488,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570160,'','','',''),
 (1489,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570191,'','','',''),
 (1490,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570207,'','','',''),
 (1491,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570300,'','','',''),
 (1492,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570332,'','','',''),
 (1493,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570347,'','','',''),
 (1494,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570378,'','','',''),
 (1495,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214597570394,'','','',''),
 (1496,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597570425,'','','',''),
 (1497,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597579161,'','','',''),
 (1498,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214597579473,'','','',''),
 (1499,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598075967,'','','',''),
 (1500,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076279,'','','',''),
 (1501,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076310,'','','',''),
 (1502,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076341,'','','',''),
 (1503,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076357,'','','',''),
 (1504,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076388,'','','',''),
 (1505,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076419,'','','',''),
 (1506,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076466,'','','',''),
 (1507,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076497,'','','',''),
 (1508,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076529,'','','',''),
 (1509,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076575,'','','',''),
 (1510,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076591,'','','',''),
 (1511,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076622,'','','',''),
 (1512,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076638,'','','',''),
 (1513,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076653,'','','',''),
 (1514,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076731,'','','',''),
 (1515,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076763,'','','',''),
 (1516,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076794,'','','',''),
 (1517,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076809,'','','',''),
 (1518,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076841,'','','',''),
 (1519,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598076856,'','','',''),
 (1520,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598076872,'','','',''),
 (1521,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598085717,'','','',''),
 (1522,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086029,'','','',''),
 (1523,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086060,'','','',''),
 (1524,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086076,'','','',''),
 (1525,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086107,'','','',''),
 (1526,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086138,'','','',''),
 (1527,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086154,'','','',''),
 (1528,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086185,'','','',''),
 (1529,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086216,'','','',''),
 (1530,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086232,'','','',''),
 (1531,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086263,'','','',''),
 (1532,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086294,'','','',''),
 (1533,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086310,'','','',''),
 (1534,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086325,'','','',''),
 (1535,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086419,'','','',''),
 (1536,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086450,'','','',''),
 (1537,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086466,'','','',''),
 (1538,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086497,'','','',''),
 (1539,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598086513,'','','',''),
 (1540,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598086544,'','','',''),
 (1541,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598096185,'','','',''),
 (1542,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598096481,'','','',''),
 (1543,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598199893,'','','',''),
 (1544,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598270452,'','','',''),
 (1545,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598352449,'','','',''),
 (1546,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598468450,'','','',''),
 (1547,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598468762,'','','',''),
 (1548,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598468778,'','','',''),
 (1549,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598468809,'','','',''),
 (1550,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598468825,'','','',''),
 (1551,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598468856,'','','',''),
 (1552,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598468887,'','','',''),
 (1553,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598468918,'','','',''),
 (1554,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598468934,'','','',''),
 (1555,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598468965,'','','',''),
 (1556,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598468981,'','','',''),
 (1557,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598469028,'','','',''),
 (1558,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598469043,'','','',''),
 (1559,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598469059,'','','',''),
 (1560,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598469152,'','','',''),
 (1561,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598469184,'','','',''),
 (1562,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598469199,'','','',''),
 (1563,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598469230,'','','',''),
 (1564,'example','Dan-PC','INFO','main','user1','','Successful Login attempt for user user1','','',1214598469246,'','','',''),
 (1565,'example','Dan-PC','INFO','main','user2','','Successful Login attempt for user user2','','',1214598469277,'','','',''),
 (1566,'example','Dan-PC','INFO','http-0.0.0.0-8087-2','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Successful Login attempt for user SuperAdmin','','',1214871397254,'','','',''),
 (1567,'example','Dan-PC','INFO','http-0.0.0.0-8087-1','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Assigning Roles to User user2 for Protection Group Limited Access','','',1214872067460,'','','',''),
 (1568,'example','Dan-PC','INFO','http-0.0.0.0-8087-3','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Deassigning Roles and Protection Group Assignment from User','','',1214872081524,'','','',''),
 (1569,'example','Dan-PC','INFO','http-0.0.0.0-8087-3','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Creating the Group Object','','',1214873499479,'','','',''),
 (1570,'example','Dan-PC','INFO','http-0.0.0.0-8087-3','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Updating the Group Object','','',1214873536080,'','','',''),
 (1571,'example','Dan-PC','INFO','http-0.0.0.0-8087-4','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Assigning Group 3 to Users','','',1214874319738,'','','',''),
 (1572,'example','Dan-PC','INFO','http-0.0.0.0-8087-4','SuperAdmin','292CAF8BF8F950EC65441BA50BB81689','Assigning Roles to Group Group2 for Protection Group Limited Access','','',1214874384826,'','','',''),
 (1573,'example','Dan-PC','INFO','http-0.0.0.0-8087-2','dumitrud','5A74C71EB4E1CC8306A17219D536081B','Successful Login attempt for user dumitrud','','',1219434240855,'','','',''),
 (1574,'example','Dan-PC','INFO','http-0.0.0.0-8087-1','SuperAdmin','5A74C71EB4E1CC8306A17219D536081B','Unsuccessful Login attempt for user SuperAdmin','','',1219434290631,'','','',''),
 (1575,'example','Dan-PC','INFO','http-0.0.0.0-8087-2','SuperAdmin','5A74C71EB4E1CC8306A17219D536081B','Unsuccessful Login attempt for user SuperAdmin','','',1219434311461,'','','',''),
 (1576,'example','Dan-PC','INFO','http-0.0.0.0-8087-2','SuperAdmin','5A74C71EB4E1CC8306A17219D536081B','Successful Login attempt for user SuperAdmin','','',1219434329504,'','','',''),
 (1577,'example','Dan-PC','INFO','http-0.0.0.0-8087-1','SuperAdmin','5A74C71EB4E1CC8306A17219D536081B','Successful Login attempt for user SuperAdmin','','',1219439055834,'','','',''),
 (1578,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856751108,'','','',''),
 (1579,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856751747,'','','',''),
 (1580,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219856762355,'','','',''),
 (1581,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219856762605,'','','',''),
 (1582,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856859590,'','','',''),
 (1583,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856859684,'','','',''),
 (1584,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856860245,'','','',''),
 (1585,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856860276,'','','',''),
 (1586,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856860620,'','','',''),
 (1587,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856901757,'','','',''),
 (1588,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856901835,'','','',''),
 (1589,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856902459,'','','',''),
 (1590,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856902490,'','','',''),
 (1591,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856902693,'','','',''),
 (1592,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856902708,'','','',''),
 (1593,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856902911,'','','',''),
 (1594,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856902927,'','','',''),
 (1595,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856903130,'','','',''),
 (1596,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856903145,'','','',''),
 (1597,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856903348,'','','',''),
 (1598,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856903364,'','','',''),
 (1599,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856903551,'','','',''),
 (1600,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856903582,'','','',''),
 (1601,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856903769,'','','',''),
 (1602,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856903800,'','','',''),
 (1603,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856904050,'','','',''),
 (1604,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856904081,'','','',''),
 (1605,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856904284,'','','',''),
 (1606,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856904315,'','','',''),
 (1607,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856904502,'','','',''),
 (1608,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856904534,'','','',''),
 (1609,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856904721,'','','',''),
 (1610,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856904736,'','','',''),
 (1611,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856904939,'','','',''),
 (1612,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856904955,'','','',''),
 (1613,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856904986,'','','',''),
 (1614,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856905017,'','','',''),
 (1615,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856905048,'','','',''),
 (1616,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856905064,'','','',''),
 (1617,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856905251,'','','',''),
 (1618,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856905282,'','','',''),
 (1619,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856905454,'','','',''),
 (1620,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856905719,'','','',''),
 (1621,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856905750,'','','',''),
 (1622,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856905953,'','','',''),
 (1623,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856905969,'','','',''),
 (1624,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856906140,'','','',''),
 (1625,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856906156,'','','',''),
 (1626,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856906343,'','','',''),
 (1627,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856906374,'','','',''),
 (1628,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856906562,'','','',''),
 (1629,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856906577,'','','',''),
 (1630,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856906780,'','','',''),
 (1631,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856906811,'','','',''),
 (1632,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856906998,'','','',''),
 (1633,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856907170,'','','',''),
 (1634,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856908761,'','','',''),
 (1635,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856908839,'','','',''),
 (1636,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856909292,'','','',''),
 (1637,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856909323,'','','',''),
 (1638,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856909510,'','','',''),
 (1639,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856909526,'','','',''),
 (1640,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856909713,'','','',''),
 (1641,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856909728,'','','',''),
 (1642,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856909962,'','','',''),
 (1643,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856909947,'','','',''),
 (1644,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856910196,'','','',''),
 (1645,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856910228,'','','',''),
 (1646,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856910399,'','','',''),
 (1647,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856910415,'','','',''),
 (1648,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856910618,'','','',''),
 (1649,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856910649,'','','',''),
 (1650,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856910820,'','','',''),
 (1651,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219856910852,'','','',''),
 (1652,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219856911039,'','','',''),
 (1653,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856911070,'','','',''),
 (1654,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911226,'','','',''),
 (1655,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911257,'','','',''),
 (1656,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856911413,'','','',''),
 (1657,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856911429,'','','',''),
 (1658,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911600,'','','',''),
 (1659,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911632,'','','',''),
 (1660,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911663,'','','',''),
 (1661,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911678,'','','',''),
 (1662,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911928,'','','',''),
 (1663,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856911990,'','','',''),
 (1664,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856912178,'','','',''),
 (1665,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856912224,'','','',''),
 (1666,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856912365,'','','',''),
 (1667,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856912427,'','','',''),
 (1668,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856912614,'','','',''),
 (1669,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856912646,'','','',''),
 (1670,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856912802,'','','',''),
 (1671,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856912833,'','','',''),
 (1672,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856913020,'','','',''),
 (1673,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856913036,'','','',''),
 (1674,'example','Dan-PC','INFO','http-8080-Processor21','user1','','Successful Login attempt for user user1','','',1219856913207,'','','',''),
 (1675,'example','Dan-PC','INFO','http-8080-Processor21','user2','','Successful Login attempt for user user2','','',1219856913394,'','','',''),
 (1676,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219856915048,'','','',''),
 (1677,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219856915126,'','','',''),
 (1678,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219856915563,'','','',''),
 (1679,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219856915594,'','','',''),
 (1680,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219856915844,'','','',''),
 (1681,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219939810862,'','','',''),
 (1682,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219939811377,'','','',''),
 (1683,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219939821221,'','','',''),
 (1684,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219939821439,'','','',''),
 (1685,'example','Dan-PC','INFO','http-0.0.0.0-8087-1','SuperAdmin','7A40908460028EE54FF4637E85FD3578','Successful Login attempt for user SuperAdmin','','',1219940145761,'','','',''),
 (1686,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940746147,'','','',''),
 (1687,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940746225,'','','',''),
 (1688,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940746755,'','','',''),
 (1689,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940746771,'','','',''),
 (1690,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940746974,'','','',''),
 (1691,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940746989,'','','',''),
 (1692,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940747161,'','','',''),
 (1693,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940747192,'','','',''),
 (1694,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940747364,'','','',''),
 (1695,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940747395,'','','',''),
 (1696,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940747567,'','','',''),
 (1697,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940747598,'','','',''),
 (1698,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940747754,'','','',''),
 (1699,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940747769,'','','',''),
 (1700,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940747941,'','','',''),
 (1701,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940747957,'','','',''),
 (1702,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940748113,'','','',''),
 (1703,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940748144,'','','',''),
 (1704,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940748378,'','','',''),
 (1705,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940748393,'','','',''),
 (1706,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940748565,'','','',''),
 (1707,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940748581,'','','',''),
 (1708,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940748799,'','','',''),
 (1709,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940748815,'','','',''),
 (1710,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940749033,'','','',''),
 (1711,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940749064,'','','',''),
 (1712,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749095,'','','',''),
 (1713,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749127,'','','',''),
 (1714,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940749158,'','','',''),
 (1715,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940749173,'','','',''),
 (1716,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749361,'','','',''),
 (1717,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749392,'','','',''),
 (1718,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749548,'','','',''),
 (1719,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940749751,'','','',''),
 (1720,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940749766,'','','',''),
 (1721,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749938,'','','',''),
 (1722,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940749953,'','','',''),
 (1723,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940750125,'','','',''),
 (1724,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940750141,'','','',''),
 (1725,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940750297,'','','',''),
 (1726,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940750312,'','','',''),
 (1727,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940750453,'','','',''),
 (1728,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940750468,'','','',''),
 (1729,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940750624,'','','',''),
 (1730,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940750640,'','','',''),
 (1731,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219940750780,'','','',''),
 (1732,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940750967,'','','',''),
 (1733,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940752652,'','','',''),
 (1734,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940752715,'','','',''),
 (1735,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753120,'','','',''),
 (1736,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753151,'','','',''),
 (1737,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940753214,'','','',''),
 (1738,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940753229,'','','',''),
 (1739,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753370,'','','',''),
 (1740,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753385,'','','',''),
 (1741,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940753432,'','','',''),
 (1742,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940753448,'','','',''),
 (1743,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753651,'','','',''),
 (1744,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753682,'','','',''),
 (1745,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940753729,'','','',''),
 (1746,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940753744,'','','',''),
 (1747,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753931,'','','',''),
 (1748,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940753947,'','','',''),
 (1749,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754009,'','','',''),
 (1750,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754041,'','','',''),
 (1751,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940754212,'','','',''),
 (1752,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940754243,'','','',''),
 (1753,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754275,'','','',''),
 (1754,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754290,'','','',''),
 (1755,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940754493,'','','',''),
 (1756,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940754509,'','','',''),
 (1757,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754540,'','','',''),
 (1758,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754555,'','','',''),
 (1759,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754587,'','','',''),
 (1760,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754602,'','','',''),
 (1761,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754836,'','','',''),
 (1762,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940754899,'','','',''),
 (1763,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755070,'','','',''),
 (1764,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755086,'','','',''),
 (1765,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940755117,'','','',''),
 (1766,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940755133,'','','',''),
 (1767,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755304,'','','',''),
 (1768,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755335,'','','',''),
 (1769,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940755367,'','','',''),
 (1770,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940755382,'','','',''),
 (1771,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755538,'','','',''),
 (1772,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755569,'','','',''),
 (1773,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219940755601,'','','',''),
 (1774,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219940755772,'','','',''),
 (1775,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940757317,'','','',''),
 (1776,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940757395,'','','',''),
 (1777,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940757847,'','','',''),
 (1778,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940757863,'','','',''),
 (1779,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219940758097,'','','',''),
 (1780,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1219941096605,'','','',''),
 (1781,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1219941096777,'','','',''),
 (1782,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1219941106808,'','','',''),
 (1783,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1219941106964,'','','',''),
 (1784,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220555478903,'','','',''),
 (1785,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220555479153,'','','',''),
 (1786,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220555691845,'','','',''),
 (1787,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220555691939,'','','',''),
 (1788,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220630832745,'','','',''),
 (1789,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1220630833382,'','','',''),
 (1790,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220630843836,'','','',''),
 (1791,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1220630843882,'','','',''),
 (1792,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220632483575,'','','',''),
 (1793,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1220632483645,'','','',''),
 (1794,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220632493917,'','','',''),
 (1795,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1220632493989,'','','',''),
 (1796,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220640554666,'','','',''),
 (1797,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1220640554985,'','','',''),
 (1798,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220640556618,'','','',''),
 (1799,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1220640556728,'','','',''),
 (1800,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220640579438,'','','',''),
 (1801,'example','Dan-PC','INFO','http-8080-Processor25','user2','','Successful Login attempt for user user2','','',1220640579495,'','','',''),
 (1802,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220640589571,'','','',''),
 (1803,'example','Dan-PC','INFO','http-8080-Processor24','user2','','Successful Login attempt for user user2','','',1220640589632,'','','',''),
 (1804,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220640833124,'','','',''),
 (1805,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220640833244,'','','',''),
 (1806,'example','Dan-PC','INFO','http-0.0.0.0-8087-3','SuperAdmin','FCCC1E845EC8B21B7586D22A75A4BFE2','Successful Login attempt for user SuperAdmin','','',1220641077088,'','','',''),
 (1807,'example','Dan-PC','INFO','http-0.0.0.0-8087-1','SuperAdmin','FCCC1E845EC8B21B7586D22A75A4BFE2','Successful log out for user SuperAdmin','','',1220642980146,'','','',''),
 (1808,'example','Dan-PC','INFO','http-0.0.0.0-8087-1','SuperAdmin','FCCC1E845EC8B21B7586D22A75A4BFE2','Successful Login attempt for user SuperAdmin','','',1220643715740,'','','',''),
 (1809,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220643821922,'','','',''),
 (1810,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220643822022,'','','',''),
 (1811,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220644321266,'','','',''),
 (1812,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220644321366,'','','',''),
 (1813,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220644422622,'','','',''),
 (1814,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220644422732,'','','',''),
 (1815,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220646345439,'','','',''),
 (1816,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220646345617,'','','',''),
 (1817,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220646767725,'','','',''),
 (1818,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220646767846,'','','',''),
 (1819,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220646833194,'','','',''),
 (1820,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220646833308,'','','',''),
 (1821,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220646848760,'','','','');
INSERT INTO `log_message` (`LOG_ID`,`APPLICATION`,`SERVER`,`CATEGORY`,`THREAD`,`USERNAME`,`SESSION_ID`,`MSG`,`THROWABLE`,`NDC`,`CREATED_ON`,`OBJECT_ID`,`OBJECT_NAME`,`ORGANIZATION`,`OPERATION`) VALUES 
 (1822,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220646848996,'','','',''),
 (1823,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220647064462,'','','',''),
 (1824,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220647064594,'','','',''),
 (1825,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220647356961,'','','',''),
 (1826,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220647357099,'','','',''),
 (1827,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220647443398,'','','',''),
 (1828,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220647443499,'','','',''),
 (1829,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220647518804,'','','',''),
 (1830,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220647518908,'','','',''),
 (1831,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220658314140,'','','',''),
 (1832,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220658314347,'','','',''),
 (1833,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220660552140,'','','',''),
 (1834,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220660552229,'','','',''),
 (1835,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220660596090,'','','',''),
 (1836,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220660596179,'','','',''),
 (1837,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220660846871,'','','',''),
 (1838,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220660846968,'','','',''),
 (1839,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220660912978,'','','',''),
 (1840,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220660913068,'','','',''),
 (1841,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220660946529,'','','',''),
 (1842,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220660946619,'','','',''),
 (1843,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661019165,'','','',''),
 (1844,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661019293,'','','',''),
 (1845,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220661036920,'','','',''),
 (1846,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661047370,'','','',''),
 (1847,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220661164197,'','','',''),
 (1848,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220661164293,'','','',''),
 (1849,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661335586,'','','',''),
 (1850,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661335678,'','','',''),
 (1851,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220661417132,'','','',''),
 (1852,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220661417222,'','','',''),
 (1853,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661825935,'','','',''),
 (1854,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220661826026,'','','',''),
 (1855,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662077774,'','','',''),
 (1856,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662077861,'','','',''),
 (1857,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662134413,'','','',''),
 (1858,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662134511,'','','',''),
 (1859,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662191047,'','','',''),
 (1860,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662191132,'','','',''),
 (1861,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662330204,'','','',''),
 (1862,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662330303,'','','',''),
 (1863,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662459712,'','','',''),
 (1864,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662459811,'','','',''),
 (1865,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662520654,'','','',''),
 (1866,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662520738,'','','',''),
 (1867,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662620156,'','','',''),
 (1868,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662620250,'','','',''),
 (1869,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662749378,'','','',''),
 (1870,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220662749463,'','','',''),
 (1871,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662859044,'','','',''),
 (1872,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220662859132,'','','',''),
 (1873,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220663078515,'','','',''),
 (1874,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220663078602,'','','',''),
 (1875,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220663115093,'','','',''),
 (1876,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220663115177,'','','',''),
 (1877,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220893648718,'','','',''),
 (1878,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220893648882,'','','',''),
 (1879,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220894675887,'','','',''),
 (1880,'example','Dan-PC','INFO','http-8080-Processor25','user1','','Successful Login attempt for user user1','','',1220894676047,'','','',''),
 (1881,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220894712899,'','','',''),
 (1882,'example','Dan-PC','INFO','http-8080-Processor24','user1','','Successful Login attempt for user user1','','',1220894713004,'','','','');
COMMIT;
/*!40000 ALTER TABLE `log_message` ENABLE KEYS */;


--
-- Definition of table `long_key`
--

DROP TABLE IF EXISTS `long_key`;
CREATE TABLE `long_key` (
  `id` decimal(38,0) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `long_key`
--

/*!40000 ALTER TABLE `long_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `long_key` (`id`,`name`) VALUES 
 ('1234567890987650000','Long_Key_NAME 1234567890987654321');
COMMIT;
/*!40000 ALTER TABLE `long_key` ENABLE KEYS */;


--
-- Definition of table `long_primitive_key`
--

DROP TABLE IF EXISTS `long_primitive_key`;
CREATE TABLE `long_primitive_key` (
  `id` decimal(38,0) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `long_primitive_key`
--

/*!40000 ALTER TABLE `long_primitive_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `long_primitive_key` (`id`,`name`) VALUES 
 ('987654321234568000','Long_Primitive_Key_NAME 987654321234567890');
COMMIT;
/*!40000 ALTER TABLE `long_primitive_key` ENABLE KEYS */;


--
-- Definition of table `luggage`
--

DROP TABLE IF EXISTS `luggage`;
CREATE TABLE `luggage` (
  `id` int(8) NOT NULL,
  `discriminator` varchar(50) default NULL,
  `capacity` int(8) default NULL,
  `key_code` int(8) default NULL,
  `expandable` varchar(1) default NULL,
  `wheel_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_luggage_wheel` (`wheel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `luggage`
--

/*!40000 ALTER TABLE `luggage` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `luggage` (`id`,`discriminator`,`capacity`,`key_code`,`expandable`,`wheel_id`) VALUES 
 (1,'HardTop',75,627,NULL,1),
 (2,'HardTop',75,985,NULL,3),
 (3,'SoftTop',55,NULL,'1',1),
 (4,'SoftTop',35,NULL,'0',2),
 (5,'HardTopType',100,890,NULL,1);
COMMIT;
/*!40000 ALTER TABLE `luggage` ENABLE KEYS */;


--
-- Definition of table `mammal`
--

DROP TABLE IF EXISTS `mammal`;
CREATE TABLE `mammal` (
  `id` int(8) NOT NULL,
  `hair_color` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `mammal`
--

/*!40000 ALTER TABLE `mammal` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `mammal` (`id`,`hair_color`) VALUES 
 (1,'Hair_Color1'),
 (2,'Hair_Color2'),
 (3,'Hair_Color3'),
 (4,'Hair_Color4'),
 (5,'Hair_Color5');
COMMIT;
/*!40000 ALTER TABLE `mammal` ENABLE KEYS */;


--
-- Definition of table `monitor`
--

DROP TABLE IF EXISTS `monitor`;
CREATE TABLE `monitor` (
  `display_id` int(8) NOT NULL,
  `brand` varchar(50) default NULL,
  PRIMARY KEY  (`display_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `monitor`
--

/*!40000 ALTER TABLE `monitor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `monitor` (`display_id`,`brand`) VALUES 
 (1,'A'),
 (2,'B'),
 (3,'C'),
 (4,'D');
COMMIT;
/*!40000 ALTER TABLE `monitor` ENABLE KEYS */;


--
-- Definition of table `no_id_key`
--

DROP TABLE IF EXISTS `no_id_key`;
CREATE TABLE `no_id_key` (
  `my_key` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`my_key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `no_id_key`
--

/*!40000 ALTER TABLE `no_id_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `no_id_key` (`my_key`,`name`) VALUES 
 (1,'NoIdKey 1'),
 (2,'NoIdKey 2');
COMMIT;
/*!40000 ALTER TABLE `no_id_key` ENABLE KEYS */;


--
-- Definition of table `object_attribute`
--

DROP TABLE IF EXISTS `object_attribute`;
CREATE TABLE `object_attribute` (
  `OBJECT_ATTRIBUTE_ID` bigint(200) NOT NULL auto_increment,
  `CURRENT_VALUE` varchar(255) default NULL,
  `PREVIOUS_VALUE` varchar(255) default NULL,
  `ATTRIBUTE` varchar(255) NOT NULL,
  PRIMARY KEY  (`OBJECT_ATTRIBUTE_ID`),
  KEY `OAID_INDX` (`OBJECT_ATTRIBUTE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `object_attribute`
--

/*!40000 ALTER TABLE `object_attribute` DISABLE KEYS */;
SET AUTOCOMMIT=0;
COMMIT;
/*!40000 ALTER TABLE `object_attribute` ENABLE KEYS */;


--
-- Definition of table `objectattributes`
--

DROP TABLE IF EXISTS `objectattributes`;
CREATE TABLE `objectattributes` (
  `LOG_ID` bigint(200) NOT NULL default '0',
  `OBJECT_ATTRIBUTE_ID` bigint(200) NOT NULL default '0',
  KEY `Index_2` (`LOG_ID`),
  KEY `FK_objectattributes_2` (`OBJECT_ATTRIBUTE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `objectattributes`
--

/*!40000 ALTER TABLE `objectattributes` DISABLE KEYS */;
SET AUTOCOMMIT=0;
COMMIT;
/*!40000 ALTER TABLE `objectattributes` ENABLE KEYS */;


--
-- Definition of table `orderline`
--

DROP TABLE IF EXISTS `orderline`;
CREATE TABLE `orderline` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `orderline`
--

/*!40000 ALTER TABLE `orderline` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `orderline` (`id`,`name`) VALUES 
 (1,'Orderline_Name1'),
 (2,'Orderline_Name2'),
 (3,'Orderline_Name3'),
 (4,'Orderline_Name4'),
 (5,'Orderline_Name5');
COMMIT;
/*!40000 ALTER TABLE `orderline` ENABLE KEYS */;


--
-- Definition of table `organization`
--

DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
  `id` int(8) NOT NULL,
  `discriminator` varchar(50) default NULL,
  `name` varchar(50) default NULL,
  `agency_budget` int(8) default NULL,
  `ceo` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `organization`
--

/*!40000 ALTER TABLE `organization` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `organization` (`id`,`discriminator`,`name`,`agency_budget`,`ceo`) VALUES 
 (1,'govt','Public Org Name',1000,NULL),
 (2,'pvt','Private Org Name',NULL,'Private CEO Name');
COMMIT;
/*!40000 ALTER TABLE `organization` ENABLE KEYS */;


--
-- Definition of table `parent`
--

DROP TABLE IF EXISTS `parent`;
CREATE TABLE `parent` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `parent`
--

/*!40000 ALTER TABLE `parent` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `parent` (`id`,`name`) VALUES 
 (1,'Parent_Name1'),
 (2,'Parent_Name2'),
 (3,'Parent_Name3'),
 (4,'Parent_Name4'),
 (5,'Parent_Name5'),
 (6,'Parent_Name6'),
 (7,'Parent_Name7'),
 (8,'Parent_Name8'),
 (9,'Parent_Name9'),
 (10,'Parent_Name10');
COMMIT;
/*!40000 ALTER TABLE `parent` ENABLE KEYS */;


--
-- Definition of table `passanger`
--

DROP TABLE IF EXISTS `passanger`;
CREATE TABLE `passanger` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `passanger`
--

/*!40000 ALTER TABLE `passanger` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `passanger` (`id`,`name`) VALUES 
 (1,'John Doe'),
 (2,'Jane Doe');
COMMIT;
/*!40000 ALTER TABLE `passanger` ENABLE KEYS */;


--
-- Definition of table `payment`
--

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` int(8) NOT NULL,
  `amount` int(8) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `payment`
--

/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `payment` (`id`,`amount`) VALUES 
 (1,1),
 (2,2),
 (3,3),
 (4,4),
 (5,5);
COMMIT;
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;


--
-- Definition of table `pendant`
--

DROP TABLE IF EXISTS `pendant`;
CREATE TABLE `pendant` (
  `id` int(8) NOT NULL,
  `shape` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pendant`
--

/*!40000 ALTER TABLE `pendant` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `pendant` (`id`,`shape`) VALUES 
 (1,'Circle Pearl'),
 (2,'Heart Opal'),
 (3,'Oval Carnelian Shell Cameo');
COMMIT;
/*!40000 ALTER TABLE `pendant` ENABLE KEYS */;


--
-- Definition of table `person`
--

DROP TABLE IF EXISTS `person`;
CREATE TABLE `person` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `address_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uq_person_address_id` (`address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `person`
--

/*!40000 ALTER TABLE `person` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `person` (`id`,`name`,`address_id`) VALUES 
 (1,'Person_Name1',1),
 (2,'Person_Name2',2),
 (3,'Person_Name3',3),
 (4,'Person_Name4',NULL),
 (5,'Person_Name5',NULL);
COMMIT;
/*!40000 ALTER TABLE `person` ENABLE KEYS */;


--
-- Definition of table `plan_table`
--

DROP TABLE IF EXISTS `plan_table`;
CREATE TABLE `plan_table` (
  `statement_id` varchar(30) default NULL,
  `plan_id` decimal(22,0) default NULL,
  `timestamp` datetime default NULL,
  `remarks` varchar(4000) default NULL,
  `operation` varchar(30) default NULL,
  `options` varchar(255) default NULL,
  `object_node` varchar(128) default NULL,
  `object_owner` varchar(30) default NULL,
  `object_name` varchar(30) default NULL,
  `object_alias` varchar(65) default NULL,
  `object_instance` decimal(22,0) default NULL,
  `object_type` varchar(30) default NULL,
  `optimizer` varchar(255) default NULL,
  `search_columns` decimal(22,0) default NULL,
  `id` decimal(22,0) default NULL,
  `parent_id` decimal(22,0) default NULL,
  `depth` decimal(22,0) default NULL,
  `position` decimal(22,0) default NULL,
  `cost` decimal(22,0) default NULL,
  `cardinality` decimal(22,0) default NULL,
  `bytes` decimal(22,0) default NULL,
  `other_tag` varchar(255) default NULL,
  `partition_start` varchar(255) default NULL,
  `partition_stop` varchar(255) default NULL,
  `partition_id` decimal(22,0) default NULL,
  `other` longtext,
  `other_xml` longtext,
  `distribution` varchar(30) default NULL,
  `cpu_cost` decimal(22,0) default NULL,
  `io_cost` decimal(22,0) default NULL,
  `temp_space` decimal(22,0) default NULL,
  `access_predicates` varchar(4000) default NULL,
  `filter_predicates` varchar(4000) default NULL,
  `projection` varchar(4000) default NULL,
  `time` decimal(22,0) default NULL,
  `qblock_name` varchar(30) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `plan_table`
--

/*!40000 ALTER TABLE `plan_table` DISABLE KEYS */;
SET AUTOCOMMIT=0;
COMMIT;
/*!40000 ALTER TABLE `plan_table` ENABLE KEYS */;


--
-- Definition of table `private_teacher`
--

DROP TABLE IF EXISTS `private_teacher`;
CREATE TABLE `private_teacher` (
  `teacher_id` int(4) NOT NULL,
  `years_experience` int(4) default NULL,
  PRIMARY KEY  (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `private_teacher`
--

/*!40000 ALTER TABLE `private_teacher` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `private_teacher` (`teacher_id`,`years_experience`) VALUES 
 (1,5),
 (2,10),
 (3,15);
COMMIT;
/*!40000 ALTER TABLE `private_teacher` ENABLE KEYS */;


--
-- Definition of table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `orderline_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uq_product_orderline_id` (`orderline_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product`
--

/*!40000 ALTER TABLE `product` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `product` (`id`,`name`,`orderline_id`) VALUES 
 (1,'Product_Name1',1),
 (2,'Product_Name2',2),
 (3,'Product_Name3',NULL);
COMMIT;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;


--
-- Definition of table `professor`
--

DROP TABLE IF EXISTS `professor`;
CREATE TABLE `professor` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `professor`
--

/*!40000 ALTER TABLE `professor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `professor` (`id`,`name`) VALUES 
 (1,'Professor_Name1'),
 (2,'Professor_Name2'),
 (3,'Professor_Name3'),
 (4,'Professor_Name4'),
 (5,'Professor_Name5'),
 (6,'Professor_Name6'),
 (7,'Professor_Name7'),
 (8,'Professor_Name8'),
 (9,'Professor_Name9'),
 (10,'Professor_Name10'),
 (11,'Professor_Name11'),
 (12,'Professor_Name12'),
 (13,'Professor_Name13'),
 (14,'Professor_Name14'),
 (15,'Professor_Name15');
COMMIT;
/*!40000 ALTER TABLE `professor` ENABLE KEYS */;


--
-- Definition of table `project`
--

DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `project`
--

/*!40000 ALTER TABLE `project` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `project` (`id`,`name`) VALUES 
 (1,'Project_Name1'),
 (2,'Project_Name2'),
 (3,'Project_Name3'),
 (4,'Project_Name4'),
 (5,'Project_Name5'),
 (6,'Project_Name6'),
 (7,'Project_Name7'),
 (8,'Project_Name8'),
 (9,'Project_Name9'),
 (10,'Project_Name10');
COMMIT;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;


--
-- Definition of table `pupil`
--

DROP TABLE IF EXISTS `pupil`;
CREATE TABLE `pupil` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `teacher_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_pupil_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pupil`
--

/*!40000 ALTER TABLE `pupil` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `pupil` (`id`,`name`,`teacher_id`) VALUES 
 (1,'Pupil_Name_1',1),
 (2,'Pupil_Name_2',1),
 (3,'Pupil_Name_3',3),
 (4,'Pupil_Name_4',3);
COMMIT;
/*!40000 ALTER TABLE `pupil` ENABLE KEYS */;


--
-- Definition of table `quest_sl_temp_explain1`
--

DROP TABLE IF EXISTS `quest_sl_temp_explain1`;
CREATE TABLE `quest_sl_temp_explain1` (
  `statement_id` varchar(30) default NULL,
  `timestamp` datetime default NULL,
  `remarks` varchar(80) default NULL,
  `operation` varchar(30) default NULL,
  `options` varchar(255) default NULL,
  `object_node` varchar(128) default NULL,
  `object_owner` varchar(30) default NULL,
  `object_name` varchar(30) default NULL,
  `object_instance` decimal(22,0) default NULL,
  `object_type` varchar(30) default NULL,
  `optimizer` varchar(255) default NULL,
  `search_columns` decimal(22,0) default NULL,
  `id` decimal(22,0) default NULL,
  `parent_id` decimal(22,0) default NULL,
  `position` decimal(22,0) default NULL,
  `cost` decimal(22,0) default NULL,
  `cardinality` decimal(22,0) default NULL,
  `bytes` decimal(22,0) default NULL,
  `other_tag` varchar(255) default NULL,
  `partition_start` varchar(255) default NULL,
  `partition_stop` varchar(255) default NULL,
  `partition_id` decimal(22,0) default NULL,
  `other` longtext,
  `distribution` varchar(30) default NULL,
  `cpu_cost` decimal(38,0) default NULL,
  `io_cost` decimal(38,0) default NULL,
  `temp_space` decimal(38,0) default NULL,
  `access_predicates` varchar(4000) default NULL,
  `filter_predicates` varchar(4000) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `quest_sl_temp_explain1`
--

/*!40000 ALTER TABLE `quest_sl_temp_explain1` DISABLE KEYS */;
SET AUTOCOMMIT=0;
COMMIT;
/*!40000 ALTER TABLE `quest_sl_temp_explain1` ENABLE KEYS */;


--
-- Definition of table `restaurant`
--

DROP TABLE IF EXISTS `restaurant`;
CREATE TABLE `restaurant` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `restaurant`
--

/*!40000 ALTER TABLE `restaurant` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `restaurant` (`id`,`name`) VALUES 
 (1,'Rest1'),
 (2,'Rest2'),
 (3,'Rest3'),
 (4,'Rest4'),
 (5,'Rest5');
COMMIT;
/*!40000 ALTER TABLE `restaurant` ENABLE KEYS */;


--
-- Definition of table `saltwater_fish_tank`
--

DROP TABLE IF EXISTS `saltwater_fish_tank`;
CREATE TABLE `saltwater_fish_tank` (
  `id` int(8) NOT NULL,
  `shape` varchar(50) default NULL,
  `num_gallons` int(8) default NULL,
  `protein_skimmer_model` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `saltwater_fish_tank`
--

/*!40000 ALTER TABLE `saltwater_fish_tank` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `saltwater_fish_tank` (`id`,`shape`,`num_gallons`,`protein_skimmer_model`) VALUES 
 (3,'Rectangular',60,'Berlin X2 Turbo Skimmer'),
 (4,'Hexagonal',20,'Prizm Pro Deluxe Skimmer');
COMMIT;
/*!40000 ALTER TABLE `saltwater_fish_tank` ENABLE KEYS */;


--
-- Definition of table `saltwater_fish_tank_substrate`
--

DROP TABLE IF EXISTS `saltwater_fish_tank_substrate`;
CREATE TABLE `saltwater_fish_tank_substrate` (
  `saltwater_fish_tank_id` int(8) NOT NULL,
  `substrate_id` int(8) NOT NULL,
  PRIMARY KEY  (`saltwater_fish_tank_id`,`substrate_id`),
  KEY `fk_swft_substrate_substrate` (`substrate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `saltwater_fish_tank_substrate`
--

/*!40000 ALTER TABLE `saltwater_fish_tank_substrate` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `saltwater_fish_tank_substrate` (`saltwater_fish_tank_id`,`substrate_id`) VALUES 
 (3,1),
 (3,2),
 (4,3),
 (4,4);
COMMIT;
/*!40000 ALTER TABLE `saltwater_fish_tank_substrate` ENABLE KEYS */;


--
-- Definition of table `shirt`
--

DROP TABLE IF EXISTS `shirt`;
CREATE TABLE `shirt` (
  `id` int(8) NOT NULL,
  `style` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `shirt`
--

/*!40000 ALTER TABLE `shirt` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `shirt` (`id`,`style`) VALUES 
 (1,'Collar'),
 (2,'Western'),
 (3,'T-Shirt');
COMMIT;
/*!40000 ALTER TABLE `shirt` ENABLE KEYS */;


--
-- Definition of table `shirt_button`
--

DROP TABLE IF EXISTS `shirt_button`;
CREATE TABLE `shirt_button` (
  `shirt_id` int(8) NOT NULL,
  `button_id` int(8) NOT NULL,
  PRIMARY KEY  (`shirt_id`,`button_id`),
  UNIQUE KEY `uq_shirt_button_shirt_id` (`shirt_id`),
  KEY `fk_shirt_button_button` (`button_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `shirt_button`
--

/*!40000 ALTER TABLE `shirt_button` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `shirt_button` (`shirt_id`,`button_id`) VALUES 
 (1,1),
 (2,2);
COMMIT;
/*!40000 ALTER TABLE `shirt_button` ENABLE KEYS */;


--
-- Definition of table `shoes`
--

DROP TABLE IF EXISTS `shoes`;
CREATE TABLE `shoes` (
  `id` int(8) NOT NULL,
  `discriminator` varchar(50) default NULL,
  `color` varchar(50) default NULL,
  `sports_type` varchar(50) default NULL,
  `designer_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_shoes_designer` (`designer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `shoes`
--

/*!40000 ALTER TABLE `shoes` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `shoes` (`id`,`discriminator`,`color`,`sports_type`,`designer_id`) VALUES 
 (1,'DesignerShoes','White',NULL,2),
 (2,'SportsShoes','Red','BasketBall',NULL),
 (3,'DesignerShoes','Black',NULL,3);
COMMIT;
/*!40000 ALTER TABLE `shoes` ENABLE KEYS */;


--
-- Definition of table `song`
--

DROP TABLE IF EXISTS `song`;
CREATE TABLE `song` (
  `id` int(8) NOT NULL,
  `title` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `song`
--

/*!40000 ALTER TABLE `song` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `song` (`id`,`title`) VALUES 
 (1,'Albinoni:  Concerto in B Flat, OP. 7 No. 3'),
 (2,'Albinoni:  Concerto in D Major, Op. 7 No. 6'),
 (3,'Marcello:  Concerto in D Minor'),
 (4,'Vivaldi:  Concerto in F Major F VII 2'),
 (5,'Vivaldi:  Concerto in A Minor F VII 5'),
 (6,'Cimarosa/Benjamin:  Concerto in C Minor'),
 (7,'Rubenstein: Melody in F, Op. 3 No. 1'),
 (8,'Schubert: Ave Maria'),
 (9,'Rimsky-Korsakov:  The Flight of the Bumble Bee'),
 (10,'Schumann:  Traumerei'),
 (11,'Dvorak:  Songs My Mother Taught Me'),
 (12,'Saint-Seans:  The Swan');
COMMIT;
/*!40000 ALTER TABLE `song` ENABLE KEYS */;


--
-- Definition of table `string_key`
--

DROP TABLE IF EXISTS `string_key`;
CREATE TABLE `string_key` (
  `id` varchar(50) NOT NULL default '',
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `string_key`
--

/*!40000 ALTER TABLE `string_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `string_key` (`id`,`name`) VALUES 
 ('ID1','String_Key_Name1'),
 ('ID2','String_Key_Name2'),
 ('ID3','String_Key_Name3'),
 ('ID4','String_Key_Name4'),
 ('ID5','String_Key_Name5');
COMMIT;
/*!40000 ALTER TABLE `string_key` ENABLE KEYS */;


--
-- Definition of table `string_primitive_key`
--

DROP TABLE IF EXISTS `string_primitive_key`;
CREATE TABLE `string_primitive_key` (
  `id` varchar(50) NOT NULL default '',
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `string_primitive_key`
--

/*!40000 ALTER TABLE `string_primitive_key` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `string_primitive_key` (`id`,`name`) VALUES 
 ('id1','String_Primitive_Key id1'),
 ('id2','String_Primitive_Key id2');
COMMIT;
/*!40000 ALTER TABLE `string_primitive_key` ENABLE KEYS */;


--
-- Definition of table `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

/*!40000 ALTER TABLE `student` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `student` (`id`,`name`) VALUES 
 (1,'Student_Name1'),
 (2,'Student_Name2'),
 (3,'Student_Name3'),
 (4,'Student_Name4'),
 (5,'Student_Name5'),
 (6,'Student_Name6'),
 (7,'Student_Name7'),
 (8,'Student_Name8'),
 (9,'Student_Name9'),
 (10,'Student_Name10');
COMMIT;
/*!40000 ALTER TABLE `student` ENABLE KEYS */;


--
-- Definition of table `substrate`
--

DROP TABLE IF EXISTS `substrate`;
CREATE TABLE `substrate` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `substrate`
--

/*!40000 ALTER TABLE `substrate` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `substrate` (`id`,`name`) VALUES 
 (1,'Live Rock'),
 (2,'Sand'),
 (3,'Crushed Coral'),
 (4,'River Pebbles');
COMMIT;
/*!40000 ALTER TABLE `substrate` ENABLE KEYS */;


--
-- Definition of table `suit`
--

DROP TABLE IF EXISTS `suit`;
CREATE TABLE `suit` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  `deck_id` int(8) default NULL,
  PRIMARY KEY  (`id`),
  KEY `fk_suit_deck` (`deck_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `suit`
--

/*!40000 ALTER TABLE `suit` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `suit` (`id`,`name`,`deck_id`) VALUES 
 (1,'Spade',1),
 (2,'Flower',1),
 (3,'Diamond',1),
 (4,'Heart',1);
COMMIT;
/*!40000 ALTER TABLE `suit` ENABLE KEYS */;


--
-- Definition of table `tank_accessory`
--

DROP TABLE IF EXISTS `tank_accessory`;
CREATE TABLE `tank_accessory` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tank_accessory`
--

/*!40000 ALTER TABLE `tank_accessory` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `tank_accessory` (`id`,`name`) VALUES 
 (1,'Filter'),
 (2,'Heater'),
 (3,'Lighting'),
 (4,'Protein Skimmer');
COMMIT;
/*!40000 ALTER TABLE `tank_accessory` ENABLE KEYS */;


--
-- Definition of table `tank_tank_accessory`
--

DROP TABLE IF EXISTS `tank_tank_accessory`;
CREATE TABLE `tank_tank_accessory` (
  `tank_id` int(8) NOT NULL,
  `tank_accessory_id` int(8) NOT NULL,
  `tank_discriminator` varchar(50) NOT NULL,
  PRIMARY KEY  (`tank_id`,`tank_accessory_id`),
  KEY `fk_tank_tank_accessory_ta` (`tank_accessory_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tank_tank_accessory`
--

/*!40000 ALTER TABLE `tank_tank_accessory` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `tank_tank_accessory` (`tank_id`,`tank_accessory_id`,`tank_discriminator`) VALUES 
 (1,1,'FreshwaterFishTank'),
 (1,2,'FreshwaterFishTank'),
 (2,2,'FreshwaterFishTank'),
 (2,3,'FreshwaterFishTank'),
 (3,1,'SaltwaterFishTank'),
 (3,4,'SaltwaterFishTank'),
 (4,1,'SaltwaterFishTank'),
 (4,2,'SaltwaterFishTank'),
 (4,4,'SaltwaterFishTank');
COMMIT;
/*!40000 ALTER TABLE `tank_tank_accessory` ENABLE KEYS */;


--
-- Definition of table `teacher`
--

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `teacher`
--

/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `teacher` (`id`,`name`) VALUES 
 (1,'Teacher_Name1'),
 (2,'Teacher_Name2'),
 (3,'Teacher_Name3');
COMMIT;
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;


--
-- Definition of table `tenured_professor`
--

DROP TABLE IF EXISTS `tenured_professor`;
CREATE TABLE `tenured_professor` (
  `professor_id` int(8) NOT NULL,
  `tenured_year` int(4) default NULL,
  PRIMARY KEY  (`professor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tenured_professor`
--

/*!40000 ALTER TABLE `tenured_professor` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `tenured_professor` (`professor_id`,`tenured_year`) VALUES 
 (1,1),
 (2,2),
 (3,3),
 (4,4),
 (5,5);
COMMIT;
/*!40000 ALTER TABLE `tenured_professor` ENABLE KEYS */;


--
-- Definition of table `undergraduate_student`
--

DROP TABLE IF EXISTS `undergraduate_student`;
CREATE TABLE `undergraduate_student` (
  `student_id` int(8) NOT NULL,
  PRIMARY KEY  (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `undergraduate_student`
--

/*!40000 ALTER TABLE `undergraduate_student` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `undergraduate_student` (`student_id`) VALUES 
 (1),
 (2),
 (3),
 (4),
 (5);
COMMIT;
/*!40000 ALTER TABLE `undergraduate_student` ENABLE KEYS */;


--
-- Definition of table `utensil`
--

DROP TABLE IF EXISTS `utensil`;
CREATE TABLE `utensil` (
  `id` int(8) NOT NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `utensil`
--

/*!40000 ALTER TABLE `utensil` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `utensil` (`id`,`name`) VALUES 
 (1,'Spoon'),
 (2,'Knife'),
 (3,'Fork');
COMMIT;
/*!40000 ALTER TABLE `utensil` ENABLE KEYS */;


--
-- Definition of table `wheel`
--

DROP TABLE IF EXISTS `wheel`;
CREATE TABLE `wheel` (
  `id` int(8) NOT NULL,
  `radius` int(8) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `wheel`
--

/*!40000 ALTER TABLE `wheel` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `wheel` (`id`,`radius`) VALUES 
 (1,1),
 (2,5),
 (3,10);
COMMIT;
/*!40000 ALTER TABLE `wheel` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

--
-- Definition of table `csm_application`
--

DROP TABLE IF EXISTS `CSM_APPLICATION`;
CREATE TABLE CSM_APPLICATION ( 
	APPLICATION_ID BIGINT AUTO_INCREMENT  NOT NULL,
	APPLICATION_NAME VARCHAR(255) NOT NULL,
	APPLICATION_DESCRIPTION VARCHAR(200) NOT NULL,
	DECLARATIVE_FLAG BOOL NOT NULL DEFAULT 0,
	ACTIVE_FLAG BOOL NOT NULL DEFAULT 0,
	UPDATE_DATE DATE DEFAULT '0000-00-00',
	DATABASE_URL VARCHAR(100),
	DATABASE_USER_NAME VARCHAR(100),
	DATABASE_PASSWORD VARCHAR(100),
	DATABASE_DIALECT VARCHAR(100),
	DATABASE_DRIVER VARCHAR(100),
	PRIMARY KEY(APPLICATION_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_application`
--

/*!40000 ALTER TABLE `csm_application` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_application` (`application_id`,`application_name`,`application_description`,`declarative_flag`,`active_flag`,`update_date`,`database_url`,`database_user_name`,`database_password`,`database_dialect`,`database_driver`) VALUES 
 ('1','csmupt','UPT Super Admin Application',0,0,'2007-02-28 13:03:02',NULL,NULL,NULL,NULL,NULL),
 ('2','sdk','sdk',1,1,'2008-04-02 00:00:00','','','2+crCBHCPUC8j2uyHEABIQ==','org.hibernate.dialect.MySQLDialect','org.gjt.mm.mysql.Driver');
COMMIT;
/*!40000 ALTER TABLE `csm_application` ENABLE KEYS */;


--
-- Definition of table `csm_filter_clause`
--

DROP TABLE IF EXISTS CSM_FILTER_CLAUSE;
CREATE TABLE CSM_FILTER_CLAUSE ( 
	FILTER_CLAUSE_ID BIGINT AUTO_INCREMENT  NOT NULL,
	CLASS_NAME VARCHAR(100) NOT NULL,
	FILTER_CHAIN VARCHAR(2000) NOT NULL,
	TARGET_CLASS_NAME VARCHAR (100) NOT NULL,
	TARGET_CLASS_ATTRIBUTE_NAME VARCHAR (100) NOT NULL,
	TARGET_CLASS_ATTRIBUTE_TYPE VARCHAR (100) NOT NULL,
	TARGET_CLASS_ALIAS VARCHAR (100),
	TARGET_CLASS_ATTRIBUTE_ALIAS VARCHAR (100),
	GENERATED_SQL_USER VARCHAR (4000) NOT NULL,
	GENERATED_SQL_GROUP VARCHAR (4000) NOT NULL,
	APPLICATION_ID BIGINT NOT NULL,
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PRIMARY KEY(FILTER_CLAUSE_ID)	
)Type=InnoDB 
;

--
-- Dumping data for table `csm_filter_clause`
--

/*!40000 ALTER TABLE `csm_filter_clause` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_filter_clause` (`filter_clause_id`,`class_name`,`filter_chain`,`target_class_name`,`target_class_attribute_name`,`target_class_attribute_type`,`target_class_alias`,`target_class_attribute_alias`,`generated_sql_user`,`application_id`,`update_date`,`generated_sql_group`) VALUES 
 ('17','gov.nih.nci.cacoresdk.domain.other.levelassociation.Card','suit, deck','gov.nih.nci.cacoresdk.domain.other.levelassociation.Deck - deck','id','java.lang.Integer',NULL,NULL,'ID in (select table_name_csm_.ID   from CARD table_name_csm_, SUIT suit1_, DECK deck2_ where table_name_csm_.SUIT_ID=suit1_.ID and suit1_.DECK_ID=deck2_.ID and deck2_.ID in ( select pe.attribute_value from csm_protection_group pg, csm_protection_element pe, csm_pg_pe pgpe, csm_user_group_role_pg ugrpg, csm_user u, csm_role_privilege rp, csm_role r, csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.user_id = u.user_id and ugrpg.protection_group_id = ANY (select pg1.protection_group_id from csm_protection_group pg1 where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id = (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id)) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'gov.nih.nci.cacoresdk.domain.other.levelassociation.Deck\' and pe.attribute=\'id\' and p.privilege_name=\'READ\' and u.login_name=:USER_NAME and pe.application_id=:APPLICATION_ID))','2','2008-06-30 00:00:00','ID in (select table_name_csm_.ID   from CARD table_name_csm_, SUIT suit1_, DECK deck2_ where table_name_csm_.SUIT_ID=suit1_.ID and suit1_.DECK_ID=deck2_.ID and deck2_.ID in ( select distinct pe.attribute_value from csm_protection_group pg, 	csm_protection_element pe, 	csm_pg_pe pgpe,	csm_user_group_role_pg ugrpg, 	csm_group g, 	csm_role_privilege rp, 	csm_role r, 	csm_privilege p where ugrpg.role_id = r.role_id and ugrpg.group_id = g.group_id and ugrpg.protection_group_id = any ( select pg1.protection_group_id from csm_protection_group pg1  where pg1.protection_group_id = pg.protection_group_id or pg1.protection_group_id =  (select pg2.parent_protection_group_id from csm_protection_group pg2 where pg2.protection_group_id = pg.protection_group_id) ) and pg.protection_group_id = pgpe.protection_group_id and pgpe.protection_element_id = pe.protection_element_id and r.role_id = rp.role_id and rp.privilege_id = p.privilege_id and pe.object_id= \'gov.nih.nci.cacoresdk.domain.other.levelassociation.Deck\' and pe.attribute=\'id\' and p.privilege_name=\'READ\' and g.group_name IN (:GROUP_NAMES ) and pe.application_id=:APPLICATION_ID))');
COMMIT;
/*!40000 ALTER TABLE `csm_filter_clause` ENABLE KEYS */;


--
-- Definition of table `csm_group`
--

DROP TABLE IF EXISTS CSM_GROUP;
CREATE TABLE CSM_GROUP ( 
	GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,
	GROUP_NAME VARCHAR(255) NOT NULL,
	GROUP_DESC VARCHAR(200),
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	APPLICATION_ID BIGINT NOT NULL,
	PRIMARY KEY(GROUP_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_group`
--

/*!40000 ALTER TABLE `csm_group` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_group` (`group_id`,`group_name`,`group_desc`,`update_date`,`application_id`) VALUES 
 ('2','Group1','Group 1 - Access to All PE\'s.  Same as user1','2008-06-30 00:00:00','2'),
 ('3','Group2','Group 2 - Same limited access as user2','2008-06-30 00:00:00','2'),
 ('4','Group3','Group3 - No access','2008-06-30 00:00:00','2');
COMMIT;
/*!40000 ALTER TABLE `csm_group` ENABLE KEYS */;


--
-- Definition of table `csm_pg_pe`
--

DROP TABLE IF EXISTS CSM_PG_PE;
CREATE TABLE CSM_PG_PE ( 
	PG_PE_ID BIGINT AUTO_INCREMENT  NOT NULL,
	PROTECTION_GROUP_ID BIGINT NOT NULL,
	PROTECTION_ELEMENT_ID BIGINT NOT NULL,
	UPDATE_DATE DATE DEFAULT '0000-00-00',
	PRIMARY KEY(PG_PE_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_pg_pe`
--

/*!40000 ALTER TABLE `csm_pg_pe` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_pg_pe` (`pg_pe_id`,`protection_group_id`,`protection_element_id`,`update_date`) VALUES 
 ('107','2','11','2007-07-17 15:30:19'),
 ('108','2','24','2007-07-17 15:30:19'),
 ('110','2','8','2007-07-17 15:30:19'),
 ('111','3','37','2007-07-17 15:30:19'),
 ('670','1','100','2007-12-19 16:40:11'),
 ('671','1','33','2007-12-19 16:40:11'),
 ('672','1','81','2007-12-19 16:40:11'),
 ('673','1','108','2007-12-19 16:40:11'),
 ('674','1','88','2007-12-19 16:40:11'),
 ('675','1','83','2007-12-19 16:40:11'),
 ('676','1','77','2007-12-19 16:40:11'),
 ('677','1','4','2007-12-19 16:40:11'),
 ('678','1','113','2007-12-19 16:40:11'),
 ('679','1','17','2007-12-19 16:40:11'),
 ('680','1','15','2007-12-19 16:40:11'),
 ('681','1','65','2007-12-19 16:40:11'),
 ('682','1','95','2007-12-19 16:40:11'),
 ('683','1','93','2007-12-19 16:40:11'),
 ('684','1','90','2007-12-19 16:40:11'),
 ('685','1','103','2007-12-19 16:40:11'),
 ('686','1','20','2007-12-19 16:40:11'),
 ('687','1','101','2007-12-19 16:40:11'),
 ('688','1','18','2007-12-19 16:40:11'),
 ('689','1','67','2007-12-19 16:40:11'),
 ('690','1','76','2007-12-19 16:40:11'),
 ('691','1','92','2007-12-19 16:40:11'),
 ('692','1','80','2007-12-19 16:40:11'),
 ('693','1','82','2007-12-19 16:40:11'),
 ('694','1','116','2007-12-19 16:40:11'),
 ('695','1','24','2007-12-19 16:40:11'),
 ('696','1','91','2007-12-19 16:40:11'),
 ('697','1','98','2007-12-19 16:40:11'),
 ('698','1','32','2007-12-19 16:40:11'),
 ('699','1','14','2007-12-19 16:40:11'),
 ('700','1','23','2007-12-19 16:40:11'),
 ('701','1','75','2007-12-19 16:40:11'),
 ('702','1','99','2007-12-19 16:40:11'),
 ('703','1','16','2007-12-19 16:40:11'),
 ('704','1','38','2007-12-19 16:40:11'),
 ('705','1','66','2007-12-19 16:40:11'),
 ('706','1','37','2007-12-19 16:40:11'),
 ('708','1','30','2007-12-19 16:40:11'),
 ('709','1','45','2007-12-19 16:40:11'),
 ('710','1','35','2007-12-19 16:40:11'),
 ('711','1','94','2007-12-19 16:40:11'),
 ('712','1','41','2007-12-19 16:40:11'),
 ('713','1','86','2007-12-19 16:40:11'),
 ('714','1','79','2007-12-19 16:40:11'),
 ('715','1','27','2007-12-19 16:40:11'),
 ('716','1','13','2007-12-19 16:40:11'),
 ('717','1','68','2007-12-19 16:40:11'),
 ('718','1','71','2007-12-19 16:40:11'),
 ('719','1','26','2007-12-19 16:40:11'),
 ('720','1','7','2007-12-19 16:40:11'),
 ('721','1','25','2007-12-19 16:40:11'),
 ('722','1','115','2007-12-19 16:40:11'),
 ('723','1','85','2007-12-19 16:40:11'),
 ('724','1','110','2007-12-19 16:40:11'),
 ('725','1','36','2007-12-19 16:40:11'),
 ('726','1','31','2007-12-19 16:40:11'),
 ('727','1','11','2007-12-19 16:40:11'),
 ('728','1','74','2007-12-19 16:40:11'),
 ('729','1','84','2007-12-19 16:40:11'),
 ('730','1','102','2007-12-19 16:40:11'),
 ('731','1','97','2007-12-19 16:40:11'),
 ('732','1','8','2007-12-19 16:40:11'),
 ('733','1','43','2007-12-19 16:40:11'),
 ('734','1','42','2007-12-19 16:40:11'),
 ('735','1','28','2007-12-19 16:40:11'),
 ('736','1','70','2007-12-19 16:40:11'),
 ('737','1','12','2007-12-19 16:40:11'),
 ('738','1','29','2007-12-19 16:40:11'),
 ('739','1','72','2007-12-19 16:40:11'),
 ('740','1','44','2007-12-19 16:40:11'),
 ('741','1','114','2007-12-19 16:40:11'),
 ('742','1','22','2007-12-19 16:40:11'),
 ('743','1','40','2007-12-19 16:40:11'),
 ('744','1','6','2007-12-19 16:40:11'),
 ('745','1','87','2007-12-19 16:40:11'),
 ('746','1','21','2007-12-19 16:40:11'),
 ('747','1','9','2007-12-19 16:40:11'),
 ('748','1','34','2007-12-19 16:40:11'),
 ('749','1','104','2007-12-19 16:40:11'),
 ('750','1','89','2007-12-19 16:40:11'),
 ('751','1','107','2007-12-19 16:40:11'),
 ('752','1','19','2007-12-19 16:40:11'),
 ('753','1','5','2007-12-19 16:40:11'),
 ('754','1','96','2007-12-19 16:40:11'),
 ('755','1','73','2007-12-19 16:40:11'),
 ('765','1','39','2007-12-27 12:41:17'),
 ('766','4','24','2008-05-26 16:33:58'),
 ('773','7','40','2008-06-30 15:14:32'),
 ('774','7','5','2008-06-30 15:14:32'),
 ('775','7','39','2008-06-30 15:14:32'),
 ('776','7','41','2008-06-30 15:14:32'),
 ('777','7','114','2008-06-30 15:14:32'),
 ('778','7','38','2008-06-30 15:14:32'),
 ('779','1','117','2008-09-05 12:27:56');
COMMIT;
/*!40000 ALTER TABLE `csm_pg_pe` ENABLE KEYS */;


--
-- Definition of table `csm_privilege`
--

DROP TABLE IF EXISTS CSM_PRIVILEGE;
CREATE TABLE CSM_PRIVILEGE ( 
	PRIVILEGE_ID BIGINT AUTO_INCREMENT  NOT NULL,
	PRIVILEGE_NAME VARCHAR(100) NOT NULL,
	PRIVILEGE_DESCRIPTION VARCHAR(200),
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PRIMARY KEY(PRIVILEGE_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_privilege`
--

/*!40000 ALTER TABLE `csm_privilege` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_privilege` (`privilege_id`,`privilege_name`,`privilege_description`,`update_date`) VALUES 
 ('1','CREATE','This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection','2007-02-28 13:03:03'),
 ('2','ACCESS','This privilege allows a user to access a particular resource.  ','2007-02-28 13:03:04'),
 ('3','READ','This privilege permits the user to read data from a file, URL, socket, database, or an object. ','2007-02-28 13:03:04'),
 ('4','WRITE','This privilege allows a user to write data to a file, URL, socket, database, or object. ','2007-02-28 13:03:04'),
 ('5','UPDATE','This privilege grants permission at an entity level and signifies that the user is allowed to update and modify data for a particular entity.','2007-02-28 13:03:04'),
 ('6','DELETE','This privilege permits a user to delete a logical entity.','2007-02-28 13:03:04'),
 ('7','EXECUTE','This privilege allows a user to execute a particular resource.','2007-02-28 13:03:04');
COMMIT;
/*!40000 ALTER TABLE `csm_privilege` ENABLE KEYS */;


--
-- Definition of table `csm_protection_element`
--

DROP TABLE IF EXISTS CSM_PROTECTION_ELEMENT;
CREATE TABLE CSM_PROTECTION_ELEMENT ( 
	PROTECTION_ELEMENT_ID BIGINT AUTO_INCREMENT  NOT NULL,
	PROTECTION_ELEMENT_NAME VARCHAR(100) NOT NULL,
	PROTECTION_ELEMENT_DESCRIPTION VARCHAR(200),
	OBJECT_ID VARCHAR(100) NOT NULL,
	ATTRIBUTE VARCHAR(100) ,
	ATTRIBUTE_VALUE VARCHAR(100) ,
	PROTECTION_ELEMENT_TYPE VARCHAR(100),
	APPLICATION_ID BIGINT NOT NULL,
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PRIMARY KEY(PROTECTION_ELEMENT_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_protection_element`
--

/*!40000 ALTER TABLE `csm_protection_element` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_protection_element` (`protection_element_id`,`protection_element_name`,`protection_element_description`,`object_id`,`attribute`,`protection_element_type`,`application_id`,`update_date`,`attribute_value`) VALUES 
 ('1','csmupt','UPT Super Admin Application','csmupt',NULL,NULL,'1','2007-02-28 13:03:03',NULL),
 ('2','sdk','sdk Application','sdk',NULL,NULL,'1','2008-04-02 00:00:00',NULL),
 ('4','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Cash','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Cash','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Cash',NULL,NULL,'2','2007-03-01 15:03:24',NULL),
 ('5','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit','issuingBank',NULL,'2','2007-12-17 00:00:00',NULL),
 ('6','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Payment','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Payment','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Payment',NULL,NULL,'2','2007-03-01 15:03:24',NULL),
 ('7','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.GraduateStudent','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.GraduateStudent','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.GraduateStudent',NULL,NULL,'2','2007-03-01 15:03:24',NULL),
 ('8','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.Student','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.Student','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.Student',NULL,NULL,'2','2007-03-01 15:03:24',NULL),
 ('9','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.UndergraduateStudent','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.UndergraduateStudent','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.UndergraduateStudent',NULL,NULL,'2','2007-03-01 15:03:24',NULL),
 ('11','gov.nih.nci.cacoresdk.domain.inheritance.onechild.Mammal','gov.nih.nci.cacoresdk.domain.inheritance.onechild.Mammal','gov.nih.nci.cacoresdk.domain.inheritance.onechild.Mammal',NULL,NULL,'2','2007-03-01 15:03:24',NULL),
 ('12','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Assistant','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Assistant','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Assistant',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('13','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.AssistantProfessor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.AssistantProfessor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.AssistantProfessor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('14','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.AssociateProfessor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.AssociateProfessor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.AssociateProfessor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('15','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Professor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Professor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.Professor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('16','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.TenuredProfessor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.TenuredProfessor','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.TenuredProfessor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('17','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.CRTMonitor','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.CRTMonitor','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.CRTMonitor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('18','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Display','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Display','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Display',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('19','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.LCDMonitor','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.LCDMonitor','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.LCDMonitor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('20','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Monitor','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Monitor','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.Monitor',NULL,NULL,'2','2007-03-01 15:03:25',NULL),
 ('21','gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Employee','gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Employee','gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Employee',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('22','gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Project','gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Project','gov.nih.nci.cacoresdk.domain.manytomany.bidirectional.Project',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('23','gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author','gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author','gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Author',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('24','gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Book','gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Book','gov.nih.nci.cacoresdk.domain.manytomany.unidirectional.Book',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('25','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Chef','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Chef','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Chef',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('26','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Restaurant','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Restaurant','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.Restaurant',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('27','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.Computer','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.Computer','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.Computer',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('28','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.HardDrive','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.HardDrive','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.HardDrive',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('29','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.Key','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.Key','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.Key',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('30','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.KeyChain','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.KeyChain','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.KeyChain',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('31','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.OrderLine','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.OrderLine','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.OrderLine',NULL,NULL,'2','2007-03-01 15:03:26',NULL),
 ('32','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.Product','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.Product','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.Product',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('33','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Child','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Child','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Child',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('34','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Parent','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Parent','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Parent',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('35','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Address','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Address','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Address',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('36','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.Person',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('37','gov.nih.nci.cacoresdk.domain.other.datatype.AllDataType','gov.nih.nci.cacoresdk.domain.other.datatype.AllDataType','gov.nih.nci.cacoresdk.domain.other.datatype.AllDataType',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('38','gov.nih.nci.cacoresdk.domain.other.levelassociation.Card','gov.nih.nci.cacoresdk.domain.other.levelassociation.Card','gov.nih.nci.cacoresdk.domain.other.levelassociation.Card','Name',NULL,'2','2007-12-21 00:00:00','Ace'),
 ('39','gov.nih.nci.cacoresdk.domain.other.levelassociation.Deck','gov.nih.nci.cacoresdk.domain.other.levelassociation.Deck','gov.nih.nci.cacoresdk.domain.other.levelassociation.Deck','id',NULL,'2','2008-06-30 00:00:00','1'),
 ('40','gov.nih.nci.cacoresdk.domain.other.levelassociation.Hand','gov.nih.nci.cacoresdk.domain.other.levelassociation.Hand','gov.nih.nci.cacoresdk.domain.other.levelassociation.Hand',NULL,NULL,'2','2007-03-01 15:03:27',NULL),
 ('41','gov.nih.nci.cacoresdk.domain.other.levelassociation.Suit','gov.nih.nci.cacoresdk.domain.other.levelassociation.Suit','gov.nih.nci.cacoresdk.domain.other.levelassociation.Suit','cardCollection',NULL,'2','2007-12-27 00:00:00',NULL),
 ('42','gov.nih.nci.cacoresdk.domain.other.primarykey.DoubleKey','gov.nih.nci.cacoresdk.domain.other.primarykey.DoubleKey','gov.nih.nci.cacoresdk.domain.other.primarykey.DoubleKey',NULL,NULL,'2','2007-03-01 15:03:28',NULL),
 ('43','gov.nih.nci.cacoresdk.domain.other.primarykey.FloatKey','gov.nih.nci.cacoresdk.domain.other.primarykey.FloatKey','gov.nih.nci.cacoresdk.domain.other.primarykey.FloatKey',NULL,NULL,'2','2007-03-01 15:03:28',NULL),
 ('44','gov.nih.nci.cacoresdk.domain.other.primarykey.IntegerKey','gov.nih.nci.cacoresdk.domain.other.primarykey.IntegerKey','gov.nih.nci.cacoresdk.domain.other.primarykey.IntegerKey',NULL,NULL,'2','2007-03-01 15:03:28',NULL),
 ('45','gov.nih.nci.cacoresdk.domain.other.primarykey.StringKey','gov.nih.nci.cacoresdk.domain.other.primarykey.StringKey','gov.nih.nci.cacoresdk.domain.other.primarykey.StringKey',NULL,NULL,'2','2007-03-01 15:03:28',NULL),
 ('65','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.DesignerShoes','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.DesignerShoes','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.DesignerShoes',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('66','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.Designer','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.Designer','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.Designer',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('67','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.Shoes','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.Shoes','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.Shoes',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('68','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.SportsShoes','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.SportsShoes','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.sametable.SportsShoes',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('70','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.GovtOrganization','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.GovtOrganization','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.GovtOrganization',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('71','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.Organization','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.Organization','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.Organization',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('72','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.PvtOrganization','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.PvtOrganization','gov.nih.nci.cacoresdk.domain.inheritance.multiplechild.sametable.PvtOrganization',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('73','gov.nih.nci.cacoresdk.domain.inheritance.onechild.sametable.Currency','gov.nih.nci.cacoresdk.domain.inheritance.onechild.sametable.Currency','gov.nih.nci.cacoresdk.domain.inheritance.onechild.sametable.Currency',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('74','gov.nih.nci.cacoresdk.domain.inheritance.onechild.sametable.Note','gov.nih.nci.cacoresdk.domain.inheritance.onechild.sametable.Note','gov.nih.nci.cacoresdk.domain.inheritance.onechild.sametable.Note',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('75','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.HardTop','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.HardTop','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.HardTop',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('76','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.Luggage','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.Luggage','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.Luggage',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('77','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.SoftTop','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.SoftTop','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.SoftTop',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('79','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.Wheel','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.Wheel','gov.nih.nci.cacoresdk.domain.inheritance.parentwithassociation.sametable.Wheel',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('80','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.CommunistGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.CommunistGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.CommunistGovt',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('81','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.DemocraticGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.DemocraticGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.DemocraticGovt',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('82','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.Goverment','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.Goverment','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.Goverment',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('83','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.ParliamantaryGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.ParliamantaryGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.ParliamantaryGovt',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('84','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.PresidentialGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.PresidentialGovt','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametable.PresidentialGovt',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('85','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.Calculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.Calculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.Calculator',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('86','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.FinancialCalculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.FinancialCalculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.FinancialCalculator',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('87','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.GraphicCalculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.GraphicCalculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.GraphicCalculator',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('88','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.ScientificCalculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.ScientificCalculator','gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.ScientificCalculator',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('89','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.withjoin.Album','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.withjoin.Album','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.withjoin.Album',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('90','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.withjoin.Song','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.withjoin.Song','gov.nih.nci.cacoresdk.domain.manytoone.unidirectional.withjoin.Song',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('91','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.withjoin.Flight','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.withjoin.Flight','gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.withjoin.Flight',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('92','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.withjoin.Shirt','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.withjoin.Shirt','gov.nih.nci.cacoresdk.domain.onetomany.unidirectional.withjoin.Shirt',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('93','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.withjoin.Chain','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.withjoin.Chain','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.withjoin.Chain',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('94','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.withjoin.Pendant','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.withjoin.Pendant','gov.nih.nci.cacoresdk.domain.onetoone.bidirectional.withjoin.Pendant',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('95','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.withjoin.Bride','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.withjoin.Bride','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.withjoin.Bride',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('96','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.withjoin.InLaw','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.withjoin.InLaw','gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.withjoin.InLaw',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('97','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Bag','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Bag','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Bag',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('98','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Handle','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Handle','gov.nih.nci.cacoresdk.domain.onetoone.unidirectional.withjoin.Handle',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('99','gov.nih.nci.cacoresdk.domain.other.primarykey.CharacterKey','gov.nih.nci.cacoresdk.domain.other.primarykey.CharacterKey','gov.nih.nci.cacoresdk.domain.other.primarykey.CharacterKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('100','gov.nih.nci.cacoresdk.domain.other.primarykey.CharacterPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.CharacterPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.CharacterPrimitiveKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('101','gov.nih.nci.cacoresdk.domain.other.primarykey.DoublePrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.DoublePrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.DoublePrimitiveKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('102','gov.nih.nci.cacoresdk.domain.other.primarykey.FloatPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.FloatPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.FloatPrimitiveKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('103','gov.nih.nci.cacoresdk.domain.other.primarykey.IntegerPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.IntegerPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.IntegerPrimitiveKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('104','gov.nih.nci.cacoresdk.domain.other.primarykey.LongKey','gov.nih.nci.cacoresdk.domain.other.primarykey.LongKey','gov.nih.nci.cacoresdk.domain.other.primarykey.LongKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('107','gov.nih.nci.cacoresdk.domain.other.primarykey.LongPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.LongPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.LongPrimitiveKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('108','gov.nih.nci.cacoresdk.domain.other.primarykey.NoIdKey','gov.nih.nci.cacoresdk.domain.other.primarykey.NoIdKey','gov.nih.nci.cacoresdk.domain.other.primarykey.NoIdKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('110','gov.nih.nci.cacoresdk.domain.other.primarykey.StringPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.StringPrimitiveKey','gov.nih.nci.cacoresdk.domain.other.primarykey.StringPrimitiveKey',NULL,NULL,'2','2007-12-13 00:00:00',NULL),
 ('113','gov.nih.nci.cacoresdk.domain.inheritance.onechild.Human','gov.nih.nci.cacoresdk.domain.inheritance.onechild.Human','gov.nih.nci.cacoresdk.domain.inheritance.onechild.Human',NULL,NULL,'2','2007-12-17 00:00:00',NULL),
 ('114','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Bank','PE for \'name\' attribute of Bank object','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Bank','name',NULL,'2','2007-12-17 00:00:00',NULL),
 ('115','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit.amount','PE for Credit.amount attribute','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit','amount',NULL,'2','2007-12-19 00:00:00',NULL),
 ('116','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit.cardNumber','PE for Credit.cardNumber attribute','gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Credit','cardNumber',NULL,'2','2007-12-19 00:00:00',NULL),
 ('117','gov.nih.nci.cacoresdk.domain.other.validationtype.AllValidationType','gov.nih.nci.cacoresdk.domain.other.validationtype.AllValidationType','gov.nih.nci.cacoresdk.domain.other.validationtype.AllValidationType',NULL,NULL,'2','2008-09-05 12:07:28',NULL);
COMMIT;
/*!40000 ALTER TABLE `csm_protection_element` ENABLE KEYS */;


--
-- Definition of table `csm_protection_group`
--

DROP TABLE IF EXISTS CSM_PROTECTION_GROUP;
CREATE TABLE CSM_PROTECTION_GROUP ( 
	PROTECTION_GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,
	PROTECTION_GROUP_NAME VARCHAR(100) NOT NULL,
	PROTECTION_GROUP_DESCRIPTION VARCHAR(200),
	APPLICATION_ID BIGINT NOT NULL,
	LARGE_ELEMENT_COUNT_FLAG BOOL NOT NULL,
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PARENT_PROTECTION_GROUP_ID BIGINT,
	PRIMARY KEY(PROTECTION_GROUP_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_protection_group`
--

/*!40000 ALTER TABLE `csm_protection_group` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_protection_group` (`protection_group_id`,`protection_group_name`,`protection_group_description`,`application_id`,`large_element_count_flag`,`update_date`,`parent_protection_group_id`) VALUES 
 ('1','All PEs','Contains all of the PEs in the entire test sdk system','2',0,'2007-12-19 00:00:00',NULL),
 ('2','Bank',NULL,'2',0,'2007-03-01 00:00:00',NULL),
 ('3','AllDataType',NULL,'2',0,'2007-03-23 00:00:00',NULL),
 ('4','Book',NULL,'2',0,'2007-03-23 00:00:00',NULL),
 ('7','Limited Access','JUnit Security Test Group with limited access to a handful of Classes and Attributes','2',0,'2007-12-19 00:00:00',NULL);
COMMIT;
/*!40000 ALTER TABLE `csm_protection_group` ENABLE KEYS */;


--
-- Definition of table `csm_role`
--

DROP TABLE IF EXISTS CSM_ROLE;
CREATE TABLE CSM_ROLE ( 
	ROLE_ID BIGINT AUTO_INCREMENT  NOT NULL,
	ROLE_NAME VARCHAR(100) NOT NULL,
	ROLE_DESCRIPTION VARCHAR(200),
	APPLICATION_ID BIGINT NOT NULL,
	ACTIVE_FLAG BOOL NOT NULL,
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PRIMARY KEY(ROLE_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_role`
--

/*!40000 ALTER TABLE `csm_role` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_role` (`role_id`,`role_name`,`role_description`,`application_id`,`active_flag`,`update_date`) VALUES 
 ('1','SuperAdmin','SuperAdmin','2',1,'2007-03-01 00:00:00'),
 ('2','Read',NULL,'2',1,'2007-03-01 00:00:00'),
 ('3','Create',NULL,'2',1,'2007-03-23 00:00:00'),
 ('5','Update',NULL,'2',1,'2008-06-30 00:00:00'),
 ('6','Delete',NULL,'2',1,'2008-06-30 00:00:00');
COMMIT;
/*!40000 ALTER TABLE `csm_role` ENABLE KEYS */;


--
-- Definition of table `csm_role_privilege`
--

DROP TABLE IF EXISTS CSM_ROLE_PRIVILEGE;
CREATE TABLE CSM_ROLE_PRIVILEGE ( 
	ROLE_PRIVILEGE_ID BIGINT AUTO_INCREMENT  NOT NULL,
	ROLE_ID BIGINT NOT NULL,
	PRIVILEGE_ID BIGINT NOT NULL,
	PRIMARY KEY(ROLE_PRIVILEGE_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_role_privilege`
--

/*!40000 ALTER TABLE `csm_role_privilege` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_role_privilege` (`role_privilege_id`,`role_id`,`privilege_id`) VALUES 
 ('1','2','3'),
 ('2','1','5'),
 ('3','1','1'),
 ('4','1','2'),
 ('5','1','7'),
 ('6','1','4'),
 ('7','1','3'),
 ('8','1','6'),
 ('9','3','1'),
 ('14','5','5'),
 ('15','6','6');
COMMIT;
/*!40000 ALTER TABLE `csm_role_privilege` ENABLE KEYS */;


--
-- Definition of table `csm_user`
--

DROP TABLE IF EXISTS CSM_USER;
CREATE TABLE CSM_USER ( 
	USER_ID BIGINT AUTO_INCREMENT  NOT NULL,
	LOGIN_NAME VARCHAR(500) NOT NULL,
	MIGRATED_FLAG BOOL NOT NULL DEFAULT 0,
	FIRST_NAME VARCHAR(100) NOT NULL,
	LAST_NAME VARCHAR(100) NOT NULL,
	ORGANIZATION VARCHAR(100),
	DEPARTMENT VARCHAR(100),
	TITLE VARCHAR(100),
	PHONE_NUMBER VARCHAR(15),
	PASSWORD VARCHAR(100),
	EMAIL_ID VARCHAR(100),
	START_DATE DATE,
	END_DATE DATE,
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PREMGRT_LOGIN_NAME VARCHAR(100),
	PRIMARY KEY(USER_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_user`
--

/*!40000 ALTER TABLE `csm_user` DISABLE KEYS */;
/* SuperAdmin password is set to 'changeme'.  The user1 and user2 passwords are set to 'password'.*/
SET AUTOCOMMIT=0;
INSERT INTO `csm_user` (`user_id`,`login_name`,`first_name`,`last_name`,`organization`,`department`,`title`,`phone_number`,`password`,`email_id`,`start_date`,`end_date`,`update_date`) VALUES 
 ('1','SuperAdmin','Super','Admin',NULL,NULL,NULL,NULL,'zJPWCwDeSgG8j2uyHEABIQ==',NULL,NULL,NULL,'2008-05-23 00:00:00'),
 ('13','user1','user1','junit',NULL,NULL,NULL,NULL,'qN+MnXquuqO8j2uyHEABIQ==',NULL,NULL,NULL,'2008-06-30 00:00:00'),
 ('14','user2','user2','junit',NULL,NULL,NULL,NULL,'qN+MnXquuqO8j2uyHEABIQ==',NULL,NULL,NULL,'2008-06-30 00:00:00');
COMMIT;
/*!40000 ALTER TABLE `csm_user` ENABLE KEYS */;


--
-- Definition of table `csm_user_group`
--

DROP TABLE IF EXISTS CSM_USER_GROUP;
CREATE TABLE CSM_USER_GROUP ( 
	USER_GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,
	USER_ID BIGINT NOT NULL,
	GROUP_ID BIGINT NOT NULL,
	PRIMARY KEY(USER_GROUP_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_user_group`
--

/*!40000 ALTER TABLE `csm_user_group` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_user_group` (`user_group_id`,`user_id`,`group_id`) VALUES 
 ('2','13','2'),
 ('3','14','3');
COMMIT;
/*!40000 ALTER TABLE `csm_user_group` ENABLE KEYS */;


--
-- Definition of table `csm_user_group_role_pg`
--

DROP TABLE IF EXISTS CSM_USER_GROUP_ROLE_PG;
CREATE TABLE CSM_USER_GROUP_ROLE_PG ( 
	USER_GROUP_ROLE_PG_ID BIGINT AUTO_INCREMENT  NOT NULL,
	USER_ID BIGINT,
	GROUP_ID BIGINT,
	ROLE_ID BIGINT NOT NULL,
	PROTECTION_GROUP_ID BIGINT NOT NULL,
	UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',
	PRIMARY KEY(USER_GROUP_ROLE_PG_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_user_group_role_pg`
--

/*!40000 ALTER TABLE `csm_user_group_role_pg` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_user_group_role_pg` (`user_group_role_pg_id`,`user_id`,`group_id`,`role_id`,`protection_group_id`,`update_date`) VALUES 
 ('1','1',NULL,'1','1','2007-03-01 00:00:00'),
 ('10','9',NULL,'1','1','2007-03-23 00:00:00'),
 ('16','12',NULL,'1','1','2007-12-12 00:00:00'),
 ('17','13',NULL,'3','1','2007-12-19 00:00:00'),
 ('18','13',NULL,'2','1','2007-12-19 00:00:00'),
 ('23',NULL,'2','3','1','2008-06-30 00:00:00'),
 ('24',NULL,'2','2','1','2008-06-30 00:00:00'),
 ('34','13',NULL,'6','1','2008-06-30 00:00:00'),
 ('35','13',NULL,'5','1','2008-06-30 00:00:00'),
 ('38','14',NULL,'3','7','2008-06-30 00:00:00'),
 ('39','14',NULL,'2','7','2008-06-30 00:00:00'),
 ('40',NULL,'3','3','7','2008-06-30 00:00:00'),
 ('41',NULL,'3','2','7','2008-06-30 00:00:00');
COMMIT;
/*!40000 ALTER TABLE `csm_user_group_role_pg` ENABLE KEYS */;


--
-- Definition of table `csm_user_pe`
--

DROP TABLE IF EXISTS CSM_USER_PE;
CREATE TABLE CSM_USER_PE ( 
	USER_PROTECTION_ELEMENT_ID BIGINT AUTO_INCREMENT  NOT NULL,
	PROTECTION_ELEMENT_ID BIGINT NOT NULL,
	USER_ID BIGINT NOT NULL,
	PRIMARY KEY(USER_PROTECTION_ELEMENT_ID)
)Type=InnoDB
;

--
-- Dumping data for table `csm_user_pe`
--

/*!40000 ALTER TABLE `csm_user_pe` DISABLE KEYS */;
SET AUTOCOMMIT=0;
INSERT INTO `csm_user_pe` (`user_protection_element_id`,`protection_element_id`,`user_id`) VALUES 
 ('3','1','1'),
 ('7','2','9'),
 ('8','2','1');
COMMIT;
/*!40000 ALTER TABLE `csm_user_pe` ENABLE KEYS */;
