DOCKER  
docker-compose up -d   
docker-compose down   
docker rmi -f catalog pricing registry api   

LOGIN container   
docker exec -it catalog sh   
docker exec -it pricing sh   
docker exec -it api sh   
docker exec -it registry sh  

BUILD   
./build.sh   
./start.sh   
./stop.sh   

DOCKER URLs   
POST http://localhost:8006/catalog/car/price   
{   
	"brand": "Tata Nexon",    
	"model": "XE"   
}   

POST http://localhost:8006/pricing/price   
{   
	"brand": "Huyndai Venue",    
	"model": "S"   
}   


http://localhost:8006/catalog/   
http://localhost:8006/pricing/   

DIRECT URLS   
POST http://localhost:8006/catalog/car/price   
{   
	"brand": "Tata Nexon",     
	"model": "XE"   
}   

POST http://localhost:8006/pricing/price    
{   
	"brand": "Huyndai Venue",    
	"model": "S"    
}   

http://localhost:8082/catalog/   
http://localhost:8081/pricing/ 
  
