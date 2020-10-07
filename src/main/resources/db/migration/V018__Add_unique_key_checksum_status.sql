UPDATE letters let SET checksum = updateQuery.checksum FROM
(SELECT counter, checksum || counter checksum, status, id FROM (
SELECT row_number() over (PARTITION BY checksum, status) counter, checksum, status, id
FROM letters
WHERE status = 'Created') duplicate
WHERE duplicate.counter > 1) updateQuery
WHERE let.id = updateQuery.id;


CREATE UNIQUE INDEX checksum_status ON letters (checksum, status)
    WHERE status = 'Created';
