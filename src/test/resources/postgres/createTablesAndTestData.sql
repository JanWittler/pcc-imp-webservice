CREATE TABLE public.user
(
    id SERIAL PRIMARY KEY NOT NULL,
    mail TEXT NOT NULL,
    password TEXT NOT NULL,
    uuid TEXT NOT NULL,
    verified BOOLEAN DEFAULT FALSE  NOT NULL,
    password_salt TEXT NOT NULL
);
CREATE UNIQUE INDEX "user_id_uindex" ON public.user (id);
CREATE UNIQUE INDEX "user_mail_uindex" ON public.user (mail);
CREATE UNIQUE INDEX "user_uuid_uindex" ON public.user (uuid);

CREATE TABLE public.sessions
(
    id SERIAL NOT NULL,
    token TEXT NOT NULL,
    PRIMARY KEY (id, token)
);

CREATE TABLE public.video
(
    id SERIAL PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    video_name TEXT NOT NULL,
    meta_name TEXT NOT NULL,
    CONSTRAINT video_user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id)
);
CREATE UNIQUE INDEX "video_video_name_uindex" ON public.video (video_name);
CREATE UNIQUE INDEX "video_id_uindex" ON public.video (id);
CREATE UNIQUE INDEX "video_meta_name_uindex" ON public.video (meta_name);


insert into "user" (mail,password,uuid,password_salt) values ('test@example.de','passwordTest',321675, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test2@example.de','passwordTest',321668, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test3@example.de','passwordTest',321698, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test4@example.de','passwordTest',321669, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test5@example.de','passwordTest',321656, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test6@example.de','passwordTest',321655, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test7@example.de','passwordTest',321658, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test8@example.de','passwordTest',321662, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test9@example.de','passwordTest',321657, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test10@example.de','passwordTest',321706, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test11@example.de','passwordTest',321707, '123asd');
insert into "user" (mail,password,uuid,password_salt) values ('test12@example.de','passwordTest',321684, '123asd');

insert into "video" (user_id,video_name,meta_name) values (3,'testvideoName234235','meta_name_test1');
insert into "video" (user_id,video_name,meta_name) values (3,'testvideoName234241','meta_name_test2');
insert into "video" (user_id,video_name,meta_name) values (4,'testvideoName234237','meta_name_test3');
insert into "video" (user_id,video_name,meta_name) values (6,'testvideoName234236','meta_name_test4');
insert into "video" (user_id,video_name,meta_name) values (8,'testvideoName234290','meta_name_test5');
insert into "video" (user_id,video_name,meta_name) values (8,'testvideoName234251','meta_name_test6');
insert into "video" (user_id,video_name,meta_name) values (9,'testvideoName234246','meta_name_test7');
insert into "video" (user_id,video_name,meta_name) values (8,'testvideoName234263','meta_name_test8');
insert into "video" (user_id,video_name,meta_name) values (9,'testvideoName23234234','meta_name_test9');
insert into "video" (user_id,video_name,meta_name) values (10,'testvideoName234234','meta_name_test10');
