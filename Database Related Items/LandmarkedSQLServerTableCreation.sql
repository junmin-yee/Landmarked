USE master;
GO

IF  DB_ID('Landmarked') IS NOT NULL
    DROP DATABASE Landmarked;
GO

CREATE DATABASE Landmarked;
GO

USE Landmarked;

CREATE TABLE Landmark
(
	LandmarkID			INT				CONSTRAINT LandmarkID_pk PRIMARY KEY IDENTITY,
	LandmarkName		VARCHAR(255)	NOT NULL,
	LandmarkLat			VARCHAR(20)		NOT NULL,
	LandmarkLong		VARCHAR(20)		NOT NULL,
	LandmarkEle			FLOAT			NOT NULL,
	LandmarkWikiInfo	VARCHAR(1024)
);

CREATE TABLE CustomLandmark
(
	CustomLandmarkID	INT				CONSTRAINT CustomLandmarkedID_pk PRIMARY KEY IDENTITY,
	CustomLandmarkName	VARCHAR(255)	NOT NULL,
	CustomLandmarkLat	VARCHAR(20)		NOT NULL,
	CustomLandmarkLong	VARCHAR(20)		NOT NULL,
	CustomLandmarkEle	FLOAT			NOT NULL,
	DateSaved			DATETIME		NOT NULL
);

CREATE TABLE Photo
(
	CustomLandmarkID	INT				CONSTRAINT CustomLandmark_Photo_fk REFERENCES CustomLandmark(CustomLandmarkID),
	PhotoFileName		VARCHAR(255)	NOT NULL,
	PhotoDateTaken		DATETIME		NOT NULL,
	PhotoFilePath		VARCHAR(1023)	NOT NULL
);

CREATE TABLE Note
(
	CustomLandmarkID	INT				CONSTRAINT CustomLandmark_Note_fk REFERENCES CustomLandmark(CustomLandmarkID),
	NoteTitle			VARCHAR(255)	NOT NULL,
	NoteText			VARCHAR(1023),
	NoteLastEdited		DATETIME		NOT NULL
);