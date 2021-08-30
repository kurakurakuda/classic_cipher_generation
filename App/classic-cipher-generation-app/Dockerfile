FROM adoptopenjdk/openjdk11:alpine-slim AS initializer
ENV GRAILS_VERSION 4.0.3
# Install Grails
WORKDIR /usr/lib/jvm
RUN ls -l
RUN wget https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip && \
    unzip grails-$GRAILS_VERSION.zip && \
    rm -rf grails-$GRAILS_VERSION.zip && \
    ln -s grails-$GRAILS_VERSION grails
# Setup Grails path.
ENV GRAILS_HOME /usr/lib/jvm/grails
ENV PATH $GRAILS_HOME/bin:$PATH
ENV GRADLE_USER_HOME /app/.gradle
# Create minimal structure to trigger grails build with specified profile.
RUN mkdir /app \
    && mkdir /app/grails-app \
    && mkdir /app/grails-app/conf
# Set Workdir
WORKDIR /app
# Copy minimun files to trigger grails download of wrapper and dependencies.
COPY gradle.properties build.gradle /app/
# Trigger gradle build
RUN [ "grails", "stats" ]

# Implemented to improve cache in CI
FROM initializer as development
# Copy source code
COPY grails-app /app/grails-app
COPY src /app/src
EXPOSE 8080
ENTRYPOINT ["grails", "run-app"]
CMD [ "" ]

# Image used to build prod war
FROM development AS builder
# Build project
RUN [ "grails", "prod", "war" ]
RUN ls -l /app/build/libs

FROM adoptopenjdk/openjdk11:alpine-slim AS production
COPY --from=builder /app/build/libs/app-*.war app.war

EXPOSE 8080
# War runs directly. (Uses urandom as entropy source for faster startup time)
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.war"]