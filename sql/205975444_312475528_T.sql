USE DB2019_Ass2
GO
-- Trigger 1
create trigger DeleteProject on ProjectConstructorEmployee
AFTER DELETE
as
SET NOCOUNT ON
begin
delete from Employee
where EID in ( select EID from deleted where EID not in (select EID from ProjectConstructorEmployee ))
end

go
-- Trigger 3
create trigger ParkingDiscount on CarParking 
AFTER UPDATE,INSERT
as
begin
SET NOCOUNT ON
IF TRIGGER_NESTLEVEL() <= 2
update CarParking
set Cost = C.Cost * 0.8
from Carparking as C,inserted , Cars , OfficialEmployee
where C.CID = inserted.CID and inserted.CID = Cars.CID and Cars.ID = OfficialEmployee.EID
end


go
-- Trigger 2
create trigger Park on CarParking
AFTER UPDATE,INSERT
as
begin
SET NOCOUNT ON
IF TRIGGER_NESTLEVEL() <= 1
update CarParking
set Cost = CASE WHEN (DATEDIFF(HOUR,inserted.StartTime,inserted.EndTime)*ParkingArea.priceperhour<ParkingArea.maxpriceperday)
                then DATEDIFF(HOUR,inserted.StartTime,inserted.EndTime)*ParkingArea.priceperhour
           else   ParkingArea.maxpriceperday end
from CarParking ,ParkingArea,inserted
where ParkingArea.AID = CarParking.ParkingAreaID AND CarParking.CID = inserted.CID and CarParking.StartTime = inserted.StartTime
end