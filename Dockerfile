FROM java:8
ADD "target/scala-2.11/NestSlack-assembly-0.1.jar" "/app/"
ENTRYPOINT ["java", "-jar", "/app/NestSlack-assembly-0.1.jar"]