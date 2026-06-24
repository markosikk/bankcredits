USE bank_credits;

INSERT INTO legal_entities
(legal_entity_name, ownership_type, legal_address, phone_number, contact_person)
VALUES
('ООО «Гангик»', 'ООО', 'г. Москва, ул. 800-летия Москвы, д. 1', '+7 495 218-45-19', 'Петросян Марк Эдуардович'),
('АО «Стамп»', 'АО', 'г. Москва, ул. Бориса Галушкина, д. 9', '+7 495 764-30-82', 'Севрюков Ярослав Сергеевич');

INSERT INTO credit_operation_types
(credit_type_name, credit_conditions, interest_rate, return_period_days)
VALUES
('Краткосрочный кредит', 'Кредит на пополнение оборотных средств', 12.50, 180),
('Инвестиционный кредит', 'Кредит на развитие бизнеса и закупку оборудования', 15.00, 730),
('Льготный кредит', 'Кредит для постоянных корпоративных клиентов', 9.75, 365);

INSERT INTO credits
(legal_entity_id, credit_type_id, amount, issue_date, actual_return_date)
VALUES
(1, 1, 500000.00, '2026-01-15', '2026-04-15'),
(2, 2, 1200000.00, '2026-02-10', NULL);

INSERT INTO credit_repayments
(credit_id, repayment_amount, repayment_date)
VALUES
(1, 300000.00, '2026-03-01'),
(1, 200000.00, '2026-04-15'),
(2, 400000.00, '2026-05-20');

INSERT INTO fines
(credit_id, fine_amount)
VALUES
(2, 15000.00);

INSERT INTO system_users
(login, password_hash, role)
VALUES
('admin', 'Admin123!', 'ADMIN'),
('user', 'User123!', 'USER');