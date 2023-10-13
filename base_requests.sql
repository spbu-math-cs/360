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

truncate Person;
truncate Token;
truncate Event;

select * from Person;
select * from Token;
select * from Event;
SELECT SESSION_USER, CURRENT_USER;

insert into Event values (1, event_type 'demo', date '17-10-2023', time '11:15', time '12:50');