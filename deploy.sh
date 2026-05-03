#!/bin/bash
# 1. Build the project
mvn clean install

# 2. Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful. Deploying to Tomcat..."
    
    # 3. Copy the WAR to Tomcat webapps
    cp bus-reservation-server/target/bus-reservation-server.war /Users/sachith/Servers/apache-tomcat-9.0.117/webapps/
    
    echo "Deployed to Tomcat! Check logs at /Users/sachith/Servers/apache-tomcat-9.0.117/logs/catalina.out"
else
    echo "Build failed. Deployment aborted."
    exit 1
fi
