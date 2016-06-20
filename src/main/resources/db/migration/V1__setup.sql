create table log_entry (
  index int not null,
  certificate_sha256 bytea not null
);

create table certificate (
    certificate_sha256 bytea not null,
    common_name text not null,
    validity_start date not null,
    validity_end date not null,
);

create table cert_subject_alternative_name (
    certifificate_sha256 bytea not null,
    san_value text not null
);