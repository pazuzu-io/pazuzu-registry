Authentication
==============

Enabling OAuth authentication
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

OAuth is enabled by default for production (prod profile) or optionally when a oauth profile has been specified.::

    -Dspring.profiles.active=dev,oauth

Acquiring token for testing
^^^^^^^^^^^^^^^^^^^^^^^^^^^

For testing purpose is convenient to acquire token from Zalando OAuth2 token server. This can be easily done using
`zign` in order to install it follow this steps::

  sudo pip3 install --upgrade stups

  sudo pip3 install --upgrade stups-zign

  stups configure stups.zalan.do

  zign token