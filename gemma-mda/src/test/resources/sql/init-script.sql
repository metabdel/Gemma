delete from CONTACT;
delete from TAXON;
delete from EXTERNAL_DATABASE;
delete from AUDIT_TRAIL;

-- all of these are used.
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();
insert into AUDIT_TRAIL VALUES ();

-- username=administrator: primary key = 1, password = test, audit trail #1, role #1
insert into CONTACT (CLASS, NAME, LAST_NAME, USER_NAME, PASSWORD, ENABLED, AUDIT_TRAIL_FK, EMAIL, PASSWORD_HINT) values ("UserImpl", "nobody",  "nobody", "administrator", "1ee223e4d9a7c2bf81996941705435d7a43bee9a", 1, 1, "admin@gemma.org", "hint");
insert into USER_ROLE (NAME, USER_NAME, USERS_FK ) values ("admin", "administrator", 1 );

-- username=test: primary key = 2, password = test, audit trail #2, role #2
insert into CONTACT (CLASS, NAME, LAST_NAME, USER_NAME, PASSWORD, ENABLED, AUDIT_TRAIL_FK, EMAIL, PASSWORD_HINT) values ("UserImpl", "test", "test", "test", "1ee223e4d9a7c2bf81996941705435d7a43bee9a", 1, 2, "test@gemma.org", "hint");
insert into USER_ROLE (NAME, USER_NAME, USERS_FK ) values ("user", "test", 2 );

-- contact
insert into CONTACT (CLASS, NAME, EMAIL, AUDIT_TRAIL_FK) values ("ContactImpl", "admin", "another.admin@gemma.org", 3);

-- taxa
insert into TAXON (SCIENTIFIC_NAME,COMMON_NAME,NCBI_ID) values ("Homo sapiens","human","9606");
insert into TAXON (SCIENTIFIC_NAME,COMMON_NAME,NCBI_ID) values ("Mus musculus","mouse","10090"); 
insert into TAXON (SCIENTIFIC_NAME,COMMON_NAME,NCBI_ID) values ("rattus","rat","10114");

-- external databases
insert into EXTERNAL_DATABASE (NAME, DESCRIPTION, WEB_URI, FTP_URI, AUDIT_TRAIL_FK) values ("PubMed", "PubMed database from NCBI", "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=PubMed", "ftp://ftp.ncbi.nlm.nih.gov/pubmed/", 4);
insert into EXTERNAL_DATABASE (NAME, DESCRIPTION,  WEB_URI, FTP_URI, AUDIT_TRAIL_FK) values ("GO", "Gene Ontology database", "http://www.godatabase.org/dev/database/", "http://archive.godatabase.org", 5);
insert into EXTERNAL_DATABASE (NAME, DESCRIPTION,  WEB_URI, FTP_URI, AUDIT_TRAIL_FK) values ("GEO", "Gene Expression Omnibus", "http://www.ncbi.nlm.nih.gov/geo/", "ftp://ftp.ncbi.nih.gov/pub/geo/DATA", 6);
insert into EXTERNAL_DATABASE (NAME, DESCRIPTION,  WEB_URI, FTP_URI, AUDIT_TRAIL_FK) values ("ArrayExpress", "EBI ArrayExpress", "http://www.ebi.ac.uk/arrayexpress/", "ftp://ftp.ebi.ac.uk/pub/databases/microarray/data/", 7);
insert into EXTERNAL_DATABASE (NAME, DESCRIPTION,  WEB_URI, FTP_URI, AUDIT_TRAIL_FK) values ("Genbank", "NCBI Genbank", "http://www.ncbi.nlm.nih.gov/Genbank/index.html", "ftp://ftp.ncbi.nih.gov/genbank/", 8);
