#!/bin/bash
psql_host=$1 # ip addr
psql_port=$2 # port
db_name=$3 # database name
psql_user=$4 # user name
psql_password=$5 # password

export PGPASSWORD=$psql_password

if [ "$#" -ne 5 ]; then
    echo "illegal number of arguments"
    exit 1;
fi

vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

memory_free=$(echo "$vmstat_mb" | awk '{print $4}' | tail -n 1 | xargs)
cpu_idle=$(echo "$vmstat_mb" | awk '{print $15}' | tail -n 1 | xargs)
cpu_kernel=$(echo "$vmstat_mb" | awk '{print $14}' | tail -n 1 | xargs)
disk_io=$(vmstat -d | awk '{print $10}' | tail -n 1 | xargs)
disk_available=$(df -BM / | awk '{print $4}' | tail -n 1 | egrep -oh "[0-9]*")
timestamp=$(vmstat -t | awk '{print $18, $NF}' | tail -n 1)

id_query_output=$(psql -h localhost -U postgres -d host_agent -c "SELECT id FROM HOST_INFO WHERE hostname='ruijieli-HP-Laptop-15-dy2xxx'")
id=$(echo "$id_query_output" | sed -n '3p' | xargs)

insert_stmt="INSERT INTO host_usage(
    timestamp,
    host_id, 
    memory_free, 
    cpu_idle, 
    cpu_kernel, 
    disk_io, 
    disk_available
) VALUES (
    '$timestamp',
    $id,
    $memory_free,
    $cpu_idle,
    $cpu_kernel,
    $disk_io,
    $disk_available
)";

psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"