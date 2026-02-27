CREATE TABLE sleep_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sleep_date DATE NOT NULL,
    bed_time TIME NOT NULL,
    wake_time TIME NOT NULL,
    total_time_in_bed BIGINT NOT NULL,
    morning_feeling VARCHAR(4) NOT NULL CHECK (morning_feeling IN ('BAD', 'OK', 'GOOD'))
);
