docker stop logistics-frontend
docker rm logistics-frontend
docker run --restart=always -p 80:8080 -d --name logistics-frontend sfirsov/logistics-frontend
