delete from reviews_list_of_words;

delete from reviews;

delete from word_data_list_of_word_packs
    where list_of_word_packs_name in ('Speakout (S) Unit 1', 'Speakout (E) Unit 1', 'Speakout (PI) Unit 1',
                                      'Speakout (PI) Unit 2', 'Speakout (I) Unit 1', 'Speakout (I) Unit 2',
                                      'Speakout (UI) Unit 1');

delete from word_packs
    where name in ('Speakout (S) Unit 1', 'Speakout (E) Unit 1', 'Speakout (PI) Unit 1',
                   'Speakout (PI) Unit 2', 'Speakout (I) Unit 1', 'Speakout (I) Unit 2',
                   'Speakout (UI) Unit 1');
