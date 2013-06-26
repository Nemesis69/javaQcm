# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table choice (
  id                        bigint auto_increment not null,
  libelle                   varchar(255),
  questionId                bigint,
  status                    varchar(255),
  constraint pk_choice primary key (id))
;

create table domain (
  id                        bigint auto_increment not null,
  libelle                   varchar(255),
  constraint uq_domain_libelle unique (libelle),
  constraint pk_domain primary key (id))
;

create table qcm (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  category                  integer,
  max_score                 decimal(38),
  constraint ck_qcm_category check (category in (0,1)),
  constraint uq_qcm_name unique (name),
  constraint pk_qcm primary key (id))
;

create table question (
  id                        bigint auto_increment not null,
  text                      varchar(255),
  domainId                  bigint,
  qcmId                     bigint,
  domain_id_value           varchar(255),
  constraint pk_question primary key (id))
;

create table response (
  id                        bigint auto_increment not null,
  choiceId                  bigint,
  questionId                bigint,
  userId                    bigint,
  constraint pk_response primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  surname                   varchar(255),
  is_admin                  tinyint(1) default 0,
  constraint pk_user primary key (id))
;

alter table choice add constraint fk_choice_questionRef_1 foreign key (questionId) references question (id) on delete restrict on update restrict;
create index ix_choice_questionRef_1 on choice (questionId);
alter table question add constraint fk_question_domain_2 foreign key (domainId) references domain (id) on delete restrict on update restrict;
create index ix_question_domain_2 on question (domainId);
alter table question add constraint fk_question_qcm_3 foreign key (qcmId) references qcm (id) on delete restrict on update restrict;
create index ix_question_qcm_3 on question (qcmId);
alter table response add constraint fk_response_choice_4 foreign key (choiceId) references choice (id) on delete restrict on update restrict;
create index ix_response_choice_4 on response (choiceId);
alter table response add constraint fk_response_question_5 foreign key (questionId) references question (id) on delete restrict on update restrict;
create index ix_response_question_5 on response (questionId);
alter table response add constraint fk_response_user_6 foreign key (userId) references user (id) on delete restrict on update restrict;
create index ix_response_user_6 on response (userId);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table choice;

drop table domain;

drop table qcm;

drop table question;

drop table response;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

