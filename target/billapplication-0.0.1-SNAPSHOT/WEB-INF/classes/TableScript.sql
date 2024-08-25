drop table customer;
create table customer(cid int primary key auto_increment, cname varchar(60),caddress varchar(200),cno Long,gstin varchar(40),emailid varchar(50));
alter table customer add column ctype varchar(10);
select * from customer;
update customer set ctype='CGST/SGST' where cid in (1,2);
update customer set ctype='IGST' where cid=3;

drop table billdetails;
Truncate billdetails;
create table billdetails(item_desc varchar(500),sac Long,taxable_value long);
alter table billdetails modify column sac varchar(10);
alter table billdetails add column cid int;
alter table billdetails add foreign key (cid) references customer(cid);
alter table billdetails add column INVOICENO varchar(100);

ALTER TABLE BILLDETAILS ADD FOREIGN KEY (INVOICENO) REFERENCES INVOICEDETAILS(INVOICENO);

alter table billdetails add column id int;
alter table billdetails change column id id int(10) primary key AUTO_INCREMENT;
alter table billdetails change column taxable_value basic_value long;
alter table billdetails add column quantity int;
alter table billdetails add column cgst int;
alter table billdetails add column igst int;
alter table billdetails add column sgst int;
desc billdetails;
select * from billdetails;
delete from billdetails where id=23;

create table admin(ID int primary key auto_increment,NAME varchar(200),ADDRESS varchar(200));
insert into admin(name,address,GSTIN) values('S BAHETI & ASSOCIATES','Office No. A104, Royal Tranquil, Near Kokane Chowk, Rahatani, Pune-411017','GST1234'); 
SELECT * FROM ADMIN;
update admin set sac='123456789';
alter table admin add column GSTIN varchar(40);
alter table admin add column sac varchar(10);
drop table admin;

create table bankDetails(ACCOUNTID INT PRIMARY KEY AUTO_INCREMENT,
BANKNAME VARCHAR(50),
ACCOUNTNAME VARCHAR(400),
ACCOUNTNUMBER VARCHAR(30),
ACCOUNTTYPE VARCHAR(30),
IFSCCODE VARCHAR(30));
SELECT * FROM bankDetails;
INSERT INTO BANKDETAILS(BANKNAME,ACCOUNTNAME,ACCOUNTNUMBER,ACCOUNTTYPE,IFSCCODE) VALUES('State Bank Of India','S Baheti And Associates','38354809085','Current','SBIN0014578');

DROP table INVOICEDETAILS;
Truncate INVOICEDETAILS;
create table INVOICEDETAILS(INVOICENO VARCHAR(100) PRIMARY KEY,
INVOICEDATE DATE,
COUNT INT);
select * from InvoiceDetails;
alter table InvoiceDetails add column total_value INT;
alter table InvoiceDetails add column cid INT references customer(cid);
delete from invoicedetails;
alter table invoicedetails modify column invoicedate varchar(100);

create table logindetails(username varchar(15),password varchar(30));
insert into logindetails values('admin','admin');
update logindetails set id=1;
select * from logindetails;
alter table logindetails add column id int primary key;