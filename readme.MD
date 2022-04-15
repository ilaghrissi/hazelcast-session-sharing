
### Step 1 create docker network
    docker network create hazelcast-network

### Step 2 run hzelcast cluster
    docker run -it --network hazelcast-network --rm -e HZ_CLUSTERNAME=hello-world -e HZ_USERCODEDEPLOYMENT_ENABLED=true -p 5701:5701 hazelcast/hazelcast:5.1.1

### Step 3 run hazelcast management
    docker run --network hazelcast-network -p 8080:8080 hazelcast/management-center