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
        { print( {..some_function()..} ) }
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
          
          if 4 > 3
               if 3 > 2
                   if 3 > 1
                       if 1 > 0
                           {print("4_biggest")}
                       ?
                   ?
               ?   
           ?
           
      ```
 
  8) Conditional's are defined inside double parenthesis -> ex: 
      ```ruby
        [ condition = ((1>0)) ] 
      ```
      
# Example

1)```ruby

     [num = 90 ]                         # integer
     [str = "krishna" ]                  # string
     [cap_str = {capitalize(str)} ]      # function
     [con = ((1>0)) ]                    # conditional / boolean -> True
     [list = [[1,2,3,4]] ]               # list
     [dict = {{"num":23,"age":19}} ]     # dictionary
     [age = dict[age] ]                  # dictionary property / item
     [arr_ele = list[2] ]                # list item
     
     {print("num","=",num)}              # num = 19
     {print("str","=",str)}              # str = krishna
     {print("cap_str","=",cap_str)}      # cap_str = KRISHNA
     {print(con)}                        # true
     {print("list","=",list)}            # list = 1 2 3 4
     {print(dict)}                       # {num: 23, age: 19}
     {print(age)}                        # 19
     {print(arr_ele)}                    # 3
     {print("Hello","World",str)}        # Hello World krishna
     {print(num+age+arr_ele)}            # 41

```

2) ```ruby

      def FizzBuzz(num)                   
        if num % 3 is 0 and num % 5 is 0
            [ans = "FizzBuzz"]
        ?
        if num % 3 is 0 and num % 5 != 0
            [ans = "Fizz"]
        ?
        if num % 3 != 0 and num % 5 is 0
            [ans = "Buzz"]
        ?
        if num % 3 != 0 and num % 5 != 0
            [ans = "NoFizzNoBuzz"]
        ?
        ret: ans: # ("ret:" mean's "return")
    ;

    [fn = {FizzBuzz(15)} ]
    
    {print(fn)}                  # FizzBuzz
    {print( {FizzBuzz(3)}) }     # Fizz
    {print( {FizzBuzz(5)}) }     # Buzz
    {print( {FizzBuzz(19)}) }    # NoFizzNoBuzz

```
