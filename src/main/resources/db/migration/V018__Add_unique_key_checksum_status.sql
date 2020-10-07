update letters let set checksum = updateQuery.checksum from
(select counter, checksum || counter checksum, status, id from (
select row_number() over (partition by checksum, status) counter, checksum, status, id
from letters) duplicate
where duplicate.counter > 1) updateQuery
where let.id = updateQuery.id;


CREATE UNIQUE INDEX checksum_status ON letters (checksum, status)
    WHERE status = 'Created';
