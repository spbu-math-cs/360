create type rank as enum ('second_grade', 'fourth_grade', 'teacher', 'god');

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
	grade int,
	comment varchar(500)
);

truncate Person;
truncate Token;
truncate Event;
truncate Team;
truncate Demo_grade;

select * from Person;
select * from Token;
select * from Event;
select * from Team;
select * from Demo_grade;

SELECT SESSION_USER, CURRENT_USER;

insert into Event values (1, event_type 'demo', date '17-10-2023', time '11:15', time '12:50');