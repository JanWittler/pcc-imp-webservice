CREATE TABLE public.user
(
    id SERIAL PRIMARY KEY NOT NULL,
    mail TEXT NOT NULL,
    password TEXT NOT NULL,
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