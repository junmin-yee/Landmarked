-- Name: LandmarkedSQLServerAdminCreation.sql
-- Date: Decemeber 11, 2018
-- Last Modified: December 12, 2018 by Junmin Yee
-- Description: SQL code to create admins for Landmarked

USE Landmarked;

-- Create Admin role
CREATE ROLE Admins ;
GRANT ALL PRIVILEGES
ON Landmarked
TO Admins;

-- Create logins for admins 
-- This code must be run in master, whoops!
CREATE LOGIN logan_francisco
WITH PASSWORD = 'Landmarked18';

CREATE LOGIN kelly_vanmeter
WITH PASSWORD = 'Landmarked18';

CREATE LOGIN garrett_hammock
WITH PASSWORD = 'Landmarked18';

CREATE LOGIN junmin_yee
WITH PASSWORD = 'Landmarked18';

-- Create users for logins
CREATE USER logan_francisco
FOR LOGIN logan_francisco;

CREATE USER kelly_vanmeter
FOR LOGIN kelly_vanmeter;

CREATE USER garrett_hammock
FOR LOGIN garrett_hammock;

CREATE USER junmin_yee
FOR LOGIN junmin_yee;

-- Add users to role
ALTER ROLE Admins
ADD MEMBER logan_francisco;

ALTER ROLE Admins
ADD MEMBER kelly_vanmeter;

ALTER ROLE Admins
ADD MEMBER garrett_hammock;

ALTER ROLE Admins
ADD MEMBER junmin_yee;