# Pazuzu Registry

[![Build Status](https://travis-ci.org/zalando-incubator/pazuzu-registry.svg?branch=master)](https://travis-ci.org/zalando-incubator/pazuzu-registry)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/018a3e5ab4bc4888aa785aa736e0aa6e)](https://www.codacy.com/app/pgronkiewicz/pazuzu-registry)
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/018a3e5ab4bc4888aa785aa736e0aa6e)](https://www.codacy.com/app/Pazuzu/pazuzu-registry)
[![Stories in Ready](https://badge.waffle.io/zalando-incubator/pazuzu-registry.png?label=ready&title=Ready)](http://waffle.io/zalando-incubator/pazuzu-registry)

## What is Pazuzu

Pazuzu is a tool that builds Docker images from feature snippets, while resolving all dependencies between them. One of the common use cases is Continuous Integration environment, where jobs require specific tooling present for building and testing. Pazuzu can significantly ease that process, by letting user choose from a wide selection of predefined Dockerfile snippets that represent those dependencies (e.g. Golang, Python, Android SDK, customized NPM installs).

The Pazuzu project is divided into three sub-projects:

1. Pazuzu Registry
2. [Pazuzu CLI](https://github.com/zalando-incubator/pazuzu)
3. [Pazuzu UI](https://github.com/zalando-incubator/pazuzu-ui)

## What is Pazuzu Registry

Pazuzu Registry is a central storage of features. A feature basically consists of instructions to be included in a Dockerfile, a list of feature dependencies, and other metadata. The registry is accessable via REST API.

## What is the status of the project

The basic functionality is already implemented. However, work has to be done cleaning up things, resolving issues, and writing documentation. Feel free to provide pull requests to support this effort.

## How to build

Clone the repository and change into the project directory. Then build the project via Maven.

```bash
git clone https://github.com/zalando-incubator/pazuzu-registry.git
cd pazuzu-registry
mvn package
```

The build does three things:

1. It generates Java code from an [Open API](https://www.openapis.org/) definition file, also called [Swagger](http://swagger.io/) file, which is located in `src/main/resources/api/swagger.yaml`. The generated source code can be found in `target/generated-sources/swagger-codegen`.

2. It compiles the Java sources.

3. It builds an executable jar file which contains everything to run the service.

For subsequent builds which require a cleanup of previous build artifacts, you can run:

```bash
mvn clean package
```

## Running the service locally with in-memory storage

```bash
java -jar target/pazuzu-registry.jar --spring.profiles.active=dev
```

This starts the service on port [8080](http://localhost:8080). The `dev` profile uses an in-memory database which is populated with some initial features, but is not persisted when the service is shut down. The server can also be started with the `dev-clean` profile, if the pre-provisioned features are not needed.

## Exploring the service's API with Swagger UI

When the service is running its API specification is exposed under the [/api](http://localhost:8080/api) endpoint. This endpoint serves a JSON respresention of the Swagger file mentioned before.

You can use [Swagger UI](http://swagger.io/swagger-ui/) to get a nice HTML rendering of the API specification. To do so, follow these steps:

1. Open a new shell and clone the Swagger UI project next to `pazuzu-registry`.

```bash
git clone https://github.com/swagger-api/swagger-ui.git
```

2. Start a server that runs the Swagger UI.

```bash
cd swagger-ui/dist
python -m SimpleHTTPServer 8000
```

3. Visit [localhost:8000](http://localhost:8080), enter `http://localhost:8080/api` in the UI and hit the Explore button. You should now see an HTML page which explains the API.

## Running the service locally inside a Docker container

* Install [Docker CE](https://docs.docker.com/engine/installation/) if not already done so.
* Install [Docker Compose](https://docs.docker.com/compose/install/) if not already done so.
* Install Python 3, Pip, and the `scm-source`, see below. For more information about `scm-source` visit [STUPS documentation](https://docs.stups.io/).

To install Python 3 and Pip on macOS, run:

```bash
brew install python3
```

To install Python 3 and Pip on Ubuntu, run:

```bash
sudo apt-get install python3 python3-pip
```

To install `scm-source` on both macOS and Ubuntu, run:

```bash
sudo pip3 install -U scm-source
```

To build and run the Docker image locally, run:

```bash
scm-source # Generates a scm-source.json file required for STUPS deployment.
docker-compose -f docker-compose-base.yml up
```

## Running the service locally in a Docker container with Postgres storage

```bash
scm-source
# Generates a scm-source.json file required for STUPS deployment.

docker-compose -f docker-compose.yml up -d
# Builds the container and runs it in the background.
```

To check if the container is up and running, execute:

```bash
docker-compose ps
```

The output should look something like this:

```bash
        Name                      Command               State           Ports
--------------------------------------------------------------------------------------
pazuzuregistry_db_1    /docker-entrypoint.sh postgres   Up      0.0.0.0:5432->5432/tcp
pazuzuregistry_web_1   /bin/sh -c java $JAVA_OPTS ...   Up      0.0.0.0:8080->8080/tcp
```

To stutdown the service gracefully, without removing it, run:

```bash
docker-compose stop
```

*THE FOLLOWING MATERIAL IS NOT YET UPDATED*

## Authentication

### Enabling OAuth authentication

OAuth is enabled by default for production (prod profile) or optionally when a oauth profile has been specified.

```
-Dspring.profiles.active=dev,oauth
```

### Acquiring token for testing

For testing purpose is convenient to acquire a token from Zalando OAuth2 token server. This can be easily done using `ztoken`. In order to install it follow these steps:

```bash
sudo pip3 install -U stups
stups configure stups.zalan.do
ztoken
```

## License

The MIT License (MIT)
Copyright © 2016 Zalando SE, https://tech.zalando.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
