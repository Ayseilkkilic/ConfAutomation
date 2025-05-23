Feature: WIFE Android Device Test
  Kullanıcı Testleri

  @mov1 @all
  Scenario: Chrome'u açıp PlanetThy adresine git
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    Then "New Releases" section should be displayed

  @mov2 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText            |
      | New Releases        |
      | Blockbuster         |
      | Award Winners       |
      | Best Series         |
      | Timeless Collection |
      | Animation           |
      | True Stories        |
      | Magical Realms      |
      | More Hollywood      |
      | Turkish Cinema      |
      | World Cinema        |
      | Accessibility       |
      | By Language         |
      | View All            |

  @mov3 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "New Releases" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | New Releases    |
      | MENU   |

  @mov4 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Blockbuster" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Blockbuster     |
      | MENU   |

  @mov5 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Award Winners" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Award Winners    |
      | MENU   |

  @mov6 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Best Series" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Best Series   |
      | MENU   |
  
  @mov7 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Timeless Collection" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Timeless Collection  |
      | MENU   |

  @mov8 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Animation" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Animation  |
      | MENU   |
  
  @mov9 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "True Stories" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | True Stories  |
      | MENU   |
  
  @mov10 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Magical Realms" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Magical Realms  |
      | MENU   |
  
  @mov11 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "More Hollywood" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | More Hollywood  |
      | MENU   |

  @mov12 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Turkish Cinema" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Turkish Cinema  |
      | MENU   |

  @mov13 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "World Cinema" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | World Cinema  |
      | Bollywood   |
      | Far East & Asia   |
      | Middle East   |
      | European   |

  @mov14 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "Accessibility" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Filter        |
      | Accessibility  |
      | MENU   |

  @mov15 @all
  Scenario Outline: Verify menu button texts are visible on the screen
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    When User click on "By Language" button
    Then "<MenuText>" should be visible on the screen

    Examples:
      | MenuText           |
      | Movies        |
