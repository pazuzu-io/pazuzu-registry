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

## How to run the service locally

```bash
java -jar target/pazuzu-registry.jar --spring.profiles.active=dev
```

This starts the service on port [8080](http://localhost:8080). The `dev` profile uses an in-memory database which is populated with some initial features, but is not persisted when the service is shut down. The server can also be started with the `dev-clean` profile, if the pre-provisioned features are not needed.

How to start the server locally (without OAuth)
-----------------------------------------------
```bash
mvn clean install
java -jar target/pazuzu-registry.jar --spring.profiles.active=dev
```

The server can also be started with the `dev-clean` profile. This profile 
will start the application with a clean DB if the pre provisioned feature
are not needed.

How to setup OSX environment
-----------------------------
 * Install [docker toolbox](https://www.docker.com/products/docker-toolbox])
 * `docker-machine start`
 * `docker-machine env default`
 * eval or export result of env command to your .bashrc or .zshrc
 * `brew install python3` # in case if you have no python installed
 * `sudo pip3 install `--upgrade scm-source
 * `run scm_source`


How a start the server locally in a container with Postgres storage
-----------------------------------------------------------
```
docker-compose up -d
```
OSX devs : Note your host ip is on DOCKER_HOST env variable
How to build Docker image for Stups infrastructure (Zalando related)
--------------------------------------------------------------------
```bash
scm-source
docker build --tag <pierone_related_address>
```

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
