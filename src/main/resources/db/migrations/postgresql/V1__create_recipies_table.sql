CREATE SCHEMA IF NOT EXISTS dbo;

CREATE TABLE dbo.recipes
(
   id         UUID                                      NOT NULL,
   title      VARCHAR(200)                              NOT NULL,
   descr      VARCHAR(500)                              NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
   updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
   CONSTRAINT pk_bookmarks PRIMARY KEY (id)
);