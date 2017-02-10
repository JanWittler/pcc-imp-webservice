CREATE TABLE public.user
(
    id SERIAL PRIMARY KEY NOT NULL,
    mail TEXT NOT NULL,
    password TEXT NOT NULL,
    uuid TEXT NOT NULL,
    verified BOOLEAN DEFAULT FALSE  NOT NULL,
    password_salt BYTEA NOT NULL
);
CREATE UNIQUE INDEX "user_id_uindex" ON public.user (id);
CREATE UNIQUE INDEX "user_mail_uindex" ON public.user (mail);
CREATE UNIQUE INDEX "user_uuid_uindex" ON public.user (uuid);

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