-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 06, 2026 at 07:45 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hotel_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `reservation`
--

CREATE TABLE `reservation` (
  `booking_no` int(11) NOT NULL,
  `guest_name` varchar(100) DEFAULT NULL,
  `address` varchar(150) DEFAULT NULL,
  `contact_number` varchar(15) DEFAULT NULL,
  `room_type` varchar(20) DEFAULT NULL,
  `check_in` date DEFAULT NULL,
  `check_out` date DEFAULT NULL,
  `room_number` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservation`
--

INSERT INTO `reservation` (`booking_no`, `guest_name`, `address`, `contact_number`, `room_type`, `check_in`, `check_out`, `room_number`) VALUES
(2, 'dinuki', 'mahiyanganaya', '1234567890', 'Single', '2026-02-21', '2026-02-24', '001'),
(3, 'Salya', 'Colombo', '0771234569', 'Double', '2026-02-14', '2026-02-16', '100'),
(4, 'anna', 'galla', '0987654321', 'Deluxe', '2026-02-18', '2026-02-21', '100'),
(6, 'sam', 'kandy', '0987654321', 'Double', '2026-02-21', '2026-02-23', '002'),
(8, 'Salya', 'Colombo', '0771234569', 'Double', '2026-02-14', '2026-02-16', '100'),
(9, 'anm', 'kandy', '1234567890', 'Deluxe', '2026-03-06', '2026-03-08', '001'),
(10, 'Jhon', 'galla', '0987654321', 'Deluxe', '2026-03-06', '2026-03-08', '003');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `reservation`
--
ALTER TABLE `reservation`
  ADD PRIMARY KEY (`booking_no`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `reservation`
--
ALTER TABLE `reservation`
  MODIFY `booking_no` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
