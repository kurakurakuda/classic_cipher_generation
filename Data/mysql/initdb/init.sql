USE classic_cipher_generation_db;

DROP TABLE IF EXISTS cryptography;
DROP TABLE IF EXISTS rail_count;
DROP TABLE IF EXISTS shift_count;
DROP TABLE IF EXISTS key_phrase;

CREATE TABLE cryptography
(
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  plain VARCHAR(100) NOT NULL,
  crypto VARCHAR(100) NOT NULL,
  type VARCHAR(10) NOT NULL,
  index id_index (id),
  index plain_index (plain),
  index crypto_index (crypto)
);

CREATE TABLE rail_count
(
  id INT NOT NULL PRIMARY KEY,
  count INT NOT NULL,
  index id_index (id)
);

CREATE TABLE shift_count
(
  id INT NOT NULL PRIMARY KEY,
  shift INT NOT NULL,
  index id_index (id)
);

CREATE TABLE key_phrase
(
  id INT NOT NULL PRIMARY KEY,
  phrase VARCHAR(10) NOT NULL,
  index id_index (id)
);
