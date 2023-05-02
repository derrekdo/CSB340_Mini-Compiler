    1      1 Identifier      int
    1      5 Identifier      loop
    1      9 Comma          
    1     11 Identifier      number
    1     17 Semicolon      
    2      1 Identifier      int
    2      5 Identifier      prime
    2     11 Op_assign      
    2     13 Integer             1
    2     14 Semicolon      
    4      1 Identifier      number
    4      8 Op_assign      
    4     10 Integer            11
    4     12 Semicolon      
    6      1 Identifier      for
    6      4 LeftParen      
    6      5 Identifier      loop
    6     10 Op_assign      
    6     12 Integer             2
    6     13 Semicolon      
    6     15 Identifier      loop
    6     20 Op_less        
    6     22 Identifier      number
    6     28 Semicolon      
    6     30 Identifier      loop
    6     34 Op_add         
    6     35 Op_add         
    6     36 RightParen     
    6     38 LeftBrace      
    7      3 Keyword_if     
    7      5 LeftParen      
    7      6 LeftParen      
    7      7 Identifier      number
    7     14 Op_mod         
    7     16 Identifier      loop
    7     20 RightParen     
    7     22 Op_equal       
    7     25 Integer             0
    7     26 RightParen     
    7     28 LeftBrace      
    8      6 Identifier      prime
    8     12 Op_assign      
    8     14 Integer             0
    8     15 Semicolon      
    9      3 RightBrace     
   10      1 RightBrace     
   12      1 Keyword_if     
   12      4 LeftParen      
   12      5 Identifier      prime
   12     11 Op_equal       
   12     14 Integer             1
   12     15 RightParen     
   13      3 Identifier      printf
   13      9 LeftParen      
   13     10 String          "%d is prime number."
   13     31 Comma          
   13     33 Identifier      number
   13     39 RightParen     
   13     40 Semicolon      
   14      1 Keyword_else   
   16      3 Identifier      printf
   16      9 LeftParen      
   16     10 String          "%d is not a prime number."
   16     37 Comma          
   16     39 Identifier      number
   16     45 RightParen     
   16     46 Semicolon      
   17      1 End_of_input   