Feature: WIFE Android Device Test
  Kullanıcı Testleri

  @mov1 @allScenarios
  Scenario: Chrome'u açıp PlanetThy adresine git
    Given Chrome tarayıcısı açık
    When Planet Thy Adresine gidilir
    When User click on "Movies" button
    Then "New Releases" section should be displayed

  @mov2 @allScenarios
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

  