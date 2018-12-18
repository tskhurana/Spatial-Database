CREATE DATABASE IF NOT EXISTS PublicSafety;

USE PublicSafety;

DROP TABLE IF EXISTS zone;
DROP TABLE IF EXISTS officer;
DROP table IF EXISTS route;
DROP TABLE IF EXISTS incident;

CREATE TABLE IF NOT EXISTS zone
(
	zoneID INT PRIMARY KEY NOT NULL,
    zoneName VARCHAR(50) NOT NULL,
    squadNumber INT,
    numVertices INT,
    zonePoints geometry NOT NULL,
    SPATIAL INDEX(zonePoints) 
) ENGINE = MYISAM;

CREATE TABLE IF NOT EXISTS officer
(
	badgeNumber INT PRIMARY KEY,
	name VARCHAR(100),
	squadNumber INT,
	currLocation point NOT NULL,
    SPATIAL INDEX(currLocation)
) ENGINE = MYISAM;

create table IF NOT EXISTS route
(
	routeNumber INT,
    numVertices INT,
    routeLatLong LineString NOT NULL,
    SPATIAL INDEX(routeLatLong)
) ENGINE = MYISAM;

CREATE TABLE IF NOT EXISTS incident
(
	incidentID INT,
    incidentType VARCHAR(100),
    incidentLocation POINT NOT NULL,
    SPATIAL INDEX(incidentLocation)
) ENGINE = MYISAM;

DELIMITER //

DROP FUNCTION IF EXISTS GCDist //
CREATE FUNCTION GCDist (
        lat1 FLOAT, lon1 FLOAT,
        lat2 FLOAT, lon2 FLOAT
     ) RETURNS FLOAT
    NO SQL DETERMINISTIC
    COMMENT 'Returns the distance in degrees on the Earth
             between two known points of latitude and longitude'
BEGIN
    RETURN DEGREES(ACOS(
              COS(RADIANS(lat1)) *
              COS(RADIANS(lat2)) *
              COS(RADIANS(lon2) - RADIANS(lon1)) +
              SIN(RADIANS(lat1)) * SIN(RADIANS(lat2))
            ));
END//
DELIMITER ;