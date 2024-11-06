FROM postgres:latest
LABEL authors="Zumin Li"

ADD schema.sql /docker-entrypoint-initdb.d/
