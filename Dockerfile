# openjdk 17 이미지를 베이스로 사용
FROM openjdk:17-jdk

# 작업 디렉토리 설정
WORKDIR /app

# 호스트의 target/oauth-0.0.1-SNAPSHOT.jar 파일을 컨테이너의 /app 디렉토리로 복사
COPY target/oauth-0.0.1-SNAPSHOT.jar /app/oauth-0.0.1-SNAPSHOT.jar

# 포트 8080을 외부로 노출
EXPOSE 18080

# 스프링 부트 애플리케이션 실행
CMD ["java", "-Xms512m", "-Xmx1024m", "-XX:MaxMetaspaceSize=256m", "-jar", "oauth-0.0.1-SNAPSHOT.jar"]
