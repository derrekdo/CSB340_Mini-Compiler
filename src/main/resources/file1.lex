    1      1 Identifier      int
    1      5 Identifier      num
    1      8 Integer             1
    1     10 Op_assign      
    1     12 Integer           245
    1     15 Semicolon      
    2      1 Identifier      int
    2      5 Identifier      num
    2      8 Integer             2
    2     10 Op_assign      
    2     12 Integer           153
    2     15 Semicolon      
    3      1 Identifier      int
    3      5 Identifier      num
    3      8 Integer             3
    3     10 Op_assign      
    3     12 Identifier      num
    3     15 Integer             1
    3     17 Op_add         
    3     19 Identifier      num
    3     22 Integer             2
    3     23 Semicolon      
    4      1 Identifier      int
    4      5 Identifier      num
    4      8 Integer             4
    4     10 Op_assign      
    4     12 LeftParen      
    4     13 Identifier      num
    4     16 Integer             3
    4     18 Op_multiply    
    4     20 Op_negate      
    4     22 RightParen     
    4     24 Op_mod         
    4     26 Integer             2
    4     27 Semicolon      
    6      1 Keyword_if     
    6      4 LeftParen      
    6      5 Identifier      num
    6      8 Integer             4
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