use registration_system_db;

create table Users(
    id int primary key auto_increment,
    first_name nvarchar(250) not null,
    last_name nvarchar(250) not null,
    age int not null,
    email nvarchar(250) not null unique,
    password nvarchar(250) not null
);
