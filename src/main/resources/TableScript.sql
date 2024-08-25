use db1;
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

SELECT * FROM ADMIN;
update admin set sac='123456789';
alter table admin add column GSTIN varchar(40);
alter table admin add column sac varchar(10);
drop table admin;
truncate table admin;

create table bankDetails(ACCOUNTID INT PRIMARY KEY AUTO_INCREMENT,
BANKNAME VARCHAR(50),
ACCOUNTNAME VARCHAR(400),
ACCOUNTNUMBER VARCHAR(30),
ACCOUNTTYPE VARCHAR(30),
IFSCCODE VARCHAR(30));
SELECT * FROM bankDetails;

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
select * from logindetails;
alter table logindetails add column id int primary key;

drop table payment_details;

create table payment_details( id int primary key,
invoiceno varchar(100),
cid int,
invoice_value long,
total_amount_received long,
amount_due long,
balance_pending long,
cgst long,
sgst long,
igst long,
taxable_value long,
discount long,
tds long,
foreign key (invoiceno) references INVOICEDETAILS(INVOICENO),
foreign key (cid) references customer(cid));


create table payment_details_history( id int primary key,
invoiceno varchar(100),
cid int,
invoice_value long,
amount_received long,
amount_due long,
balance_pending long,
cgst long,
sgst long,
igst long,
taxable_value long,
date_of_receipt date,
discount long,
tds long,
foreign key (invoiceno) references INVOICEDETAILS(INVOICENO),
foreign key (cid) references customer(cid));