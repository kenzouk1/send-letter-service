update letters let set checksum = updateQuery.checksum from
(select counter, checksum || counter checksum, status, id from (
select row_number() over (partition by checksum, status) counter, checksum, status, id
from letters) duplicate
where duplicate.counter > 1) updateQuery
where let.id = updateQuery.id;

alter table letters
add column instances int
default 0;

alter table letters
add constraint checksum_status unique (checksum, status, instances)

