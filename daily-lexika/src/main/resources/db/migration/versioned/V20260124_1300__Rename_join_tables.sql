ALTER TABLE reviews_list_of_words RENAME TO reviews_words;
ALTER TABLE word_data_list_of_word_packs RENAME TO word_data_word_packs;
ALTER TABLE reviews_words RENAME COLUMN list_of_words_id TO word_id;
ALTER TABLE reviews_words RENAME COLUMN reviews_id TO review_id;
