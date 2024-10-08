# This is from ChatGPT
FROM openjdk:11
WORKDIR /usr/src/myapp
COPY stockquote.jar /usr/src/myapp
COPY src/main/resources /usr/src/myapp/src/main/resources
COPY src/main/log /usr/src/myapp/src/main/log

EXPOSE 443
EXPOSE 5432

ENTRYPOINT ["java", "-jar", "stockquote.jar"]