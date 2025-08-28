# ---- Stage 1: build ----
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /src

# Make Maven use your GitHub repos from settings.xml
COPY settings.xml /root/.m2/settings.xml

# copy the whole project (parent + modules)
COPY . .

# Which runnable module to build (change if needed)
ARG MODULE=pg.payments.standalone

# Build the selected module (along with its deps) and collect the JAR to /out/app.jar
RUN mvn -B -DskipTests -pl ${MODULE} -am clean package \
 && JAR_PATH=$(find . -type f -path "*/${MODULE}/target/*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n1) \
 && echo "Using JAR: $JAR_PATH" \
 && mkdir -p /out \
 && cp "$JAR_PATH" /out/app.jar

# ---- Stage 2: runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /out/app.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -XX:+UseG1GC"
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
