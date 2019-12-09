-- Necessary SQL for Keep-Alive
-- change this:
-- SET ROLE $DB_USER
create table tasks
(
    id                 uuid    not null
        constraint tasks_pk
            primary key,
    signature          text    not null,
    worker_type        varchar(50),
    queued             boolean not null,
    goal               text    not null,
    done               boolean default false,
    retry              integer default 0,
    created_at         timestamp,
    done_at            timestamp,
    final_failure_text text,
    final_failure_code integer
);

create unique index tasks_signature_uindex
    on tasks (signature);

create unique index tasks_id_uindex
    on tasks (id);

--
-- create a task in the tasks table. Creted tasks are queued as default
--
create or replace function create_task(id uuid, signature text, worker_type character varying, goal text) returns void
    security definer
    language sql
as
$$
INSERT INTO tasks (id, signature, worker_type, queued, goal, created_at)
VALUES ($1, $2, $3, true, $4, now());
$$;

--
-- Select all tasks that were actively executed by a worker and were not finished successfully in the previous
-- application runtime
--
create or replace function get_interrupted_tasks()
    returns TABLE
            (
                id          uuid,
                signature   text,
                goal        text,
                worker_type character varying
            )
    security definer
    language sql
as
$$
select id, signature, goal, worker_type
from tasks
where queued = false
  AND done = false
  AND done_at IS NULL
  AND final_failure_text IS NULL
  AND final_failure_code IS NULL;
$$;

--
-- Select all tasks that were await execution within a Queue in the previous application runtime
--
create or replace function get_queued_tasks()
    returns TABLE
            (
                id          uuid,
                signature   text,
                goal        text,
                worker_type character varying
            )
    security definer
    language sql
as
$$
select id, signature, goal, worker_type
from tasks
where queued = true
  AND done = false
  AND done_at IS NULL
  AND final_failure_text IS NULL
  AND final_failure_code IS NULL;
$$;

--
-- Increment the execution counter for a task. Used to determine whether a task is still allowed to be executed by a
-- RetryFailureWorker
--
create or replace function increment_task_execution_counter(id uuid) returns integer
    security definer
    language sql
as
$$
UPDATE tasks
set retry = retry + 1
WHERE id = $1
RETURNING tasks.retry;
$$;

--
-- Set a task to be finally failed. The corresponding task will not be executed anymore by any worker
--
create or replace function update_task_finally_failed(id uuid, failure_code integer, failure_text text) returns void
    security definer
    language sql
as
$$
update tasks
set done               = false,
    queued             = false,
    final_failure_code = $2,
    final_failure_text = $3
where tasks.id = $1;
$$;

--
-- Change the current state of a task. States can be QUEUED, RUNNING or DONE
--
create or replace function update_task_state(id uuid, state character varying) returns void
    security definer
    language plpgsql
as
$$
BEGIN
    CASE $2
        WHEN 'QUEUED', 'queued' THEN update tasks set queued = true where tasks.id = $1;
        WHEN 'RUNNING', 'running' THEN update tasks set queued = false where tasks.id = $1;
        WHEN 'DONE', 'done' THEN update tasks set queued= false, done = true, done_at = now() where tasks.id = $1;
        END CASE;
END
$$;

--
-- Change the worker which should execute this task. Necessary to preserve this information for correct execution
-- after recovery
--
create or replace function update_task_worker(id uuid, worker_type character varying) returns void
    security definer
    language sql
as
$$
update tasks
set worker_type = $2
where tasks.id = $1;
$$;
