create table product
(
   id varchar(50) not null,
   name varchar(255) not null,
   price decimal(19, 2) not null,
   price_before_discount decimal(19, 2) not null,
   qty integer not null,
   brand varchar(255) not null,
   options text null,
   pics text null,
   rating decimal(19, 2) not null default 0,
   reviews integer not null default 0,
   product_group varchar(255) not null,
   primary key(id)
);