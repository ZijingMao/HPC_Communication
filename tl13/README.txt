   1. a summary of the current state of a compiler,

      - How complete is your compiler?
	I complete three of the Language Extensions:
          1. Permit operators to occur adjacent to identifiers/numbers/keywords without intervening spaces (so that,for example, both "x := x + 1" and "x:=x+1;" would be legal---and equivalent---statements)(complete)

          2. multi-line comments whose beginning is marked by "$" whose ending is also marked by "$" (complete)

          3. Support additional base types:
              floating point numbers 
              characters (complete)
              strings 


       - known bugs 
		No bug.

  2. a summary of how you have tested your compiler
      Test based on test.tl13:
               %test
		$comment
		comment comment comment
		cccooommeennttt
		$

		program
   		var X as char;
   		var Y as char;
   		var Z as int;
   
		begin
   		X:='\'';
   		Y:='a';
   		Z:=Z+1;

		end		
  
  3. which programming language was used to implement the compiler,
		Java

  4. instructions for how to build and run your compiler
	   
    	I. For first extension, implement it by reading each character in the stream
	II. For second extension, implement it by add a new comment Symbols for identifying comment
	III. For third extension, add character symbol to identify the character token and change methods in the parser and iloc to pass type checking and also change the iloc code

  5. a description of any assistance received if any (This does not need
     to include assistance received from the instructor, from the TA, or
     on piazza.), the statement that "This submission is my own work. All
     assistance other than that received from the instructor, from the TA,
     or on the class piazza forum, has been described in full above.",
		This submission is my own work. All assistance other than that received from the instructor, from the TA,
     or on the class piazza forum, has been described in full above.

 6. your name
	   Qian Huang

 7. the date the README.txt file was created or last modified. (This
    should be the same as the date of the last commit into bazaar.)
		05/01/2013
