DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS availability;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS events;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255),
                       full_name VARCHAR(255),
                       created_at TIMESTAMP
);

CREATE TABLE units (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       rooms_count INT,
                       type VARCHAR(50),
                       floor INT,
                       base_price DOUBLE,
                       final_price DOUBLE,
                       description CLOB,
                       available BOOLEAN,
                       user_id BIGINT NOT NULL,
                       CONSTRAINT fk_unit_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE availability (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              unit_id BIGINT NOT NULL,
                              start_date DATE,
                              end_date DATE,
                              CONSTRAINT fk_availability_unit FOREIGN KEY (unit_id) REFERENCES units(id)
);

CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          unit_id BIGINT NOT NULL,
                          start_date DATE,
                          end_date DATE,
                          status VARCHAR(50),
                          created_at TIMESTAMP,
                          CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT fk_booking_unit FOREIGN KEY (unit_id) REFERENCES units(id)
);

CREATE TABLE payments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          booking_id BIGINT NOT NULL,
                          amount DOUBLE,
                          status VARCHAR(50),
                          created_at TIMESTAMP,
                          is_refunded BOOLEAN,
                          CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE events (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        event_type VARCHAR(255),
                        description VARCHAR(255),
                        occurred_at TIMESTAMP
);
