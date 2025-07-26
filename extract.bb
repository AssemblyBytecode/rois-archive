; extract.bb 
; Extracts all files and directories from a .roisarchive
; Rois Archive no longer supports XP. 

; Planned Features:
; Binary Support (Priority)
; Basic Compression

; If the program crashed, the temp dir may still exist. Don't waste resources kids!
; it may not be much, but it adds up when you test every time you change something :D
If FileType(GetEnv("TEMP") + "\Rois Archive") <> 2 Then CreateDir GetEnv("TEMP") + "\Rois Archive"
tempDir$ = GetEnv("TEMP") + "\Rois Archive"

AppTitle "Rois Archive Extractor"

archivePath$ = CommandLine$()
If FileType(archivePath$) <> 1 Then
	Print "Enter archive path:"
	archivePath$ = Input$()
EndIf

If FileType(archivePath$) <> 1 Then 
	RuntimeError("The archive path given is currently non-accessible or invalid and doesn't exist. Ensure you have formatted path as following: DriveLetter:\Path\To\File.roisarchive")
EndIf

; Get hash of archive file to name the directory with the extracted files
; Taken from the old obfusc_function.bb and modified for needs
ExecFile("cmd.exe /c " + Chr(34) + "certutil.exe -hashfile " + Chr(34) + archivePath$ + Chr(34) + " MD5 > " + Chr(34) + tempDir$ + "\hash.txt" + Chr(34))
Delay  2500 ; Delay to support slower machines.
hashedOutputFile = ReadFile(tempDir$ + "\hash.txt")
ReadLine$(hashedOutputFile) ; Don't know how to skip line so just read to get there.
archiveHash$ = ReadLine$(hashedOutputFile)
Print archiveHash$
CloseFile hashedOutputFile
DeleteFile tempDir$ + "\hash.txt"

Global archive = ReadFile(archivePath$)

If FileType(archiveHash) = 2 Then DeleteDir archiveHash
CreateDir archiveHash$
ChangeDir archiveHash$

Global arg1$ = ""
Global arg2$ = ""
Global cmd$ = ""
Global args$ = ""
Global spacePos = 0
Global lineCount = 0

While Not Eof(archive)
	lineCount = lineCount + 1
	instruction$ = ReadLine$(archive)
	instruction$ = Trim$(instruction$) ; Trim to avoid problems like thinking ; is a parameter/arg
	If Not instruction$ Then Goto SkipLine ; Skip lines if empty
	If instruction$ = "" Then Goto SkipLine
	Print "Parsing CMD/Instruction:"
	Print instruction$
	; Get CMD/Instruction and get all arguments
	spacePos = Instr(instruction$ + " ", " ")
	If spacePos <= 1 Then RuntimeError("Malformed! Line: " + Str(lineCount - 1)) ; Idk why it is over the actual line count.
	cmd$ = Left$(instruction$, spacePos - 1)
	args$ = Mid$(instruction$, spacePos + 1)
	
	; Set argument values
	If cmd$ <> "~" Then getArgs$(args$)
	
	; Start reading and parsing file
	Select cmd$
		Case "/." ; Binaries (end by creating blank line or using ~)
			Print "Binary File"
			Print arg1$
			file = WriteFile(arg1$)
			finished = False
			Repeat
				lineCount = lineCount + 1
				byte$ = ReadLine$(archive)
				byte$ = Trim(byte$) ; Trim to avoid again
				If byte$ = "~" Then Exit
				getArgs$(byte$)
				For i=1 To Int(arg2$) ; Very basic compression, but it works well for BMPs and more.
					WriteByte(file, Int(arg1$))
				Next
				Print "Wrote byte " + Int(arg1$) + Chr(32) + Int(arg2$)  + " times"
			Forever
			CloseFile file
		
		Case "." ; Plain text
			Print "Plain Text File"
			Print arg1$
			file = WriteFile(arg1$)
			For i=1 To Int(arg2$)
				lineCount = lineCount + 1
				WriteLine(file, ReadLine$(archive))
			Next
			CloseFile file
		
		Case ":" ; Directories ( ; has been removed. You must now create a subdirectory like the following: : examplepardir\exampledir)
			Print "Directory"
			Print arg1$
			CreateDir arg1$
		
		Case "!"
			Print "Archive Author Comment"
			Print args$
			Delay 1500
		
		Default
			RuntimeError("Not a valid v1.1 Rois Archive!")
	End Select
	.SkipLine
Wend

Function getArgs$(argumentString$)
	argumentString$ = Trim$(argumentString$)
	spacePos = Instr(argumentString$ + " ", " ")
	If spacePos <= 1 Then RuntimeError("Malformed! Line: " + Str(lineCount - 1))
	arg1$ = Left$(argumentString$, spacePos - 1)
	arg2$ = Mid$(argumentString$, spacePos + 1)
End Function

ExecFile("explorer.exe " + CurrentDir$())
DeleteDir  GetEnv("TEMP") + "\Rois Archive"
CloseFile archive
Print "Completed"
Print "Note: Close program to delete directory."
Print "It's a strange bug I know btw."
WaitKey
End