    1      1 Identifier      num1
    1      6 Op_assign      
    1      8 Integer           245
    1     11 Semicolon      
    2      1 Identifier      num2
    2      6 Op_assign      
    2      8 Integer           153
    2     11 Semicolon      
    3      1 Identifier      num3
    3      6 Op_assign      
    3      8 Identifier      num1
    3     13 Op_add         
    3     15 Identifier      num2
    3     19 Semicolon      
    4      1 Identifier      num4
    4      6 Op_assign      
    4      8 LeftParen      
    4      9 Identifier      num3
    4     14 Op_multiply    
    4     16 Op_negate      
    4     17 Integer             1
    4     18 RightParen     
    4     20 Op_mod         
    4     22 Integer             2
    4     23 Semicolon      
    6      1 Keyword_if     
    6      4 LeftParen      
    6      5 Identifier      num4
    6     10 Op_greaterequal
    6     13 Integer             0
    6     14 RightParen     
    6     16 LeftBrace      
    7      5 Keyword_print  
    7     10 LeftParen      
    7     11 String          "yes"
    7     16 RightParen     
    8      1 RightBrace     
    8      3 Keyword_else   
    8      8 LeftBrace      
    9      5 Keyword_print  
    9     10 LeftParen      
    9     11 String          "no"
    9     15 RightParen     
   10      1 RightBrace     
   11      1 End_of_input