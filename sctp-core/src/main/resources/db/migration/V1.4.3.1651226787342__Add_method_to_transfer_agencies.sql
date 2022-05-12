-- Add the transfer method field to transfer agencies table
ALTER TABLE transfer_agencies ADD COLUMN `transfer_method` VARCHAR(50) NOT NULL AFTER `name`;
