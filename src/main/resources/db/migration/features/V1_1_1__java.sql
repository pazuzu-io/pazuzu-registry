INSERT INTO FEATURE (NAME, DESCRIPTION, CREATED_AT, UPDATED_AT, AUTHOR, SNIPPET, TEST_SNIPPET) VALUES
  ('java', 'basic java feature', now(), now(), 'Pazuzu team', 'RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list

RUN apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections
RUN apt-get update && apt-get install -y oracle-java8-installer
RUN update-java-alternatives -s java-8-oracle', '#!/usr/bin/env bats

@test "Check that Java is installed" {
    command java -version
}');