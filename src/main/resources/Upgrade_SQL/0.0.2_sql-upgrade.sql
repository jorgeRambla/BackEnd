-- noinspection SqlNoDataSourceInspectionForFile

alter table murcy_question alter column title type varchar(512) using title::varchar(512);
alter table murcy_quiz alter column title type varchar(512) using title::varchar(512);
alter table murcy_individual_answer	add options varchar(5096);
UPDATE murcy_individual_answer
   SET options=(SELECT STRING_AGG(options_id, ',') FROM murcy_individual_answer_options WHERE murcy_individual_answer.id=murcy_individual_answer_options.individual_answer_id)
    where id != -1;
drop table murcy_individual_answer_options;