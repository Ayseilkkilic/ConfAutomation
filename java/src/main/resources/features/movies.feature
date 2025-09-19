Feature: Basit Uygulama Testi
  Uygulama açılır ve ana ekranda kısa bir bekleme yapılır.

 
  @movies @all
  Scenario: Uygulama açılması, Dil seçimi ve Moies modülüne tıklanılması.
    Given Uygulama açıldı
    And User click on "<language>" button
    Then "<expectedText>" should be visible on the screen
    And User click on "<moviesModul>" button
    And User click on "<moviesCategoryBlockBuster>" button
    And User click on "<moviesCategoryAwardWinners>" button
    Then User click on AnatomyPoster button
    Then User scroll to TheGoodFather poster
    Then User click on TheGoodFather poster
    Then User click on Watch Button poster


      Examples:
      | language | expectedText | moviesModul | moviesCategoryBlockBuster    | moviesCategoryAwardWinners |
      | English  | Movies       | Movies      | Blockbuster                  | Award Winners              |
                                           
    
   
    

 
  Scenario Outline: Uygulama açılması, Dil seçimi ve Moies modülüne tıklanılması.
    And User click on home button
    And User click on "<language>" button
    Then "<expectedText>" should be visible on the screen
    And User click on "<moviesModul>" button
    And User scroll to TheGoodFather poster
    And User click on TheGoodFather poster
          Examples:
      | language | expectedText | moviesModul |
      | English  | Movies       | Movies      |




  
