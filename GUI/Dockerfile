FROM amd64/ubuntu:24.04 AS build

RUN apt-get update && apt-get install -y xorg libgtk-3-0 wget software-properties-common maven \
    && wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - \
    && add-apt-repository --yes https://packages.adoptium.net/artifactory/deb/ \
    && apt-get update && apt-get install -y temurin-24-jdk \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .
RUN mvn -B jpro:release

FROM amd64/ubuntu:24.04

RUN apt-get update && apt-get install -y xorg libgtk-3-0 wget software-properties-common unzip \
    && wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - \
    && add-apt-repository --yes https://packages.adoptium.net/artifactory/deb/ \
    && apt-get update && apt-get install -y temurin-24-jdk \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/*-jpro.zip ./app.zip
RUN unzip app.zip && rm app.zip

EXPOSE 8080
CMD ["./javafx-client-1.0.0-jpro/bin/start.sh"]