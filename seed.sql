INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'MONDAY', '05:00:00', '08:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'MONDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'MONDAY', '08:00:00', '15:00:00', 200000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'MONDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'MONDAY', '15:00:00', '17:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'MONDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'MONDAY', '17:00:00', '20:30:00', 350000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'MONDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'MONDAY', '20:30:00', '23:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'MONDAY' AND start_time = '20:30:00');


INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'TUESDAY', '05:00:00', '08:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'TUESDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'TUESDAY', '08:00:00', '15:00:00', 200000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'TUESDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'TUESDAY', '15:00:00', '17:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'TUESDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'TUESDAY', '17:00:00', '20:30:00', 350000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'TUESDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'TUESDAY', '20:30:00', '23:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'TUESDAY' AND start_time = '20:30:00');


INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'WEDNESDAY', '05:00:00', '08:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'WEDNESDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'WEDNESDAY', '08:00:00', '15:00:00', 200000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'WEDNESDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'WEDNESDAY', '15:00:00', '17:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'WEDNESDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'WEDNESDAY', '17:00:00', '20:30:00', 350000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'WEDNESDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'WEDNESDAY', '20:30:00', '23:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'WEDNESDAY' AND start_time = '20:30:00');


INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'THURSDAY', '05:00:00', '08:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'THURSDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'THURSDAY', '08:00:00', '15:00:00', 200000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'THURSDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'THURSDAY', '15:00:00', '17:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'THURSDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'THURSDAY', '17:00:00', '20:30:00', 350000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'THURSDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'THURSDAY', '20:30:00', '23:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'THURSDAY' AND start_time = '20:30:00');


INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'FRIDAY', '05:00:00', '08:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'FRIDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'FRIDAY', '08:00:00', '15:00:00', 200000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'FRIDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'FRIDAY', '15:00:00', '17:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'FRIDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'FRIDAY', '17:00:00', '20:30:00', 350000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'FRIDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'FRIDAY', '20:30:00', '23:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'FRIDAY' AND start_time = '20:30:00');


INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SATURDAY', '05:00:00', '08:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SATURDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SATURDAY', '08:00:00', '15:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SATURDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SATURDAY', '15:00:00', '17:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SATURDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SATURDAY', '17:00:00', '20:30:00', 400000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SATURDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SATURDAY', '20:30:00', '23:00:00', 350000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SATURDAY' AND start_time = '20:30:00');


INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SUNDAY', '05:00:00', '08:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SUNDAY' AND start_time = '05:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SUNDAY', '08:00:00', '15:00:00', 250000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SUNDAY' AND start_time = '08:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SUNDAY', '15:00:00', '17:00:00', 300000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SUNDAY' AND start_time = '15:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SUNDAY', '17:00:00', '20:30:00', 400000, true FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SUNDAY' AND start_time = '17:00:00');

INSERT INTO time_slots (pitch_id, day_of_week, start_time, end_time, price, is_golden_hour)
SELECT id, 'SUNDAY', '20:30:00', '23:00:00', 350000, false FROM pitches WHERE id NOT IN (SELECT pitch_id FROM time_slots WHERE day_of_week = 'SUNDAY' AND start_time = '20:30:00');
