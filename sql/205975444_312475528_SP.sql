USE DB2019_Ass2
GO
--Sp 1
CREATE PROCEDURE sp_AddMunicipalEnployee
	@EID int,
	@LastName varchar(255),
	@FirstName varchar(255),
	@BirthDate date,
	@StreetName varchar(255),
	@Number int,
	@door int ,
	@City varchar(255)
	
AS
BEGIN
	insert into Employee values(@EID,@LastName,@FirstName,@BirthDate,@StreetName,@Number,@door,@City);
END
GO
-- Sp 2
GO
CREATE PROCEDURE sp_AddMunicipalEnployeeOfficial
	@EID int, @LastName nvarchar(255), @FirstName nvarchar(255), @BirthDate date, @StreetName nvarchar(255), @Number int, @door int, @City nvarchar(255),
	@StartDate date, @Degree nvarchar(255), @DepartmentID int

	AS
BEGIN
	insert into Employee (EID,LastName,FirstName,BirthDate, StreetName,Number,door,City) 
	values(@EID,@LastName,@FirstName,@BirthDate,@StreetName,@Number,@door,@city)
	insert into OfficialEmployee(EID,StartDate,Degree,DepartmentID) 
	values(@EID,@StartDate,@Degree,@DepartmentID)
END
GO
-- Sp 3
CREATE PROCEDURE sp_AddMunicipalEnployeeConstructor
	@EID int, @LastName nvarchar(255), @FirstName nvarchar(255), @BirthDate date, @StreetName nvarchar(255), @Number int, @door int, @City nvarchar(255),
	@CompanyName nvarchar(255), @SalaryPerDay int

	AS BEGIN
	insert into Employee (EID,LastName,FirstName,BirthDate, StreetName,Number,door,City) 
	values(@EID,@LastName,@FirstName,@BirthDate,@StreetName,@Number,@door,@City)
	insert into ConstructorEmployee(EID,CompanyName,SalaryPerDay) 
	values(@EID,@CompanyName, @SalaryPerDay)
END
GO
-- Sp 4
CREATE PROCEDURE sp_StartParking
	@CID int, @StartTime date, @ParkingAreaID int

	AS BEGIN
	insert into CarParking(CID,StartTime,ParkingAreaID) 
	values(@CID,@StartTime,@ParkingAreaID)
END
GO
-- Sp 5
CREATE PROCEDURE sp_EndParking
	@CID int, @StartTime date, @EndTime date
	as UPDATE CarParking
	SET EndTime = @EndTime
	WHERE CID = @CID AND StartTime = @StartTime;
GO