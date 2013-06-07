# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table question (
  id                        bigint not null,
  text                      varchar(255),
  domain_id                 bigint,
  constraint pk_question primary key (id))
;

create table response (
  id                        bigint not null,
  libelle                   varchar(255),
  questionId                bigint,
  constraint pk_response primary key (id))
;

create sequence question_seq;

create sequence response_seq;

alter table response add constraint fk_response_questionRef_1 foreign key (questionId) references question (id) on delete restrict on update restrict;
create index ix_response_questionRef_1 on response (questionId);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists question;

drop table if exists response;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists question_seq;

drop sequence if exists response_seq;

