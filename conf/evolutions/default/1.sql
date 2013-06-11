# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table choice (
  id                        bigint not null,
  libelle                   varchar(255),
  questionId                bigint,
  status                    varchar(255),
  constraint pk_choice primary key (id))
;

create table question (
  id                        bigint not null,
  text                      varchar(255),
  domain_id                 bigint,
  constraint pk_question primary key (id))
;

create sequence choice_seq;

create sequence question_seq;

alter table choice add constraint fk_choice_questionRef_1 foreign key (questionId) references question (id) on delete restrict on update restrict;
create index ix_choice_questionRef_1 on choice (questionId);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists choice;

drop table if exists question;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists choice_seq;

drop sequence if exists question_seq;

