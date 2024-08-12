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

lscpu_out=`lscpu`

cpu_number=$(echo "$lscpu_out"  | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_out"  | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_out"  | egrep "^Model name:" | awk -F ':' '{print $2}' | xargs)
clockspeed=$(echo "$lscpu_out" | egrep -oh "\@.*Hz" | egrep -oh '[0-9]+\.?[0-9]+')
cpu_mhz=$(echo $clockspeed "* 1000" | bc)
l2_cache=$(echo "$lscpu_out"  | egrep "L2" | awk '{print $3}' | xargs)
total_mem=$(vmstat --unit M | tail -1 | awk '{print $4}')
timestamp=$(date +%Y-%m-%d" "%H:%M:%S)

insert_stmt="INSERT INTO host_info(
    hostname, 
    cpu_number, 
    cpu_architecture, 
    cpu_model, 
    cpu_mhz, 
    l2_cache, 
    "timestamp",
    total_mem
) VALUES (
    '$hostname',
    $cpu_number,
    '$cpu_architecture',
    '$cpu_model',
    $cpu_mhz,
    $l2_cache,
    '$timestamp',
    $total_mem
)"

psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"