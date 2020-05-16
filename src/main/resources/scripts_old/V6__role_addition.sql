INSERT INTO public.roles (id, name) VALUES (9, 'ROLE_PRP') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
--adding a pr worker with privileges
INSERT INTO public.users (id, created_at, updated_at, email, password, username)
VALUES (9, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'eradusic1@etf.unsa.ba',
        '$2a$10$uKBANDuasXdXGsJ7O1AAyuieHpwMGXQ191AgRRRzSFa9bKDab9S4e', 'eradusic1') ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;

INSERT INTO public.user_roles (user_id, role_id) VALUES (9, 5) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;
INSERT INTO public.user_roles (user_id, role_id) VALUES (9, 9) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;

INSERT INTO public.contact_information (id, address, city, country, email, phone_number)
VALUES (9, 'Adresa 46', 'Sarajevo', 'BiH', 'eradusic1@etf.unsa.ba', '+38733222112');

INSERT INTO public.employee_profiles (id, created_at, updated_at, name, surname, date_of_birth, jmbg, account_id, business_id, contact_info_id)
VALUES (9, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'Esmina', 'Radusic', '1998-01-17','1235462891234',9, 1, 9);
