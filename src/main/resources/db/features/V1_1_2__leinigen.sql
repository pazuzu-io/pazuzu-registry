INSERT INTO FEATURE (NAME, VERSION, DESCRIPTION, CREATED_AT, UPDATED_AT, STATUS, AUTHOR, SNIPPET, TEST_SNIPPET) VALUES
  ('leiningen', '2.7.1', 'demo feature with no dependency', now(), now(), 'APPROVED', 'Pazuzu team', 'RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -O /usr/bin/lein \
    && chmod +x /usr/bin/lein', '
@test "Check that Leiningen is installed" {
    command lein -v
}');