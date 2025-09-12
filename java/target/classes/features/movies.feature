Feature: Basit Uygulama Testi
  Uygulama açılır ve ana ekranda kısa bir bekleme yapılır.

  # @smoke
  # Scenario: Uygulama açılır ve ana ekranda 30 saniye beklenir
  #   Given Uygulama açıldı
  #   Then 25 saniye beklenir

  @smoke
  Scenario: English butonuna tıklanır
    Given Uygulama açıldı
    Then 25 saniye beklenir
    And User click on "English" button
    Then 25 saniye beklenir
