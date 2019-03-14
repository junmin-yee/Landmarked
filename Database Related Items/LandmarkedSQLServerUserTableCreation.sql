-- Name: LandmarkedSQLServerTableCreation.sql
-- Date: January 13, 2019
-- Last Modified: March 13, 2019 by Junmin Yee
-- Description: SQL code to create tables for landmark structure
-- Jan 15 Edit: Added title documentation
-- Mar 13 Edit: Added SQL code to create intermediate table for AppUser and Landmark

USE Landmarked;

CREATE TABLE AppUser
(
	UserID			INT				CONSTRAINT UserID_pk PRIMARY KEY IDENTITY,
	FirstName		VARCHAR(63)		NOT NULL,
	LastName		VARCHAR(63)		NOT NULL,
	Email			VARCHAR(255)	NOT NULL
);

CREATE TABLE UserLandmark
(
	UserID			INT				CONSTRAINT UserLandmark_User_fk REFERENCES AppUser(UserID),
	LandmarkID		INT				CONSTRAINT UserLandmark_Landmark_fk REFERENCES Landmark(LandmarkID),
	CONSTRAINT UserID_LandmarkID_pk PRIMARY KEY(UserID, LandmarkID)
);

ALTER TABLE CustomLandmark
ADD UserID INT CONSTRAINT CustomLandmark_User_UserID_fk REFERENCES AppUser(UserID);