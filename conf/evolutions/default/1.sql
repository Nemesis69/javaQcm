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

create table qcm (
  id                        bigint not null,
  name                      varchar(255),
  category                  varchar(255),
  constraint uq_qcm_name unique (name),
  constraint pk_qcm primary key (id))
;

create table question (
  id                        bigint not null,
  text                      varchar(255),
  domain_id                 bigint,
  qcmId                     bigint,
  constraint pk_question primary key (id))
;

create table response (
  id                        bigint not null,
  choiceId                  bigint,
  questionId                bigint,
  userId                    bigint,
  constraint pk_response primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  surname                   varchar(255),
  is_admin                  boolean,
  constraint pk_user primary key (id))
;

create sequence choice_seq;

create sequence qcm_seq;

create sequence question_seq;

create sequence response_seq;

create sequence user_seq;

alter table choice add constraint fk_choice_questionRef_1 foreign key (questionId) references question (id) on delete restrict on update restrict;
create index ix_choice_questionRef_1 on choice (questionId);
alter table question add constraint fk_question_qcm_2 foreign key (qcmId) references qcm (id) on delete restrict on update restrict;
create index ix_question_qcm_2 on question (qcmId);
alter table response add constraint fk_response_choice_3 foreign key (choiceId) references choice (id) on delete restrict on update restrict;
create index ix_response_choice_3 on response (choiceId);
alter table response add constraint fk_response_question_4 foreign key (questionId) references question (id) on delete restrict on update restrict;
create index ix_response_question_4 on response (questionId);
alter table response add constraint fk_response_user_5 foreign key (userId) references user (id) on delete restrict on update restrict;
create index ix_response_user_5 on response (userId);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists choice;

drop table if exists qcm;

drop table if exists question;

drop table if exists response;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists choice_seq;

drop sequence if exists qcm_seq;

drop sequence if exists question_seq;

drop sequence if exists response_seq;

drop sequence if exists user_seq;

