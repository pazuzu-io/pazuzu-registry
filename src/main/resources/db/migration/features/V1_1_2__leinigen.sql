INSERT INTO FEATURE (NAME, DESCRIPTION, CREATED_AT, UPDATED_AT, STATUS, AUTHOR, SNIPPET, TEST_SNIPPET) VALUES
  ('leiningen', 'demo feature with no dependency', now(), now(), 'PENDING', 'Pazuzu team', 'RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/bin/lein \
    && chmod +x /usr/bin/lein', '#!/usr/bin/env bats

@test "Check that Leiningen is installed" {
    command lein -v
}');