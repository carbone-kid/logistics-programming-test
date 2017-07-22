docker stop logistics-backend
docker rm logistics-backend
docker run --restart=always -p 8080:8080 -d --name logistics-backend sfirsov/logistics-backend
#docker run -p 8080:8080 -t sfirsov/logistics-backend