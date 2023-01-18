# Radha_Programming_Language

A basic programming language that includes features of both Python and JavaScript created by Axnjr (Yakshit Chhipa) as a personal project to include in my resume .

# Small Docs / A few rules before starting .
 
 1) Variable declartion and assignment inside square brackets -> ex: 
    ```ruby
        [ x = 90 ]
    ```
 
 2) List's / array's are defined inside double square brackets -> ex: 
     ```ruby
        [ arr = [[1,2,3,4,5]] ] 
     ```
 
 3) Dictionarie's / object's are defined inside double curly brackets -> ex: 
     ```ruby
        [ dict = {{"name":"Krishna","age":19}} ] 
     ```
 
 4) Functions are called inside single curly brackets -> ex: 
     ```ruby 
        { print("Hello World") } 
        { print( {capitalize("world")} }
        { print( {..some_user_defined_function()..} ) }
     ```
 
 5) Functions are defined using "def" keyword and must end with ";" ( identation not neccesary but new line is neccesary )
      ex: 
      ```ruby
          def Sum(a,b) 
              ret: a+b
          ;
      ```
 
 6) For loops must end with ":" else same as for-in loop's in python ( identation not neccesary but new line is neccesary )
       ex: 
       ```ruby
            for [i] in (10)
                 {print(i)}
            :
            
            for [i] in [[1,2,3,4,5]]
                  {print(i)}
            :
            
            for [i] in (0,50,5)
                  {print(i)}
            :
       ```
 
  7) if statement's same as python but they end with "?" ( identation not neccesary but new line is neccesary )
      ex: 
      ```ruby
          if 8 + 2 is 10
              {print("YES")}
          ?
      ```
 
  8) Conditional's are defined inside double parenthesis -> ex: 
      ```ruby
        [ condition = ((1>0)) ] 
      ```
