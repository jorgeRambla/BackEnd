-- noinspection SqlNoDataSourceInspectionForFile

alter table murcy_question alter column title type varchar(512) using title::varchar(512);
alter table murcy_quiz alter column title type varchar(512) using title::varchar(512);
UPDATE murcy_individual_answer
   SET options=(SELECT string_agg(options_id::text, ',') FROM murcy_individual_answer_options WHERE murcy_individual_answer.id=murcy_individual_answer_options.individual_answer_id)
    where id != -1;
drop table murcy_individual_answer_options;