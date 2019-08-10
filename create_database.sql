CREATE DATABASE IF NOT EXISTS gwan_parkinglots;
CREATE USER 'gwan'@'localhost' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON gwan_parkinglots.* to 'gwan'@'localhost';
