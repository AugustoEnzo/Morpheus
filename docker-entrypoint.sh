#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# ------------------------------------------------------------------------
# Creates the examples database and respective user. This database location
# and access credentials are defined on the environment variables
# ------------------------------------------------------------------------
set -e

set POSTGRES_USER = "admin"
set POSTGRES_PASSWORD = "9HQXvYP$&*"

set MORPHEUS_USER = "morpheus"SUPERSET_USER
set MORPHEUS_USER_PASSWORD = "MORPHEUS@ADMIN2023"
set MORPHEUS_DB = "morpheus"

set SUPERSET_USER = "superset"
set SUPERSET_USER_PASSWORD = "superset"
set SUPERSET_DB = "superset"

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" <<-EOSQL
  CREATE USER ${SUPERSET_USER} WITH PASSWORD '${SUPERSET_USER_PASSWORD}';
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" <<-EOSQL
  CREATE DATABASE ${SUPERSET_DB};
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" <<-EOSQL
  GRANT ALL PRIVILEGES ON DATABASE ${SUPERSET_DB} TO ${SUPERSET_USER};
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" -d "${SUPERSET_DB}" <<-EOSQL
   GRANT ALL ON SCHEMA public TO ${SUPERSET_USER};
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" <<-EOSQL
  CREATE USER ${MORPHEUS_USER} WITH PASSWORD '${MORPHEUS_USER_PASSWORD}';
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" <<-EOSQL
  CREATE DATABASE ${MORPHEUS_DB};
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -p "${POSTGRES_PASSWORD}" <<-EOSQL
  GRANT ALL PRIVILEGES ON DATABASE ${MORPHEUS_DB} TO ${MORPHEUS_USER};
EOSQL

psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" -d "${MORPHEUS_DB}" <<-EOSQL
   GRANT ALL ON SCHEMA public TO ${MORPHEUS_USER};
EOSQL