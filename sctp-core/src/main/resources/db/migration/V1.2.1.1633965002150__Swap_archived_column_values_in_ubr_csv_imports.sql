-- validation_status was initially created as VARCHAR, so we'll use the enum name instead of its ordinal

-- See: org.cga.sctp.targeting.importation.UbrHouseholdImport#ValidationStatus

update ubr_csv_imports set validation_status = 'Valid' where validation_status = '0';
update ubr_csv_imports set validation_status = 'Error' where validation_status = '1';