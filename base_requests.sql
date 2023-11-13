


-- CREATE ENUMS
create type rank as enum ('second_grade', 'fourth_grade', 'teacher', 'god');
create type event_type as enum ('demo', '_360');

-- DELETE TABLES
drop table if exists Person;
drop table if exists Token;
drop table if exists Event;
drop table if exists Team;
drop table if exists Demo_grade;
drop table if exists Person_team;
drop table if exists Invite;
drop table if exists Inteam_grade;

-- CREATE TABLES
create table if not exists Person(
	id serial primary key,
	first_name varchar(24),
	last_name varchar(24),
	father_name varchar(24),
	login varchar(20) unique,
	password varchar(20),
	rank rank
);

create table if not exists Token(
	id serial primary key,
	login varchar(20),
	token varchar(128) 
);

create table if not exists Event(
	id serial primary key,
	type event_type,
	date date,
	start time,
	finish time
);

create table if not exists Team(
	id serial primary key,
	number int,
	name varchar(50),
	project_name varchar(50),
	teacher_id serial references Person(id)
);

create table if not exists Demo_grade(
    id serial primary key,
	event_id serial references Event(id),
	person_id serial references Person(id),
	team_id serial references Team(id),
    level int,
    grade int,
    presentation int,
    additional int,
	comment varchar(500)
);

create table if not exists Person_team(
	id serial primary key,
	person_id serial references Person(id),
	team_id serial references Team(id)
);

create table if not exists Invite(
	id serial primary key,
	team_id serial references Team(id),
	from_whom serial references Person(id),
	to_whom serial references Person(id)
);

create table if not exists Inteam_grade(
	id serial primary key,
	event_id serial references Event(id),
	evaluator_id serial references Person(id),
	assessed_id serial references Person(id)
);

-- CLEAR TABLES
truncate Person restart identity cascade;
truncate Token restart identity cascade;
truncate Event restart identity cascade;
truncate Team restart identity cascade;
truncate Demo_grade restart identity cascade;

-- SHOW TABLES
select * from Person;
select * from Token;
select * from Event;
select * from Team;
select * from Demo_grade;
select * from Person_team;
select * from Invite;
select * from Inteam_grade;

SELECT SESSION_USER, CURRENT_USER;

-- SET OPTIONS
SET datestyle = dmy;
set client_encoding='WIN866';

-- INSERT START VALUES
insert into Person (first_name, last_name, father_name, login, password, rank) values
('преподаватель', ' препод', 'учитель', 'prepod', '12345678', rank 'teacher');

insert into Team (number, name, project_name, teacher_id) values
(1, 'Team 1', 'Roomkn', 1),
(2, 'Team 2', 'Topic Keeper', 1),
(3, 'Team 3', 'IG Platform', 1),
(4, 'Team 4', 'Zakroma', 1),
(5, 'Team 5', 'RollPlayer', 1),
(6, 'Team 6', 'GiveGift', 1),
(7, 'Team 7', '360', 1);

insert into Event values (1, event_type 'demo', date '17-10-2023', time '11:15', time '12:50');