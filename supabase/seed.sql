-- ==============================================================================
-- FUTBOL TARGET: SEED DATA (QUESTIONS, FOOTBALLERS & SECRET STATS)
-- ==============================================================================

-- 1. QUESTIONS
ALTER TABLE public.questions ADD COLUMN IF NOT EXISTS question_key TEXT;

INSERT INTO public.questions (id, question_key, title, competition, stat_type, target, pick_count, difficulty, description, eligible_competitions)
VALUES
('pl-goals-500', 'pl-goals-500', '5 futbolcuyla 500 Premier League golüne mümkün olduğunca yaklaş', 'Premier League', 'goals', 500, 5, 'Orta', '5 futbolcu seçerek Premier League''de toplam 500 gole mümkün olduğunca yaklaş.', ARRAY['Premier League']),
('curated-premier-league-500', 'curated-premier-league-500', 'Premier League Gol Avcıları', 'Premier League', 'goals', 500, 5, 'Orta', '5 futbolcu seçerek Premier League''de toplam 500 gole en yakın skora ulaş.', ARRAY['Premier League']),
('curated-career-1500', 'curated-career-1500', 'Kariyer Gol Kralları', 'Kariyer', 'goals', 1500, 5, 'Zor', '5 efsane futbolcu ile kariyer boyunca toplam 1500 gole en yakın toplamı elde et.', ARRAY['Kariyer', '5 Büyük Lig']),
('curated-la-liga-600', 'curated-la-liga-600', 'La Liga Efsaneleri', 'La Liga', 'goals', 600, 5, 'Orta', '5 futbolcu seçerek İspanya La Liga''da 600 gole en çok yaklaşan kazanır.', ARRAY['La Liga']),
('curated-champions-league-200', 'curated-champions-league-200', 'Şampiyonlar Ligi Golcüleri', 'Champions League', 'goals', 200, 5, 'Zor', 'UEFA Şampiyonlar Ligi''nde 5 seçimle 200 gol hedefine ulaş.', ARRAY['Champions League']),
('curated-top-5-leagues-800', 'curated-top-5-leagues-800', '5 Büyük Lig Gol Düellosu', '5 Büyük Lig', 'goals', 800, 5, 'Orta', 'Avrupa''nın 5 büyük liginde atılan toplam 800 gole en yakın 5 futbolcuyu seç.', ARRAY['5 Büyük Lig']),
('curated-pl-assists-250', 'curated-pl-assists-250', 'Premier League Asist Ustaları', 'Premier League', 'assists', 250, 5, 'Zor', 'Premier League''de toplam 250 asiste en yakın kadroyu kur.', ARRAY['Premier League'])
ON CONFLICT (id) DO UPDATE SET
    question_key = EXCLUDED.question_key,
    title = EXCLUDED.title,
    target = EXCLUDED.target,
    description = EXCLUDED.description;

-- 2. FOOTBALLERS (PUBLIC INFO: NAME, NATIONALITY, POSITION, CLUB)
INSERT INTO public.footballers (id, name, nationality, position, photo_url, club_name, eligible_competitions)
VALUES
('lionel-messi', 'Lionel Messi', 'Arjantin', 'RW / ST', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=150', 'Inter Miami', ARRAY['Kariyer', 'La Liga', 'Ligue 1', 'Champions League', '5 Büyük Lig']),
('cristiano-ronaldo', 'Cristiano Ronaldo', 'Portekiz', 'ST / LW', 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=150', 'Al-Nassr', ARRAY['Kariyer', 'Premier League', 'La Liga', 'Serie A', 'Champions League', '5 Büyük Lig']),
('erling-haaland', 'Erling Haaland', 'Norveç', 'ST', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6?w=150', 'Manchester City', ARRAY['Kariyer', 'Bundesliga', 'Premier League', 'Champions League', '5 Büyük Lig']),
('kylian-mbappe', 'Kylian Mbappé', 'Fransa', 'ST / LW', 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=150', 'Real Madrid', ARRAY['Kariyer', 'Ligue 1', 'La Liga', 'Champions League', '5 Büyük Lig']),
('harry-kane', 'Harry Kane', 'İngiltere', 'ST', 'https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=150', 'Bayern Munich', ARRAY['Kariyer', 'Premier League', 'Bundesliga', 'Champions League', '5 Büyük Lig']),
('robert-lewandowski', 'Robert Lewandowski', 'Polonya', 'ST', 'https://images.unsplash.com/photo-1489944440615-453fc2b6a9a9?w=150', 'Barcelona', ARRAY['Kariyer', 'Bundesliga', 'La Liga', 'Champions League', '5 Büyük Lig']),
('mohamed-salah', 'Mohamed Salah', 'Mısır', 'RW', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=150', 'Liverpool', ARRAY['Kariyer', 'Premier League', 'Serie A', 'Champions League', '5 Büyük Lig']),
('kevin-de-bruyne', 'Kevin De Bruyne', 'Belçika', 'CAM', 'https://images.unsplash.com/photo-1560272564-c83b66b1ad12?w=150', 'Manchester City', ARRAY['Kariyer', 'Premier League', 'Bundesliga', 'Champions League', '5 Büyük Lig']),
('karim-benzema', 'Karim Benzema', 'Fransa', 'ST', 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=150', 'Al-Ittihad', ARRAY['Kariyer', 'La Liga', 'Ligue 1', 'Champions League', '5 Büyük Lig']),
('neymar-jr', 'Neymar Jr', 'Brezilya', 'LW / CAM', 'https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=150', 'Al-Hilal', ARRAY['Kariyer', 'La Liga', 'Ligue 1', 'Champions League', '5 Büyük Lig']),
('sergio-aguero', 'Sergio Agüero', 'Arjantin', 'ST', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=150', 'Emekli', ARRAY['Kariyer', 'Premier League', 'La Liga', 'Champions League', '5 Büyük Lig']),
('wayne-rooney', 'Wayne Rooney', 'İngiltere', 'ST / CAM', 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=150', 'Emekli', ARRAY['Kariyer', 'Premier League', 'Champions League', '5 Büyük Lig']),
('thierry-henry', 'Thierry Henry', 'Fransa', 'ST / LW', 'https://images.unsplash.com/photo-1489944440615-453fc2b6a9a9?w=150', 'Emekli', ARRAY['Kariyer', 'Premier League', 'La Liga', 'Ligue 1', 'Champions League', '5 Büyük Lig']),
('alan-shearer', 'Alan Shearer', 'İngiltere', 'ST', 'https://images.unsplash.com/photo-1560272564-c83b66b1ad12?w=150', 'Emekli', ARRAY['Kariyer', 'Premier League']),
('son-heung-min', 'Son Heung-min', 'Güney Kore', 'LW / ST', 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=150', 'Tottenham Hotspur', ARRAY['Kariyer', 'Premier League', 'Bundesliga', 'Champions League', '5 Büyük Lig']),
('sadio-mane', 'Sadio Mané', 'Senegal', 'LW / ST', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6?w=150', 'Al-Nassr', ARRAY['Kariyer', 'Premier League', 'Bundesliga', 'Champions League', '5 Büyük Lig']),
('raheem-sterling', 'Raheem Sterling', 'İngiltere', 'LW / RW', 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=150', 'Arsenal', ARRAY['Kariyer', 'Premier League', 'Champions League', '5 Büyük Lig']),
('romelu-lukaku', 'Romelu Lukaku', 'Belçika', 'ST', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=150', 'Napoli', ARRAY['Kariyer', 'Premier League', 'Serie A', 'Champions League', '5 Büyük Lig']),
('jamie-vardy', 'Jamie Vardy', 'İngiltere', 'ST', 'https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=150', 'Leicester City', ARRAY['Kariyer', 'Premier League']),
('luis-suarez', 'Luis Suárez', 'Uruguay', 'ST', 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=150', 'Inter Miami', ARRAY['Kariyer', 'Premier League', 'La Liga', 'Champions League', '5 Büyük Lig'])
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    nationality = EXCLUDED.nationality,
    position = EXCLUDED.position,
    club_name = EXCLUDED.club_name;

-- 3. FOOTBALLER SECRET STATS (HIDDEN DURING PICKING)
INSERT INTO public.footballer_stats (footballer_id, competition, stat_type, stat_value)
VALUES
-- Messi
('lionel-messi', 'La Liga', 'goals', 474),
('lionel-messi', 'Ligue 1', 'goals', 22),
('lionel-messi', '5 Büyük Lig', 'goals', 496),
('lionel-messi', 'Champions League', 'goals', 129),
('lionel-messi', 'Kariyer', 'goals', 838),
('lionel-messi', 'La Liga', 'assists', 192),
('lionel-messi', 'Kariyer', 'assists', 374),

-- Cristiano Ronaldo
('cristiano-ronaldo', 'Premier League', 'goals', 103),
('cristiano-ronaldo', 'La Liga', 'goals', 311),
('cristiano-ronaldo', 'Serie A', 'goals', 81),
('cristiano-ronaldo', '5 Büyük Lig', 'goals', 495),
('cristiano-ronaldo', 'Champions League', 'goals', 140),
('cristiano-ronaldo', 'Kariyer', 'goals', 895),

-- Erling Haaland
('erling-haaland', 'Bundesliga', 'goals', 62),
('erling-haaland', 'Premier League', 'goals', 75),
('erling-haaland', '5 Büyük Lig', 'goals', 137),
('erling-haaland', 'Champions League', 'goals', 44),
('erling-haaland', 'Kariyer', 'goals', 260),

-- Kylian Mbappe
('kylian-mbappe', 'Ligue 1', 'goals', 191),
('kylian-mbappe', 'La Liga', 'goals', 10),
('kylian-mbappe', '5 Büyük Lig', 'goals', 201),
('kylian-mbappe', 'Champions League', 'goals', 49),
('kylian-mbappe', 'Kariyer', 'goals', 290),

-- Harry Kane
('harry-kane', 'Premier League', 'goals', 213),
('harry-kane', 'Bundesliga', 'goals', 45),
('harry-kane', '5 Büyük Lig', 'goals', 258),
('harry-kane', 'Champions League', 'goals', 33),
('harry-kane', 'Kariyer', 'goals', 360),
('harry-kane', 'Premier League', 'assists', 46),

-- Robert Lewandowski
('robert-lewandowski', 'Bundesliga', 'goals', 312),
('robert-lewandowski', 'La Liga', 'goals', 50),
('robert-lewandowski', '5 Büyük Lig', 'goals', 362),
('robert-lewandowski', 'Champions League', 'goals', 94),
('robert-lewandowski', 'Kariyer', 'goals', 645),

-- Mohamed Salah
('mohamed-salah', 'Premier League', 'goals', 160),
('mohamed-salah', 'Serie A', 'goals', 35),
('mohamed-salah', '5 Büyük Lig', 'goals', 195),
('mohamed-salah', 'Champions League', 'goals', 44),
('mohamed-salah', 'Kariyer', 'goals', 320),
('mohamed-salah', 'Premier League', 'assists', 74),

-- Kevin De Bruyne
('kevin-de-bruyne', 'Premier League', 'goals', 68),
('kevin-de-bruyne', 'Bundesliga', 'goals', 13),
('kevin-de-bruyne', '5 Büyük Lig', 'goals', 81),
('kevin-de-bruyne', 'Premier League', 'assists', 113),
('kevin-de-bruyne', 'Kariyer', 'assists', 255),

-- Karim Benzema
('karim-benzema', 'La Liga', 'goals', 238),
('karim-benzema', 'Ligue 1', 'goals', 43),
('karim-benzema', '5 Büyük Lig', 'goals', 281),
('karim-benzema', 'Champions League', 'goals', 90),
('karim-benzema', 'Kariyer', 'goals', 472),

-- Neymar Jr
('neymar-jr', 'La Liga', 'goals', 68),
('neymar-jr', 'Ligue 1', 'goals', 82),
('neymar-jr', '5 Büyük Lig', 'goals', 150),
('neymar-jr', 'Champions League', 'goals', 43),
('neymar-jr', 'Kariyer', 'goals', 360),

-- Sergio Aguero
('sergio-aguero', 'Premier League', 'goals', 184),
('sergio-aguero', 'La Liga', 'goals', 75),
('sergio-aguero', '5 Büyük Lig', 'goals', 259),
('sergio-aguero', 'Kariyer', 'goals', 385),

-- Wayne Rooney
('wayne-rooney', 'Premier League', 'goals', 208),
('wayne-rooney', 'Premier League', 'assists', 103),
('wayne-rooney', 'Kariyer', 'goals', 313),

-- Thierry Henry
('thierry-henry', 'Premier League', 'goals', 175),
('thierry-henry', 'La Liga', 'goals', 35),
('thierry-henry', 'Premier League', 'assists', 74),
('thierry-henry', 'Kariyer', 'goals', 360),

-- Alan Shearer
('alan-shearer', 'Premier League', 'goals', 260),
('alan-shearer', 'Premier League', 'assists', 64),
('alan-shearer', 'Kariyer', 'goals', 379),

-- Son Heung-min
('son-heung-min', 'Premier League', 'goals', 123),
('son-heung-min', 'Bundesliga', 'goals', 41),
('son-heung-min', '5 Büyük Lig', 'goals', 164),
('son-heung-min', 'Kariyer', 'goals', 225),

-- Sadio Mane
('sadio-mane', 'Premier League', 'goals', 111),
('sadio-mane', 'Bundesliga', 'goals', 7),
('sadio-mane', '5 Büyük Lig', 'goals', 118),
('sadio-mane', 'Kariyer', 'goals', 215),

-- Raheem Sterling
('raheem-sterling', 'Premier League', 'goals', 123),
('raheem-sterling', 'Premier League', 'assists', 63),
('raheem-sterling', 'Kariyer', 'goals', 175),

-- Romelu Lukaku
('romelu-lukaku', 'Premier League', 'goals', 121),
('romelu-lukaku', 'Serie A', 'goals', 71),
('romelu-lukaku', '5 Büyük Lig', 'goals', 192),
('romelu-lukaku', 'Kariyer', 'goals', 310),

-- Jamie Vardy
('jamie-vardy', 'Premier League', 'goals', 140),
('jamie-vardy', 'Kariyer', 'goals', 195),

-- Luis Suarez
('luis-suarez', 'Premier League', 'goals', 69),
('luis-suarez', 'La Liga', 'goals', 178),
('luis-suarez', '5 Büyük Lig', 'goals', 247),
('luis-suarez', 'Kariyer', 'goals', 500)
ON CONFLICT (footballer_id, competition, stat_type) DO UPDATE SET
    stat_value = EXCLUDED.stat_value;
