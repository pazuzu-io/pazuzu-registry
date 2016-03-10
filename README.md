# pazuzu-registry
------------------------------------------------------------

[![Build Status](https://travis-ci.org/zalando/pazuzu-registry.svg?branch=master)](https://travis-ci.org/zalando/pazuzu-registry)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/018a3e5ab4bc4888aa785aa736e0aa6e)](https://www.codacy.com/app/pgronkiewicz/pazuzu-registry)

What is Pazuzu
------------------------------------------------------------
Pazuzu is a tool that builds Docker images from feature snippets, while resolving all dependencies between them. One of the common use cases is Continuous Integration environment, where jobs require specific tooling present to build and test. Pazuzu can significantly ease that process, by letting user choose from a wide selection of predefined Dockerfile snippets that represent those dependencies (e.x. golang, python, Android SDK). 

What is Pazuzu Registry
------------------------------------------------------------
Pazuzu Registry is a central storage of features, together with their dependencies and test cases. Pazuzu client can access it for building Docker images with a specified set of features. 

What is current stage?
------------------------------------------------------------
Project is still in early stages - this file will be updated to reflect the progress and document usage and functionality

What are the current libraries/Frameworks used?
------------------------------------------------------------
For the API documentation and specs we are using [swagger.io](http://swagger.io/)


License
------------------------------------------------------------

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
