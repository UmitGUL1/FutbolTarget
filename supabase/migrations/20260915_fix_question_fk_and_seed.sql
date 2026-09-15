-- ==============================================================================
-- FIX: MATCHES QUESTION FOREIGN KEY CONSTRAINT & IDEMPOTENT SEED DATA
-- Migration file: supabase/migrations/20260915_fix_question_fk_and_seed.sql
-- ==============================================================================

-- 1. ENSURE QUESTION_KEY COLUMN EXISTS IN QUESTIONS TABLE
ALTER TABLE public.questions ADD COLUMN IF NOT EXISTS question_key TEXT;

-- 2. SEED QUESTIONS IDEMPOTENTLY (Including the exact requested Premier League question)
INSERT INTO public.questions (id, question_key, title, competition, stat_type, target, pick_count, difficulty, description, eligible_competitions)
VALUES
(
    'pl-goals-500',
    'pl-goals-500',
    '5 futbolcuyla 500 Premier League golüne mümkün olduğunca yaklaş',
    'Premier League',
    'goals',
    500,
    5,
    'Orta',
    '5 futbolcu seçerek Premier League''de toplam 500 gole mümkün olduğunca yaklaş.',
    ARRAY['Premier League']
),
(
    'curated-premier-league-500',
    'curated-premier-league-500',
    'Premier League Gol Avcıları (500 Gol)',
    'Premier League',
    'goals',
    500,
    5,
    'Orta',
    '5 futbolcu seçerek Premier League''de toplam 500 gole en yakın skora ulaş.',
    ARRAY['Premier League']
),
(
    'curated-career-1500',
    'curated-career-1500',
    'Kariyer Gol Kralları',
    'Kariyer',
    'goals',
    1500,
    5,
    'Zor',
    '5 efsane futbolcu ile kariyer boyunca toplam 1500 gole en yakın toplamı elde et.',
    ARRAY['Kariyer', '5 Büyük Lig']
),
(
    'curated-la-liga-600',
    'curated-la-liga-600',
    'La Liga Efsaneleri',
    'La Liga',
    'goals',
    600,
    5,
    'Orta',
    '5 futbolcu seçerek İspanya La Liga''da 600 gole en çok yaklaşan kazanır.',
    ARRAY['La Liga']
),
(
    'curated-champions-league-200',
    'curated-champions-league-200',
    'Şampiyonlar Ligi Golcüleri',
    'Champions League',
    'goals',
    200,
    5,
    'Zor',
    'UEFA Şampiyonlar Ligi''nde 5 seçimle 200 gol hedefine ulaş.',
    ARRAY['Champions League']
),
(
    'curated-top-5-leagues-800',
    'curated-top-5-leagues-800',
    '5 Büyük Lig Gol Düellosu',
    '5 Büyük Lig',
    'goals',
    800,
    5,
    'Orta',
    'Avrupa''nın 5 büyük liginde atılan toplam 800 gole en yakın 5 futbolcuyu seç.',
    ARRAY['5 Büyük Lig']
),
(
    'curated-pl-assists-250',
    'curated-pl-assists-250',
    'Premier League Asist Ustaları',
    'Premier League',
    'assists',
    250,
    5,
    'Zor',
    'Premier League''de toplam 250 asiste en yakın kadroyu kur.',
    ARRAY['Premier League']
)
ON CONFLICT (id) DO UPDATE SET
    question_key = EXCLUDED.question_key,
    title = EXCLUDED.title,
    competition = EXCLUDED.competition,
    stat_type = EXCLUDED.stat_type,
    target = EXCLUDED.target,
    pick_count = EXCLUDED.pick_count,
    difficulty = EXCLUDED.difficulty,
    description = EXCLUDED.description,
    eligible_competitions = EXCLUDED.eligible_competitions;

-- 3. SEED FOOTBALLERS IDEMPOTENTLY
INSERT INTO public.footballers (id, name, nationality, position, photo_url, club_name, eligible_competitions)
VALUES
('alan-shearer', 'Alan Shearer', 'İngiltere', 'Forvet', '', 'Newcastle / Blackburn', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League']),
('harry-kane', 'Harry Kane', 'İngiltere', 'Forvet', '', 'Bayern Munich / Spurs', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Bundesliga', 'Champions League']),
('wayne-rooney', 'Wayne Rooney', 'İngiltere', 'Forvet', '', 'Manchester United / Everton', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Champions League']),
('sergio-aguero', 'Sergio Agüero', 'Arjantin', 'Forvet', '', 'Manchester City / Atletico', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'La Liga', 'Champions League']),
('thierry-henry', 'Thierry Henry', 'Fransa', 'Forvet', '', 'Arsenal / Barcelona', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'La Liga', 'Champions League']),
('mohamed-salah', 'Mohamed Salah', 'Mısır', 'Sağ Kanat', '', 'Liverpool', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Serie A', 'Champions League']),
('erling-haaland', 'Erling Haaland', 'Norveç', 'Forvet', '', 'Manchester City / Dortmund', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Bundesliga', 'Champions League']),
('cristiano-ronaldo', 'Cristiano Ronaldo', 'Portekiz', 'Forvet', '', 'Al-Nassr / Real / Man Utd', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'La Liga', 'Serie A', 'Champions League']),
('lionel-messi', 'Lionel Messi', 'Arjantin', 'Sağ Kanat / Forvet', '', 'Inter Miami / Barcelona / PSG', ARRAY['Kariyer', '5 Büyük Lig', 'La Liga', 'Ligue 1', 'Champions League']),
('kylian-mbappe', 'Kylian Mbappé', 'Fransa', 'Forvet', '', 'Real Madrid / PSG', ARRAY['Kariyer', '5 Büyük Lig', 'Ligue 1', 'La Liga', 'Champions League']),
('robert-lewandowski', 'Robert Lewandowski', 'Polonya', 'Forvet', '', 'Barcelona / Bayern / BVB', ARRAY['Kariyer', '5 Büyük Lig', 'Bundesliga', 'La Liga', 'Champions League']),
('karim-benzema', 'Karim Benzema', 'Fransa', 'Forvet', '', 'Al-Ittihad / Real Madrid', ARRAY['Kariyer', '5 Büyük Lig', 'La Liga', 'Ligue 1', 'Champions League']),
('neymar-jr', 'Neymar Jr', 'Brezilya', 'Sol Kanat', '', 'Al-Hilal / PSG / Santos', ARRAY['Kariyer', '5 Büyük Lig', 'La Liga', 'Ligue 1', 'Champions League']),
('kevin-de-bruyne', 'Kevin De Bruyne', 'Belçika', 'Orta Saha', '', 'Manchester City / Wolfsburg', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Bundesliga', 'Champions League']),
('son-heung-min', 'Son Heung-min', 'Güney Kore', 'Sol Kanat', '', 'Tottenham Hotspur', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Bundesliga', 'Champions League']),
('sadio-mane', 'Sadio Mané', 'Senegal', 'Sol Kanat', '', 'Al-Nassr / Liverpool / Bayern', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Bundesliga', 'Champions League']),
('raheem-sterling', 'Raheem Sterling', 'İngiltere', 'Kanat', '', 'Arsenal / Chelsea / Man City', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Champions League']),
('romelu-lukaku', 'Romelu Lukaku', 'Belçika', 'Forvet', '', 'Napoli / Chelsea / Inter / Everton', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'Serie A', 'Champions League']),
('jamie-vardy', 'Jamie Vardy', 'İngiltere', 'Forvet', '', 'Leicester City', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League']),
('luis-suarez', 'Luis Suárez', 'Uruguay', 'Forvet', '', 'Inter Miami / Barcelona / Liverpool', ARRAY['Kariyer', '5 Büyük Lig', 'Premier League', 'La Liga', 'Champions League'])
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    nationality = EXCLUDED.nationality,
    position = EXCLUDED.position,
    club_name = EXCLUDED.club_name,
    eligible_competitions = EXCLUDED.eligible_competitions;

-- 4. SEED SECRET STATS IDEMPOTENTLY
INSERT INTO public.footballer_stats (footballer_id, competition, stat_type, stat_value)
VALUES
-- Alan Shearer
('alan-shearer', 'Premier League', 'goals', 260),
('alan-shearer', '5 Büyük Lig', 'goals', 260),
('alan-shearer', 'Kariyer', 'goals', 379),
('alan-shearer', 'Premier League', 'assists', 64),
-- Harry Kane
('harry-kane', 'Premier League', 'goals', 213),
('harry-kane', 'Bundesliga', 'goals', 45),
('harry-kane', '5 Büyük Lig', 'goals', 258),
('harry-kane', 'Champions League', 'goals', 39),
('harry-kane', 'Kariyer', 'goals', 412),
('harry-kane', 'Premier League', 'assists', 46),
-- Wayne Rooney
('wayne-rooney', 'Premier League', 'goals', 208),
('wayne-rooney', '5 Büyük Lig', 'goals', 208),
('wayne-rooney', 'Champions League', 'goals', 34),
('wayne-rooney', 'Kariyer', 'goals', 366),
('wayne-rooney', 'Premier League', 'assists', 103),
-- Sergio Aguero
('sergio-aguero', 'Premier League', 'goals', 184),
('sergio-aguero', 'La Liga', 'goals', 75),
('sergio-aguero', '5 Büyük Lig', 'goals', 259),
('sergio-aguero', 'Champions League', 'goals', 47),
('sergio-aguero', 'Kariyer', 'goals', 426),
('sergio-aguero', 'Premier League', 'assists', 47),
-- Thierry Henry
('thierry-henry', 'Premier League', 'goals', 175),
('thierry-henry', 'La Liga', 'goals', 35),
('thierry-henry', 'Ligue 1', 'goals', 20),
('thierry-henry', '5 Büyük Lig', 'goals', 233),
('thierry-henry', 'Champions League', 'goals', 51),
('thierry-henry', 'Kariyer', 'goals', 411),
('thierry-henry', 'Premier League', 'assists', 74),
-- Mohamed Salah
('mohamed-salah', 'Premier League', 'goals', 160),
('mohamed-salah', 'Serie A', 'goals', 35),
('mohamed-salah', '5 Büyük Lig', 'goals', 195),
('mohamed-salah', 'Champions League', 'goals', 48),
('mohamed-salah', 'Kariyer', 'goals', 345),
('mohamed-salah', 'Premier League', 'assists', 72),
-- Erling Haaland
('erling-haaland', 'Premier League', 'goals', 75),
('erling-haaland', 'Bundesliga', 'goals', 62),
('erling-haaland', '5 Büyük Lig', 'goals', 137),
('erling-haaland', 'Champions League', 'goals', 44),
('erling-haaland', 'Kariyer', 'goals', 260),
-- Cristiano Ronaldo
('cristiano-ronaldo', 'Premier League', 'goals', 103),
('cristiano-ronaldo', 'La Liga', 'goals', 311),
('cristiano-ronaldo', 'Serie A', 'goals', 81),
('cristiano-ronaldo', '5 Büyük Lig', 'goals', 495),
('cristiano-ronaldo', 'Champions League', 'goals', 140),
('cristiano-ronaldo', 'Kariyer', 'goals', 895),
-- Lionel Messi
('lionel-messi', 'La Liga', 'goals', 474),
('lionel-messi', 'Ligue 1', 'goals', 22),
('lionel-messi', '5 Büyük Lig', 'goals', 496),
('lionel-messi', 'Champions League', 'goals', 129),
('lionel-messi', 'Kariyer', 'goals', 838),
('lionel-messi', 'La Liga', 'assists', 192),
('lionel-messi', 'Kariyer', 'assists', 374),
-- Kylian Mbappe
('kylian-mbappe', 'Ligue 1', 'goals', 191),
('kylian-mbappe', 'La Liga', 'goals', 10),
('kylian-mbappe', '5 Büyük Lig', 'goals', 201),
('kylian-mbappe', 'Champions League', 'goals', 49),
('kylian-mbappe', 'Kariyer', 'goals', 290),
-- Robert Lewandowski
('robert-lewandowski', 'Bundesliga', 'goals', 312),
('robert-lewandowski', 'La Liga', 'goals', 50),
('robert-lewandowski', '5 Büyük Lig', 'goals', 362),
('robert-lewandowski', 'Champions League', 'goals', 94),
('robert-lewandowski', 'Kariyer', 'goals', 645),
-- Karim Benzema
('karim-benzema', 'La Liga', 'goals', 238),
('karim-benzema', 'Ligue 1', 'goals', 43),
('karim-benzema', '5 Büyük Lig', 'goals', 281),
('karim-benzema', 'Champions League', 'goals', 90),
('karim-benzema', 'Kariyer', 'goals', 472),
-- Jamie Vardy
('jamie-vardy', 'Premier League', 'goals', 137),
('jamie-vardy', '5 Büyük Lig', 'goals', 137),
('jamie-vardy', 'Kariyer', 'goals', 192),
-- Romelu Lukaku
('romelu-lukaku', 'Premier League', 'goals', 121),
('romelu-lukaku', 'Serie A', 'goals', 71),
('romelu-lukaku', '5 Büyük Lig', 'goals', 192),
('romelu-lukaku', 'Kariyer', 'goals', 389),
-- Son Heung-min
('son-heung-min', 'Premier League', 'goals', 122),
('son-heung-min', 'Bundesliga', 'goals', 41),
('son-heung-min', '5 Büyük Lig', 'goals', 163),
('son-heung-min', 'Kariyer', 'goals', 218),
-- Sadio Mane
('sadio-mane', 'Premier League', 'goals', 111),
('sadio-mane', 'Bundesliga', 'goals', 7),
('sadio-mane', '5 Büyük Lig', 'goals', 118),
('sadio-mane', 'Champions League', 'goals', 27),
('sadio-mane', 'Kariyer', 'goals', 223),
-- Raheem Sterling
('raheem-sterling', 'Premier League', 'goals', 123),
('raheem-sterling', '5 Büyük Lig', 'goals', 123),
('raheem-sterling', 'Champions League', 'goals', 27),
('raheem-sterling', 'Kariyer', 'goals', 173),
-- Luis Suarez
('luis-suarez', 'La Liga', 'goals', 178),
('luis-suarez', 'Premier League', 'goals', 69),
('luis-suarez', '5 Büyük Lig', 'goals', 247),
('luis-suarez', 'Champions League', 'goals', 27),
('luis-suarez', 'Kariyer', 'goals', 557),
-- Kevin De Bruyne
('kevin-de-bruyne', 'Premier League', 'goals', 68),
('kevin-de-bruyne', 'Premier League', 'assists', 112),
('kevin-de-bruyne', 'Bundesliga', 'goals', 13),
('kevin-de-bruyne', 'Bundesliga', 'assists', 28),
('kevin-de-bruyne', 'Kariyer', 'assists', 245)
ON CONFLICT (footballer_id, competition, stat_type) DO UPDATE SET
    stat_value = EXCLUDED.stat_value;


-- 5. UPDATE JOIN_ROOM RPC WITH SELF-HEALING QUESTION FALLBACK
CREATE OR REPLACE FUNCTION public.join_room(
    p_room_code TEXT,
    p_user_id UUID,
    p_username TEXT
)
RETURNS JSONB AS $$
DECLARE
    v_room RECORD;
    v_question_id TEXT;
    v_match_id UUID;
    v_first_player UUID;
    v_clean_code TEXT;
BEGIN
    v_clean_code := UPPER(TRIM(p_room_code));

    -- Find waiting room
    SELECT * INTO v_room FROM public.rooms 
    WHERE room_code = v_clean_code AND status = 'waiting'
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Oda bulunamadı veya maç çoktan başladı.');
    END IF;

    IF v_room.host_user_id = p_user_id THEN
        RETURN jsonb_build_object('success', false, 'error', 'Kendi oluşturduğunuz odaya konuk olarak katılamazsınız.');
    END IF;

    -- Upsert guest profile
    INSERT INTO public.profiles (id, username)
    VALUES (p_user_id, COALESCE(p_username, 'Oyuncu-' || substr(p_user_id::text, 1, 4)))
    ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username, updated_at = NOW();

    -- Select random question from pool
    SELECT id INTO v_question_id FROM public.questions ORDER BY RANDOM() LIMIT 1;

    -- Self-healing fallback: If questions table was empty, insert default question and use its ID
    IF v_question_id IS NULL THEN
        INSERT INTO public.questions (
            id, question_key, title, competition, stat_type, target, pick_count, difficulty, description, eligible_competitions
        ) VALUES (
            'pl-goals-500',
            'pl-goals-500',
            '5 futbolcuyla 500 Premier League golüne mümkün olduğunca yaklaş',
            'Premier League',
            'goals',
            500,
            5,
            'Orta',
            '5 futbolcu seçerek Premier League''de toplam 500 gole mümkün olduğunca yaklaş.',
            ARRAY['Premier League']
        )
        ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title
        RETURNING id INTO v_question_id;
    END IF;

    -- Randomly decide first player
    IF random() > 0.5 THEN
        v_first_player := v_room.host_user_id;
    ELSE
        v_first_player := p_user_id;
    END IF;

    -- Update room
    UPDATE public.rooms 
    SET guest_user_id = p_user_id, status = 'in_match'
    WHERE id = v_room.id;

    -- Create server authoritative match (Guaranteed to have valid question_id)
    INSERT INTO public.matches (
        room_id,
        player_a_id,
        player_b_id,
        question_id,
        first_player_id,
        current_player_id,
        status,
        current_pick_number,
        turn_started_at,
        turn_deadline_at
    ) VALUES (
        v_room.id,
        v_room.host_user_id,
        p_user_id,
        v_question_id,
        v_first_player,
        v_first_player,
        'picking',
        1,
        NOW(),
        NOW() + INTERVAL '20 seconds'
    ) RETURNING id INTO v_match_id;

    RETURN jsonb_build_object(
        'success', true,
        'match_id', v_match_id,
        'room_id', v_room.id,
        'room_code', v_clean_code,
        'player_a_id', v_room.host_user_id,
        'player_b_id', p_user_id,
        'question_id', v_question_id,
        'first_player_id', v_first_player,
        'current_player_id', v_first_player,
        'status', 'picking',
        'turn_deadline_at', (NOW() + INTERVAL '20 seconds')
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


-- 6. UPDATE REQUEST_REMATCH RPC WITH SELF-HEALING QUESTION FALLBACK
CREATE OR REPLACE FUNCTION public.request_rematch(
    p_match_id UUID,
    p_user_id UUID
)
RETURNS JSONB AS $$
DECLARE
    v_match RECORD;
    v_room RECORD;
    v_question_id TEXT;
    v_new_match_id UUID;
    v_first_player UUID;
BEGIN
    SELECT * INTO v_match FROM public.matches WHERE id = p_match_id;
    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Maç bulunamadı.');
    END IF;

    SELECT * INTO v_room FROM public.rooms WHERE id = v_match.room_id FOR UPDATE;

    IF v_user_id = v_room.host_user_id THEN
        UPDATE public.rooms SET rematch_host_ready = TRUE WHERE id = v_room.id;
    ELSIF v_user_id = v_room.guest_user_id THEN
        UPDATE public.rooms SET rematch_guest_ready = TRUE WHERE id = v_room.id;
    END IF;

    -- Refresh room
    SELECT * INTO v_room FROM public.rooms WHERE id = v_match.room_id;

    IF v_room.rematch_host_ready AND v_room.rematch_guest_ready THEN
        UPDATE public.rooms SET rematch_host_ready = FALSE, rematch_guest_ready = FALSE, status = 'in_match' WHERE id = v_room.id;

        SELECT id INTO v_question_id FROM public.questions ORDER BY RANDOM() LIMIT 1;
        IF v_question_id IS NULL THEN
            v_question_id := v_match.question_id;
        END IF;
        IF v_question_id IS NULL THEN
            INSERT INTO public.questions (
                id, question_key, title, competition, stat_type, target, pick_count, difficulty, description, eligible_competitions
            ) VALUES (
                'pl-goals-500',
                'pl-goals-500',
                '5 futbolcuyla 500 Premier League golüne mümkün olduğunca yaklaş',
                'Premier League',
                'goals',
                500,
                5,
                'Orta',
                '5 futbolcu seçerek Premier League''de toplam 500 gole mümkün olduğunca yaklaş.',
                ARRAY['Premier League']
            )
            ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title
            RETURNING id INTO v_question_id;
        END IF;

        IF random() > 0.5 THEN
            v_first_player := v_room.host_user_id;
        ELSE
            v_first_player := v_room.guest_user_id;
        END IF;

        INSERT INTO public.matches (
            room_id,
            player_a_id,
            player_b_id,
            question_id,
            first_player_id,
            current_player_id,
            status,
            current_pick_number,
            turn_started_at,
            turn_deadline_at
        ) VALUES (
            v_room.id,
            v_room.host_user_id,
            v_room.guest_user_id,
            v_question_id,
            v_first_player,
            v_first_player,
            'picking',
            1,
            NOW(),
            NOW() + INTERVAL '20 seconds'
        ) RETURNING id INTO v_new_match_id;

        RETURN jsonb_build_object('success', true, 'rematch_ready', true, 'new_match_id', v_new_match_id);
    ELSE
        RETURN jsonb_build_object('success', true, 'rematch_ready', false, 'message', 'Rakibin tekrar oyna yanıtı bekleniyor...');
    END IF;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


-- 7. UPDATE SUBMIT_PICK RPC TO ENSURE NO FOOTBALLER FOREIGN KEY ISSUE
CREATE OR REPLACE FUNCTION public.submit_pick(
    p_match_id UUID,
    p_user_id UUID,
    p_footballer_id TEXT
)
RETURNS JSONB AS $$
DECLARE
    v_match RECORD;
    v_question RECORD;
    v_stat_val INT := 0;
    v_player_pick_count INT;
    v_total_picks INT;
    v_next_player UUID;
    v_existing_pick INT;
    v_total_a INT;
    v_total_b INT;
    v_diff_a INT;
    v_diff_b INT;
    v_winner TEXT;
BEGIN
    SELECT * INTO v_match FROM public.matches WHERE id = p_match_id FOR UPDATE;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Maç bulunamadı.');
    END IF;

    IF v_match.status != 'picking' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Maç seçim aşamasında değil.');
    END IF;

    IF v_match.current_player_id != p_user_id THEN
        RETURN jsonb_build_object('success', false, 'error', 'Şu anda sıra sizde değil.');
    END IF;

    SELECT COUNT(*) INTO v_existing_pick FROM public.match_picks 
    WHERE match_id = p_match_id AND footballer_id = p_footballer_id;

    IF v_existing_pick > 0 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Bu futbolcu bu maçta daha önce seçildi.');
    END IF;

    SELECT * INTO v_question FROM public.questions WHERE id = v_match.question_id;

    -- Fetch secret stat value from footballer_stats
    SELECT stat_value INTO v_stat_val FROM public.footballer_stats 
    WHERE footballer_id = p_footballer_id 
      AND competition = v_question.competition 
      AND stat_type = v_question.stat_type;

    IF v_stat_val IS NULL THEN
        IF v_question.competition = 'Kariyer' THEN
            SELECT COALESCE(SUM(stat_value), 0) INTO v_stat_val 
            FROM public.footballer_stats 
            WHERE footballer_id = p_footballer_id AND stat_type = v_question.stat_type;
        ELSE
            v_stat_val := 0;
        END IF;
    END IF;

    -- Ensure footballer row exists in footballers table to avoid foreign key failure
    IF p_footballer_id IS NOT NULL THEN
        INSERT INTO public.footballers (id, name, nationality, position, photo_url, club_name, eligible_competitions)
        VALUES (p_footballer_id, p_footballer_id, 'Bilinmiyor', 'Forvet', '', 'Bilinmiyor', ARRAY['Premier League', 'Kariyer'])
        ON CONFLICT (id) DO NOTHING;
    END IF;

    SELECT COUNT(*) + 1 INTO v_player_pick_count FROM public.match_picks 
    WHERE match_id = p_match_id AND player_id = p_user_id;

    INSERT INTO public.match_picks (
        match_id,
        player_id,
        pick_number,
        footballer_id,
        is_timeout,
        stat_value
    ) VALUES (
        p_match_id,
        p_user_id,
        v_player_pick_count,
        p_footballer_id,
        FALSE,
        v_stat_val
    );

    SELECT COUNT(*) INTO v_total_picks FROM public.match_picks WHERE match_id = p_match_id;

    IF v_total_picks >= (v_question.pick_count * 2) THEN
        SELECT COALESCE(SUM(stat_value), 0) INTO v_total_a FROM public.match_picks 
        WHERE match_id = p_match_id AND player_id = v_match.player_a_id;

        SELECT COALESCE(SUM(stat_value), 0) INTO v_total_b FROM public.match_picks 
        WHERE match_id = p_match_id AND player_id = v_match.player_b_id;

        v_diff_a := ABS(v_question.target - v_total_a);
        v_diff_b := ABS(v_question.target - v_total_b);

        IF v_diff_a < v_diff_b THEN
            v_winner := 'A';
        ELSIF v_diff_b < v_diff_a THEN
            v_winner := 'B';
        ELSE
            v_winner := 'draw';
        END IF;

        UPDATE public.matches SET
            status = 'reveal',
            winner_id = v_winner,
            player_a_total = v_total_a,
            player_b_total = v_total_b,
            finished_at = NOW()
        WHERE id = p_match_id;

        RETURN jsonb_build_object(
            'success', true,
            'status', 'reveal',
            'winner', v_winner,
            'player_a_total', v_total_a,
            'player_b_total', v_total_b
        );
    ELSE
        IF v_match.current_player_id = v_match.player_a_id THEN
            v_next_player := v_match.player_b_id;
        ELSE
            v_next_player := v_match.player_a_id;
        END IF;

        UPDATE public.matches SET
            current_player_id = v_next_player,
            current_pick_number = current_pick_number + 1,
            turn_started_at = NOW(),
            turn_deadline_at = NOW() + INTERVAL '15 seconds'
        WHERE id = p_match_id;

        RETURN jsonb_build_object(
            'success', true,
            'status', 'picking',
            'next_player_id', v_next_player,
            'turn_deadline_at', (NOW() + INTERVAL '15 seconds')
        );
    END IF;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;


-- 8. ENSURE PERMISSIONS ARE GRANTED
GRANT USAGE ON SCHEMA public TO anon, authenticated;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO anon, authenticated;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO anon, authenticated;
