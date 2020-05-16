DROP TABLE IF EXISTS product_comments;

CREATE TABLE public.product_comments
(
    id bigint NOT NULL,
    product_id integer NOT NULL,
    first_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    text character varying(255) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    CONSTRAINT product_comments_pk PRIMARY KEY (id),
    CONSTRAINT fklvw9kwav1pell1wg6xo0dmme6 FOREIGN KEY (product_id)
        REFERENCES public.products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)