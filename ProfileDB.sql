DROP USER Profile CASCADE;

create user Profile identified by Profile
default Tablespace users
Temporary tablespace temp
Quota 10M on users;

grant create table, create session, create sequence, create role, create procedure, create type, create trigger,create synonym to Profile;

connect Profile/Profile

CREATE TABLE MyUsers
(
    ID           Number,
    Code         char(12),
    Address1     varchar(100),
    Address2     varchar(100),
    Address3     varchar(100),
    Name         varchar2(40),
    DOB          date,
    StateID      Number(2),
    CityID       Number(2),  
    Gender       char(1),
    Marital      char(1),
    PhNo         char(10),   
    Email        varchar(50),
    Remarks      varchar(1000),
    hasCar       char(1),
    hasHome      char(1),
    Pic          Blob,
    CONSTRAINT PK_Users PRIMARY KEY (ID)
);
CREATE TABLE UserHobbies
(
	UserID Number,
	HobbyID Number,
	CONSTRAINT PK_UserHobby PRIMARY KEY(UserID,HobbyID) 
);
CREATE TABLE Hobby
(
	ID Number,
	Name varchar(100),
	CONSTRAINT PK_Hobby PRIMARY KEY(ID)
);
create sequence seqId;
create sequence seqCode;
create sequence seqHobbyId;
insert into MyUsers values(seqId.NEXTVAL,'201806120001','24B PRATAPADITYA ROAD','','','Riddhi Dutta',to_date('17/05/1992','dd/mm/yyyy'),
20,1,'M','U','9038963271','rite2riddhi@gmail.com','i love coding','Y','N',NULL);  

insert into MyUsers values(seqId.NEXTVAL,'201806120002','24 DOVER LANE','','','Subh Dutta',to_date('18/06/1994','dd/mm/yyyy'),
18,1,'M','S','8584905808','subh@gmail.com','i love cricket','Y','Y',NULL); 

insert into Hobby values(seqHobbyId.NEXTVAL,'Coding');
insert into Hobby values(seqHobbyId.NEXTVAL,'Cricket');
insert into Hobby values(seqHobbyId.NEXTVAL,'Football');
insert into Hobby values(seqHobbyId.NEXTVAL,'Singing');
insert into Hobby values(seqHobbyId.NEXTVAL,'Dancing');
insert into Hobby values(seqHobbyId.NEXTVAL,'Philately');
insert into Hobby values(seqHobbyId.NEXTVAL,'Fishing');
insert into Hobby values(seqHobbyId.NEXTVAL,'Rowing');
insert into Hobby values(seqHobbyId.NEXTVAL,'Trekking');
insert into Hobby values(seqHobbyId.NEXTVAL,'Book Reading');
insert into Hobby values(seqHobbyId.NEXTVAL,'Rugby');
insert into Hobby values(seqHobbyId.NEXTVAL,'Hockey');
insert into Hobby values(seqHobbyId.NEXTVAL,'Table Tennis');
insert into Hobby values(seqHobbyId.NEXTVAL,'Chatting');
insert into Hobby values(seqHobbyId.NEXTVAL,'Video Games');
insert into Hobby values(seqHobbyId.NEXTVAL,'Driving');
insert into Hobby values(seqHobbyId.NEXTVAL,'Chess');
insert into Hobby values(seqHobbyId.NEXTVAL,'Carrom');
insert into Hobby values(seqHobbyId.NEXTVAL,'Ludo');
insert into Hobby values(seqHobbyId.NEXTVAL,'Sleeping');

insert into UserHobbies (UserID,HobbyID) select u.id,h.id from MyUsers u,Hobby h where u.name='Riddhi Dutta' and h.name='Coding';
insert into UserHobbies (UserID,HobbyID) select u.id,h.id from MyUsers u,Hobby h where u.name='Riddhi Dutta' and h.name='Cricket';
insert into UserHobbies (UserID,HobbyID) select u.id,h.id from MyUsers u,Hobby h where u.name='Riddhi Dutta' and h.name='Singing';

commit;