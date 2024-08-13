# Introduction
What does this project/product do?
This project monitors the ressource usage of Linux virtual machines
Who are the users?
Jarvis tech team
What are the technologies you have used?
bash, docker, git, google cloud, psql

# Quick Start
Use markdown code block for your quick-start commands
- Start a psql instance using psql_docker.sh
- Create tables using ddl.sql
- Insert hardware specs data into the DB using host_info.sh
- Insert hardware usage data into the DB using host_usage.sh
- Crontab setup

# Implemenation
Discuss how you implement the project.

## Architecture
Draw a cluster diagram with three Linux hosts, a DB, and agents (use draw.io website). Image must be saved to the `assets` directory.

## Scripts
Shell script description and usage (use markdown code block for script usage)
- psql_docker.sh:
  - `test`
- host_info.sh
  - `test`
- host_usage.sh
  - `test`
- crontab
  - `test`
- queries.sql (describe what business problem you are trying to resolve)
  - `test`

## Database Modeling
Describe the schema of each table using markdown table syntax (do not put any sql code)
- `host_info`
- `host_usage`

# Test
The bash scripts were tested locally by running each commands individually, to make sure that they have the right 

# Deployment
The app is deployed on Google Cloud. One VM is chosen to host the database server, which runs inside a Docker container. All VMs involved will send
1. Send the hardware information and the host name to the database once
2. Every minute, they will send the usage info to the database; the job is triggered by crontab.

# Improvements
The following things can be improved in the future:
1. All manual steps can be automated with `gcloud` with a `.sh` script that runs the users' laptop, which would be responsible for deploying the Docker container on the VM that hosts the Postgresql database and sending the right scripts to each VM (could be a CD/CI pipeline on Gitlab).
2. If the VM with the Docker container on it crashes, we lose all the data. It would be a great idea to back it up somewhere, could be in another region
3. For the host_usage table, we can use the host name and the timestamp as a primary key for easier access.
4. More customizability can be added (e.g. let the user choose the container name)