CREATE TABLE "users" (
	"id" serial NOT NULL,
	"telegram_user_id" integer NOT NULL,
	"user_email" TEXT NOT NULL,
	"user_password" TEXT NOT NULL,
	CONSTRAINT "users_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "word" (
	"id" serial NOT NULL,
	"group_id" integer NOT NULL,
	"word_original" TEXT NOT NULL,
	"word_translation" TEXT NOT NULL,
	"created_date" TIMESTAMP NOT NULL,
	CONSTRAINT "word_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "word_group" (
	"id" serial NOT NULL,
	"user_id" serial NOT NULL,
	"name" TEXT NOT NULL,
	"created_date" TIMESTAMP NOT NULL,
	"mixing_mode_id" integer NOT NULL,
	CONSTRAINT "word_group_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "group_repeat_date" (
	"id" serial NOT NULL,
	"group_id" integer NOT NULL,
	"date" TIMESTAMP NOT NULL,
	"is_actual" BOOLEAN NOT NULL,
	"is_daily_repeat" BOOLEAN NOT NULL,
	CONSTRAINT "group_repeat_date_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "word_info" (
	"id" serial NOT NULL,
	"word_id" integer NOT NULL,
	"repeat_id" integer NOT NULL,
	"is_done" BOOLEAN NOT NULL,
	CONSTRAINT "word_info_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);




ALTER TABLE "word" ADD CONSTRAINT "word_fk0" FOREIGN KEY ("group_id") REFERENCES "word_group"("id");

ALTER TABLE "word_group" ADD CONSTRAINT "word_group_fk0" FOREIGN KEY ("user_id") REFERENCES "users"("id");

ALTER TABLE "group_repeat_date" ADD CONSTRAINT "group_repeat_date_fk0" FOREIGN KEY ("group_id") REFERENCES "word_group"("id");

ALTER TABLE "word_info" ADD CONSTRAINT "word_info_fk0" FOREIGN KEY ("word_id") REFERENCES "word"("id");

ALTER TABLE "word_info" ADD CONSTRAINT "word_info_fk1" FOREIGN KEY ("repeat_id") REFERENCES "group_repeat_date"("id");