ALTER TABLE center ADD (IS_UUID_CONVERTED NUMBER(1) DEFAULT 0 NOT NULL);

-- APPS-6418 make update_Date same as create_date for records with null update_date
update uuid_hierarchy set update_date = create_date 
where update_date is null and create_date is not null;
commit;