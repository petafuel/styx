alter table tasks
    add class text not null default '';

alter table tokens alter column service type varchar(255) using service::varchar(255);

--

drop function create_task(uuid, text, varchar, text);

create or replace function create_task(id uuid, signature text, worker_type character varying, goal text,
                                       class text) returns void
    security definer
    language sql
as
$$
INSERT INTO tasks (id, signature, worker_type, queued, goal, created_at, class)
VALUES ($1, $2, $3, true, $4, now(), $5);
$$;

--

drop function get_interrupted_tasks();

create or replace function get_interrupted_tasks()
    returns TABLE(id uuid, signature text, goal text, worker_type character varying, class text)
    security definer
    language sql
as
$$
select id, signature, goal, worker_type, class
from tasks
where queued = false
  AND done = false
  AND done_at IS NULL
  AND final_failure_text IS NULL
  AND final_failure_code IS NULL;
$$;

drop function get_queued_tasks();

create or replace function get_queued_tasks()
    returns TABLE(id uuid, signature text, goal text, worker_type character varying, class text)
    security definer
    language sql
as
$$
select id, signature, goal, worker_type, class
from tasks
where queued = true
  AND done = false
  AND done_at IS NULL
  AND final_failure_text IS NULL
  AND final_failure_code IS NULL;
$$;

alter function get_queued_tasks() owner to dev_styx;

