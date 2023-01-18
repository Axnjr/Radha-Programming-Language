/** 
 * 1) Variable declartion and assignment inside square brackets -> ex: [ x = 90 ]
 * 
 * 2) List's / array's are defined inside double square brackets -> ex: [ arr = [[1,2,3,4,5]] ]
 * 
 * 3) Dictionarie's / object's are defined inside double curly brackets -> ex: [ dict = {{"name":"Krishna","age":19}} ]
 * 
 * 4) Functions are called inside single curly brackets -> ex: { print("Hello World") }
 * 
 * 5) Functions are defined using "def" keyword and must end with ";" ( identation not neccesary )
 *      ex: 
 *          def Sum() 
 *              ...code...
 *          ;
 * 
 * 6) For loops must end with ":" else same as for-in loop's in python ( identation not neccesary but new line is neccesary )
 *       ex: 
 *            for [i] in (10)
 *                 {print(i)}
 *            :
 * 
 *  7) if statement's same as python but they end with "?" ( identation not neccesary but new line is neccesary )
 *      ex: 
 *          if 8 + 2 is 10
 *              {print("YES")}
 *          ?
 * 
 *  8) Conditional's are defined inside double parenthesis -> ex: [ condition = ((1>0)) ]
 */

class BuiltInFunctions_RadhaProgrammingLanguage{
    constructor(){
        this.builtInFunctions = 
                                [
                                    "print",
                                    "capitalize",
                                    "casefold",
                                    "type",
                                    // I had to move on to a new project hence these many functions only : )
                                    // if you want you can add some functions that mimic python built-in functions : )
                                    // append it's name in this array and update the FunctionCaller() method : )
                                ]

        this.userDefinedFunctions = [] // empty array to store user defined functions .
    }

    print(data){ 
        if(typeof(data)==="string"){ 
            data = data.replaceAll(","," ") // we don't display comma's in between variable's to be printed hence they are seprated by whitespace .
            console.log(data.replace(/'/g,"").replace(/"/g,"")) 
        }
        else{ console.log(data) }
    }

    capitalize(data){ return data.toUpperCase() }

    casefold(data){ return data.toLowerCase() }

    type(data){ 
        if(isNaN(Number(data))){ return "<class 'str'>" }
        if(!isNaN(Number(data))){ return "<class 'int'>" }
    }

    FunctionCaller(index,params){
        if(index===0){ this.print(params) }
        if(index===1){ return this.capitalize(params) }
        if(index===2){ this.casefold(params) }
        if(index===3){ this.type(params) } 
    }

}

class RadhaProgrammingLanguage extends BuiltInFunctions_RadhaProgrammingLanguage{

    constructor(code){
        super();
        let temp_code = code.split(/\n\s/g)

        for(let i = 0 ; i < temp_code.length ; i++){
            if(!this.isValid(temp_code[i])){
                throw ` 
                    Parenthesis don't match at line ${i} you might
                    have forgoten a closing or opening bracket !!
                `
            }
        }

        this.CODE = code.split(" ").join("")
        this.LETTERS =/^[a-zA-Z]*$/
        this.NUMBERS = /[0-9]/gi
        this.KEYWORDS = [
            "for","in","if","is","else","def","and","or",
            "brk:","con:","ret:","True","False","None","not"
        ]
        this.VARIABLES = new Map() // hash map to store variables .
        this.FUNCTIONS = new Map() // hash map to store user defined functions .
        this.CURRENTLY_RUNNING_FUNCTION_STACK = [] // stack to hold currently executuing function .

        this.MainProgramThread(0,this.CODE)
    }

    isValid(s){
        var st = []
        for(var l of s)
            if ((l="({[]})".indexOf(l))>-1)
                if (st[st.length-1]+l===5)
                    st.length--;
                else
                    st.push(l);
        return st.length===0
    };

    CheckIfString(str){
        let l = str.length-1
        if( (str[0] === `"` || str[0] === `'`) && (str[l] === `"` || str[l] === `'`) ){ return true }
        else return false 
    }

    MainProgramThread(index,script){ 
        let c = script || this.CODE

        for(let pointer = index ; pointer < c.length ; pointer++ ){

            if(/\s/g.test(c[pointer])){ pointer++ } // skipping white spaces

            if(/#/g.test(c[pointer])){ // it's a comment
                while(c.length){ 
                    if(c[pointer] === "\n"){  break } 
                    pointer++ 
                }
            }

            if(c[pointer] === "["){ // assignment of variables
                if( c[pointer+1] === "]" ){ break } // in case of empty declaration [] like this we must break else loop
                pointer++ ; let varName= "" ; let varValue = "" ; let i = 0

                while(true){ // for variable name
                    if(c[pointer] === "="){ break }
                    varName += c[pointer]
                    pointer++ 
                }

                if(this.KEYWORDS.includes(varName)){ // variable name cannot be same as name of keywords .
                    throw `
                        Syntax Error : invalid syntax "${varName}" 
                        is a reserved keyword it cannot be used !!
                    `
                }
                pointer++ // move ahead

                if( c[pointer]+c[pointer+1] === "[[" ){ // array is declared
                    pointer = pointer + 1
                    varValue =  this.ArrayDeclaration(pointer,c)
                    this.VARIABLES.set(varName,varValue)
                }

                else if( c[pointer]+c[pointer+1] === "{{" ){  // dictionary / object is declared .
                    pointer = pointer + 1 ; let varValue = ""
                    while(true){
                        varValue += c[pointer]
                        if( c[pointer] === "}" ){ break }
                        pointer++
                    }
                    varValue = `${varValue}`
                    this.VARIABLES.set(varName,JSON.parse(varValue))
                }

                else{ // integer or string is declared 

                    while(true){
                        if(c[pointer] === "]"){ break }
                        varValue += c[pointer]
                        pointer++
                    }

                    if( this.VARIABLES.has(varValue[0]) &&  varValue[1] === "[" ){
                        varValue = this.ReturnPropertyOfObject(varValue)
                    }

                    if( this.builtInFunctions.includes(varValue.split("(")[0].replace(/[{}]/g,""))  ){ 
                        let fn = varValue.split("(")[0].replace(/[{}]/g,"")
                        let pr = varValue.split("(")[1].replace(/[{}]/g,"")
                        // in case variable is assigned value returned from a function .
                        // Execute function will then call Function Caller which will again call
                        // the respective function that return the executed value to the 
                        // Function Caller which then returns to Execute function and then it returns here .
                        varValue = this.ExecuteFunction(fn,pr) 
                    }

                    else{  varValue = this.ProcessData(varValue) }
                    this.VARIABLES.set(varName,varValue)
                }
            }
            if(c[pointer] === "{"){ this.HandleDefinedFunctions(pointer+1,c) ; break ; }

            if( c[pointer]+c[pointer+1]+c[pointer+2] === "for" ){  this.ForLoopInterpreter(pointer+3,c) ; break ; }

            let k = c[pointer]+c[pointer+1]+c[pointer+2]+c[pointer+3]
            if( k === "ret:" ){ // return statement .
                if(this.CURRENTLY_RUNNING_FUNCTION_STACK.length>0){
                    let fnName = this.CURRENTLY_RUNNING_FUNCTION_STACK[0]
                    let ret_val = this.ProcessData(this.LoopTillGivenCharacter(pointer+4,":",c)[1])
                    this.VARIABLES.set(fnName,ret_val)
                    this.CURRENTLY_RUNNING_FUNCTION_STACK.pop()
                }
            }

            if( c[pointer]+c[pointer+1] === "if" ){ 
                this.IfInterpreter(pointer+2,c) ; break ; 
            }

            if( c[pointer]+c[pointer+1]+c[pointer+2] === "def" ){ this.RegisterUserDefinedFunctions(pointer+3) ; break }

        }
    }

    ArrayDeclaration(pointer,c){ 
        let varValue = [] ; pointer++
        while(c[pointer]+c[pointer+1] !== "]]"){
            varValue += c[pointer]
            if(pointer>this.CODE.length){ break }
            pointer++
        }
        varValue = `[${varValue}]`
        return JSON.parse(varValue)
    }

    ReturnElementAtIndexOfArray(varValue){
        varValue = varValue.split("[")
        if(this.VARIABLES.has(varValue[0])){
            let temp = this.VARIABLES.get(varValue[0]) // array whose element is required
            let index = varValue[1].replace("]","") // index of element to be searched
            if( !typeof(temp) === "object" ){ throw `TypeError: "${this.type(temp)}" object is not subscriptable .` } // given variable is not an array
            if( index >= temp.length ){ throw `IndexError: list index out of range` } // index is greater than array length .
            return temp[index]
        }
        else if( !this.VARIABLES.has(varValue[0]) ){ throw `NameError : name "${varValue[0]}" is not defined .` }
    }

    ReturnPropertyOfObject(data){
        data = data.split("[")
        let obj = this.VARIABLES.get(data[0])
        if(obj.hasOwnProperty(data[1])){ return obj[data[1]]+"" }
        else{ throw `Name Error : "${data[1]}" not found in ${obj} .` }
    }

    ProcessData(varValue){

        if( this.VARIABLES.has(varValue) ){  varValue = this.VARIABLES.get(varValue)  } // if variable is assigned another variable

        else if( varValue[0]==="{" ){  
            this.HandleDefinedFunctions(1,varValue)
            if(this.isValid(varValue)){  varValue = this.VARIABLES.get(varValue.replace(/[{}]/g,"")) } // to avoid error's in parenthesis occured during executuion , don't disturb this block ..
            else{ varValue = this.VARIABLES.get(varValue.replace(/[{}]/g,"")+")") } // don't make changes read above comment .
        }

        else if( varValue.includes("((") ){ varValue = eval(varValue.replaceAll(/[()]/g,"")) } // conditional's

        else if(varValue.includes("+")){ varValue = this.HandleAddition(varValue) } // for 2+2 or "hello"+"world" such cases

        else if(varValue.includes("-")){ varValue = this.HandleSubtraction(varValue) } // for 4-2 such cases

        else if(varValue.includes("*")){ varValue = this.HandleMultiplication(varValue) } // for 2*2 such cases

        else if(varValue.includes("/")){ varValue = this.HandleDivision(varValue) }  // for 2/2 such cases

        else if(varValue.includes("%")){ varValue = this.HandleMod(varValue) }  // for 2%2 such cases

        else if(varValue.includes("[")){ varValue = this.ReturnElementAtIndexOfArray(varValue) }

        else if( !this.CheckIfString(varValue) && isNaN(Number(varValue)) && !this.KEYWORDS.includes(varValue) ){ 
            throw `NameError : name "${varValue}" is not defined .`  
        }

        return varValue
    }
    
    HandleAddition(data){ 
        data = data.split("+") ; let int_sum = 0 ; let str_sum = "" ;

        for(let i in data){  data[i] = this.ProcessData(data[i])  }

        for(let i = 0 ; i < data.length ; i++ ){

            if( this.CheckIfString(data[i]) ){ str_sum += data[i].replace(/"/g,"").replace(/'/g,"") } // for adding strings 

            if(!this.CheckIfString(data[i])){  int_sum += Number(data[i]) } // for adding numbers

        }
        if(int_sum===0){ return str_sum } // two strings were passed . BUG FOUND <--
        if(str_sum===""){ return int_sum+"" } // two integers were passed
        else{  throw `TypeError: can only concatenate str (not "int") to str`  } // one string and one integer were passed which cannot be added in python.
    }

    HandleSubtraction(data){
        data = data.split("-") 

        if( this.CheckIfString(data[0]) ){ // if it is a program string (quotes prresent) then it can't be subtracted .
            throw `TypeError: unsupported operand type(s) for -: '${this.type(data[0])}' and '${this.type(data[1])}' ` 
        }

        data[0] = this.ProcessData(data[0]) ; let ans = data[0] 

        for(let i = 1 ; i < data.length ; i++){
            if( this.CheckIfString(data[i]) ){  // strings can't be subtracted .
                throw `TypeError: unsupported operand type(s) for -: '${this.type(data[i])}' and '${this.type(data[i+1])}' ` 
            }
            data[i] = this.ProcessData(data[i]) 
            ans -= Number(data[i]) 
        }
        return ans+""
    }

    HandleMultiplication(data){
        data = data.split("*") ; let ans = 1
        for(let i = 0 ; i < data.length ; i++){
    
            if( this.CheckIfString(data[i]) ){  // strings can't be multiplied .
                throw `TypeError: can't multiply sequence by non-int of type '${this.type(data[i])}'` 
            }

            data[i] = this.ProcessData(data[i]) 
            ans = ans * data[i]
        }
        return ans
    }

    HandleDivision(data){
        data = data.split("/")

        if( this.CheckIfString(data[0]) || this.CheckIfString(data[1]) ){  // strings can't be divided
            throw `TypeError: can't divide sequence by non-int of type '${this.type(data[0])}'` 
        }

        data[0] = this.ProcessData(data[0])
        data[1] = this.ProcessData(data[1])
        return data[0]/data[1]
    }

    HandleMod(data){
        data = data.split("%")

        if( this.CheckIfString(data[0]) || this.CheckIfString(data[1]) ){  // strings can't be divided
            throw `TypeError: can't divide sequence by non-int of type '${this.type(data[0])}'` 
        }

        data[0] = this.ProcessData(data[0])
        data[1] = this.ProcessData(data[1])
        return data[0]%data[1]
    }

    RegisterUserDefinedFunctions(pointer){
        let c = this.CODE ; let fnName = "" ; let params = "" ; let fn_code = "" ; let temp ;

        // temp[0] has the modified pointer and temp[1] has the value after looping .
        temp = this.LoopTillGivenCharacter(pointer,"(",c) // looping to get fnName (function name)
        fnName = temp[1]

        temp = this.LoopTillGivenCharacter(temp[0]+1,")",c) // looping to get params (function paramaters)
        params = temp[1]

        temp = this.LoopTillGivenCharacter(temp[0]+1,";",c,`Syntax Error : Function never ended ";" not found .`) // looping to get fnCode (function code/script)
        fn_code = temp[1] ; pointer = temp[0]+1

        let FN_OBJECT = {}
        FN_OBJECT.PARAMETERS = params.split(",")
        FN_OBJECT.CODE = fn_code + ":" // adding a semi colon bcuz else it would give error in case of user defined function .
        this.userDefinedFunctions.push(fnName)
        this.FUNCTIONS.set(fnName,FN_OBJECT)
        this.MainProgramThread(pointer+1,c)
    }

    LoopTillGivenCharacter(pointer,till,optional_script,optional_error_message,optional_condition){ // "till" is the character till which we have to loop .
        let return_value = "" ;
        optional_condition = optional_condition || false
        if(optional_script===undefined){ optional_script = this.CODE } // if optional_script not provided then entire code is considered .

        // console.log(optional_script)

        while(true){
            if(optional_script[pointer]===undefined){ break }
            
            if( optional_script[pointer] === till ){ 
                if(optional_condition){ return_value += optional_script[pointer] ; break } // optional condition specifies wheather "till" character should also be included in the return result string .
                else{ break }
            }

            if( pointer > this.CODE.length ){
                if(optional_error_message === undefined){ break } // optional error message for more symantic erorrs .
                else{ throw optional_error_message }
            }
            return_value += optional_script[pointer]
            pointer++
        }
        return [pointer,return_value]
    }

    HandleDefinedFunctions(pointer,script){
        let c = script ; let fnName = "" ; let PARAM_VALUES = "" ; let temp ;

        temp = this.LoopTillGivenCharacter(pointer,"(",c)
        fnName = temp[1] ; pointer = temp[0]+1

        temp = this.LoopTillGivenCharacter(pointer,")",c)
        PARAM_VALUES = temp[1] ; pointer = temp[0]+1

        // console.log(PARAM_VALUES)

        if( this.builtInFunctions.includes(fnName) ){ this.ExecuteFunction(fnName,PARAM_VALUES) }

        if( !this.builtInFunctions.includes(fnName) && !this.userDefinedFunctions.includes(fnName) ){ 
            throw `Name "${fnName}" is not defined !`
        }
        
        if( this.userDefinedFunctions.includes(fnName) ){ 
            let script = this.FUNCTIONS.get(fnName).CODE // execute the function script stored in the FUNCTION hash map.
            let PARAMS = this.FUNCTIONS.get(fnName).PARAMETERS ; PARAM_VALUES = PARAM_VALUES.split(",")

            if(PARAM_VALUES.length !== PARAMS.length){ // uneven argument number (arguments = params)
                throw `"${fnName}" takes ${PARAMS.length} arguments got ${PARAM_VALUES.length}`
            }

            for(let i in PARAMS){  this.VARIABLES.set(PARAMS[i],PARAM_VALUES[i])  }
            this.CURRENTLY_RUNNING_FUNCTION_STACK.push(fnName+"("+PARAM_VALUES+")") //  added ending bracket check in case of print()
            this.MainProgramThread(0,script,fnName)
        }
        this.MainProgramThread(pointer,c)
    }

    ExecuteFunction(fnName,params){
        params = params.replace(")","") ; let temp = params.split(",")

        if(temp.length>1){
            for(let i in temp){  temp[i] = this.ProcessData(temp[i])  }
            params = temp+"" ;
        }
        else{  params = this.ProcessData(params) }
       
        for(let i = 0; i < this.builtInFunctions.length ; i++){
            if(this.builtInFunctions[i] === fnName){ var ans = this.FunctionCaller(i,params) || null }
        }

        return ans
    }

    ForLoopInterpreter(pointer,script){ 
        let c = script ;  let loop = ""  ;  let loop_script = ""  ; let temp ;

        while( c[pointer] != "\n" ){  loop += c[pointer] ; loop += "" ; pointer++ }   
        loop = loop.split("in")
        if(loop.length <= 1){ throw `Syntax Error : "in" missing .` } // "in" neccesary for looping .

        temp = this.LoopTillGivenCharacter(pointer,":",c,'Syntax Error : Loop end ":" not found .')
        loop_script = temp[1] ; pointer = temp[0]

        if( loop[1].includes("[") && this.LETTERS.test(loop[1]) ){ // variable having an array 
            let temp = loop[1].replace(/[[|]|]/g,"")
            let start = loop[0].replace(/[[|]|]/g,"")
            if( !this.VARIABLES.has(temp) ){ throw `NameError : name "${temp}" is not defined .` }
            this.VARIABLES.set(start,0)
            this.ExecuteDirectForLoop(start,this.VARIABLES.get(temp),loop_script)
        }

        if( loop[1].includes("[[") ){   // array directly given 
            let start = loop[0].replace(/[[|]|]/g,"")
            this.VARIABLES.set(start,0)
            var loopArray = this.ArrayDeclaration(loop[1][2],loop[1]) ;
            this.ExecuteDirectForLoop(start,loopArray,loop_script)
        }

        if( loop[1].includes("(") ){ // Range tuple given 
            loop[1] = loop[1].replace(/[()]/g,"").split(",") 
            let start = loop[0].replace(/[[|]|]/g,"")
            for(let i in loop[1]){ loop[1][i] = this.ProcessData(loop[1][i]) }
            this.VARIABLES.set(start,loop[1][0])
            this.ExecuteRangeForLoop(loop[1][0],loop[1][1],loop[1][2],start,loop_script)
        }

        this.MainProgramThread(pointer,c)        
    }

    ExecuteDirectForLoop(start,arr,loop_script){
        for(let i of arr){ 
            this.VARIABLES.set(start,i) ;
            this.MainProgramThread(0,loop_script) 
        }
    }

    ExecuteRangeForLoop(start,end,inc,varValue,loop_script){
        if( end === undefined ){ end = start ; start = 0 } // (10) in such range start must be 0 and end will be 10 therfore ..
        if(inc === undefined){ inc = 1 } // incrementing factor is also optional parameter .
        for(let i = Number(start) ; i < Number(end) ; i = i + Number(inc) ){
            this.VARIABLES.set(varValue,i+1) ; this.MainProgramThread(0,loop_script)
        }
    }

    IfInterpreter(pointer,script){
        let c = script ; let CONDITION ; let ans ; let temp ; let if_script ; let t = [] ; let word = "" ;
        temp = this.LoopTillGivenCharacter(pointer,"\n",c) // temprary variable
       
        CONDITION = temp[1].replaceAll("or","||").replaceAll("and","&&").replaceAll("is","===").replaceAll("True","true").replaceAll("False","false") // replacements according to python .
        pointer = temp[0] ; 

        for(let i = 0 ; i < CONDITION.length ; i++){
            if(this.LETTERS.test(CONDITION[i])){ word += CONDITION[i] }
            else{
                t.push(word)
                t.push(CONDITION[i])
                word = ""
            }
            if(i === CONDITION.length-1){  t.push(word)  } // at last point we will add the word to the temp array anyways .
        }

        CONDITION = t.filter(function(str) { return /\S/.test(str); });
        for(let i in CONDITION){
            if(this.LETTERS.test(CONDITION[i])){
                CONDITION[i] = this.ProcessData(CONDITION[i])
            } 
        }

        ans = eval(CONDITION.join("")) // I could have used eval for arithematic also but I was enjoying coding that part by myself, till this point I got little bored :)
        temp = this.LoopTillGivenCharacter(pointer,"?",c,"Syntax Error : if statement never ended missing '?' . ",true)
        pointer = temp[0]+2 ; if_script = temp[1]
        if(ans){ this.MainProgramThread(0,if_script) }
        // work pending lol !!
        // else{
        //     let k = c[pointer]+c[pointer+1]+c[pointer+2]+c[pointer+3]
        //     if(k === "else"){
        //         let x = this.LoopTillGivenCharacter(pointer,"|",c,"Syntax Error : else statement never ended missing '|' .",true)
        //         pointer = x[0]
        //         this.MainProgramThread(0,x[1])
        //     }
        // }
        this.MainProgramThread(pointer,c)
    }

}

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

//---------------------------OUTPUT---------------------------//
// num = 15
// str = krishna
// cap_str = KRISHNA
// true
// list = 1 2 3 4
// {num: 23, age: 19}
// 19
// 3
// Hello World krishna
// 37
// FizzBuzz
//----------------------------END-----------------------------//
