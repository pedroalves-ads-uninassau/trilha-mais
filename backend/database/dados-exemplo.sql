-- =====================================================================
-- TRILHA+ — Dados de exemplo (mesmos dados das telas do front-end)
-- Rodar DEPOIS do schema.sql (ou depois de subir o back-end uma vez).
-- Pode rodar mais de uma vez: nada é duplicado.
--
-- Login de demonstração:  demo@trilhamais.app  /  senha 123456
-- =====================================================================

BEGIN;

-- Estudante de demonstração (senha 123456 em hash BCrypt)
INSERT INTO users (name, email, password, streak_days, avatar_emoji)
VALUES ('Estudante Demo', 'demo@trilhamais.app',
        '$2a$10$/jdKq3t/R.ln48cLlYJvZuRFCZR34LzFdqqwjuLWIpyV7Tc7lDZIC', 0, '🎓')
ON CONFLICT (email) DO NOTHING;

-- Matérias (cores e ícones iguais ao app)
INSERT INTO subjects (name, description, color_key, primary_color, icon, user_id, created_at)
SELECT v.name, v.description, v.color_key, v.primary_color, v.icon, u.id, CURRENT_TIMESTAMP
FROM users u
CROSS JOIN (VALUES
    ('Português',  'Linguagens, gramática e interpretação', 'portugues',  '#2563EB', '📔'),
    ('Matemática', 'Álgebra, funções e probabilidade',      'matematica', '#8B5CF6', '📐'),
    ('História',   'Brasil e história geral',               'historia',   '#EA580C', '📗'),
    ('Biologia',   'Citologia, genética e ecologia',        'biologia',   '#10B981', '🔬')
) AS v(name, description, color_key, primary_color, icon)
WHERE u.email = 'demo@trilhamais.app'
  AND NOT EXISTS (SELECT 1 FROM subjects s WHERE s.user_id = u.id AND s.name = v.name);

-- Assuntos com status (pendente, andamento, concluido)
INSERT INTO topics (name, description, status, subject_id, created_at)
SELECT v.topic, NULL, v.status, s.id, CURRENT_TIMESTAMP
FROM subjects s
JOIN users u ON u.id = s.user_id
JOIN (VALUES
    ('Português',  'Variações linguísticas',   'andamento'),
    ('Português',  'Figuras de linguagem',     'concluido'),
    ('Português',  'Classes gramaticais',      'pendente'),
    ('Português',  'Interpretação de texto',   'pendente'),
    ('Português',  'Concordância verbal',      'pendente'),
    ('Matemática', 'Função quadrática',        'andamento'),
    ('Matemática', 'Equações',                 'concluido'),
    ('Matemática', 'Porcentagem',              'concluido'),
    ('Matemática', 'Progressão aritmética',    'pendente'),
    ('Matemática', 'Probabilidade',            'pendente'),
    ('Matemática', 'Matrizes',                 'pendente'),
    ('História',   'Brasil Colônia',           'concluido'),
    ('História',   'Revolução Industrial',     'pendente'),
    ('História',   'Primeira Guerra Mundial',  'pendente'),
    ('História',   'Segunda Guerra Mundial',   'pendente'),
    ('História',   'República Velha',          'pendente'),
    ('Biologia',   'Citologia',                'concluido'),
    ('Biologia',   'Genética',                 'concluido'),
    ('Biologia',   'Ecologia',                 'concluido'),
    ('Biologia',   'Fisiologia humana',        'concluido'),
    ('Biologia',   'Botânica',                 'concluido'),
    ('Biologia',   'Zoologia',                 'pendente'),
    ('Biologia',   'Evolução',                 'pendente')
) AS v(subject, topic, status) ON v.subject = s.name
WHERE u.email = 'demo@trilhamais.app'
  AND NOT EXISTS (SELECT 1 FROM topics t WHERE t.subject_id = s.id AND t.name = v.topic);

-- Material de estudo de exemplo
INSERT INTO study_materials (title, content, type, topic_id, created_at)
SELECT 'Resumo: variações linguísticas',
       'A língua muda conforme a região (regional), o grupo social (social), a situação (situacional) e o tempo (histórica).',
       'SUMMARY', t.id, CURRENT_TIMESTAMP
FROM topics t
JOIN subjects s ON s.id = t.subject_id
JOIN users u ON u.id = s.user_id
WHERE u.email = 'demo@trilhamais.app' AND t.name = 'Variações linguísticas'
  AND NOT EXISTS (SELECT 1 FROM study_materials m WHERE m.topic_id = t.id);

-- Agenda: hoje, amanhã e depois de amanhã (ligada à matéria e ao assunto)
INSERT INTO schedules (title, subject_name, color_key, icon, scheduled_date, days_ahead, scheduled_time,
                       completed, user_id, subject_id, topic_id, created_at)
SELECT t.name, s.name, s.color_key, s.icon, CURRENT_DATE + v.days_ahead, v.days_ahead, v.time,
       FALSE, u.id, s.id, t.id, CURRENT_TIMESTAMP
FROM users u
JOIN subjects s ON s.user_id = u.id
JOIN topics t ON t.subject_id = s.id
JOIN (VALUES
    ('Variações linguísticas',  0, '14:00'),
    ('Função quadrática',       0, '16:00'),
    ('Revolução Industrial',    1, '10:00'),
    ('Zoologia',                1, '15:00'),
    ('Classes gramaticais',     2, '09:00'),
    ('Probabilidade',           2, '14:00'),
    ('Primeira Guerra Mundial', 2, '17:00')
) AS v(topic, days_ahead, time) ON v.topic = t.name
WHERE u.email = 'demo@trilhamais.app'
  AND NOT EXISTS (SELECT 1 FROM schedules sc WHERE sc.user_id = u.id AND sc.topic_id = t.id);

-- Configurações padrão do Perfil
INSERT INTO user_settings (user_id, notifications_enabled, theme)
SELECT u.id, TRUE, 'light' FROM users u
WHERE u.email = 'demo@trilhamais.app'
  AND NOT EXISTS (SELECT 1 FROM user_settings us WHERE us.user_id = u.id);

COMMIT;
