create type rank as enum ('second_grade', 'fourth_grade', 'teacher', 'god');

drop table if exists Person;
drop table if exists Token;
drop table if exists Event;
drop table if exists Team;
drop table if exists Demo_grade;

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

create type event_type as enum ('demo', '_360');

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

truncate Person restart identity cascade;
truncate Token restart identity cascade;
truncate Event restart identity cascade;
truncate Team restart identity cascade;
truncate Demo_grade restart identity cascade;

select * from Person;
select * from Token;
select * from Event;
select * from Team;
select * from Demo_grade;

SELECT SESSION_USER, CURRENT_USER;

insert into Person (first_name, last_name, father_name, login, password, rank) values
('преподаватель', ' препод', 'учитель', 'prepod', '12345678', rank 'teacher');

insert into Team (number, name, project_name, teacher_id) values
(1, 'Team 1', 'Project 1', 1),
(2, 'Team 2', 'Project 2', 1),
(3, 'Team 3', 'Project 3', 1),
(4, 'Team 4', 'Project 4', 1),
(5, 'Team 5', 'Project 5', 1),
(6, 'Team 6', 'Project 6', 1),
(7, 'NERABOTAEM', 'Project 7', 1);


SET datestyle = dmy;
insert into Event values (1, event_type 'demo', date '17-10-2023', time '11:15', time '12:50');

-- delete from Demo_grade where id < 5;






