------ ***** spring 기초 ***** ------

show user;
-- USER이(가) "MYMVC_USER"입니다.

create table spring_test
(no         number
,name       varchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_TEST이(가) 생성되었습니다.


select *
from spring_test;

--------------------------------------------------------------------------------------

show user;
-- USER이(가) "HR"입니다.

create table spring_exam
(no         number
,name       varchar2(100)
,address    Nvarchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_EXAM이(가) 생성되었습니다.

select *
from spring_exam;

--------------------------------------------------------------------------------------

show user;
-- USER이(가) "MYMVC_USER"입니다.

insert into spring_test(no, name, writeday)
values(102, '박보영', default);

insert into spring_test(no, name, writeday)
values(103, '변우석', default);

commit;

select no, name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from spring_test
order by no asc;

delete from spring_test
where no = 1009;

commit;



-----------------------------------------------------------------------------------------
show user;
-- USER이(가) "MYMVC_USER"입니다.


create table tbl_main_image_product
(imgno           number not null
,productname     Nvarchar2(20) not null
,imgfilename     varchar2(100) not null
,constraint PK_tbl_main_image_product primary key(imgno)
);
-- Table TBL_MAIN_IMAGE_PRODUCT이(가) 생성되었습니다.


create sequence seq_main_image_product
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_MAIN_IMAGE_PRODUCT이(가) 생성되었습니다.


insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '미샤' ,'미샤.png');  
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '원더플레이스', '원더플레이스.png'); 
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '레노보', '레노보.png'); 
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '동원', '동원.png'); 

commit;

select imgno, productname, imgfilename
from tbl_main_image_product
order by imgno asc;


select *
from tbl_member
where userid = 'eomjh' and pwd='9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382';




--- 로그인 처리 ---
SELECT userid, name, coin, point, pwdchangegap,
       NVL(lastlogingap, trunc(months_between(sysdate, registerday))) AS lastlogingap, 
	   idle, email, mobile, postcode, address, detailaddress, extraaddress 
FROM 
( select userid, name, coin, point, 
         trunc( months_between(sysdate, lastpwdchangedate) ) AS pwdchangegap,
		 registerday, idle, email, mobile, postcode, address, detailaddress, extraaddress 
  from tbl_member 
  where status = 1 and userid = 'eomjh' and pwd = '9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382'
) M 
CROSS JOIN 
( select trunc( months_between(sysdate, max(logindate)) ) as lastlogingap 
  from tbl_loginhistory 
  where fk_userid = 'eomjh' ) H;



select userid, lastpwdchangedate, idle
from tbl_member
where userid in ('ejss0125', 'eomjh', 'leess');


select *
from tbl_loginhistory
order by historyno















