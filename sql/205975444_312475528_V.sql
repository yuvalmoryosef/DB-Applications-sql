USE DB2019_Ass2
-- View 1
go
CREATE VIEW ConstructionEmployeeOverFifty as
SELECT  C.EID, CompanyName, SalaryPerDay
FROM ConstructorEmployee as C join Employee as E on C.EID = E.EID
WHERE DATEDIFF(year,E.BirthDate,GETDATE()) > 50
go
--View 2
CREATE VIEW ApartmentNumberInNeighborhood as 
SELECT N.NID,count(*) as ApartmentNumber
from Apartment as A ,Neighborhood AS N
WHERE N.NID = A.NeighborhoodID
group by N.NID
--View 3
go
CREATE VIEW MaxParking AS
SELECT result.ParkingAreaID as AID,result.CID,max(result.CountPA) as MaxParkingCount
from (SELECT ParkingAreaID,CID,count(CID) as CountPA from CarParking
group by ParkingAreaID,CID) as result
group by result.ParkingAreaID,result.CID