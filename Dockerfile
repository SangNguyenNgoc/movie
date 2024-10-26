# Sử dụng hình ảnh Maven chính thức từ DockerHub
FROM maven:3.8.4-openjdk-17 AS build

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Cài đặt các dependencies của Maven và chạy ứng dụng bằng lệnh spring-boot:run
CMD ["mvn", "spring-boot:run"]


#base image: linux alpine os with open jdk 8
#FROM maven:3.8.4-openjdk-17 AS build
#
#WORKDIR /app
#
#COPY . .

#
#RUN mvn clean package
#
#FROM openjdk:19-jdk-alpine
#
##copy jar from local into docker image
#COPY --from=build /app/target/accountservice-0.0.1-SNAPSHOT.jar accountservice-0.0.1-SNAPSHOT.jar
#
## Expose port
#EXPOSE 8080
##command line to run jar
#ENTRYPOINT ["java","-jar","/accountservice-0.0.1-SNAPSHOT.jar"]