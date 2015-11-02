# rabbit-puppy
Configure a remote RabbitMQ server based on YAML configuration

# docker
run "mvn clean package docker:build"
cd target/docker
docker-compose up
