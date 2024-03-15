-- MySQL dump 10.13  Distrib 8.3.0, for macos14.2 (x86_64)
--
-- Host: localhost    Database: db_im
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Admin`
--

DROP TABLE IF EXISTS `Admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Admin` (
  `Admin_ID` int NOT NULL AUTO_INCREMENT,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Admin_ID`),
  CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`Admin_ID`) REFERENCES `Admin_Info` (`Admin_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Admin`
--

LOCK TABLES `Admin` WRITE;
/*!40000 ALTER TABLE `Admin` DISABLE KEYS */;
INSERT INTO `Admin` VALUES (1,'System Administrator'),(2,'Support Administrator');
/*!40000 ALTER TABLE `Admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Admin_Info`
--

DROP TABLE IF EXISTS `Admin_Info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Admin_Info` (
  `Admin_ID` int NOT NULL AUTO_INCREMENT,
  `Admin_Name` varchar(255) DEFAULT NULL,
  `Contact_Number` varchar(15) DEFAULT NULL,
  `Gender` enum('Male','Female','Other') DEFAULT NULL,
  `Date_of_Birth` date DEFAULT NULL,
  PRIMARY KEY (`Admin_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Admin_Info`
--

LOCK TABLES `Admin_Info` WRITE;
/*!40000 ALTER TABLE `Admin_Info` DISABLE KEYS */;
INSERT INTO `Admin_Info` VALUES (1,'John Doe','1234567890','Male','1990-01-01'),(2,'Jane Smith','9876543210','Female','1985-05-15');
/*!40000 ALTER TABLE `Admin_Info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Cash_Advance`
--

DROP TABLE IF EXISTS `Cash_Advance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Cash_Advance` (
  `Cash_Advance_ID` int NOT NULL AUTO_INCREMENT,
  `Employee_ID` int DEFAULT NULL,
  `Amount` decimal(10,2) DEFAULT NULL,
  `Date_Requested` date DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Cash_Advance_ID`),
  KEY `Employee_ID` (`Employee_ID`),
  CONSTRAINT `cash_advance_ibfk_1` FOREIGN KEY (`Employee_ID`) REFERENCES `Employees` (`Employee_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Cash_Advance`
--

LOCK TABLES `Cash_Advance` WRITE;
/*!40000 ALTER TABLE `Cash_Advance` DISABLE KEYS */;
INSERT INTO `Cash_Advance` VALUES (7,10,100.00,'2024-03-15','meow meow'),(9,13,200.00,'2024-03-15','rawr');
/*!40000 ALTER TABLE `Cash_Advance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Deduction`
--

DROP TABLE IF EXISTS `Deduction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Deduction` (
  `Deduction_ID` int NOT NULL AUTO_INCREMENT,
  `Deduction_Type` varchar(255) DEFAULT NULL,
  `Amount` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`Deduction_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Deduction`
--

LOCK TABLES `Deduction` WRITE;
/*!40000 ALTER TABLE `Deduction` DISABLE KEYS */;
INSERT INTO `Deduction` VALUES (1,'Pag-Ibig',20.00),(2,'Philhealth',30.00),(3,'SSS',50.00);
/*!40000 ALTER TABLE `Deduction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Employee_Cash_Advance`
--

DROP TABLE IF EXISTS `Employee_Cash_Advance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Employee_Cash_Advance` (
  `Cash_Advance_ID` int NOT NULL,
  `PaySlip_ID` int NOT NULL,
  PRIMARY KEY (`Cash_Advance_ID`,`PaySlip_ID`),
  KEY `PaySlip_ID` (`PaySlip_ID`),
  CONSTRAINT `employee_cash_advance_ibfk_1` FOREIGN KEY (`Cash_Advance_ID`) REFERENCES `Cash_Advance` (`Cash_Advance_ID`),
  CONSTRAINT `employee_cash_advance_ibfk_2` FOREIGN KEY (`PaySlip_ID`) REFERENCES `PaySlip` (`PaySlip_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Employee_Cash_Advance`
--

LOCK TABLES `Employee_Cash_Advance` WRITE;
/*!40000 ALTER TABLE `Employee_Cash_Advance` DISABLE KEYS */;
/*!40000 ALTER TABLE `Employee_Cash_Advance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Employee_Deductions`
--

DROP TABLE IF EXISTS `Employee_Deductions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Employee_Deductions` (
  `Deduction_ID` int NOT NULL,
  `PaySlip_ID` int NOT NULL,
  PRIMARY KEY (`Deduction_ID`,`PaySlip_ID`),
  KEY `PaySlip_ID` (`PaySlip_ID`),
  CONSTRAINT `employee_deductions_ibfk_1` FOREIGN KEY (`Deduction_ID`) REFERENCES `Deduction` (`Deduction_ID`),
  CONSTRAINT `employee_deductions_ibfk_2` FOREIGN KEY (`PaySlip_ID`) REFERENCES `PaySlip` (`PaySlip_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Employee_Deductions`
--

LOCK TABLES `Employee_Deductions` WRITE;
/*!40000 ALTER TABLE `Employee_Deductions` DISABLE KEYS */;
/*!40000 ALTER TABLE `Employee_Deductions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `Employee_ID` int NOT NULL AUTO_INCREMENT,
  `Employee_Name` varchar(255) DEFAULT NULL,
  `Contact_Number` varchar(15) DEFAULT NULL,
  `Gender` varchar(10) DEFAULT NULL,
  `Date_of_Birth` date DEFAULT NULL,
  PRIMARY KEY (`Employee_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES (10,'meow','11111','Male','2002-02-02'),(12,'testq','12345678','Female','2005-05-05'),(13,'rawr','129312','Male','2011-01-01');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PackType`
--

DROP TABLE IF EXISTS `PackType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PackType` (
  `PackType_ID` int NOT NULL AUTO_INCREMENT,
  `Size` varchar(50) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Rate` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`PackType_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PackType`
--

LOCK TABLES `PackType` WRITE;
/*!40000 ALTER TABLE `PackType` DISABLE KEYS */;
INSERT INTO `PackType` VALUES (1,'Small','10x20',50.00),(2,'Medium','20x40',100.00),(3,'Large','40x80',200.00);
/*!40000 ALTER TABLE `PackType` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PaySlip`
--

DROP TABLE IF EXISTS `PaySlip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PaySlip` (
  `PaySlip_ID` int NOT NULL AUTO_INCREMENT,
  `Transaction_ID` int NOT NULL,
  PRIMARY KEY (`PaySlip_ID`,`Transaction_ID`),
  KEY `Transaction_ID` (`Transaction_ID`),
  CONSTRAINT `payslip_ibfk_1` FOREIGN KEY (`Transaction_ID`) REFERENCES `Transaction` (`Transaction_ID`),
  CONSTRAINT `payslip_ibfk_2` FOREIGN KEY (`Transaction_ID`) REFERENCES `Piecework_Details` (`Transaction_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PaySlip`
--

LOCK TABLES `PaySlip` WRITE;
/*!40000 ALTER TABLE `PaySlip` DISABLE KEYS */;
/*!40000 ALTER TABLE `PaySlip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `piecework`
--

DROP TABLE IF EXISTS `piecework`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `piecework` (
  `Employee_ID` int NOT NULL,
  `Job_Type_Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Employee_ID`),
  CONSTRAINT `piecework_ibfk_1` FOREIGN KEY (`Employee_ID`) REFERENCES `Employees` (`Employee_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `piecework`
--

LOCK TABLES `piecework` WRITE;
/*!40000 ALTER TABLE `piecework` DISABLE KEYS */;
INSERT INTO `piecework` VALUES (10,'piecework'),(12,'piecework'),(13,'piecework');
/*!40000 ALTER TABLE `piecework` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Piecework_Details`
--

DROP TABLE IF EXISTS `Piecework_Details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Piecework_Details` (
  `Transaction_ID` int NOT NULL AUTO_INCREMENT,
  `Employee_ID` int DEFAULT NULL,
  `PackType_ID` int DEFAULT NULL,
  `Quantity` int DEFAULT NULL,
  PRIMARY KEY (`Transaction_ID`),
  KEY `Employee_ID` (`Employee_ID`),
  KEY `PackType_ID` (`PackType_ID`),
  CONSTRAINT `piecework_details_ibfk_1` FOREIGN KEY (`Employee_ID`) REFERENCES `Employees` (`Employee_ID`),
  CONSTRAINT `piecework_details_ibfk_2` FOREIGN KEY (`PackType_ID`) REFERENCES `PackType` (`PackType_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Piecework_Details`
--

LOCK TABLES `Piecework_Details` WRITE;
/*!40000 ALTER TABLE `Piecework_Details` DISABLE KEYS */;
INSERT INTO `Piecework_Details` VALUES (38,10,1,1),(39,10,3,1),(40,10,2,1),(44,13,3,2),(45,13,2,2),(46,13,1,2);
/*!40000 ALTER TABLE `Piecework_Details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transaction`
--

DROP TABLE IF EXISTS `Transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transaction` (
  `Transaction_ID` int NOT NULL AUTO_INCREMENT,
  `Date` date DEFAULT NULL,
  `Admin_ID` int DEFAULT NULL,
  PRIMARY KEY (`Transaction_ID`),
  KEY `Admin_ID` (`Admin_ID`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`Admin_ID`) REFERENCES `Admin_Info` (`Admin_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transaction`
--

LOCK TABLES `Transaction` WRITE;
/*!40000 ALTER TABLE `Transaction` DISABLE KEYS */;
INSERT INTO `Transaction` VALUES (4,'2024-03-15',1),(5,'2024-03-15',1),(6,'2024-03-15',1),(7,'2024-03-15',1),(8,'2024-03-15',1),(9,'2024-03-15',1),(10,'2024-03-15',1),(11,'2024-03-15',1),(12,'2024-03-15',1),(13,'2024-03-15',1),(14,'2024-03-15',1),(15,'2024-03-15',1),(16,'2024-03-15',1),(17,'2024-03-15',1),(18,'2024-03-15',1),(19,'2024-03-15',1),(20,'2024-03-15',1),(21,'2024-03-15',1),(22,'2024-03-15',1),(23,'2024-03-15',1),(24,'2024-03-15',1),(25,'2024-03-15',1),(26,'2024-03-15',1),(27,'2024-03-15',1),(28,'2024-03-15',1),(29,'2024-03-15',1),(30,'2024-03-15',1),(31,'2024-03-15',1),(32,'2024-03-15',1),(33,'2024-03-15',1),(34,'2024-03-15',1),(35,'2024-03-15',1),(36,'2024-03-15',1),(37,'2024-03-15',2),(38,'2024-03-15',2),(39,'2024-03-15',2),(40,'2024-03-15',2),(41,'2024-03-15',2),(42,'2024-03-15',2),(43,'2024-03-15',2),(44,'2024-03-15',2),(45,'2024-03-15',2),(46,'2024-03-15',2);
/*!40000 ALTER TABLE `Transaction` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-15 13:56:54
