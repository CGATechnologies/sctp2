-- Initially set all records to false since previously before this, records were actually deleted
update ubr_csv_imports set archived = false where validation_status = 'Valid' or validation_status = '0';
update ubr_csv_imports set archived = true where validation_status = 'Error' or validation_status = '1';