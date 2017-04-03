# Pazuzu Registry

[![Build Status](https://travis-ci.org/zalando-incubator/pazuzu-registry.svg?branch=master)](https://travis-ci.org/zalando-incubator/pazuzu-registry)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/018a3e5ab4bc4888aa785aa736e0aa6e)](https://www.codacy.com/app/pgronkiewicz/pazuzu-registry)
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/018a3e5ab4bc4888aa785aa736e0aa6e)](https://www.codacy.com/app/Pazuzu/pazuzu-registry)
[![Stories in Ready](https://badge.waffle.io/zalando-incubator/pazuzu-registry.png?label=ready&title=Ready)](http://waffle.io/zalando-incubator/pazuzu-registry)

## What is Pazuzu

Pazuzu is a tool that builds Docker images from feature snippets, while 
resolving all dependencies between them. One of the common use cases is 
Continuous Integration environment, where jobs require specific tooling present 
for building and testing. Pazuzu can significantly ease that process, by letting user 
choose from a wide selection of predefined Dockerfile snippets that represent 
those dependencies (e.g. Golang, Python, Android SDK, customized NPM installs). 

## What is Pazuzu Registry

Pazuzu Registry is a central storage of features. A feature basically consists of instructions to be included in a Dockerfile, a list of feature dependencies, and other metadata. The registry is accessable via REST API. The [Pazuzu CLI](https://github.com/zalando-incubator/pazuzu) is used to request the API in order to generate a custom Dockerfile.

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

## How to run the service locally

```bash
java -jar target/pazuzu-registry.jar --spring.profiles.active=dev
```

This starts the service on port [8080](http://localhost:8080). The server can also be started with the `dev-clean` profile. This profile will start the application with a clean database if the pre-provisioned features are not needed.

## How to explore the service's API

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

## How to run the service locally inside a Docker container

If you have not already installed [Docker](https://www.docker.com/), please do so by following the [Install Docker guide](https://docs.docker.com/engine/installation/) for Docker CE.

You should also have Python 3, Pip, and the `scm-source` tool installed. The latter is part of the [STUPS](https://stups.io/) tools which are introduced during the [STUPS workshop](https://github.bus.zalan.do/stups/stups-training).

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

To build the Docker image, run:

```bash
scm-source # Generates a scm-source.json file required for STUPS deployment.
docker build -t pazuzu-registry .
```

Finally, run the generated Docker image like this:

```bash
docker run pazuzu-registry
```

License
-------

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
