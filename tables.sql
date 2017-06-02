CREATE TABLE public."time"
(
  "timestamp" timestamp NOT NULL,
  CONSTRAINT "time_pkey" PRIMARY KEY ("timestamp")
);
CREATE TABLE public."tweet"
(
  "tweetid" integer NOT NULL,
  "handle" character(40),
  "text" text,
  "anzretweets" integer,
  "anzlikes" integer,
  "originalautor" character(40),
  CONSTRAINT "tweet_pkey" PRIMARY KEY ("tweetid")
);
CREATE TABLE public."hashtag"
(
  "name" character(40) NOT NULL,
  "haeufigkeit_gesamt" integer,
  CONSTRAINT "hashtag_pkey" PRIMARY KEY ("name")
);
CREATE TABLE public."kommtvorin"
(
  "name" character(40) NOT NULL,
  "tweetid" integer NOT NULL,
  CONSTRAINT "kommtvorin_pkey" PRIMARY KEY ("name", "tweetid"),
  CONSTRAINT "kommtvorin_name_fkey" FOREIGN KEY ("name")
      REFERENCES public."hashtag" ("name") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "kommtvorin_tweetid_fkey" FOREIGN KEY ("tweetid")
      REFERENCES public."tweet" ("tweetid") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE public."getweetetam"
(
  "timestamp" timestamp,
  "tweetid" integer NOT NULL,
  CONSTRAINT "getweetetam_pkey" PRIMARY KEY ("tweetid"),
  CONSTRAINT "getweetetam_timestamp_fkey" FOREIGN KEY ("timestamp")
      REFERENCES public."time" ("timestamp") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "getweetetam_tweetid_fkey" FOREIGN KEY ("tweetid")
      REFERENCES public."tweet" ("tweetid") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE public."genutztam"
(
  "name" character(40) NOT NULL,
  "timestamp" timestamp NOT NULL,
  CONSTRAINT "genutztam_pkey" PRIMARY KEY ("name", "timestamp"),
  CONSTRAINT "genutztam_Name_fkey" FOREIGN KEY ("name")
      REFERENCES public."hashtag" ("name") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "genutztam_timestamp_fkey" FOREIGN KEY ("timestamp")
      REFERENCES public."time" ("timestamp") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE TABLE public."erscheintzsmmit"
(
  "name1" character(40) NOT NULL,
  "name2" character(40) NOT NULL,
  "haeufigkeitgesamt" integer,
  CONSTRAINT "erscheintzsmmit_pkey" PRIMARY KEY ("name1", "name2"),
  CONSTRAINT "erscheintzsmmit_Name1_fkey" FOREIGN KEY ("name1")
      REFERENCES public."hashtag" ("name") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "erscheintzsmmit_name2_fkey" FOREIGN KEY ("name2")
      REFERENCES public."hashtag" ("name") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
