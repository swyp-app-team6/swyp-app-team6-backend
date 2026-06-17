FROM eclipse-temurin:25-jre

WORKDIR /app

COPY build/libs/*.jar ./app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:InitialRAMPercentage=70.0 \
           -XX:MaxRAMPercentage=70.0 \
           -XX:MetaspaceSize=128M \
           -XX:MaxMetaspaceSize=256M \
           -XX:+UseG1GC \
           -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-local} -jar /app/app.jar"]
