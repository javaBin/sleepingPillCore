#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    create user sp with login password 'bingo';
    CREATE DATABASE sleepingpill with owner sp;
EOSQL