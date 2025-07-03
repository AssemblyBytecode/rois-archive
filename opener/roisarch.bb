ShowPointer()
AppTitle "Rois Archive Opener"

archivePath$ = CommandLine$()

If archivePath$ = ""
	RuntimeError("Usage: roisarch Drive:\Path\To\Archive.roisarchive")
EndIf

; Find the last backslash manually
lastSlash = 0
For i = 1 To Len(archivePath$)
	If Mid$(archivePath$, i, 1) = "\"
		lastSlash = i
	EndIf
Next

date$ = CurrentDate()
date$ = Replace(date$, " ", "_")

time$ = CurrentTime()
time$ = Replace(time$, ":", "-")

archiveFolderName$ = "RoisArchive_" + date$ + "_" + time$

currentDirectory = CurrentDir()

CreateDir CurrentDir$() + archiveFolderName$

arcFile = ReadFile(archivePath$)
If Not arcFile
	RuntimeError("The file path given may not exist or the program is unable to open the file." + CurrentDir$())
EndIf

ChangeDir archiveFolderName$

Global arg1$ = ""
Global arg2$ = ""
Global cmd$ = ""
Global args$ = ""
Global spacePos

While Not Eof(arcFile)
	; Stop previous arguments and commands from "coming back from the dead"
	arg1$ = ""
	arg2$ = ""
	cmd$ = ""
	args$ = ""
	spacePos = 0
	Line$ = Trim(ReadLine$(arcFile))
	
	; Skip empty lines using label
	If Line$ = "" Then Goto SkipLine
	
	; Parse command and arguments
	spacePos = Instr(Line$ + " ", " ")
	If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
	cmd$ = Upper$(Left$(Line$, spacePos - 1))
	args$ = Mid$(Line$, spacePos + 1)
	
	spacePos = 0
	
	Select cmd$
		
		Case "." ; Create files
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			If Not arg1$ Then RuntimeError("File doesn't have a filename.")
			arg2 = Int(Mid$(args$, spacePos + 1))
			file = WriteFile(arg1$)
			Print "Creating File: " + arg1$
			Print "Writing " + arg2 + " lines."
			If arg2 Then 
				For i=1 To arg2
					WriteLine(file, ReadLine(arcFile))
				Next
			Else
				Print "This file has no data."
			EndIf
			CloseFile(file)
		
		
		Case ";" ; Create Subdirectory
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			arg2$ = Mid$(args$, spacePos + 1)
			
			If arg1$ = "" Then RuntimeError("No directory given.")
			If arg2$ = "" Then RuntimeError("Subdirectory doesn't have a name.")
			
			Print "Creating Subdirectory: " + arg2$
			Print arg1$ + "\" + arg2$
			CreateDir arg1$ + "\" + arg2$
		
			
		Case ":" ; Create directory
			spacePos = Instr(args$ + " ", " ")
			If spacePos <= 1 Then RuntimeError("Malformed: " + FilePos(arcFile))
			arg1$ = Left$(args$, spacePos - 1)
			Print  arg1$
			If Not arg1$ Then RuntimeError("Directory doesn't have a name.")
			
			CreateDir arg1$
				
				
		Case "?" ; Execute CMDs or files 
			ExecFile args$
		
		Case "!" ; Add Comments during opening process.
			Print "[--------------------]"
			Print "Author Archive Comment"
			Print args$
			Print "[____________________]"
			Delay 1500
		
		Default
			RuntimeError "Not a valid v1.0 Rois Archive!"
		
	End Select

.SkipLine
Wend

ExecFile "explorer.exe " + CurrentDir$()
CloseFile file

End