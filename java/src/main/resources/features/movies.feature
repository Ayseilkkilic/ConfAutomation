Feature: Basit Uygulama Testi
  Uygulama açılır ve ana ekranda kısa bir bekleme yapılır.

 
  @smoke @all
  Scenario: English butonuna tıklanır
    Given Uygulama açıldı
    Then 2 saniye beklenir
    And User click on "English" button
    Then 2 saniye beklenir

  @smoke1 @all
  Scenario Outline: Dil seçimi sonrası başlık görünürlüğü
    Given Uygulama açıldı
    Then 2 saniye beklenir
    And User click on "<language>" button
    Then 2 saniye beklenir
    Then "<expectedText>" should be visible on the screen

    Examples:
      | language | expectedText |
      | English  | Movies       |
