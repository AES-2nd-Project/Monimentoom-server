
    create table goods (
        price integer,
        id bigint not null auto_increment,
        user_id bigint not null,
        description varchar(255),
        image_url varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table positions (
        height_unit integer not null,
        width_unit integer not null,
        x integer not null,
        y integer not null,
        goods_id bigint not null,
        id bigint not null auto_increment,
        room_id bigint not null,
        wall enum ('LEFT','RIGHT') not null,
        primary key (id)
    ) engine=InnoDB;

    create table rooms (
        id bigint not null auto_increment,
        user_id bigint not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users (
        id bigint not null auto_increment,
        email varchar(255) not null,
        nickname varchar(255) not null,
        password varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create index idx_positions_room_id 
       on positions (room_id);

    create index idx_positions_goods_id 
       on positions (goods_id);

    alter table rooms 
       add constraint UK1kuqhbfxed2e8t571uo82n545 unique (name);

    create index idx_nickname 
       on users (nickname);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table users 
       add constraint UK2ty1xmrrgtn89xt7kyxx6ta7h unique (nickname);

    alter table goods 
       add constraint FKovmhvi2lnpxl8r3mjrysrs197 
       foreign key (user_id) 
       references users (id);

    alter table positions 
       add constraint FKsqwsfunmr5ymq8wfwkl8dsrh 
       foreign key (goods_id) 
       references goods (id);

    alter table positions 
       add constraint FKti2okotb17da6ll4rat2fdqy0 
       foreign key (room_id) 
       references rooms (id);

    alter table rooms 
       add constraint FKa84ab0lpjkgd9beja545d9ysd 
       foreign key (user_id) 
       references users (id);
