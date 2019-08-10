# ParkingLots
Toll Parking Library

# How to build from sources
## Prerequisites
- Use a Linux machine
- The local TCP port 9999 should not be in use
- Install a Java [JDK](https://jdk.java.net/)
- Install [git](https://git-scm.com/)
- Install [maven](https://maven.apache.org/)
- Install [MySQL](https://www.mysql.com/) or [MariaDB](https://mariadb.org/) 

## Clone the project
```console
foo@bar:~$ git clone https://github.com/PierrotGwan/ParkingLots.git
foo@bar:~$ cd ParkingLots
```
## Create the database
```console
foo@bar:~$ . ./create_database.sh
```
The SQL root password should be provided for creation of the right database / user.
## Build the project
```console
foo@bar:~$ cd parkinglots
foo@bar:~$ mvn clean package
```
# How to run
## Run the application
```console
foo@bar:~$ java -jar target/parkinglots-boot.jar
```
## Navigate
With your favorite browser, navigate to [the project](http://localhost:9999/index.html)
All API documentation is available there.
