-- Name: LandmarkedSQLServerTableCreation.sql
-- Date: January 13, 2019
-- Last Modified: January 15, 2018 by Junmin Yee
-- Description: SQL code to create tables for landmark structure
-- Jan 15 Edit: Added title documentation

USE Landmarked;

CREATE TABLE AppUser
(
	UserID			INT				CONSTRAINT UserID_pk PRIMARY KEY IDENTITY,
	FirstName		VARCHAR(63)		NOT NULL,
	LastName		VARCHAR(63)		NOT NULL,
	Email			VARCHAR(255)	NOT NULL
);

ALTER TABLE CustomLandmark
ADD UserID INT CONSTRAINT CustomLandmark_User_UserID_fk REFERENCES AppUser(UserID);