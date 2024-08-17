# Radha Programming Language

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

# Installation Guide for Radha Programming Language

Welcome to the Radha Programming Language! This guide will walk you through the steps required to install and set up the environment on your machine.

## Prerequisites

Before installing the Radha Programming Language, ensure that you have the following: You can either run the java version of radha-lang for that you should have below requirments, or you can use the below [JavaScript CDN](#cdn) 👇

1. **Java Development Kit (JDK)**
   - Version 8 or higher
   - [Download JDK](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
   - Ensure Java is added to your `PATH` environment variable.

2. **Git (Optional)**
   - Used to clone the repository
   - [Download Git](https://git-scm.com/downloads)

3. **Text Editor or IDE**
   - Any editor that supports Java, such as VS Code, IntelliJ IDEA, or Eclipse.

## Step-by-Step Installation

### 1. Clone the Repository

First, clone the repository to your local machine:

```bash
git clone https://github.com/yourusername/Radha-Programming-Language.git
```
Alternatively, you can download the repository as a ZIP file and extract it to a directory of your choice.

### 2. Compile the Source Code

Navigate to the root directory of the project and compile the source code:
```bash
cd Radha-Programming-Language
javac -d bin src/*.java
```

This command compiles all .java files in the src directory and places the compiled .class files in the bin directory.

### 3. Set Up Execution Script

Windows:
- Replace `C:\path\to\your\project\bin` with the actual path to your bin directory in the `radha.bat` file.
- Add the location of radha.bat to your PATH environment variable for easy execution from any directory.

Linux / macOS:
- Replace `/home/yourusername/Radha-Programming-Language/bin` with the actual path to your bin directory in the `radha.sh` file.
- Make the script executable:
```
chmod +x radha.sh
```
- Optionally, add the directory containing radha.sh to your PATH environment variable:
```
export PATH=$PATH:/path/to/radha.sh/directory
```

# Example

1)
```ruby
# main.radha
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

2) 
 ```ruby
# main.radha
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
3) Execute the `radha` file
```bash
.\radha <filename>.radha
```

4) From js file
```js
import { RadhaProgrammingLanguage } from "./Language"

let LANGUAGE_START1 = new RadhaProgrammingLanguage(`
    
    [num = 90 ]                         # integer
    [str = "krishna" ]                  # string
    [cap_str = {capitalize(str)} ]      # function
    [con = ((1>0)) ]                    # conditional / boolean -> True
    [list = [[1,2,3,4]] ]               # list
    [dict = {{"num":23,"age":19}} ]     # dictionary
    [age = dict[age] ]                  # dictionary property / item
    [arr_ele = list[2] ]                # list item


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

    [fn = {FizzBuzz(15)} ]              # function

    {print("num","=",num)}
    {print("str","=",str)}
    {print("cap_str","=",cap_str)}
    {print(con)}
    {print("list","=",list)}
    {print(dict)}
    {print(age)}
    {print(arr_ele)}
    {print("Hello","World",str)}
    {print(num+age+arr_ele)}
    {print(fn)}

`)
```

<a name="cdn"># CDN </a>
 
 https://cdn.jsdelivr.net/gh/Axnjr/Radha-Programming-Language@main/Language.js
