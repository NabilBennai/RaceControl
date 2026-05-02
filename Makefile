install:
	cd frontend && npm install
	./mvnw -f backend/pom.xml -q -DskipTests compile
start:
	docker compose up -d --build
stop:
	docker compose down
logs:
	docker compose logs -f
frontend-start:
	cd frontend && npm start
backend-start:
	./mvnw -f backend/pom.xml spring-boot:run
test:
	cd frontend && npm test -- --watch=false
	./mvnw -f backend/pom.xml test
build:
	cd frontend && npm run build
	./mvnw -f backend/pom.xml clean package
lint:
	cd frontend && npm run lint
format:
	cd frontend && npm run format
docker-build:
	docker compose build
docker-clean:
	docker compose down -v --remove-orphans
