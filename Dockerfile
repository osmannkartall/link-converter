FROM openjdk:22

COPY /build/libs/*.jar link-converter.jar

EXPOSE 8080

CMD ["java", "-jar", "link-converter.jar"]