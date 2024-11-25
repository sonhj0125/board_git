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


--------------------------------------------------------


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


--------------------------------------------------------


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


--------------------------------------------------------

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

insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '미샤', '미샤.png');  
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '원더플레이스', '원더플레이스.png'); 
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '레노보', '레노보.png'); 
insert into tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval, '동원', '동원.png'); 

commit;

select imgno, productname, imgfilename
from tbl_main_image_product
order by imgno asc;




--- 로그인 처리 ---
SELECT userid, name, coin, point, pwdchangegap,
       NVL(lastlogingap, trunc(months_between(sysdate, registerday))) AS lastlogingap, 
	   idle, email, mobile, postcode, address, detailaddress, extraaddress 
FROM 
( select userid, name, coin, point, 
         trunc( months_between(sysdate, lastpwdchangedate) ) AS pwdchangegap,
		 registerday, idle, email, mobile, postcode, address, detailaddress, extraaddress 
  from tbl_member 
  where status = 1 and userid = 'kimdy' and pwd = '9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382'
) M 
CROSS JOIN 
( select trunc( months_between(sysdate, max(logindate)) ) as lastlogingap 
  from tbl_loginhistory 
  where fk_userid = 'kimdy' ) H;



select userid, lastpwdchangedate, idle,
       trunc( months_between(sysdate, lastpwdchangedate) ) AS pwdchangegap
from tbl_member
where userid in ('kimdy', 'eomjh', 'leess');




------- **** spring 게시판(답변글쓰기가 없고, 파일첨부도 없는) 글쓰기 **** -------

show user;
-- USER이(가) "MYMVC_USER"입니다.    

desc tbl_member;

create table tbl_board
(seq         number                not null    -- 글번호
,fk_userid   varchar2(20)          not null    -- 사용자ID
,name        varchar2(20)          not null    -- 글쓴이 (제3정규화 위배 : 식별자가 아닌 컬럼이 식별자가 아닌 컬럼에 의존)
,subject     Nvarchar2(200)        not null    -- 글제목
,content     Nvarchar2(2000)       not null    -- 글내용   -- clob (최대 4GB까지 허용) 
,pw          varchar2(20)          not null    -- 글암호
,readCount   number default 0      not null    -- 글조회수
,regDate     date default sysdate  not null    -- 글쓴시간
,status      number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);
-- Table TBL_BOARD이(가) 생성되었습니다.

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

select seq, fk_userid, name, subject
     , readCount, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
from tbl_board
where status = 1
order by seq desc;



select historyno, fk_userid, to_char(logindate, 'yyyy-mm-dd hh24:mi:ss') as logindate, clientip
from tbl_loginhistory
order by historyno desc;



-- 글 1개 조회하기
SELECT previouseq, previoussubject
     , seq, fk_userid, name, subject, content, readCount, regDate, pw
     , nextseq, nextsubject
FROM
(
    select lag(seq, 1) over(order by seq desc) AS previouseq
        -- lag(seq) 와 같음
         , lag(subject, 1) over(order by seq desc) AS previoussubject
         , seq, fk_userid, name, subject, content, readCount
         , to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') as regDate, pw
         , lead(seq, 1) over(order by seq desc) AS nextseq
         , lead(subject, 1) over(order by seq desc) AS nextsubject
    from tbl_board
    where status = 1
) V
WHERE V.seq = 3;




-----------------------------------------------------------------------------

   ----- **** 댓글 게시판 **** -----

/* 
  댓글쓰기(tbl_comment 테이블)를 성공하면 원게시물(tbl_board 테이블)에
  댓글의 갯수(1씩 증가)를 알려주는 컬럼 commentCount 을 추가하겠다. 
*/
drop table tbl_board purge;
-- Table TBL_BOARD이(가) 삭제되었습니다.

create table tbl_board
(seq           number                not null    -- 글번호
,fk_userid     varchar2(20)          not null    -- 사용자ID
,name          varchar2(20)          not null    -- 글쓴이 (제3정규화 위배 : 식별자가 아닌 컬럼이 식별자가 아닌 컬럼에 의존)
,subject       Nvarchar2(200)        not null    -- 글제목
,content       Nvarchar2(2000)       not null    -- 글내용   -- clob (최대 4GB까지 허용) 
,pw            varchar2(20)          not null    -- 글암호
,readCount     number default 0      not null    -- 글조회수
,regDate       date default sysdate  not null    -- 글쓴시간
,status        number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된 글
,commentCount  number default 0      not null    -- 댓글의 개수
,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);
-- Table TBL_BOARD이(가) 생성되었습니다.


drop sequence boardSeq;
-- Sequence BOARDSEQ이(가) 삭제되었습니다.

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence BOARDSEQ이(가) 생성되었습니다.



----- **** 댓글 테이블 생성 **** -----
create table tbl_comment
(seq           number               not null   -- 댓글번호
,fk_userid     varchar2(20)         not null   -- 사용자ID
,name          varchar2(20)         not null   -- 성명
,content       varchar2(1000)       not null   -- 댓글내용
,regDate       date default sysdate not null   -- 작성일자
,parentSeq     number               not null   -- 원게시물 글번호
,status        number(1) default 1  not null   -- 글삭제여부
                                               -- 1 : 사용가능한 글,  0 : 삭제된 글
                                               -- 댓글은 원글이 삭제되면 자동적으로 삭제되어야 한다.
,constraint PK_tbl_comment_seq primary key(seq)
,constraint FK_tbl_comment_userid foreign key(fk_userid) references tbl_member(userid)
,constraint FK_tbl_comment_parentSeq foreign key(parentSeq) references tbl_board(seq) on delete cascade
,constraint CK_tbl_comment_status check( status in(1,0) ) 
);
-- Table TBL_COMMENT이(가) 생성되었습니다.

create sequence commentSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence COMMENTSEQ이(가) 생성되었습니다.


select *
from tbl_comment
order by seq desc;

select *
from tbl_board
order by seq desc;



-- ==== Transaction 처리를 위한 시나리오 만들기 ==== --
---- 회원들이 게시판에 글쓰기를 하면 글작성 1건당 POINT 를 100점을 준다.
---- 회원들이 게시판에서 댓글쓰기를 하면 댓글작성 1건당 POINT 를 50점을 준다.
---- 그런데 데이터베이스 오류 발생시 스프링에서 롤백해주는 Transaction 처리를 알아보려고 일부러 POINT 는 300을 초과할 수 없다고 하겠다.

select *
from tbl_member;

update tbl_member set point = 0;

commit;

-- tbl_member 테이블에 point 컬럼에 check 제약을 추가한다.
alter table tbl_member
add constraint CK_tbl_member_point check( point between 0 and 300 );
-- Table TBL_MEMBER이(가) 변경되었습니다.


update tbl_member set point = 301
where userid = 'kimdy';
/*
    오류 보고 -
    ORA-02290: 체크 제약조건(MYMVC_USER.CK_TBL_MEMBER_POINT)이 위배되었습니다
*/

update tbl_member set point = 300
where userid = 'kimdy';
-- 1 행 이(가) 업데이트되었습니다.

rollback;
-- 롤백 완료.



select *
from tbl_board
order by seq desc;

select *
from tbl_comment;

select seq, subject, commentcount
from tbl_board
order by seq desc;

select userid, point
from tbl_member
where userid in ('eomjh','kimdy');

-- 원글에 대한 댓글 조회
select seq, fk_userid, name, content, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
from tbl_comment
where status = 1 and parentSeq = 4
order by seq desc;


-- ==== Transaction 처리를 위한 시나리오 때문에 만들었던 포인트 체크 제약을 없애겠다. ==== --
alter table tbl_member
drop constraint CK_tbl_member_point;
-- Table TBL_MEMBER이(가) 변경되었습니다.



update tbl_board set status = 0
where seq between 1 and 5;

commit;

update tbl_board set status = 1
where seq between 1 and 5;

commit;



-- 페이징 처리를 한, 검색이 있는 글 목록 조회하기
SELECT rno, seq, fk_userid, name, subject
     , readCount, regDate , commentCount
FROM
(
    select row_number() over(order by seq desc) AS rno
         , seq, fk_userid, name, subject
         , readCount, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
         , commentCount
    from tbl_board
    where status = 1
    and lower(subject) like '%' || lower('java') || '%'
 -- and lower(content) like '%' || lower('글내용') || '%'
 -- and (lower(subject) like '%' || lower('글제목') || '%' or  lower(content) like '%' || lower('글내용') || '%')
 -- and lower(name) like '%' || lower('글쓴이') || '%'
) V
WHERE V.rno between 1 and 10;




-------------------------------------------------------------------------------
/* 임시로 주석 처리
begin
    for i in 1..100 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
        values(boardSeq.nextval, 'kimdy', '김다영', '김다영입니다 '||i, '안녕하세요? 김다영 '|| i ||' 입니다.', '1234', default, default, default);
    end loop;
end;

begin
    for i in 101..200 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status)
        values(boardSeq.nextval, 'eomjh', '엄정화', '엄정화입니다 '||i, '안녕하세요? 엄정화 '|| i ||' 입니다.', '1234', default, default, default);
    end loop;
end;
*/
commit;
-- 커밋 완료.



-- 검색 조건이 있을 시 글 1개 조회하기
SELECT previousseq, previoussubject
     , seq, fk_userid, name, subject, content, readCount, regDate, pw
     , nextseq, nextsubject
FROM
(
    select lag(seq, 1) over(order by seq desc) AS previousseq
         , lag(subject, 1) over(order by seq desc) AS previoussubject
         , seq, fk_userid, name, subject, content, readCount
         , to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') as regDate, pw
         , lead(seq, 1) over(order by seq desc) AS nextseq
         , lead(subject, 1) over(order by seq desc) AS nextsubject
    from tbl_board
    where status = 1
    and lower(subject) like '%' || lower('JavA') || '%'
) V
WHERE V.seq = 14;



-- 원게시물에 달린 댓글 내용들을 페이징 처리하기
SELECT seq, fk_userid, name, content, regDate
FROM
(
    select row_number() over(order by seq desc) AS rno
        , seq, fk_userid, name, content, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
    from tbl_comment
    where status = 1 and parentSeq = 213
) V
where V.rno between 1 and 5;





select count(*)
from tbl_comment
where status = 1 and parentSeq = 213;





----------------------------------------------------------------------
      ---- **** 댓글 및 답변글 및 파일첨부가 있는 게시판 **** ----
      
--- *** 답변글쓰기는 일반회원은 불가하고 직원(관리파트)들만 답변글쓰기가 가능하도록 한다. *** ---

--- *** tbl_member(회원) 테이블에 gradelevel 이라는 컬럼을 추가하겠다. *** ---
alter table tbl_member
add gradelevel number default 1;
-- Table TBL_MEMBER이(가) 변경되었습니다.

-- *** 직원(관리자)들에게는 gradelevel 컬럼의 값을 10 으로 부여하겠다. 
--     gradelevel 컬럼의 값이 10 인 직원들만 답변글쓰기가 가능하다 *** --
update tbl_member set gradelevel = 10
where userid in('admin', 'kimdy');
-- 2개 행 이(가) 업데이트되었습니다.

commit;
-- 커밋 완료.

select *
from tbl_member
order by gradelevel desc;


drop table tbl_comment purge;
drop sequence commentSeq;
drop table tbl_board purge;
drop sequence boardSeq;


create table tbl_board
(seq           number                not null    -- 글번호
,fk_userid     varchar2(20)          not null    -- 사용자ID
,name          varchar2(20)          not null    -- 글쓴이 
,subject       Nvarchar2(200)        not null    -- 글제목
--,content     Nvarchar2(2000)       not null    -- 글내용
,content       clob                  not null    -- 글내용   CLOB(4GB 까지 저장 가능한 데이터 타입) 타입
,pw            varchar2(20)          not null    -- 글암호
,readCount     number default 0      not null    -- 글조회수
,regDate       date default sysdate  not null    -- 글쓴시간
,status        number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,commentCount  number default 0      not null    -- 댓글의 개수 

,groupno       number                not null    -- 답변글쓰기에 있어서 그룹번호 
                                                 -- 원글(부모글)과 답변글은 동일한 groupno 를 가진다.
                                                 -- 답변글이 아닌 원글(부모글)인 경우 groupno 의 값은 groupno 컬럼의 최대값(max)+1 로 한다.

,fk_seq         number default 0      not null   -- fk_seq 컬럼은 절대로 foreign key가 아니다.!!!!!!
                                                 -- fk_seq 컬럼은 자신의 글(답변글)에 있어서 
                                                 -- 원글(부모글)이 누구인지에 대한 정보값이다.
                                                 -- 답변글쓰기에 있어서 답변글이라면 fk_seq 컬럼의 값은 
                                                 -- 원글(부모글)의 seq 컬럼의 값을 가지게 되며,
                                                 -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.

,depthno        number default 0       not null  -- 답변글쓰기에 있어서 답변글 이라면
                                                 -- 원글(부모글)의 depthno + 1 을 가지게 되며,
                                                 -- 답변글이 아닌 원글일 경우 0 을 가지도록 한다.

,fileName       varchar2(255)                    -- WAS(톰캣)에 저장될 파일명(2024062609291535243254235235234.png)                                       
,orgFilename    varchar2(255)                    -- 진짜 파일명(강아지.png)  // 사용자가 파일을 업로드 하거나 파일을 다운로드 할 때 사용되는 파일명 
,fileSize       number                           -- 파일크기  

,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);
-- Table TBL_BOARD이(가) 생성되었습니다.

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


create table tbl_comment
(seq           number               not null   -- 댓글번호
,fk_userid     varchar2(20)         not null   -- 사용자ID
,name          varchar2(20)         not null   -- 성명
,content       varchar2(1000)       not null   -- 댓글내용
,regDate       date default sysdate not null   -- 작성일자
,parentSeq     number               not null   -- 원게시물 글번호
,status        number(1) default 1  not null   -- 글삭제여부
                                               -- 1 : 사용가능한 글,  0 : 삭제된 글
                                               -- 댓글은 원글이 삭제되면 자동적으로 삭제되어야 한다.
,constraint PK_tbl_comment_seq primary key(seq)
,constraint FK_tbl_comment_userid foreign key(fk_userid) references tbl_member(userid)
,constraint FK_tbl_comment_parentSeq foreign key(parentSeq) references tbl_board(seq) on delete cascade
,constraint CK_tbl_comment_status check( status in(1,0) ) 
);
--  Table TBL_COMMENT이(가) 생성되었습니다.

create sequence commentSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;


desc tbl_board;

/* 임시로 주석 처리
begin
    for i in 1..100 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status, groupno)
        values(boardSeq.nextval, 'kimdy', '김다영', '김다영입니다 '||i, '안녕하세요? 김다영'|| i ||'입니다.', '1234', default, default, default, i);
    end loop;
end;
-- PL/SQL 프로시저가 성공적으로 완료되었습니다.

begin
    for i in 101..200 loop
        insert into tbl_board(seq, fk_userid, name, subject, content, pw, readCount, regDate, status, groupno)
        values(boardSeq.nextval, 'eomjh', '엄정화', '엄정화 입니다'||i, '안녕하세요? 엄정화'|| i ||' 입니다.', '1234', default, default, default, i);
    end loop;
end;
-- PL/SQL 프로시저가 성공적으로 완료되었습니다.
*/

commit;
-- PL/SQL 프로시저가 성공적으로 완료되었습니다.

select * 
from tbl_board 
order by seq desc;

update tbl_board set subject = '문의드립니다. 자바가 뭔가요?'
where seq = 198;
-- 1 행 이(가) 업데이트되었습니다.

commit;

select * 
from tbl_board 
where seq = 198;



-- tbl_board 테이블에서 groupno 컬럼의 최대값 알아오기
select NVL(max(groupno),0) 
from tbl_board;



--- *** 답변형 게시판의 글 목록 조회하기 *** ---
SELECT seq, fk_userid, name, subject, readCount, regDate, commentCount
     , groupno, fk_seq, depthno
FROM
(
    SELECT rownum AS RNO
         , seq, fk_userid, name, subject, readCount, regDate, commentCount
         , groupno, fk_seq, depthno
    FROM
    (
        select seq, fk_userid, name, subject
             , readCount, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
             , commentCount
             , groupno, fk_seq, depthno
        from tbl_board
        where status = 1
    /*
    <choose>
        <when test="searchType == 'subject' and searchWord != ''">
            and lower(subject) like '%' || lower(#{searchWord}) || '%'
        </when>
        <when test="searchType == 'content' and searchWord != ''">
            and lower(content) like '%' || lower(#{searchWord}) || '%'
        </when>
        <when test="searchType == 'subject_content' and searchWord != ''">
            and (lower(subject) like '%' || lower(#{searchWord}) || '%' or 
                 lower(content) like '%' || lower(#{searchWord}) || '%')
        </when>
        <when test="searchType == 'name' and searchWord != ''">
            and lower(name) like '%' || lower(#{searchWord}) || '%'
        </when>
        <otherwise></otherwise>
    </choose>
    */
        start with fk_seq = 0
        connect by prior seq = fk_seq
        order siblings by groupno desc, seq asc
    ) V
) T  
WHERE RNO between 1 and 10;
/*
    order by 로 정렬할 경우는 select 된 모든 데이터를 가지고 정렬을 하는 것이고,
    order siblings by 각 계층별로 정렬을 하는 것이다.
    order by 로만 정렬을 하게 되면 계층 구조가 깨질 수 있기 때문에 계층구조는 그대로 유지하면서 동일 부모를 가진 자식 행들끼리의 정렬 기준을 주는 것이 order siblings by 이다.
*/



select *
from tbl_board
order by seq desc;



----- **** ==== 댓글쓰기에 파일첨부까지 한 것 ==== **** -----
alter table tbl_comment
add fileName varchar2(255); -- WAS(톰캣)에 저장될 파일명(2023112409291535243254235235234.png)
-- Table TBL_COMMENT이(가) 변경되었습니다.

alter table tbl_comment
add orgFilename varchar2(255); -- 진짜 파일명(강아지.png)  // 사용자가 파일을 업로드 하거나 파일을 다운로드 할때 사용되어지는 파일명 
-- Table TBL_COMMENT이(가) 변경되었습니다.

alter table tbl_comment
add fileSize number;  -- 파일크기
-- Table TBL_COMMENT이(가) 변경되었습니다.


select *
from tbl_comment
where parentSeq = 212
order by seq desc;



-- 댓글 정보 가져오기 (commentList.action)
SELECT seq, fk_userid, name, content, regDate
     , fileName, orgFilename, fileSize
FROM
(
    select row_number() over(order by seq desc) AS rno
         , seq, fk_userid, name, content, to_char(regDate, 'yyyy-mm-dd hh24:mi:ss') AS regDate
         , nvl(fileName, ' ') AS fileName
         , nvl(orgFilename, ' ') AS orgFilename
         , nvl(to_char(fileSize), ' ') AS fileSize
    from tbl_comment
    where status = 1 and parentSeq = 212
) V
where V.rno between 1 and 5;





--- 서울시 따릉이대여소 마스터 정보 ---
create table seoul_bicycle_rental
(lendplace_id  varchar2(20)
,statn_addr1   Nvarchar2(100)
,statn_addr2   Nvarchar2(100)
,statn_lat     number
,statn_lnt     number
);
-- Table SEOUL_BICYCLE_RENTAL이(가) 생성되었습니다.


select count(*)  -- 3296
from seoul_bicycle_rental;



select statn_addr1
     , instr(statn_addr1, ' ', 1)+1
     , substr(statn_addr1, instr(statn_addr1, ' ', 1)+1) -- 문자열에서 최초로 공백이 나올 때 그 다음의 문자열 위치를 알려주는 것(1부터 시작)
     
     , instr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), ' ', 1)
     , substr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), 1, instr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), ' ', 1)-1)
   
     , lendplace_id, statn_addr1, statn_addr2, statn_lat, statn_lnt   
from seoul_bicycle_rental;


SELECT *
FROM
(
select substr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), 1, instr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), ' ', 1)-1) AS GU
     , lendplace_id, statn_addr1, statn_addr2, statn_lat, statn_lnt   
from seoul_bicycle_rental
) V
WHERE NOT V.GU LIKE '%구';


SELECT GU, COUNT(*) AS CNT
FROM 
(
select substr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), 1, instr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), ' ', 1)-1) AS GU
     , lendplace_id, statn_addr1, statn_addr2, statn_lat, statn_lnt   
from seoul_bicycle_rental
) V
WHERE GU LIKE '%구'
GROUP BY GU
ORDER BY CNT DESC;



WITH A 
AS (
    SELECT GU, COUNT(*) AS CNT
    FROM 
    (
    select substr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), 1, instr(substr(statn_addr1, instr(statn_addr1, ' ', 1)+1), ' ', 1)-1) AS GU
         , lendplace_id, statn_addr1, statn_addr2, statn_lat, statn_lnt   
    from seoul_bicycle_rental
    ) V
    WHERE GU LIKE '%구'
    GROUP BY GU
    ORDER BY CNT DESC       
)
SELECT A.GU, A.CNT, TO_CHAR( ROUND((A.CNT / B.TOTAL) * 100, 1), '990.0') AS PERCNTAGE
FROM A CROSS JOIN (SELECT SUM(CNT) AS TOTAL FROM A) B;






---- **** Arround Advice 를 위해서 만든 테이블 **** ----
show user;
-- USER이(가) "MYMVC_USER"입니다.

/*
drop table tbl_empManger_accessTime purge;
drop sequence seq_seqAccessTime;
*/

create table tbl_empManger_accessTime
(seqAccessTime   number
,pageUrl         varchar2(150) not null
,fk_userid       varchar2(40) not null
,clientIP        varchar2(30) not null
,accessTime      varchar2(20) default sysdate not null
,constraint PK_tbl_empManger_accessTime primary key(seqAccessTime)
,constraint FK_tbl_empManger_accessTime foreign key(fk_userid) references tbl_member(userid)
);
-- Table TBL_EMPMANGER_ACCESSTIME이(가) 생성되었습니다.

create sequence seq_seqAccessTime
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_SEQACCESSTIME이(가) 생성되었습니다.

select * 
from tbl_empManger_accessTime
order by seqAccessTime desc;


SELECT case 
         when instr(PAGEURL, 'employeeList.action', 1, 1) > 0 then '직원목록' 
         when instr(PAGEURL, 'chart.action', 1, 1) > 0   then '통계차트'
         else '기타'
       end AS PAGENAME
     , name     
     , CNT
FROM 
(
    SELECT M.name, A.pageurl, A.cnt 
    FROM 
    (
        select NVL(substr(pageurl, 1, instr(pageurl, '?', 1, 1)-1), pageurl) AS PAGEURL 
             , fk_userid
             , count(*) AS CNT 
        from tbl_empManger_accessTime
        group by NVL(substr(pageurl, 1, instr(pageurl, '?', 1, 1)-1), pageurl), fk_userid 
    ) A JOIN tbl_member M
    ON A.fk_userid = M.userid
) V
ORDER BY 1, 2;






------------- >>>>>>>> 일정관리(풀캘린더) 시작 <<<<<<<< -------------

show user;
-- USER이(가) "MYMVC_USER"입니다.


-- *** 캘린더 대분류(내캘린더, 사내캘린더  분류) ***
create table tbl_calendar_large_category 
(lgcatgono   number(3) not null      -- 캘린더 대분류 번호
,lgcatgoname varchar2(50) not null   -- 캘린더 대분류 명
,constraint PK_tbl_calendar_large_category primary key(lgcatgono)
);
-- Table TBL_CALENDAR_LARGE_CATEGORY이(가) 생성되었습니다.

insert into tbl_calendar_large_category(lgcatgono, lgcatgoname)
values(1, '내캘린더');

insert into tbl_calendar_large_category(lgcatgono, lgcatgoname)
values(2, '사내캘린더');

commit;
-- 커밋 완료.

select * 
from tbl_calendar_large_category;


-- *** 캘린더 소분류 *** 
-- (예: 내캘린더중 점심약속, 내캘린더중 저녁약속, 내캘린더중 운동, 내캘린더중 휴가, 내캘린더중 여행, 내캘린더중 출장 등등) 
-- (예: 사내캘린더중 플젝주제선정, 사내캘린더중 플젝요구사항, 사내캘린더중 DB모델링, 사내캘린더중 플젝코딩, 사내캘린더중 PPT작성, 사내캘린더중 플젝발표 등등) 
create table tbl_calendar_small_category 
(smcatgono    number(8) not null      -- 캘린더 소분류 번호
,fk_lgcatgono number(3) not null      -- 캘린더 대분류 번호
,smcatgoname  varchar2(400) not null  -- 캘린더 소분류 명
,fk_userid    varchar2(40) not null   -- 캘린더 소분류 작성자 유저아이디
,constraint PK_tbl_calendar_small_category primary key(smcatgono)
,constraint FK_small_category_fk_lgcatgono foreign key(fk_lgcatgono) 
            references tbl_calendar_large_category(lgcatgono) on delete cascade
,constraint FK_small_category_fk_userid foreign key(fk_userid) references tbl_member(userid)            
);
-- Table TBL_CALENDAR_SMALL_CATEGORY이(가) 생성되었습니다.


create sequence seq_smcatgono
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_SMCATGONO이(가) 생성되었습니다.


select *
from tbl_calendar_small_category
order by smcatgono desc;


-- *** 캘린더 일정 *** 
create table tbl_calendar_schedule 
(scheduleno    number                 -- 일정관리 번호
,startdate     date                   -- 시작일자
,enddate       date                   -- 종료일자
,subject       varchar2(400)          -- 제목
,color         varchar2(50)           -- 색상
,place         varchar2(200)          -- 장소
,joinuser      varchar2(4000)         -- 공유자   
,content       varchar2(4000)         -- 내용   
,fk_smcatgono  number(8)              -- 캘린더 소분류 번호
,fk_lgcatgono  number(3)              -- 캘린더 대분류 번호
,fk_userid     varchar2(40) not null  -- 캘린더 일정 작성자 유저아이디
,constraint PK_schedule_scheduleno primary key(scheduleno)
,constraint FK_schedule_fk_smcatgono foreign key(fk_smcatgono) 
            references tbl_calendar_small_category(smcatgono) on delete cascade
,constraint FK_schedule_fk_lgcatgono foreign key(fk_lgcatgono) 
            references tbl_calendar_large_category(lgcatgono) on delete cascade   
,constraint FK_schedule_fk_userid foreign key(fk_userid) references tbl_member(userid) 
);
-- Table TBL_CALENDAR_SCHEDULE이(가) 생성되었습니다.

create sequence seq_scheduleno
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_SCHEDULENO이(가) 생성되었습니다.

select *
from tbl_calendar_schedule 
order by scheduleno desc;


-- 일정 상세 보기
select SD.scheduleno
     , to_char(SD.startdate,'yyyy-mm-dd hh24:mi') as startdate
     , to_char(SD.enddate,'yyyy-mm-dd hh24:mi') as enddate  
     , SD.subject
     , SD.color
     , nvl(SD.place,'-') as place
     , nvl(SD.joinuser,'공유자가 없습니다.') as joinuser
     , nvl(SD.content,'') as content
     , SD.fk_smcatgono
     , SD.fk_lgcatgono
     , SD.fk_userid
     , M.name
     , SC.smcatgoname
from tbl_calendar_schedule SD 
JOIN tbl_member M
ON SD.fk_userid = M.userid
JOIN tbl_calendar_small_category SC
ON SD.fk_smcatgono = SC.smcatgono
where SD.scheduleno = 21;


insert into tbl_member(userid, pwd, name, email, mobile, postcode, address, detailaddress, extraaddress, gender, birthday, coin, point, registerday, lastpwdchangedate, status, idle, gradelevel)  
values('leesunsin', '9695b88a59a1610320897fa84cb7e144cc51f2984520efb77111d94b402a8382', '이순신', '2IjrnBPpI++CfWQ7CQhjIw==', 'fCQoIgca24/q72dIoEVMzw==', '15864', '경기 군포시 오금로 15-17', '101동 202호', ' (금정동)', '1', '1995-10-04', 0, 0, default, default, default, default, default);
-- 1 행 이(가) 삽입되었습니다.

commit;

select *
from tbl_member
where name = '이순신';

------------- >>>>>>>> 일정관리(풀캘린더) 끝 <<<<<<<< -------------


-- ========================================================================= --
-- ********************** 사원관리 ********************** --
show user;
-- USER이(가) "HR"입니다.

select distinct nvl(department_id, -9999) AS department_id
from employees
order by department_id asc;


SELECT E.department_id, D.department_name, E.employee_id, 
       E.first_name || ' ' || E.last_name AS fullname,
       to_char(E.hire_date, 'yyyy-mm-dd') AS hire_date,
       nvl(E.salary + E.salary*E.commission_pct, E.salary) AS monthsal,
       func_gender(E.jubun) as gender,
       func_age(E.jubun) as age
FROM employees E LEFT JOIN departments D
ON E.department_id = D.department_id
ORDER BY E.department_id, E.employee_id;



desc employees;


select *
from employees
order by employee_id;

select *
from tbl_board
where seq = 212;



------ ==== Spring Scheduler(스프링 스케줄러)를 사용한 email 자동 발송하기 ==== ------
show user;
-- USER이(가) "MYMVC_USER"입니다.

desc tbl_member;

create table tbl_reservation
(reservationSeq    number        not null
,fk_userid         varchar2(40)  not null
,reservationDate   date          not null
,mailSendCheck     number default 0 not null  -- 메일발송 했으면 1, 메일발송을 안했으면 0 으로 한다.
,constraint PK_tbl_reservation primary key(reservationSeq)
,constraint FK_tbl_reservation foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_reservation check(mailSendCheck in(0,1))
);
-- Table TBL_RESERVATION이(가) 생성되었습니다.

create sequence seq_reservation
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_RESERVATION이(가) 생성되었습니다.

select userid, email
from tbl_member
where userid in('ejss0125', 'eomjh');


select to_char(sysdate, 'yyyy-mm-dd') AS 오늘날짜
from dual;  -- 2024-07-19

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'ejss0125', to_date('2024-07-21 13:00', 'yyyy-mm-dd hh24:mi'));
-- 1 행 이(가) 삽입되었습니다.

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'eomjh', to_date('2024-07-21 14:00', 'yyyy-mm-dd hh24:mi'));
-- 1 행 이(가) 삽입되었습니다.

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'ejss0125', to_date('2024-07-22 11:00', 'yyyy-mm-dd hh24:mi'));
-- 1 행 이(가) 삽입되었습니다.

insert into tbl_reservation(reservationSeq, fk_userid, reservationDate)
values(seq_reservation.nextval, 'eomjh', to_date('2024-07-22 15:00', 'yyyy-mm-dd hh24:mi'));
-- 1 행 이(가) 삽입되었습니다.

commit;


select reservationSeq, fk_userid, 
       to_char(reservationDate, 'yyyy-mm-dd hh24:mi:ss') as reservationDate, 
       mailSendCheck
from tbl_reservation
order by reservationSeq desc;


-- *** 오늘로 부터 2일(이틀) 뒤에 예약되어진 회원들을 조회하기 *** --

-- 예약번호, 고객ID, 고객명, 이메일주소, 예약일자 시간



select reservationSeq, fk_userid, name, email, to_char(reservationDate, 'yyyy-mm-dd hh24:mi:ss') AS reservationDate
from tbl_member M join 
(
    select *
    from tbl_reservation 
    where mailSendCheck = 0 and to_char(reservationDate, 'yyyymmdd') = to_char(sysdate + 2, 'yyyymmdd')
) R
on R.fk_userid = M.userid



--===================================== 스프링 보안 시작 ================================================================

/*
   ============ >>>>>>>>> 스프링 보안(Spring Security) <<<<<<<<<< ============
*/

-- 기존 테이블이 존재할 경우를 대비해서 테이블 삭제
DROP TABLE security_member_authority PURGE;
DROP TABLE security_member PURGE;

-- 기존 시퀀스가 존재할 경우를 대비해서 시퀀스 삭제
DROP SEQUENCE member_authority_seq;


----------------------------------------------------------------
-- >>> 회원 테이블(인증 테이블) 생성하기 <<< --
CREATE TABLE security_member (
  member_id          VARCHAR2(20)  NOT NULL        -- 회원아이디
 ,member_pwd         VARCHAR2(200) NOT NULL        -- 비밀번호 (Spring Security 에서 제공해주는 SHA-256 암호화를 사용하는 대상)
 ,member_name        NVARCHAR2(30) NOT NULL        -- 회원명 
 ,email              VARCHAR2(200) NOT NULL        -- 이메일 (AES-256 암호화/복호화 대상) 
 ,mobile             VARCHAR2(200)                 -- 연락처 (AES-256 암호화/복호화 대상) 
 ,postcode           VARCHAR2(10)                  -- 우편번호
 ,address            VARCHAR2(200)                 -- 주소
 ,detailaddress      VARCHAR2(200)                 -- 상세주소
 ,extraaddress       VARCHAR2(200)                 -- 참고항목
 ,gender             VARCHAR2(1)                   -- 성별   남자:1  / 여자:2
 ,birthday           VARCHAR2(10)                  -- 생년월일   
 ,registerday        DATE DEFAULT SYSDATE          -- 가입일자 
 ,modify_date        DATE DEFAULT SYSDATE          -- 회원정보수정일자
 ,lastpwdchangedate  DATE DEFAULT SYSDATE          -- 마지막으로 암호를 변경한 날짜
 ,enabled            NUMBER(1) DEFAULT 1 NOT NULL  -- Spring Security 에서는 enabled 컬럼의 값이 1이어야만 회원이 존재하는것으로 인식한다. 반드시 enabled 컬럼이 존재해야만 한다.!!!
 ,CONSTRAINT PK_security_member_member_id PRIMARY KEY(member_id)
 ,CONSTRAINT UQ_security_member_email UNIQUE(email)
 ,CONSTRAINT CK_security_member_gender CHECK( gender in('1','2') )
);
/*
 enabled 컬럼 - Sring Security 에서는 계정활성화 여부를 알려주는 enabled를 반드시 넣어주어야 한다.
               enabled 컬럼의 값이 false일 경우 로그인을 할 수 없도록 Sring Security 는 설계가 되어있기 때문이다.

출처: http://goldenraccoon.tistory.com/entry/SPRING-SECURITY-DB-LOGIN [황금너구리 블로그] 
*/
-- Table SECURITY_MEMBER이(가) 생성되었습니다.



-- >>> 어쏘러티(권한, 역할) 테이블 생성하기 <<< --
CREATE TABLE security_member_authority (
  num            NUMBER NOT NULL
 ,fk_member_id   VARCHAR2(20) NOT NULL
 ,authority      VARCHAR2(100) NOT NULL 
 ,CONSTRAINT PK_security_member_authority PRIMARY KEY(num)
 ,CONSTRAINT UQ_security_member_authority UNIQUE(fk_member_id, authority)
 ,CONSTRAINT FK_security_member_authority FOREIGN KEY(fk_member_id) REFERENCES security_member(member_id) ON DELETE CASCADE
);
-- Table SECURITY_MEMBER_AUTHORITY이(가) 생성되었습니다.


-- >>> 어쏘러티(권한, 역할) 테이블에 사용할 시퀀스 생성하기 <<< --
DROP SEQUENCE member_authority_seq;
-- Sequence MEMBER_AUTHORITY_SEQ이(가) 삭제되었습니다.


CREATE SEQUENCE member_authority_seq
START WITH 1
INCREMENT BY 1
NOMAXVALUE
NOMINVALUE
NOCYCLE
NOCACHE;
-- Sequence MEMBER_AUTHORITY_SEQ이(가) 생성되었습니다.

---------------------------------------------------------------------------
select *
from security_member
order by registerday desc;
/*
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
MEMBER_ID  MEMBER_NAME  MEMBER_PWD                                                          BIRTH_DATE  ENABLED  EMAIL                                          TEL             POSTCODE  ADDR1                     ADDR2                           CREATED_DATE  MODIFY_DATE  LAST_LOGIN       
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   
*/

/*
delete from security_loginhistory where fk_member_id = 'sec_admin2';
delete from security_member_authority where fk_member_id = 'sec_admin2';
delete from security_member where member_id = 'sec_admin2';
commit;
*/


select *
from security_member_authority
order by fk_member_id;
/*
-----------------------------
NUM  FK_MEMBER_ID  AUTHORITY
-----------------------------

*/

---------------------------------------------------------------------------

/*
  !!! 중요 !!!  
  한명의 사용자는 여러개의 권한을 가질 수 있다.(ROLE_USER, ROLE_ADMIN 등등)

>>> SPRING SECURITY를 사용하지 않고 권한을 체크하는 경우 <<<
회원별로 레벨을 부여하고 게시판 정보에 읽기권한레벨, 쓰기권한레벨을 넣어 준 뒤
회원의 레벨이 게시판의 권한레벨보다 클 경우(부등호로 비교) 권한을 주는 방식이다.

>>> 하지만 SPRING SECURITY는 레벨의 비교가 아닌(부등호로 비교하는 것이 아니라),
    권한(String)이 일치(등호로 비교)하는 대상에 대해 권한을 주기 때문에
관리자 권한 즉, ROLE_ADMIN을 보유하고 있더라도 사용자 권한인 ROLE_USER를 보유하고 있지 않을 경우 접근을 못하게 된다.
따라서 한 유저는 여러개의 권한(1:n)을 보유할 수 있어야 한다.!!!!

출처: http://goldenraccoon.tistory.com/entry/SPRING-SECURITY-DB-LOGIN [황금너구리 블로그]
*/
INSERT INTO security_member_authority(num, fk_member_id, authority) 
VALUES (member_authority_seq.NEXTVAL, 'sec_user1', 'ROLE_USER');  -- 회원임.

INSERT INTO security_member_authority(num, fk_member_id, authority) 
VALUES (member_authority_seq.NEXTVAL, 'sec_user2', 'ROLE_USER_SPECIAL');  -- 특별회원임.

INSERT INTO security_member_authority(num, fk_member_id, authority) 
VALUES (member_authority_seq.NEXTVAL, 'sec_user2', 'ROLE_USER');  -- 특별회원임.

INSERT INTO security_member_authority(num, fk_member_id, authority) 
VALUES (member_authority_seq.NEXTVAL, 'sec_admin1', 'ROLE_USER_SPECIAL');  -- 관리자임.

INSERT INTO security_member_authority(num, fk_member_id, authority) 
VALUES (member_authority_seq.NEXTVAL, 'sec_admin1', 'ROLE_ADMIN');  -- 관리자임.

INSERT INTO security_member_authority(num, fk_member_id, authority) 
VALUES (member_authority_seq.NEXTVAL, 'sec_admin1', 'ROLE_USER');  -- 관리자임.

COMMIT;


drop table security_member_authority purge;
-- Table SECURITY_MEMBER_AUTHORITY이(가) 삭제되었습니다.

select *
from security_member_authority
order by fk_member_id;
/*
-----------------------------
NUM  FK_MEMBER_ID  AUTHORITY
-----------------------------
6    sec_admin1      ROLE_ADMIN
3    sec_admin1      ROLE_USER
5    sec_admin1      ROLE_USER_SPECIAL
1    sec_user1      ROLE_USER
2    sec_user2      ROLE_USER
4    sec_user2      ROLE_USER_SPECIAL
*/

create table security_loginhistory
(historyno      number
,fk_member_id   varchar2(20) not null          -- 회원아이디
,logindate      date default sysdate not null  -- 로그인되어진 접속날짜및시간
,clientip       varchar2(20) not null
,constraint  PK_security_loginhistory primary key(historyno)
,constraint  FK_security_loginhistory foreign key(fk_member_id) references security_member(member_id) on delete cascade
);
-- Table SECURITY_LOGINHISTORY이(가) 생성되었습니다.

create sequence seq_security_loginhistory
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_SECURITY_LOGINHISTORY이(가) 생성되었습니다.

select historyno, fk_member_id, to_char(logindate, 'yyyy-mm-dd hh24:mi:ss') AS logindate, clientip 
from security_loginhistory
order by historyno desc;

------------------------------------------------------------------------

update security_member set enabled = 0
where member_id = 'sec_user1';
commit;

update security_member set enabled = 1
where member_id = 'sec_user1';
commit;


select A.member_id, A.member_name, A.member_pwd, A.enabled, B.authority
from security_member A join security_member_authority B
on A.member_id = B.fk_member_id
order by B.num desc;


select A.member_id, A.member_name, A.member_pwd, A.enabled, B.authority
from security_member A join security_member_authority B
on A.member_id = B.fk_member_id
where B.fk_member_id in (select fk_member_id 
                         from security_member_authority 
                         group by fk_member_id
                         having count(*) > 1)  -- 2개 이상의 권한을 가진 사용자 조회
order by B.num desc;






















