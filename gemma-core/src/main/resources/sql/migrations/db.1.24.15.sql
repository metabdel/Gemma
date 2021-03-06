CREATE TABLE EXPRESSION_EXPERIMENT_SPLIT_RELATION (
  EXPRESSION_EXPERIMENT_FK bigint(20) NOT NULL,
  OTHER_PART_FK bigint(20) NOT NULL,
  PRIMARY KEY (EXPRESSION_EXPERIMENT_FK,OTHER_PART_FK),
  KEY EXPRESSION_EXPERIMENT_OTHER_PART_FKC (EXPRESSION_EXPERIMENT_FK),
  KEY OTHER_PART_FKC (OTHER_PART_FK),
  CONSTRAINT EXPRESSION_EXPERIMENT_OTHER_PART_FKC FOREIGN KEY (EXPRESSION_EXPERIMENT_FK) REFERENCES INVESTIGATION (ID),
  CONSTRAINT INVESTIGATION_OTHER_PART_FKC FOREIGN KEY (OTHER_PART_FK) REFERENCES INVESTIGATION (ID)
 );
 