FROM tomcat:9-jre8-openjdk-slim
RUN echo "Building Application Image!"

USER root
EXPOSE 8080

COPY ./target/appdynamics-onboarding-api-v1.war ${CATALINA_HOME}/webapps/appd-onboarding-api.war

RUN rm -frv ${CATALINA_HOME}/webapps/ROOT*

RUN ${CATALINA_HOME}/bin/startup.sh 