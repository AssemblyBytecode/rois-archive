# Rois Archive



## A new way to archive so easily it can be done right in notepad.




### Introduction



Rois Archive is a lightweight markup language written in Blitz Basic used
for archiving. It's great for portable applications/executables, keeping a record
of files, and easy modification in case of errors.



### Rewrite Notice (7/23)



Sorry for the hold up!
I am currently rewriting Rois Archive. 
Please Check out the failiure code, it contains my funniest mistakes
from thinking Blitz Basic arrays are like Lua arrays using 1 for the first entry.




### Rois Archive Cheat Sheet



#### /.

Creates a file with the following parameter as filename.
HOWEVER: Unlike . which reads all lines until specified count,
this uses bytes, and as an added bonus, basic compression.
Also, if you have a very large amount of padding for example with 
spaces in a binary simply do the following:
```
32 <number of times to repeat>
```
This is great for uncompressed BMPs, very wasteful files, and more with
this problem.



Example Usage:
```
/. example.txt
72 1
111 1
119 1
100 1
121 1
44 1
32 1
121 1
97 1
108 2
33 1
~
```



#### .

Creates a file with these two following parameters: filename lines



filename: self explanatory, name of file.
lines: how long the file contents go for in lines.



NOTE: If example usage doesn't work try replacing it to look for six
lines and create a blank line at the end.



Example Usage:
```
. example.txt 5
Science
Technology
Engineering
and
Mathematics
```




#### :

Creates a directory with the following parameter as the name.
You can create subdirectories with this instruction using backslashes,
but I personally prefer using ; (REMOVED)



#### !

Displays a comment from the author of the archive. Put whatever you want right after,
don't make a new line though, then it'll show it the next time someone opens the archive
up.
