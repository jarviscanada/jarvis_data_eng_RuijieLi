#! /bin/sh

# Capture CLI arguments
cmd=$1
db_username=$2
db_password=$3

# Start docker
# Make sure you understand the double pipe operator
sudo systemctl status docker > /dev/null || systemctl start docker


# Check container status (try the following cmds on terminal)
sudo docker container inspect jrvs-psql 1>/dev/null 2>/dev/null
container_status=$?
# if [ container_status -ne 0 ]; then
#     echo 'creating container...'
# fi
# User switch case to handle create|stop|start opetions
case $cmd in 
    create)
    
        # Check if the container is already created
        if [ $container_status -eq 0 ]; then
            echo 'Container already exists'
            exit 1	
        fi

        # Check # of CLI arguments
        if [ $# -ne 3 ]; then
            echo 'Create requires username and password'
            exit 1
        fi
        echo 'creating volume...'
        # Create container
        sudo docker volume create pgsql
        echo 'volume created'
        # Start the container
        export PGPASSWORD='password'

        echo 'creating docker container...'
        sudo docker container create --name jrvs-psql -p 5432:5432 -e POSTGRES_PASSWORD=$PGPASSWORD -v pgsql:/var/lib/postgresql/data postgres:9.6-alpine
        echo 'docker container created'

        echo 'starting docker container...'
        sudo docker container start jrvs-psql
        echo 'docker container started'

        echo 'waiting for db to connect...'
        sudo docker exec -it jrvs-psql pg_isready
        dbready=$?
        while [ $dbready -ne 0 ]
        do
            sudo docker exec -it jrvs-psql pg_isready
            dbready=$?
        done
        echo 'db connected!'

        # create user
        export formatted_db_pwd="'${db_password}'"

        echo 'running query to create user...'
        psql \
            -h localhost\
            -p 5432\
            -U postgres\
            -d postgres\
            -c "CREATE USER ${db_username} WITH PASSWORD ${formatted_db_pwd}; ALTER USER ${db_username} WITH SUPERUSER"
        echo 'user created!'
        export PGPASSWORD=$db_password
        echo "Now you can connect to ${db_username} with ${db_password}"
        # Make sure you understand what's `$?`
        exit $?
        ;;

    start|stop) 
        # Check instance status; exit 1 if container has not been created
        if [ $container_status -ne 0 ]; then
            echo 'container does not exist'
            exit 1;
        fi

        # Start or stop the container
        docker container $cmd jrvs-psql
        exit $?
        ;;	

    *)
        echo 'Illegal command'
        echo 'Commands: start|stop|create'
        exit 1
        ;;
esac
