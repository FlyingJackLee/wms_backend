-- 注意应当与src中的一致
DO
$$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM   pg_catalog.pg_database
            WHERE  datname = 'wms_test'
        ) THEN
            RAISE NOTICE 'Creating database: %', 'wms_test';
            EXECUTE 'CREATE DATABASE wms_test';
        ELSE
            RAISE NOTICE 'Database % already exists.', 'wms_test';
        END IF;
    END
$$;

-- create necessary data types
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname  = 'citext') THEN
        CREATE EXTENSION citext;
    END IF;
    -- phone number
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname  = 'cn_phone_number') THEN
        CREATE DOMAIN cn_phone_number AS varchar(11) CHECK ( value ~ '^1[3-9]\d{9}$');
    END IF;
    -- email
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname  = 'email') THEN
        CREATE DOMAIN email AS citext
            CHECK ( value ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$' );
    END IF;
END$$;