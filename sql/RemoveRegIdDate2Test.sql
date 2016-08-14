
ALTER TABLE `ryspringoaut1`.`account` 
DROP COLUMN `registrationId`,
DROP COLUMN `registrationDate`;

DELETE FROM `ryspringoaut1`.`schema_version` WHERE `installed_rank`='2';

ALTER TABLE `ryspringoaut1`.`account` 
DROP COLUMN `registration_id`,
DROP COLUMN `registration_date`;
