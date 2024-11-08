FROM postgres:13.16
LABEL authors="Zumin Li"

ADD schema.sql /docker-entrypoint-initdb.d/
