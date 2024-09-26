#使用maven
FROM maven:3-amazoncorretto-17 AS build

#將目錄下的檔案複製到app資料夾中
COPY . /app

#設定之後執行操作的位置
WORKDIR /app

#使用maven打包，不經過測試
RUN mvn clean package -DskipTests

#使用amazoncorretto
FROM amazoncorretto:17-alpine

#從上面build的部分取得jar，複製到新容器中
COPY --from=build /app/target/*.jar app.jar
#將.env複製到容器中
COPY ./.env .env

#使用port 8080
EXPOSE 8080

#啟動Spring Boot專案
ENTRYPOINT ["java", "-jar", "app.jar"]
