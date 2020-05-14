--dodati usera
--dodati rolu useru
--dodati emp profile
--dodati office profile - zaposliti ga
--dodati server office

INSERT INTO public.users (id, created_at, updated_at, email, password, username, otp)
VALUES (20, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'server1@gmail.com',
'$2a$10$uKBANDuasXdXGsJ7O1AAyuieHpwMGXQ191AgRRRzSFa9bKDab9S4e', 'server1', false)
ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;

INSERT INTO public.user_roles (user_id, role_id)
VALUES (20, 10) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;

INSERT INTO public.contact_information (id, address, city, country, email, phone_number)
VALUES (20, 'address', 'Sarajevo', 'BiH', 'rfejzic1@etf.unsa.ba', '+38733222111')
ON CONFLICT ON CONSTRAINT contact_information_pkey DO NOTHING;

INSERT INTO public.employee_profiles (id, created_at, updated_at, name,
surname, date_of_birth, jmbg, account_id, business_id,contact_info_id)
VALUES (20, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'server1',
'server1','2008-11-11','1235467891234', 20, 1, 20)
ON CONFLICT ON CONSTRAINT employee_profiles_pkey DO NOTHING;

INSERT INTO public.office_profiles(id, created_at, updated_at, employee_id, office_id)
VALUES (20, '2020-03-25 14:45:36.674000','2020-03-25 14:45:36.674000',20, 1)
ON CONFLICT ON CONSTRAINT office_profiles_pkey DO NOTHING;

drop table  if exists  server_office;
create table if not exists server_office
(
    id        bigint not null
        constraint server_office_pkey
            primary key,
    office_id bigint
        constraint fklkbt9vijk5wbovdxaqvaauob7
            references offices
            on delete cascade,
    user_id   bigint
        constraint fk5wmby66ycj8afuhsvmals19r2
            references users
            on delete cascade
);

INSERT INTO public.server_office(id, user_id, office_id)
VALUES(1, 20, 1);

