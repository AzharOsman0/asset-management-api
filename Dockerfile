#Contains the base image of the Java Development kit tools needed to run a java application 
FROM openjdk:17-jdk-slim

#sets the working directoriy in the docker container to /app, all instructions like copy add and run will be executed in this directory

WORKDIR /app

# Copy the Jar file into the container - the COPY instruction copies files or directories from the local file system into the docker image

COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

#tells docker that the container we are making will listen on a specfied network port at runtime, 8080 is the sport the application will run on its a hint

EXPOSE 8080

#A instruction that will run when the docker container starts it will run java jar and the name of the jar file that will execute is app.jar which we copied into the /app directory

ENTRYPOINT ["java", "-jar", "app.jar"]