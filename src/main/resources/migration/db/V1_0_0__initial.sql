/*
 * 	Script to create initial database
 * 
 * 
 * 
 * 
 * 
 * 
 */

create user ELibrary with password 'changePassword';

create schema ELibrary authorization ELibrary; 

create sequence ELibrary.systemseq;

create table ELibrary.BOOKSERIES (
	bs_Id		BIGINT primary key,
 	bs_Parent	BIGINT references ELibrary.BOOKSERIES(bs_Id),
	bs_Name		text not null,
	bs_Comment	text
);	

grant select, insert, update, delete on ELibrary.BOOKSERIES to ELibrary;

create table ELibrary.BOOKAUTHORS (
	ba_Id		BIGINT primary key,
	ba_Name		text not null,
	ba_Comment	text
);	

grant select, insert, update, delete on ELibrary.BOOKAUTHORS to ELibrary;

create table ELibrary.BOOKPUBLISHERS (
	bp_Id		BIGINT primary key,
	bp_Name		text not null,
	bp_Comment	text
);	

grant select, insert, update, delete on ELibrary.BOOKPUBLISHERS to ELibrary;

create table ELibrary.BOOKLIST (
	bl_Id 		BIGINT primary key,
	bl_Parent 	BIGINT references ELibrary.BOOKLIST(bl_Id),
	bl_Code 	text,
	bs_Id 		BIGINT references ELibrary.BOOKSERIES(bs_Id),
	bs_Seq 		text,
	bl_Title 	text not null,
	bl_Year 	integer not null,
	bp_Id 		BIGINT references ELibrary.BOOKPUBLISHERS(bp_Id),
	bl_Comment 	text,
	bl_Tags 	text,
	bl_Image 	bytea,
	bl_Mime 	text,
	bl_Content 	bytea not null,
	bl_Page 	integer
);	

grant select, insert, update, delete on ELibrary.BOOKLIST to ELibrary;

create table ELibrary.BOOK2AUTHORS (
	bl_Id		BIGINT,
	ba_Id		BIGINT,
	primary key(bl_Id, ba_Id)
);	

grant select, insert, update, delete on ELibrary.BOOK2AUTHORS to ELibrary;
