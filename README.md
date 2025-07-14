# Rois Archive



## A new way to archive so easily it can be done right in notepad.




### Introduction



Rois Archive is a lightweight markup language written in Blitz Basic used
for archiving. It's great for portable applications/executables, keeping a record
of files, and easy modification in case of errors.



### Rois Archive Cheat Sheet



#### .

Creates a file with these two following parameters: filename lines



filename: self explanatory, name of file.
lines: how long the file contents go for in lines.



NOTE: If example usage doesn't work try replacing it to look for four
lines and create a blank line at the end.



Example Usage
`. scary.txt 3`
`BOO!`
`Did I scare you?`
`Well I hope I did :D`




#### :

Creates a directory with the following parameter as the name.
You can create subdirectories with this instruction using backslashes,
but I personally prefer using ;



#### ;

Creates a subdirectory with these two following parameters: directory directoryname
Also, maybe in future versions this will become deprecated? I am not sure,
but I have been thinking.



directory: The parent directory's name. Add a backslash if you are planning for the
subdirectory to be in a subdirectory like the following: example\\to\\subdirectory



directoryname: self explanatory, name of directory.



#### !

Displays a comment from the author of the archive. Put whatever you want right after,
don't make a new line though, then it'll show it the next time someone opens the archive
up.

#### ?

Execute a command or application, will be removed because malware could be executed.

