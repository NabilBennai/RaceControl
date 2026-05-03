UPDATE leagues
SET game_platform = 'OTHER'
WHERE game_platform IN ('PC', 'PS5', 'XBOX', 'CROSSPLAY');

UPDATE league_members
SET role = 'DRIVER'
WHERE role = 'MEMBER';
