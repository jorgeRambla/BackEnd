-- noinspection SqlNoDataSourceInspectionForFile

/*Add owner_id column to auditable entity*/
alter table murcy_auditable_workflow add owner_id bigint;

UPDATE murcy_auditable_workflow
   SET owner_id=(SELECT applicant_id FROM murcy_editor_request WHERE murcy_auditable_workflow.id=murcy_editor_request.id)
    where id != -1;

alter table murcy_editor_request drop column applicant_id;

UPDATE murcy_auditable_workflow
   SET owner_id=(SELECT user_id FROM murcy_question WHERE murcy_auditable_workflow.id=murcy_question.id)
    where id != -1;

alter table murcy_question drop column user_id;

UPDATE murcy_auditable_workflow
   SET owner_id=(SELECT user_id FROM murcy_quiz WHERE murcy_auditable_workflow.id=murcy_quiz.id)
    where id != -1;

alter table murcy_quiz drop column user_id;
