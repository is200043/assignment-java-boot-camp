create table product
(
    id                    varchar(50)    not null,
    name                  varchar(255)   not null,
    price                 decimal(19, 2) not null,
    price_before_discount decimal(19, 2) not null,
    qty                   integer        not null,
    brand                 varchar(100)   not null,
    options               text           null,
    pics                  text           null,
    rating                decimal(19, 2) not null default 0,
    reviews               integer        not null default 0,
    product_group         varchar(100)   not null,
    merchant_id           varchar(50)    not null,
    end_promotion_at      datetime       null,
    primary key (id)
);

create table user
(
    id        varchar(50)  not null,
    name      varchar(150) not null,
    address   varchar(255) not null,
    district  varchar(100) not null,
    province  varchar(100) not null,
    zipcode   varchar(5)   not null,
    mobile_no varchar(10)  not null,
    primary key (id)
);

create table user_item
(
    user_id    varchar(50)    not null,
    product_id varchar(50)    not null,
    qty        integer        not null,
    price      decimal(19, 2) not null,
    options    text           null,
    primary key (user_id, product_id)
);

create table user_purchance_history
(
    id              varchar(50)    not null,
    user_id         varchar(50)    not null,
    total_price     decimal(19, 2) not null,
    total_shipping  decimal(19, 2) not null,
    total_discount  decimal(19, 2) not null,
    total_amount    decimal(19, 2) not null,
    payment_type    varchar(50)    not null,
    payment_marking varchar(255)   not null,
    primary key (id)
);

create table user_purchance_history_detail
(
    transaction_id varchar(50)    not null,
    product_id     varchar(50)    not null,
    qty            integer        not null,
    price          decimal(19, 2) not null,
    options        text           null,
    primary key (transaction_id, product_id)
);