Feature: Basit Uygulama Testi
  Uygulama açılır ve ana ekranda kısa bir bekleme yapılır.

 
  @movies @all
  Scenario: Uygulama açılması, Dil seçimi ve Moies modülüne tıklanılması.
    Given Uygulama açıldı
    And User click on "<language>" button
    Then "<expectedText>" should be visible on the screen
    And User click on "<moviesModul>" button

      Examples:
      | language | expectedText | moviesModul |
      | English  | Movies       | Movies      |
  
    
   
    

  @movies @all
  Scenario Outline: Uygulama açılması, Dil seçimi ve Moies modülüne tıklanılması.
    Given Uygulama açıldı
    And User click on "<language>" button
    Then "<expectedText>" should be visible on the screen
    And User click on "<moviesModul>" button
          Examples:
      | language | expectedText | moviesModul |
      | English  | Movies       | Movies      |




  
