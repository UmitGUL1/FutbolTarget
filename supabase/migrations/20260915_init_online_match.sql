-- ==============================================================================
-- FUTBOL TARGET: ONLINE MULTIPLAYER "ARKADAŞLA OYNA" (SUPABASE MIGRATION)
-- ==============================================================================

-- 1. PROFILES TABLE
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY,
    username TEXT NOT NULL,
    rank TEXT NOT NULL DEFAULT 'Gold',
    rank_points INT NOT NULL DEFAULT 3,
    wins INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0,
    draws INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 2. QUESTIONS TABLE (Curated question pool)
CREATE TABLE IF NOT EXISTS public.questions (
    id TEXT PRIMARY KEY,
    question_key TEXT,
    title TEXT NOT NULL,
    competition TEXT NOT NULL,
    stat_type TEXT NOT NULL,
    target INT NOT NULL,
    pick_count INT NOT NULL DEFAULT 5,
    difficulty TEXT NOT NULL DEFAULT 'Orta',
    description TEXT NOT NULL,
    eligible_competitions TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. FOOTBALLERS TABLE
CREATE TABLE IF NOT EXISTS public.footballers (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    nationality TEXT NOT NULL,
    position TEXT NOT NULL,
    photo_url TEXT NOT NULL DEFAULT '',
    club_name TEXT NOT NULL,
    eligible_competitions TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. FOOTBALLER STATS TABLE (Secret / hidden server stats)
CREATE TABLE IF NOT EXISTS public.footballer_stats (
    id BIGSERIAL PRIMARY KEY,
    footballer_id TEXT NOT NULL REFERENCES public.footballers(id) ON DELETE CASCADE,
    competition TEXT NOT NULL,
    stat_type TEXT NOT NULL,
    stat_value INT NOT NULL DEFAULT 0,
    UNIQUE (footballer_id, competition, stat_type)
);

-- 5. ROOMS TABLE (6-character code)
CREATE TABLE IF NOT EXISTS public.rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_code TEXT UNIQUE NOT NULL,
    host_user_id UUID NOT NULL,
    guest_user_id UUID,
    status TEXT NOT NULL DEFAULT 'waiting' CHECK (status IN ('waiting', 'ready', 'in_match', 'finished', 'cancelled')),
    active_match_id UUID,
    rematch_host_ready BOOLEAN NOT NULL DEFAULT FALSE,
    rematch_guest_ready BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() + INTERVAL '2 hours')
);

ALTER TABLE public.rooms ADD COLUMN IF NOT EXISTS active_match_id UUID;
CREATE INDEX IF NOT EXISTS idx_rooms_code ON public.rooms (room_code);
CREATE INDEX IF NOT EXISTS idx_rooms_active_match ON public.rooms (active_match_id);

-- 6. MATCHES TABLE (Server authoritative match state)
CREATE TABLE IF NOT EXISTS public.matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL REFERENCES public.rooms(id) ON DELETE CASCADE,
    player_a_id UUID NOT NULL,
    player_b_id UUID NOT NULL,
    question_id TEXT NOT NULL REFERENCES public.questions(id),
    first_player_id UUID NOT NULL,
    current_player_id UUID NOT NULL,
    status TEXT NOT NULL DEFAULT 'picking' CHECK (status IN ('waiting', 'picking', 'reveal', 'finished', 'cancelled')),
    current_pick_number INT NOT NULL DEFAULT 1,
    turn_started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    turn_deadline_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() + INTERVAL '20 seconds'),
    winner_id TEXT CHECK (winner_id IN ('A', 'B', 'draw') OR winner_id IS NULL),
    player_a_total INT NOT NULL DEFAULT 0,
    player_b_total INT NOT NULL DEFAULT 0,
    finish_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    finished_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_matches_room ON public.matches (room_id);

-- 7. MATCH PICKS TABLE
CREATE TABLE IF NOT EXISTS public.match_picks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id UUID NOT NULL REFERENCES public.matches(id) ON DELETE CASCADE,
    player_id UUID NOT NULL,
    pick_number INT NOT NULL,
    footballer_id TEXT REFERENCES public.footballers(id),
    is_timeout BOOLEAN NOT NULL DEFAULT FALSE,
    stat_value INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Ensure a footballer is picked at most once per match
CREATE UNIQUE INDEX IF NOT EXISTS idx_match_unique_footballer 
ON public.match_picks (match_id, footballer_id) 
WHERE footballer_id IS NOT NULL;

-- 8. HELPER FUNCTION: GENERATE 6-CHAR CLEAN ROOM CODE
CREATE OR REPLACE FUNCTION public.generate_room_code() 
RETURNS TEXT AS $$
DECLARE
    -- Excludes confusing characters (0, O, 1, I)
    chars CONSTANT TEXT := 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    result TEXT := '';
    i INT;
    collision_check INT;
BEGIN
    LOOP
        result := '';
        FOR i IN 1..6 LOOP
            result := result || substr(chars, floor(random() * length(chars) + 1)::INT, 1);
        END LOOP;

        SELECT COUNT(*) INTO collision_check FROM public.rooms WHERE room_code = result AND status != 'finished';
        EXIT WHEN collision_check = 0;
    END LOOP;
    RETURN result;
END;
$$ LANGUAGE plpgsql VOLATILE;

-- 9. RPC: CREATE ROOM
CREATE OR REPLACE FUNCTION public.create_room(
    p_user_id UUID,
    p_username TEXT
)
RETURNS JSONB AS $$
DECLARE
    v_code TEXT;
    v_room_id UUID;
BEGIN
    -- Upsert profile
    INSERT INTO public.profiles (id, username)
    VALUES (p_user_id, COALESCE(p_username, 'Oyuncu-' || substr(p_user_id::text, 1, 4)))
    ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username, updated_at = NOW();

    -- Generate safe room code
    v_code := public.generate_room_code();

    -- Insert room
    INSERT INTO public.rooms (room_code, host_user_id, status)
    VALUES (v_code, p_user_id, 'waiting')
    RETURNING id INTO v_room_id;

    RETURN jsonb_build_object(
        'success', true,
        'room_id', v_room_id,
        'room_code', v_code,
        'status', 'waiting'
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 10. RPC: JOIN ROOM
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

    -- Handle collision gracefully if testing with same user ID (e.g. shared device/session/backup)
    IF v_room.host_user_id = p_user_id THEN
        p_user_id := gen_random_uuid();
    END IF;

    -- Upsert guest profile
    INSERT INTO public.profiles (id, username)
    VALUES (p_user_id, COALESCE(p_username, 'Oyuncu-' || substr(p_user_id::text, 1, 4)))
    ON CONFLICT (id) DO UPDATE SET username = EXCLUDED.username, updated_at = NOW();

    -- Select random question from pool
    SELECT id INTO v_question_id FROM public.questions ORDER BY RANDOM() LIMIT 1;
    IF v_question_id IS NULL THEN
        -- Self-healing fallback: insert default question so foreign key constraint is guaranteed
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

    -- Create server authoritative match FIRST
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

    -- Update room atomically with guest_user_id, in_match status, AND active_match_id
    -- This guarantees Realtime payload delivers active_match_id to the host client!
    UPDATE public.rooms 
    SET guest_user_id = p_user_id, status = 'in_match', active_match_id = v_match_id
    WHERE id = v_room.id;

    RETURN jsonb_build_object(
        'success', true,
        'match_id', v_match_id,
        'room_id', v_room.id,
        'room_code', v_clean_code,
        'host_user_id', v_room.host_user_id,
        'guest_user_id', p_user_id,
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

-- 11. RPC: SUBMIT PICK (Server Authoritative)
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
    -- Lock match row
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

    -- Check duplicate pick
    SELECT COUNT(*) INTO v_existing_pick FROM public.match_picks 
    WHERE match_id = p_match_id AND footballer_id = p_footballer_id;

    IF v_existing_pick > 0 THEN
        RETURN jsonb_build_object('success', false, 'error', 'Bu futbolcu bu maçta daha önce seçildi.');
    END IF;

    -- Get question
    SELECT * INTO v_question FROM public.questions WHERE id = v_match.question_id;

    -- Fetch secret stat value from footballer_stats
    SELECT stat_value INTO v_stat_val FROM public.footballer_stats 
    WHERE footballer_id = p_footballer_id 
      AND competition = v_question.competition 
      AND stat_type = v_question.stat_type;

    IF v_stat_val IS NULL THEN
        -- Check career aggregation if competition is Kariyer
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

    -- Calculate pick number for this player
    SELECT COUNT(*) + 1 INTO v_player_pick_count FROM public.match_picks 
    WHERE match_id = p_match_id AND player_id = p_user_id;

    -- Insert pick
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

    -- Check total picks so far
    SELECT COUNT(*) INTO v_total_picks FROM public.match_picks WHERE match_id = p_match_id;

    -- 5 picks each = 10 picks total
    IF v_total_picks >= (v_question.pick_count * 2) THEN
        -- Calculate totals
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

        -- Transition to reveal
        UPDATE public.matches SET
            status = 'reveal',
            winner_id = v_winner,
            player_a_total = v_total_a,
            player_b_total = v_total_b,
            finished_at = NOW()
        WHERE id = p_match_id;

        -- Update player profiles
        IF v_winner = 'A' THEN
            UPDATE public.profiles SET wins = wins + 1 WHERE id = v_match.player_a_id;
            UPDATE public.profiles SET losses = losses + 1 WHERE id = v_match.player_b_id;
        ELSIF v_winner = 'B' THEN
            UPDATE public.profiles SET wins = wins + 1 WHERE id = v_match.player_b_id;
            UPDATE public.profiles SET losses = losses + 1 WHERE id = v_match.player_a_id;
        ELSE
            UPDATE public.profiles SET draws = draws + 1 WHERE id IN (v_match.player_a_id, v_match.player_b_id);
        END IF;

        RETURN jsonb_build_object(
            'success', true,
            'status', 'reveal',
            'winner', v_winner,
            'total_a', v_total_a,
            'total_b', v_total_b
        );
    ELSE
        -- Switch to other player
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

-- 12. RPC: HANDLE TIMEOUT
CREATE OR REPLACE FUNCTION public.handle_turn_timeout(
    p_match_id UUID
)
RETURNS JSONB AS $$
DECLARE
    v_match RECORD;
    v_question RECORD;
    v_player_pick_count INT;
    v_total_picks INT;
    v_next_player UUID;
    v_total_a INT;
    v_total_b INT;
    v_diff_a INT;
    v_diff_b INT;
    v_winner TEXT;
BEGIN
    SELECT * INTO v_match FROM public.matches WHERE id = p_match_id FOR UPDATE;

    IF NOT FOUND OR v_match.status != 'picking' THEN
        RETURN jsonb_build_object('success', false, 'error', 'Maç seçimde değil.');
    END IF;

    -- Ensure deadline passed
    IF NOW() < v_match.turn_deadline_at THEN
        RETURN jsonb_build_object('success', false, 'error', 'Süre henüz dolmadı.');
    END IF;

    SELECT * INTO v_question FROM public.questions WHERE id = v_match.question_id;

    SELECT COUNT(*) + 1 INTO v_player_pick_count FROM public.match_picks 
    WHERE match_id = p_match_id AND player_id = v_match.current_player_id;

    -- Insert timeout pick (0 points)
    INSERT INTO public.match_picks (
        match_id,
        player_id,
        pick_number,
        footballer_id,
        is_timeout,
        stat_value
    ) VALUES (
        p_match_id,
        v_match.current_player_id,
        v_player_pick_count,
        NULL,
        TRUE,
        0
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

        RETURN jsonb_build_object('success', true, 'status', 'reveal', 'winner', v_winner);
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

        RETURN jsonb_build_object('success', true, 'status', 'picking', 'next_player_id', v_next_player);
    END IF;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 13. RPC: GET MATCH STATE (STAT PRIVACY ENFORCEMENT & FALLBACK BY MATCH OR ROOM ID)
CREATE OR REPLACE FUNCTION public.get_match_state(
    p_match_id UUID,
    p_user_id UUID
)
RETURNS JSONB AS $$
DECLARE
    v_match RECORD;
    v_question RECORD;
    v_picks JSONB;
    v_is_revealed BOOLEAN;
BEGIN
    -- Bulletproof: match can be looked up either by matches.id OR rooms.id
    SELECT * INTO v_match FROM public.matches 
    WHERE id = p_match_id OR room_id = p_match_id
    ORDER BY created_at DESC LIMIT 1;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Maç bulunamadı.');
    END IF;

    SELECT * INTO v_question FROM public.questions WHERE id = v_match.question_id;

    v_is_revealed := (v_match.status IN ('reveal', 'finished'));

    -- Protect hidden stats: only return real stat_value during reveal or finished!
    SELECT jsonb_agg(
        jsonb_build_object(
            'id', p.id,
            'player_id', p.player_id,
            'pick_number', p.pick_number,
            'footballer_id', p.footballer_id,
            'is_timeout', p.is_timeout,
            'stat_value', CASE WHEN v_is_revealed THEN p.stat_value ELSE 0 END,
            'created_at', p.created_at
        ) ORDER BY p.created_at ASC
    ) INTO v_picks FROM public.match_picks p WHERE p.match_id = p_match_id;

    RETURN jsonb_build_object(
        'success', true,
        'match_id', v_match.id,
        'room_id', v_match.room_id,
        'player_a_id', v_match.player_a_id,
        'player_b_id', v_match.player_b_id,
        'current_player_id', v_match.current_player_id,
        'status', v_match.status,
        'current_pick_number', v_match.current_pick_number,
        'turn_deadline_at', v_match.turn_deadline_at,
        'winner_id', v_match.winner_id,
        'player_a_total', CASE WHEN v_is_revealed THEN v_match.player_a_total ELSE 0 END,
        'player_b_total', CASE WHEN v_is_revealed THEN v_match.player_b_total ELSE 0 END,
        'question', row_to_json(v_question),
        'picks', COALESCE(v_picks, '[]'::jsonb)
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 13b. RPC: GET ROOM STATE (POLLING & SYNC HELPER)
CREATE OR REPLACE FUNCTION public.get_room_state(
    p_room_id UUID DEFAULT NULL,
    p_room_code TEXT DEFAULT NULL
)
RETURNS JSONB AS $$
DECLARE
    v_room RECORD;
BEGIN
    IF p_room_id IS NOT NULL THEN
        SELECT * INTO v_room FROM public.rooms WHERE id = p_room_id;
    ELSIF p_room_code IS NOT NULL THEN
        SELECT * INTO v_room FROM public.rooms WHERE room_code = UPPER(TRIM(p_room_code));
    ELSE
        RETURN jsonb_build_object('success', false, 'error', 'Oda bilgisi eksik.');
    END IF;

    IF NOT FOUND THEN
        RETURN jsonb_build_object('success', false, 'error', 'Oda bulunamadı.');
    END IF;

    RETURN jsonb_build_object(
        'success', true,
        'room_id', v_room.id,
        'room_code', v_room.room_code,
        'status', v_room.status,
        'host_user_id', v_room.host_user_id,
        'guest_user_id', v_room.guest_user_id,
        'active_match_id', v_room.active_match_id
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- 14. RPC: REMATCH REQUEST
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
        -- Both ready: reset flags, create new match
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

-- 15. ENABLE ROW LEVEL SECURITY
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.questions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.footballers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.footballer_stats ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.rooms ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.matches ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.match_picks ENABLE ROW LEVEL SECURITY;

-- 16. RLS POLICIES
CREATE POLICY "Public read profiles" ON public.profiles FOR SELECT USING (true);
CREATE POLICY "Public read questions" ON public.questions FOR SELECT USING (true);
CREATE POLICY "Public read footballers" ON public.footballers FOR SELECT USING (true);
CREATE POLICY "Deny direct public read of footballer_stats" ON public.footballer_stats FOR SELECT USING (false);

CREATE POLICY "Public read active rooms" ON public.rooms FOR SELECT USING (true);
CREATE POLICY "Public read active matches" ON public.matches FOR SELECT USING (true);
CREATE POLICY "Public read match_picks" ON public.match_picks FOR SELECT USING (true);

-- 17. ENABLE REALTIME
ALTER PUBLICATION supabase_realtime ADD TABLE public.rooms;
ALTER PUBLICATION supabase_realtime ADD TABLE public.matches;
ALTER PUBLICATION supabase_realtime ADD TABLE public.match_picks;

-- 18. GRANT PERMISSIONS TO ANON AND AUTHENTICATED ROLES
GRANT USAGE ON SCHEMA public TO anon, authenticated;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO anon, authenticated;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO anon, authenticated;

-- 19. IDEMPOTENT SEED DATA (QUESTIONS, FOOTBALLERS, SECRET STATS)
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

