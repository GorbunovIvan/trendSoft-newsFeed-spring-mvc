FROM tomcat:10.1.13-jdk21-openjdk-slim

WORKDIR /usr/local/tomcat/webapps/

# Rename your WAR file to ROOT.war and copy it into the Tomcat webapps directory
# Without renaming it the URL to app will be 'localhost:8080/my-app' instead of 'localhost:8080'
COPY target/trendSoft-newsFeed-spring-mvc-0.0.1-SNAPSHOT.war ./ROOT.war

EXPOSE 8080

ENTRYPOINT ["catalina.sh", "run"]

# Run:
#   'docker build -t trendsoft-news-feed-spring-mvc-image .'
#   'docker-compose build'
#   'docker-compose up'