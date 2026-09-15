# Futbol Target - Online "Arkadaşla Oyna" Modu Kurulum Kılavuzu (Supabase)

Bu kılavuz, Futbol Target projesindeki online oda kodlu 2 kişilik maç modunun Supabase ile nasıl çalıştırılacağını ve test edileceğini adım adım açıklar.

---

## 1. Supabase Projesi Oluşturma
1. [supabase.com](https://supabase.com) adresine gidin ve oturum açın.
2. **New Project** butonuna tıklayın.
3. Proje adını (örneğin: `futbol-target-db`) ve veritabanı şifrenizi belirleyin, ardından **Create new project** butonuna tıklayın.

---

## 2. API URL ve Anon Key Alma
1. Supabase Dashboard'da sol menüden **Project Settings** (Dişli çark) > **API** sekmesine gidin.
2. Burada yer alan şu değerleri kopyalayın:
   - **Project URL** (örnek: `https://xyzproject.supabase.co`)
   - **Project API Keys** altındaki `anon` / `public` key (JWT token).

---

## 3. Ortam Değişkenlerini (Environment Variables) Tanımlama
AI Studio Secrets panelinde veya projenin kök dizinindeki `.env` dosyasında şu değerleri tanımlayın:

```bash
SUPABASE_URL=https://xyzproject.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

*(Not: Proje hem `SUPABASE_URL` hem de `EXPO_PUBLIC_SUPABASE_URL` formatlarını destekler).*

---

## 4. SQL Migration ve Seed Dosyalarını Uygulama
1. Supabase Dashboard'da sol menüdeki **SQL Editor** sekmesine gidin.
2. **New Query** oluşturun.
3. `supabase/migrations/20260915_init_online_match.sql` dosyasının içeriğini kopyalayıp yapıştırın ve **Run** butonuna basın.
   - Bu işlem `profiles`, `questions`, `footballers`, `footballer_stats`, `rooms`, `matches`, `match_picks` tablolarını oluşturur.
   - `create_room`, `join_room`, `submit_pick`, `handle_turn_timeout`, `get_match_state`, `request_rematch` server-authoritative RPC fonksiyonlarını tanımlar.
   - RLS güvenlik politikalarını ve Realtime yayınını aktif hale getirir.
4. Yeni bir sorgu açarak `supabase/seed.sql` dosyasının içeriğini yapıştırın ve **Run** butonuna basın.
   - Bu işlem örnek soruları, futbolcuları ve gizli istatistikleri yükler.

---

## 5. Realtime Yayınının Kontrolü
Migration dosyası `ALTER PUBLICATION supabase_realtime ADD TABLE rooms, matches, match_picks;` komutunu otomatik çalıştırır.
Kontrol etmek için:
1. Supabase Dashboard > **Database** > **Replication** sekmesine gidin.
2. `supabase_realtime` altında `rooms`, `matches` ve `match_picks` tablolarının açık olduğunu doğrulayın.

---

## 6. Güvenlik ve Hile Koruması (Server Authoritative)
- **Stat Değerlerinin Gizliliği:** Seçim aşamasında (`picking`) futbolcuların ilgili istatistikleri client'a **kesinlikle iletilmez** (`get_match_state` RPC'si ve RLS koruması ile `stat_value = 0` maskelenir).
- **Zaman Aşımı ve Süre:** Turn süresi (`turn_deadline_at`) veritabanında saklanır. 1. seçim 20 sn, sonraki seçimler 15 sn'dir. Süre dolarsa `handle_turn_timeout` fonksiyonu ile 0 puan işlenir.
- **Çift Seçim Engeli:** `unique(match_id, footballer_id)` kısıtlaması sayesinde iki oyuncu aynı anda aynı futbolcuyu seçmeye çalışsa dahi race condition oluşamaz.
- **Kazanan Hesabı:** Reveal aşamasında server `player_a_total`, `player_b_total` ve `winner_id` değerlerini hesaplar. Client hesaplamasına güvenilmez.

---

## 7. İki Oyuncu ile Test Etme
1. **Oyuncu A:**
   - Ana menüde **"Arkadaşla Oyna (Online)"** kartına tıklar.
   - **"Oda Oluştur"** butonuna basar.
   - Ekranda 6 karakterlik oda kodu belirir (Örn: `F7K2XP`). "Arkadaşını bekliyorsun..." durumuna geçer.
2. **Oyuncu B:**
   - Farklı bir cihazda veya emülatörde **"Arkadaşla Oyna"** kartına tıklar.
   - **Oda Kodu** kutucuğuna `F7K2XP` yazar ve **"Odaya Katıl"** butonuna basar.
3. **Maç Başlangıcı:**
   - İki oyuncu da otomatik olarak maç ekranına geçer.
   - İlk başlayan oyuncu server tarafından rastgele seçilir.
   - Sırası gelen oyuncu futbolcu arar ve seçer. Seçim anında rakibin ekranında görünür (istatistik gizlidir).
   - 5'er seçim tamamlandığında Reveal animasyonu oynatılır ve server tarafından hesaplanan resmi kazanan sonuç ekranında görüntülenir.
