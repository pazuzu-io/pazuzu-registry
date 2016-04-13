Introduction
============

What is Pazuzu?
^^^^^^^^^^^^^^^

Pazuzu is a tool that builds Docker images from feature snippets, while resolving all dependencies between them. 
One of the common use cases is Continuous Integration environment, where jobs require specific tooling present for building and testing. Pazuzu can significantly ease that process, by letting user choose from a wide selection of predefined Dockerfile snippets that represent those dependencies (e.g. Golang, Python, Android SDK, customized NPM installs). 

What is Pazuzu Registry?
^^^^^^^^^^^^^^^^^^^^^^^^

Pazuzu Registry is a central storage of features, together with their dependencies and test cases. You can access it by command line and request your custom Dockerfile which you can pipe to Docker build command.

How to start the server locally (without OAuth)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

::

    mvn clean install
    java -jar target/pazuzu-registry.jar --spring.profiles.active=dev

How to build Docker image for Stups infrastructure (Zalando related)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

::

    scm-source
    docker build --tag <pierone_related_address>
